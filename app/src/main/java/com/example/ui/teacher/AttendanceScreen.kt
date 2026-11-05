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
import com.example.data.model.BatchAttendanceRecord
import com.example.data.model.RosterStudent
import com.example.data.model.StudentBatch
import com.example.ui.theme.ArenaTheme

private val DAYS_OF_WEEK = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

@Composable
fun AttendanceScreen(
    batches: List<StudentBatch>,
    roster: List<RosterStudent>,
    batchAttendance: List<BatchAttendanceRecord>,
    theme: ArenaTheme,
    onSaveBatch: (StudentBatch) -> Unit,
    onMarkAttendance: (batchId: String, studentId: String, studentName: String, date: String, status: String) -> Unit,
    onOpenBatchManager: (() -> Unit)? = null,
    initialSelectedDay: String = "Monday",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDay by remember { mutableStateOf(initialSelectedDay) }
    var selectedDate by remember { mutableStateOf("24 Oct 2026") }
    var showEditBatchDialog by remember { mutableStateOf(false) }
    var showCreateBatchDialog by remember { mutableStateOf(false) }

    // Find all batches for the selected day
    val batchesForSelectedDay = remember(batches, selectedDay) {
        batches.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
    }

    var selectedBatchId by remember(batchesForSelectedDay) {
        mutableStateOf(batchesForSelectedDay.firstOrNull()?.id ?: "")
    }

    // Active batch
    val activeBatch = batchesForSelectedDay.find { it.id == selectedBatchId } ?: batchesForSelectedDay.firstOrNull()

    // Students enrolled in this active batch
    val studentsInBatch = remember(activeBatch, roster) {
        if (activeBatch == null) emptyList() else {
            val enrolledIds = activeBatch.getStudentIdList()
            roster.filter { it.id in enrolledIds }
        }
    }

    // Attendance stats for selected batch & date
    val stats = remember(studentsInBatch, batchAttendance, activeBatch, selectedDate) {
        val bId = activeBatch?.id ?: ""
        var present = 0
        var absent = 0
        var late = 0
        var unmarked = 0

        studentsInBatch.forEach { st ->
            val rec = batchAttendance.find { it.batchId == bId && it.studentId == st.id && it.date == selectedDate }
            when (rec?.status) {
                "PRESENT" -> present++
                "ABSENT" -> absent++
                "LATE_HALF_DAY" -> late++
                else -> unmarked++
            }
        }
        AttendanceDayStats(
            total = studentsInBatch.size,
            present = present,
            absent = absent,
            lateHalfDay = late,
            unmarked = unmarked
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("attendance_screen_container"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Day of Week Filter Bar (Monday .. Sunday)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5EB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Filter Scholars by Batch Day",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E1E24)
                            )
                            Text(
                                text = "Select a day to take & edit attendance",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        if (onOpenBatchManager != null) {
                            TextButton(
                                onClick = onOpenBatchManager,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Manage Batches", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val dayScrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(dayScrollState),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DAYS_OF_WEEK.forEach { day ->
                            val isDaySelected = selectedDay.equals(day, ignoreCase = true)
                            val dayBatchCount = batches.count { it.dayOfWeek.equals(day, ignoreCase = true) }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDaySelected) theme.primary else Color(0xFFF4F4F8),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDaySelected) theme.primary else Color(0xFFE2E2EA)
                                ),
                                modifier = Modifier
                                    .clickable { selectedDay = day }
                                    .testTag("attendance_day_tab_${day.lowercase()}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = day.take(3).uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = if (isDaySelected) Color.White else Color(0xFF2C3E50)
                                    )
                                    Text(
                                        text = if (dayBatchCount > 0) "$dayBatchCount ${if (dayBatchCount == 1) "batch" else "batches"}" else "No batch",
                                        fontSize = 9.sp,
                                        color = if (isDaySelected) Color.White.copy(alpha = 0.85f) else Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Batch Card with the Top Right Corner '+' icon to edit batch & add students!
        item {
            if (activeBatch != null) {
                Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("attendance_batch_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeBatch.name,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1E1E24)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
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
                                text = "Timing: ${activeBatch.timeSlot} • Weekly Class Slot",
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

                        // Top Right Corner '+' icon: Allows teacher to edit the batch!
                        IconButton(
                            onClick = { showEditBatchDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(theme.primary.copy(alpha = 0.12f))
                                .border(1.dp, theme.primary.copy(alpha = 0.35f), CircleShape)
                                .testTag("attendance_batch_edit_plus_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Edit Batch & Add Students (+)",
                                tint = theme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Multi-batch switcher if multiple batches exist on this day
                    if (batchesForSelectedDay.size > 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Switch $selectedDay Batch:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            batchesForSelectedDay.forEach { b ->
                                val isCur = b.id == activeBatch.id
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isCur) theme.primary else Color(0xFFF1F1F6),
                                    modifier = Modifier.clickable { selectedBatchId = b.id }
                                ) {
                                    Text(
                                        text = b.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCur) Color.White else Color(0xFF333344),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("No Batch Configured for $selectedDay", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Tap 'Create Batch' to set up a $selectedDay class", fontSize = 11.sp, color = Color(0xFF5D4037))
                    }
                    Button(
                        onClick = { showCreateBatchDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("attendance_create_batch_day_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create")
                    }
                }
            }
        }
    }

        // Session Date & Attendance Stats Bar (Moves with scrolling!)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFE)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8EE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("attendance_session_card")
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = theme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Session: $selectedDate",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF1E1E24)
                            )
                        }

                        TextButton(
                            onClick = {
                                selectedDate = if (selectedDate == "24 Oct 2026") "17 Oct 2026" else "24 Oct 2026"
                                Toast.makeText(context, "Session changed to $selectedDate (Edit anytime)", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.EditCalendar, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change Date", fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Stats Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        AttendanceStatPill(
                            label = "Present",
                            count = stats.present,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceStatPill(
                            label = "Absent",
                            count = stats.absent,
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceStatPill(
                            label = "Late/Half",
                            count = stats.lateHalfDay,
                            color = Color(0xFF616161),
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceStatPill(
                            label = "Unmarked",
                            count = stats.unmarked,
                            color = Color(0xFFE65100),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Students List for the Selected Batch Day
        if (studentsInBatch.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PeopleOutline,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
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
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (activeBatch != null) {
                                    showEditBatchDialog = true
                                } else {
                                    showCreateBatchDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("attendance_empty_add_students_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (activeBatch != null) "Add Students to Batch" else "Create $selectedDay Batch")
                        }
                    }
                }
            }
        } else {
            items(studentsInBatch, key = { it.id }) { student ->
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("attendance_student_${student.rollNo}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Student Info Row: Roll No, Name, Tier, Tokens, Current Status Pill
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
                                            .background(theme.primary.copy(alpha = 0.12f)),
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

                            // Three Clickable Status Boxes:
                            // 1 Green box (Present), 1 Red box (Absent), 1 Grey box (Late / Half-day)
                            // Allows teacher to edit attendance afterwards!
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 1. GREEN BOX: Present
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
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
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

                                // 2. RED BOX: Absent
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
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
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

                                // 3. GREY BOX: Late / Half-Day
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
                                            Toast.makeText(context, "${student.name}: Marked Late / Half-Day", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("attendance_box_late_${student.rollNo}")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = "Late / Half-Day",
                                            tint = if (isLate) Color.White else Color(0xFF616161),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Late/Half",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
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

    // Edit Batch Dialog triggered by '+' icon on batch card
    if (showEditBatchDialog && activeBatch != null) {
        CreateEditBatchDialog(
            initialBatch = activeBatch,
            isEditing = true,
            roster = roster,
            allBatches = batches,
            theme = theme,
            onDismiss = { showEditBatchDialog = false },
            onSave = { updated ->
                onSaveBatch(updated)
                Toast.makeText(context, "Updated ${updated.name} roster (${updated.getStudentIdList().size} scholars)", Toast.LENGTH_SHORT).show()
                showEditBatchDialog = false
            }
        )
    }

    // Create Batch Dialog for this selected day
    if (showCreateBatchDialog) {
        val newBatch = StudentBatch(
            id = "batch_${selectedDay.lowercase().take(3)}_${System.currentTimeMillis()}",
            name = "$selectedDay Batch",
            dayOfWeek = selectedDay,
            timeSlot = "5:00 PM - 7:00 PM",
            studentIds = ""
        )
        CreateEditBatchDialog(
            initialBatch = newBatch,
            isEditing = false,
            roster = roster,
            allBatches = batches,
            theme = theme,
            onDismiss = { showCreateBatchDialog = false },
            onSave = { created ->
                onSaveBatch(created)
                selectedBatchId = created.id
                Toast.makeText(context, "Created ${created.name}!", Toast.LENGTH_SHORT).show()
                showCreateBatchDialog = false
            }
        )
    }
}

private data class AttendanceDayStats(
    val total: Int,
    val present: Int,
    val absent: Int,
    val lateHalfDay: Int,
    val unmarked: Int
)

@Composable
private fun AttendanceStatPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
