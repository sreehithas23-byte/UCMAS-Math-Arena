package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserProfile
import com.example.ui.theme.ArenaTheme

/**
 * Settings Screen matching Screenshot 4:
 * - Red header bar with back navigation "< Settings"
 * - Profile avatar, name, and "Student Profile" subtitle
 * - Class & UID pill chip
 * - Section headers marked with a vertical red bar: "| Account", "| Preferences", "| Activities"
 * - White rounded cards with red circular icon badges and chevron arrows
 */
@Composable
fun SchoolSettingsDialog(
    profile: UserProfile,
    theme: ArenaTheme,
    onDismiss: () -> Unit,
    onOpenThemePicker: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenGoogleSignIn: () -> Unit,
    onSwitchPortal: (ArenaPortal) -> Unit,
    isSoundEnabled: Boolean,
    onToggleSound: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("school_settings_dialog"),
            color = Color(0xFFFFF7F7) // Soft blush pink background matching screenshots
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Red Top Header Bar
                Surface(
                    color = theme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("settings_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Avatar Section
                    Box(
                        modifier = Modifier.size(88.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(82.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF2F2))
                                .border(3.dp, theme.primary.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            RobloxAvatarFigure(
                                shirtName = profile.equippedShirt,
                                pantsName = profile.equippedPants,
                                accessoryName = profile.equippedAccessory,
                                scale = 0.65f
                            )
                        }

                        // Red eye / badge icon on bottom right
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(theme.primary)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "View Profile",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color(0xFF1E1E24)
                    )

                    Text(
                        text = "Student Profile",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = theme.primary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Class & UID Pill Chip
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E0E0)),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color(0xFF555566),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Class", fontSize = 10.sp, color = Color.Gray)
                                    Text(
                                        profile.className.ifEmpty { "GRADE 8 - A" },
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = Color(0xFF222233)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .height(28.dp)
                                    .width(1.dp)
                                    .background(Color(0xFFEEEEEE))
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = Color(0xFF555566),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("UID", fontSize = 10.sp, color = Color.Gray)
                                    Text(
                                        profile.uid,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = Color(0xFF222233)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Section: | Account
                    SettingsSectionHeader(title = "Account", theme = theme)
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E5E5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            SettingsItemRow(
                                icon = Icons.Default.People,
                                title = "Manage Students",
                                theme = theme,
                                onClick = onOpenGoogleSignIn
                            )
                            Divider(color = Color(0xFFF7F0F0))
                            SettingsItemRow(
                                icon = Icons.Default.Translate,
                                title = "Change Language",
                                subtitle = "EN",
                                theme = theme,
                                onClick = {}
                            )
                            Divider(color = Color(0xFFF7F0F0))
                            SettingsItemRow(
                                icon = Icons.Default.LockReset,
                                title = "Change Password",
                                theme = theme,
                                onClick = {}
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section: | Preferences
                    SettingsSectionHeader(title = "Preferences", theme = theme)
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E5E5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            SettingsItemRow(
                                icon = Icons.Default.Palette,
                                title = "Theme",
                                subtitle = theme.displayName,
                                theme = theme,
                                onClick = onOpenThemePicker
                            )
                            Divider(color = Color(0xFFF7F0F0))
                            SettingsItemRow(
                                icon = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                title = "Sound & Haptic Feedback",
                                subtitle = if (isSoundEnabled) "Enabled" else "Muted",
                                theme = theme,
                                onClick = onToggleSound
                            )
                            Divider(color = Color(0xFFF7F0F0))
                            SettingsItemRow(
                                icon = Icons.Default.AccountBalanceWallet,
                                title = "Token Wallet",
                                subtitle = "${profile.tokens} Tokens",
                                theme = theme,
                                onClick = onOpenWallet
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section: | Portal Access
                    SettingsSectionHeader(title = "Portal Switcher", theme = theme)
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E5E5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            SettingsItemRow(
                                icon = Icons.Default.FamilyRestroom,
                                title = "Parent Oversight Portal",
                                theme = theme,
                                onClick = {
                                    onSwitchPortal(ArenaPortal.PARENT)
                                    onDismiss()
                                }
                            )
                            Divider(color = Color(0xFFF7F0F0))
                            SettingsItemRow(
                                icon = Icons.Default.AdminPanelSettings,
                                title = "Faculty & Teacher Portal",
                                theme = theme,
                                onClick = {
                                    onSwitchPortal(ArenaPortal.TEACHER)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Footer branding
                    Text(
                        text = "v2.0.16 © Copyright 2026 Powered By MICM Net Solutions Pvt. Ltd.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, theme: ArenaTheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(theme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222233),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    theme: ArenaTheme,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF2F2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = theme.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF222233),
            modifier = Modifier.weight(1f)
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFC0C0C8),
            modifier = Modifier.size(20.dp)
        )
    }
}
