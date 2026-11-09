package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*
import java.security.MessageDigest

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
    version = 2,
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
                    .fallbackToDestructiveMigration()
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
    }
}
