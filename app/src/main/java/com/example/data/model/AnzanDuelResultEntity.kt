package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing results and telemetry for Anzan 1-v-1 Duels.
 * Captures head-to-head mental math match performance against AI tutors or peer challengers.
 */
@Entity(tableName = "anzan_duel_results")
data class AnzanDuelResultEntity(
    @PrimaryKey(autoGenerate = true) val duelId: Long = 0,
    val studentId: String = "std_1",
    val studentName: String = "SREEHITH A S",
    val opponentName: String,
    val opponentClass: String = "Class VI-A",
    val opponentTier: String = "Diamond",
    val opponentType: String = "PEER", // "AI", "PEER", "RIVAL"
    val operationType: String = "ADD_SUB", // "ADD_SUB", "MULTIPLICATION", "COMBINATION"
    val digitCount: Int = 2,
    val rowCount: Int = 2,
    val flashSpeedMs: Int = 200,
    val totalRounds: Int = 5,
    val playerScore: Int,
    val opponentScore: Int,
    val resultStatus: String, // "VICTORY", "DEFEAT", "DRAW"
    val isVictory: Boolean,
    val correctAnswers: Int,
    val playerAccuracyPercent: Float,
    val avgLatencyMs: Long,
    val maxCombo: Int = 1,
    val tokensEarned: Int = 0,
    val xpEarned: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String = "Today"
)
