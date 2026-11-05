package com.example.ui.components

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.ArenaTheme

@Composable
fun DynamicProfileCard(
    profile: UserProfile,
    theme: ArenaTheme,
    isParentView: Boolean,
    onToggleView: () -> Unit,
    onDailyCheckIn: () -> Unit,
    onPayFees: () -> Unit,
    isAdmin: Boolean = false,
    onEditCard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isParentView) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardFlip"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("dynamic_profile_card")
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 14f * density
            }
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(theme.primary.copy(alpha = 0.5f), theme.accent.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        if (rotation <= 90f) {
            // Front: Student View
            StudentCardFront(
                profile = profile,
                theme = theme,
                onToggleView = onToggleView,
                onDailyCheckIn = onDailyCheckIn,
                isAdmin = isAdmin,
                onEditCard = onEditCard
            )
        } else {
            // Back: Parent View (rotate 180 so it isn't mirrored)
            Box(
                modifier = Modifier.graphicsLayer { rotationY = 180f }
            ) {
                ParentCardBack(
                    profile = profile,
                    theme = theme,
                    onToggleView = onToggleView,
                    onPayFees = onPayFees,
                    isAdmin = isAdmin,
                    onEditCard = onEditCard
                )
            }
        }
    }
}

@Composable
private fun StudentCardFront(
    profile: UserProfile,
    theme: ArenaTheme,
    onToggleView: () -> Unit,
    onDailyCheckIn: () -> Unit,
    isAdmin: Boolean,
    onEditCard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Top row: Circular Avatar, Info (Name, Class, UID), and 2026-2027 Circular Switcher (matching Screenshot 2)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Avatar with red ring outline and eye badge
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF2F2))
                        .border(2.dp, theme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    RobloxAvatarFigure(
                        shirtName = profile.equippedShirt,
                        pantsName = profile.equippedPants,
                        accessoryName = profile.equippedAccessory,
                        scale = 0.45f
                    )
                }

                // Small red badge on bottom-right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(theme.primary)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Active Profile",
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E1E24),
                        fontSize = 16.sp
                    )
                    if (isAdmin) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = onEditCard, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Card", tint = theme.primary, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Class: ${profile.className.ifEmpty { "GRADE 8 - A" }}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF444455),
                    fontSize = 12.sp
                )
                Text(
                    text = "UID : ${profile.uid}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF777788),
                    fontSize = 11.sp
                )
            }

            // Circular 2026-2027 View Switcher Badge (matching Screenshot 2)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleView() }
                    .padding(4.dp)
                    .testTag("switch_to_parent_view_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(theme.primary)
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch to Parent View",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "2026-2027",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF222233)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Token Balance & Streak Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Token Pill (Teacher can tap to edit tokens when Admin unlocked)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF8E1),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                modifier = Modifier
                    .weight(1f)
                    .then(if (isAdmin) Modifier.clickable { onEditCard() } else Modifier)
                    .testTag("student_tokens_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Tokens",
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${profile.tokens} Tokens",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF5D4037)
                            )
                            if (isAdmin) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Teacher Edit Tokens",
                                    tint = theme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isAdmin) "Teacher: Tap to Edit" else "UCMAS Wallet",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = if (isAdmin) theme.primary else Color(0xFF8D6E63),
                            fontWeight = if (isAdmin) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // 7-Day Streak Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFEBEE),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDailyCheckIn() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "${profile.streakDays}-Day Streak",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            text = "Tap to Check In",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = Color(0xFFE53935)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // XP Level Progress Bar
        val xpInCurrentLevel = profile.xp % 300
        val xpProgress = (xpInCurrentLevel / 300f).coerceIn(0f, 1f)
        val animatedProgress by animateFloatAsState(
            targetValue = xpProgress,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "xpBar"
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level ${profile.level} (Mastery)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = theme.primary
                )
                Text(
                    text = "$xpInCurrentLevel / 300 XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF757588)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFEEEEEE))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(theme.primary, theme.accent, Color(0xFFFFD700))
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun ParentCardBack(
    profile: UserProfile,
    theme: ArenaTheme,
    onToggleView: () -> Unit,
    onPayFees: () -> Unit,
    isAdmin: Boolean,
    onEditCard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1565C0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FamilyRestroom,
                    contentDescription = "Parent Portal",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Parent Oversight: ${profile.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E24)
                    )
                    if (isAdmin) {
                        IconButton(onClick = onEditCard, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Card", tint = Color(0xFF1565C0), modifier = Modifier.size(14.dp))
                        }
                    }
                }
                Text(
                    text = "${profile.className} • UID: ${profile.uid} • Level ${profile.level}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757588)
                )
            }

            // Return to student view
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleView() }
                    .padding(4.dp)
                    .testTag("switch_to_student_view_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1565C0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch to Student View",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Student",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1565C0)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Stats: Attendance + Fees
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Attendance Summary
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8F5E9),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Attendance",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "${profile.attendancePercent}% (${profile.attendedSessions}/${profile.totalSessions})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B5E20)
                    )
                }
            }

            // Fee Status
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (profile.feeStatus == "Paid") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (profile.feeStatus == "Paid") Color(0xFFA5D6A7) else Color(0xFFFFCDD2)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Monthly Fee",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (profile.feeStatus == "Paid") Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    Text(
                        text = "₹${profile.feeAmount} (${profile.feeStatus})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (profile.feeStatus == "Paid") Color(0xFF1B5E20) else Color(0xFFB71C1C)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action button
        if (profile.feeStatus != "Paid") {
            Button(
                onClick = onPayFees,
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parent_pay_fee_card_button")
            ) {
                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pay Term Fee Online (₹${profile.feeAmount})")
            }
        } else {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "October Term Fees Cleared • Receipt #UC-8924",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
