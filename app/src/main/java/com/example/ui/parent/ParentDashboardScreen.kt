package com.example.ui.parent

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalyticsRecord
import com.example.data.model.AttendanceLog
import com.example.data.model.ClassDiaryEntry
import com.example.data.model.HomeworkRecord
import com.example.data.model.NoticeEvent
import com.example.data.model.UserProfile
import com.example.ui.theme.ArenaTheme

enum class DiaryTab {
    CLASSWORK,
    HOMEWORK
}

@Composable
fun ParentDashboardScreen(
    profile: UserProfile,
    homeworkList: List<HomeworkRecord>,
    attendanceLogs: List<AttendanceLog>,
    classDiary: List<ClassDiaryEntry>,
    notices: List<NoticeEvent>,
    analytics: AnalyticsRecord?,
    theme: ArenaTheme,
    onPayFees: () -> Unit,
    onConfirmPayment: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPaymentModal by remember { mutableStateOf(false) }
    var showReceiptModal by remember { mutableStateOf(false) }
    var showFullPaymentScreen by remember { mutableStateOf(false) }
    var diaryTab by remember { mutableStateOf(DiaryTab.CLASSWORK) }

    val currentAnalytics = analytics ?: AnalyticsRecord()
    var showFullAnalyticsScreen by remember { mutableStateOf(false) }

    if (showFullPaymentScreen) {
        PaymentScreen(
            profile = profile,
            theme = theme,
            onConfirmPayment = { utr ->
                onConfirmPayment(utr)
            },
            onBack = { showFullPaymentScreen = false }
        )
        return
    }

    if (showFullAnalyticsScreen) {
        com.example.ui.analytics.PerformanceAnalyticsScreen(
            analytics = currentAnalytics,
            theme = theme,
            studentName = profile.name,
            className = profile.className,
            onBack = { showFullAnalyticsScreen = false }
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section: "What is Done in Class" Diary with See Classwork & See Homework tabs
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("parent_class_diary_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(theme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("What is Done in Class", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                            Text("Daily classroom activities & homework tracking", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2-Option Pill Selector: See Classwork vs See Homework
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF4F4F8),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(3.dp)) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (diaryTab == DiaryTab.CLASSWORK) theme.primary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { diaryTab = DiaryTab.CLASSWORK }
                                    .testTag("see_classwork_tab")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = if (diaryTab == DiaryTab.CLASSWORK) Color.White else Color(0xFF555566),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "See Classwork",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (diaryTab == DiaryTab.CLASSWORK) Color.White else Color(0xFF555566)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (diaryTab == DiaryTab.HOMEWORK) theme.primary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { diaryTab = DiaryTab.HOMEWORK }
                                    .testTag("see_homework_tab")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Assignment,
                                        contentDescription = null,
                                        tint = if (diaryTab == DiaryTab.HOMEWORK) Color.White else Color(0xFF555566),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "See Homework",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (diaryTab == DiaryTab.HOMEWORK) Color.White else Color(0xFF555566)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredDiary = classDiary.filter { it.type == diaryTab.name }

                    if (filteredDiary.isEmpty()) {
                        Text(
                            text = "No ${diaryTab.name.lowercase()} entries logged for this week yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        filteredDiary.forEach { entry ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (diaryTab == DiaryTab.CLASSWORK) Color(0xFFF1F8E9) else Color(0xFFFFF8E1),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (diaryTab == DiaryTab.CLASSWORK) Color(0xFFA5D6A7) else Color(0xFFFFD54F)
                                ),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(entry.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (diaryTab == DiaryTab.CLASSWORK) Color(0xFF2E7D32) else Color(0xFFE65100)
                                        ) {
                                            Text(entry.date, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(entry.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF333344), fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Formula Focus: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = theme.primary)
                                        Text(entry.formulaFocus, fontSize = 10.sp, color = theme.primary, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("Logged by ${entry.teacherName}", fontSize = 9.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Analytics & Performance with AI-Generated Dynamic Charts
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                modifier = Modifier.fillMaxWidth().testTag("parent_analytics_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1565C0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Insights, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Speed & Accuracy Analytics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                        }
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE3F2FD)) {
                            Text("AI Dynamic", fontSize = 10.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Calculation Latency per Operation (ms):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Operation Latencies (Dynamically fed from currentAnalytics!)
                    val opLatencies = listOf(
                        Triple("Addition (+)", currentAnalytics.addLatencyMs, Color(0xFF2E7D32)),
                        Triple("Subtraction (-)", currentAnalytics.subLatencyMs, Color(0xFF1565C0)),
                        Triple("Multiplication (×)", currentAnalytics.mulLatencyMs, Color(0xFFFFA000)),
                        Triple("Division (÷)", currentAnalytics.divLatencyMs, Color(0xFFD32F2F))
                    )

                    opLatencies.forEach { (opName, ms, color) ->
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(opName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333344))
                                Text("$ms ms", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFFEEEEF2))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((ms / 500f).coerceIn(0.1f, 1f))
                                        .fillMaxHeight()
                                        .background(color)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Accuracy Trend Line Chart (Fed by Teacher Input)
                    Text("Daily Accuracy Trend (Last 7 Days):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    val accuracyPoints = listOf(
                        currentAnalytics.monAcc.toFloat(),
                        currentAnalytics.tueAcc.toFloat(),
                        currentAnalytics.wedAcc.toFloat(),
                        currentAnalytics.thuAcc.toFloat(),
                        currentAnalytics.friAcc.toFloat(),
                        currentAnalytics.satAcc.toFloat(),
                        currentAnalytics.sunAcc.toFloat()
                    )
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(Color(0xFFFAFAFC), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val spacing = w / (accuracyPoints.size - 1)
                            val path = Path()

                            accuracyPoints.forEachIndexed { i, acc ->
                                val x = i * spacing
                                val y = h - ((acc - 80f) / 20f).coerceIn(0f, 1f) * (h - 20f)
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }

                            drawPath(
                                path = path,
                                color = Color(0xFF2E7D32),
                                style = Stroke(width = 3.dp.toPx())
                            )

                            accuracyPoints.forEachIndexed { i, acc ->
                                val x = i * spacing
                                val y = h - ((acc - 80f) / 20f).coerceIn(0f, 1f) * (h - 20f)
                                drawCircle(color = Color(0xFF2E7D32), radius = 4.dp.toPx(), center = Offset(x, y))
                                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(x, y))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEachIndexed { idx, d ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(d, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text("${accuracyPoints[idx].toInt()}%", fontSize = 8.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showFullAnalyticsScreen = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        modifier = Modifier.fillMaxWidth().testTag("open_recharts_analytics_btn")
                    ) {
                        Icon(Icons.Default.QueryStats, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Full Recharts Analytics Visualizer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section: Attendance & Fee Portal
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                modifier = Modifier.fillMaxWidth().testTag("parent_attendance_fee_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Attendance & Fee Portal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Class Sessions", fontSize = 10.sp, color = Color(0xFF2E7D32))
                                Text("${profile.attendancePercent}% (11 / 12)", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF1B5E20))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (profile.feeStatus == "Paid") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Monthly Term Fee", fontSize = 10.sp, color = if (profile.feeStatus == "Paid") Color(0xFF2E7D32) else Color(0xFFC62828))
                                Text("₹${profile.feeAmount} (${profile.feeStatus})", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = if (profile.feeStatus == "Paid") Color(0xFF1B5E20) else Color(0xFFB71C1C))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("October 2026 Session Attendance Calendar:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    val daysGrid = (1..30).toList()
                    val rows = daysGrid.chunked(7)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        rows.forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                week.forEach { d ->
                                    val log = attendanceLogs.find { it.day == d }
                                    val isHoliday = log?.status == "Holiday" || d % 7 == 0
                                    val isAbsent = log?.status == "Absent" || d in listOf(4, 18)
                                    val color = when {
                                        isHoliday -> Color(0xFFFFD54F)
                                        isAbsent -> Color(0xFFFF5252)
                                        else -> Color(0xFF4CAF50)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(color.copy(alpha = 0.15f))
                                            .border(1.dp, color, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$d",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = color
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("● Present (Green)   ● Absent (Red)   ● Sunday/Holiday (Yellow)", fontSize = 10.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (profile.feeStatus != "Paid") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { showFullPaymentScreen = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                                modifier = Modifier.fillMaxWidth().testTag("parent_pay_fee_modal_button")
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pay Term Fee via UPI QR (₹${profile.feeAmount})", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showFullPaymentScreen = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("view_payment_details_btn")
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = theme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("View Fee Breakdown & Payment Details", color = theme.primary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showFullPaymentScreen = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("view_receipt_button")
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("View Official Fee Receipt & History (#UC-8924)", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section: Homework Log & Teacher Feedback
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                modifier = Modifier.fillMaxWidth().testTag("parent_homework_log_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE65100)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Homework Verification Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    homeworkList.forEach { hw ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9F9FC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEF)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(hw.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE8F5E9)) {
                                        Text(hw.status, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Score: ${hw.score} • Formulas: ${hw.formulaUsed}", fontSize = 11.sp, color = theme.primary, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Teacher Feedback: \"${hw.feedback}\"", fontSize = 11.sp, color = Color(0xFF555566), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            }
                        }
                    }
                }
            }
        }

        // Section: UCMAS Competition Notices & Events
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD54F)),
                modifier = Modifier.fillMaxWidth().testTag("competition_notice_board")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("UCMAS Center Notices & Events", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    notices.forEach { note ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("• ${note.title}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF3E2723))
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE65100)) {
                                    Text(note.date, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text(note.description, fontSize = 11.sp, color = Color(0xFF6D4C41))
                            Text("Venue: ${note.venue}", fontSize = 10.sp, color = Color(0xFF8D6E63), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Payment Dialog
    if (showPaymentModal) {
        AlertDialog(
            onDismissRequest = { showPaymentModal = false },
            title = { Text("Online Fee Payment", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("UCMAS Thane Kopri East Center", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("Student: ${profile.name} (${profile.className})", fontSize = 12.sp, color = Color.Gray)
                    Text("Fee for October 2026: ₹${profile.feeAmount}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = theme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Select Payment Gateway:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF1F8E9), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("UPI (Google Pay / PhonePe / Paytm)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPayFees()
                        showPaymentModal = false
                        Toast.makeText(context, "Payment Successful! Receipt generated.", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                ) {
                    Text("Pay ₹${profile.feeAmount}")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaymentModal = false }) { Text("Cancel") }
            }
        )
    }

    // Receipt Dialog
    if (showReceiptModal) {
        AlertDialog(
            onDismissRequest = { showReceiptModal = false },
            title = { Text("Official Fee Receipt", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(color = Color(0xFFF5F5FA), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("RECEIPT NO: #UC-8924", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = theme.primary)
                            Text("UCMAS Mental Arithmetic • Kopri East Center", fontSize = 11.sp, color = Color.Gray)
                            Divider(modifier = Modifier.padding(vertical = 6.dp))
                            Text("Student Name: ${profile.name}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("UID: ${profile.uid} • ${profile.className}", fontSize = 11.sp, color = Color.Gray)
                            Text("Amount Paid: ₹${profile.feeAmount}.00", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Payment Mode: UPI Confirmed", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            Text("Date: 23-Oct-2026 • Status: CLEARED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showReceiptModal = false }) { Text("Close") }
            }
        )
    }
}
