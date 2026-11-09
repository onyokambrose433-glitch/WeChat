package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Database(
    entities = [
        UserEntity::class,
        SessionEntity::class,
        PostEntity::class,
        CommentEntity::class,
        StoryEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        CommunityEntity::class,
        PollEntity::class,
        LifeTaskEntity::class,
        LifeReminderEntity::class,
        SavedResourceEntity::class,
        CallRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WeChatDatabase : RoomDatabase() {
    abstract fun dao(): WeChatDao

    companion object {
        @Volatile
        private var INSTANCE: WeChatDatabase? = null

        fun getDatabase(context: Context): WeChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeChatDatabase::class.java,
                    "wechat_platform.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun hashPassword(password: String, salt: String): String {
            val bytes = "$password:$salt".toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.fold("") { str, it -> str + "%02x".format(it) }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.dao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: WeChatDao) {
            val now = System.currentTimeMillis()

            // 1. Initial Verified Users / Contacts
            val salt = UUID.randomUUID().toString()
            val defaultPassHash = hashPassword("WeChat@2026", salt)

            val alex = UserEntity(
                id = "user_alex",
                username = "alex_chen",
                fullName = "Alex Chen",
                email = "alex.chen@wechat.net",
                phone = "+1 (555) 234-5678",
                bio = "AI Systems Architect & Open-Source Contributor. Exploring decentralized trust & quantum messaging.",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                dateOfBirth = "1994-06-15",
                passwordHash = defaultPassHash,
                salt = salt,
                isVerified = true,
                trustScore = 99,
                followersCount = 1420,
                followingCount = 380
            )

            val sophia = UserEntity(
                id = "user_sophia",
                username = "sophia_v",
                fullName = "Sophia Vance",
                email = "sophia.v@wechat.net",
                phone = "+44 20 7946 0912",
                bio = "Head of Product Design @ Horizon Labs. Passionate about human-centric interfaces and spatial computing.",
                avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150",
                dateOfBirth = "1996-03-22",
                passwordHash = defaultPassHash,
                salt = salt,
                isVerified = true,
                trustScore = 97,
                followersCount = 3280,
                followingCount = 490
            )

            val marcus = UserEntity(
                id = "user_marcus",
                username = "marcus_dev",
                fullName = "Marcus Thorne",
                email = "marcus.t@wechat.net",
                phone = "+1 (555) 876-5432",
                bio = "Distributed Systems Engineer. Rust, Kotlin, High-performance data pipelines.",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                dateOfBirth = "1992-11-04",
                passwordHash = defaultPassHash,
                salt = salt,
                isVerified = true,
                trustScore = 95,
                followersCount = 890,
                followingCount = 210
            )

            dao.insertUser(alex)
            dao.insertUser(sophia)
            dao.insertUser(marcus)

            // 2. Initial Stories
            dao.insertStory(
                StoryEntity(
                    id = "story_1",
                    userId = alex.id,
                    username = alex.username,
                    userFullName = alex.fullName,
                    caption = "Deploying WeChat 2.0 distributed core nodes today! 🚀",
                    backgroundHex = "#E15712",
                    viewsCount = 340,
                    createdAt = now - 3600000L
                )
            )
            dao.insertStory(
                StoryEntity(
                    id = "story_2",
                    userId = sophia.id,
                    username = sophia.username,
                    userFullName = sophia.fullName,
                    caption = "New design system guidelines finalized. Check out the crisp brand orange & blue!",
                    backgroundHex = "#2FA0E1",
                    viewsCount = 512,
                    createdAt = now - 7200000L
                )
            )

            // 3. Initial Feed Posts
            dao.insertPost(
                PostEntity(
                    id = "post_1",
                    authorId = alex.id,
                    authorUsername = alex.username,
                    authorFullName = alex.fullName,
                    content = "Excited to welcome everyone to WeChat Next-Gen! Our end-to-end messaging protocol, cryptographic Trust Scores, and integrated Life Dashboard are fully operational. Looking forward to everyone's feedback!",
                    likesCount = 84,
                    commentsCount = 19,
                    repostsCount = 7,
                    tags = "#Engineering,#SocialTrust,#WeChatLaunch",
                    visibility = "PUBLIC",
                    isLiked = true,
                    isBookmarked = true,
                    createdAt = now - 1800000L
                )
            )

            dao.insertPost(
                PostEntity(
                    id = "post_2",
                    authorId = sophia.id,
                    authorUsername = sophia.username,
                    authorFullName = sophia.fullName,
                    content = "Just published our study on collaborative workspaces and cognitive clarity: reducing notification fatigue through smart message actions increases team productivity by 41%. Thoughts on turning chats into structured decisions?",
                    likesCount = 142,
                    commentsCount = 38,
                    repostsCount = 24,
                    tags = "#Design,#Productivity,#Workspaces",
                    visibility = "PUBLIC",
                    isLiked = false,
                    isBookmarked = false,
                    createdAt = now - 7200000L
                )
            )

            dao.insertPost(
                PostEntity(
                    id = "post_3",
                    authorId = marcus.id,
                    authorUsername = marcus.username,
                    authorFullName = marcus.fullName,
                    content = "Benchmark update: SQLite with WAL mode & asynchronous replication handles 40,000 queries per second with sub-millisecond latencies on modern ARM devices. Local-first architecture is the future.",
                    likesCount = 67,
                    commentsCount = 12,
                    repostsCount = 5,
                    tags = "#Performance,#MobileTech,#Database",
                    visibility = "PUBLIC",
                    isLiked = false,
                    isBookmarked = false,
                    createdAt = now - 14400000L
                )
            )

            // 4. Initial Comments
            dao.insertComment(
                CommentEntity(
                    id = "com_1",
                    postId = "post_1",
                    authorId = sophia.id,
                    authorUsername = sophia.username,
                    authorFullName = sophia.fullName,
                    content = "The fluid transitions and contrast between brand orange and blue feel incredibly sharp!",
                    createdAt = now - 1200000L
                )
            )

            // 5. Initial Conversations & Messages
            val conv1 = ConversationEntity(
                id = "conv_alex",
                title = "Alex Chen",
                isGroup = false,
                lastMessage = "Let's review the new group decision feature shortly.",
                lastMessageTime = now - 600000L,
                unreadCount = 1,
                isPinned = true,
                peerOnline = true
            )
            val conv2 = ConversationEntity(
                id = "conv_sophia",
                title = "Sophia Vance",
                isGroup = false,
                lastMessage = "Sent the updated high-fidelity component specs.",
                lastMessageTime = now - 1800000L,
                unreadCount = 0,
                isPinned = true,
                peerOnline = true
            )
            val conv3 = ConversationEntity(
                id = "conv_team",
                title = "WeChat Core Engineering",
                isGroup = true,
                lastMessage = "Marcus: Zero build errors on the release candidate! 🎉",
                lastMessageTime = now - 3600000L,
                unreadCount = 3,
                isPinned = false,
                peerOnline = true,
                membersCount = 8
            )

            dao.insertConversation(conv1)
            dao.insertConversation(conv2)
            dao.insertConversation(conv3)

            // Messages for Alex
            dao.insertMessage(
                MessageEntity(
                    id = "msg_1",
                    conversationId = conv1.id,
                    senderId = alex.id,
                    senderName = alex.fullName,
                    content = "Hey! Welcome to the new WeChat communication platform.",
                    messageType = "TEXT",
                    timestamp = now - 1200000L,
                    isOutgoing = false,
                    status = "READ",
                    reaction = "👋"
                )
            )
            dao.insertMessage(
                MessageEntity(
                    id = "msg_2",
                    conversationId = conv1.id,
                    senderId = "self",
                    senderName = "Me",
                    content = "Thanks Alex! The interface looks clean, fast and responsive.",
                    messageType = "TEXT",
                    timestamp = now - 900000L,
                    isOutgoing = true,
                    status = "READ"
                )
            )
            dao.insertMessage(
                MessageEntity(
                    id = "msg_3",
                    conversationId = conv1.id,
                    senderId = alex.id,
                    senderName = alex.fullName,
                    content = "Let's review the new group decision feature shortly.",
                    messageType = "TEXT",
                    timestamp = now - 600000L,
                    isOutgoing = false,
                    status = "DELIVERED",
                    reaction = "💡",
                    smartActionNote = "Actionable: Task or Reminder"
                )
            )

            // Messages for Sophia
            dao.insertMessage(
                MessageEntity(
                    id = "msg_s1",
                    conversationId = conv2.id,
                    senderId = sophia.id,
                    senderName = sophia.fullName,
                    content = "Hi! I've uploaded the visual identity specifications for the design tokens.",
                    messageType = "TEXT",
                    timestamp = now - 2400000L,
                    isOutgoing = false,
                    status = "READ"
                )
            )
            dao.insertMessage(
                MessageEntity(
                    id = "msg_s2",
                    conversationId = conv2.id,
                    senderId = sophia.id,
                    senderName = sophia.fullName,
                    content = "Brand_Design_Tokens_v2.pdf (1.4 MB)",
                    messageType = "DOC",
                    timestamp = now - 1800000L,
                    isOutgoing = false,
                    status = "READ",
                    reaction = "🔥"
                )
            )

            // 6. Initial Communities
            dao.insertCommunity(
                CommunityEntity(
                    id = "comm_tech",
                    name = "Modern Software Architects",
                    description = "A global community of backend and mobile developers designing scalable, resilient systems.",
                    category = "Technology",
                    memberCount = 1480,
                    isPrivate = false,
                    iconColorHex = "#2FA0E1",
                    isJoined = true
                )
            )
            dao.insertCommunity(
                CommunityEntity(
                    id = "comm_design",
                    name = "UI/UX & Product Innovators",
                    description = "Exploring cutting-edge typography, tactile feedback, and human-first interactions.",
                    category = "Creative",
                    memberCount = 920,
                    isPrivate = false,
                    iconColorHex = "#E15712",
                    isJoined = true
                )
            )
            dao.insertCommunity(
                CommunityEntity(
                    id = "comm_business",
                    name = "Global Founders & Creators",
                    description = "Strategic discussions on product-led growth, venture funding, and developer ecosystems.",
                    category = "Business",
                    memberCount = 610,
                    isPrivate = true,
                    iconColorHex = "#1A7BB8",
                    isJoined = false
                )
            )

            // 7. Initial Poll
            dao.insertPoll(
                PollEntity(
                    id = "poll_1",
                    communityId = "comm_tech",
                    question = "Which feature architecture do you prioritize most in next-gen social platforms?",
                    optionsJson = "End-to-End Cryptographic Trust|Smart Message to Task Actions|Digital Identity & Portability",
                    votesJson = "142|98|65",
                    userVotedIndex = 0,
                    totalVotes = 305
                )
            )

            // 8. Initial Call Logs
            dao.insertCallRecord(
                CallRecordEntity(
                    id = "call_1",
                    peerName = "Alex Chen",
                    callType = "VIDEO",
                    isIncoming = true,
                    durationSeconds = 480,
                    status = "COMPLETED",
                    timestamp = now - 86400000L
                )
            )
            dao.insertCallRecord(
                CallRecordEntity(
                    id = "call_2",
                    peerName = "Sophia Vance",
                    callType = "AUDIO",
                    isIncoming = false,
                    durationSeconds = 190,
                    status = "COMPLETED",
                    timestamp = now - 172800000L
                )
            )
        }
    }
}
