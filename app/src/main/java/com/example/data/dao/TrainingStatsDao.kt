package com.example.data.dao

import androidx.room.*
import com.example.data.model.TrainingSessionStatsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Training Session statistics.
 * Provides reactive Flow streams and suspend write operations for mental math practice telemetry.
 */
@Dao
interface TrainingStatsDao {

    @Query("SELECT * FROM training_session_stats ORDER BY timestamp DESC")
    fun getAllTrainingSessions(): Flow<List<TrainingSessionStatsEntity>>

    @Query("SELECT * FROM training_session_stats WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getTrainingSessionsByStudent(studentId: String): Flow<List<TrainingSessionStatsEntity>>

    @Query("SELECT * FROM training_session_stats WHERE studentId = :studentId AND sessionType = :sessionType ORDER BY timestamp DESC")
    fun getTrainingSessionsByType(studentId: String, sessionType: String): Flow<List<TrainingSessionStatsEntity>>

    @Query("SELECT SUM(problemsCorrect) FROM training_session_stats WHERE studentId = :studentId")
    fun getTotalProblemsSolved(studentId: String): Flow<Int?>

    @Query("SELECT SUM(problemsAttempted) FROM training_session_stats WHERE studentId = :studentId")
    fun getTotalProblemsAttempted(studentId: String): Flow<Int?>

    @Query("SELECT AVG(accuracyPercentage) FROM training_session_stats WHERE studentId = :studentId")
    fun getAverageTrainingAccuracy(studentId: String): Flow<Float?>

    @Query("SELECT SUM(durationSeconds) FROM training_session_stats WHERE studentId = :studentId")
    fun getTotalTrainingDurationSeconds(studentId: String): Flow<Int?>

    @Query("SELECT AVG(avgResponseTimeMs) FROM training_session_stats WHERE studentId = :studentId")
    fun getOverallAverageResponseTime(studentId: String): Flow<Long?>

    @Query("SELECT * FROM training_session_stats ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentTrainingSessions(limit: Int = 10): Flow<List<TrainingSessionStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingSession(session: TrainingSessionStatsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingSessions(sessions: List<TrainingSessionStatsEntity>)

    @Delete
    suspend fun deleteTrainingSession(session: TrainingSessionStatsEntity)

    @Query("DELETE FROM training_session_stats WHERE studentId = :studentId")
    suspend fun clearTrainingSessionsForStudent(studentId: String)
}
