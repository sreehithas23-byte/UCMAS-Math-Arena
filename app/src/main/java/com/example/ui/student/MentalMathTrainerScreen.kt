package com.example.ui.student

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class TrainerGameMode(val title: String, val subtitle: String, val icon: ImageVector) {
    PROGRESSIVE("Endless Climb", "Difficulty scales with your streak", Icons.Default.TrendingUp),
    TIMED_BLITZ("60s Blitz", "Speed run under timed pressure", Icons.Default.Bolt),
    FORMULA_FOCUS("Bead Formulas", "Direct, Friends of 5 & 10 rules", Icons.Default.Calculate)
}

data class GeneratedProblem(
    val terms: List<Int>,
    val operators: List<Char>,
    val correctAnswer: Int,
    val displayFormula: String,
    val formulaTip: String,
    val level: Int,
    val difficultyLabel: String,
    val baseTokens: Int,
    val baseXP: Int
)

/**
 * High-precision problem generator with progressive difficulty stages.
 */
object ProblemGenerator {
    fun generate(level: Int, mode: TrainerGameMode): GeneratedProblem {
        val effectiveLevel = level.coerceIn(1, 8)
        return when (effectiveLevel) {
            1 -> {
                // Novice: 1-digit addition & subtraction (Direct)
                val a = Random.nextInt(2, 9)
                val isAddition = Random.nextBoolean()
                val b = if (isAddition) Random.nextInt(1, 9) else Random.nextInt(1, a)
                val op = if (isAddition) '+' else '-'
                val ans = if (isAddition) a + b else a - b
                val tip = if (isAddition) "Direct upper/lower beads movement" else "Direct push-away of beads"
                GeneratedProblem(
                    terms = listOf(a, b),
                    operators = listOf(op),
                    correctAnswer = ans,
                    displayFormula = "$a $op $b",
                    formulaTip = tip,
                    level = 1,
                    difficultyLabel = "Novice • Direct",
                    baseTokens = 5,
                    baseXP = 15
                )
            }
            2 -> {
                // Apprentice: Friend of 5 formulas (+/- 1 to 4 using 5 bead)
                val friendPairs = listOf(Pair(4, 1), Pair(3, 2), Pair(2, 3), Pair(1, 4))
                val pair = friendPairs.random()
                val isAddition = Random.nextBoolean()
                val (a, b) = if (isAddition) Pair(Random.nextInt(1, 4), pair.first) else Pair(5, pair.first)
                val op = if (isAddition) '+' else '-'
                val ans = if (isAddition) a + b else a - b
                val tip = if (isAddition) "+${pair.first} = +5 - ${pair.second} (Friend of 5)" else "-${pair.first} = -5 + ${pair.second} (Friend of 5)"
                GeneratedProblem(
                    terms = listOf(a, b),
                    operators = listOf(op),
                    correctAnswer = ans,
                    displayFormula = "$a $op $b",
                    formulaTip = tip,
                    level = 2,
                    difficultyLabel = "Apprentice • Friend of 5",
                    baseTokens = 8,
                    baseXP = 25
                )
            }
            3 -> {
                // Scholar: Friend of 10 formulas (+/- with 10 carry)
                val friendPairs = listOf(Pair(9, 1), Pair(8, 2), Pair(7, 3), Pair(6, 4))
                val pair = friendPairs.random()
                val base = Random.nextInt(5, 9)
                val isAdd = Random.nextBoolean()
                val ans = if (isAdd) base + pair.first else (base + 10) - pair.first
                val formula = if (isAdd) "$base + ${pair.first}" else "${base + 10} - ${pair.first}"
                val tip = if (isAdd) "+${pair.first} = +10 - ${pair.second} (Friend of 10)" else "-${pair.first} = -10 + ${pair.second} (Friend of 10)"
                GeneratedProblem(
                    terms = if (isAdd) listOf(base, pair.first) else listOf(base + 10, pair.first),
                    operators = listOf(if (isAdd) '+' else '-'),
                    correctAnswer = ans,
                    displayFormula = formula,
                    formulaTip = tip,
                    level = 3,
                    difficultyLabel = "Scholar • Friend of 10",
                    baseTokens = 12,
                    baseXP = 35
                )
            }
            4 -> {
                // Adept: 2-Digit Addition without and with light carry
                val a = Random.nextInt(15, 65)
                val b = Random.nextInt(11, 35)
                val isAdd = Random.nextBoolean()
                val ans = if (isAdd) a + b else a - (b % a).coerceAtLeast(5)
                val op = if (isAdd) '+' else '-'
                val secondTerm = if (isAdd) b else (b % a).coerceAtLeast(5)
                val tip = "Visualize tens rod then units rod with Anzan"
                GeneratedProblem(
                    terms = listOf(a, secondTerm),
                    operators = listOf(op),
                    correctAnswer = ans,
                    displayFormula = "$a $op $secondTerm",
                    formulaTip = tip,
                    level = 4,
                    difficultyLabel = "Adept • 2-Digit Anzan",
                    baseTokens = 15,
                    baseXP = 45
                )
            }
            5 -> {
                // Tactician: 3-Term Chain Addition/Subtraction
                val a = Random.nextInt(20, 50)
                val b = Random.nextInt(10, 30)
                val c = Random.nextInt(5, 20)
                val ans = a + b - c
                GeneratedProblem(
                    terms = listOf(a, b, c),
                    operators = listOf('+', '-'),
                    correctAnswer = ans,
                    displayFormula = "$a + $b - $c",
                    formulaTip = "Sequential continuous bead manipulation on mind abacus",
                    level = 5,
                    difficultyLabel = "Tactician • 3-Row Flash",
                    baseTokens = 20,
                    baseXP = 60
                )
            }
            6 -> {
                // Warrior: Rapid Multiplication (1-digit × 2-digit)
                val a = Random.nextInt(12, 45)
                val b = Random.nextInt(3, 9)
                val ans = a * b
                GeneratedProblem(
                    terms = listOf(a, b),
                    operators = listOf('×'),
                    correctAnswer = ans,
                    displayFormula = "$a × $b",
                    formulaTip = "Multiply tens ($a × 10) then units, combine products",
                    level = 6,
                    difficultyLabel = "Warrior • Multiplication",
                    baseTokens = 25,
                    baseXP = 75
                )
            }
            7 -> {
                // Master: 3-Digit Addition & Subtraction
                val a = Random.nextInt(110, 480)
                val b = Random.nextInt(105, 390)
                val isAdd = Random.nextBoolean()
                val ans = if (isAdd) a + b else a - (b.coerceAtMost(a - 10))
                val op = if (isAdd) '+' else '-'
                val secondTerm = if (isAdd) b else b.coerceAtMost(a - 10)
                GeneratedProblem(
                    terms = listOf(a, secondTerm),
                    operators = listOf(op),
                    correctAnswer = ans,
                    displayFormula = "$a $op $secondTerm",
                    formulaTip = "Hundreds rod -> Tens rod -> Units rod progression",
                    level = 7,
                    difficultyLabel = "Master • 3-Digit Anzan",
                    baseTokens = 30,
                    baseXP = 90
                )
            }
            else -> {
                // Grandmaster: 4-Row Mixed Fast Anzan
                val a = Random.nextInt(30, 80)
                val b = Random.nextInt(20, 60)
                val c = Random.nextInt(15, 45)
                val d = Random.nextInt(10, 30)
                val ans = a + b - c + d
                GeneratedProblem(
                    terms = listOf(a, b, c, d),
                    operators = listOf('+', '-', '+'),
                    correctAnswer = ans,
                    displayFormula = "$a + $b - $c + $d",
                    formulaTip = "High-speed multi-term mental flash execution",
                    level = 8,
                    difficultyLabel = "Grandmaster • Hyper Anzan",
                    baseTokens = 40,
                    baseXP = 120
                )
            }
        }
    }
}

/**
 * Premium Mental Math Trainer with progressive difficulty, glassmorphism,
 * dynamic combo fire multipliers, smooth radial countdowns, and sound hooks.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MentalMathTrainerScreen(
    theme: ArenaTheme,
    userProfile: UserProfile,
    onBack: () -> Unit,
    onBeadClick: () -> Unit,
    onCorrectChime: () -> Unit,
    onErrorBuzz: () -> Unit,
    onCoinSound: () -> Unit,
    onTriggerVictoryParticles: () -> Unit,
    onAddReward: (title: String, tokens: Int, xp: Int, category: String, addTicket: Boolean) -> Unit,
    onRecordTrainingSession: ((sessionType: String, drillName: String, formulaCategory: String, digitCount: Int, rowCount: Int, flashSpeedMs: Int, durationSeconds: Int, attempted: Int, correct: Int, accuracy: Float, avgResponseMs: Long, tokens: Int, xp: Int, highestCombo: Int, score: Int, rating: String) -> Unit)? = null
) {
    var selectedMode by remember { mutableStateOf(TrainerGameMode.PROGRESSIVE) }
    var currentLevel by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var comboStreak by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var totalAttempted by remember { mutableIntStateOf(0) }
    var totalCorrect by remember { mutableIntStateOf(0) }
    var totalTokensWon by remember { mutableIntStateOf(0) }
    var totalXpWon by remember { mutableIntStateOf(0) }

    var currentProblem by remember { mutableStateOf(ProblemGenerator.generate(1, selectedMode)) }
    var userInput by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }
    var answerState by remember { mutableStateOf<Boolean?>(null) } // null = neutral, true = correct, false = wrong
    var isSessionComplete by remember { mutableStateOf(false) }

    // Timed blitz timer (seconds remaining)
    var blitzSecondsLeft by remember { mutableIntStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Per-problem timer for latency tracking
    var problemStartTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var totalLatencyMs by remember { mutableLongStateOf(0L) }

    val coroutineScope = rememberCoroutineScope()

    // Floating reward badge popup
    var rewardFloatingBadge by remember { mutableStateOf<String?>(null) }

    // Shake animation for wrong answers
    val shakeOffset = remember { Animatable(0f) }

    // Floating particles & combo multiplier
    val comboMultiplier = when {
        comboStreak >= 10 -> 3.0f
        comboStreak >= 5 -> 2.0f
        comboStreak >= 3 -> 1.5f
        else -> 1.0f
    }

    // Level progression XP progress bar
    val xpForNextLevel = 100
    val currentLevelXp = (totalCorrect * 25) % xpForNextLevel
    val levelProgress = (currentLevelXp.toFloat() / xpForNextLevel).coerceIn(0f, 1f)

    // Blitz 60-second countdown timer effect
    LaunchedEffect(selectedMode, isTimerRunning) {
        if (selectedMode == TrainerGameMode.TIMED_BLITZ && isTimerRunning) {
            while (blitzSecondsLeft > 0 && isTimerRunning) {
                delay(1000)
                blitzSecondsLeft--
            }
            if (blitzSecondsLeft <= 0) {
                isTimerRunning = false
                isSessionComplete = true
                onTriggerVictoryParticles()
            }
        }
    }

    fun submitAnswer() {
        val parsedInput = userInput.trim().toIntOrNull()
        if (parsedInput == null) return

        val latency = System.currentTimeMillis() - problemStartTime
        totalLatencyMs += latency
        totalAttempted++

        if (parsedInput == currentProblem.correctAnswer) {
            // Correct Answer!
            answerState = true
            totalCorrect++
            comboStreak++
            if (comboStreak > maxCombo) maxCombo = comboStreak

            val earnedTokens = (currentProblem.baseTokens * comboMultiplier).toInt()
            val earnedXP = (currentProblem.baseXP * comboMultiplier).toInt()
            score += (100 * comboMultiplier * currentProblem.level).toInt()
            totalTokensWon += earnedTokens
            totalXpWon += earnedXP

            onCorrectChime()
            onCoinSound()

            if (comboStreak == 3 || comboStreak == 5 || comboStreak == 10 || comboStreak % 5 == 0 || currentLevelXp + 25 >= xpForNextLevel) {
                onTriggerVictoryParticles()
            }

            // Progressive Level Up check
            if (selectedMode == TrainerGameMode.PROGRESSIVE && totalCorrect > 0 && totalCorrect % 4 == 0) {
                currentLevel = (currentLevel + 1).coerceAtMost(8)
            }

            onAddReward(
                "Mental Math ${currentProblem.difficultyLabel}",
                earnedTokens,
                earnedXP,
                "Mental Math Trainer",
                comboStreak >= 5
            )

            rewardFloatingBadge = "+${earnedTokens} Tokens  +${earnedXP} XP" + if (comboStreak >= 3) "  🔥 ${comboStreak}x Streak!" else ""

            coroutineScope.launch {
                delay(400)
                answerState = null
                userInput = ""
                showHint = false
                currentProblem = ProblemGenerator.generate(currentLevel, selectedMode)
                problemStartTime = System.currentTimeMillis()
                delay(800)
                rewardFloatingBadge = null
            }
        } else {
            // Wrong Answer
            answerState = false
            comboStreak = 0
            onErrorBuzz()

            coroutineScope.launch {
                // Shake effect
                shakeOffset.animateTo(
                    targetValue = 1f,
                    animationSpec = keyframes {
                        durationMillis = 350
                        0.0f at 0
                        -16.0f at 70
                        16.0f at 140
                        -10.0f at 210
                        10.0f at 280
                        0.0f at 350
                    }
                )
                delay(400)
                answerState = null
                userInput = ""
            }
        }
    }

    fun handleKeyPress(key: String) {
        onBeadClick()
        if (answerState != null) return

        if (selectedMode == TrainerGameMode.TIMED_BLITZ && !isTimerRunning) {
            isTimerRunning = true
        }

        when (key) {
            "C" -> userInput = ""
            "⌫" -> if (userInput.isNotEmpty()) userInput = userInput.dropLast(1)
            "ENTER" -> submitAnswer()
            "-" -> {
                if (userInput.isEmpty()) userInput = "-"
            }
            else -> {
                if (userInput.length < 6) {
                    userInput += key
                }
            }
        }
    }

    fun finishSession() {
        val avgLatency = if (totalAttempted > 0) totalLatencyMs / totalAttempted else 1200L
        val accuracy = if (totalAttempted > 0) (totalCorrect.toFloat() / totalAttempted) * 100f else 0f
        val rating = when {
            accuracy >= 90f && totalAttempted >= 8 -> "S"
            accuracy >= 75f -> "A"
            accuracy >= 60f -> "B"
            else -> "C"
        }

        onRecordTrainingSession?.invoke(
            "MENTAL_MATH_TRAINER",
            "Progressive Mental Math: ${selectedMode.title}",
            currentProblem.formulaTip,
            if (currentLevel >= 6) 3 else 2,
            if (currentLevel >= 5) 3 else 2,
            300,
            if (selectedMode == TrainerGameMode.TIMED_BLITZ) 60 else (totalLatencyMs / 1000).toInt().coerceAtLeast(30),
            totalAttempted,
            totalCorrect,
            accuracy,
            avgLatency,
            totalTokensWon,
            totalXpWon,
            maxCombo,
            score,
            rating
        )
        isSessionComplete = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E0A12),
                        Color(0xFF2B0E17),
                        Color(0xFF15060A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Bar with Luxury Metallic Gradient & Back Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (totalAttempted > 0 && !isSessionComplete) {
                            finishSession()
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .testTag("trainer_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Title Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MENTAL MATH TRAINER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Finish / End Button
                IconButton(
                    onClick = { finishSession() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935).copy(alpha = 0.25f))
                        .testTag("trainer_finish_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Finish",
                        tint = Color(0xFFFF8A80)
                    )
                }
            }

            // Mode Selector Pill Tabs
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrainerGameMode.values().forEach { mode ->
                        val isSelected = selectedMode == mode
                        val pillBgModifier = if (isSelected) {
                            Modifier.background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFE53935), Color(0xFFC62828))
                                )
                            )
                        } else {
                            Modifier.background(Color.Transparent)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .then(pillBgModifier)
                                .clickable {
                                    if (selectedMode != mode) {
                                        selectedMode = mode
                                        userInput = ""
                                        blitzSecondsLeft = 60
                                        isTimerRunning = false
                                        currentProblem = ProblemGenerator.generate(currentLevel, mode)
                                    }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = mode.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = mode.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Top Status Dashboard: Level + Score + Combo Multiplier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Current Level Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "STAGE $currentLevel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = currentProblem.difficultyLabel,
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { levelProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(CircleShape),
                            color = Color(0xFFFFD54F),
                            trackColor = Color.White.copy(alpha = 0.15f)
                        )
                    }
                }

                // Live Score & Tokens Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SCORE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "$score",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "+$totalTokensWon",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD54F)
                            )
                        }
                    }
                }

                // Dynamic Combo Flame Multiplier
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (comboStreak >= 3) Color(0xFFE53935).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(
                        1.dp,
                        if (comboStreak >= 3) Color(0xFFFF5252) else Color.White.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (comboStreak >= 3) "🔥 STREAK" else "COMBO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (comboStreak >= 3) Color(0xFFFF8A80) else Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "${comboStreak}x",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (comboStreak >= 3) Color(0xFFFFD54F) else Color.White
                        )
                        Text(
                            text = "${comboMultiplier}x Bonus",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (comboStreak >= 3) Color(0xFFFFD54F) else Color.Gray
                        )
                    }
                }
            }

            // Glowing Visual Progress Streak Indicator rewarding consecutive correct answers
            VisualStreakProgressIndicator(
                comboStreak = comboStreak,
                comboMultiplier = comboMultiplier
            )

            // Blitz Countdown Indicator (when in Timed Blitz mode)
            if (selectedMode == TrainerGameMode.TIMED_BLITZ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (blitzSecondsLeft <= 10) Color(0xFFD32F2F).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(
                        1.dp,
                        if (blitzSecondsLeft <= 10) Color(0xFFFF5252) else Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (blitzSecondsLeft <= 10) Color(0xFFFF5252) else Color(0xFFFFD54F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isTimerRunning) "Time Remaining" else "Starts on First Tap",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Text(
                            text = "${blitzSecondsLeft}s",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (blitzSecondsLeft <= 10) Color(0xFFFF5252) else Color(0xFFFFD54F)
                        )
                    }
                }
            }

            // Smooth animated background color for Hero Card
            val animatedHeroBg by animateColorAsState(
                targetValue = when (answerState) {
                    true -> Color(0xFF1B5E20).copy(alpha = 0.52f)
                    false -> Color(0xFFB71C1C).copy(alpha = 0.52f)
                    null -> Color(0xFF28131B).copy(alpha = 0.92f)
                },
                animationSpec = tween(280),
                label = "animatedHeroBg"
            )

            // Floating Reward Badge on correct answer
            AnimatedVisibility(
                visible = rewardFloatingBadge != null,
                enter = slideInVertically(
                    initialOffsetY = { -it / 2 },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)
                ) + fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.85f),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(animationSpec = tween(250)) + scaleOut(targetScale = 1.05f)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1B5E20),
                    border = BorderStroke(1.5.dp, Color(0xFF81C784)),
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = rewardFloatingBadge ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // HERO PROBLEM STAGE: Luxury Glass Card with Glow & Spring Transitions
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = animatedHeroBg),
                border = when (answerState) {
                    true -> BorderStroke(2.5.dp, Color(0xFF66BB6A))
                    false -> BorderStroke(2.5.dp, Color(0xFFFF5252))
                    null -> BorderStroke(
                        if (comboStreak >= 3) 2.dp else 1.5.dp,
                        Brush.horizontalGradient(
                            if (comboStreak >= 10) {
                                listOf(Color(0xFFFFD700), Color(0xFFFF1744), Color(0xFFE040FB))
                            } else if (comboStreak >= 5) {
                                listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF))
                            } else if (comboStreak >= 3) {
                                listOf(Color(0xFFFF9100), Color(0xFFE53935))
                            } else {
                                listOf(Color(0xFFFFD54F).copy(alpha = 0.6f), Color(0xFFE53935).copy(alpha = 0.8f))
                            }
                        )
                    )
                },
                elevation = CardDefaults.cardElevation(defaultElevation = if (comboStreak >= 3) 10.dp else 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = shakeOffset.value.dp)
                    .shadow(
                        elevation = if (comboStreak >= 3) 14.dp else 6.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = if (comboStreak >= 5) Color(0xFF00E5FF).copy(alpha = 0.35f) else Color(0xFFE53935),
                        spotColor = if (comboStreak >= 3) Color(0xFFFFD54F).copy(alpha = 0.5f) else Color(0xFFE53935)
                    )
                    .testTag("trainer_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Stage Header Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE53935).copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "PROBLEM #${totalAttempted + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCDD2),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Bead Hint Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .clickable { showHint = !showHint }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("trainer_hint_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Hint",
                                tint = if (showHint) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showHint) "Hide Rule" else "Bead Rule",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showHint) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Problem Formula Display with Spring Motion
                    AnimatedContent(
                        targetState = currentProblem.displayFormula,
                        transitionSpec = {
                            (slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) { height -> height / 2 } + fadeIn(animationSpec = tween(260)) + scaleIn(initialScale = 0.88f)) togetherWith
                                    (slideOutVertically(
                                        animationSpec = tween(180)
                                    ) { height -> -height / 2 } + fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 1.08f))
                        },
                        label = "ProblemAnimation"
                    ) { formula ->
                        Text(
                            text = formula,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Expandable Bead Formula Tip
                    AnimatedVisibility(visible = showHint) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFFD54F).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFFFD54F).copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "UCMAS Abacus Technique",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = currentProblem.formulaTip,
                                fontSize = 11.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // User Input Display Pill with Cursor
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.4f),
                        border = BorderStroke(
                            1.5.dp,
                            when (answerState) {
                                true -> Color(0xFF66BB6A)
                                false -> Color(0xFFFF5252)
                                null -> Color.White.copy(alpha = 0.3f)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(52.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (userInput.isEmpty()) {
                                Text(
                                    text = "Tap Keypad...",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.35f),
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userInput,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Black,
                                        color = when (answerState) {
                                            true -> Color(0xFF66BB6A)
                                            false -> Color(0xFFFF5252)
                                            null -> Color(0xFFFFD54F)
                                        },
                                        letterSpacing = 2.sp
                                    )
                                    // Subtle blinking cursor
                                    if (answerState == null) {
                                        Text(
                                            text = "|",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.padding(start = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tactile Keypad
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "⌫")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { key ->
                            TactileKeypadButton(
                                key = key,
                                onClick = { handleKeyPress(key) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Wide Submit Button with bouncy spring animation
                var isSubmitPressed by remember { mutableStateOf(false) }
                val submitScale by animateFloatAsState(
                    targetValue = if (isSubmitPressed) 0.94f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "submitScale"
                )

                Button(
                    onClick = {
                        isSubmitPressed = true
                        submitAnswer()
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (comboStreak >= 5) Color(0xFFD50000) else Color(0xFFE53935)
                    ),
                    modifier = Modifier
                        .scale(submitScale)
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = if (comboStreak >= 3) 8.dp else 4.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = if (comboStreak >= 3) Color(0xFFFF5252) else Color.Transparent
                        )
                        .testTag("trainer_submit_button")
                ) {
                    LaunchedEffect(isSubmitPressed) {
                        if (isSubmitPressed) {
                            delay(90)
                            isSubmitPressed = false
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SUBMIT ANSWER",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }

    // SESSION SUMMARY DIALOG
    if (isSessionComplete) {
        val accuracy = if (totalAttempted > 0) (totalCorrect.toFloat() / totalAttempted) * 100f else 0f
        val rank = when {
            accuracy >= 90f && totalAttempted >= 8 -> "S"
            accuracy >= 75f -> "A"
            accuracy >= 60f -> "B"
            else -> "C"
        }

        AlertDialog(
            onDismissRequest = { isSessionComplete = false; onBack() },
            containerColor = Color(0xFF261018),
            shape = RoundedCornerShape(28.dp),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFFFD54F), Color(0xFFFFA000))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rank,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF3E2723)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Session Complete!",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Grade $rank • ${selectedMode.title}",
                        fontSize = 12.sp,
                        color = Color(0xFFFFD54F)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Solved", fontSize = 11.sp, color = Color.Gray)
                            Text("$totalCorrect / $totalAttempted", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Accuracy", fontSize = 11.sp, color = Color.Gray)
                            Text("${accuracy.toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF66BB6A))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Max Streak", fontSize = 11.sp, color = Color.Gray)
                            Text("${maxCombo}x", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                    // Rewards Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+$totalTokensWon Tokens", fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F), fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+$totalXpWon XP", fontWeight = FontWeight.Bold, color = Color(0xFF90CAF9), fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isSessionComplete = false
                        totalAttempted = 0
                        totalCorrect = 0
                        score = 0
                        comboStreak = 0
                        maxCombo = 0
                        totalTokensWon = 0
                        totalXpWon = 0
                        blitzSecondsLeft = 60
                        isTimerRunning = false
                        currentProblem = ProblemGenerator.generate(1, selectedMode)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("trainer_restart_button")
                ) {
                    Text("Train Again")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isSessionComplete = false
                        onBack()
                    }
                ) {
                    Text("Return to Hub", color = Color.White.copy(alpha = 0.8f))
                }
            }
        )
    }
}

data class StreakTierInfo(
    val name: String,
    val icon: ImageVector,
    val colors: List<Color>,
    val glowColor: Color
)

/**
 * Visual Progress Streak Indicator that rewards consecutive correct answers with a glowing
 * animated beam, dynamic milestone crystal pips (1-10), and fluid breathing glow animations.
 */
@Composable
fun VisualStreakProgressIndicator(
    comboStreak: Int,
    comboMultiplier: Float,
    modifier: Modifier = Modifier
) {
    // Current streak within a 10-step milestone loop
    val targetProgress = if (comboStreak == 0) 0f else if (comboStreak % 10 == 0) 1f else ((comboStreak % 10).toFloat() / 10f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "streakProgress"
    )

    // Breathing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "streakGlowTransition")
    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowIntensity"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    // Determine current Tier styling
    val tierInfo = when {
        comboStreak >= 10 -> StreakTierInfo(
            name = "COSMIC SUPERNOVA • 3.0x",
            icon = Icons.Default.Whatshot,
            colors = listOf(Color(0xFFFFD700), Color(0xFFFF1744), Color(0xFFE040FB)),
            glowColor = Color(0xFFFF1744)
        )
        comboStreak >= 5 -> StreakTierInfo(
            name = "PLASMA SURGE • 2.0x",
            icon = Icons.Default.Bolt,
            colors = listOf(Color(0xFF00E5FF), Color(0xFF2979FF), Color(0xFF7C4DFF)),
            glowColor = Color(0xFF00E5FF)
        )
        comboStreak >= 3 -> StreakTierInfo(
            name = "FLAME STREAK • 1.5x",
            icon = Icons.Default.LocalFireDepartment,
            colors = listOf(Color(0xFFFF9100), Color(0xFFFF3D00), Color(0xFFD50000)),
            glowColor = Color(0xFFFF6D00)
        )
        comboStreak > 0 -> StreakTierInfo(
            name = "EMBER IGNITION • 1.0x",
            icon = Icons.Default.Flare,
            colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA000)),
            glowColor = Color(0xFFFFD54F)
        )
        else -> StreakTierInfo(
            name = "STREAK CHARGER • READY",
            icon = Icons.Default.ElectricBolt,
            colors = listOf(Color(0xFF888899), Color(0xFF555566)),
            glowColor = Color.Transparent
        )
    }

    // Render container with glowing aura
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF230E16).copy(alpha = 0.9f)
        ),
        border = BorderStroke(
            width = if (comboStreak >= 3) 1.8.dp else 1.dp,
            brush = if (comboStreak >= 3) {
                Brush.horizontalGradient(tierInfo.colors)
            } else {
                SolidColor(Color.White.copy(alpha = 0.15f))
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (comboStreak >= 3) 8.dp else 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (comboStreak >= 3) (8.dp * glowIntensity) else 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = tierInfo.glowColor.copy(alpha = 0.4f),
                spotColor = tierInfo.glowColor.copy(alpha = 0.6f)
            )
            .testTag("visual_streak_indicator")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Tier Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(
                                if (comboStreak > 0) Brush.radialGradient(tierInfo.colors)
                                else SolidColor(Color.White.copy(alpha = 0.12f))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tierInfo.icon,
                            contentDescription = null,
                            tint = if (comboStreak > 0) Color.White else Color.Gray,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tierInfo.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (comboStreak > 0) tierInfo.colors.first() else Color.White.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp
                    )
                }

                // Streak counter pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (comboStreak >= 3) tierInfo.colors.last().copy(alpha = 0.28f) else Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, if (comboStreak >= 3) tierInfo.colors.first().copy(alpha = 0.5f) else Color.White.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${comboStreak} CONSECUTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (comboStreak >= 3) Color.White else Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // GLOWING FLUID STREAK BEAM / PROGRESS BAR WITH SHIMMER & COMET HEAD
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(10.dp))
            ) {
                val totalWidth = maxWidth
                if (animatedProgress > 0f) {
                    val filledWidth = totalWidth * animatedProgress

                    // Dynamic Gradient Fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(filledWidth)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    tierInfo.colors + listOf(Color.White.copy(alpha = 0.8f))
                                )
                            )
                    )

                    // Shimmer wave sweep across the fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(filledWidth)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    0.0f to Color.Transparent,
                                    shimmerOffset.coerceIn(0.05f, 0.75f) to Color.White.copy(alpha = 0.45f),
                                    (shimmerOffset + 0.25f).coerceAtMost(1f) to Color.Transparent
                                )
                            )
                    )

                    // Glowing comet head orb at the leading edge
                    Box(
                        modifier = Modifier
                            .offset(x = (filledWidth - 14.dp).coerceAtLeast(0.dp), y = 1.dp)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, tierInfo.colors.first(), CircleShape)
                            .shadow(
                                elevation = 8.dp * glowIntensity,
                                shape = CircleShape,
                                spotColor = tierInfo.glowColor
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 10 MILESTONE PIPS / GEMS (1 to 10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (pip in 1..10) {
                    val isLit = (comboStreak > 0) && (
                        (comboStreak % 10 >= pip) || (comboStreak > 0 && comboStreak % 10 == 0)
                    )
                    val isMilestone = pip == 3 || pip == 5 || pip == 10

                    val pipScale by animateFloatAsState(
                        targetValue = if (isLit) 1.28f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "pipScale_$pip"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.scale(pipScale)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (isMilestone) 18.dp else 13.dp)
                                .clip(if (isMilestone) RoundedCornerShape(5.dp) else CircleShape)
                                .background(
                                    when {
                                        isLit -> Brush.radialGradient(tierInfo.colors)
                                        isMilestone -> SolidColor(Color.White.copy(alpha = 0.2f))
                                        else -> SolidColor(Color.White.copy(alpha = 0.08f))
                                    }
                                )
                                .border(
                                    width = 1.2.dp,
                                    color = when {
                                        isLit -> Color.White
                                        isMilestone -> Color.White.copy(alpha = 0.45f)
                                        else -> Color.White.copy(alpha = 0.12f)
                                    },
                                    shape = if (isMilestone) RoundedCornerShape(5.dp) else CircleShape
                                )
                                .shadow(
                                    elevation = if (isLit) (6.dp * glowIntensity) else 0.dp,
                                    shape = if (isMilestone) RoundedCornerShape(5.dp) else CircleShape,
                                    spotColor = tierInfo.glowColor
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isMilestone) {
                                Text(
                                    text = when (pip) {
                                        3 -> "1.5"
                                        5 -> "2x"
                                        else -> "3x"
                                    },
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isLit) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Streak Booster Banner with expanding spring animation
            AnimatedVisibility(
                visible = comboStreak >= 3,
                enter = expandVertically(
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                ) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = tierInfo.colors.last().copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, tierInfo.colors.first().copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = tierInfo.colors.first(),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    comboStreak >= 10 -> "3.0x SUPERNOVA ACTIVE! ALL REWARDS TRIPLED"
                                    comboStreak >= 5 -> "2.0x PLASMA ACTIVE! REWARDS DOUBLED"
                                    else -> "1.5x FLAME STREAK ACTIVE! +50% BONUS"
                                },
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = tierInfo.colors.first(),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TactileKeypadButton(
    key: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "keyScale_$key"
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = when (key) {
            "C" -> Color(0xFFD32F2F).copy(alpha = 0.28f)
            "⌫" -> Color(0xFF5D4037).copy(alpha = 0.42f)
            else -> Color.White.copy(alpha = 0.09f)
        },
        border = BorderStroke(
            1.dp,
            when (key) {
                "C" -> Color(0xFFFF8A80).copy(alpha = 0.45f)
                "⌫" -> Color(0xFFFFB74D).copy(alpha = 0.45f)
                else -> Color.White.copy(alpha = 0.18f)
            }
        ),
        modifier = modifier
            .scale(animatedScale)
            .height(54.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                isPressed = true
                onClick()
            }
            .testTag("trainer_key_$key")
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(80)
                isPressed = false
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = key,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = when (key) {
                    "C" -> Color(0xFFFF8A80)
                    "⌫" -> Color(0xFFFFB74D)
                    else -> Color.White
                }
            )
        }
    }
}
