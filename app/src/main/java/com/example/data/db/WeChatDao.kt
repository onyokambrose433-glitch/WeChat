package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeChatDao {
    // --- Users ---
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)

    // --- Sessions ---
    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY lastActive DESC LIMIT 1")
    fun getActiveSessionFlow(): Flow<SessionEntity?>

    @Query("SELECT * FROM sessions WHERE isActive = 1 ORDER BY lastActive DESC LIMIT 1")
    suspend fun getActiveSession(): SessionEntity?

    @Query("SELECT * FROM sessions WHERE userId = :userId ORDER BY lastActive DESC")
    fun getSessionsForUserFlow(userId: String): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Query("UPDATE sessions SET isActive = 0 WHERE token = :token")
    suspend fun revokeSession(token: String)

    @Query("DELETE FROM sessions")
    suspend fun clearAllSessions()

    // --- Posts ---
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPostsFlow(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId LIMIT 1")
    suspend fun getPostById(postId: String): PostEntity?

    @Query("SELECT * FROM posts WHERE authorId = :userId ORDER BY createdAt DESC")
    fun getPostsByAuthorFlow(userId: String): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)

    // --- Comments ---
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPostFlow(postId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    // --- Stories ---
    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllStoriesFlow(): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    // --- Conversations ---
    @Query("SELECT * FROM conversations ORDER BY isPinned DESC, lastMessageTime DESC")
    fun getAllConversationsFlow(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: String)

    // --- Messages ---
    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversationFlow(convId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :msgId")
    suspend fun deleteMessage(msgId: String)

    // --- Communities ---
    @Query("SELECT * FROM communities ORDER BY memberCount DESC")
    fun getAllCommunitiesFlow(): Flow<List<CommunityEntity>>

    @Query("SELECT * FROM communities WHERE id = :id LIMIT 1")
    suspend fun getCommunityById(id: String): CommunityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunity(community: CommunityEntity)

    @Update
    suspend fun updateCommunity(community: CommunityEntity)

    // --- Polls ---
    @Query("SELECT * FROM polls ORDER BY createdAt DESC")
    fun getAllPollsFlow(): Flow<List<PollEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoll(poll: PollEntity)

    @Update
    suspend fun updatePoll(poll: PollEntity)

    // --- Life Tasks ---
    @Query("SELECT * FROM life_tasks WHERE userId = :userId ORDER BY isCompleted ASC, createdAt DESC")
    fun getTasksFlow(userId: String): Flow<List<LifeTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: LifeTaskEntity)

    @Update
    suspend fun updateTask(task: LifeTaskEntity)

    @Query("DELETE FROM life_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    // --- Life Reminders ---
    @Query("SELECT * FROM life_reminders WHERE userId = :userId ORDER BY isDone ASC")
    fun getRemindersFlow(userId: String): Flow<List<LifeReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: LifeReminderEntity)

    @Update
    suspend fun updateReminder(reminder: LifeReminderEntity)

    @Query("DELETE FROM life_reminders WHERE id = :reminderId")
    suspend fun deleteReminder(reminderId: String)

    // --- Saved Resources ---
    @Query("SELECT * FROM saved_resources WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSavedResourcesFlow(userId: String): Flow<List<SavedResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedResource(resource: SavedResourceEntity)

    @Query("DELETE FROM saved_resources WHERE id = :resId")
    suspend fun deleteSavedResource(resId: String)

    // --- Call Records ---
    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallRecordsFlow(): Flow<List<CallRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallRecord(call: CallRecordEntity)
}
