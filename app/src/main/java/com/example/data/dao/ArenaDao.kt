package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArenaDao {
    @Query("SELECT * FROM user_profile WHERE uid = :uid LIMIT 1")
    fun getUserProfile(uid: String = "NHSSASTD4879"): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("SELECT * FROM token_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TokenTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TokenTransaction)

    @Query("SELECT * FROM roster_students ORDER BY rollNo ASC")
    fun getAllRosterStudents(): Flow<List<RosterStudent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRosterStudents(students: List<RosterStudent>)

    @Update
    suspend fun updateRosterStudent(student: RosterStudent)

    @Delete
    suspend fun deleteRosterStudent(student: RosterStudent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleStudent(student: RosterStudent)

    @Query("SELECT * FROM homework_records ORDER BY date DESC")
    fun getAllHomeworkRecords(): Flow<List<HomeworkRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeworkRecords(records: List<HomeworkRecord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleHomework(record: HomeworkRecord)

    @Update
    suspend fun updateHomeworkRecord(record: HomeworkRecord)

    @Query("SELECT * FROM speed_league_entries ORDER BY rank ASC")
    fun getSpeedLeague(): Flow<List<SpeedLeagueEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeedLeague(entries: List<SpeedLeagueEntry>)

    @Query("SELECT * FROM attendance_logs ORDER BY day ASC")
    fun getAttendanceLogs(): Flow<List<AttendanceLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceLogs(logs: List<AttendanceLog>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(log: AttendanceLog)

    // Class Diary (Classwork & Homework)
    @Query("SELECT * FROM class_diary_entries ORDER BY date DESC")
    fun getAllClassDiary(): Flow<List<ClassDiaryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassDiary(entry: ClassDiaryEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassDiaryList(entries: List<ClassDiaryEntry>)

    // Notices & Events
    @Query("SELECT * FROM center_notices ORDER BY date DESC")
    fun getAllNotices(): Flow<List<NoticeEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: NoticeEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoticesList(notices: List<NoticeEvent>)

    // Avatar Items (Roblox style clothes & accessories)
    @Query("SELECT * FROM avatar_items")
    fun getAllAvatarItems(): Flow<List<AvatarItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvatarItem(item: AvatarItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvatarItemsList(items: List<AvatarItem>)

    @Update
    suspend fun updateAvatarItem(item: AvatarItem)

    // Analytics Record
    @Query("SELECT * FROM analytics_records WHERE id = 1 LIMIT 1")
    fun getAnalytics(): Flow<AnalyticsRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAnalytics(analytics: AnalyticsRecord)

    // Pending Students
    @Query("SELECT * FROM pending_students ORDER BY dateRegistered DESC")
    fun getAllPendingStudents(): Flow<List<PendingStudent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingStudent(student: PendingStudent)

    @Update
    suspend fun updatePendingStudent(student: PendingStudent)

    // Student Batches
    @Query("SELECT * FROM student_batches ORDER BY name ASC")
    fun getAllBatches(): Flow<List<StudentBatch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: StudentBatch)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatches(batches: List<StudentBatch>)

    @Update
    suspend fun updateBatch(batch: StudentBatch)

    @Delete
    suspend fun deleteBatch(batch: StudentBatch)

    // Batch Attendance Records
    @Query("SELECT * FROM batch_attendance_records")
    fun getAllBatchAttendance(): Flow<List<BatchAttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatchAttendance(record: BatchAttendanceRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatchAttendanceList(records: List<BatchAttendanceRecord>)
}

