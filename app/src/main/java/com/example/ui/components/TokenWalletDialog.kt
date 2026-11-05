package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TokenTransaction
import com.example.data.model.UserProfile
import com.example.ui.theme.ArenaTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TokenWalletDialog(
    profile: UserProfile,
    transactions: List<TokenTransaction>,
    theme: ArenaTheme,
    onDailyCheckIn: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        tint = Color(0xFFFFA000)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UCMAS Token Ledger",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Balance Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF8E1),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "AVAILABLE BALANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8D6E63),
                            letterSpacing = 1.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFFA000),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${profile.tokens}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF5D4037)
                            )
                        }
                        Text(
                            text = "${profile.xp} Total XP • Level ${profile.level}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF795548)
                        )
                    }
                }

                // Daily Check-in CTA
                Button(
                    onClick = onDailyCheckIn,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wallet_daily_checkin_button")
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Daily Check-In (+50 Tokens & +50 XP)")
                }

                Text(
                    text = "Recent Transactions Ledger",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333344)
                )

                // Transactions List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (transactions.isEmpty()) {
                        item {
                            Text(
                                text = "No transactions yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    } else {
                        items(transactions) { tx ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF9F9FB),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECEF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tx.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF222233)
                                        )
                                        Text(
                                            text = "${tx.category} • ${dateFormat.format(Date(tx.timestamp))}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = Color(0xFF888899)
                                        )
                                    }

                                    Text(
                                        text = (if (tx.isCredit) "+" else "-") + "${tx.amount}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (tx.isCredit) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    )
                                }
                            }
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
