package com.example.ui.student

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.ArenaTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Difficulty and math category configuration for Raid Bosses.
 */
enum class RaidMathType {
    DOUBLE_DIGIT_ADD_SUB,
    TRIPLE_DIGIT_COMBINED,
    VEDIC_MULTIPLICATION,
    GRANDMASTER_HYBRID
}

data class RaidBossTier(
    val id: String,
    val name: String,
    val title: String,
    val levelReq: Int,
    val maxHp: Int,
    val attackIntervalSec: Int,
    val bossAttackDamage: Int,
    val tokenReward: Int,
    val xpReward: Int,
    val badgeTitle: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val accentColor: Color,
    val difficulty: String,
    val description: String,
    val mathType: RaidMathType
)

data class RaidMathProblem(
    val expression: String,
    val answer: Int,
    val formulaHint: String
)

/**
 * Helper to generate tiered math problems matching UCMAS training levels.
 */
object RaidProblemGenerator {
    fun generateProblem(mathType: RaidMathType): RaidMathProblem {
        return when (mathType) {
            RaidMathType.DOUBLE_DIGIT_ADD_SUB -> {
                val isAdd = Random.nextBoolean()
                if (isAdd) {
                    val a = Random.nextInt(15, 65)
                    val b = Random.nextInt(12, 35)
                    val hint = if ((a % 10) + (b % 10) >= 10) "Carry over: Big Friend +${b % 10} = -${10 - (b % 10)} + 10" else "Direct Soroban Addition"
                    RaidMathProblem("$a + $b", a + b, hint)
                } else {
                    val a = Random.nextInt(35, 95)
                    val b = Random.nextInt(12, a - 5)
                    val hint = if ((a % 10) < (b % 10)) "Borrow: -${b % 10} = -10 + ${10 - (b % 10)}" else "Direct Lower Beads Subtraction"
                    RaidMathProblem("$a - $b", a - b, hint)
                }
            }
            RaidMathType.TRIPLE_DIGIT_COMBINED -> {
                val op = Random.nextInt(3)
                when (op) {
                    0 -> {
                        val a = Random.nextInt(120, 480)
                        val b = Random.nextInt(110, 390)
                        RaidMathProblem("$a + $b", a + b, "3-Digit Flash: Align hundred column first")
                    }
                    1 -> {
                        val a = Random.nextInt(350, 890)
                        val b = Random.nextInt(110, a - 50)
                        RaidMathProblem("$a - $b", a - b, "3-Digit Sub: Complement of tenths column")
                    }
                    else -> {
                        val a = Random.nextInt(40, 150)
                        val b = Random.nextInt(25, 90)
                        val c = Random.nextInt(15, 45)
                        RaidMathProblem("$a + $b - $c", a + b - c, "3-Step Sequence: Mental Flash Anzan")
                    }
                }
            }
            RaidMathType.VEDIC_MULTIPLICATION -> {
                val isTwoByTwo = Random.nextBoolean()
                if (isTwoByTwo) {
                    val a = Random.nextInt(12, 35)
                    val b = Random.nextInt(11, 19)
                    RaidMathProblem("$a × $b", a * b, "Vedic Formula: Vertically & Crosswise ($a × $b)")
                } else {
                    val a = Random.nextInt(24, 88)
                    val b = Random.nextInt(3, 9)
                    RaidMathProblem("$a × $b", a * b, "Multiplication Flash: ($a × $b)")
                }
            }
            RaidMathType.GRANDMASTER_HYBRID -> {
                val choice = Random.nextInt(3)
                when (choice) {
                    0 -> {
                        val a = Random.nextInt(125, 650)
                        val b = Random.nextInt(110, 340)
                        RaidMathProblem("$a + $b", a + b, "Grandmaster Flash Anzan 3-Digit Sum")
                    }
                    1 -> {
                        val a = Random.nextInt(35, 95)
                        val b = Random.nextInt(6, 12)
                        RaidMathProblem("$a × $b", a * b, "Lightning Speed Product ($a × $b)")
                    }
                    else -> {
                        val a = Random.nextInt(15, 30)
                        val b = Random.nextInt(4, 8)
                        val c = Random.nextInt(20, 60)
                        RaidMathProblem("($a × $b) + $c", (a * b) + c, "Hybrid Sequence: Product then Add")
                    }
                }
            }
        }
    }
}

/**
 * Complete BossRaidsScreen component featuring Boss Selection Lobby,
 * Multi-Level Combat Arena, Boss Attack Timers, Combo Multipliers,
 * and Digital Reward Payouts.
 */
@Composable
fun BossRaidsScreen(
    theme: ArenaTheme,
    userProfile: UserProfile,
    onAddReward: (title: String, tokens: Int, xp: Int, category: String, addTicket: Boolean) -> Unit,
    onBack: () -> Unit,
    onBeadClick: () -> Unit,
    onCorrectChime: () -> Unit,
    onErrorBuzz: () -> Unit,
    onCoinSound: () -> Unit,
    onTriggerVictoryParticles: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bossCatalog = remember {
        listOf(
            RaidBossTier(
                id = "boss_golem",
                name = "Soroban Golem",
                title = "Stone Guardian of Double Digits",
                levelReq = 1,
                maxHp = 900,
                attackIntervalSec = 9,
                bossAttackDamage = 20,
                tokenReward = 150,
                xpReward = 300,
                badgeTitle = "Golem Smasher",
                icon = Icons.Default.Shield,
                primaryColor = Color(0xFF5D4037),
                accentColor = Color(0xFFFFB300),
                difficulty = "NOVICE (LVL 1-3)",
                description = "Master of 2-digit addition and subtraction. A great opening test for budding mental arithmetic champions.",
                mathType = RaidMathType.DOUBLE_DIGIT_ADD_SUB
            ),
            RaidBossTier(
                id = "boss_wyvern",
                name = "Flash Anzan Wyvern",
                title = "Aerial Predator of 3-Digit Sums",
                levelReq = 4,
                maxHp = 1800,
                attackIntervalSec = 8,
                bossAttackDamage = 25,
                tokenReward = 300,
                xpReward = 600,
                badgeTitle = "Wyvern Tamer",
                icon = Icons.Default.FlashOn,
                primaryColor = Color(0xFF1565C0),
                accentColor = Color(0xFF00E5FF),
                difficulty = "INTERMEDIATE (LVL 4-7)",
                description = "Fast flying beats of triple digits! Requires mental visualization of abacus beads under pressure.",
                mathType = RaidMathType.TRIPLE_DIGIT_COMBINED
            ),
            RaidBossTier(
                id = "boss_hydra",
                name = "Vedic Multiplication Hydra",
                title = "Multi-Headed Beast of Fast Products",
                levelReq = 8,
                maxHp = 2600,
                attackIntervalSec = 7,
                bossAttackDamage = 30,
                tokenReward = 450,
                xpReward = 900,
                badgeTitle = "Hydra Slayer",
                icon = Icons.Default.Bolt,
                primaryColor = Color(0xFF6A1B9A),
                accentColor = Color(0xFFE040FB),
                difficulty = "ADVANCED (LVL 8-11)",
                description = "Strikes with multi-digit products. Test your mental multiplication skills to sever each mathematical head.",
                mathType = RaidMathType.VEDIC_MULTIPLICATION
            ),
            RaidBossTier(
                id = "boss_overlord",
                name = "Grandmaster Abacus Lord",
                title = "Supreme Soroban Emperor",
                levelReq = 12,
                maxHp = 3800,
                attackIntervalSec = 6,
                bossAttackDamage = 35,
                tokenReward = 600,
                xpReward = 1200,
                badgeTitle = "Grandmaster Champion",
                icon = Icons.Default.WorkspacePremium,
                primaryColor = Color(0xFFB71C1C),
                accentColor = Color(0xFFFFD700),
                difficulty = "GRANDMASTER (LVL 12+)",
                description = "The ultimate challenge of the UCMAS Arena. Lightning timers, multi-step chains, and massive digital rewards!",
                mathType = RaidMathType.GRANDMASTER_HYBRID
            )
        )
    }

    var selectedBossIndex by remember { mutableStateOf<Int?>(null) }
    var raidsCompletedCount by remember { mutableIntStateOf(0) }

    // Intercept Back button: return to boss lobby if battle active, else back to hub
    BackHandler(enabled = true) {
        if (selectedBossIndex != null) {
            selectedBossIndex = null
        } else {
            onBack()
        }
    }

    val activeBoss = selectedBossIndex?.let { bossCatalog[it] }

    if (activeBoss == null) {
        // Boss Selection Lobby
        BossRaidLobby(
            theme = theme,
            bosses = bossCatalog,
            userProfile = userProfile,
            raidsCompleted = raidsCompletedCount,
            onSelectBoss = { idx ->
                onBeadClick()
                selectedBossIndex = idx
            },
            onBack = onBack,
            modifier = modifier
        )
    } else {
        // Active Boss Raid Battle
        BossRaidBattleView(
            theme = theme,
            boss = activeBoss,
            userProfile = userProfile,
            onBattleVictory = { tokens, xp, badge ->
                raidsCompletedCount++
                onAddReward("Boss Raid Victory: ${activeBoss.name}", tokens, xp, "Boss Raid", false)
                onCoinSound()
                onTriggerVictoryParticles()
            },
            onExitBattle = {
                selectedBossIndex = null
            },
            onBeadClick = onBeadClick,
            onCorrectChime = onCorrectChime,
            onErrorBuzz = onErrorBuzz,
            modifier = modifier
        )
    }
}

/**
 * Lobby screen displaying all Raid Bosses, level recommendations, rewards, and student boss tickets.
 */
@Composable
private fun BossRaidLobby(
    theme: ArenaTheme,
    bosses: List<RaidBossTier>,
    userProfile: UserProfile,
    raidsCompleted: Int,
    onSelectBoss: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FC))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Navigation Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("raid_lobby_back_btn")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Return to Hub", tint = Color(0xFF222233))
                }

                Text(
                    text = "BOSS RAIDS ARENA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.primary,
                    letterSpacing = 1.sp
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF8E1),
                    border = BorderStroke(1.dp, Color(0xFFFFD54F))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${userProfile.bossTickets} Tickets",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }
        }

        // Hero Banner: Boss Raids Explanation & Training Progress
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2C)),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("raid_hero_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF1E1E2C),
                                    Color(0xFF2A1B3D),
                                    Color(0xFF381530)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFF5252).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFFFF5252))
                            ) {
                                Text(
                                    text = "PvE MULTI-LEVEL COMBAT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFF8A80),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lvl ${userProfile.level}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Face Legendary Math Bosses",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Solve rapid-fire mental arithmetic to strike through multi-phase boss shields. Earn huge Token bounties, Training XP, and exclusive Boss Slayer badges!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCFD8DC),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RaidStatBadge(
                                label = "Victories",
                                value = "$raidsCompleted Won",
                                icon = Icons.Default.EmojiEvents,
                                color = Color(0xFFFFD700)
                            )
                            RaidStatBadge(
                                label = "Tokens Pool",
                                value = "Up to 600",
                                icon = Icons.Default.MonetizationOn,
                                color = Color(0xFF69F0AE)
                            )
                            RaidStatBadge(
                                label = "Streak Boost",
                                value = "${userProfile.streakDays}d Active",
                                icon = Icons.Default.LocalFireDepartment,
                                color = Color(0xFFFFAB40)
                            )
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "AVAILABLE RAID BOSSES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF555566),
                letterSpacing = 1.sp
            )
        }

        // List of Boss Cards
        itemsIndexed(bosses) { index, boss ->
            BossSelectionCard(
                boss = boss,
                isRecommended = userProfile.level >= boss.levelReq,
                userLevel = userProfile.level,
                testTag = "boss_card_$index",
                onBattleClick = { onSelectBoss(index) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RaidStatBadge(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(label, fontSize = 9.sp, color = Color(0xFFB0BEC5))
                Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

/**
 * Individual Boss Card in the lobby.
 */
@Composable
private fun BossSelectionCard(
    boss: RaidBossTier,
    isRecommended: Boolean,
    userLevel: Int,
    testTag: String,
    onBattleClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, if (isRecommended) boss.accentColor.copy(alpha = 0.4f) else Color(0xFFEEEEEE)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Boss Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(boss.primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(boss.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = boss.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E24)
                        )
                    }
                    Text(
                        text = boss.title,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = Color(0xFF757588)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = boss.primaryColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = boss.difficulty,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = boss.primaryColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = boss.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF424242),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Boss Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${boss.maxHp} HP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555566))

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${boss.attackIntervalSec}s Timer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF555566))
                }

                // Reward badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFFF8E1),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F))
                    ) {
                        Text(
                            text = "+${boss.tokenReward} Tokens",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, Color(0xFFA5D6A7))
                    ) {
                        Text(
                            text = "+${boss.xpReward} XP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Challenge Button
            Button(
                onClick = onBattleClick,
                colors = ButtonDefaults.buttonColors(containerColor = boss.primaryColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("start_raid_${boss.id}")
            ) {
                Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ENGAGE RAID BATTLE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * Active Combat Arena view where student solves math problems against the Raid Boss.
 */
@Composable
private fun BossRaidBattleView(
    theme: ArenaTheme,
    boss: RaidBossTier,
    userProfile: UserProfile,
    onBattleVictory: (tokens: Int, xp: Int, badge: String) -> Unit,
    onExitBattle: () -> Unit,
    onBeadClick: () -> Unit,
    onCorrectChime: () -> Unit,
    onErrorBuzz: () -> Unit,
    modifier: Modifier = Modifier
) {
    var bossCurrentHp by remember { mutableIntStateOf(boss.maxHp) }
    var playerHp by remember { mutableIntStateOf(100) }
    var playerShieldActive by remember { mutableStateOf(false) }
    var comboStreak by remember { mutableIntStateOf(0) }
    var attackTimer by remember { mutableIntStateOf(boss.attackIntervalSec) }

    var battleWon by remember { mutableStateOf(false) }
    var battleLost by remember { mutableStateOf(false) }

    var currentProblem by remember {
        mutableStateOf(RaidProblemGenerator.generateProblem(boss.mathType))
    }
    var inputAnswer by remember { mutableStateOf("") }
    var floatingCombatText by remember { mutableStateOf<String?>(null) }
    var specialAbilityCharge by remember { mutableIntStateOf(0) } // Max 3 for Special Move

    // Boss Attack Countdown Loop
    LaunchedEffect(bossCurrentHp, playerHp, battleWon, battleLost) {
        if (battleWon || battleLost) return@LaunchedEffect
        attackTimer = boss.attackIntervalSec

        while (attackTimer > 0 && !battleWon && !battleLost) {
            delay(1000)
            attackTimer--
        }

        if (attackTimer == 0 && !battleWon && !battleLost) {
            // Boss attacks!
            if (playerShieldActive) {
                playerShieldActive = false
                floatingCombatText = "SHIELD ABSORBED ATTACK!"
                onErrorBuzz()
            } else {
                playerHp = (playerHp - boss.bossAttackDamage).coerceAtLeast(0)
                onErrorBuzz()
                comboStreak = 0
                floatingCombatText = "BOSS STRIKE! -${boss.bossAttackDamage} HP"
                if (playerHp <= 0) {
                    battleLost = true
                }
            }
            attackTimer = boss.attackIntervalSec
        }
    }

    // Clear floating text after brief delay
    LaunchedEffect(floatingCombatText) {
        if (floatingCombatText != null) {
            delay(1500)
            floatingCombatText = null
        }
    }

    // Combat Attack Logic
    fun handleStrike() {
        val userVal = inputAnswer.toIntOrNull()
        if (userVal == currentProblem.answer) {
            onCorrectChime()
            comboStreak++
            specialAbilityCharge = (specialAbilityCharge + 1).coerceAtMost(3)

            val isCrit = comboStreak >= 3
            val critMultiplier = if (isCrit) 2.2f else 1.0f
            val baseDamage = 320
            val totalDamage = (baseDamage * critMultiplier).toInt()

            bossCurrentHp = (bossCurrentHp - totalDamage).coerceAtLeast(0)
            floatingCombatText = if (isCrit) "CRITICAL HIT! -$totalDamage HP (x$comboStreak)" else "HIT! -$totalDamage HP"

            if (bossCurrentHp <= 0) {
                battleWon = true
                onBattleVictory(boss.tokenReward, boss.xpReward, boss.badgeTitle)
            } else {
                currentProblem = RaidProblemGenerator.generateProblem(boss.mathType)
                inputAnswer = ""
            }
        } else {
            onErrorBuzz()
            comboStreak = 0
            playerHp = (playerHp - 12).coerceAtLeast(0)
            floatingCombatText = "WRONG ANSWER! -12 HP"
            if (playerHp <= 0) {
                battleLost = true
            } else {
                inputAnswer = ""
            }
        }
    }

    fun handleKeyClick(key: String) {
        onBeadClick()
        when (key) {
            "STRIKE" -> handleStrike()
            "C" -> if (inputAnswer.isNotEmpty()) inputAnswer = inputAnswer.dropLast(1)
            else -> if (inputAnswer.length < 5) inputAnswer += key
        }
    }

    fun activateShieldPowerUp() {
        if (specialAbilityCharge >= 3 && !playerShieldActive) {
            playerShieldActive = true
            specialAbilityCharge = 0
            floatingCombatText = "ABACUS BARRIER ACTIVATED!"
            onBeadClick()
        }
    }

    // Determine Boss Combat Phase (1, 2, or 3)
    val hpPercent = (bossCurrentHp.toFloat() / boss.maxHp).coerceIn(0f, 1f)
    val bossPhase = when {
        hpPercent > 0.66f -> "PHASE 1: OUTER SHIELD"
        hpPercent > 0.33f -> "PHASE 2: FRENZY CORE"
        else -> "PHASE 3: ENRAGED BURST"
    }

    val animatedBossHp by animateFloatAsState(targetValue = hpPercent, label = "bossHpAnim")
    val animatedPlayerHp by animateFloatAsState(targetValue = playerHp / 100f, label = "playerHpAnim")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F1A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onExitBattle,
                    modifier = Modifier.testTag("exit_battle_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Exit Raid", tint = Color.White)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = boss.primaryColor.copy(alpha = 0.3f),
                    border = BorderStroke(1.dp, boss.primaryColor)
                ) {
                    Text(
                        text = bossPhase,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Attack in ${attackTimer}s",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (attackTimer <= 2) Color(0xFFFF5252) else Color(0xFFFFD54F),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Boss Encounter Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
                elevation = CardDefaults.cardElevation(6.dp),
                border = BorderStroke(1.dp, boss.primaryColor.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_boss_card")
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
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(boss.primaryColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(boss.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = boss.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = boss.title,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = Color(0xFFB0BEC5)
                            )
                        }

                        // Floating Damage indicator
                        if (floatingCombatText != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFF1744)
                            ) {
                                Text(
                                    text = floatingCombatText!!,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Boss HP Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "BOSS HEALTH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8A80)
                        )
                        Text(
                            text = "$bossCurrentHp / ${boss.maxHp} HP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFF263238))
                            .testTag("boss_hp_bar")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedBossHp)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFFFF1744),
                                            Color(0xFFFF5252),
                                            Color(0xFFFF8A80)
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }

        // Student Player HP, Shield & Combo Multiplier
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Player HP
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1E2E),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Student HP: $playerHp%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF263238))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedPlayerHp)
                                    .fillMaxHeight()
                                    .background(Color(0xFF4CAF50))
                            )
                        }
                    }
                }

                // Combo Streak Multiplier
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1E2E),
                    border = BorderStroke(
                        1.dp,
                        if (comboStreak >= 3) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (comboStreak >= 3) Color(0xFFFFD700) else Color(0xFF9E9E9E),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (comboStreak >= 3) "COMBO x$comboStreak (CRIT!)" else "Combo: x$comboStreak",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (comboStreak >= 3) Color(0xFFFFD700) else Color(0xFFCFD8DC)
                            )
                        }
                    }
                }
            }
        }

        // Special Power-Up Button (Abacus Barrier)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (playerShieldActive) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFF1E1E2E),
                    border = BorderStroke(1.dp, if (playerShieldActive) Color(0xFF00E676) else Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .clickable(enabled = specialAbilityCharge >= 3 && !playerShieldActive) {
                            activateShieldPowerUp()
                        }
                        .testTag("powerup_barrier_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = if (playerShieldActive) Color(0xFF00E676) else if (specialAbilityCharge >= 3) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                playerShieldActive -> "Barrier Active (Blocks 1 Hit)"
                                specialAbilityCharge >= 3 -> "Abacus Barrier READY! (Tap)"
                                else -> "Barrier: $specialAbilityCharge/3 Charged"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (playerShieldActive) Color(0xFF00E676) else if (specialAbilityCharge >= 3) Color(0xFFFFD700) else Color.Gray
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.05f)
                ) {
                    Text(
                        text = "Formula: ${currentProblem.formulaHint}",
                        fontSize = 9.sp,
                        color = Color(0xFFB0BEC5),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Victory State
        if (battleWon) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("raid_victory_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF8E1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "BOSS CONQUERED!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2E7D32)
                        )

                        Text(
                            text = "You defeated ${boss.name} with brilliant mental arithmetic!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF555566),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Digital Rewards Breakdown
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF9F9FC),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E6)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "DIGITAL REWARDS EARNED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF757588)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tokens Credited", fontSize = 12.sp, color = Color(0xFF424242))
                                    Text("+${boss.tokenReward} Tokens", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("UCMAS Training XP", fontSize = 12.sp, color = Color(0xFF424242))
                                    Text("+${boss.xpReward} XP", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Badge Unlocked", fontSize = 12.sp, color = Color(0xFF424242))
                                    Text(boss.badgeTitle, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onExitBattle,
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("claim_rewards_btn")
                        ) {
                            Text("CLAIM REWARDS & RETURN", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else if (battleLost) {
            // Defeat State
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("raid_defeat_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RAID INCOMPLETE",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The ${boss.name} overpowered your defense. Review your formulas and try again!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                playerHp = 100
                                bossCurrentHp = boss.maxHp
                                comboStreak = 0
                                battleLost = false
                                inputAnswer = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("retry_raid_btn")
                        ) {
                            Text("RETRY RAID BATTLE", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = onExitBattle,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Return to Boss Lobby")
                        }
                    }
                }
            }
        } else {
            // Active Math Question Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("raid_math_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RAPID MENTAL ARITHMETIC",
                            style = MaterialTheme.typography.labelSmall,
                            color = theme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${currentProblem.expression} = ?",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF1E1E24)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Answer Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF4F5F9),
                            border = BorderStroke(1.dp, theme.primary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(44.dp)
                                .testTag("raid_answer_box")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = inputAnswer.ifEmpty { "Strike Input" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (inputAnswer.isEmpty()) Color.Gray else Color(0xFF1E1E24)
                                )
                            }
                        }
                    }
                }
            }

            // Numeric Keypad
            item {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "STRIKE")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    keys.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                    border = BorderStroke(
                                        1.dp,
                                        if (isStrike) Color(0xFFB71C1C) else Color(0xFFE0E0E6)
                                    ),
                                    modifier = Modifier
                                        .weight(if (isStrike) 1.2f else 1f)
                                        .height(48.dp)
                                        .clickable { handleKeyClick(k) }
                                        .testTag("raid_key_$k")
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
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
    }
}
