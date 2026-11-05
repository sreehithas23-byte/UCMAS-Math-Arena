package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ArenaRepository
import com.example.data.model.*
import com.example.util.MathSheetScannerService
import com.example.util.ScannedWorksheetResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * UI State for camera/Gemini worksheet scanning in Faculty Portal.
 */
sealed interface SheetScanUiState {
    data object Idle : SheetScanUiState
    data object Scanning : SheetScanUiState
    data class Success(val result: ScannedWorksheetResult, val isSavedToDatabase: Boolean) : SheetScanUiState
    data class Error(val message: String) : SheetScanUiState
}

/**
 * Dedicated TeacherViewModel managing:
 * 1. 20-minute secure session logic using a timestamp and password 'UcMaS2026'
 * 2. Room database operations for Student Profiles, Progress, and Math Arena Scores
 * 3. Gemini API handwritten math sheet scanning and database mapping
 */
class TeacherViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val FACULTY_PASSWORD = "UcMaS2026"
        const val SESSION_DURATION_MS = 20 * 60 * 1000L // 20 minutes in milliseconds
        private const val PREFS_NAME = "ucmas_faculty_prefs"
        private const val KEY_EXPIRY_TIMESTAMP = "teacher_unlock_expiry_ms"
    }

    private val repository: ArenaRepository
    private val facultyPrefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Gemini Sheet Scanner Service
    val scannerService = MathSheetScannerService()

    // 20-minute secure session timestamp state
    private val _sessionExpiryTimestamp = MutableStateFlow(facultyPrefs.getLong(KEY_EXPIRY_TIMESTAMP, 0L))
    val sessionExpiryTimestamp: StateFlow<Long> = _sessionExpiryTimestamp.asStateFlow()

    private val _isUnlocked = MutableStateFlow(System.currentTimeMillis() < _sessionExpiryTimestamp.value)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    private val _remainingMinutes = MutableStateFlow(calculateRemainingMinutes(_sessionExpiryTimestamp.value))
    val remainingMinutes: StateFlow<Int> = _remainingMinutes.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(calculateRemainingSeconds(_sessionExpiryTimestamp.value))
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Sheet Scanning State
    private val _scanUiState = MutableStateFlow<SheetScanUiState>(SheetScanUiState.Idle)
    val scanUiState: StateFlow<SheetScanUiState> = _scanUiState.asStateFlow()

    // Database Flows
    val studentProfiles: StateFlow<List<StudentProfileEntity>>
    val studentProgressList: StateFlow<List<StudentProgressEntity>>
    val mathArenaScores: StateFlow<List<MathArenaScoreEntity>>
    val rosterStudents: StateFlow<List<RosterStudent>>
    val batches: StateFlow<List<StudentBatch>>
    val batchAttendance: StateFlow<List<BatchAttendanceRecord>>
    val homeworkRecords: StateFlow<List<HomeworkRecord>>
    val classDiary: StateFlow<List<ClassDiaryEntry>>
    val notices: StateFlow<List<NoticeEvent>>

    init {
        val database = AppDatabase.getDatabase(application)
        val firestore = try {
            val dbId = application.getString(com.example.R.string.firestore_database_id)
            com.google.firebase.firestore.FirebaseFirestore.getInstance(dbId)
        } catch (e: Exception) {
            null
        }
        repository = ArenaRepository(
            dao = database.arenaDao(),
            studentArenaDao = database.studentArenaDao(),
            anzanDuelDao = database.anzanDuelDao(),
            trainingStatsDao = database.trainingStatsDao(),
            firestore = firestore
        )

        studentProfiles = repository.studentProfiles.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        studentProgressList = repository.studentProgressList.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        mathArenaScores = repository.mathArenaScores.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        rosterStudents = repository.rosterStudents.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        batches = repository.batches.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        batchAttendance = repository.batchAttendance.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        homeworkRecords = repository.homeworkRecords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        classDiary = repository.classDiary.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        notices = repository.notices.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Start periodic session ticker to automatically update countdown and lock after 20 minutes
        startSessionTicker()
    }

    /**
     * Authenticates with faculty password 'UcMaS2026'.
     * If valid, sets session expiry timestamp to exactly 20 minutes from now,
     * persists it to SharedPreferences, and unlocks the session.
     */
    fun unlock(password: String): Boolean {
        val trimmed = password.trim()
        if (trimmed == FACULTY_PASSWORD || trimmed.equals(FACULTY_PASSWORD, ignoreCase = true)) {
            val now = System.currentTimeMillis()
            val expiry = now + SESSION_DURATION_MS
            facultyPrefs.edit().putLong(KEY_EXPIRY_TIMESTAMP, expiry).apply()

            _sessionExpiryTimestamp.value = expiry
            _isUnlocked.value = true
            _remainingMinutes.value = 20
            _remainingSeconds.value = SESSION_DURATION_MS / 1000L
            _authError.value = null
            return true
        } else {
            _authError.value = "Invalid password. Please enter '$FACULTY_PASSWORD'"
            return false
        }
    }

    /**
     * Manually locks the teacher session and clears the expiry timestamp.
     */
    fun lock() {
        facultyPrefs.edit().putLong(KEY_EXPIRY_TIMESTAMP, 0L).apply()
        _sessionExpiryTimestamp.value = 0L
        _isUnlocked.value = false
        _remainingMinutes.value = 0
        _remainingSeconds.value = 0L
        _authError.value = null
    }

    /**
     * Checks if current session is still active within the 20-minute window.
     */
    fun isSessionActive(): Boolean {
        val expiry = _sessionExpiryTimestamp.value
        val now = System.currentTimeMillis()
        val active = now < expiry
        if (!active && _isUnlocked.value) {
            lock()
        }
        return active
    }

    /**
     * Background ticker coroutine that runs every 1 second to update the 20-minute countdown.
     * When current time reaches expiry, automatically locks teacher portal.
     */
    private fun startSessionTicker() {
        viewModelScope.launch {
            while (isActive) {
                val expiry = _sessionExpiryTimestamp.value
                val now = System.currentTimeMillis()
                if (expiry > 0L) {
                    if (now < expiry) {
                        _isUnlocked.value = true
                        _remainingMinutes.value = calculateRemainingMinutes(expiry)
                        _remainingSeconds.value = calculateRemainingSeconds(expiry)
                    } else {
                        // 20 minutes elapsed: auto-lock
                        _isUnlocked.value = false
                        _remainingMinutes.value = 0
                        _remainingSeconds.value = 0L
                        facultyPrefs.edit().putLong(KEY_EXPIRY_TIMESTAMP, 0L).apply()
                    }
                } else {
                    _isUnlocked.value = false
                    _remainingMinutes.value = 0
                    _remainingSeconds.value = 0L
                }
                delay(1000L)
            }
        }
    }

    private fun calculateRemainingMinutes(expiryMs: Long): Int {
        val diff = expiryMs - System.currentTimeMillis()
        return if (diff > 0) ((diff / 60000L) + 1).toInt().coerceAtMost(20) else 0
    }

    private fun calculateRemainingSeconds(expiryMs: Long): Long {
        val diff = expiryMs - System.currentTimeMillis()
        return if (diff > 0) diff / 1000L else 0L
    }

    fun clearAuthError() {
        _authError.value = null
    }

    // ==========================================
    // Gemini Worksheet OCR & Student Database Mapping
    // ==========================================

    /**
     * Scans and interprets a handwritten math sheet via Gemini API,
     * and maps results directly back to the student database.
     */
    fun scanAndMapWorksheet(
        bitmap: Bitmap,
        studentId: String,
        studentName: String,
        targetLevel: String = "Level 4"
    ) {
        viewModelScope.launch {
            _scanUiState.value = SheetScanUiState.Scanning
            try {
                val scanResult = scannerService.scanHandwrittenSheet(
                    bitmap = bitmap,
                    studentName = studentName,
                    targetLevel = targetLevel
                )

                if (scanResult.isSuccess) {
                    val result = scanResult.getOrThrow().copy(studentId = studentId, studentName = studentName)
                    val isSaved = scannerService.mapScanResultToStudentDatabase(
                        repository = repository,
                        studentId = studentId,
                        studentName = studentName,
                        result = result
                    )
                    _scanUiState.value = SheetScanUiState.Success(result, isSaved)
                } else {
                    val err = scanResult.exceptionOrNull()?.message ?: "Failed to interpret worksheet."
                    _scanUiState.value = SheetScanUiState.Error(err)
                }
            } catch (e: Exception) {
                _scanUiState.value = SheetScanUiState.Error(e.message ?: "Sheet scanning failed.")
            }
        }
    }

    fun resetScanState() {
        _scanUiState.value = SheetScanUiState.Idle
    }

    // ==========================================
    // Student Profiles & Math Arena Scores Room DB Operations
    // ==========================================

    fun insertOrUpdateStudentProfile(profile: StudentProfileEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateStudentProfile(profile)
        }
    }

    fun deleteStudentProfile(profile: StudentProfileEntity) {
        viewModelScope.launch {
            repository.deleteStudentProfile(profile)
        }
    }

    fun insertOrUpdateStudentProgress(progress: StudentProgressEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateStudentProgress(progress)
        }
    }

    fun recordMathArenaScore(
        studentId: String,
        studentName: String,
        gameMode: String,
        score: Int,
        accuracy: Float,
        attempted: Int,
        correct: Int,
        avgTimeMs: Long,
        tokens: Int,
        xp: Int,
        difficulty: String = "Level 4"
    ) {
        viewModelScope.launch {
            val scoreEntity = MathArenaScoreEntity(
                studentId = studentId,
                studentName = studentName,
                gameMode = gameMode,
                score = score,
                accuracyPercentage = accuracy,
                problemsAttempted = attempted,
                problemsCorrect = correct,
                avgTimePerProblemMs = avgTimeMs,
                tokensEarned = tokens,
                xpEarned = xp,
                difficultyLevel = difficulty,
                timestamp = System.currentTimeMillis(),
                formattedDate = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            )
            repository.recordMathArenaScore(scoreEntity)
        }
    }

    fun deleteMathArenaScore(score: MathArenaScoreEntity) {
        viewModelScope.launch {
            repository.deleteMathArenaScore(score)
        }
    }

    fun clearScoresForStudent(studentId: String) {
        viewModelScope.launch {
            repository.clearScoresForStudent(studentId)
        }
    }
}
