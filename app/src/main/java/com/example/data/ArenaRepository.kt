package com.example.data

import com.example.data.dao.AnzanDuelDao
import com.example.data.dao.ArenaDao
import com.example.data.dao.StudentArenaDao
import com.example.data.dao.TrainingStatsDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class ArenaRepository(
    private val dao: ArenaDao,
    private val studentArenaDao: StudentArenaDao? = null,
    private val anzanDuelDao: AnzanDuelDao? = null,
    private val trainingStatsDao: TrainingStatsDao? = null,
    private val firestore: com.google.firebase.firestore.FirebaseFirestore? = null
) {

    private fun syncToFirestore(collection: String, documentId: String, data: Map<String, Any?>) {
        try {
            firestore?.collection(collection)?.document(documentId)?.set(data)
        } catch (e: Exception) {
            android.util.Log.w("ArenaRepository", "Firestore sync skipped: ${e.message}")
        }
    }

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val transactions: Flow<List<TokenTransaction>> = dao.getAllTransactions()
    val rosterStudents: Flow<List<RosterStudent>> = dao.getAllRosterStudents()
    val homeworkRecords: Flow<List<HomeworkRecord>> = dao.getAllHomeworkRecords()
    val speedLeague: Flow<List<SpeedLeagueEntry>> = dao.getSpeedLeague()
    val attendanceLogs: Flow<List<AttendanceLog>> = dao.getAttendanceLogs()
    val classDiary: Flow<List<ClassDiaryEntry>> = dao.getAllClassDiary()
    val notices: Flow<List<NoticeEvent>> = dao.getAllNotices()
    val avatarItems: Flow<List<AvatarItem>> = dao.getAllAvatarItems()
    val analytics: Flow<AnalyticsRecord?> = dao.getAnalytics()
    val pendingStudents: Flow<List<PendingStudent>> = dao.getAllPendingStudents()
    val batches: Flow<List<StudentBatch>> = dao.getAllBatches()
    val batchAttendance: Flow<List<BatchAttendanceRecord>> = dao.getAllBatchAttendance()

    // Room persistence flows for Student Profiles, Progress, and Math Arena Scores
    val studentProfiles: Flow<List<StudentProfileEntity>> = studentArenaDao?.getAllStudentProfiles() ?: flowOf(emptyList())
    val studentProgressList: Flow<List<StudentProgressEntity>> = studentArenaDao?.getAllStudentProgress() ?: flowOf(emptyList())
    val mathArenaScores: Flow<List<MathArenaScoreEntity>> = studentArenaDao?.getAllScores() ?: flowOf(emptyList())

    // Room persistence flows for Anzan Duels & Training Sessions
    val duelResults: Flow<List<AnzanDuelResultEntity>> = anzanDuelDao?.getAllDuelResults() ?: flowOf(emptyList())
    val trainingSessions: Flow<List<TrainingSessionStatsEntity>> = trainingStatsDao?.getAllTrainingSessions() ?: flowOf(emptyList())

    suspend fun initializeDataIfEmpty() {
        val currentProfile = dao.getUserProfile().firstOrNull()
        if (currentProfile == null) {
            val initialProfile = UserProfile(
                uid = "NHSSASTD4879",
                name = "SREEHITH A S",
                className = "Class VI-A",
                email = "sreehithas23@gmail.com",
                accountStatus = "APPROVED",
                tokens = 1450,
                xp = 3850,
                level = 14,
                streakDays = 7,
                lastCheckInDate = "",
                activeTheme = "RED",
                activeSkin = "Gold Imperial",
                equippedShirt = "Academy Red Blazer",
                equippedPants = "Classic Black Slacks",
                equippedAccessory = "Golden Abacus Belt",
                bossTickets = 3,
                feeStatus = "Due",
                feeAmount = 2400,
                attendancePercent = 92,
                attendedSessions = 11,
                totalSessions = 12
            )
            dao.insertOrUpdateProfile(initialProfile)

            // Initial transactions
            dao.insertTransaction(TokenTransaction(title = "Welcome Bonus", amount = 500, isCredit = true, category = "Bonus"))
            dao.insertTransaction(TokenTransaction(title = "7-Day Streak Milestone", amount = 250, isCredit = true, category = "Milestone"))
            dao.insertTransaction(TokenTransaction(title = "Anzan Speed Drill Victory", amount = 50, isCredit = true, category = "Practice"))
            dao.insertTransaction(TokenTransaction(title = "Boss Raid: Conqueror Slayer", amount = 500, isCredit = true, category = "Raid"))
            dao.insertTransaction(TokenTransaction(title = "Bead Skin: Gold Imperial", amount = 350, isCredit = false, category = "Shop"))

            // Initial class roster
            val roster = listOf(
                RosterStudent("std_1", "SREEHITH A S", "VI-01", "Diamond", 98, 220, 95, 1450, true),
                RosterStudent("std_2", "Aarav Sharma", "VI-02", "Platinum", 94, 280, 92, 1200, true),
                RosterStudent("std_3", "Tanvi Patil", "VI-03", "Platinum", 96, 260, 100, 1340, true),
                RosterStudent("std_4", "Rohan Kulkarni", "VI-04", "Gold", 88, 350, 85, 950, false),
                RosterStudent("std_5", "Ananya Deshmukh", "VI-05", "Gold", 91, 310, 90, 1100, true),
                RosterStudent("std_6", "Devansh Nair", "VI-06", "Silver", 82, 420, 80, 720, true)
            )
            dao.insertRosterStudents(roster)

            // Speed League leaderboard
            val league = listOf(
                SpeedLeagueEntry(1, "Tanvi Patil", "Diamond", 210, 99),
                SpeedLeagueEntry(2, "Aarav Sharma", "Diamond", 215, 97),
                SpeedLeagueEntry(3, "SREEHITH A S", "Diamond", 220, 98, isCurrentUser = true),
                SpeedLeagueEntry(4, "Ananya Deshmukh", "Platinum", 270, 94),
                SpeedLeagueEntry(5, "Rohan Kulkarni", "Platinum", 310, 90),
                SpeedLeagueEntry(6, "Devansh Nair", "Gold", 380, 85),
                SpeedLeagueEntry(7, "Pooja Varma", "Gold", 410, 84),
                SpeedLeagueEntry(8, "Kavya Iyer", "Silver", 460, 81)
            )
            dao.insertSpeedLeague(league)

            // Homework records
            val homework = listOf(
                HomeworkRecord(
                    id = "hw_101",
                    title = "Worksheet #42: Big Friend +9 & +8",
                    date = "Today",
                    status = "Verified",
                    score = "20 / 20 (100%)",
                    feedback = "Flawless finger dexterity and formula transitions! Approved with Distinction.",
                    formulaUsed = "+9 = -1 + 10, +8 = -2 + 10"
                ),
                HomeworkRecord(
                    id = "hw_102",
                    title = "Worksheet #41: Combination Formulas",
                    date = "Yesterday",
                    status = "Verified",
                    score = "19 / 20 (95%)",
                    feedback = "Great speed! Be careful with the carry bead on column 3.",
                    formulaUsed = "+6 = +1 - 5 + 10"
                ),
                HomeworkRecord(
                    id = "hw_103",
                    title = "Worksheet #40: Rapid Flash 2-Digit",
                    date = "3 days ago",
                    status = "Verified",
                    score = "18 / 20 (90%)",
                    feedback = "Teacher Meera Joshi: Very strong visual memory on mental anzan.",
                    formulaUsed = "Direct 2-Digit Mental Add"
                )
            )
            dao.insertHomeworkRecords(homework)

            // Monthly attendance (30 days)
            val logs = (1..30).map { day ->
                val status = when {
                    day % 7 == 0 -> "Holiday"
                    day in listOf(4, 18) -> "Absent"
                    else -> "Present"
                }
                AttendanceLog(day = day, monthYear = "October 2026", status = status)
            }
            dao.insertAttendanceLogs(logs)

            // Initial Class Diary (Classwork and Homework entries for Parents portal)
            val diary = listOf(
                ClassDiaryEntry(
                    id = "cd_1",
                    date = "Today (23 Oct 2026)",
                    type = "CLASSWORK",
                    title = "Class VI-A: Master Formula +7 & +8 (Big Friends)",
                    description = "Practiced 40 oral mental calculations using Friend of 10. Completed 4-row flash calculations at 350ms speed. Physical abacus rod speed test conducted.",
                    formulaFocus = "+7 = -3 + 10, +8 = -2 + 10"
                ),
                ClassDiaryEntry(
                    id = "cd_2",
                    date = "Today (23 Oct 2026)",
                    type = "HOMEWORK",
                    title = "Worksheet Page 43 & 44 (Oral + Written)",
                    description = "Complete 20 sums on Page 43 using mental anzan. Time yourself and note the seconds taken on Page 44. Upload worksheet in AI Sheet Scanner.",
                    formulaFocus = "Combination (+6, +7, +8, +9)"
                ),
                ClassDiaryEntry(
                    id = "cd_3",
                    date = "21 Oct 2026",
                    type = "CLASSWORK",
                    title = "2-Digit Addition Drill & Flash Speed Tuning",
                    description = "Trained on rapid bead placement on upper deck (5s). Introduced 1-v-1 Anzan duels between students.",
                    formulaFocus = "Direct Mental Addition"
                ),
                ClassDiaryEntry(
                    id = "cd_4",
                    date = "21 Oct 2026",
                    type = "HOMEWORK",
                    title = "Worksheet #41 Revision",
                    description = "Practice 15 flash arithmetic sets in the Student Zone Arcade mode at 500ms speed.",
                    formulaFocus = "Flash Speed Survival"
                )
            )
            dao.insertClassDiaryList(diary)

            // Initial Center Notices
            val noticesList = listOf(
                NoticeEvent(
                    id = "n_1",
                    title = "32nd UCMAS National Abacus Competition 2026",
                    description = "Registration open for Class VI students. Exam date: 15th November 2026 at CIDCO Exhibition Centre.",
                    date = "15 Nov 2026",
                    venue = "CIDCO Exhibition Centre, Vashi, Navi Mumbai",
                    category = "Competition"
                ),
                NoticeEvent(
                    id = "n_2",
                    title = "Formula Mastery Workshop (Friend of 10)",
                    description = "Special session with Grandmaster Trainer at Thane Kopri East Center.",
                    date = "26 Oct 2026",
                    venue = "UCMAS Kopri East Branch, Room 2",
                    category = "Workshop"
                ),
                NoticeEvent(
                    id = "n_3",
                    title = "Term Evaluation & Level Promotion Exam",
                    description = "Assessment for Level 14 to Level 15 Grandmaster promotion. Hall tickets distributed next week.",
                    date = "05 Nov 2026",
                    venue = "Main Examination Hall",
                    category = "Exam"
                )
            )
            dao.insertNoticesList(noticesList)

            // Initial Avatar Items (Avatar Wardrobe)
            val avatars = listOf(
                // Shirts
                AvatarItem("sh_1", "Academy Red Blazer", "SHIRT", 0, false, true, 0xFFD32F2F, "Official UCMAS Academy Uniform Blazer"),
                AvatarItem("sh_2", "Cyber Neon Hoodie", "SHIRT", 200, false, false, 0xFF00E5FF, "Glow-in-the-dark calculation hoodie"),
                AvatarItem("sh_3", "Golden Dragon Gi", "SHIRT", 350, false, false, 0xFFFFB300, "Gold-embroidered martial arts math gi"),
                AvatarItem("sh_4", "Teacher Grandmaster Robe", "SHIRT", 0, true, false, 0xFF6A1B9A, "Awarded exclusively by Teacher for top distinction"),

                // Pants
                AvatarItem("pa_1", "Classic Black Slacks", "PANTS", 0, false, true, 0xFF212121, "Crisp formal academy trousers"),
                AvatarItem("pa_2", "Speedster Track Pants", "PANTS", 150, false, false, 0xFF1565C0, "Aerodynamic lightning racing pants"),
                AvatarItem("pa_3", "Imperial Gold Armor Pants", "PANTS", 300, false, false, 0xFFFF8F00, "Armored plates earned through speed drills"),
                AvatarItem("pa_4", "National Championship Hakama", "PANTS", 0, true, false, 0xFFB71C1C, "Teacher Exclusive: Best in State honors"),

                // Accessories
                AvatarItem("ac_1", "Golden Abacus Belt", "ACCESSORY", 0, false, true, 0xFFFFD700, "Carries mini brass soroban beads"),
                AvatarItem("ac_2", "Lightning Mind Visor", "ACCESSORY", 250, false, false, 0xFF00E5FF, "Digital HUD tracking millisecond latency"),
                AvatarItem("ac_3", "Dragon Speed Aura", "ACCESSORY", 400, false, false, 0xFFFF5252, "Burning arithmetic energy surrounding avatar"),
                AvatarItem("ac_4", "Grandmaster Laurel Crown", "ACCESSORY", 0, true, false, 0xFFFFD700, "Teacher Exclusive: Conferred by Tr. Meera Joshi"),

                // Heads
                AvatarItem("he_1", "Classic Blocky Head", "HEAD", 0, false, true, 0xFFFFE082, "Iconic blocky head with cheerful smile"),
                AvatarItem("he_2", "Ninja Math Headband", "HEAD", 180, false, false, 0xFFC62828, "Tied with Thane Kopri East insignia")
            )
            dao.insertAvatarItemsList(avatars)

            // Initial Analytics record
            dao.insertOrUpdateAnalytics(
                AnalyticsRecord(
                    id = 1,
                    addLatencyMs = 210,
                    subLatencyMs = 235,
                    mulLatencyMs = 310,
                    divLatencyMs = 390,
                    monAcc = 92,
                    tueAcc = 95,
                    wedAcc = 93,
                    thuAcc = 98,
                    friAcc = 96,
                    satAcc = 100,
                    sunAcc = 98
                )
            )

            // Initial Pending Student approval (Matching user's exact Aran Jain example!)
            dao.insertPendingStudent(
                PendingStudent(
                    id = "pend_aran",
                    name = "Aran Jain",
                    email = "aran.jain@example.com",
                    requestedClass = "Class VI-A",
                    dateRegistered = "Today (Pending Approval)",
                    status = "PENDING"
                )
            )
        }

        // Ensure batches are seeded
        val currentBatches = dao.getAllBatches().firstOrNull()
        if (currentBatches.isNullOrEmpty()) {
            val initialBatches = listOf(
                StudentBatch("batch_mon", "Monday Champions", "Monday", "5:00 PM - 7:00 PM", "std_1,std_3,std_4"),
                StudentBatch("batch_thu", "Thursday Elite", "Thursday", "5:00 PM - 7:00 PM", "std_2,std_5"),
                StudentBatch("batch_sat", "Saturday Masters", "Saturday", "10:00 AM - 12:00 PM", "std_6"),
                StudentBatch("batch_sun", "Sunday Foundation", "Sunday", "10:00 AM - 12:00 PM", "")
            )
            dao.insertBatches(initialBatches)

            val initialAttendance = listOf(
                BatchAttendanceRecord("batch_mon_std_1_24_Oct_2026", "batch_mon", "std_1", "SREEHITH A S", "24 Oct 2026", "PRESENT"),
                BatchAttendanceRecord("batch_mon_std_3_24_Oct_2026", "batch_mon", "std_3", "Tanvi Patil", "24 Oct 2026", "PRESENT"),
                BatchAttendanceRecord("batch_mon_std_4_24_Oct_2026", "batch_mon", "std_4", "Rohan Kulkarni", "24 Oct 2026", "LATE_HALF_DAY"),
                BatchAttendanceRecord("batch_thu_std_2_24_Oct_2026", "batch_thu", "std_2", "Aarav Sharma", "24 Oct 2026", "PRESENT"),
                BatchAttendanceRecord("batch_thu_std_5_24_Oct_2026", "batch_thu", "std_5", "Ananya Deshmukh", "24 Oct 2026", "PRESENT"),
                BatchAttendanceRecord("batch_sat_std_6_24_Oct_2026", "batch_sat", "std_6", "Devansh Nair", "24 Oct 2026", "PRESENT")
            )
            dao.insertBatchAttendanceList(initialAttendance)
        }

        // Initialize Student Profiles, Progress, and Math Arena Scores if empty
        if (studentArenaDao != null) {
            val existingProfiles = studentArenaDao.getAllStudentProfiles().firstOrNull()
            if (existingProfiles.isNullOrEmpty()) {
                val seedProfiles = listOf(
                    StudentProfileEntity(
                        studentId = "std_1",
                        fullName = "SREEHITH A S",
                        rollNumber = "VI-01",
                        gradeLevel = "Level 4 - Higher Grand Master",
                        email = "sreehithas23@gmail.com",
                        phoneNumber = "+91 98200 12345",
                        parentName = "Suresh A",
                        parentContact = "+91 98200 12340",
                        batchId = "batch_mon",
                        batchDay = "Monday",
                        admissionDate = "2026-01-15",
                        avatarSkin = "Gold Imperial",
                        totalTokens = 1450,
                        currentXp = 3850,
                        rankTitle = "Master Speedster"
                    ),
                    StudentProfileEntity(
                        studentId = "std_2",
                        fullName = "Aarav Sharma",
                        rollNumber = "VI-02",
                        gradeLevel = "Level 4 - Grand Master",
                        email = "aarav.s@example.com",
                        phoneNumber = "+91 98201 23456",
                        parentName = "Vikram Sharma",
                        parentContact = "+91 98201 23450",
                        batchId = "batch_thu",
                        batchDay = "Thursday",
                        admissionDate = "2026-02-10",
                        avatarSkin = "Silver Classic",
                        totalTokens = 1200,
                        currentXp = 3200,
                        rankTitle = "Lightning Abacist"
                    ),
                    StudentProfileEntity(
                        studentId = "std_3",
                        fullName = "Tanvi Patil",
                        rollNumber = "VI-03",
                        gradeLevel = "Level 5 - Supreme Grand Master",
                        email = "tanvi.p@example.com",
                        phoneNumber = "+91 98202 34567",
                        parentName = "Pradeep Patil",
                        parentContact = "+91 98202 34560",
                        batchId = "batch_mon",
                        batchDay = "Monday",
                        admissionDate = "2025-11-20",
                        avatarSkin = "Imperial Jade",
                        totalTokens = 1340,
                        currentXp = 3600,
                        rankTitle = "Mental Anzan Ace"
                    ),
                    StudentProfileEntity(
                        studentId = "std_4",
                        fullName = "Rohan Kulkarni",
                        rollNumber = "VI-04",
                        gradeLevel = "Level 3 - Advanced Master",
                        email = "rohan.k@example.com",
                        phoneNumber = "+91 98203 45678",
                        parentName = "Nitin Kulkarni",
                        parentContact = "+91 98203 45670",
                        batchId = "batch_mon",
                        batchDay = "Monday",
                        admissionDate = "2026-03-01",
                        avatarSkin = "Bronze Fighter",
                        totalTokens = 950,
                        currentXp = 2400,
                        rankTitle = "Rapid Adder"
                    ),
                    StudentProfileEntity(
                        studentId = "std_5",
                        fullName = "Ananya Deshmukh",
                        rollNumber = "VI-05",
                        gradeLevel = "Level 4 - Grand Master",
                        email = "ananya.d@example.com",
                        phoneNumber = "+91 98204 56789",
                        parentName = "Rajesh Deshmukh",
                        parentContact = "+91 98204 56780",
                        batchId = "batch_thu",
                        batchDay = "Thursday",
                        admissionDate = "2026-02-18",
                        avatarSkin = "Gold Imperial",
                        totalTokens = 1100,
                        currentXp = 2900,
                        rankTitle = "Formula Specialist"
                    ),
                    StudentProfileEntity(
                        studentId = "std_6",
                        fullName = "Devansh Nair",
                        rollNumber = "VI-06",
                        gradeLevel = "Level 3 - Intermediate",
                        email = "devansh.n@example.com",
                        phoneNumber = "+91 98205 67890",
                        parentName = "Manoj Nair",
                        parentContact = "+91 98205 67891",
                        batchId = "batch_sat",
                        batchDay = "Saturday",
                        admissionDate = "2026-04-05",
                        avatarSkin = "Classic Wood",
                        totalTokens = 720,
                        currentXp = 1850,
                        rankTitle = "Apprentice Scholar"
                    )
                )
                studentArenaDao.insertStudentProfiles(seedProfiles)

                // Seed Progress Metrics
                val seedProgress = listOf(
                    StudentProgressEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        currentLevel = 4,
                        term = "Term 2",
                        abacusSpeedMs = 210,
                        flashAnzanSpeedMs = 180,
                        mentalAccuracyPercent = 98,
                        problemsAttempted = 1420,
                        problemsSolvedCorrectly = 1395,
                        formulasMastered = 19,
                        formulasTotal = 20,
                        attendanceRatePercent = 95,
                        streakDays = 7,
                        leagueTier = "Diamond",
                        weeklyPracticeMinutes = 180
                    ),
                    StudentProgressEntity(
                        studentId = "std_2",
                        studentName = "Aarav Sharma",
                        currentLevel = 4,
                        term = "Term 2",
                        abacusSpeedMs = 230,
                        flashAnzanSpeedMs = 215,
                        mentalAccuracyPercent = 94,
                        problemsAttempted = 1180,
                        problemsSolvedCorrectly = 1110,
                        formulasMastered = 17,
                        formulasTotal = 20,
                        attendanceRatePercent = 92,
                        streakDays = 5,
                        leagueTier = "Platinum",
                        weeklyPracticeMinutes = 140
                    ),
                    StudentProgressEntity(
                        studentId = "std_3",
                        studentName = "Tanvi Patil",
                        currentLevel = 5,
                        term = "Term 3",
                        abacusSpeedMs = 195,
                        flashAnzanSpeedMs = 170,
                        mentalAccuracyPercent = 99,
                        problemsAttempted = 1680,
                        problemsSolvedCorrectly = 1665,
                        formulasMastered = 20,
                        formulasTotal = 20,
                        attendanceRatePercent = 100,
                        streakDays = 14,
                        leagueTier = "Diamond",
                        weeklyPracticeMinutes = 210
                    ),
                    StudentProgressEntity(
                        studentId = "std_4",
                        studentName = "Rohan Kulkarni",
                        currentLevel = 3,
                        term = "Term 1",
                        abacusSpeedMs = 310,
                        flashAnzanSpeedMs = 280,
                        mentalAccuracyPercent = 88,
                        problemsAttempted = 890,
                        problemsSolvedCorrectly = 785,
                        formulasMastered = 14,
                        formulasTotal = 20,
                        attendanceRatePercent = 85,
                        streakDays = 3,
                        leagueTier = "Gold",
                        weeklyPracticeMinutes = 95
                    ),
                    StudentProgressEntity(
                        studentId = "std_5",
                        studentName = "Ananya Deshmukh",
                        currentLevel = 4,
                        term = "Term 2",
                        abacusSpeedMs = 250,
                        flashAnzanSpeedMs = 225,
                        mentalAccuracyPercent = 93,
                        problemsAttempted = 1050,
                        problemsSolvedCorrectly = 975,
                        formulasMastered = 16,
                        formulasTotal = 20,
                        attendanceRatePercent = 90,
                        streakDays = 6,
                        leagueTier = "Platinum",
                        weeklyPracticeMinutes = 125
                    ),
                    StudentProgressEntity(
                        studentId = "std_6",
                        studentName = "Devansh Nair",
                        currentLevel = 3,
                        term = "Term 1",
                        abacusSpeedMs = 380,
                        flashAnzanSpeedMs = 340,
                        mentalAccuracyPercent = 82,
                        problemsAttempted = 620,
                        problemsSolvedCorrectly = 510,
                        formulasMastered = 12,
                        formulasTotal = 20,
                        attendanceRatePercent = 80,
                        streakDays = 2,
                        leagueTier = "Silver",
                        weeklyPracticeMinutes = 70
                    )
                )
                studentArenaDao.insertProgressList(seedProgress)

                // Seed Math Arena Scores
                val seedScores = listOf(
                    MathArenaScoreEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        gameMode = "FLASH_ANZAN",
                        score = 980,
                        accuracyPercentage = 98.5f,
                        problemsAttempted = 20,
                        problemsCorrect = 20,
                        avgTimePerProblemMs = 220,
                        tokensEarned = 50,
                        xpEarned = 120,
                        difficultyLevel = "Level 4 Rapid",
                        formattedDate = "Today"
                    ),
                    MathArenaScoreEntity(
                        studentId = "std_3",
                        studentName = "Tanvi Patil",
                        gameMode = "SPEED_SPRINT",
                        score = 995,
                        accuracyPercentage = 99.0f,
                        problemsAttempted = 25,
                        problemsCorrect = 25,
                        avgTimePerProblemMs = 210,
                        tokensEarned = 60,
                        xpEarned = 140,
                        difficultyLevel = "Level 5 Supreme",
                        formattedDate = "Today"
                    ),
                    MathArenaScoreEntity(
                        studentId = "std_2",
                        studentName = "Aarav Sharma",
                        gameMode = "ANZAN_DUEL",
                        score = 940,
                        accuracyPercentage = 96.0f,
                        problemsAttempted = 18,
                        problemsCorrect = 17,
                        avgTimePerProblemMs = 260,
                        tokensEarned = 40,
                        xpEarned = 100,
                        difficultyLevel = "Level 4 Competitive",
                        formattedDate = "Yesterday"
                    ),
                    MathArenaScoreEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        gameMode = "BOSS_BATTLE",
                        score = 1450,
                        accuracyPercentage = 95.0f,
                        problemsAttempted = 32,
                        problemsCorrect = 30,
                        avgTimePerProblemMs = 240,
                        tokensEarned = 100,
                        xpEarned = 250,
                        difficultyLevel = "Conqueror Titan Raid",
                        formattedDate = "Yesterday"
                    ),
                    MathArenaScoreEntity(
                        studentId = "std_5",
                        studentName = "Ananya Deshmukh",
                        gameMode = "DECIMAL_DASH",
                        score = 890,
                        accuracyPercentage = 92.5f,
                        problemsAttempted = 20,
                        problemsCorrect = 19,
                        avgTimePerProblemMs = 290,
                        tokensEarned = 35,
                        xpEarned = 85,
                        difficultyLevel = "Double Decimals",
                        formattedDate = "2 days ago"
                    ),
                    MathArenaScoreEntity(
                        studentId = "std_4",
                        studentName = "Rohan Kulkarni",
                        gameMode = "FLASH_ANZAN",
                        score = 820,
                        accuracyPercentage = 88.0f,
                        problemsAttempted = 20,
                        problemsCorrect = 16,
                        avgTimePerProblemMs = 340,
                        tokensEarned = 30,
                        xpEarned = 75,
                        difficultyLevel = "Single & Double Digit",
                        formattedDate = "3 days ago"
                    )
                )
                studentArenaDao.insertScores(seedScores)
            }
        }

        // Seed initial Anzan Duel Results
        if (anzanDuelDao != null) {
            val existingDuels = anzanDuelDao.getAllDuelResults().firstOrNull()
            if (existingDuels.isNullOrEmpty()) {
                val seedDuels = listOf(
                    AnzanDuelResultEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        opponentName = "Aarav Sharma",
                        opponentClass = "Class VI-A",
                        opponentTier = "Diamond",
                        opponentType = "PEER",
                        operationType = "ADD_SUB",
                        digitCount = 2,
                        rowCount = 2,
                        flashSpeedMs = 200,
                        totalRounds = 5,
                        playerScore = 135,
                        opponentScore = 95,
                        resultStatus = "VICTORY",
                        isVictory = true,
                        correctAnswers = 5,
                        playerAccuracyPercent = 100.0f,
                        avgLatencyMs = 215,
                        maxCombo = 4,
                        tokensEarned = 50,
                        xpEarned = 120,
                        formattedDate = "Today"
                    ),
                    AnzanDuelResultEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        opponentName = "Tanvi Patil",
                        opponentClass = "Class VI-A",
                        opponentTier = "Diamond",
                        opponentType = "RIVAL",
                        operationType = "ADD_SUB",
                        digitCount = 2,
                        rowCount = 2,
                        flashSpeedMs = 180,
                        totalRounds = 5,
                        playerScore = 110,
                        opponentScore = 125,
                        resultStatus = "DEFEAT",
                        isVictory = false,
                        correctAnswers = 4,
                        playerAccuracyPercent = 80.0f,
                        avgLatencyMs = 240,
                        maxCombo = 2,
                        tokensEarned = 15,
                        xpEarned = 40,
                        formattedDate = "Yesterday"
                    ),
                    AnzanDuelResultEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        opponentName = "Rohan Kulkarni",
                        opponentClass = "Class VI-A",
                        opponentTier = "Platinum",
                        opponentType = "PEER",
                        operationType = "ADD_SUB",
                        digitCount = 2,
                        rowCount = 2,
                        flashSpeedMs = 250,
                        totalRounds = 5,
                        playerScore = 145,
                        opponentScore = 75,
                        resultStatus = "VICTORY",
                        isVictory = true,
                        correctAnswers = 5,
                        playerAccuracyPercent = 100.0f,
                        avgLatencyMs = 195,
                        maxCombo = 4,
                        tokensEarned = 50,
                        xpEarned = 120,
                        formattedDate = "2 days ago"
                    )
                )
                anzanDuelDao.insertDuelResults(seedDuels)
            }
        }

        // Seed initial Training Session Statistics
        if (trainingStatsDao != null) {
            val existingSessions = trainingStatsDao.getAllTrainingSessions().firstOrNull()
            if (existingSessions.isNullOrEmpty()) {
                val seedSessions = listOf(
                    TrainingSessionStatsEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        sessionType = "FLASH_ARCADE",
                        drillName = "Flash Anzan Sprint (2-Digit)",
                        formulaCategory = "Friend of 10",
                        digitCount = 2,
                        rowCount = 4,
                        flashSpeedMs = 220,
                        durationSeconds = 180,
                        problemsAttempted = 25,
                        problemsCorrect = 24,
                        accuracyPercentage = 96.0f,
                        avgResponseTimeMs = 210,
                        tokensEarned = 60,
                        xpEarned = 150,
                        highestCombo = 12,
                        score = 1240,
                        rating = "S",
                        formattedDate = "Today"
                    ),
                    TrainingSessionStatsEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        sessionType = "BOSS_RAID",
                        drillName = "Conqueror Titan Raid (Combination Formulas)",
                        formulaCategory = "Combination (+6, +7, +8, +9)",
                        digitCount = 2,
                        rowCount = 5,
                        flashSpeedMs = 240,
                        durationSeconds = 240,
                        problemsAttempted = 32,
                        problemsCorrect = 30,
                        accuracyPercentage = 93.8f,
                        avgResponseTimeMs = 235,
                        tokensEarned = 120,
                        xpEarned = 300,
                        highestCombo = 15,
                        score = 2850,
                        rating = "S",
                        formattedDate = "Yesterday"
                    ),
                    TrainingSessionStatsEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        sessionType = "SPEED_LEAGUE",
                        drillName = "Diamond Speed League Qualifier",
                        formulaCategory = "Direct Mental Addition",
                        digitCount = 2,
                        rowCount = 3,
                        flashSpeedMs = 200,
                        durationSeconds = 120,
                        problemsAttempted = 20,
                        problemsCorrect = 20,
                        accuracyPercentage = 100.0f,
                        avgResponseTimeMs = 195,
                        tokensEarned = 50,
                        xpEarned = 130,
                        highestCombo = 20,
                        score = 1580,
                        rating = "S",
                        formattedDate = "2 days ago"
                    ),
                    TrainingSessionStatsEntity(
                        studentId = "std_1",
                        studentName = "SREEHITH A S",
                        sessionType = "ABACUS_PRACTICE",
                        drillName = "Physical Soroban Bead Speed Drill",
                        formulaCategory = "Friend of 5 (+4..+1)",
                        digitCount = 1,
                        rowCount = 6,
                        flashSpeedMs = 300,
                        durationSeconds = 300,
                        problemsAttempted = 40,
                        problemsCorrect = 39,
                        accuracyPercentage = 97.5f,
                        avgResponseTimeMs = 250,
                        tokensEarned = 75,
                        xpEarned = 180,
                        highestCombo = 25,
                        score = 1920,
                        rating = "A",
                        formattedDate = "3 days ago"
                    )
                )
                trainingStatsDao.insertTrainingSessions(seedSessions)
            }
        }
    }

    suspend fun updateProfile(profile: UserProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun addReward(
        title: String,
        tokensDelta: Int,
        xpDelta: Int,
        category: String,
        addBossTicket: Boolean = false
    ) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        val newTokens = (profile.tokens + tokensDelta).coerceAtLeast(0)
        val newXp = profile.xp + xpDelta
        val newLevel = (newXp / 300) + 1
        val newTickets = if (addBossTicket) profile.bossTickets + 1 else profile.bossTickets

        val updated = profile.copy(
            tokens = newTokens,
            xp = newXp,
            level = newLevel,
            bossTickets = newTickets
        )
        dao.insertOrUpdateProfile(updated)

        dao.insertTransaction(
            TokenTransaction(
                title = title,
                amount = kotlin.math.abs(tokensDelta),
                isCredit = tokensDelta >= 0,
                category = category
            )
        )
    }

    suspend fun claimDailyCheckIn(): Boolean {
        val profile = dao.getUserProfile().firstOrNull() ?: return false
        val todayStr = "2026-10-23"
        if (profile.lastCheckInDate == todayStr) return false

        val newStreak = profile.streakDays + 1
        var tokensEarned = 50
        var xpEarned = 50

        val milestoneHit = newStreak % 7 == 0
        if (milestoneHit) {
            tokensEarned += 250
            xpEarned += 500
        }

        val updated = profile.copy(
            tokens = profile.tokens + tokensEarned,
            xp = profile.xp + xpEarned,
            level = ((profile.xp + xpEarned) / 300) + 1,
            streakDays = newStreak,
            lastCheckInDate = todayStr
        )
        dao.insertOrUpdateProfile(updated)

        dao.insertTransaction(
            TokenTransaction(
                title = if (milestoneHit) "7-Day Streak Milestone Reward!" else "Daily Attendance Check-In",
                amount = tokensEarned,
                isCredit = true,
                category = "Daily Streak"
            )
        )
        return true
    }

    suspend fun payFees(): Boolean {
        return payFeesWithRef("UPI-${System.currentTimeMillis().toString().takeLast(8)}")
    }

    suspend fun payFeesWithRef(utr: String): Boolean {
        val profile = dao.getUserProfile().firstOrNull() ?: return false
        val updated = profile.copy(feeStatus = "Paid")
        dao.insertOrUpdateProfile(updated)
        dao.insertTransaction(
            TokenTransaction(
                title = "Term Fee Cleared (Ref: $utr)",
                amount = profile.feeAmount,
                isCredit = false,
                category = "Fee Payment"
            )
        )
        syncToFirestore("fee_payments", "pay_${System.currentTimeMillis()}", mapOf(
            "studentId" to profile.uid,
            "studentName" to profile.name,
            "amount" to profile.feeAmount,
            "status" to "PAID",
            "utr" to utr,
            "timestamp" to System.currentTimeMillis()
        ))
        syncToFirestore("students", profile.uid, mapOf(
            "uid" to profile.uid,
            "name" to profile.name,
            "feeStatus" to "Paid",
            "tokens" to profile.tokens,
            "xp" to profile.xp,
            "level" to profile.level
        ))
        return true
    }

    suspend fun updateTheme(themeName: String) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        dao.insertOrUpdateProfile(profile.copy(activeTheme = themeName))
    }

    suspend fun equipSkin(skinName: String) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        dao.insertOrUpdateProfile(profile.copy(activeSkin = skinName))
    }

    suspend fun equipAvatarItem(category: String, itemName: String) {
        val profile = dao.getUserProfile().firstOrNull() ?: return
        val updated = when (category) {
            "SHIRT" -> profile.copy(equippedShirt = itemName)
            "PANTS" -> profile.copy(equippedPants = itemName)
            "ACCESSORY" -> profile.copy(equippedAccessory = itemName)
            else -> profile
        }
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun unlockAvatarItem(item: AvatarItem) {
        dao.updateAvatarItem(item.copy(isUnlocked = true))
    }

    suspend fun grantTeacherAvatar(itemId: String) {
        val items = dao.getAllAvatarItems().firstOrNull() ?: return
        val target = items.find { it.id == itemId } ?: return
        dao.updateAvatarItem(target.copy(isUnlocked = true))
    }

    suspend fun addRosterStudent(student: RosterStudent) {
        dao.insertSingleStudent(student)
    }

    suspend fun updateRosterStudent(student: RosterStudent) {
        dao.updateRosterStudent(student)
    }

    suspend fun deleteRosterStudent(student: RosterStudent) {
        dao.deleteRosterStudent(student)
    }

    suspend fun submitHomework(record: HomeworkRecord) {
        dao.insertSingleHomework(record)
        syncToFirestore("homework", record.id, mapOf(
            "id" to record.id,
            "title" to record.title,
            "date" to record.date,
            "status" to record.status,
            "score" to record.score,
            "feedback" to record.feedback,
            "formulaUsed" to record.formulaUsed
        ))
    }

    suspend fun updateHomeworkRecord(record: HomeworkRecord) {
        dao.updateHomeworkRecord(record)
        syncToFirestore("homework", record.id, mapOf(
            "id" to record.id,
            "title" to record.title,
            "date" to record.date,
            "status" to record.status,
            "score" to record.score,
            "feedback" to record.feedback,
            "formulaUsed" to record.formulaUsed
        ))
    }

    suspend fun addClassDiary(entry: ClassDiaryEntry) {
        dao.insertClassDiary(entry)
        syncToFirestore("class_diary", entry.id, mapOf(
            "id" to entry.id,
            "date" to entry.date,
            "type" to entry.type,
            "title" to entry.title,
            "description" to entry.description,
            "formulaFocus" to entry.formulaFocus,
            "teacherName" to entry.teacherName
        ))
    }

    suspend fun addNotice(notice: NoticeEvent) {
        dao.insertNotice(notice)
        syncToFirestore("notices", notice.id, mapOf(
            "id" to notice.id,
            "title" to notice.title,
            "date" to notice.date,
            "description" to notice.description,
            "venue" to notice.venue,
            "category" to notice.category
        ))
    }

    suspend fun updateAttendance(log: AttendanceLog) {
        dao.insertOrUpdateAttendance(log)
        syncToFirestore("attendance", "day_${log.day}", mapOf(
            "day" to log.day,
            "monthYear" to log.monthYear,
            "status" to log.status
        ))
    }

    suspend fun updateAnalytics(record: AnalyticsRecord) {
        dao.insertOrUpdateAnalytics(record)
        syncToFirestore("analytics", "current_record", mapOf(
            "addLatencyMs" to record.addLatencyMs,
            "subLatencyMs" to record.subLatencyMs,
            "mulLatencyMs" to record.mulLatencyMs,
            "divLatencyMs" to record.divLatencyMs,
            "monAcc" to record.monAcc,
            "tueAcc" to record.tueAcc,
            "wedAcc" to record.wedAcc,
            "thuAcc" to record.thuAcc,
            "friAcc" to record.friAcc,
            "satAcc" to record.satAcc,
            "sunAcc" to record.sunAcc
        ))
    }

    suspend fun saveBatch(batch: StudentBatch) {
        dao.insertBatch(batch)
        syncToFirestore("batches", batch.id, mapOf(
            "id" to batch.id,
            "name" to batch.name,
            "dayOfWeek" to batch.dayOfWeek,
            "timeSlot" to batch.timeSlot,
            "studentIds" to batch.studentIds
        ))
    }

    suspend fun deleteBatch(batch: StudentBatch) {
        dao.deleteBatch(batch)
    }

    suspend fun markBatchAttendance(batchId: String, studentId: String, studentName: String, date: String, status: String) {
        val record = BatchAttendanceRecord(
            id = "${batchId}_${studentId}_${date.replace(" ", "_")}",
            batchId = batchId,
            studentId = studentId,
            studentName = studentName,
            date = date,
            status = status
        )
        dao.insertBatchAttendance(record)
        syncToFirestore("batch_attendance", record.id, mapOf(
            "id" to record.id,
            "batchId" to record.batchId,
            "studentId" to record.studentId,
            "studentName" to record.studentName,
            "date" to record.date,
            "status" to record.status
        ))
    }

    suspend fun approvePendingStudent(studentId: String, assignedClass: String, assignedLevel: Int, assignedBatchId: String = "batch_mon") {
        val pendings = dao.getAllPendingStudents().firstOrNull() ?: return
        val st = pendings.find { it.id == studentId } ?: return
        dao.updatePendingStudent(st.copy(status = "APPROVED", requestedClass = assignedClass))

        syncToFirestore("pending_students", studentId, mapOf(
            "id" to st.id,
            "name" to st.name,
            "email" to st.email,
            "requestedClass" to assignedClass,
            "status" to "APPROVED",
            "assignedLevel" to assignedLevel,
            "batchId" to assignedBatchId
        ))

        val newStudentId = "std_${st.id}"
        // Also add to roster
        val newRoster = RosterStudent(
            id = newStudentId,
            name = st.name,
            rollNo = "VI-${(7..99).random()}",
            tier = "Gold",
            accuracy = 92,
            avgLatencyMs = 290,
            attendanceRate = 100,
            tokens = 500,
            isPresentToday = true,
            batchId = assignedBatchId
        )
        dao.insertSingleStudent(newRoster)

        // Assign to the selected batch
        val allBatches = dao.getAllBatches().firstOrNull() ?: emptyList()
        val targetBatch = allBatches.find { it.id == assignedBatchId }
        if (targetBatch != null) {
            val updatedIds = (targetBatch.getStudentIdList() + newStudentId).distinct()
            dao.updateBatch(targetBatch.withStudentIds(updatedIds))
        }

        syncToFirestore("roster", newRoster.id, mapOf(
            "id" to newRoster.id,
            "name" to newRoster.name,
            "rollNo" to newRoster.rollNo,
            "tokens" to newRoster.tokens,
            "tier" to newRoster.tier,
            "batchId" to assignedBatchId
        ))
    }


    suspend fun rejectPendingStudent(studentId: String) {
        val pendings = dao.getAllPendingStudents().firstOrNull() ?: return
        val st = pendings.find { it.id == studentId } ?: return
        dao.updatePendingStudent(st.copy(status = "REJECTED"))
        syncToFirestore("pending_students", studentId, mapOf(
            "id" to st.id,
            "status" to "REJECTED"
        ))
    }

    suspend fun registerGoogleStudent(name: String, email: String, className: String): PendingStudent {
        val student = PendingStudent(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            email = email,
            requestedClass = className,
            dateRegistered = "Today",
            status = "PENDING"
        )
        dao.insertPendingStudent(student)
        syncToFirestore("pending_students", student.id, mapOf(
            "id" to student.id,
            "name" to student.name,
            "email" to student.email,
            "requestedClass" to student.requestedClass,
            "dateRegistered" to student.dateRegistered,
            "status" to student.status
        ))
        return student
    }

    suspend fun resetToFreshInstallation() {
        val freshProfile = UserProfile(
            uid = "NHSSASTD4879",
            name = "SREEHITH A S",
            className = "Class VI-A",
            email = "sreehithas23@gmail.com",
            accountStatus = "APPROVED",
            tokens = 1450,
            xp = 3850,
            level = 14,
            streakDays = 7,
            lastCheckInDate = "",
            activeTheme = "RED",
            activeSkin = "Gold Imperial",
            equippedShirt = "Academy Red Blazer",
            equippedPants = "Classic Black Slacks",
            equippedAccessory = "Golden Abacus Belt",
            bossTickets = 3,
            feeStatus = "Due",
            feeAmount = 2400,
            attendancePercent = 92,
            attendedSessions = 11,
            totalSessions = 12
        )
        dao.insertOrUpdateProfile(freshProfile)
    }

    // ==========================================
    // Student Profiles, Progress & Arena Scores Room Operations
    // ==========================================
    suspend fun insertOrUpdateStudentProfile(profile: StudentProfileEntity) {
        studentArenaDao?.insertStudentProfile(profile)
    }

    suspend fun deleteStudentProfile(profile: StudentProfileEntity) {
        studentArenaDao?.deleteStudentProfile(profile)
    }

    suspend fun deleteStudentProfileById(studentId: String) {
        studentArenaDao?.deleteStudentProfileById(studentId)
    }

    suspend fun insertOrUpdateStudentProgress(progress: StudentProgressEntity) {
        studentArenaDao?.insertOrUpdateProgress(progress)
    }

    suspend fun recordMathArenaScore(score: MathArenaScoreEntity): Long {
        return studentArenaDao?.insertScore(score) ?: 0L
    }

    suspend fun deleteMathArenaScore(score: MathArenaScoreEntity) {
        studentArenaDao?.deleteScore(score)
    }

    suspend fun clearScoresForStudent(studentId: String) {
        studentArenaDao?.clearScoresForStudent(studentId)
    }

    // ==========================================
    // Anzan 1-v-1 Duel Results Room Operations
    // ==========================================
    suspend fun recordAnzanDuelResult(result: AnzanDuelResultEntity): Long {
        val id = anzanDuelDao?.insertDuelResult(result) ?: 0L
        syncToFirestore("anzan_duels", "duel_${System.currentTimeMillis()}", mapOf(
            "studentId" to result.studentId,
            "studentName" to result.studentName,
            "opponentName" to result.opponentName,
            "playerScore" to result.playerScore,
            "opponentScore" to result.opponentScore,
            "resultStatus" to result.resultStatus,
            "isVictory" to result.isVictory,
            "accuracy" to result.playerAccuracyPercent,
            "timestamp" to result.timestamp
        ))
        return id
    }

    suspend fun deleteAnzanDuelResult(result: AnzanDuelResultEntity) {
        anzanDuelDao?.deleteDuelResult(result)
    }

    suspend fun clearAnzanDuelsForStudent(studentId: String) {
        anzanDuelDao?.clearDuelsForStudent(studentId)
    }

    // ==========================================
    // Training Session Statistics Room Operations
    // ==========================================
    suspend fun recordTrainingSession(session: TrainingSessionStatsEntity): Long {
        val id = trainingStatsDao?.insertTrainingSession(session) ?: 0L
        syncToFirestore("training_stats", "session_${System.currentTimeMillis()}", mapOf(
            "studentId" to session.studentId,
            "sessionType" to session.sessionType,
            "drillName" to session.drillName,
            "problemsAttempted" to session.problemsAttempted,
            "problemsCorrect" to session.problemsCorrect,
            "accuracyPercentage" to session.accuracyPercentage,
            "avgResponseTimeMs" to session.avgResponseTimeMs,
            "score" to session.score,
            "timestamp" to session.timestamp
        ))
        return id
    }

    suspend fun deleteTrainingSession(session: TrainingSessionStatsEntity) {
        trainingStatsDao?.deleteTrainingSession(session)
    }

    suspend fun clearTrainingSessionsForStudent(studentId: String) {
        trainingStatsDao?.clearTrainingSessionsForStudent(studentId)
    }
}

