package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing training session statistics and performance metrics across
 * Flash Arcade, Boss Raid, Speed League, and Abacus Practice drills.
 */
@Entity(tableName = "training_session_stats")
data class TrainingSessionStatsEntity(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val studentId: String = "std_1",
    val studentName: String = "SREEHITH A S",
    val sessionType: String, // "FLASH_ARCADE", "BOSS_RAID", "SPEED_LEAGUE", "ABACUS_PRACTICE", "DECIMAL_DASH"
    val drillName: String,
    val formulaCategory: String = "Friend of 10", // "Direct", "Friend of 5", "Friend of 10", "Combination"
    val digitCount: Int = 2,
    val rowCount: Int = 4,
    val flashSpeedMs: Int = 250,
    val durationSeconds: Int = 120,
    val problemsAttempted: Int,
    val problemsCorrect: Int,
    val accuracyPercentage: Float,
    val avgResponseTimeMs: Long,
    val tokensEarned: Int = 0,
    val xpEarned: Int = 0,
    val highestCombo: Int = 1,
    val score: Int = 0,
    val rating: String = "A", // "S", "A", "B", "C"
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String = "Today"
)
