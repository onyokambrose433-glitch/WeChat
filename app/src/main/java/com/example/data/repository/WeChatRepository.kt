package com.example.data.repository

import com.example.data.db.WeChatDao
import com.example.data.db.WeChatDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class WeChatRepository(private val dao: WeChatDao) {

    // --- Authentication & Sessions ---

    val activeSessionFlow: Flow<SessionEntity?> = dao.getActiveSessionFlow()

    val currentUserFlow: Flow<UserEntity?> = activeSessionFlow.flatMapLatest { session ->
        if (session != null) {
            dao.getAllUsersFlow().map { users ->
                users.find { it.id == session.userId }
            }
        } else {
            flowOf(null)
        }
    }

    suspend fun registerUser(
        fullName: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        dateOfBirth: String,
        bio: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim().lowercase().removePrefix("@")
        val cleanEmail = email.trim().lowercase()

        // Validation
        if (cleanUsername.length < 3) {
            return@withContext Result.failure(IllegalArgumentException("Username must be at least 3 characters"))
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return@withContext Result.failure(IllegalArgumentException("Enter a valid email address"))
        }
        if (password.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        // Check uniqueness
        if (dao.getUserByUsername(cleanUsername) != null) {
            return@withContext Result.failure(IllegalArgumentException("Username @$cleanUsername is already taken"))
        }
        if (dao.getUserByEmail(cleanEmail) != null) {
            return@withContext Result.failure(IllegalArgumentException("Account with email $cleanEmail already exists"))
        }

        val salt = UUID.randomUUID().toString()
        val passwordHash = WeChatDatabase.hashPassword(password, salt)
        val userId = "user_" + UUID.randomUUID().toString().take(8)

        val newUser = UserEntity(
            id = userId,
            username = cleanUsername,
            fullName = fullName.trim(),
            email = cleanEmail,
            phone = phone.trim(),
            dateOfBirth = dateOfBirth.trim(),
            bio = bio.trim().ifEmpty { "WeChat member. Connect and innovate." },
            avatarUrl = "",
            passwordHash = passwordHash,
            salt = salt,
            isVerified = true,
            trustScore = 96,
            followersCount = 12,
            followingCount = 18
        )

        dao.insertUser(newUser)

        // Create persistent session
        val token = "session_" + UUID.randomUUID().toString()
        val session = SessionEntity(
            token = token,
            userId = newUser.id,
            username = newUser.username,
            deviceName = "Android Device (Current Session)",
            ipAddress = "192.168.1.104",
            isActive = true
        )
        dao.insertSession(session)

        Result.success(newUser)
    }

    suspend fun loginUser(
        identifier: String,
        password: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val clean = identifier.trim().lowercase().removePrefix("@")
        val user = dao.getUserByUsername(clean) ?: dao.getUserByEmail(clean)
            ?: return@withContext Result.failure(IllegalArgumentException("Invalid username, email or password"))

        val computedHash = WeChatDatabase.hashPassword(password, user.salt)
        if (computedHash != user.passwordHash) {
            return@withContext Result.failure(IllegalArgumentException("Invalid username, email or password"))
        }

        // Create or update session
        val token = "session_" + UUID.randomUUID().toString()
        val session = SessionEntity(
            token = token,
            userId = user.id,
            username = user.username,
            deviceName = "Android Device (Authenticated)",
            ipAddress = "192.168.1.104",
            isActive = true
        )
        dao.insertSession(session)

        Result.success(user)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        val current = dao.getActiveSession()
        if (current != null) {
            dao.revokeSession(current.token)
        }
        dao.clearAllSessions()
    }

    suspend fun changePassword(oldPass: String, newPass: String): Result<Unit> = withContext(Dispatchers.IO) {
        val session = dao.getActiveSession() ?: return@withContext Result.failure(IllegalStateException("Not logged in"))
        val user = dao.getUserById(session.userId) ?: return@withContext Result.failure(IllegalStateException("User not found"))

        val oldHash = WeChatDatabase.hashPassword(oldPass, user.salt)
        if (oldHash != user.passwordHash) {
            return@withContext Result.failure(IllegalArgumentException("Current password does not match"))
        }

        val newSalt = UUID.randomUUID().toString()
        val newHash = WeChatDatabase.hashPassword(newPass, newSalt)
        dao.updateUser(user.copy(passwordHash = newHash, salt = newSalt))
        Result.success(Unit)
    }

    suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        val session = dao.getActiveSession() ?: return@withContext Result.failure(IllegalStateException("Not logged in"))
        dao.deleteUserById(session.userId)
        dao.clearAllSessions()
        Result.success(Unit)
    }

    fun getSessionsFlow(userId: String): Flow<List<SessionEntity>> = dao.getSessionsForUserFlow(userId)

    suspend fun revokeSession(token: String) = withContext(Dispatchers.IO) {
        dao.revokeSession(token)
    }

    // --- Posts & Feed ---

    fun getAllPostsFlow(): Flow<List<PostEntity>> = dao.getAllPostsFlow()

    suspend fun createPost(
        content: String,
        tags: String,
        mediaType: String,
        mediaUrl: String,
        visibility: String
    ): Result<PostEntity> = withContext(Dispatchers.IO) {
        val session = dao.getActiveSession() ?: return@withContext Result.failure(IllegalStateException("Login required"))
        val user = dao.getUserById(session.userId) ?: return@withContext Result.failure(IllegalStateException("User not found"))

        val newPost = PostEntity(
            id = "post_" + UUID.randomUUID().toString().take(8),
            authorId = user.id,
            authorUsername = user.username,
            authorFullName = user.fullName,
            authorAvatar = user.avatarUrl,
            content = content.trim(),
            tags = tags.trim(),
            mediaType = mediaType,
            mediaUrl = mediaUrl,
            visibility = visibility,
            createdAt = System.currentTimeMillis()
        )
        dao.insertPost(newPost)
        Result.success(newPost)
    }

    suspend fun toggleLike(postId: String) = withContext(Dispatchers.IO) {
        val post = dao.getPostById(postId) ?: return@withContext
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        dao.updatePost(post.copy(isLiked = newLiked, likesCount = newCount))
    }

    suspend fun toggleBookmark(postId: String) = withContext(Dispatchers.IO) {
        val post = dao.getPostById(postId) ?: return@withContext
        dao.updatePost(post.copy(isBookmarked = !post.isBookmarked))
    }

    suspend fun deletePost(postId: String) = withContext(Dispatchers.IO) {
        dao.deletePost(postId)
    }

    fun getCommentsFlow(postId: String): Flow<List<CommentEntity>> = dao.getCommentsForPostFlow(postId)

    suspend fun addComment(postId: String, text: String): Result<CommentEntity> = withContext(Dispatchers.IO) {
        val session = dao.getActiveSession() ?: return@withContext Result.failure(IllegalStateException("Login required"))
        val user = dao.getUserById(session.userId) ?: return@withContext Result.failure(IllegalStateException("User not found"))

        val comment = CommentEntity(
            id = "com_" + UUID.randomUUID().toString().take(8),
            postId = postId,
            authorId = user.id,
            authorUsername = user.username,
            authorFullName = user.fullName,
            authorAvatar = user.avatarUrl,
            content = text.trim(),
            createdAt = System.currentTimeMillis()
        )
        dao.insertComment(comment)
        val post = dao.getPostById(postId)
        if (post != null) {
            dao.updatePost(post.copy(commentsCount = post.commentsCount + 1))
        }
        Result.success(comment)
    }

    // --- Stories ---

    fun getAllStoriesFlow(): Flow<List<StoryEntity>> = dao.getAllStoriesFlow()

    suspend fun createStory(caption: String, bgHex: String): Result<StoryEntity> = withContext(Dispatchers.IO) {
        val session = dao.getActiveSession() ?: return@withContext Result.failure(IllegalStateException("Login required"))
        val user = dao.getUserById(session.userId) ?: return@withContext Result.failure(IllegalStateException("User not found"))

        val story = StoryEntity(
            id = "story_" + UUID.randomUUID().toString().take(8),
            userId = user.id,
            username = user.username,
            userFullName = user.fullName,
            caption = caption.trim(),
            backgroundHex = bgHex,
            createdAt = System.currentTimeMillis(),
            expiresAt = System.currentTimeMillis() + 86400000L
        )
        dao.insertStory(story)
        Result.success(story)
    }

    // --- Conversations & Messages ---

    fun getAllConversationsFlow(): Flow<List<ConversationEntity>> = dao.getAllConversationsFlow()

    fun getMessagesFlow(convId: String): Flow<List<MessageEntity>> = dao.getMessagesForConversationFlow(convId)

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        messageType: String = "TEXT"
    ): Result<MessageEntity> = withContext(Dispatchers.IO) {
        val session = dao.getActiveSession()
        val senderId = session?.userId ?: "self"
        val senderName = session?.username ?: "Me"

        val msg = MessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            content = content.trim(),
            messageType = messageType,
            timestamp = System.currentTimeMillis(),
            isOutgoing = true,
            status = "DELIVERED"
        )
        dao.insertMessage(msg)

        val conv = dao.getConversationById(conversationId)
        if (conv != null) {
            dao.updateConversation(
                conv.copy(
                    lastMessage = if (messageType == "TEXT") content else "[$messageType Attachment]",
                    lastMessageTime = System.currentTimeMillis()
                )
            )
        }
        Result.success(msg)
    }

    suspend fun reactToMessage(messageId: String, reaction: String) = withContext(Dispatchers.IO) {
        val messages = dao.getAllPostsFlow() // dummy touch
        // Update message directly
        val convs = dao.getAllConversationsFlow().firstOrNull() ?: emptyList()
        for (c in convs) {
            val list = dao.getMessagesForConversationFlow(c.id).firstOrNull() ?: emptyList()
            val m = list.find { it.id == messageId }
            if (m != null) {
                dao.updateMessage(m.copy(reaction = if (m.reaction == reaction) "" else reaction))
                break
            }
        }
    }

    suspend fun createConversation(peerName: String, isGroup: Boolean): ConversationEntity = withContext(Dispatchers.IO) {
        val newConv = ConversationEntity(
            id = "conv_" + UUID.randomUUID().toString().take(8),
            title = peerName.trim(),
            isGroup = isGroup,
            lastMessage = "Conversation started",
            lastMessageTime = System.currentTimeMillis(),
            unreadCount = 0,
            membersCount = if (isGroup) 4 else 2
        )
        dao.insertConversation(newConv)
        newConv
    }

    // --- Communities & Polls ---

    fun getAllCommunitiesFlow(): Flow<List<CommunityEntity>> = dao.getAllCommunitiesFlow()

    suspend fun createCommunity(
        name: String,
        description: String,
        category: String,
        isPrivate: Boolean,
        rules: String
    ): Result<CommunityEntity> = withContext(Dispatchers.IO) {
        val session = dao.getActiveSession()
        val colors = listOf("#E15712", "#2FA0E1", "#1A7BB8", "#B8420B")
        val comm = CommunityEntity(
            id = "comm_" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            description = description.trim(),
            category = category,
            memberCount = 1,
            isPrivate = isPrivate,
            rules = rules.trim().ifEmpty { "Be respectful and contribute valuable insights." },
            iconColorHex = colors.random(),
            isJoined = true,
            createdBy = session?.userId ?: ""
        )
        dao.insertCommunity(comm)
        Result.success(comm)
    }

    suspend fun toggleJoinCommunity(id: String) = withContext(Dispatchers.IO) {
        val c = dao.getCommunityById(id) ?: return@withContext
        val newJoined = !c.isJoined
        val newCount = if (newJoined) c.memberCount + 1 else (c.memberCount - 1).coerceAtLeast(1)
        dao.updateCommunity(c.copy(isJoined = newJoined, memberCount = newCount))
    }

    fun getAllPollsFlow(): Flow<List<PollEntity>> = dao.getAllPollsFlow()

    suspend fun votePoll(pollId: String, optionIndex: Int) = withContext(Dispatchers.IO) {
        val polls = dao.getAllPollsFlow().firstOrNull() ?: return@withContext
        val poll = polls.find { it.id == pollId } ?: return@withContext
        if (poll.userVotedIndex == optionIndex) return@withContext

        val votes = poll.votesJson.split("|").map { it.toIntOrNull() ?: 0 }.toMutableList()
        if (poll.userVotedIndex in votes.indices) {
            votes[poll.userVotedIndex] = (votes[poll.userVotedIndex] - 1).coerceAtLeast(0)
        }
        if (optionIndex in votes.indices) {
            votes[optionIndex] += 1
        }
        val total = votes.sum()
        dao.updatePoll(
            poll.copy(
                votesJson = votes.joinToString("|"),
                userVotedIndex = optionIndex,
                totalVotes = total
            )
        )
    }

    suspend fun createPoll(communityId: String, question: String, options: List<String>) = withContext(Dispatchers.IO) {
        val poll = PollEntity(
            id = "poll_" + UUID.randomUUID().toString().take(8),
            communityId = communityId,
            question = question.trim(),
            optionsJson = options.joinToString("|"),
            votesJson = options.map { 0 }.joinToString("|"),
            userVotedIndex = -1,
            totalVotes = 0
        )
        dao.insertPoll(poll)
    }

    // --- Life Dashboard (Tasks, Reminders, Saved Resources) ---

    fun getTasksFlow(userId: String): Flow<List<LifeTaskEntity>> = dao.getTasksFlow(userId)

    suspend fun addTask(
        userId: String,
        title: String,
        description: String = "",
        dueDate: String = "Today",
        priority: String = "NORMAL",
        origin: String = "MANUAL"
    ) = withContext(Dispatchers.IO) {
        val task = LifeTaskEntity(
            id = "task_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            title = title.trim(),
            description = description.trim(),
            isCompleted = false,
            dueDate = dueDate,
            priority = priority,
            origin = origin
        )
        dao.insertTask(task)
    }

    suspend fun toggleTask(task: LifeTaskEntity) = withContext(Dispatchers.IO) {
        dao.updateTask(task.copy(isCompleted = !task.isCompleted))
    }

    suspend fun deleteTask(taskId: String) = withContext(Dispatchers.IO) {
        dao.deleteTask(taskId)
    }

    fun getRemindersFlow(userId: String): Flow<List<LifeReminderEntity>> = dao.getRemindersFlow(userId)

    suspend fun addReminder(userId: String, title: String, reminderTime: String, category: String = "General") =
        withContext(Dispatchers.IO) {
            val r = LifeReminderEntity(
                id = "rem_" + UUID.randomUUID().toString().take(8),
                userId = userId,
                title = title.trim(),
                reminderTime = reminderTime,
                isDone = false,
                category = category
            )
            dao.insertReminder(r)
        }

    suspend fun deleteReminder(id: String) = withContext(Dispatchers.IO) {
        dao.deleteReminder(id)
    }

    fun getSavedResourcesFlow(userId: String): Flow<List<SavedResourceEntity>> = dao.getSavedResourcesFlow(userId)

    suspend fun saveResource(userId: String, title: String, summary: String, type: String = "NOTE") =
        withContext(Dispatchers.IO) {
            val r = SavedResourceEntity(
                id = "res_" + UUID.randomUUID().toString().take(8),
                userId = userId,
                title = title.trim(),
                summary = summary.trim(),
                type = type
            )
            dao.insertSavedResource(r)
        }

    suspend fun deleteSavedResource(resId: String) = withContext(Dispatchers.IO) {
        dao.deleteSavedResource(resId)
    }

    // --- Smart Message Actions ---
    suspend fun convertMessageToTask(message: MessageEntity, userId: String) = withContext(Dispatchers.IO) {
        val title = "Action: " + message.content.take(45)
        addTask(
            userId = userId,
            title = title,
            description = "Created from message by ${message.senderName}: \"${message.content}\"",
            dueDate = "Today",
            priority = "HIGH",
            origin = "MESSAGE_ACTION"
        )
        dao.updateMessage(message.copy(smartActionNote = "Converted to Task"))
    }

    suspend fun convertMessageToReminder(message: MessageEntity, userId: String) = withContext(Dispatchers.IO) {
        val title = "Reminder: " + message.content.take(40)
        addReminder(
            userId = userId,
            title = title,
            reminderTime = "In 2 hours",
            category = "Chat Reminder"
        )
        dao.updateMessage(message.copy(smartActionNote = "Reminder set for in 2 hours"))
    }

    suspend fun convertMessageToResource(message: MessageEntity, userId: String) = withContext(Dispatchers.IO) {
        saveResource(
            userId = userId,
            title = "Saved from chat with ${message.senderName}",
            summary = message.content,
            type = if (message.messageType == "DOC") "FILE" else "MESSAGE"
        )
        dao.updateMessage(message.copy(smartActionNote = "Saved to Library"))
    }

    // --- Calls & Logs ---

    fun getAllCallRecordsFlow(): Flow<List<CallRecordEntity>> = dao.getAllCallRecordsFlow()

    suspend fun logCall(
        peerName: String,
        callType: String,
        durationSeconds: Int,
        status: String
    ) = withContext(Dispatchers.IO) {
        val log = CallRecordEntity(
            id = "call_" + UUID.randomUUID().toString().take(8),
            peerName = peerName,
            callType = callType,
            durationSeconds = durationSeconds,
            status = status,
            timestamp = System.currentTimeMillis()
        )
        dao.insertCallRecord(log)
    }
}
