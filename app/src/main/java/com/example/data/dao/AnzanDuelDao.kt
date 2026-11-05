package com.example.data.dao

import androidx.room.*
import com.example.data.model.AnzanDuelResultEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Anzan 1-v-1 Duel results.
 * Provides reactive Flow streams and suspend write operations.
 */
@Dao
interface AnzanDuelDao {

    @Query("SELECT * FROM anzan_duel_results ORDER BY timestamp DESC")
    fun getAllDuelResults(): Flow<List<AnzanDuelResultEntity>>

    @Query("SELECT * FROM anzan_duel_results WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getDuelResultsByStudent(studentId: String): Flow<List<AnzanDuelResultEntity>>

    @Query("SELECT * FROM anzan_duel_results WHERE studentId = :studentId AND isVictory = 1 ORDER BY timestamp DESC")
    fun getDuelVictories(studentId: String): Flow<List<AnzanDuelResultEntity>>

    @Query("SELECT COUNT(*) FROM anzan_duel_results WHERE studentId = :studentId AND isVictory = 1")
    fun getWinCount(studentId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM anzan_duel_results WHERE studentId = :studentId")
    fun getTotalDuelsCount(studentId: String): Flow<Int>

    @Query("SELECT AVG(playerAccuracyPercent) FROM anzan_duel_results WHERE studentId = :studentId")
    fun getAverageDuelAccuracy(studentId: String): Flow<Float?>

    @Query("SELECT AVG(avgLatencyMs) FROM anzan_duel_results WHERE studentId = :studentId")
    fun getAverageLatencyMs(studentId: String): Flow<Float?>

    @Query("SELECT * FROM anzan_duel_results ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentDuels(limit: Int = 10): Flow<List<AnzanDuelResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuelResult(result: AnzanDuelResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDuelResults(results: List<AnzanDuelResultEntity>)

    @Delete
    suspend fun deleteDuelResult(result: AnzanDuelResultEntity)

    @Query("DELETE FROM anzan_duel_results WHERE studentId = :studentId")
    suspend fun clearDuelsForStudent(studentId: String)
}
