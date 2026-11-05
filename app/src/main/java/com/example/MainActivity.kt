package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.calculator.CalculatorSuiteScreen
import com.example.ui.components.*
import com.example.ui.parent.ParentDashboardScreen
import com.example.ui.student.StudentZoneScreen
import com.example.ui.teacher.TeacherAdminScreen
import com.example.ui.theme.UcmasArenaTheme
import com.example.util.configureAppCheck
import com.example.viewmodel.ArenaViewModel
import com.example.viewmodel.TeacherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure App Check before ViewModels or Firebase operations per Firebase AI Logic contract
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            val viewModel: ArenaViewModel = viewModel()
            val teacherViewModel: TeacherViewModel = viewModel()
            val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val transactions by viewModel.transactions.collectAsStateWithLifecycle()
            val rosterStudents by viewModel.rosterStudents.collectAsStateWithLifecycle()
            val homeworkRecords by viewModel.homeworkRecords.collectAsStateWithLifecycle()
            val speedLeague by viewModel.speedLeague.collectAsStateWithLifecycle()
            val attendanceLogs by viewModel.attendanceLogs.collectAsStateWithLifecycle()
            val classDiary by viewModel.classDiary.collectAsStateWithLifecycle()
            val notices by viewModel.notices.collectAsStateWithLifecycle()
            val avatarItems by viewModel.avatarItems.collectAsStateWithLifecycle()
            val analytics by viewModel.analytics.collectAsStateWithLifecycle()
            val pendingStudents by viewModel.pendingStudents.collectAsStateWithLifecycle()
            val batches by viewModel.batches.collectAsStateWithLifecycle()
            val batchAttendance by viewModel.batchAttendance.collectAsStateWithLifecycle()

            val currentPortal by viewModel.currentPortal.collectAsStateWithLifecycle()
            val isParentView by viewModel.isParentView.collectAsStateWithLifecycle()
            val isAdminUnlocked by viewModel.isAdminUnlocked.collectAsStateWithLifecycle()
            val isStudentSubScreenOpen by viewModel.isStudentSubScreenOpen.collectAsStateWithLifecycle()

            val studentProfiles by viewModel.studentProfiles.collectAsStateWithLifecycle()
            val studentProgressList by viewModel.studentProgressList.collectAsStateWithLifecycle()
            val mathArenaScores by viewModel.mathArenaScores.collectAsStateWithLifecycle()
            val teacherUnlockExpiry by viewModel.teacherUnlockExpiryMs.collectAsStateWithLifecycle()
            val isTeacherActive = System.currentTimeMillis() < teacherUnlockExpiry
            val isFacultyUnlocked by teacherViewModel.isUnlocked.collectAsStateWithLifecycle()
            val remainingTeacherMinutes by teacherViewModel.remainingMinutes.collectAsStateWithLifecycle()

            val showThemePicker by viewModel.showThemePicker.collectAsStateWithLifecycle()
            val showWalletDialog by viewModel.showWalletDialog.collectAsStateWithLifecycle()
            val showGoogleSignInDialog by viewModel.showGoogleSignInDialog.collectAsStateWithLifecycle()
            val showEditStudentCardDialog by viewModel.showEditStudentCardDialog.collectAsStateWithLifecycle()
            val showPaymentDialog by viewModel.showPaymentDialog.collectAsStateWithLifecycle()
            val showVictoryParticles by viewModel.showVictoryParticles.collectAsStateWithLifecycle()
            val isSoundEnabled by viewModel.isSoundEnabled.collectAsStateWithLifecycle()

            var easterEggTapCount by remember { mutableIntStateOf(0) }
            var isTeacherUnlocked by remember { mutableStateOf(false) }
            var showTeacherPinDialog by remember { mutableStateOf(false) }
            var showSchoolSettingsDialog by remember { mutableStateOf(false) }
            var showQuickMenuBottomSheet by remember { mutableStateOf(false) }

            // State-based navigation handler between portals
            fun navigateToPortal(portal: ArenaPortal) {
                if (portal == ArenaPortal.TEACHER && !teacherViewModel.isSessionActive() && !viewModel.isTeacherSessionActive()) {
                    showTeacherPinDialog = true
                } else {
                    viewModel.setPortal(portal)
                }
            }

            // BackHandler for State-based navigation: returning to Student portal from secondary portals
            BackHandler(enabled = currentPortal != ArenaPortal.STUDENT) {
                viewModel.setPortal(ArenaPortal.STUDENT)
            }

            UcmasArenaTheme(arenaTheme = activeTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(activeTheme.background)
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = activeTheme.background,
                        floatingActionButton = {
                            if (!isStudentSubScreenOpen && currentPortal == ArenaPortal.STUDENT) {
                                FloatingActionButton(
                                    onClick = { showQuickMenuBottomSheet = true },
                                    containerColor = Color(0xFFE53935),
                                    contentColor = Color.White,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp, end = 4.dp)
                                        .size(56.dp)
                                        .testTag("fab_quick_menu")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Quick Menu",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        },
                        bottomBar = {
                            // Material 3 State-Based Portal Navigation Switcher
                            // Teacher option only appears when unlocked by clicking "ucmas thane kopri" 7 times or entering UcMaS2026!
                            AnimatedVisibility(
                                visible = !isStudentSubScreenOpen,
                                enter = slideInVertically(initialOffsetY = { it }),
                                exit = slideOutVertically(targetOffsetY = { it })
                            ) {
                                NavigationBar(
                                    containerColor = Color.White,
                                    tonalElevation = 8.dp,
                                    modifier = Modifier.testTag("portal_navigation_bar")
                                ) {
                                    val navItems = buildList {
                                        add(Triple(ArenaPortal.STUDENT, "Student", Icons.Default.School))
                                        add(Triple(ArenaPortal.PARENT, "Parent", Icons.Default.FamilyRestroom))
                                        if (isTeacherUnlocked || isTeacherActive) {
                                            add(Triple(ArenaPortal.TEACHER, "Teacher", Icons.Default.AdminPanelSettings))
                                        }
                                        add(Triple(ArenaPortal.CALCULATOR, "Calculators", Icons.Default.Calculate))
                                    }

                                    navItems.forEach { (portal, label, icon) ->
                                        val isSelected = currentPortal == portal
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { navigateToPortal(portal) },
                                            icon = {
                                                BadgedBox(
                                                    badge = {
                                                        if (portal == ArenaPortal.TEACHER && !isTeacherActive) {
                                                            Badge(
                                                                containerColor = Color(0xFFE53935)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Lock,
                                                                    contentDescription = "Password Protected",
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(8.dp)
                                                                )
                                                            }
                                                        } else if (portal == ArenaPortal.TEACHER && pendingStudents.isNotEmpty()) {
                                                            Badge(
                                                                containerColor = Color(0xFFFF9800)
                                                            ) {
                                                                Text("${pendingStudents.size}")
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = label
                                                    )
                                                }
                                            },
                                            label = {
                                                Text(
                                                    text = label,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                                    fontSize = 11.sp
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = activeTheme.primary,
                                                selectedTextColor = activeTheme.primary,
                                                indicatorColor = activeTheme.primary.copy(alpha = 0.15f),
                                                unselectedIconColor = Color(0xFF757588),
                                                unselectedTextColor = Color(0xFF757588)
                                            ),
                                            modifier = Modifier.testTag("portal_nav_${portal.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Top Curved Red Gradient Header Banner matching Screenshot 2
                            if (currentPortal != ArenaPortal.TEACHER) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFFE53935),
                                                    Color(0xFFC62828)
                                                )
                                            )
                                        )
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Left: Logo box + School Title
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    if (teacherViewModel.isSessionActive() || viewModel.isTeacherSessionActive()) {
                                                        isTeacherUnlocked = true
                                                        viewModel.setPortal(ArenaPortal.TEACHER)
                                                        val mins = if (remainingTeacherMinutes > 0) remainingTeacherMinutes else viewModel.getRemainingUnlockMinutes()
                                                        android.widget.Toast.makeText(
                                                            this@MainActivity,
                                                            "Faculty Mode active! ($mins min remaining)",
                                                            android.widget.Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        easterEggTapCount++
                                                        if (easterEggTapCount >= 7) {
                                                            easterEggTapCount = 0
                                                            showTeacherPinDialog = true
                                                        } else if (easterEggTapCount in 3..6) {
                                                            android.widget.Toast.makeText(
                                                                this@MainActivity,
                                                                "${7 - easterEggTapCount} more taps on school title to unlock Teacher",
                                                                android.widget.Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }
                                                .testTag("ucmas_brand_title_tap")
                                        ) {
                                            // White logo container matching Screenshot 2
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color.White,
                                                shadowElevation = 2.dp,
                                                modifier = Modifier.size(44.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxSize().padding(2.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Calculate,
                                                        contentDescription = "Logo",
                                                        tint = Color(0xFFC62828),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Text(
                                                        text = "UCMAS",
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 8.sp,
                                                        color = Color(0xFFC62828),
                                                        lineHeight = 9.sp
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "UCMAS THANE KOPRI EAST",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 14.sp,
                                                    color = Color.White,
                                                    letterSpacing = 0.5.sp,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(top = 1.dp)
                                                ) {
                                                    if (isTeacherActive) {
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = Color(0xFF2E7D32)
                                                        ) {
                                                            Text(
                                                                text = "TEACHER ACTIVE",
                                                                fontSize = 8.sp,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = Color.White,
                                                                maxLines = 1,
                                                                softWrap = false,
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(5.dp))
                                                    }
                                                    Text(
                                                        text = if (isTeacherActive) "Faculty Mode Enabled (${viewModel.getRemainingUnlockMinutes()}m left)" else "Mental Arithmetic & Abacus Arena",
                                                        color = Color.White.copy(alpha = 0.85f),
                                                        fontSize = 10.sp,
                                                        maxLines = 1,
                                                        softWrap = false
                                                    )
                                                }
                                            }
                                        }

                                        // Right side Controls: Token Pill, Sound Toggle, and Round Power/Settings Button matching Screenshot 2
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            // Live Token Wallet Pill
                                            Surface(
                                                shape = RoundedCornerShape(16.dp),
                                                color = Color.White.copy(alpha = 0.95f),
                                                modifier = Modifier
                                                    .clickable { viewModel.openWalletDialog(true) }
                                                    .testTag("header_token_pill")
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.MonetizationOn,
                                                        contentDescription = "Tokens",
                                                        tint = Color(0xFFFFA000),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "${userProfile.tokens}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF5D4037)
                                                    )
                                                }
                                            }

                                            // Sound Toggle
                                            IconButton(
                                                onClick = { viewModel.toggleSound() },
                                                modifier = Modifier.size(34.dp).testTag("sound_toggle_button")
                                            ) {
                                                Icon(
                                                    imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                                    contentDescription = "Toggle Audio",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            // Power / Quick Settings circular switch button matching Screenshot 2
                                            IconButton(
                                                onClick = { showSchoolSettingsDialog = true },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White)
                                                    .testTag("school_settings_power_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PowerSettingsNew,
                                                    contentDescription = "Settings / Account",
                                                    tint = Color(0xFFC62828),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Dynamic Profile Card
                            // Hidden during PvP, PvE, Flash Arcade, AI Scanner, and Wardrobe!
                            val shouldShowProfileCard = (!isStudentSubScreenOpen && currentPortal == ArenaPortal.STUDENT) || currentPortal == ArenaPortal.PARENT

                            if (shouldShowProfileCard) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    DynamicProfileCard(
                                        profile = userProfile,
                                        theme = activeTheme,
                                        isParentView = isParentView || currentPortal == ArenaPortal.PARENT,
                                        onToggleView = {
                                            viewModel.toggleParentView()
                                            if (currentPortal == ArenaPortal.STUDENT && !isParentView) {
                                                viewModel.setPortal(ArenaPortal.PARENT)
                                            } else if (currentPortal == ArenaPortal.PARENT && isParentView) {
                                                viewModel.setPortal(ArenaPortal.STUDENT)
                                            }
                                        },
                                        onDailyCheckIn = { viewModel.dailyCheckIn() },
                                        onPayFees = { viewModel.payFees() },
                                        isAdmin = isAdminUnlocked,
                                        onEditCard = { viewModel.openEditStudentCard(true) }
                                    )
                                }
                            }

                            // Portal Views
                            Box(modifier = Modifier.weight(1f)) {
                                when (currentPortal) {
                                    ArenaPortal.STUDENT -> {
                                        StudentZoneScreen(
                                            profile = userProfile,
                                            avatarItems = avatarItems,
                                            theme = activeTheme,
                                            speedLeague = speedLeague,
                                            analytics = analytics,
                                            onAddReward = { title, tokens, xp, cat, addTicket ->
                                                viewModel.addReward(title, tokens, xp, cat, addTicket)
                                            },
                                            onSubmitHomework = { rec -> viewModel.submitHomework(rec) },
                                            onEquipSkin = { skin -> viewModel.equipSkin(skin) },
                                            onEquipAvatarItem = { cat, name -> viewModel.equipAvatarItem(cat, name) },
                                            onPurchaseAvatarItem = { cost, item -> viewModel.purchaseAvatarItem(cost, item) },
                                            onSubScreenChange = { isOpen -> viewModel.setStudentSubScreenOpen(isOpen) },
                                            onBeadClick = { viewModel.soundManager.playBeadClick() },
                                            onCorrectChime = { viewModel.soundManager.playCorrectChime() },
                                            onErrorBuzz = { viewModel.soundManager.playErrorBuzz() },
                                            onFlashTick = { viewModel.soundManager.playFlashTick() },
                                            onCoinSound = { viewModel.soundManager.playCoinSound() },
                                            onTriggerVictoryParticles = { viewModel.triggerVictoryParticles() },
                                            onRecordDuel = { oppName, oppTier, pScore, oppScore, rounds, correct, acc, latency, combo, isWin ->
                                                viewModel.recordAnzanDuel(
                                                    opponentName = oppName,
                                                    opponentTier = oppTier,
                                                    opponentType = "PEER",
                                                    playerScore = pScore,
                                                    opponentScore = oppScore,
                                                    totalRounds = rounds,
                                                    correctAnswers = correct,
                                                    playerAccuracy = acc,
                                                    avgLatencyMs = latency,
                                                    maxCombo = combo,
                                                    tokens = if (isWin) 25 else 5,
                                                    xp = if (isWin) 75 else 20
                                                )
                                            },
                                            onOpenICardDetails = { viewModel.toggleParentView() },
                                            onOpenAttendanceSheet = { viewModel.setPortal(ArenaPortal.PARENT) },
                                            onOpenParentPortal = { viewModel.setPortal(ArenaPortal.PARENT) },
                                            onRecordTrainingSession = { sessionType, drillName, formulaCategory, digitCount, rowCount, flashSpeedMs, durationSeconds, attempted, correct, accuracy, avgResponseMs, tokens, xp, highestCombo, score, rating ->
                                                viewModel.recordTrainingSession(
                                                    sessionType = sessionType,
                                                    drillName = drillName,
                                                    formulaCategory = formulaCategory,
                                                    digitCount = digitCount,
                                                    rowCount = rowCount,
                                                    flashSpeedMs = flashSpeedMs,
                                                    durationSeconds = durationSeconds,
                                                    attempted = attempted,
                                                    correct = correct,
                                                    accuracy = accuracy,
                                                    avgResponseMs = avgResponseMs,
                                                    tokens = tokens,
                                                    xp = xp,
                                                    highestCombo = highestCombo,
                                                    score = score,
                                                    rating = rating
                                                )
                                            }
                                        )
                                    }

                                    ArenaPortal.PARENT -> {
                                        ParentDashboardScreen(
                                            profile = userProfile,
                                            homeworkList = homeworkRecords,
                                            attendanceLogs = attendanceLogs,
                                            classDiary = classDiary,
                                            notices = notices,
                                            analytics = analytics,
                                            theme = activeTheme,
                                            onPayFees = { viewModel.payFees() },
                                            onConfirmPayment = { utr -> viewModel.payFeesWithRef(utr) }
                                        )
                                    }

                                    ArenaPortal.TEACHER -> {
                                        TeacherAdminScreen(
                                            roster = rosterStudents,
                                            pendingStudents = pendingStudents,
                                            classDiary = classDiary,
                                            notices = notices,
                                            homeworkList = homeworkRecords,
                                            avatarItems = avatarItems,
                                            analytics = analytics,
                                            theme = activeTheme,
                                            batches = batches,
                                            batchAttendance = batchAttendance,
                                            onSaveBatch = { b -> viewModel.createOrUpdateBatch(b) },
                                            onDeleteBatch = { b -> viewModel.deleteBatch(b) },
                                            onMarkBatchAttendance = { bId, sId, sName, date, status ->
                                                viewModel.markBatchAttendance(bId, sId, sName, date, status)
                                            },
                                            onExitFacultyMode = { viewModel.setPortal(ArenaPortal.STUDENT) },
                                            onApproveStudent = { id, cls, lvl, bId -> viewModel.approveStudent(id, cls, lvl, bId) },
                                            onRejectStudent = { id -> viewModel.rejectStudent(id) },
                                            onAddStudent = { st -> viewModel.addRosterStudent(st) },
                                            onUpdateStudent = { st -> viewModel.updateRosterStudent(st) },
                                            onManualTokenAdjustment = { name, amt, reason ->
                                                viewModel.addReward(
                                                    title = "$reason: $name",
                                                    tokensDelta = amt,
                                                    xpDelta = 0,
                                                    category = "Teacher Grant",
                                                    addTicket = false
                                                )
                                                viewModel.soundManager.playCoinSound()
                                            },
                                            onAddClassDiary = { entry -> viewModel.addClassDiary(entry) },
                                            onAddNotice = { notice -> viewModel.addNotice(notice) },
                                            onUpdateAttendance = { log -> viewModel.updateAttendance(log) },
                                            onUpdateAnalytics = { rec -> viewModel.updateAnalytics(rec) },
                                            onGrantAvatarItem = { id -> viewModel.grantTeacherAvatar(id) },
                                            currentStudentProfile = userProfile,
                                            onUpdateStudentTokens = { newTokens, reason ->
                                                viewModel.setStudentTokens(newTokens, reason)
                                            },
                                            onUpdateRosterTokens = { id, newTokens, reason ->
                                                viewModel.setRosterStudentTokens(id, newTokens, reason)
                                            },
                                            studentProfiles = studentProfiles,
                                            studentProgressList = studentProgressList,
                                            mathArenaScores = mathArenaScores,
                                            remainingUnlockMinutes = remainingTeacherMinutes,
                                            onLockFaculty = {
                                                teacherViewModel.lock()
                                                viewModel.lockTeacherSession()
                                                viewModel.setPortal(ArenaPortal.STUDENT)
                                                android.widget.Toast.makeText(
                                                    this@MainActivity,
                                                    "Faculty Portal locked.",
                                                    android.widget.Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onRecordMathScore = { sId, sName, mode, score, acc, att, cor, avg, tok, xp, diff ->
                                                teacherViewModel.recordMathArenaScore(sId, sName, mode, score, acc, att, cor, avg, tok, xp, diff)
                                            },
                                            onDeleteMathScore = { scoreItem ->
                                                teacherViewModel.deleteMathArenaScore(scoreItem)
                                            }
                                        )
                                    }

                                    ArenaPortal.CALCULATOR -> {
                                        CalculatorSuiteScreen(
                                            theme = activeTheme,
                                            onBeadClick = { viewModel.soundManager.playBeadClick() }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Theme Picker Dialog
                    if (showThemePicker) {
                        ThemePickerDialog(
                            currentTheme = activeTheme,
                            onSelectTheme = { theme -> viewModel.selectTheme(theme) },
                            onDismiss = { viewModel.openThemePicker(false) }
                        )
                    }

                    // Token Wallet Dialog
                    if (showWalletDialog) {
                        TokenWalletDialog(
                            profile = userProfile,
                            transactions = transactions,
                            theme = activeTheme,
                            onDailyCheckIn = { viewModel.dailyCheckIn() },
                            onDismiss = { viewModel.openWalletDialog(false) }
                        )
                    }

                    // Google Sign-In & Approval Dialog
                    if (showGoogleSignInDialog) {
                        GoogleSignInDialog(
                            theme = activeTheme,
                            currentStudentName = userProfile.name,
                            currentStudentEmail = userProfile.email,
                            pendingStudents = pendingStudents,
                            onSelectApprovedStudent = { name, email -> viewModel.selectApprovedStudent(name, email) },
                            onRegisterNewStudent = { name, email, cls -> viewModel.registerGoogleStudent(name, email, cls) },
                            onDismiss = { viewModel.openGoogleSignIn(false) }
                        )
                    }

                    // Edit Student Card Dialog (Teacher Mode)
                    if (showEditStudentCardDialog) {
                        EditStudentCardDialog(
                            profile = userProfile,
                            theme = activeTheme,
                            onSaveProfile = { updated -> viewModel.saveEditedProfile(updated) },
                            onDismiss = { viewModel.openEditStudentCard(false) }
                        )
                    }

                    // UPI Fee Payment Dialog with QR Code
                    if (showPaymentDialog) {
                        UpiFeePaymentDialog(
                            profile = userProfile,
                            theme = activeTheme,
                            onConfirmPayment = { utr -> viewModel.payFeesWithRef(utr) },
                            onDismiss = { viewModel.openPaymentDialog(false) }
                        )
                    }

                    // 60 FPS Particle Explosion Overlay on Victory
                    if (showVictoryParticles) {
                        ParticleExplosion(
                            isActive = showVictoryParticles,
                            onAnimationEnd = { viewModel.dismissVictoryParticles() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Teacher Security PIN Dialog for state-based portal switching
                    if (showTeacherPinDialog) {
                        var teacherPinInput by remember { mutableStateOf("") }
                        var teacherPinError by remember { mutableStateOf(false) }

                        AlertDialog(
                            onDismissRequest = {
                                showTeacherPinDialog = false
                                teacherPinInput = ""
                                teacherPinError = false
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = activeTheme.primary
                                )
                            },
                            title = {
                                Text("Faculty Portal Login", fontWeight = FontWeight.Bold)
                            },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Please enter the faculty password to unlock Teacher Mode for the next 20 minutes:\n(Password: UcMaS2026)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF555566)
                                    )
                                    OutlinedTextField(
                                        value = teacherPinInput,
                                        onValueChange = {
                                            teacherPinInput = it
                                            teacherPinError = false
                                        },
                                        label = { Text("Faculty Password") },
                                        placeholder = { Text("UcMaS2026") },
                                        singleLine = true,
                                        isError = teacherPinError,
                                        supportingText = if (teacherPinError) { { Text("Invalid password. Please enter UcMaS2026", color = Color.Red) } } else null,
                                        modifier = Modifier.fillMaxWidth().testTag("teacher_nav_pin_input")
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val input = teacherPinInput.trim()
                                        val unlocked = teacherViewModel.unlock(input)
                                        if (unlocked) {
                                            viewModel.unlockTeacherSession(20)
                                            isTeacherUnlocked = true
                                            viewModel.setPortal(ArenaPortal.TEACHER)
                                            showTeacherPinDialog = false
                                            teacherPinInput = ""
                                            teacherPinError = false
                                            android.widget.Toast.makeText(
                                                this@MainActivity,
                                                "Faculty Mode unlocked for 20 minutes! Welcome, Teacher.",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            teacherPinError = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = activeTheme.primary),
                                    modifier = Modifier.testTag("teacher_nav_pin_submit")
                                ) {
                                    Text("Unlock (20 Mins)")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showTeacherPinDialog = false
                                        teacherPinInput = ""
                                        teacherPinError = false
                                    }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    // School Settings Screen Dialog matching Screenshot 4
                    if (showSchoolSettingsDialog) {
                        SchoolSettingsDialog(
                            profile = userProfile,
                            theme = activeTheme,
                            onDismiss = { showSchoolSettingsDialog = false },
                            onOpenThemePicker = { viewModel.openThemePicker(true) },
                            onOpenWallet = { viewModel.openWalletDialog(true) },
                            onOpenGoogleSignIn = { viewModel.openGoogleSignIn(true) },
                            onSwitchPortal = { portal ->
                                navigateToPortal(portal)
                            },
                            isSoundEnabled = isSoundEnabled,
                            onToggleSound = { viewModel.toggleSound() }
                        )
                    }

                    // Quick Menu Modal Bottom Sheet matching Screenshot 3
                    if (showQuickMenuBottomSheet) {
                        QuickMenuBottomSheet(
                            theme = activeTheme,
                            onDismiss = { showQuickMenuBottomSheet = false },
                            onNavigateHome = { viewModel.setPortal(ArenaPortal.STUDENT) },
                            onOpenNotifications = {
                                android.widget.Toast.makeText(this@MainActivity, "No new notifications", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            onOpenStudents = { viewModel.setPortal(ArenaPortal.PARENT) },
                            onOpenSettings = { showSchoolSettingsDialog = true },
                            onOpenICard = { viewModel.toggleParentView() },
                            onOpenFees = { viewModel.openPaymentDialog(true) },
                            onOpenAttendance = { viewModel.setPortal(ArenaPortal.PARENT) },
                            onOpenTimeTable = {
                                android.widget.Toast.makeText(this@MainActivity, "Weekly Batch Schedule: Sat & Sun 10:00 AM - 12:00 PM", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            onOpenClassWork = { viewModel.setPortal(ArenaPortal.STUDENT) },
                            onOpenHomework = { viewModel.setPortal(ArenaPortal.PARENT) },
                            onOpenReportCard = { viewModel.setPortal(ArenaPortal.PARENT) },
                            onOpenMentalMathTrainer = { viewModel.setPortal(ArenaPortal.STUDENT) },
                            onActionSelected = { actionName ->
                                android.widget.Toast.makeText(this@MainActivity, "$actionName selected", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}
