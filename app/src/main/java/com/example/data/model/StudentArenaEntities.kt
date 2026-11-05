package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing student profiles at UCMAS Thane Kopri.
 */
@Entity(tableName = "student_profiles")
data class StudentProfileEntity(
    @PrimaryKey val studentId: String,
    val fullName: String,
    val rollNumber: String,
    val gradeLevel: String = "Level 4 - Higher Grand Master",
    val email: String = "",
    val phoneNumber: String = "",
    val parentName: String = "",
    val parentContact: String = "",
    val batchId: String = "batch_mon",
    val batchDay: String = "Monday",
    val admissionDate: String = "2026-01-15",
    val avatarSkin: String = "Gold Imperial",
    val totalTokens: Int = 1450,
    val currentXp: Int = 3850,
    val rankTitle: String = "Master Speedster",
    val isActive: Boolean = true
)

/**
 * Room database entity storing student learning progress and speed metrics.
 */
@Entity(tableName = "student_progress")
data class StudentProgressEntity(
    @PrimaryKey val studentId: String,
    val studentName: String,
    val currentLevel: Int = 4,
    val term: String = "Term 2",
    val currentModule: String = "Module 4 - Higher Grand Master",
    val abacusSpeedMs: Int = 210,
    val flashAnzanSpeedMs: Int = 180,
    val mentalAccuracyPercent: Int = 96,
    val problemsAttempted: Int = 1240,
    val problemsSolvedCorrectly: Int = 1190,
    val formulasMastered: Int = 18,
    val formulasTotal: Int = 20,
    val attendanceRatePercent: Int = 94,
    val streakDays: Int = 7,
    val leagueTier: String = "Platinum",
    val weeklyPracticeMinutes: Int = 160,
    val totalXp: Int = 3850,
    val totalTokens: Int = 1450,
    val duelsWon: Int = 18,
    val duelsTotal: Int = 22,
    val trainingSessionsCount: Int = 45,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Room database entity storing Math Arena competition and game scores.
 */
@Entity(tableName = "math_arena_scores")
data class MathArenaScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val gameMode: String, // "FLASH_ANZAN", "SPEED_SPRINT", "DECIMAL_DASH", "BOSS_BATTLE", "ANZAN_DUEL"
    val score: Int,
    val accuracyPercentage: Float,
    val problemsAttempted: Int,
    val problemsCorrect: Int,
    val avgTimePerProblemMs: Long,
    val tokensEarned: Int,
    val xpEarned: Int,
    val difficultyLevel: String = "Standard",
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String = ""
)
