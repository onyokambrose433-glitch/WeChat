package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.WeChatDatabase
import com.example.data.model.*
import com.example.data.repository.WeChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppNavDestination {
    HOME,
    DISCOVER,
    CREATE,
    MESSAGES,
    PROFILE
}

data class ActiveCallState(
    val isRinging: Boolean = false,
    val isConnected: Boolean = false,
    val peerName: String = "",
    val callType: String = "AUDIO", // AUDIO, VIDEO
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isCameraOn: Boolean = true,
    val isSpeakerOn: Boolean = false
)

class WeChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = WeChatDatabase.getDatabase(application)
    val repository = WeChatRepository(db.dao())

    // Navigation & Screen selection
    private val _currentDestination = MutableStateFlow(AppNavDestination.HOME)
    val currentDestination: StateFlow<AppNavDestination> = _currentDestination.asStateFlow()

    // Sub-screens / Overlays
    private val _selectedConversation = MutableStateFlow<ConversationEntity?>(null)
    val selectedConversation: StateFlow<ConversationEntity?> = _selectedConversation.asStateFlow()

    private val _selectedPostForComments = MutableStateFlow<PostEntity?>(null)
    val selectedPostForComments: StateFlow<PostEntity?> = _selectedPostForComments.asStateFlow()

    private val _viewingStory = MutableStateFlow<StoryEntity?>(null)
    val viewingStory: StateFlow<StoryEntity?> = _viewingStory.asStateFlow()

    // Active Call
    private val _activeCall = MutableStateFlow<ActiveCallState?>(null)
    val activeCall: StateFlow<ActiveCallState?> = _activeCall.asStateFlow()
    private var callTimerJob: Job? = null

    // User & Session
    val currentUser = repository.currentUserFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val activeSession = repository.activeSessionFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // Feed & Content
    val posts = repository.getAllPostsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val stories = repository.getAllStoriesFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Chats
    val conversations = repository.getAllConversationsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val activeMessages: StateFlow<List<MessageEntity>> = _selectedConversation.flatMapLatest { conv ->
        if (conv != null) repository.getMessagesFlow(conv.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activePostComments: StateFlow<List<CommentEntity>> = _selectedPostForComments.flatMapLatest { post ->
        if (post != null) repository.getCommentsFlow(post.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Communities & Polls
    val communities = repository.getAllCommunitiesFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val polls = repository.getAllPollsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Life Dashboard
    val tasks: StateFlow<List<LifeTaskEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getTasksFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<LifeReminderEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getRemindersFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedResources: StateFlow<List<SavedResourceEntity>> = currentUser.flatMapLatest { user ->
        if (user != null) repository.getSavedResourcesFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callLogs = repository.getAllCallRecordsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Feedback message
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    fun navigateTo(dest: AppNavDestination) {
        _currentDestination.value = dest
        _selectedConversation.value = null
        _selectedPostForComments.value = null
    }

    fun openConversation(conv: ConversationEntity) {
        _selectedConversation.value = conv
    }

    fun closeConversation() {
        _selectedConversation.value = null
    }

    fun openComments(post: PostEntity) {
        _selectedPostForComments.value = post
    }

    fun closeComments() {
        _selectedPostForComments.value = null
    }

    fun openStory(story: StoryEntity) {
        _viewingStory.value = story
    }

    fun closeStory() {
        _viewingStory.value = null
    }

    fun showFeedback(msg: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(msg)
        }
    }

    // --- Auth Actions ---

    fun register(
        fullName: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        dob: String,
        bio: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(fullName, username, email, phone, password, dob, bio)
            result.onSuccess {
                showFeedback("Welcome to WeChat, ${it.fullName}!")
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Registration failed")
            }
        }
    }

    fun login(
        identifier: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.loginUser(identifier, pass)
            result.onSuccess {
                showFeedback("Welcome back, ${it.fullName}!")
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Authentication failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            showFeedback("Logged out successfully")
        }
    }

    fun changePassword(oldPass: String, newPass: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.changePassword(oldPass, newPass)
            result.onSuccess {
                showFeedback("Password updated securely")
                onDone(true, "Password updated successfully")
            }.onFailure {
                onDone(false, it.message ?: "Update failed")
            }
        }
    }

    fun deleteAccount(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAccount()
            showFeedback("Account data removed permanently")
            onDone()
        }
    }

    // --- Feed Actions ---

    fun createPost(content: String, tags: String, mediaType: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val res = repository.createPost(
                content = content,
                tags = tags,
                mediaType = mediaType,
                mediaUrl = "",
                visibility = "PUBLIC"
            )
            res.onSuccess {
                showFeedback("Post published to your network")
                onDone()
            }.onFailure {
                showFeedback(it.message ?: "Failed to post")
            }
        }
    }

    fun toggleLikePost(postId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun toggleBookmarkPost(postId: String) {
        viewModelScope.launch {
            repository.toggleBookmark(postId)
            showFeedback("Saved to your Universal Library")
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
            showFeedback("Post removed")
        }
    }

    fun addComment(postId: String, text: String) {
        viewModelScope.launch {
            repository.addComment(postId, text)
        }
    }

    // --- Story Actions ---

    fun createStory(caption: String, bgHex: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.createStory(caption, bgHex)
            showFeedback("Moment shared to your story")
            onDone()
        }
    }

    // --- Message Actions ---

    fun sendMessage(content: String, type: String = "TEXT") {
        val conv = _selectedConversation.value ?: return
        viewModelScope.launch {
            repository.sendMessage(conv.id, content, type)

            // Simulate intelligent auto-acknowledgement from peer when chatting with Alex or Sophia
            if (conv.title.contains("Alex") || conv.title.contains("Sophia")) {
                delay(1200)
                val reply = when {
                    content.contains("call", true) -> "I'm ready for the video call whenever you are!"
                    content.contains("meet", true) || content.contains("time", true) -> "Sounds great. I'll add this to our workspace schedule."
                    content.contains("doc", true) || content.contains("file", true) -> "Received the asset. I've saved it into our shared folder."
                    else -> "Got it! Reviewing this with the team right now."
                }
                val peerId = if (conv.title.contains("Alex")) "user_alex" else "user_sophia"
                val peerMsg = MessageEntity(
                    id = "msg_reply_" + System.currentTimeMillis(),
                    conversationId = conv.id,
                    senderId = peerId,
                    senderName = conv.title,
                    content = reply,
                    messageType = "TEXT",
                    timestamp = System.currentTimeMillis(),
                    isOutgoing = false,
                    status = "READ"
                )
                db.dao().insertMessage(peerMsg)
                db.dao().updateConversation(conv.copy(lastMessage = reply, lastMessageTime = System.currentTimeMillis()))
            }
        }
    }

    fun reactToMessage(msgId: String, reaction: String) {
        viewModelScope.launch {
            repository.reactToMessage(msgId, reaction)
        }
    }

    fun convertMessageToTask(message: MessageEntity) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.convertMessageToTask(message, user.id)
            showFeedback("Message converted to Life Dashboard Task")
        }
    }

    fun convertMessageToReminder(message: MessageEntity) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.convertMessageToReminder(message, user.id)
            showFeedback("Message scheduled as 2-hour reminder")
        }
    }

    fun convertMessageToResource(message: MessageEntity) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.convertMessageToResource(message, user.id)
            showFeedback("Message saved to Universal Library")
        }
    }

    fun startNewChat(peerName: String, isGroup: Boolean) {
        viewModelScope.launch {
            val conv = repository.createConversation(peerName, isGroup)
            _selectedConversation.value = conv
            _currentDestination.value = AppNavDestination.MESSAGES
        }
    }

    // --- Communities & Polls ---

    fun createCommunity(name: String, desc: String, category: String, isPrivate: Boolean, rules: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.createCommunity(name, desc, category, isPrivate, rules)
            showFeedback("Community \"$name\" created successfully")
            onDone()
        }
    }

    fun toggleJoinCommunity(commId: String) {
        viewModelScope.launch {
            repository.toggleJoinCommunity(commId)
        }
    }

    fun votePoll(pollId: String, index: Int) {
        viewModelScope.launch {
            repository.votePoll(pollId, index)
            showFeedback("Vote recorded in Group Decision Center")
        }
    }

    fun createPoll(commId: String, question: String, options: List<String>, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.createPoll(commId, question, options)
            showFeedback("Poll proposed to community")
            onDone()
        }
    }

    // --- Life Dashboard Actions ---

    fun addTask(title: String, dueDate: String, priority: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.addTask(user.id, title, dueDate = dueDate, priority = priority)
            showFeedback("Task added to Life Dashboard")
        }
    }

    fun toggleTask(task: LifeTaskEntity) {
        viewModelScope.launch {
            repository.toggleTask(task)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    fun addReminder(title: String, time: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.addReminder(user.id, title, time)
            showFeedback("Reminder scheduled")
        }
    }

    fun deleteReminder(remId: String) {
        viewModelScope.launch {
            repository.deleteReminder(remId)
        }
    }

    fun saveResource(title: String, summary: String, type: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.saveResource(user.id, title, summary, type)
            showFeedback("Resource saved to Universal Library")
        }
    }

    fun deleteResource(resId: String) {
        viewModelScope.launch {
            repository.deleteSavedResource(resId)
        }
    }

    // --- Calling (WebRTC / Audio & Video simulation) ---

    fun startCall(peerName: String, type: String) {
        _activeCall.value = ActiveCallState(
            isRinging = true,
            isConnected = false,
            peerName = peerName,
            callType = type,
            durationSeconds = 0
        )

        // Connect after 2.5s
        viewModelScope.launch {
            delay(2500)
            _activeCall.value = _activeCall.value?.copy(isRinging = false, isConnected = true)
            startCallTimer()
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    fun toggleMuteCall() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleCameraCall() {
        _activeCall.value = _activeCall.value?.let { it.copy(isCameraOn = !it.isCameraOn) }
    }

    fun toggleSpeakerCall() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun endCall() {
        val call = _activeCall.value
        callTimerJob?.cancel()
        if (call != null) {
            viewModelScope.launch {
                repository.logCall(
                    peerName = call.peerName,
                    callType = call.callType,
                    durationSeconds = call.durationSeconds,
                    status = if (call.isConnected) "COMPLETED" else "CANCELLED"
                )
            }
        }
        _activeCall.value = null
        showFeedback("Call ended")
    }
}
