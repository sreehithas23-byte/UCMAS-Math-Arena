package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val uid: String = "NHSSASTD4879",
    val name: String = "SREEHITH A S",
    val className: String = "Class VI-A",
    val email: String = "sreehithas23@gmail.com",
    val accountStatus: String = "APPROVED", // "APPROVED", "PENDING"
    val tokens: Int = 1450,
    val xp: Int = 3850,
    val level: Int = 14,
    val streakDays: Int = 7,
    val lastCheckInDate: String = "",
    val activeTheme: String = "RED",
    val activeSkin: String = "Gold Imperial",
    val equippedShirt: String = "Academy Red Blazer",
    val equippedPants: String = "Classic Black Slacks",
    val equippedAccessory: String = "Golden Abacus Belt",
    val bossTickets: Int = 3,
    val feeStatus: String = "Due",
    val feeAmount: Int = 2400,
    val attendancePercent: Int = 92,
    val attendedSessions: Int = 11,
    val totalSessions: Int = 12
)

@Entity(tableName = "token_transactions")
data class TokenTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Int,
    val isCredit: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String
)

@Entity(tableName = "roster_students")
data class RosterStudent(
    @PrimaryKey val id: String,
    val name: String,
    val rollNo: String,
    val tier: String,
    val accuracy: Int,
    val avgLatencyMs: Int,
    val attendanceRate: Int,
    val tokens: Int,
    val isPresentToday: Boolean = true,
    val batchId: String = "batch_mon"
)

@Entity(tableName = "homework_records")
data class HomeworkRecord(
    @PrimaryKey val id: String,
    val title: String,
    val date: String,
    val status: String, // "Verified", "Pending Review"
    val score: String,
    val feedback: String,
    val formulaUsed: String
)

@Entity(tableName = "speed_league_entries")
data class SpeedLeagueEntry(
    @PrimaryKey val rank: Int,
    val name: String,
    val tier: String, // Bronze, Silver, Gold, Platinum, Diamond
    val avgSpeedMs: Int,
    val accuracy: Int,
    val isCurrentUser: Boolean = false
)

@Entity(tableName = "attendance_logs")
data class AttendanceLog(
    @PrimaryKey val day: Int,
    val monthYear: String,
    val status: String // "Present", "Absent", "Holiday"
)

@Entity(tableName = "class_diary_entries")
data class ClassDiaryEntry(
    @PrimaryKey val id: String,
    val date: String,
    val type: String, // "CLASSWORK" or "HOMEWORK"
    val title: String,
    val description: String,
    val formulaFocus: String,
    val teacherName: String = "Tr. Meera Joshi"
)

@Entity(tableName = "center_notices")
data class NoticeEvent(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val date: String,
    val venue: String,
    val category: String // "Competition", "Workshop", "Exam"
)

@Entity(tableName = "avatar_items")
data class AvatarItem(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // "SHIRT", "PANTS", "ACCESSORY", "HEAD"
    val costTokens: Int,
    val isTeacherExclusive: Boolean,
    val isUnlocked: Boolean,
    val previewColorHex: Long,
    val description: String
)

@Entity(tableName = "analytics_records")
data class AnalyticsRecord(
    @PrimaryKey val id: Int = 1,
    val addLatencyMs: Int = 210,
    val subLatencyMs: Int = 235,
    val mulLatencyMs: Int = 310,
    val divLatencyMs: Int = 390,
    val monAcc: Int = 92,
    val tueAcc: Int = 95,
    val wedAcc: Int = 93,
    val thuAcc: Int = 98,
    val friAcc: Int = 96,
    val satAcc: Int = 100,
    val sunAcc: Int = 98
)

@Entity(tableName = "pending_students")
data class PendingStudent(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val requestedClass: String = "Class VI-A",
    val dateRegistered: String,
    val status: String = "PENDING" // "PENDING", "APPROVED", "REJECTED"
)

enum class AttendanceBoxStatus(val label: String, val colorHex: Long) {
    PRESENT("Present", 0xFF2E7D32),
    ABSENT("Absent", 0xFFC62828),
    LATE_HALF_DAY("Late / Half-Day", 0xFF616161)
}

@Entity(tableName = "student_batches")
data class StudentBatch(
    @PrimaryKey val id: String,
    val name: String,
    val dayOfWeek: String, // "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    val timeSlot: String = "5:00 PM - 7:00 PM",
    val studentIds: String = "" // Comma-separated roster student IDs
) {
    fun getStudentIdList(): List<String> {
        return if (studentIds.isBlank()) emptyList() else studentIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    fun withStudentIds(ids: List<String>): StudentBatch {
        return copy(studentIds = ids.distinct().joinToString(","))
    }
}

@Entity(tableName = "batch_attendance_records")
data class BatchAttendanceRecord(
    @PrimaryKey val id: String, // "${batchId}_${studentId}_${date}"
    val batchId: String,
    val studentId: String,
    val studentName: String,
    val date: String, // e.g. "2026-10-24"
    val status: String // "PRESENT", "ABSENT", "LATE_HALF_DAY"
)

