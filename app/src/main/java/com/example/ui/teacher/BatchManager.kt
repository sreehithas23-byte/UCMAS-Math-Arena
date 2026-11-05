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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.RosterStudent
import com.example.data.model.StudentBatch
import com.example.ui.theme.ArenaTheme

private val DAYS_OF_WEEK = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
private val DEFAULT_TIME_SLOTS = listOf(
    "5:00 PM - 7:00 PM",
    "9:00 AM - 11:00 AM",
    "11:00 AM - 1:00 PM",
    "4:00 PM - 6:00 PM",
    "6:00 PM - 8:00 PM"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BatchManager(
    batches: List<StudentBatch>,
    roster: List<RosterStudent>,
    theme: ArenaTheme,
    onSaveBatch: (StudentBatch) -> Unit,
    onDeleteBatch: ((StudentBatch) -> Unit)? = null,
    onNavigateToAttendance: ((dayOfWeek: String, batchId: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDayFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var editingBatch by remember { mutableStateOf<StudentBatch?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var batchToDelete by remember { mutableStateOf<StudentBatch?>(null) }

    val filteredBatches = remember(batches, selectedDayFilter, searchQuery) {
        batches.filter { batch ->
            val matchesDay = if (selectedDayFilter == "All") true else batch.dayOfWeek.equals(selectedDayFilter, ignoreCase = true)
            val matchesSearch = if (searchQuery.isBlank()) true else {
                batch.name.contains(searchQuery, ignoreCase = true) ||
                batch.dayOfWeek.contains(searchQuery, ignoreCase = true) ||
                batch.timeSlot.contains(searchQuery, ignoreCase = true)
            }
            matchesDay && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("batch_manager_container"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Top Header: Weekly Batch Manager card that moves/scrolls up smoothly with content!
        item(key = "header_weekly_batch_manager") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E5E5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_batch_manager_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(theme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = "Batches",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Weekly Batch Manager",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E1E24)
                                )
                                Text(
                                    text = "Students attend weekly once • Kopri Center",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("create_new_batch_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Batch", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search Bar with smooth rounded pill
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search batches by name or day...", fontSize = 12.sp, color = Color.Gray) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = theme.primary, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.primary,
                            unfocusedBorderColor = Color(0xFFE2E2EA),
                            focusedContainerColor = Color(0xFFFFF9F9),
                            unfocusedContainerColor = Color(0xFFFBFBFC)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("batch_search_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Day of Week Filter Pills
                    val filterScrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(filterScrollState),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val daysWithAll = listOf("All") + DAYS_OF_WEEK
                        daysWithAll.forEach { day ->
                            val isSelected = selectedDayFilter == day
                            val batchCount = if (day == "All") batches.size else batches.count { it.dayOfWeek.equals(day, ignoreCase = true) }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) theme.primary else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) theme.primary else Color(0xFFE8D8D8)
                                ),
                                modifier = Modifier
                                    .clickable { selectedDayFilter = day }
                                    .testTag("batch_filter_day_${day.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = day,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF333344)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) Color.White.copy(alpha = 0.25f) else Color(0xFFFFEBEE)
                                    ) {
                                        Text(
                                            text = "$batchCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else theme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Batches List or Empty State inside scrollable LazyColumn
        if (filteredBatches.isEmpty()) {
            item(key = "empty_batch_state") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventBusy,
                                contentDescription = null,
                                tint = theme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (selectedDayFilter == "All") "No Batches Created Yet" else "No Batch for $selectedDayFilter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF333344)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create a batch to assign registered scholars to their weekly class slot.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showCreateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (selectedDayFilter == "All") "Create First Batch" else "Create $selectedDayFilter Batch")
                        }
                    }
                }
            }
        } else {
            items(filteredBatches, key = { it.id }) { batch ->
                val enrolledStudentIds = batch.getStudentIdList()
                val enrolledStudents = roster.filter { it.id in enrolledStudentIds }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF2E6E6)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("batch_card_${batch.id}")
                ) {
                    Column {
                        // Attached Ribbon Row (matching Screenshot 2 date badge aesthetic)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(topStart = 18.dp, bottomEnd = 12.dp),
                                color = theme.primary
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = batch.dayOfWeek,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.padding(end = 12.dp, top = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFFFF0F0)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = theme.primary,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = batch.timeSlot,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = theme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = { editingBatch = batch },
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(theme.primary.copy(alpha = 0.1f))
                                        .testTag("batch_card_edit_plus_${batch.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Batch",
                                        tint = theme.primary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = batch.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color(0xFF1E1E24)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Enrolled count & registered total
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (enrolledStudents.isNotEmpty()) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (enrolledStudents.isNotEmpty()) Color(0xFF2E7D32) else Color(0xFFE65100),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${enrolledStudents.size} scholars assigned (out of ${roster.size} registered)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (enrolledStudents.isNotEmpty()) Color(0xFF2E7D32) else Color(0xFFE65100)
                                        )
                                    }
                                }

                                Text(
                                    text = "Weekly Slot",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF673AB7)
                                )
                            }

                            // Enrolled student chips (or empty prompt)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (enrolledStudents.isEmpty()) {
                                Text(
                                    text = "No students assigned yet. Tap the '+' icon or 'Edit Batch' to assign registered scholars.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            } else {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    enrolledStudents.take(8).forEach { student ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFFF5F5FA),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E8))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = student.name,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF2C3E50)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "(${student.rollNo})",
                                                    fontSize = 8.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                    if (enrolledStudents.size > 8) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = theme.primary.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = "+${enrolledStudents.size - 8} more",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = theme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action buttons row: Take Attendance, Edit, Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Take Attendance button
                                Button(
                                    onClick = {
                                        onNavigateToAttendance?.invoke(batch.dayOfWeek, batch.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .testTag("batch_take_attendance_${batch.id}")
                                ) {
                                    Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Take Attendance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                // Edit button
                                OutlinedButton(
                                    onClick = { editingBatch = batch },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("batch_edit_btn_${batch.id}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit", fontSize = 11.sp)
                                }

                                // Delete button
                                if (onDeleteBatch != null) {
                                    IconButton(
                                        onClick = { batchToDelete = batch },
                                        modifier = Modifier.size(36.dp).testTag("batch_delete_btn_${batch.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Batch",
                                            tint = Color(0xFFD32F2F),
                                            modifier = Modifier.size(18.dp)
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

    // Create or Edit Batch Modal
    if (showCreateDialog || editingBatch != null) {
        val isEditing = editingBatch != null
        val targetBatch = editingBatch ?: StudentBatch(
            id = "batch_${System.currentTimeMillis()}",
            name = if (selectedDayFilter != "All") "$selectedDayFilter Batch" else "Monday Class",
            dayOfWeek = if (selectedDayFilter != "All") selectedDayFilter else "Monday",
            timeSlot = "5:00 PM - 7:00 PM",
            studentIds = ""
        )

        CreateEditBatchDialog(
            initialBatch = targetBatch,
            isEditing = isEditing,
            roster = roster,
            allBatches = batches,
            theme = theme,
            onDismiss = {
                showCreateDialog = false
                editingBatch = null
            },
            onSave = { updatedBatch ->
                onSaveBatch(updatedBatch)
                Toast.makeText(context, "Batch '${updatedBatch.name}' saved with ${updatedBatch.getStudentIdList().size} scholars!", Toast.LENGTH_SHORT).show()
                showCreateDialog = false
                editingBatch = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (batchToDelete != null) {
        val b = batchToDelete!!
        AlertDialog(
            onDismissRequest = { batchToDelete = null },
            title = {
                Text("Delete Batch?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to delete '${b.name}' (${b.dayOfWeek})? Assigned students will become unassigned.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteBatch?.invoke(b)
                        Toast.makeText(context, "Batch '${b.name}' deleted", Toast.LENGTH_SHORT).show()
                        batchToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { batchToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CreateEditBatchDialog(
    initialBatch: StudentBatch,
    isEditing: Boolean,
    roster: List<RosterStudent>,
    allBatches: List<StudentBatch>,
    theme: ArenaTheme,
    onDismiss: () -> Unit,
    onSave: (StudentBatch) -> Unit
) {
    var name by remember { mutableStateOf(initialBatch.name) }
    var selectedDay by remember { mutableStateOf(initialBatch.dayOfWeek) }
    var timeSlot by remember { mutableStateOf(initialBatch.timeSlot) }
    var selectedStudentIds by remember { mutableStateOf(initialBatch.getStudentIdList().toSet()) }
    var studentSearchQuery by remember { mutableStateOf("") }

    val filteredRoster = remember(roster, studentSearchQuery) {
        if (studentSearchQuery.isBlank()) roster else {
            roster.filter {
                it.name.contains(studentSearchQuery, ignoreCase = true) ||
                it.rollNo.contains(studentSearchQuery, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.EditCalendar else Icons.Default.AddBusiness,
                    contentDescription = null,
                    tint = theme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Edit Batch & Scholars" else "Create Weekly Batch",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Batch Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Batch Name (e.g. Thursday Champions)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("batch_name_input")
                )

                // Day of Week Selector
                Text(
                    text = "Weekly Class Day (Students attend once weekly):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333344)
                )
                val daysScroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(daysScroll),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DAYS_OF_WEEK.forEach { day ->
                        val isDaySelected = selectedDay.equals(day, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isDaySelected) theme.primary else Color(0xFFF0F0F5),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDaySelected) theme.primary else Color(0xFFDCDCE5)
                            ),
                            modifier = Modifier
                                .clickable { selectedDay = day }
                                .testTag("select_batch_day_${day.lowercase()}")
                        ) {
                            Text(
                                text = day,
                                fontSize = 11.sp,
                                fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isDaySelected) Color.White else Color(0xFF333344),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Timing Selector / Custom Input
                OutlinedTextField(
                    value = timeSlot,
                    onValueChange = { timeSlot = it },
                    label = { Text("Class Timing") },
                    placeholder = { Text("e.g. 5:00 PM - 7:00 PM") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("batch_timing_input")
                )

                // Quick Timing Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("5:00 PM - 7:00 PM", "9:00 AM - 11:00 AM", "4:00 PM - 6:00 PM").forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (timeSlot == preset) theme.primary.copy(alpha = 0.15f) else Color(0xFFF5F5FA),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (timeSlot == preset) theme.primary else Color(0xFFE0E0E0)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { timeSlot = preset }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (timeSlot == preset) theme.primary else Color(0xFF555566),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                Divider(color = Color(0xFFE5E5EB))

                // Assign Students to Batch Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Assign Registered Scholars",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1E1E24)
                        )
                        Text(
                            text = "${selectedStudentIds.size} of ${roster.size} scholars assigned",
                            fontSize = 10.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = { selectedStudentIds = roster.map { it.id }.toSet() },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Select All", fontSize = 10.sp)
                        }
                        TextButton(
                            onClick = { selectedStudentIds = emptySet() },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Clear", fontSize = 10.sp, color = Color.Red)
                        }
                    }
                }

                // Student search
                OutlinedTextField(
                    value = studentSearchQuery,
                    onValueChange = { studentSearchQuery = it },
                    placeholder = { Text("Filter registered students...", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("batch_student_search_input")
                )

                // Scrollable Student Checkbox List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredRoster, key = { it.id }) { student ->
                        val isChecked = student.id in selectedStudentIds

                        // Check if assigned in another batch
                        val otherBatch = allBatches.firstOrNull { b ->
                            b.id != initialBatch.id && student.id in b.getStudentIdList()
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isChecked) theme.primary.copy(alpha = 0.08f) else Color(0xFFFAFAFD),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isChecked) theme.primary else Color(0xFFE2E2E8)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedStudentIds = if (isChecked) {
                                        selectedStudentIds - student.id
                                    } else {
                                        selectedStudentIds + student.id
                                    }
                                }
                                .testTag("batch_student_item_${student.rollNo}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checked ->
                                            selectedStudentIds = if (checked) {
                                                selectedStudentIds + student.id
                                            } else {
                                                selectedStudentIds - student.id
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = theme.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = student.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF1E1E24)
                                        )
                                        Text(
                                            text = "Roll: ${student.rollNo} • Tier: ${student.tier}",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                if (otherBatch != null) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFFFF3E0)
                                    ) {
                                        Text(
                                            text = "In ${otherBatch.name} (${otherBatch.dayOfWeek})",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE65100),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
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
                    if (name.isNotBlank()) {
                        val updated = initialBatch.copy(
                            name = name.trim(),
                            dayOfWeek = selectedDay,
                            timeSlot = timeSlot.trim().ifEmpty { "5:00 PM - 7:00 PM" },
                            studentIds = selectedStudentIds.joinToString(",")
                        )
                        onSave(updated)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_batch_button")
            ) {
                Text(if (isEditing) "Save Changes" else "Create Batch", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}
