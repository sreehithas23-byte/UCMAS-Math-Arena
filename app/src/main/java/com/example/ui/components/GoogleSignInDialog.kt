package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PendingStudent
import com.example.ui.theme.ArenaTheme

@Composable
fun GoogleSignInDialog(
    theme: ArenaTheme,
    currentStudentName: String,
    currentStudentEmail: String,
    pendingStudents: List<PendingStudent>,
    onSelectApprovedStudent: (name: String, email: String) -> Unit,
    onRegisterNewStudent: (name: String, email: String, className: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var showCustomInput by remember { mutableStateOf(false) }
    var inputName by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var inputClass by remember { mutableStateOf("Class VI-A") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4285F4)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("G", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text("Google Account Sign-In", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Sign in to UCMAS Thane Kopri East Math Arena with your verified student Google Account.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                // Currently active account
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F8E9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF2E7D32)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(currentStudentName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(currentStudentEmail, fontSize = 11.sp, color = Color.Gray)
                        }
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF2E7D32)) {
                            Text("ACTIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }

                Text("Available Profiles / Pending Approvals:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.DarkGray)

                // Sreehith Approved profile
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E6)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectApprovedStudent("SREEHITH A S", "sreehithas23@gmail.com")
                            Toast.makeText(context, "Switched to SREEHITH A S (Approved)", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("SA", fontWeight = FontWeight.Bold, color = theme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SREEHITH A S (Class VI-A)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("sreehithas23@gmail.com", fontSize = 10.sp, color = Color.Gray)
                        }
                        Text("Approved", fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }

                // Pending Students list (e.g. Aran Jain)
                pendingStudents.forEach { pend ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (pend.status == "APPROVED") Color(0xFFF1F8E9) else Color(0xFFFFF8E1),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (pend.status == "APPROVED") Color(0xFFA5D6A7) else Color(0xFFFFD54F)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (pend.status == "APPROVED") {
                                    onSelectApprovedStudent(pend.name, pend.email)
                                    Toast.makeText(context, "Welcome back, ${pend.name}!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                } else {
                                    Toast.makeText(context, "${pend.name}'s account is PENDING teacher approval!", Toast.LENGTH_LONG).show()
                                }
                            }
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (pend.status == "APPROVED") Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = if (pend.status == "APPROVED") Color(0xFF2E7D32) else Color(0xFFFFA000),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${pend.name} (${pend.requestedClass})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text(pend.email, fontSize = 10.sp, color = Color.Gray)
                            }
                            Surface(shape = RoundedCornerShape(4.dp), color = if (pend.status == "APPROVED") Color(0xFF2E7D32) else Color(0xFFFFA000)) {
                                Text(
                                    text = pend.status,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                if (!showCustomInput) {
                    OutlinedButton(
                        onClick = { showCustomInput = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("sign_in_other_google_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign In with New Google Account", fontSize = 11.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            label = { Text("Student Name (e.g. Aran Jain)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputEmail,
                            onValueChange = { inputEmail = it },
                            label = { Text("Google Email (e.g. aran.jain@gmail.com)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (inputName.isNotBlank() && inputEmail.isNotBlank()) {
                                    onRegisterNewStudent(inputName.trim(), inputEmail.trim(), inputClass)
                                    showCustomInput = false
                                    Toast.makeText(context, "Google Sign-In submitted for ${inputName}! Waiting for teacher approval.", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit for Teacher Approval")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White
    )
}
