package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.ArenaTheme

@Composable
fun EditStudentCardDialog(
    profile: UserProfile,
    theme: ArenaTheme,
    onSaveProfile: (UserProfile) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var className by remember { mutableStateOf(profile.className) }
    var level by remember { mutableStateOf(profile.level.toString()) }
    var uid by remember { mutableStateOf(profile.uid) }
    var tokens by remember { mutableStateOf(profile.tokens.toString()) }
    var feeAmount by remember { mutableStateOf(profile.feeAmount.toString()) }
    var feeStatus by remember { mutableStateOf(profile.feeStatus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Student Card Details", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Class") },
                        modifier = Modifier.weight(1f).testTag("edit_class_input")
                    )
                    OutlinedTextField(
                        value = level,
                        onValueChange = { level = it },
                        label = { Text("Level") },
                        modifier = Modifier.weight(1f).testTag("edit_level_input")
                    )
                }
                OutlinedTextField(
                    value = uid,
                    onValueChange = { uid = it },
                    label = { Text("Student UID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF8E1),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Token Balance (Teacher Control):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                            Text("Current: $tokens", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFFA000))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = tokens,
                            onValueChange = { tokens = it },
                            label = { Text("Exact Token Amount") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_tokens_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(50, 100, 250, 500).forEach { delta ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            val cur = tokens.toIntOrNull() ?: 0
                                            tokens = (cur + delta).toString()
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
                                        val cur = tokens.toIntOrNull() ?: 0
                                        tokens = maxOf(0, cur - 100).toString()
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
                    }
                }

                OutlinedTextField(
                    value = feeAmount,
                    onValueChange = { feeAmount = it },
                    label = { Text("Monthly Fee (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = profile.copy(
                        name = name.trim(),
                        className = className.trim(),
                        level = level.toIntOrNull() ?: profile.level,
                        uid = uid.trim(),
                        tokens = tokens.toIntOrNull() ?: profile.tokens,
                        feeAmount = feeAmount.toIntOrNull() ?: profile.feeAmount,
                        feeStatus = feeStatus
                    )
                    onSaveProfile(updated)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
