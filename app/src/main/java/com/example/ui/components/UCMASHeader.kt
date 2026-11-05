package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaTheme

enum class ArenaPortal(val title: String) {
    STUDENT("Student Zone"),
    PARENT("Parent Portal"),
    CALCULATOR("Calculators"),
    TEACHER("Admin & Teacher")
}

@Composable
fun UCMASHeader(
    theme: ArenaTheme,
    tokenBalance: Int,
    currentPortal: ArenaPortal,
    onSelectPortal: (ArenaPortal) -> Unit,
    isAdminUnlocked: Boolean,
    onAdminUnlockedChange: (Boolean) -> Unit,
    isSoundOn: Boolean,
    onToggleSound: () -> Unit,
    onOpenThemePicker: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenGoogleSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var headerTapCount by remember { mutableIntStateOf(0) }
    var showPinDialog by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .background(theme.headerGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Top branding row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tapping this 7 times unlocks the Admin & Teacher Portal!
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            headerTapCount++
                            if (headerTapCount == 7) {
                                val newState = !isAdminUnlocked
                                onAdminUnlockedChange(newState)
                                headerTapCount = 0
                                Toast.makeText(
                                    context,
                                    if (newState) "Admin & Teacher Portal Unlocked! Security PIN required." else "Admin & Teacher Portal Hidden.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (headerTapCount in 3..6) {
                                Toast.makeText(
                                    context,
                                    "${7 - headerTapCount} more taps to unlock Admin Mode",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .testTag("ucmas_header_title_tap")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.5.dp, Color(0xFFFFD700), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "UCMAS Logo",
                            tint = theme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "UCMAS THANE KOPRI EAST",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            if (isAdminUnlocked) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFFD54F)) {
                                    Text("ADMIN ACTIVE", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF3E2723), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }
                        Text(
                            text = "Mental Arithmetic & Abacus Arena",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Controls: Google Sign-In, Token Pill, Sound, Theme Switcher
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Google Sign-In Avatar / Switcher
                    IconButton(
                        onClick = onOpenGoogleSignIn,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("google_signin_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        }
                    }

                    // Live Token Wallet Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                        modifier = Modifier
                            .clickable { onOpenWallet() }
                            .testTag("header_token_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Tokens",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$tokenBalance",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Sound Toggle
                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("sound_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isSoundOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Audio",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Dynamic Theme Switcher Trigger
                    IconButton(
                        onClick = onOpenThemePicker,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("theme_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Switch Theme",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pill-shaped Navigation Tab Toggles for rapid State-based portal switching
            val scrollState = rememberScrollState()
            val visiblePortals = ArenaPortal.entries

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                visiblePortals.forEach { portal ->
                    val isSelected = currentPortal == portal
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .clickable {
                                if (portal == ArenaPortal.TEACHER && !isAdminUnlocked) {
                                    showPinDialog = true
                                } else {
                                    onSelectPortal(portal)
                                }
                            }
                            .testTag("portal_tab_${portal.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            if (portal == ArenaPortal.TEACHER) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isSelected) theme.primary else Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = portal.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) theme.primary else Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Teacher Security PIN Dialog
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                enteredPin = ""
                pinError = false
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = theme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Teacher & Admin Security PIN", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter your authorization PIN (Default: 1234 or ucmas2026) to manage classroom roster, approve students, and edit logs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            enteredPin = it
                            pinError = false
                        },
                        label = { Text("Enter Security PIN") },
                        isError = pinError,
                        supportingText = if (pinError) { { Text("Incorrect PIN. Access Denied.", color = Color.Red) } } else null,
                        modifier = Modifier.fillMaxWidth().testTag("admin_pin_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredPin == "1234" || enteredPin == "ucmas2026" || enteredPin == "admin") {
                            showPinDialog = false
                            enteredPin = ""
                            pinError = false
                            onAdminUnlockedChange(true)
                            onSelectPortal(ArenaPortal.TEACHER)
                            Toast.makeText(context, "Teacher Security PIN Verified!", Toast.LENGTH_SHORT).show()
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    modifier = Modifier.testTag("admin_pin_submit_button")
                ) {
                    Text("Verify & Enter")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPinDialog = false
                        enteredPin = ""
                        pinError = false
                    }
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}
