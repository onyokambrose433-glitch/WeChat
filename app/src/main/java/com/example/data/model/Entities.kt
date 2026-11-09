package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val bio: String = "",
    val avatarUrl: String = "",
    val coverUrl: String = "",
    val dateOfBirth: String = "",
    val passwordHash: String,
    val salt: String,
    val isVerified: Boolean = true,
    val trustScore: Int = 98,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val token: String,
    val userId: String,
    val username: String,
    val deviceName: String,
    val ipAddress: String,
    val lastActive: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorUsername: String,
    val authorFullName: String,
    val authorAvatar: String = "",
    val content: String,
    val mediaUrl: String = "",
    val mediaType: String = "NONE", // NONE, IMAGE, DOCUMENT
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val repostsCount: Int = 0,
    val tags: String = "", // comma-separated
    val visibility: String = "PUBLIC", // PUBLIC, FRIENDS, PRIVATE
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val postId: String,
    val authorId: String,
    val authorUsername: String,
    val authorFullName: String,
    val authorAvatar: String = "",
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val userFullName: String,
    val userAvatar: String = "",
    val caption: String = "",
    val backgroundHex: String = "#E15712",
    val viewsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 86400000L
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val isGroup: Boolean = false,
    val avatarUrl: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isMuted: Boolean = false,
    val peerOnline: Boolean = true,
    val membersCount: Int = 2
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String = "",
    val content: String,
    val messageType: String = "TEXT", // TEXT, VOICE, IMAGE, DOC, LOCATION
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean = true,
    val status: String = "READ", // SENT, DELIVERED, READ
    val reaction: String = "",
    val smartActionNote: String = ""
)

@Entity(tableName = "communities")
data class CommunityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String, // Technology, Business, Education, Creative, Local
    val memberCount: Int = 1,
    val isPrivate: Boolean = false,
    val rules: String = "Be respectful and contribute valuable insights.",
    val iconColorHex: String = "#2FA0E1",
    val isJoined: Boolean = false,
    val createdBy: String = ""
)

@Entity(tableName = "polls")
data class PollEntity(
    @PrimaryKey val id: String,
    val communityId: String = "",
    val question: String,
    val optionsJson: String, // JSON string or piped options: Option A|Option B|Option C
    val votesJson: String = "0|0|0",
    val userVotedIndex: Int = -1,
    val totalVotes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "life_tasks")
data class LifeTaskEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val dueDate: String = "Today",
    val priority: String = "NORMAL", // HIGH, NORMAL, LOW
    val origin: String = "MANUAL", // MANUAL, MESSAGE_ACTION
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "life_reminders")
data class LifeReminderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val reminderTime: String,
    val isDone: Boolean = false,
    val category: String = "General"
)

@Entity(tableName = "saved_resources")
data class SavedResourceEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val summary: String,
    val type: String = "NOTE", // NOTE, LINK, FILE, MESSAGE
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "call_logs")
data class CallRecordEntity(
    @PrimaryKey val id: String,
    val peerName: String,
    val peerAvatar: String = "",
    val callType: String = "AUDIO", // AUDIO, VIDEO
    val isIncoming: Boolean = false,
    val durationSeconds: Int = 0,
    val status: String = "COMPLETED", // MISSED, COMPLETED, DECLINED
    val timestamp: Long = System.currentTimeMillis()
)
