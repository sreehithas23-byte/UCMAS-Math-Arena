package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ArenaRepository
import com.example.data.model.*
import com.example.ui.components.ArenaPortal
import com.example.ui.theme.ArenaTheme
import com.example.util.SoundHapticManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArenaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ArenaRepository
    val soundManager = SoundHapticManager(application.applicationContext)

    val userProfile: StateFlow<UserProfile>
    val transactions: StateFlow<List<TokenTransaction>>
    val rosterStudents: StateFlow<List<RosterStudent>>
    val homeworkRecords: StateFlow<List<HomeworkRecord>>
    val speedLeague: StateFlow<List<SpeedLeagueEntry>>
    val attendanceLogs: StateFlow<List<AttendanceLog>>
    val classDiary: StateFlow<List<ClassDiaryEntry>>
    val notices: StateFlow<List<NoticeEvent>>
    val avatarItems: StateFlow<List<AvatarItem>>
    val analytics: StateFlow<AnalyticsRecord?>
    val pendingStudents: StateFlow<List<PendingStudent>>
    val batches: StateFlow<List<StudentBatch>>
    val batchAttendance: StateFlow<List<BatchAttendanceRecord>>

    val studentProfiles: StateFlow<List<StudentProfileEntity>>
    val studentProgressList: StateFlow<List<StudentProgressEntity>>
    val mathArenaScores: StateFlow<List<MathArenaScoreEntity>>
    val duelResults: StateFlow<List<AnzanDuelResultEntity>>
    val trainingSessions: StateFlow<List<TrainingSessionStatsEntity>>

    private val facultyPrefs = application.getSharedPreferences("ucmas_faculty_prefs", Context.MODE_PRIVATE)
    private val _teacherUnlockExpiryMs = MutableStateFlow(facultyPrefs.getLong("teacher_unlock_expiry_ms", 0L))
    val teacherUnlockExpiryMs: StateFlow<Long> = _teacherUnlockExpiryMs.asStateFlow()

    private val _currentPortal = MutableStateFlow(ArenaPortal.STUDENT)
    val currentPortal: StateFlow<ArenaPortal> = _currentPortal.asStateFlow()

    private val _isParentView = MutableStateFlow(false)
    val isParentView: StateFlow<Boolean> = _isParentView.asStateFlow()

    private val _activeTheme = MutableStateFlow(ArenaTheme.RED)
    val activeTheme: StateFlow<ArenaTheme> = _activeTheme.asStateFlow()

    private val _isAdminUnlocked = MutableStateFlow(System.currentTimeMillis() < facultyPrefs.getLong("teacher_unlock_expiry_ms", 0L))
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _isStudentSubScreenOpen = MutableStateFlow(false)
    val isStudentSubScreenOpen: StateFlow<Boolean> = _isStudentSubScreenOpen.asStateFlow()

    private val _showThemePicker = MutableStateFlow(false)
    val showThemePicker: StateFlow<Boolean> = _showThemePicker.asStateFlow()

    private val _showWalletDialog = MutableStateFlow(false)
    val showWalletDialog: StateFlow<Boolean> = _showWalletDialog.asStateFlow()

    private val _showGoogleSignInDialog = MutableStateFlow(false)
    val showGoogleSignInDialog: StateFlow<Boolean> = _showGoogleSignInDialog.asStateFlow()

    private val _showEditStudentCardDialog = MutableStateFlow(false)
    val showEditStudentCardDialog: StateFlow<Boolean> = _showEditStudentCardDialog.asStateFlow()

    private val _showPaymentDialog = MutableStateFlow(false)
    val showPaymentDialog: StateFlow<Boolean> = _showPaymentDialog.asStateFlow()

    private val _showVictoryParticles = MutableStateFlow(false)
    val showVictoryParticles: StateFlow<Boolean> = _showVictoryParticles.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        val firestore = try {
            val dbId = application.getString(com.example.R.string.firestore_database_id)
            com.google.firebase.firestore.FirebaseFirestore.getInstance(dbId)
        } catch (e: Exception) {
            null
        }
        repository = ArenaRepository(
            database.arenaDao(),
            database.studentArenaDao(),
            database.anzanDuelDao(),
            database.trainingStatsDao(),
            firestore
        )

        userProfile = repository.userProfile
            .filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UserProfile()
            )

        transactions = repository.transactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        rosterStudents = repository.rosterStudents.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        homeworkRecords = repository.homeworkRecords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        speedLeague = repository.speedLeague.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        attendanceLogs = repository.attendanceLogs.stateIn(
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

        avatarItems = repository.avatarItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        analytics = repository.analytics.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AnalyticsRecord()
        )

        pendingStudents = repository.pendingStudents.stateIn(
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

        // Room-backed Student Profiles, Progress, and Math Arena Scores
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

        duelResults = repository.duelResults.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        trainingSessions = repository.trainingSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initializeDataIfEmpty()
        }

        viewModelScope.launch {
            userProfile.collect { prof ->
                _activeTheme.value = ArenaTheme.fromString(prof.activeTheme)
            }
        }
    }

    fun setPortal(portal: ArenaPortal) {
        _currentPortal.value = portal
        soundManager.playBeadClick()
    }

    fun toggleParentView() {
        _isParentView.value = !_isParentView.value
        soundManager.playBeadClick()
    }

    fun setAdminUnlocked(unlocked: Boolean) {
        _isAdminUnlocked.value = unlocked
    }

    // Checks if faculty unlock is currently active within the 20-minute window
    fun isTeacherSessionActive(): Boolean {
        val expiry = _teacherUnlockExpiryMs.value
        val isActive = System.currentTimeMillis() < expiry
        if (!isActive && _isAdminUnlocked.value) {
            _isAdminUnlocked.value = false
        }
        return isActive
    }

    // Calculates remaining minutes in the 20-minute faculty session
    fun getRemainingUnlockMinutes(): Int {
        val diff = _teacherUnlockExpiryMs.value - System.currentTimeMillis()
        return if (diff > 0) ((diff / 60000) + 1).toInt().coerceAtMost(20) else 0
    }

    // Unlocks faculty mode for 20 minutes and persists expiry to SharedPreferences
    fun unlockTeacherSession(durationMinutes: Int = 20) {
        val expiry = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)
        facultyPrefs.edit().putLong("teacher_unlock_expiry_ms", expiry).apply()
        _teacherUnlockExpiryMs.value = expiry
        _isAdminUnlocked.value = true
    }

    // Manually locks faculty mode
    fun lockTeacherSession() {
        facultyPrefs.edit().putLong("teacher_unlock_expiry_ms", 0L).apply()
        _teacherUnlockExpiryMs.value = 0L
        _isAdminUnlocked.value = false
        if (_currentPortal.value == ArenaPortal.TEACHER) {
            _currentPortal.value = ArenaPortal.STUDENT
        }
    }

    fun setStudentSubScreenOpen(isOpen: Boolean) {
        _isStudentSubScreenOpen.value = isOpen
    }

    fun toggleSound() {
        val current = _isSoundEnabled.value
        _isSoundEnabled.value = !current
        soundManager.isSoundEnabled = !current
    }

    fun openThemePicker(show: Boolean) {
        _showThemePicker.value = show
    }

    fun openWalletDialog(show: Boolean) {
        _showWalletDialog.value = show
    }

    fun openGoogleSignIn(show: Boolean) {
        _showGoogleSignInDialog.value = show
    }

    fun openEditStudentCard(show: Boolean) {
        _showEditStudentCardDialog.value = show
    }

    fun selectTheme(theme: ArenaTheme) {
        _activeTheme.value = theme
        _showThemePicker.value = false
        soundManager.playBeadClick()
        viewModelScope.launch {
            repository.updateTheme(theme.name)
        }
    }

    fun equipSkin(skinName: String) {
        soundManager.playBeadClick()
        viewModelScope.launch {
            repository.equipSkin(skinName)
        }
    }

    fun equipAvatarItem(category: String, itemName: String) {
        soundManager.playBeadClick()
        viewModelScope.launch {
            repository.equipAvatarItem(category, itemName)
        }
    }

    fun purchaseAvatarItem(cost: Int, item: AvatarItem) {
        soundManager.playCoinSound()
        viewModelScope.launch {
            repository.addReward("Wardrobe: ${item.name}", -cost, 0, "Avatar Wardrobe Item", false)
            repository.unlockAvatarItem(item)
            repository.equipAvatarItem(item.category, item.name)
        }
    }

    fun grantTeacherAvatar(itemId: String) {
        soundManager.playVictoryFanfare()
        viewModelScope.launch {
            repository.grantTeacherAvatar(itemId)
        }
    }

    fun dailyCheckIn() {
        viewModelScope.launch {
            val success = repository.claimDailyCheckIn()
            if (success) {
                soundManager.playVictoryFanfare()
                triggerVictoryParticles()
            } else {
                soundManager.playBeadClick()
            }
        }
    }

    fun openPaymentDialog(open: Boolean) {
        _showPaymentDialog.value = open
    }

    fun payFees() {
        openPaymentDialog(true)
    }

    fun payFeesWithRef(utr: String) {
        soundManager.playVictoryFanfare()
        triggerVictoryParticles()
        viewModelScope.launch {
            repository.payFeesWithRef(utr)
        }
    }

    fun resetToFreshInstallation() {
        viewModelScope.launch {
            repository.resetToFreshInstallation()
        }
    }

    fun addReward(title: String, tokensDelta: Int, xpDelta: Int, category: String, addTicket: Boolean) {
        viewModelScope.launch {
            repository.addReward(title, tokensDelta, xpDelta, category, addTicket)
        }
    }

    fun submitHomework(record: HomeworkRecord) {
        viewModelScope.launch {
            repository.submitHomework(record)
        }
    }

    fun addRosterStudent(student: RosterStudent) {
        viewModelScope.launch {
            repository.addRosterStudent(student)
        }
    }

    fun updateRosterStudent(student: RosterStudent) {
        viewModelScope.launch {
            repository.updateRosterStudent(student)
        }
    }

    fun approveStudent(id: String, className: String, level: Int, batchId: String = "batch_mon") {
        viewModelScope.launch {
            repository.approvePendingStudent(id, className, level, batchId)
        }
    }

    fun createOrUpdateBatch(batch: StudentBatch) {
        viewModelScope.launch {
            repository.saveBatch(batch)
            soundManager.playBeadClick()
        }
    }

    fun deleteBatch(batch: StudentBatch) {
        viewModelScope.launch {
            repository.deleteBatch(batch)
            soundManager.playBeadClick()
        }
    }

    fun markBatchAttendance(batchId: String, studentId: String, studentName: String, date: String, status: String) {
        viewModelScope.launch {
            repository.markBatchAttendance(batchId, studentId, studentName, date, status)
            when (status) {
                "PRESENT" -> soundManager.playCorrectChime()
                "ABSENT" -> soundManager.playErrorBuzz()
                else -> soundManager.playBeadClick()
            }
        }
    }


    fun rejectStudent(id: String) {
        viewModelScope.launch {
            repository.rejectPendingStudent(id)
        }
    }

    fun registerGoogleStudent(name: String, email: String, className: String) {
        viewModelScope.launch {
            repository.registerGoogleStudent(name, email, className)
        }
    }

    fun selectApprovedStudent(name: String, email: String) {
        viewModelScope.launch {
            val prof = userProfile.value
            repository.updateProfile(prof.copy(name = name, email = email, accountStatus = "APPROVED"))
        }
    }

    fun addClassDiary(entry: ClassDiaryEntry) {
        viewModelScope.launch {
            repository.addClassDiary(entry)
        }
    }

    fun addNotice(notice: NoticeEvent) {
        viewModelScope.launch {
            repository.addNotice(notice)
        }
    }

    fun updateAttendance(log: AttendanceLog) {
        viewModelScope.launch {
            repository.updateAttendance(log)
        }
    }

    fun updateAnalytics(record: AnalyticsRecord) {
        viewModelScope.launch {
            repository.updateAnalytics(record)
        }
    }

    fun saveEditedProfile(profile: UserProfile) {
        viewModelScope.launch {
            val oldTokens = userProfile.value.tokens
            val delta = profile.tokens - oldTokens
            if (delta != 0) {
                repository.addReward("Teacher Token Adjustment", delta, 0, "Teacher Grant", false)
            }
            repository.updateProfile(profile)
            soundManager.playCoinSound()
        }
    }

    fun setStudentTokens(newTokens: Int, reason: String = "Teacher Adjustment") {
        viewModelScope.launch {
            val cur = userProfile.value
            val delta = newTokens - cur.tokens
            repository.updateProfile(cur.copy(tokens = newTokens))
            repository.addReward(reason, delta, 0, "Teacher Grant", false)
            val matchingRoster = rosterStudents.value.find { it.name == cur.name }
            if (matchingRoster != null) {
                repository.updateRosterStudent(matchingRoster.copy(tokens = newTokens))
            }
            soundManager.playCoinSound()
        }
    }

    fun setRosterStudentTokens(studentId: String, newTokens: Int, reason: String = "Teacher Adjustment") {
        viewModelScope.launch {
            val student = rosterStudents.value.find { it.id == studentId }
            if (student != null) {
                repository.updateRosterStudent(student.copy(tokens = newTokens))
                if (student.name == userProfile.value.name) {
                    val cur = userProfile.value
                    repository.updateProfile(cur.copy(tokens = newTokens))
                    val delta = newTokens - cur.tokens
                    repository.addReward(reason, delta, 0, "Teacher Grant", false)
                }
                soundManager.playCoinSound()
            }
        }
    }

    fun triggerVictoryParticles() {
        _showVictoryParticles.value = true
    }

    fun dismissVictoryParticles() {
        _showVictoryParticles.value = false
    }

    // ==========================================
    // Student Profiles, Progress & Arena Scores (Room)
    // ==========================================
    fun createOrUpdateStudentProfile(profile: StudentProfileEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateStudentProfile(profile)
        }
    }

    fun deleteStudentProfile(profile: StudentProfileEntity) {
        viewModelScope.launch {
            repository.deleteStudentProfile(profile)
        }
    }

    fun createOrUpdateStudentProgress(progress: StudentProgressEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateStudentProgress(progress)
        }
    }

    fun recordMathScore(
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
        difficulty: String = "Standard"
    ) {
        viewModelScope.launch {
            val entry = MathArenaScoreEntity(
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
                formattedDate = "Today"
            )
            repository.recordMathArenaScore(entry)
            if (tokens > 0 || xp > 0) {
                repository.addReward(
                    title = "Arena Score: $gameMode",
                    tokensDelta = tokens,
                    xpDelta = xp,
                    category = "Arena Game",
                    addBossTicket = false
                )
            }
        }
    }

    fun deleteMathScore(score: MathArenaScoreEntity) {
        viewModelScope.launch {
            repository.deleteMathArenaScore(score)
        }
    }

    fun recordAnzanDuel(
        opponentName: String,
        opponentTier: String = "Diamond",
        opponentType: String = "PEER",
        playerScore: Int,
        opponentScore: Int,
        totalRounds: Int = 5,
        correctAnswers: Int,
        playerAccuracy: Float,
        avgLatencyMs: Long,
        maxCombo: Int,
        tokens: Int,
        xp: Int
    ) {
        viewModelScope.launch {
            val isWin = playerScore > opponentScore
            val isTie = playerScore == opponentScore
            val status = when {
                isWin -> "VICTORY"
                isTie -> "DRAW"
                else -> "DEFEAT"
            }
            val duelEntity = AnzanDuelResultEntity(
                studentId = userProfile.value.uid,
                studentName = userProfile.value.name,
                opponentName = opponentName,
                opponentClass = userProfile.value.className,
                opponentTier = opponentTier,
                opponentType = opponentType,
                operationType = "ADD_SUB",
                digitCount = 2,
                rowCount = 2,
                flashSpeedMs = 200,
                totalRounds = totalRounds,
                playerScore = playerScore,
                opponentScore = opponentScore,
                resultStatus = status,
                isVictory = isWin,
                correctAnswers = correctAnswers,
                playerAccuracyPercent = playerAccuracy,
                avgLatencyMs = avgLatencyMs,
                maxCombo = maxCombo,
                tokensEarned = tokens,
                xpEarned = xp,
                timestamp = System.currentTimeMillis(),
                formattedDate = "Today"
            )
            repository.recordAnzanDuelResult(duelEntity)

            // Also update student progress in Room
            val currentProg = studentProgressList.value.find { it.studentId == userProfile.value.uid }
            if (currentProg != null) {
                val newAttempted = currentProg.problemsAttempted + totalRounds
                val newCorrect = currentProg.problemsSolvedCorrectly + correctAnswers
                val newAcc = ((newCorrect.toFloat() / newAttempted) * 100).toInt()
                val updatedProg = currentProg.copy(
                    problemsAttempted = newAttempted,
                    problemsSolvedCorrectly = newCorrect,
                    mentalAccuracyPercent = newAcc,
                    duelsWon = if (isWin) currentProg.duelsWon + 1 else currentProg.duelsWon,
                    duelsTotal = currentProg.duelsTotal + 1,
                    totalXp = currentProg.totalXp + xp,
                    totalTokens = currentProg.totalTokens + tokens,
                    lastUpdatedTimestamp = System.currentTimeMillis()
                )
                repository.insertOrUpdateStudentProgress(updatedProg)
            }
        }
    }

    fun deleteAnzanDuel(duel: AnzanDuelResultEntity) {
        viewModelScope.launch {
            repository.deleteAnzanDuelResult(duel)
        }
    }

    fun recordTrainingSession(
        sessionType: String,
        drillName: String,
        formulaCategory: String = "Friend of 10",
        digitCount: Int = 2,
        rowCount: Int = 4,
        flashSpeedMs: Int = 220,
        durationSeconds: Int = 120,
        attempted: Int,
        correct: Int,
        accuracy: Float,
        avgResponseMs: Long,
        tokens: Int,
        xp: Int,
        highestCombo: Int = 1,
        score: Int = 0,
        rating: String = "A"
    ) {
        viewModelScope.launch {
            val session = TrainingSessionStatsEntity(
                studentId = userProfile.value.uid,
                studentName = userProfile.value.name,
                sessionType = sessionType,
                drillName = drillName,
                formulaCategory = formulaCategory,
                digitCount = digitCount,
                rowCount = rowCount,
                flashSpeedMs = flashSpeedMs,
                durationSeconds = durationSeconds,
                problemsAttempted = attempted,
                problemsCorrect = correct,
                accuracyPercentage = accuracy,
                avgResponseTimeMs = avgResponseMs,
                tokensEarned = tokens,
                xpEarned = xp,
                highestCombo = highestCombo,
                score = score,
                rating = rating,
                timestamp = System.currentTimeMillis(),
                formattedDate = "Today"
            )
            repository.recordTrainingSession(session)

            // Update student progress statistics in Room
            val currentProg = studentProgressList.value.find { it.studentId == userProfile.value.uid }
            if (currentProg != null) {
                val newAttempted = currentProg.problemsAttempted + attempted
                val newCorrect = currentProg.problemsSolvedCorrectly + correct
                val newAcc = ((newCorrect.toFloat() / newAttempted) * 100).toInt()
                val updatedProg = currentProg.copy(
                    problemsAttempted = newAttempted,
                    problemsSolvedCorrectly = newCorrect,
                    mentalAccuracyPercent = newAcc,
                    trainingSessionsCount = currentProg.trainingSessionsCount + 1,
                    totalXp = currentProg.totalXp + xp,
                    totalTokens = currentProg.totalTokens + tokens,
                    weeklyPracticeMinutes = currentProg.weeklyPracticeMinutes + (durationSeconds / 60),
                    lastUpdatedTimestamp = System.currentTimeMillis()
                )
                repository.insertOrUpdateStudentProgress(updatedProg)
            }
        }
    }

    fun deleteTrainingSession(session: TrainingSessionStatsEntity) {
        viewModelScope.launch {
            repository.deleteTrainingSession(session)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
