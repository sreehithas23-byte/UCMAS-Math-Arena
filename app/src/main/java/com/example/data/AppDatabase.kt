package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AnzanDuelDao
import com.example.data.dao.ArenaDao
import com.example.data.dao.StudentArenaDao
import com.example.data.dao.TrainingStatsDao
import com.example.data.model.*

@Database(
    entities = [
        UserProfile::class,
        TokenTransaction::class,
        RosterStudent::class,
        HomeworkRecord::class,
        SpeedLeagueEntry::class,
        AttendanceLog::class,
        ClassDiaryEntry::class,
        NoticeEvent::class,
        AvatarItem::class,
        AnalyticsRecord::class,
        PendingStudent::class,
        StudentBatch::class,
        BatchAttendanceRecord::class,
        StudentProfileEntity::class,
        StudentProgressEntity::class,
        MathArenaScoreEntity::class,
        AnzanDuelResultEntity::class,
        TrainingSessionStatsEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun arenaDao(): ArenaDao
    abstract fun studentArenaDao(): StudentArenaDao
    abstract fun anzanDuelDao(): AnzanDuelDao
    abstract fun trainingStatsDao(): TrainingStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ucmas_arena_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
