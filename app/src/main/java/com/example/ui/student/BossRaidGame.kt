package com.example.ui.student

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

data class RaidBoss(
    val stage: Int,
    val name: String,
    val maxHp: Int,
    val subtitle: String,
    val attackIntervalSec: Int
)

@Composable
fun BossRaidGame(
    theme: ArenaTheme,
    bossTickets: Int,
    onRaidWin: (tokens: Int, xp: Int) -> Unit,
    onBack: () -> Unit,
    onBeadClick: () -> Unit,
    onCorrectChime: () -> Unit,
    onErrorBuzz: () -> Unit
) {
    val bosses = listOf(
        RaidBoss(1, "The Digit Conqueror", 1200, "Master of Double-Digit Arithmetic", 8),
        RaidBoss(2, "Abacus Titan", 2500, "Ancient Beast of Triple Combinations", 7),
        RaidBoss(3, "Chronos Speed Demon", 4000, "Supreme Ruler of Rapid Milliseconds", 5)
    )

    var currentBossIndex by remember { mutableIntStateOf(0) }
    val boss = bosses[currentBossIndex]
    var bossCurrentHp by remember { mutableIntStateOf(boss.maxHp) }
    var playerHp by remember { mutableIntStateOf(100) }
    var comboCount by remember { mutableIntStateOf(0) }
    var battleWon by remember { mutableStateOf(false) }
    var battleLost by remember { mutableStateOf(false) }

    // Math Question
    var n1 by remember { mutableIntStateOf(Random.nextInt(15, 75)) }
    var n2 by remember { mutableIntStateOf(Random.nextInt(12, 60)) }
    var op by remember { mutableStateOf("+") }
    val correctResult = if (op == "+") n1 + n2 else n1 - n2

    var inputAnswer by remember { mutableStateOf("") }
    var attackTimer by remember { mutableIntStateOf(boss.attackIntervalSec) }

    // Boss Attack Countdown
    LaunchedEffect(currentBossIndex, bossCurrentHp, battleWon, battleLost) {
        if (battleWon || battleLost) return@LaunchedEffect
        attackTimer = boss.attackIntervalSec
        while (attackTimer > 0 && !battleWon && !battleLost) {
            delay(1000)
            attackTimer--
        }
        if (attackTimer == 0 && !battleWon && !battleLost) {
            // Boss attacks player!
            playerHp = (playerHp - 25).coerceAtLeast(0)
            onErrorBuzz()
            comboCount = 0
            if (playerHp == 0) {
                battleLost = true
            } else {
                attackTimer = boss.attackIntervalSec
            }
        }
    }

    fun attackBoss() {
        val userVal = inputAnswer.toIntOrNull()
        if (userVal == correctResult) {
            onCorrectChime()
            comboCount++
            val critMultiplier = if (comboCount >= 3) 2.2f else 1.0f
            val baseDamage = 350
            val totalDamage = (baseDamage * critMultiplier).toInt()

            bossCurrentHp = (bossCurrentHp - totalDamage).coerceAtLeast(0)
            if (bossCurrentHp == 0) {
                if (currentBossIndex < bosses.size - 1) {
                    currentBossIndex++
                    bossCurrentHp = bosses[currentBossIndex].maxHp
                    playerHp = 100
                    comboCount = 0
                } else {
                    battleWon = true
                    onRaidWin(500, 1000)
                }
            }
        } else {
            onErrorBuzz()
            comboCount = 0
            playerHp = (playerHp - 15).coerceAtLeast(0)
            if (playerHp == 0) {
                battleLost = true
            }
        }

        // New Problem
        inputAnswer = ""
        n1 = Random.nextInt(25, 99)
        n2 = Random.nextInt(10, n1)
        op = if (Random.nextBoolean()) "+" else "-"
    }

    fun append(d: String) {
        onBeadClick()
        if (inputAnswer.length < 4) inputAnswer += d
    }

    fun clear() {
        onBeadClick()
        if (inputAnswer.isNotEmpty()) inputAnswer = inputAnswer.dropLast(1)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "STAGE ${boss.stage}/3: RAID BATTLE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = theme.primary
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF8E1),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$bossTickets Tickets", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Boss Card with HP Bar
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC62828)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(boss.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text(boss.subtitle, style = MaterialTheme.typography.labelSmall, color = Color(0xFFB0BEC5), fontSize = 10.sp)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFD32F2F)
                    ) {
                        Text(
                            text = "Attack in: ${attackTimer}s",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Boss HP Bar
                val bossHpRatio = (bossCurrentHp.toFloat() / boss.maxHp).coerceIn(0f, 1f)
                val animatedBossHp by animateFloatAsState(targetValue = bossHpRatio, label = "bossHp")

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("BOSS HP", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFF8A80), fontWeight = FontWeight.Bold)
                        Text("$bossCurrentHp / ${boss.maxHp}", style = MaterialTheme.typography.labelSmall, color = Color.White, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF37474F))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedBossHp)
                                .fillMaxHeight()
                                .background(Brush.horizontalGradient(listOf(Color(0xFFFF1744), Color(0xFFFF5252))))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Player HP & Combo Multiplier
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                modifier = Modifier.weight(1f)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Player HP: $playerHp%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2E7D32))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (comboCount >= 3) Color(0xFFFFF8E1) else Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (comboCount >= 3) Color(0xFFFFD54F) else Color(0xFFE0E0E6)),
                modifier = Modifier.weight(1f)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (comboCount >= 3) "COMBO x${comboCount} (CRIT!)" else "Combo: x$comboCount",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (comboCount >= 3) Color(0xFFE65100) else Color(0xFF555566)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (battleWon) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(60.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("BOSS CONQUERED!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                    Text("Classroom Co-Op Raid Completed!", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF555566))
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF8E1),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
                    ) {
                        Text(
                            text = "+500 Tokens  •  +1,000 XP Awarded",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5D4037),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = theme.primary), modifier = Modifier.fillMaxWidth()) {
                        Text("Return to Arena")
                    }
                }
            }
        } else if (battleLost) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("RAID FAILED", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                    Text("The boss overpowered you. Rest up and try again!", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Try Again")
                    }
                }
            }
        } else {
            // Rapid Attack Question
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SOLVE RAPIDLY TO STRIKE BOSS", style = MaterialTheme.typography.labelSmall, color = theme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$n1 $op $n2 = ?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF1E1E24)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF4F4F8),
                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = inputAnswer.ifEmpty { "Strike!" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (inputAnswer.isEmpty()) Color.Gray else Color(0xFF1E1E24)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Attack Keypad
            val raidKeys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "STRIKE")
            )

            raidKeys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { k ->
                        val isStrike = k == "STRIKE"
                        val isClear = k == "C"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                isStrike -> Color(0xFFD32F2F)
                                isClear -> Color(0xFFFFEBEE)
                                else -> Color.White
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4E4E8)),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clickable {
                                    when (k) {
                                        "STRIKE" -> attackBoss()
                                        "C" -> clear()
                                        else -> append(k)
                                    }
                                }
                                .testTag("raid_key_$k")
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = k,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        isStrike -> Color.White
                                        isClear -> Color(0xFFC62828)
                                        else -> Color(0xFF222233)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
