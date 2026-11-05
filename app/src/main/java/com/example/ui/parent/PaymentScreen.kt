package com.example.ui.parent

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

enum class FeeTermPeriod(val termTitle: String, val periodLabel: String, val fee: Int, val isCurrent: Boolean) {
    TERM_4("Term 4 (Current)", "Oct 2026 – Dec 2026", 2400, true),
    TERM_1_UPCOMING("Term 1 (Upcoming)", "Jan 2027 – Mar 2027", 2400, false),
    TERM_3_PAST("Term 3 (Archived)", "Jul 2026 – Sep 2026", 2400, false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    profile: UserProfile,
    theme: ArenaTheme,
    onConfirmPayment: (utrNumber: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler(onBack = onBack)

    var selectedTerm by remember { mutableStateOf(FeeTermPeriod.TERM_4) }
    var utrInput by remember { mutableStateOf("") }
    var isUtrError by remember { mutableStateOf(false) }
    var showReceiptModal by remember { mutableStateOf(false) }
    var showBankDetails by remember { mutableStateOf(false) }

    val upiId = "seemaadukia54205@okicici"
    val merchantName = "Seema Adukia • UCMAS Thane Kopri"
    val isPaid = profile.feeStatus == "Paid"

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("payment_screen"),
        containerColor = Color(0xFFF7F7FA),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Term Fee Payment",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1E1E24)
                        )
                        Text(
                            text = "UCMAS Mental Arithmetic • Kopri East Center",
                            fontSize = 11.sp,
                            color = Color(0xFF6B6B7A)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("payment_screen_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Parent Dashboard",
                            tint = Color(0xFF1E1E24)
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPaid) Color(0xFF81C784) else Color(0xFFEF9A9A)
                        ),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.PendingActions,
                                contentDescription = null,
                                tint = if (isPaid) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPaid) "PAID" else "DUE",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                color = if (isPaid) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Banner
            item {
                if (isPaid) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
                        modifier = Modifier.fillMaxWidth().testTag("payment_status_paid_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2E7D32)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Term Fees Cleared & Settled",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "Receipt #UC-8924 • Cleared via UPI Instant Settlement",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            OutlinedButton(
                                onClick = { showReceiptModal = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B5E20)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("banner_view_receipt_btn")
                            ) {
                                Text("Receipt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D)),
                        modifier = Modifier.fillMaxWidth().testTag("payment_status_due_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE65100)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Term Fee Due: ₹${profile.feeAmount}.00",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFFBF360C)
                                )
                                Text(
                                    text = "Due Date: 31st October 2026 • Please scan UPI QR below",
                                    fontSize = 11.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }
            }

            // Student & Invoice Metadata Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5EB)),
                    modifier = Modifier.fillMaxWidth().testTag("student_invoice_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = profile.name,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1E1E24)
                                )
                                Text(
                                    text = "Student UID: ${profile.uid} • ${profile.className}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B6B7A)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = theme.primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "Level ${profile.level}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = theme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEF2))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Billing Cycle", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text("Oct 2026 – Dec 2026", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Invoice Number", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text("#INV-UCMAS-8924", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Training Center", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text("Kopri East, Thane (W)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF333344))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Center Code", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text("MH-THA-042", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF333344))
                            }
                        }
                    }
                }
            }

            // Term Selector Tabs
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select Billing Period:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B6B7A)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FeeTermPeriod.values().forEach { term ->
                            val isSelected = selectedTerm == term
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) theme.primary else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) theme.primary else Color(0xFFDCDCE5)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTerm = term }
                                    .testTag("fee_term_tab_${term.name.lowercase()}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = term.termTitle,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF222233),
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹${term.fee}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) Color.White else theme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Itemized Fee Breakdown Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5EB)),
                    modifier = Modifier.fillMaxWidth().testTag("fee_breakdown_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = theme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Itemized Fee Breakdown",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1E24)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        FeeLineItem(
                            title = "Mental Arithmetic Tuition (3 Months)",
                            description = "12 weekly 2-hour classroom master sessions",
                            amount = "₹1,800.00"
                        )
                        FeeLineItem(
                            title = "UCMAS Speed Drill Workbooks & Flash Cards",
                            description = "Official Level ${profile.level} Big & Small Friend sheets",
                            amount = "₹350.00"
                        )
                        FeeLineItem(
                            title = "National Abacus League & Examination Fee",
                            description = "Official hall ticket, grading & promotion certificate",
                            amount = "₹250.00"
                        )

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEF2))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Term Amount", fontSize = 12.sp, color = Color.Gray)
                            Text("₹2,400.00", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333344))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Center Early Bird / Academy Grant", fontSize = 12.sp, color = Color(0xFF2E7D32))
                            Text("- ₹0.00", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2E7D32))
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEF2))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Net Payable Amount:", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF1E1E24))
                            Text("₹${selectedTerm.fee}.00", fontWeight = FontWeight.Black, fontSize = 18.sp, color = theme.primary)
                        }
                    }
                }
            }

            // QR Code Scanner Section
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth().testTag("payment_qr_code_card")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF673AB7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Pay UPI QR",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E24)
                                )
                                Text(
                                    text = "Beneficiary: Seema Adukia (UCMAS Thane Kopri)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF673AB7)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Official Google Pay QR Image
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E2E8)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("placeholder_qr_canvas")
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.gpay_fee_qr),
                                contentDescription = "Official Google Pay QR Code - Seema Adukia",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 380.dp)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Copy UPI ID Action
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFFF4F4F8),
                            modifier = Modifier
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("UCMAS UPI ID", upiId)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("copy_upi_id_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = theme.primary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("UPI ID: $upiId", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = theme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("(Tap to Copy)", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Supported UPI App Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UpiAppBadge("Google Pay", Color(0xFF4285F4))
                            UpiAppBadge("PhonePe", Color(0xFF5F259F))
                            UpiAppBadge("Paytm", Color(0xFF00B9F1))
                            UpiAppBadge("BHIM", Color(0xFF00796B))
                            UpiAppBadge("Cred", Color(0xFF212121))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Scan with any UPI app to pay ₹${selectedTerm.fee}.00 instantly.",
                            fontSize = 11.sp,
                            color = Color(0xFF6B6B7A),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Direct Bank Transfer Accordion
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5EB)),
                    modifier = Modifier.fillMaxWidth().testTag("bank_transfer_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showBankDetails = !showBankDetails }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Prefer Direct Bank Transfer (NEFT/IMPS)?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                                    Text("ICICI Bank Current Account details", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            Icon(
                                imageVector = if (showBankDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (showBankDetails) "Collapse" else "Expand",
                                tint = Color.Gray
                            )
                        }

                        AnimatedVisibility(visible = showBankDetails) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .background(Color(0xFFF8F9FA), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BankDetailRow("Beneficiary Name", "UCMAS Mental Arithmetic Thane Kopri", context)
                                BankDetailRow("Bank Name", "ICICI Bank Ltd", context)
                                BankDetailRow("Account Number", "010205009823", context, isCopyable = true)
                                BankDetailRow("IFSC Code", "ICIC0000102", context, isCopyable = true)
                                BankDetailRow("Branch", "Kopri East, Thane (W) - 400603", context)
                            }
                        }
                    }
                }
            }

            // Payment Verification & Reference Submission Section
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5EB)),
                    modifier = Modifier.fillMaxWidth().testTag("payment_verification_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Confirm & Clear Payment",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1E24)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "After transferring funds in your UPI app, enter your 12-digit UTR reference number or click instant confirmation below:",
                            fontSize = 11.sp,
                            color = Color(0xFF555566)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = utrInput,
                            onValueChange = {
                                utrInput = it
                                isUtrError = false
                            },
                            label = { Text("12-Digit UPI Ref / UTR No.") },
                            placeholder = { Text("e.g. 429810238123 or TxnRef") },
                            singleLine = true,
                            isError = isUtrError,
                            supportingText = if (isUtrError) {
                                { Text("Please enter a valid reference number or use demo confirmation.", color = Color.Red) }
                            } else null,
                            leadingIcon = {
                                Icon(Icons.Default.Pin, contentDescription = null, tint = Color.Gray)
                            },
                            trailingIcon = {
                                if (utrInput.isNotBlank()) {
                                    IconButton(onClick = { utrInput = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("payment_utr_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val ref = if (utrInput.isNotBlank()) utrInput.trim() else "UPI-${System.currentTimeMillis().toString().takeLast(8)}"
                                onConfirmPayment(ref)
                                Toast.makeText(context, "Payment verified! Receipt #UC-8924 generated.", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("verify_payment_button")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit UTR & Generate Receipt",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                val demoRef = "UPI-${(10000000..99999999).random()}"
                                onConfirmPayment(demoRef)
                                Toast.makeText(context, "Demo Payment Approved! Receipt #UC-8924 created.", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("instant_demo_pay_button")
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = theme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Instant One-Tap Demo Payment (₹${selectedTerm.fee})",
                                color = theme.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Steps Guide
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F9)),
                    modifier = Modifier.fillMaxWidth().testTag("payment_steps_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "How Payment Verification Works:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        PaymentStepRow("1", "Open any UPI app (GPay, PhonePe, Paytm, BHIM)")
                        PaymentStepRow("2", "Scan the QR above or enter UPI ID $upiId")
                        PaymentStepRow("3", "Enter amount ₹${selectedTerm.fee} and complete transaction")
                        PaymentStepRow("4", "Paste UTR/Txn number above to immediately unlock official cleared receipt")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Official Receipt Modal Dialog
    if (showReceiptModal) {
        AlertDialog(
            onDismissRequest = { showReceiptModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Official UCMAS Fee Receipt", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(0xFFF7F7FA),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E2E8)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("RECEIPT #UC-8924", fontWeight = FontWeight.Black, fontSize = 13.sp, color = theme.primary)
                                    Text("UCMAS Thane Kopri East", fontSize = 11.sp, color = Color.Gray)
                                }
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF2E7D32)) {
                                    Text("CLEARED", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Text("Student: ${profile.name}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("UID: ${profile.uid} • ${profile.className}", fontSize = 11.sp, color = Color.Gray)
                            Text("Term: Oct 2026 – Dec 2026 (Level ${profile.level})", fontSize = 11.sp, color = Color.Gray)
                            Text("Amount Paid: ₹2,400.00", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF1B5E20))
                            Text("Payment Mode: UPI Instant Cleared", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            Text("Status: OFFICIAL TAX RECEIPT ISSUED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "Invoice PDF downloaded to device storage.", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("download_invoice_button")
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Receipt link copied to share with parents.", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("share_receipt_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReceiptModal = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun FeeLineItem(title: String, description: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E1E24))
            Text(text = description, fontSize = 10.sp, color = Color.Gray)
        }
        Text(text = amount, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
    }
}

@Composable
private fun BankDetailRow(label: String, value: String, context: Context, isCopyable: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222233))
            if (isCopyable) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy $label",
                    tint = Color(0xFF1565C0),
                    modifier = Modifier
                        .size(14.dp)
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(label, value)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "$label copied!", Toast.LENGTH_SHORT).show()
                        }
                )
            }
        }
    }
}

@Composable
private fun PaymentStepRow(step: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF2C3E50),
            modifier = Modifier.size(18.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(step, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 10.sp, color = Color(0xFF333344))
    }
}

@Composable
private fun UpiAppBadge(name: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
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
