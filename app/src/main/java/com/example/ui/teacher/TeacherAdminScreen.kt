package com.example.ui.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.ArenaTheme

enum class TeacherPortalTab(val title: String) {
    APPROVALS("Approvals"),
    BATCHES("Batches"),
    ATTENDANCE("Attendance"),
    SCORES_PROGRESS("Profiles & Scores"),
    ROSTER("Roster"),
    DIARY("Classwork/HW"),
    ANALYTICS_INPUT("Speed/Accuracy Data"),
    AVATAR_GIFT("Avatar Wardrobe")
}

@Composable
fun TeacherAdminScreen(
    roster: List<RosterStudent>,
    pendingStudents: List<PendingStudent>,
    classDiary: List<ClassDiaryEntry>,
    notices: List<NoticeEvent>,
    homeworkList: List<HomeworkRecord>,
    avatarItems: List<AvatarItem>,
    analytics: AnalyticsRecord?,
    theme: ArenaTheme,
    onApproveStudent: (id: String, className: String, level: Int, batchId: String) -> Unit,
    onRejectStudent: (id: String) -> Unit,
    onAddStudent: (RosterStudent) -> Unit,
    onUpdateStudent: (RosterStudent) -> Unit,
    onManualTokenAdjustment: (studentName: String, amount: Int, reason: String) -> Unit,
    onAddClassDiary: (ClassDiaryEntry) -> Unit,
    onAddNotice: (NoticeEvent) -> Unit,
    onUpdateAttendance: (AttendanceLog) -> Unit,
    onUpdateAnalytics: (AnalyticsRecord) -> Unit,
    onGrantAvatarItem: (itemId: String) -> Unit,
    currentStudentProfile: com.example.data.model.UserProfile? = null,
    onUpdateStudentTokens: ((newTokens: Int, reason: String) -> Unit)? = null,
    onUpdateRosterTokens: ((id: String, newTokens: Int, reason: String) -> Unit)? = null,
    batches: List<StudentBatch> = emptyList(),
    batchAttendance: List<BatchAttendanceRecord> = emptyList(),
    onSaveBatch: (StudentBatch) -> Unit = {},
    onDeleteBatch: ((StudentBatch) -> Unit)? = null,
    onMarkBatchAttendance: (batchId: String, studentId: String, studentName: String, date: String, status: String) -> Unit = { _, _, _, _, _ -> },
    onExitFacultyMode: (() -> Unit)? = null,
    studentProfiles: List<StudentProfileEntity> = emptyList(),
    studentProgressList: List<StudentProgressEntity> = emptyList(),
    mathArenaScores: List<MathArenaScoreEntity> = emptyList(),
    remainingUnlockMinutes: Int = 0,
    onLockFaculty: (() -> Unit)? = null,
    onRecordMathScore: ((studentId: String, studentName: String, gameMode: String, score: Int, accuracy: Float, attempted: Int, correct: Int, avgTimeMs: Long, tokens: Int, xp: Int, difficulty: String) -> Unit)? = null,
    onDeleteMathScore: ((MathArenaScoreEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(TeacherPortalTab.APPROVALS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("teacher_admin_screen_root")
    ) {
        // Faculty Mode Top Banner & Navigation Header
        Surface(
            color = Color.White,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E7D32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Faculty Portal",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FACULTY PORTAL",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF2E7D32),
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE8F5E9)) {
                                Text(
                                    text = if (remainingUnlockMinutes > 0) "ACTIVE (${remainingUnlockMinutes}m left)" else "ACTIVE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "UCMAS Thane Kopri East • Student, Batch & Attendance Admin",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onLockFaculty != null) {
                        FilledTonalButton(
                            onClick = onLockFaculty,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFFFF3E0),
                                contentColor = Color(0xFFE65100)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("lock_faculty_mode_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock Faculty",
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Lock",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (onExitFacultyMode != null) {
                        FilledTonalButton(
                            onClick = onExitFacultyMode,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFFFEBEE),
                                contentColor = Color(0xFFC62828)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("exit_faculty_mode_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Exit Faculty Mode",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Exit",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Teacher Navigation Scrollable Tabs
        val scrollState = rememberScrollState()
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TeacherPortalTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) theme.primary else Color.Transparent,
                        modifier = Modifier
                            .clickable { selectedTab = tab }
                            .testTag("teacher_tab_${tab.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            if (tab == TeacherPortalTab.APPROVALS && pendingStudents.any { it.status == "PENDING" }) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFF1744)))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF555566)
                            )
                        }
                    }
                }
            }
        }

        // Active Tab Content with weight(1f) to ensure full scrollability
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTab) {
                TeacherPortalTab.APPROVALS -> StudentApprovalsTab(pendingStudents, batches, theme, onApproveStudent, onRejectStudent)
                TeacherPortalTab.BATCHES -> BatchManager(
                    batches = batches,
                    roster = roster,
                    theme = theme,
                    onSaveBatch = onSaveBatch,
                    onDeleteBatch = onDeleteBatch,
                    onNavigateToAttendance = { _, _ ->
                        selectedTab = TeacherPortalTab.ATTENDANCE
                    }
                )
                TeacherPortalTab.ATTENDANCE -> AttendanceScreen(
                    batches = batches,
                    roster = roster,
                    batchAttendance = batchAttendance,
                    theme = theme,
                    onSaveBatch = onSaveBatch,
                    onMarkAttendance = onMarkBatchAttendance,
                    onOpenBatchManager = {
                        selectedTab = TeacherPortalTab.BATCHES
                    }
                )
                TeacherPortalTab.SCORES_PROGRESS -> StudentProfilesAndScoresTab(
                    profiles = studentProfiles,
                    progressList = studentProgressList,
                    scores = mathArenaScores,
                    theme = theme,
                    onRecordScore = { studentId, studentName, gameMode, score, accuracy, attempted, correct, avgTimeMs, tokens, xp, difficulty ->
                        onRecordMathScore?.invoke(studentId, studentName, gameMode, score, accuracy, attempted, correct, avgTimeMs, tokens, xp, difficulty)
                    },
                    onDeleteScore = { scoreItem ->
                        onDeleteMathScore?.invoke(scoreItem)
                    }
                )
                TeacherPortalTab.ROSTER -> ClassroomRosterTab(roster, theme, onAddStudent, onUpdateStudent, onManualTokenAdjustment, currentStudentProfile, onUpdateStudentTokens, onUpdateRosterTokens)
                TeacherPortalTab.DIARY -> ClassworkHomeworkDiaryTab(classDiary, theme, onAddClassDiary)
                TeacherPortalTab.ANALYTICS_INPUT -> SpeedAccuracyAnalyticsTab(analytics, theme, onUpdateAnalytics)
                TeacherPortalTab.AVATAR_GIFT -> TeacherAvatarGiftingTab(avatarItems, theme, onGrantAvatarItem)
            }
        }
    }
}


@Composable
private fun StudentApprovalsTab(
    pendingStudents: List<PendingStudent>,
    batches: List<StudentBatch>,
    theme: ArenaTheme,
    onApproveStudent: (id: String, className: String, level: Int, batchId: String) -> Unit,
    onRejectStudent: (id: String) -> Unit
) {
    val context = LocalContext.current
    var approveDialogStudent by remember { mutableStateOf<PendingStudent?>(null) }
    var assignedClass by remember { mutableStateOf("Class VI-A") }
    var assignedLevel by remember { mutableIntStateOf(14) }
    var selectedBatchId by remember { mutableStateOf(batches.firstOrNull()?.id ?: "batch_mon") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Pending Student Approvals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Approve student Google accounts before they log in", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF8E1)) {
                Text(
                    text = "${pendingStudents.count { it.status == "PENDING" }} Pending",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFFE65100),
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (pendingStudents.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No student approval requests pending.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(pendingStudents) { student ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (student.status == "PENDING") Color(0xFFFFD54F) else Color(0xFFE0E0E6)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("pending_student_${student.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF4285F4)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("G", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(student.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text(student.email, fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (student.status) {
                                        "APPROVED" -> Color(0xFFE8F5E9)
                                        "REJECTED" -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFFFF8E1)
                                    }
                                ) {
                                    Text(
                                        text = student.status,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = when (student.status) {
                                            "APPROVED" -> Color(0xFF2E7D32)
                                            "REJECTED" -> Color(0xFFC62828)
                                            else -> Color(0xFFE65100)
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Target Class: ${student.requestedClass} • Submitted: ${student.dateRegistered}", fontSize = 11.sp, color = Color(0xFF555566))

                            if (student.status == "PENDING") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            approveDialogStudent = student
                                            selectedBatchId = batches.firstOrNull()?.id ?: "batch_mon"
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("approve_btn_${student.id}")
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Approve Student")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onRejectStudent(student.id)
                                            Toast.makeText(context, "Rejected sign-in for ${student.name}", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(0.7f)
                                    ) {
                                        Text("Reject", color = Color(0xFFC62828))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    approveDialogStudent?.let { st ->
        AlertDialog(
            onDismissRequest = { approveDialogStudent = null },
            title = { Text("Confirm Student Admission") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Approving Google login for ${st.name} (${st.email})", fontSize = 12.sp)

                    Text("Assign Weekly Batch (Students attend weekly once):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.primary)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        batches.forEach { batch ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedBatchId == batch.id) theme.primary.copy(alpha = 0.12f) else Color(0xFFF5F5F7),
                                border = if (selectedBatchId == batch.id) androidx.compose.foundation.BorderStroke(1.5.dp, theme.primary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedBatchId = batch.id }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(batch.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${batch.dayOfWeek} • ${batch.timeSlot}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    if (selectedBatchId == batch.id) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = theme.primary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = assignedClass,
                        onValueChange = { assignedClass = it },
                        label = { Text("Assign Class") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = assignedLevel.toString(),
                        onValueChange = { assignedLevel = it.toIntOrNull() ?: 1 },
                        label = { Text("Assign UCMAS Level (1-15)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val chosenBatchName = batches.find { it.id == selectedBatchId }?.name ?: "Weekly Batch"
                        onApproveStudent(st.id, assignedClass, assignedLevel, selectedBatchId)
                        Toast.makeText(context, "${st.name} approved & enrolled in $chosenBatchName!", Toast.LENGTH_LONG).show()
                        approveDialogStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Confirm & Admit")
                }
            },
            dismissButton = {
                TextButton(onClick = { approveDialogStudent = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ClassroomRosterTab(
    roster: List<RosterStudent>,
    theme: ArenaTheme,
    onAddStudent: (RosterStudent) -> Unit,
    onUpdateStudent: (RosterStudent) -> Unit,
    onManualTokenAdjustment: (String, Int, String) -> Unit,
    currentStudentProfile: com.example.data.model.UserProfile? = null,
    onUpdateStudentTokens: ((newTokens: Int, reason: String) -> Unit)? = null,
    onUpdateRosterTokens: ((id: String, newTokens: Int, reason: String) -> Unit)? = null
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<RosterStudent?>(null) }
    var tokenAdjustmentStudent by remember { mutableStateOf<RosterStudent?>(null) }
    var showActiveStudentTokenDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Class VI-A Roster Editor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${roster.size} Enrolled Scholars • Teacher Token Grants", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_student_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Student", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Scholar Token Edit Banner
        if (currentStudentProfile != null) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD54F)),
                modifier = Modifier.fillMaxWidth().testTag("active_student_token_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFFFA000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Active Scholar: ${currentStudentProfile.name}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text("Token Balance: ${currentStudentProfile.tokens} Tokens", fontSize = 11.sp, color = Color(0xFF5D4037), fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Button(
                        onClick = { showActiveStudentTokenDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("edit_active_student_tokens_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Tokens", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(roster) { student ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape).background(theme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(student.rollNo, fontWeight = FontWeight.Bold, color = theme.primary, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(student.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Tier: ${student.tier} • ${student.tokens} Tokens • ${student.accuracy}% Acc", fontSize = 10.sp, color = Color.Gray)
                        }

                        // Dedicated Token Adjustment Button
                        IconButton(
                            onClick = { tokenAdjustmentStudent = student },
                            modifier = Modifier.testTag("adjust_tokens_${student.rollNo}")
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "Edit Tokens", tint = Color(0xFFFFA000), modifier = Modifier.size(22.dp))
                        }

                        // Edit Details Button
                        IconButton(onClick = { editingStudent = student }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Student", tint = theme.primary, modifier = Modifier.size(18.dp))
                        }

                        // Attendance Toggle
                        IconButton(
                            onClick = {
                                onUpdateStudent(student.copy(isPresentToday = !student.isPresentToday))
                                Toast.makeText(context, "${student.name} attendance updated!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = if (student.isPresentToday) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = "Toggle Attendance",
                                tint = if (student.isPresentToday) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Student Modal
    editingStudent?.let { st ->
        var editName by remember { mutableStateOf(st.name) }
        var editTier by remember { mutableStateOf(st.tier) }
        var editTokens by remember { mutableStateOf(st.tokens.toString()) }

        AlertDialog(
            onDismissRequest = { editingStudent = null },
            title = { Text("Edit Scholar: ${st.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = editTier, onValueChange = { editTier = it }, label = { Text("League Tier (Diamond, Gold...)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = editTokens, onValueChange = { editTokens = it }, label = { Text("Token Balance") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateStudent(st.copy(name = editName.trim(), tier = editTier.trim(), tokens = editTokens.toIntOrNull() ?: st.tokens))
                        Toast.makeText(context, "Saved changes for ${st.name}", Toast.LENGTH_SHORT).show()
                        editingStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { editingStudent = null }) { Text("Cancel") } }
        )
    }

    // Direct Token Adjustment Modal for Roster Student
    tokenAdjustmentStudent?.let { st ->
        var targetTokens by remember { mutableStateOf(st.tokens.toString()) }
        var grantReason by remember { mutableStateOf("Teacher Classroom Distinction") }

        AlertDialog(
            onDismissRequest = { tokenAdjustmentStudent = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFA000))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Tokens: ${st.name}", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Current Balance: ${st.tokens} Tokens", fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = targetTokens,
                        onValueChange = { targetTokens = it },
                        label = { Text("New Token Balance") },
                        modifier = Modifier.fillMaxWidth().testTag("token_balance_input")
                    )
                    Text("Quick Token Adjustments:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(50, 100, 250, 500).forEach { delta ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF8E1),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val cur = targetTokens.toIntOrNull() ?: st.tokens
                                        targetTokens = (cur + delta).toString()
                                    }
                            ) {
                                Text(
                                    text = "+$delta",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFEBEE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val cur = targetTokens.toIntOrNull() ?: st.tokens
                                    targetTokens = maxOf(0, cur - 100).toString()
                                }
                        ) {
                            Text(
                                text = "-100",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = grantReason,
                        onValueChange = { grantReason = it },
                        label = { Text("Reason / Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newAmount = targetTokens.toIntOrNull() ?: st.tokens
                        onUpdateRosterTokens?.invoke(st.id, newAmount, grantReason.trim().ifEmpty { "Teacher Adjustment" })
                            ?: onUpdateStudent(st.copy(tokens = newAmount))
                        Toast.makeText(context, "Updated ${st.name}'s balance to $newAmount Tokens!", Toast.LENGTH_SHORT).show()
                        tokenAdjustmentStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                ) {
                    Text("Apply Tokens", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { tokenAdjustmentStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Direct Token Adjustment Modal for Active Student Profile (Sreehith)
    if (showActiveStudentTokenDialog && currentStudentProfile != null) {
        var activeTargetTokens by remember { mutableStateOf(currentStudentProfile.tokens.toString()) }
        var activeReason by remember { mutableStateOf("Teacher Merit Grant") }

        AlertDialog(
            onDismissRequest = { showActiveStudentTokenDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFA000))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Tokens: ${currentStudentProfile.name}", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Current Balance: ${currentStudentProfile.tokens} Tokens", fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = activeTargetTokens,
                        onValueChange = { activeTargetTokens = it },
                        label = { Text("New Token Balance") },
                        modifier = Modifier.fillMaxWidth().testTag("active_token_balance_input")
                    )
                    Text("Quick Adjustments:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(50, 100, 250, 500).forEach { delta ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF8E1),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val cur = activeTargetTokens.toIntOrNull() ?: currentStudentProfile.tokens
                                        activeTargetTokens = (cur + delta).toString()
                                    }
                            ) {
                                Text(
                                    text = "+$delta",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFEBEE),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val cur = activeTargetTokens.toIntOrNull() ?: currentStudentProfile.tokens
                                    activeTargetTokens = maxOf(0, cur - 100).toString()
                                }
                        ) {
                            Text(
                                text = "-100",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = activeReason,
                        onValueChange = { activeReason = it },
                        label = { Text("Reason / Note") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newAmount = activeTargetTokens.toIntOrNull() ?: currentStudentProfile.tokens
                        onUpdateStudentTokens?.invoke(newAmount, activeReason.trim().ifEmpty { "Teacher Adjustment" })
                        Toast.makeText(context, "Saved ${currentStudentProfile.name}'s balance to $newAmount Tokens!", Toast.LENGTH_SHORT).show()
                        showActiveStudentTokenDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                ) {
                    Text("Apply Tokens", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showActiveStudentTokenDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Student Modal
    if (showAddDialog) {
        var nameInput by remember { mutableStateOf("") }
        var rollInput by remember { mutableStateOf("VI-07") }
        var tierInput by remember { mutableStateOf("Gold") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Student to Roster") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Student Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rollInput, onValueChange = { rollInput = it }, label = { Text("Roll No") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            val newSt = RosterStudent(
                                id = "std_${System.currentTimeMillis()}",
                                name = nameInput.trim(),
                                rollNo = rollInput.trim(),
                                tier = tierInput,
                                accuracy = 90,
                                avgLatencyMs = 280,
                                attendanceRate = 95,
                                tokens = 500,
                                isPresentToday = true
                            )
                            onAddStudent(newSt)
                            showAddDialog = false
                            Toast.makeText(context, "Added ${newSt.name}!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                ) { Text("Add") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ClassworkHomeworkDiaryTab(
    classDiary: List<ClassDiaryEntry>,
    theme: ArenaTheme,
    onAddClassDiary: (ClassDiaryEntry) -> Unit
) {
    val context = LocalContext.current
    var showAddDiaryDialog by remember { mutableStateOf(false) }
    var entryType by remember { mutableStateOf("CLASSWORK") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var formulaFocus by remember { mutableStateOf("+7 = -3 + 10") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Classwork & Homework Diary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Entries visible to parents in Parents Portal", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Button(
                onClick = { showAddDiaryDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_diary_entry_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Entry", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(classDiary) { entry ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(entry.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Surface(shape = RoundedCornerShape(4.dp), color = if (entry.type == "CLASSWORK") Color(0xFFE8F5E9) else Color(0xFFFFF8E1)) {
                                Text(entry.type, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (entry.type == "CLASSWORK") Color(0xFF2E7D32) else Color(0xFFE65100), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(entry.description, fontSize = 11.sp, color = Color(0xFF555566))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Formula: ${entry.formulaFocus} • Date: ${entry.date}", fontSize = 10.sp, color = theme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    if (showAddDiaryDialog) {
        AlertDialog(
            onDismissRequest = { showAddDiaryDialog = false },
            title = { Text("Log Classwork / Homework") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = entryType == "CLASSWORK",
                            onClick = { entryType = "CLASSWORK" },
                            label = { Text("Classwork") }
                        )
                        FilterChip(
                            selected = entryType == "HOMEWORK",
                            onClick = { entryType = "HOMEWORK" },
                            label = { Text("Homework") }
                        )
                    }
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title / Topic") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Detailed Summary") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = formulaFocus, onValueChange = { formulaFocus = it }, label = { Text("Formula Focus") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val newEntry = ClassDiaryEntry(
                                id = "cd_${System.currentTimeMillis()}",
                                date = "Today (23 Oct 2026)",
                                type = entryType,
                                title = title.trim(),
                                description = description.trim(),
                                formulaFocus = formulaFocus.trim()
                            )
                            onAddClassDiary(newEntry)
                            showAddDiaryDialog = false
                            Toast.makeText(context, "$entryType added to Parents Portal diary!", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                ) { Text("Publish to Diary") }
            },
            dismissButton = { TextButton(onClick = { showAddDiaryDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun WeeklyBatchAttendanceTab(
    batches: List<StudentBatch>,
    roster: List<RosterStudent>,
    batchAttendance: List<BatchAttendanceRecord>,
    theme: ArenaTheme,
    onSaveBatch: (StudentBatch) -> Unit,
    onMarkAttendance: (batchId: String, studentId: String, studentName: String, date: String, status: String) -> Unit
) {
    val context = LocalContext.current
    val weekDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    var selectedDay by remember { mutableStateOf("Monday") }
    var selectedDate by remember { mutableStateOf("24 Oct 2026") }
    var showCreateBatchDialog by remember { mutableStateOf(false) }
    var showEditBatchDialog by remember { mutableStateOf(false) }

    // Find the active batch for the selected day or fallback
    val activeBatch = batches.find { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
        ?: batches.firstOrNull()

    // Get all students enrolled in the active batch
    val enrolledStudentIds = activeBatch?.getStudentIdList() ?: emptyList()
    val studentsInBatch = roster.filter { student ->
        enrolledStudentIds.contains(student.id) || (activeBatch != null && student.batchId == activeBatch.id)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weekly Batch Attendance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Students attend weekly once • Real-time attendance register",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Button(
                onClick = { showCreateBatchDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("create_batch_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Batch", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Weekly Day Tabs: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
        val daysScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(daysScrollState),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            weekDays.forEach { day ->
                val dayBatch = batches.find { it.dayOfWeek.equals(day, ignoreCase = true) }
                val count = if (dayBatch != null) {
                    val batchIds = dayBatch.getStudentIdList()
                    roster.count { it.batchId == dayBatch.id || batchIds.contains(it.id) }
                } else 0

                val isSelected = selectedDay.equals(day, ignoreCase = true)

                FilterChip(
                    selected = isSelected,
                    onClick = { selectedDay = day },
                    label = {
                        Text(
                            text = "$day ($count)",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = theme.primary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("batch_day_chip_${day.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Batch Info Card with + Icon in Top Right Corner to edit batch
        if (activeBatch != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primary.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth().testTag("active_batch_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeBatch.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E1E24)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = theme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "${activeBatch.dayOfWeek}s",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = theme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Timing: ${activeBatch.timeSlot} • Weekly Class",
                                fontSize = 11.sp,
                                color = Color(0xFF666677)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${studentsInBatch.size} Scholars Enrolled (out of ${roster.size} registered)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                        }

                        // Top Right Corner '+' Icon: Allows teacher to edit the batch!
                        IconButton(
                            onClick = { showEditBatchDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(theme.primary.copy(alpha = 0.12f))
                                .border(1.dp, theme.primary.copy(alpha = 0.3f), CircleShape)
                                .testTag("edit_batch_plus_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Edit Batch & Student Roster",
                                tint = theme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("No Batch Configured for $selectedDay", fontWeight = FontWeight.Bold)
                        Text("Tap 'Create Batch' to set up a $selectedDay class", fontSize = 11.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = { showCreateBatchDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Create")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Session Date Info Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = theme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Session Date: $selectedDate",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF333344)
                )
            }

            TextButton(
                onClick = {
                    selectedDate = if (selectedDate == "24 Oct 2026") "17 Oct 2026" else "24 Oct 2026"
                    Toast.makeText(context, "Switched to session: $selectedDate (Edit anytime)", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Change Session Date", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Students List for the Selected Batch
        if (studentsInBatch.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PeopleOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No students in ${activeBatch?.name ?: "$selectedDay Batch"}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF444455)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Click the '+' icon on the top right corner of the batch card to add registered students to this batch.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showEditBatchDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Students to Batch")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(studentsInBatch) { student ->
                    val currentRecord = batchAttendance.find {
                        it.batchId == (activeBatch?.id ?: "") &&
                        it.studentId == student.id &&
                        it.date == selectedDate
                    }
                    val currentStatus = currentRecord?.status ?: "UNMARKED"

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5EB)),
                        modifier = Modifier.fillMaxWidth().testTag("attendance_student_${student.rollNo}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Student Name & Roll No Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(theme.primary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = student.rollNo,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = theme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = student.name,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF1E1E24)
                                        )
                                        Text(
                                            text = "Tier: ${student.tier} • ${student.tokens} Tokens",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                // Status Indicator Pill
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (currentStatus) {
                                        "PRESENT" -> Color(0xFFE8F5E9)
                                        "ABSENT" -> Color(0xFFFFEBEE)
                                        "LATE_HALF_DAY" -> Color(0xFFEEEEEE)
                                        else -> Color(0xFFFFF8E1)
                                    }
                                ) {
                                    Text(
                                        text = when (currentStatus) {
                                            "PRESENT" -> "Present ✓"
                                            "ABSENT" -> "Absent ✗"
                                            "LATE_HALF_DAY" -> "Late / Half-Day"
                                            else -> "Unmarked"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = when (currentStatus) {
                                            "PRESENT" -> Color(0xFF2E7D32)
                                            "ABSENT" -> Color(0xFFC62828)
                                            "LATE_HALF_DAY" -> Color(0xFF616161)
                                            else -> Color(0xFFE65100)
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 3 Boxes: 1 Green box (Present), 1 Red box (Absent), 1 Grey box (Late / Half-day)
                            // Allow teacher to edit attendance afterwards!
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 1. Green Box: Present
                                val isPresent = currentStatus == "PRESENT"
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isPresent) Color(0xFF2E7D32) else Color(0xFFE8F5E9),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isPresent) 2.dp else 1.dp,
                                        color = Color(0xFF2E7D32)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable {
                                            val bId = activeBatch?.id ?: "batch_mon"
                                            onMarkAttendance(bId, student.id, student.name, selectedDate, "PRESENT")
                                            Toast.makeText(context, "${student.name}: Present ✓", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("attendance_box_present_${student.rollNo}")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Present",
                                            tint = if (isPresent) Color.White else Color(0xFF2E7D32),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Present",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isPresent) Color.White else Color(0xFF2E7D32)
                                        )
                                    }
                                }

                                // 2. Red Box: Absent
                                val isAbsent = currentStatus == "ABSENT"
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isAbsent) Color(0xFFC62828) else Color(0xFFFFEBEE),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isAbsent) 2.dp else 1.dp,
                                        color = Color(0xFFC62828)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable {
                                            val bId = activeBatch?.id ?: "batch_mon"
                                            onMarkAttendance(bId, student.id, student.name, selectedDate, "ABSENT")
                                            Toast.makeText(context, "${student.name}: Absent ✗", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("attendance_box_absent_${student.rollNo}")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Absent",
                                            tint = if (isAbsent) Color.White else Color(0xFFC62828),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Absent",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isAbsent) Color.White else Color(0xFFC62828)
                                        )
                                    }
                                }

                                // 3. Grey Box: Late / Half-Day
                                val isLate = currentStatus == "LATE_HALF_DAY"
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isLate) Color(0xFF616161) else Color(0xFFEEEEEE),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isLate) 2.dp else 1.dp,
                                        color = Color(0xFF616161)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable {
                                            val bId = activeBatch?.id ?: "batch_mon"
                                            onMarkAttendance(bId, student.id, student.name, selectedDate, "LATE_HALF_DAY")
                                            Toast.makeText(context, "${student.name}: Late / Half-Day", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("attendance_box_late_${student.rollNo}")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = "Late / Half-Day",
                                            tint = if (isLate) Color.White else Color(0xFF616161),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Late",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isLate) Color.White else Color(0xFF616161)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Batch Dialog (Opened by '+' Icon on Top Right Corner of Batch Card)
    if (showEditBatchDialog && activeBatch != null) {
        var editName by remember { mutableStateOf(activeBatch.name) }
        var editDay by remember { mutableStateOf(activeBatch.dayOfWeek) }
        var editTime by remember { mutableStateOf(activeBatch.timeSlot) }
        var selectedStudentIds by remember { mutableStateOf(activeBatch.getStudentIdList()) }

        AlertDialog(
            onDismissRequest = { showEditBatchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = theme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Batch & Roster")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Batch Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editDay,
                        onValueChange = { editDay = it },
                        label = { Text("Weekly Day (e.g. Monday, Thursday)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editTime,
                        onValueChange = { editTime = it },
                        label = { Text("Class Timing (e.g. 5:00 PM - 7:00 PM)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add Students to Batch (${selectedStudentIds.size} of ${roster.size} Enrolled):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = theme.primary
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(roster) { student ->
                            val isEnrolled = selectedStudentIds.contains(student.id)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isEnrolled) theme.primary.copy(alpha = 0.12f) else Color(0xFFF7F7FA),
                                border = if (isEnrolled) androidx.compose.foundation.BorderStroke(1.dp, theme.primary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedStudentIds = if (isEnrolled) {
                                            selectedStudentIds - student.id
                                        } else {
                                            selectedStudentIds + student.id
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isEnrolled,
                                            onCheckedChange = { checked ->
                                                selectedStudentIds = if (checked) {
                                                    selectedStudentIds + student.id
                                                } else {
                                                    selectedStudentIds - student.id
                                                }
                                            }
                                        )
                                        Column {
                                            Text(student.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Roll: ${student.rollNo} • Tier: ${student.tier}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedBatch = activeBatch.copy(
                            name = editName.trim(),
                            dayOfWeek = editDay.trim(),
                            timeSlot = editTime.trim()
                        ).withStudentIds(selectedStudentIds)
                        onSaveBatch(updatedBatch)
                        showEditBatchDialog = false
                        Toast.makeText(context, "Batch updated with ${selectedStudentIds.size} students!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBatchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Create Batch Dialog
    if (showCreateBatchDialog) {
        var newBatchName by remember { mutableStateOf("$selectedDay Champions") }
        var newBatchDay by remember { mutableStateOf(selectedDay) }
        var newBatchTime by remember { mutableStateOf("5:00 PM - 7:00 PM") }
        var newSelectedStudentIds by remember { mutableStateOf(listOf<String>()) }

        AlertDialog(
            onDismissRequest = { showCreateBatchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = theme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create New Weekly Batch")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newBatchName,
                        onValueChange = { newBatchName = it },
                        label = { Text("Batch Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newBatchDay,
                        onValueChange = { newBatchDay = it },
                        label = { Text("Day of Week (e.g. Monday, Thursday)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newBatchTime,
                        onValueChange = { newBatchTime = it },
                        label = { Text("Timing (e.g. 5:00 PM - 7:00 PM)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add Students from Roster (${newSelectedStudentIds.size} Selected):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = theme.primary
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(roster) { student ->
                            val isSelected = newSelectedStudentIds.contains(student.id)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) theme.primary.copy(alpha = 0.12f) else Color(0xFFF7F7FA),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, theme.primary) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        newSelectedStudentIds = if (isSelected) {
                                            newSelectedStudentIds - student.id
                                        } else {
                                            newSelectedStudentIds + student.id
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            newSelectedStudentIds = if (checked) {
                                                newSelectedStudentIds + student.id
                                            } else {
                                                newSelectedStudentIds - student.id
                                            }
                                        }
                                    )
                                    Column {
                                        Text(student.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Roll: ${student.rollNo} • Tier: ${student.tier}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newBatch = StudentBatch(
                            id = "batch_${System.currentTimeMillis()}",
                            name = newBatchName.trim(),
                            dayOfWeek = newBatchDay.trim(),
                            timeSlot = newBatchTime.trim()
                        ).withStudentIds(newSelectedStudentIds)
                        onSaveBatch(newBatch)
                        showCreateBatchDialog = false
                        Toast.makeText(context, "Batch ${newBatch.name} created!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                ) {
                    Text("Create Batch")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateBatchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
private fun SpeedAccuracyAnalyticsTab(
    analytics: AnalyticsRecord?,
    theme: ArenaTheme,
    onUpdateAnalytics: (AnalyticsRecord) -> Unit
) {
    val context = LocalContext.current
    val current = analytics ?: AnalyticsRecord()
    var showVisualizerPreview by remember { mutableStateOf(false) }

    if (showVisualizerPreview) {
        com.example.ui.analytics.PerformanceAnalyticsScreen(
            analytics = current,
            theme = theme,
            studentName = "Teacher Preview (Sreehith A S)",
            className = "Class VI-A",
            onBack = { showVisualizerPreview = false }
        )
        return
    }

    var addMs by remember { mutableStateOf(current.addLatencyMs.toString()) }
    var subMs by remember { mutableStateOf(current.subLatencyMs.toString()) }
    var mulMs by remember { mutableStateOf(current.mulLatencyMs.toString()) }
    var divMs by remember { mutableStateOf(current.divLatencyMs.toString()) }

    var mon by remember { mutableStateOf(current.monAcc.toString()) }
    var tue by remember { mutableStateOf(current.tueAcc.toString()) }
    var wed by remember { mutableStateOf(current.wedAcc.toString()) }
    var thu by remember { mutableStateOf(current.thuAcc.toString()) }
    var fri by remember { mutableStateOf(current.friAcc.toString()) }
    var sat by remember { mutableStateOf(current.satAcc.toString()) }
    var sun by remember { mutableStateOf(current.sunAcc.toString()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Enter Latency per Operation (ms):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("Updates parent charts in real-time", fontSize = 10.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = addMs, onValueChange = { addMs = it }, label = { Text("Add (+)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = subMs, onValueChange = { subMs = it }, label = { Text("Sub (-)") }, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = mulMs, onValueChange = { mulMs = it }, label = { Text("Mul (×)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = divMs, onValueChange = { divMs = it }, label = { Text("Div (÷)") }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Enter 7-Day Accuracy Trend (%):", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("AI draws line chart from these percentages", fontSize = 10.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = mon, onValueChange = { mon = it }, label = { Text("Mon") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = tue, onValueChange = { tue = it }, label = { Text("Tue") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = wed, onValueChange = { wed = it }, label = { Text("Wed") }, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = thu, onValueChange = { thu = it }, label = { Text("Thu") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = fri, onValueChange = { fri = it }, label = { Text("Fri") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = sat, onValueChange = { sat = it }, label = { Text("Sat") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = sun, onValueChange = { sun = it }, label = { Text("Sun") }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val updated = AnalyticsRecord(
                            id = 1,
                            addLatencyMs = addMs.toIntOrNull() ?: 210,
                            subLatencyMs = subMs.toIntOrNull() ?: 235,
                            mulLatencyMs = mulMs.toIntOrNull() ?: 310,
                            divLatencyMs = divMs.toIntOrNull() ?: 390,
                            monAcc = mon.toIntOrNull() ?: 92,
                            tueAcc = tue.toIntOrNull() ?: 95,
                            wedAcc = wed.toIntOrNull() ?: 93,
                            thuAcc = thu.toIntOrNull() ?: 98,
                            friAcc = fri.toIntOrNull() ?: 96,
                            satAcc = sat.toIntOrNull() ?: 100,
                            sunAcc = sun.toIntOrNull() ?: 98
                        )
                        onUpdateAnalytics(updated)
                        Toast.makeText(context, "Analytics updated! AI regenerated parent charts.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.AutoGraph, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("REGENERATE AI ANALYTICS CHARTS", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showVisualizerPreview = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.primary),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primary),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("preview_recharts_analytics_btn")
                ) {
                    Icon(Icons.Default.QueryStats, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PREVIEW STUDENT RECHARTS VISUALIZER", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TeacherAvatarGiftingTab(
    avatarItems: List<AvatarItem>,
    theme: ArenaTheme,
    onGrantAvatarItem: (itemId: String) -> Unit
) {
    val context = LocalContext.current
    val teacherItems = avatarItems.filter { it.isTeacherExclusive }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Teacher Exclusive Avatar Awards", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Grant special avatar shirts, pants, and crowns to students", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(teacherItems) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(item.previewColorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(item.description, fontSize = 11.sp, color = Color.Gray)
                            Text("Category: ${item.category}", fontSize = 10.sp, color = theme.primary, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                onGrantAvatarItem(item.id)
                                Toast.makeText(context, "Conferred ${item.name} to Student Sreehith!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (item.isUnlocked) Color(0xFF2E7D32) else theme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (item.isUnlocked) "Granted ✓" else "Grant Award", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
