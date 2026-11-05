package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaTheme

data class QuickMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector? = null,
    val textBadge: String? = null,
    val isPrimary: Boolean = false
)

/**
 * Quick Menu Modal Bottom Sheet matching Screenshot 3:
 * - 4 columns of circular action buttons
 * - Top row of 4 solid red circular icons: Home, Notification, Students, Settings
 * - Grid of rounded secondary items: Userapplication, ICard Details, Fee Payment, Class Work, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickMenuBottomSheet(
    theme: ArenaTheme,
    onDismiss: () -> Unit,
    onNavigateHome: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenStudents: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenICard: () -> Unit,
    onOpenFees: () -> Unit,
    onOpenAttendance: () -> Unit,
    onOpenTimeTable: () -> Unit,
    onOpenClassWork: () -> Unit,
    onOpenHomework: () -> Unit,
    onOpenReportCard: () -> Unit,
    onOpenMentalMathTrainer: (() -> Unit)? = null,
    onActionSelected: (String) -> Unit
) {
    val primaryItems = listOf(
        QuickMenuItem("home", "Home", Icons.Default.Home, isPrimary = true),
        QuickMenuItem("notification", "Notification", Icons.Default.Notifications, isPrimary = true),
        QuickMenuItem("students", "Students", Icons.Default.Groups, isPrimary = true),
        QuickMenuItem("settings", "Settings", Icons.Default.Settings, isPrimary = true)
    )

    val secondaryItems = listOf(
        QuickMenuItem("userapplication", "Userapplication", Icons.Default.Assignment),
        QuickMenuItem("icard", "ICard Details", Icons.Default.FormatListBulleted),
        QuickMenuItem("datachange", "StudentDataChangeRequest", textBadge = "S"),
        QuickMenuItem("fees", "Activity Fees", Icons.Default.Payments),
        QuickMenuItem("report_card", "Report Card", Icons.Default.ReceiptLong),
        QuickMenuItem("class_work", "Class Work", Icons.Default.MenuBook),
        QuickMenuItem("homework", "Home Work", Icons.Default.HomeWork),
        QuickMenuItem("activity_center", "Activity Center", textBadge = "AC"),
        QuickMenuItem("my_visit", "My Visit", textBadge = "MV"),
        QuickMenuItem("screening", "Screening Report", textBadge = "SR"),
        QuickMenuItem("leave", "Leave", Icons.Default.CalendarToday),
        QuickMenuItem("edit_profile", "Edit Student Profile", Icons.Default.Edit),
        QuickMenuItem("profile", "Student Profile", Icons.Default.Person),
        QuickMenuItem("attendance", "Attendance", Icons.Default.FactCheck),
        QuickMenuItem("timetable", "Time Table", Icons.Default.AccessTime)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFFFF7F7),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF888888))
            )
        },
        modifier = Modifier.testTag("quick_menu_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Row 1: Primary 4 solid red buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                primaryItems.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(76.dp)
                            .clickable {
                                when (item.id) {
                                    "home" -> { onNavigateHome(); onDismiss() }
                                    "notification" -> { onOpenNotifications(); onDismiss() }
                                    "students" -> { onOpenStudents(); onDismiss() }
                                    "settings" -> { onOpenSettings(); onDismiss() }
                                }
                            }
                            .testTag("quick_menu_${item.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon ?: Icons.Default.Check,
                                contentDescription = item.title,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF222233),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            HorizontalDivider(
                color = Color(0xFFEEEEEE),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Remaining Secondary Items in 3 or 4 columns
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .padding(bottom = 24.dp)
            ) {
                items(secondaryItems) { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (item.id) {
                                    "icard" -> onOpenICard()
                                    "fees" -> onOpenFees()
                                    "attendance" -> onOpenAttendance()
                                    "timetable" -> onOpenTimeTable()
                                    "class_work" -> onOpenClassWork()
                                    "homework" -> onOpenHomework()
                                    "report_card" -> onOpenReportCard()
                                    "activity_center" -> onOpenMentalMathTrainer?.invoke() ?: onActionSelected(item.title)
                                    else -> onActionSelected(item.title)
                                }
                                onDismiss()
                            }
                            .testTag("quick_menu_${item.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.5.dp, Color(0xFFFFCDD2), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.icon != null) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else if (item.textBadge != null) {
                                Text(
                                    text = item.textBadge,
                                    color = Color(0xFFE53935),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333344),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }
    }
}
