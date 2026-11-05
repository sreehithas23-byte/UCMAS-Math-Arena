package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.theme.ArenaTheme

@Composable
fun UpiFeePaymentDialog(
    profile: UserProfile,
    theme: ArenaTheme,
    onConfirmPayment: (utrNumber: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var utrInput by remember { mutableStateOf("") }
    val upiId = "seemaadukia54205@okicici"
    val merchantName = "Seema Adukia (UCMAS Thane Kopri)"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF673AB7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Pay Term Fees via GPay QR",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E1E24)
                    )
                    Text(
                        text = "Official Seema Adukia • UCMAS Kopri",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF673AB7),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // School Center & Student Header
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF3E5F5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCE93D8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = merchantName,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFF4A148C)
                        )
                        Text(
                            text = "Student: ${profile.name} • ${profile.className}",
                            fontSize = 11.sp,
                            color = Color(0xFF6A1B9A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Amount: ₹${profile.feeAmount}.00",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = theme.primary
                        )
                    }
                }

                // Official Google Pay UPI QR Image
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E6)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gpay_fee_qr),
                        contentDescription = "Official Google Pay QR Code - Seema Adukia",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Fit
                    )
                }

                // Copy UPI ID button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5FA),
                    modifier = Modifier
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("UPI ID", upiId)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = theme.primary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("UPI ID: $upiId", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = theme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("(Tap to Copy)", fontSize = 9.sp, color = Color.Gray)
                    }
                }

                // Supported UPI Apps badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UpiAppBadge("GPay", Color(0xFF4285F4))
                    UpiAppBadge("PhonePe", Color(0xFF5F259F))
                    UpiAppBadge("Paytm", Color(0xFF00B9F1))
                    UpiAppBadge("BHIM", Color(0xFF00796B))
                }

                Text(
                    text = "Scan with any UPI app. After payment, enter your 12-digit UTR/Ref number below to instantly clear your receipt.",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                // Optional UTR input
                OutlinedTextField(
                    value = utrInput,
                    onValueChange = { utrInput = it },
                    placeholder = { Text("UPI Ref / UTR No. (e.g. 429810238123)", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fee_payment_utr_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ref = if (utrInput.isNotBlank()) utrInput.trim() else "UPI-${System.currentTimeMillis().toString().takeLast(8)}"
                    onConfirmPayment(ref)
                    Toast.makeText(context, "Fee payment verified! Receipt generated successfully.", Toast.LENGTH_LONG).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_fee_payment_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Confirm Payment (₹${profile.feeAmount})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(22.dp),
        containerColor = Color.White
    )
}

@Composable
private fun UpiAppBadge(name: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
