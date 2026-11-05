package com.example.ui.analytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalyticsRecord
import com.example.ui.theme.ArenaTheme

data class PerformanceDataPoint(
    val dayLabel: String,
    val fullDate: String,
    val accuracy: Float,
    val latencyMs: Int,
    val formulasTested: String,
    val questionsCount: Int
)

enum class TimeRangeFilter(val label: String) {
    WEEK_7_DAYS("7 Days"),
    DAYS_14("14 Days"),
    MONTH_30("30 Days")
}

@Composable
fun PerformanceAnalyticsScreen(
    analytics: AnalyticsRecord?,
    theme: ArenaTheme,
    studentName: String = "SREEHITH A S",
    className: String = "Class VI-A",
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timeFilter by remember { mutableStateOf(TimeRangeFilter.WEEK_7_DAYS) }
    val currentAnalytics = analytics ?: AnalyticsRecord()

    // Data points derived from stored analytics and historical logs
    val dataPoints = remember(timeFilter, currentAnalytics) {
        when (timeFilter) {
            TimeRangeFilter.WEEK_7_DAYS -> listOf(
                PerformanceDataPoint("Mon", "17 Oct 2026", currentAnalytics.monAcc.toFloat(), currentAnalytics.addLatencyMs + 15, "Direct 2-Digit", 30),
                PerformanceDataPoint("Tue", "18 Oct 2026", currentAnalytics.tueAcc.toFloat(), currentAnalytics.addLatencyMs + 10, "+5 Little Friends", 35),
                PerformanceDataPoint("Wed", "19 Oct 2026", currentAnalytics.wedAcc.toFloat(), currentAnalytics.subLatencyMs + 5, "+10 Big Friends", 40),
                PerformanceDataPoint("Thu", "20 Oct 2026", currentAnalytics.thuAcc.toFloat(), currentAnalytics.addLatencyMs - 5, "Combination Formulas", 45),
                PerformanceDataPoint("Fri", "21 Oct 2026", currentAnalytics.friAcc.toFloat(), currentAnalytics.addLatencyMs, "Rapid 3-Row Anzan", 50),
                PerformanceDataPoint("Sat", "22 Oct 2026", currentAnalytics.satAcc.toFloat(), currentAnalytics.addLatencyMs - 12, "Speed League Drills", 60),
                PerformanceDataPoint("Sun", "23 Oct 2026 (Today)", currentAnalytics.sunAcc.toFloat(), currentAnalytics.addLatencyMs - 8, "CIDCO Qualifier Prep", 40)
            )
            TimeRangeFilter.DAYS_14 -> listOf(
                PerformanceDataPoint("D-14", "10 Oct", 88f, 260, "+5 Friends", 25),
                PerformanceDataPoint("D-12", "12 Oct", 90f, 255, "+10 Friends", 30),
                PerformanceDataPoint("D-10", "14 Oct", 89f, 250, "Direct 2-Digit", 35),
                PerformanceDataPoint("D-8", "16 Oct", 91f, 240, "Combination", 40),
                PerformanceDataPoint("D-6", "18 Oct", currentAnalytics.tueAcc.toFloat(), currentAnalytics.addLatencyMs + 10, "Speed League", 45),
                PerformanceDataPoint("D-4", "20 Oct", currentAnalytics.thuAcc.toFloat(), currentAnalytics.addLatencyMs - 5, "Flash Arcade", 50),
                PerformanceDataPoint("D-2", "22 Oct", currentAnalytics.satAcc.toFloat(), currentAnalytics.addLatencyMs - 12, "PvP Duels", 55),
                PerformanceDataPoint("Today", "23 Oct", currentAnalytics.sunAcc.toFloat(), currentAnalytics.addLatencyMs - 8, "National Arena", 60)
            )
            TimeRangeFilter.MONTH_30 -> listOf(
                PerformanceDataPoint("W1", "1-7 Oct", 86f, 280, "Base Soroban Rods", 150),
                PerformanceDataPoint("W2", "8-14 Oct", 90f, 255, "Big Friend Transitions", 210),
                PerformanceDataPoint("W3", "15-21 Oct", 94f, 230, "Combo Formulations", 260),
                PerformanceDataPoint("W4", "22-28 Oct", 98f, 212, "Grandmaster Speed Drills", 320)
            )
        }
    }

    var selectedPointIndex by remember { mutableIntStateOf(dataPoints.size - 1) }
    val activePoint = dataPoints.getOrNull(selectedPointIndex) ?: dataPoints.last()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        // Top App Bar
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .background(theme.headerGradient)
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PERFORMANCE ANALYTICS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$studentName • $className • Live Recharts Engine",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF00E676)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Room DB", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // KPI Summary Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricKpiCard(
                        title = "Avg Latency",
                        value = "${activePoint.latencyMs} ms",
                        subtitle = "Diamond Benchmark",
                        valueColor = Color(0xFF2E7D32),
                        icon = Icons.Default.Timer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricKpiCard(
                        title = "Accuracy",
                        value = "${activePoint.accuracy.toInt()}%",
                        subtitle = "+4.2% this week",
                        valueColor = theme.primary,
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                    MetricKpiCard(
                        title = "Sums Tested",
                        value = "${activePoint.questionsCount}",
                        subtitle = "100% Completed",
                        valueColor = Color(0xFFE65100),
                        icon = Icons.Default.Checklist,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Interactive Recharts-Style Accuracy Area Chart
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                    modifier = Modifier.fillMaxWidth().testTag("interactive_recharts_accuracy_chart")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Accuracy Over Time (Area Chart)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E24)
                                )
                                Text(
                                    text = "Drag or tap on points for interactive tooltip",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }

                            // Time Filter Pills
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF2F2F5)
                            ) {
                                Row(modifier = Modifier.padding(2.dp)) {
                                    TimeRangeFilter.entries.forEach { filter ->
                                        val isSel = timeFilter == filter
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSel) theme.primary else Color.Transparent,
                                            modifier = Modifier
                                                .clickable { timeFilter = filter }
                                                .testTag("filter_${filter.name.lowercase()}")
                                        ) {
                                            Text(
                                                text = filter.label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) Color.White else Color(0xFF555566),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Interactive Scrubber Tooltip Banner (Like Recharts Tooltip)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E1E28),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${activePoint.fullDate} (${activePoint.dayLabel})",
                                        color = Color(0xFF00E5FF),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Formula: ${activePoint.formulasTested}",
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Accuracy", color = Color(0xFFB0BEC5), fontSize = 9.sp)
                                        Text("${activePoint.accuracy.toInt()}%", color = Color(0xFF00E676), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Speed", color = Color(0xFFB0BEC5), fontSize = 9.sp)
                                        Text("${activePoint.latencyMs} ms", color = Color(0xFFFFD54F), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Recharts Spline Area Canvas with Scrubber
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .pointerInput(dataPoints) {
                                    detectTapGestures { offset ->
                                        val spacing = size.width / (dataPoints.size - 1)
                                        val idx = (offset.x / spacing).toInt().coerceIn(0, dataPoints.size - 1)
                                        selectedPointIndex = idx
                                    }
                                }
                                .pointerInput(dataPoints) {
                                    detectDragGestures { change, _ ->
                                        val spacing = size.width / (dataPoints.size - 1)
                                        val idx = (change.position.x / spacing).toInt().coerceIn(0, dataPoints.size - 1)
                                        selectedPointIndex = idx
                                    }
                                }
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val spacing = w / (dataPoints.size - 1)

                                // Draw horizontal grid lines (80%, 90%, 100%)
                                val gridY80 = h * 0.9f
                                val gridY90 = h * 0.5f
                                val gridY100 = h * 0.1f

                                drawLine(Color(0xFFEAEAEA), Offset(0f, gridY80), Offset(w, gridY80), strokeWidth = 1.dp.toPx())
                                drawLine(Color(0xFFEAEAEA), Offset(0f, gridY90), Offset(w, gridY90), strokeWidth = 1.dp.toPx())
                                drawLine(Color(0xFFEAEAEA), Offset(0f, gridY100), Offset(w, gridY100), strokeWidth = 1.dp.toPx())

                                val path = Path()
                                val fillPath = Path()

                                fillPath.moveTo(0f, h)

                                dataPoints.forEachIndexed { i, pt ->
                                    val x = i * spacing
                                    val normalized = ((pt.accuracy - 80f) / 20f).coerceIn(0.05f, 0.95f)
                                    val y = h - normalized * (h - 24.dp.toPx())

                                    if (i == 0) {
                                        path.moveTo(x, y)
                                        fillPath.lineTo(x, y)
                                    } else {
                                        val prevX = (i - 1) * spacing
                                        val prevNorm = ((dataPoints[i - 1].accuracy - 80f) / 20f).coerceIn(0.05f, 0.95f)
                                        val prevY = h - prevNorm * (h - 24.dp.toPx())

                                        val cx1 = prevX + (x - prevX) / 2
                                        val cy1 = prevY
                                        val cx2 = prevX + (x - prevX) / 2
                                        val cy2 = y

                                        path.cubicTo(cx1, cy1, cx2, cy2, x, y)
                                        fillPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
                                    }
                                }

                                fillPath.lineTo(w, h)
                                fillPath.close()

                                // Area Gradient Fill (like Recharts LinearGradient)
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(theme.primary.copy(alpha = 0.35f), Color.Transparent)
                                    )
                                )

                                // Stroke Line
                                drawPath(
                                    path = path,
                                    color = theme.primary,
                                    style = Stroke(width = 3.dp.toPx())
                                )

                                // Active vertical scrubber line
                                val selectedX = selectedPointIndex * spacing
                                drawLine(
                                    color = Color(0xFF1E1E28).copy(alpha = 0.6f),
                                    start = Offset(selectedX, 0f),
                                    end = Offset(selectedX, h),
                                    strokeWidth = 1.5.dp.toPx()
                                )

                                // Draw circles on points
                                dataPoints.forEachIndexed { i, pt ->
                                    val x = i * spacing
                                    val normalized = ((pt.accuracy - 80f) / 20f).coerceIn(0.05f, 0.95f)
                                    val y = h - normalized * (h - 24.dp.toPx())

                                    val isCurrent = i == selectedPointIndex
                                    drawCircle(
                                        color = if (isCurrent) Color(0xFF00E5FF) else theme.primary,
                                        radius = if (isCurrent) 6.dp.toPx() else 4.dp.toPx(),
                                        center = Offset(x, y)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = if (isCurrent) 3.dp.toPx() else 2.dp.toPx(),
                                        center = Offset(x, y)
                                    )
                                }
                            }
                        }

                        // X-Axis Day Labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            dataPoints.forEachIndexed { idx, pt ->
                                val isSelected = idx == selectedPointIndex
                                Text(
                                    text = pt.dayLabel,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    color = if (isSelected) theme.primary else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Operation Latency Grouped Bar Chart (like Recharts BarChart)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                    modifier = Modifier.fillMaxWidth().testTag("recharts_grouped_barchart")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Calculation Latency by Operation",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E24)
                                )
                                Text("Student vs Class Benchmark vs National Speed Target", fontSize = 10.sp, color = Color.Gray)
                            }
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = theme.primary)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val barData = listOf(
                            Triple("Addition (+)", currentAnalytics.addLatencyMs, 200),
                            Triple("Subtraction (-)", currentAnalytics.subLatencyMs, 220),
                            Triple("Multiplication (×)", currentAnalytics.mulLatencyMs, 300),
                            Triple("Division (÷)", currentAnalytics.divLatencyMs, 350)
                        )

                        barData.forEach { (op, actualMs, targetMs) ->
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(op, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF222233))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Actual: $actualMs ms", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = theme.primary)
                                        Text("Target: $targetMs ms", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFEEEEF4))
                                ) {
                                    // Target marker shadow
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth((targetMs / 500f).coerceIn(0.1f, 1f))
                                            .fillMaxHeight()
                                            .background(Color(0xFFB0BEC5).copy(alpha = 0.4f))
                                    )
                                    // Actual latency bar
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth((actualMs / 500f).coerceIn(0.1f, 1f))
                                            .fillMaxHeight()
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(theme.primary, theme.accent)
                                                )
                                            )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(theme.primary))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Student Speed", fontSize = 10.sp, color = Color(0xFF555566))
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFB0BEC5)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("National Target Benchmark", fontSize = 10.sp, color = Color(0xFF555566))
                        }
                    }
                }
            }

            // Formula Accuracy Breakdown Table
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Abacus Formula Mastery Breakdown",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E24)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        listOf(
                            Triple("Direct Bead Addition/Subtraction", "99% Mastery", Color(0xFF2E7D32)),
                            Triple("Little Friends (+5 / -5 combinations)", "96% Mastery", Color(0xFF1565C0)),
                            Triple("Big Friends (+10 / -10 combinations)", "95% Mastery", Color(0xFFFFA000)),
                            Triple("Family / Combination (+6 to +9)", "92% Mastery", Color(0xFF6A1B9A))
                        ).forEach { (formula, stat, col) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF9F9FC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEF)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(formula, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF222233))
                                    Surface(shape = RoundedCornerShape(6.dp), color = col.copy(alpha = 0.12f)) {
                                        Text(stat, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = col, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MetricKpiCard(
    title: String,
    value: String,
    subtitle: String,
    valueColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Icon(icon, contentDescription = null, tint = valueColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
            Text(subtitle, fontSize = 9.sp, color = Color(0xFF757588), maxLines = 1)
        }
    }
}
