package com.example.data.dao

import androidx.room.*
import com.example.data.model.MathArenaScoreEntity
import com.example.data.model.StudentProfileEntity
import com.example.data.model.StudentProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Student Profiles, Progress, and Math Arena Scores.
 */
@Dao
interface StudentArenaDao {

    // ==========================================
    // 1. STUDENT PROFILES
    // ==========================================
    @Query("SELECT * FROM student_profiles ORDER BY rollNumber ASC")
    fun getAllStudentProfiles(): Flow<List<StudentProfileEntity>>

    @Query("SELECT * FROM student_profiles WHERE studentId = :studentId LIMIT 1")
    fun getStudentProfileById(studentId: String): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM student_profiles WHERE studentId = :studentId LIMIT 1")
    suspend fun getStudentProfileDirect(studentId: String): StudentProfileEntity?

    @Query("SELECT * FROM student_profiles WHERE batchId = :batchId ORDER BY fullName ASC")
    fun getStudentProfilesByBatch(batchId: String): Flow<List<StudentProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProfile(profile: StudentProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentProfiles(profiles: List<StudentProfileEntity>)

    @Update
    suspend fun updateStudentProfile(profile: StudentProfileEntity)

    @Delete
    suspend fun deleteStudentProfile(profile: StudentProfileEntity)

    @Query("DELETE FROM student_profiles WHERE studentId = :studentId")
    suspend fun deleteStudentProfileById(studentId: String)


    // ==========================================
    // 2. STUDENT PROGRESS
    // ==========================================
    @Query("SELECT * FROM student_progress ORDER BY currentLevel DESC, mentalAccuracyPercent DESC")
    fun getAllStudentProgress(): Flow<List<StudentProgressEntity>>

    @Query("SELECT * FROM student_progress WHERE studentId = :studentId LIMIT 1")
    fun getStudentProgress(studentId: String): Flow<StudentProgressEntity?>

    @Query("SELECT * FROM student_progress WHERE studentId = :studentId LIMIT 1")
    suspend fun getStudentProgressDirect(studentId: String): StudentProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: StudentProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressList(list: List<StudentProgressEntity>)

    @Delete
    suspend fun deleteProgress(progress: StudentProgressEntity)

    @Query("UPDATE student_progress SET streakDays = streakDays + 1, lastUpdatedTimestamp = :timestamp WHERE studentId = :studentId")
    suspend fun incrementStreak(studentId: String, timestamp: Long = System.currentTimeMillis())


    // ==========================================
    // 3. MATH ARENA SCORES
    // ==========================================
    @Query("SELECT * FROM math_arena_scores ORDER BY timestamp DESC")
    fun getAllScores(): Flow<List<MathArenaScoreEntity>>

    @Query("SELECT * FROM math_arena_scores WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getScoresByStudent(studentId: String): Flow<List<MathArenaScoreEntity>>

    @Query("SELECT * FROM math_arena_scores WHERE gameMode = :gameMode ORDER BY score DESC LIMIT :limit")
    fun getTopScoresByGameMode(gameMode: String, limit: Int = 10): Flow<List<MathArenaScoreEntity>>

    @Query("SELECT * FROM math_arena_scores ORDER BY score DESC LIMIT :limit")
    fun getHighScoresLeaderboard(limit: Int = 20): Flow<List<MathArenaScoreEntity>>

    @Query("SELECT * FROM math_arena_scores ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentScores(limit: Int = 20): Flow<List<MathArenaScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: MathArenaScoreEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScores(scores: List<MathArenaScoreEntity>)

    @Delete
    suspend fun deleteScore(score: MathArenaScoreEntity)

    @Query("DELETE FROM math_arena_scores WHERE studentId = :studentId")
    suspend fun clearScoresForStudent(studentId: String)

    @Query("SELECT AVG(accuracyPercentage) FROM math_arena_scores WHERE studentId = :studentId")
    suspend fun getAverageAccuracy(studentId: String): Float?

    @Query("SELECT MAX(score) FROM math_arena_scores WHERE studentId = :studentId AND gameMode = :gameMode")
    suspend fun getBestScore(studentId: String, gameMode: String): Int?
}
