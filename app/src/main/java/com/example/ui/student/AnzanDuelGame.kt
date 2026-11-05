package com.example.ui.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.ArenaTheme
import com.example.viewmodel.AnzanDuelViewModel
import com.example.viewmodel.DuelRound
import com.example.viewmodel.DuelState
import com.example.viewmodel.OpponentPlayer
import kotlinx.coroutines.launch

/**
 * AnzanDuelGame / AnzanDuelScreen
 * Speed-based 1-v-1 mental math competition layout featuring:
 * - Real-time countdown timer with circular progress and dynamic color transitions
 * - High-speed ergonomic on-screen numeric keypad with testTags
 * - Split-screen live duelist telemetry and combo multiplier
 * - Real-time calculation latency feedback
 */
@Composable
fun AnzanDuelScreen(
    theme: ArenaTheme,
    onMatchWin: (tokens: Int, xp: Int) -> Unit,
    onBack: () -> Unit,
    onBeadClick: () -> Unit,
    onCorrectChime: () -> Unit,
    onErrorBuzz: () -> Unit,
    modifier: Modifier = Modifier,
    onDuelFinished: ((opponentName: String, opponentTier: String, playerScore: Int, opponentScore: Int, totalRounds: Int, correctAnswers: Int, playerAccuracy: Float, avgLatencyMs: Long, maxCombo: Int, isWin: Boolean) -> Unit)? = null,
    duelViewModel: AnzanDuelViewModel = viewModel()
) {
    AnzanDuelGame(
        theme = theme,
        onMatchWin = onMatchWin,
        onBack = onBack,
        onBeadClick = onBeadClick,
        onCorrectChime = onCorrectChime,
        onErrorBuzz = onErrorBuzz,
        modifier = modifier,
        onDuelFinished = onDuelFinished,
        duelViewModel = duelViewModel
    )
}

@Composable
fun AnzanDuelGame(
    theme: ArenaTheme,
    onMatchWin: (tokens: Int, xp: Int) -> Unit,
    onBack: () -> Unit,
    onBeadClick: () -> Unit,
    onCorrectChime: () -> Unit,
    onErrorBuzz: () -> Unit,
    modifier: Modifier = Modifier,
    onDuelFinished: ((opponentName: String, opponentTier: String, playerScore: Int, opponentScore: Int, totalRounds: Int, correctAnswers: Int, playerAccuracy: Float, avgLatencyMs: Long, maxCombo: Int, isWin: Boolean) -> Unit)? = null,
    duelViewModel: AnzanDuelViewModel = viewModel()
) {
    val duelState by duelViewModel.duelState.collectAsStateWithLifecycle()
    val opponent by duelViewModel.opponent.collectAsStateWithLifecycle()
    val currentRound by duelViewModel.currentRound.collectAsStateWithLifecycle()
    val totalRounds by duelViewModel.totalRounds.collectAsStateWithLifecycle()
    val roundData by duelViewModel.roundData.collectAsStateWithLifecycle()
    val playerScore by duelViewModel.playerScore.collectAsStateWithLifecycle()
    val opponentScore by duelViewModel.opponentScore.collectAsStateWithLifecycle()
    val userAnswer by duelViewModel.userAnswer.collectAsStateWithLifecycle()
    val roundTimeLeft by duelViewModel.roundTimeLeft.collectAsStateWithLifecycle()
    val roundTimeRemainingMs by duelViewModel.roundTimeRemainingMs.collectAsStateWithLifecycle()
    val roundTimeFraction by duelViewModel.roundTimeFraction.collectAsStateWithLifecycle()
    val countdownTimer by duelViewModel.countdownTimer.collectAsStateWithLifecycle()
    val comboMultiplier by duelViewModel.comboMultiplier.collectAsStateWithLifecycle()
    val maxCombo by duelViewModel.maxCombo.collectAsStateWithLifecycle()
    val correctAnswersCount by duelViewModel.correctAnswersCount.collectAsStateWithLifecycle()
    val opponentStatusText by duelViewModel.opponentStatusText.collectAsStateWithLifecycle()
    val playerLatencyMs by duelViewModel.playerLatencyMs.collectAsStateWithLifecycle()
    val isWrongAnswerShake by duelViewModel.isWrongAnswerShake.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    // Shake offset for wrong answer feedback
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isWrongAnswerShake) {
        if (isWrongAnswerShake) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    -16f at 50
                    16f at 100
                    -12f at 150
                    12f at 200
                    -6f at 250
                    6f at 300
                    0f at 350
                }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    duelViewModel.resetToLobby()
                    onBack()
                },
                modifier = Modifier.testTag("duel_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Return to Arena Hub",
                    tint = Color(0xFF1E1E24)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ANZAN SPEED DUEL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = theme.primary,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "1-v-1 UCMAS Mental Arithmetic",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            if (duelState == DuelState.IN_PROGRESS || duelState == DuelState.ROUND_TRANSITION) {
                // Round step pills
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = theme.primary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "R$currentRound/$totalRounds",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = theme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (duelState) {
            DuelState.LOBBY -> {
                AnzanDuelLobby(
                    theme = theme,
                    onStartMatch = { duelViewModel.startMatchmaking() }
                )
            }

            DuelState.MATCHMAKING -> {
                AnzanDuelMatchmaking(theme = theme)
            }

            DuelState.MATCH_FOUND -> {
                AnzanDuelMatchFound(opponent = opponent, theme = theme)
            }

            DuelState.COUNTDOWN -> {
                AnzanDuelCountdown(countdown = countdownTimer, theme = theme)
            }

            DuelState.IN_PROGRESS, DuelState.ROUND_TRANSITION -> {
                // Head-to-Head Duelist HUD
                AnzanDuelScoreboard(
                    playerScore = playerScore,
                    opponentScore = opponentScore,
                    opponent = opponent,
                    opponentStatusText = opponentStatusText,
                    comboMultiplier = comboMultiplier,
                    theme = theme
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Real-time Countdown Timer and Mental Math Problem Arena
                AnzanMathArenaCard(
                    roundData = roundData,
                    currentRound = currentRound,
                    totalRounds = totalRounds,
                    userAnswer = userAnswer,
                    roundTimeRemainingMs = roundTimeRemainingMs,
                    roundTimeFraction = roundTimeFraction,
                    isWrongShake = isWrongAnswerShake,
                    shakeOffset = shakeOffset.value,
                    playerLatencyMs = playerLatencyMs,
                    theme = theme
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Dedicated Speed-Based Numeric Keypad
                AnzanNumericKeypad(
                    theme = theme,
                    canSubmit = userAnswer.isNotEmpty() && userAnswer != "-",
                    onDigitClick = { digit ->
                        onBeadClick()
                        duelViewModel.appendDigit(digit)
                    },
                    onBackspace = {
                        onBeadClick()
                        duelViewModel.backspaceDigit()
                    },
                    onClear = {
                        onBeadClick()
                        duelViewModel.clearAnswer()
                    },
                    onToggleNegative = {
                        onBeadClick()
                        duelViewModel.toggleNegative()
                    },
                    onSubmit = {
                        duelViewModel.submitAnswer(
                            onCorrect = onCorrectChime,
                            onError = onErrorBuzz
                        )
                    }
                )
            }

            DuelState.GAME_OVER -> {
                AnzanDuelGameOver(
                    playerScore = playerScore,
                    opponentScore = opponentScore,
                    opponent = opponent,
                    correctAnswersCount = correctAnswersCount,
                    totalRounds = totalRounds,
                    maxCombo = maxCombo,
                    playerLatencyMs = playerLatencyMs,
                    theme = theme,
                    onMatchWin = onMatchWin,
                    onDuelFinished = onDuelFinished,
                    onPlayAgain = { duelViewModel.startMatchmaking() },
                    onExit = {
                        duelViewModel.resetToLobby()
                        onBack()
                    }
                )
            }
        }
    }
}

/**
 * Scoreboard displaying Player vs Opponent live telemetry
 */
@Composable
private fun AnzanDuelScoreboard(
    playerScore: Int,
    opponentScore: Int,
    opponent: OpponentPlayer?,
    opponentStatusText: String,
    comboMultiplier: Int,
    theme: ArenaTheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player Box (Left)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(2.dp, theme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "YOU",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = theme.primary
                    )
                }

                Text(
                    text = "$playerScore",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E1E24)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (comboMultiplier > 1) Color(0xFFFFF3E0) else Color(0xFFF5F5F7)
                ) {
                    Text(
                        text = if (comboMultiplier > 1) "🔥 x$comboMultiplier COMBO" else "x1 MULTIPLIER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (comboMultiplier > 1) Color(0xFFE65100) else Color.Gray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Animated VS Badge
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A1A24)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "VS",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
            )
        }

        // Opponent Box (Right)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E6)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = opponent?.name?.take(11) ?: "RIVAL",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    maxLines = 1
                )

                Text(
                    text = "$opponentScore",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E1E24)
                )

                Text(
                    text = opponentStatusText,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = Color(0xFF616170),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Real-Time Countdown Timer and Mental Math Problem Arena Card
 */
@Composable
private fun AnzanMathArenaCard(
    roundData: DuelRound?,
    currentRound: Int,
    totalRounds: Int,
    userAnswer: String,
    roundTimeRemainingMs: Long,
    roundTimeFraction: Float,
    isWrongShake: Boolean,
    shakeOffset: Float,
    playerLatencyMs: Long,
    theme: ArenaTheme
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE8E8EE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row: Round Badge & Real-Time Circular Countdown Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = theme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "ROUND $currentRound OF $totalRounds",
                            style = MaterialTheme.typography.labelSmall,
                            color = theme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (playerLatencyMs > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Prev speed: ${playerLatencyMs}ms",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Dynamic Circular Countdown Timer
                RealTimeCircularCountdownTimer(
                    fraction = roundTimeFraction,
                    remainingMs = roundTimeRemainingMs,
                    theme = theme
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mental Arithmetic Expression Box
            roundData?.let { rd ->
                Text(
                    text = "${rd.expressionDisplay} = ?",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF1E1E24),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Input Display with shake effect
            Box(
                modifier = Modifier
                    .offset(x = shakeOffset.dp)
                    .fillMaxWidth(0.72f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isWrongShake) Color(0xFFFFEBEE) else Color(0xFFF6F7FA))
                    .border(
                        width = 2.dp,
                        color = if (isWrongShake) Color(0xFFD32F2F) else theme.primary,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (userAnswer.isEmpty()) {
                    // Pulsing placeholder cursor
                    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
                    val cursorAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 0.9f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "cursor_alpha"
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Tap Keypad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray.copy(alpha = cursorAlpha)
                        )
                    }
                } else {
                    Text(
                        text = userAnswer,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isWrongShake) Color(0xFFD32F2F) else Color(0xFF1E1E24),
                        letterSpacing = 2.sp
                    )
                }
            }

            // Real-time linear progress bar under input box
            Spacer(modifier = Modifier.height(12.dp))
            val timerColor by animateColorAsState(
                targetValue = when {
                    roundTimeFraction > 0.5f -> Color(0xFF00E676)
                    roundTimeFraction > 0.25f -> Color(0xFFFFB300)
                    else -> Color(0xFFFF1744)
                },
                label = "linear_timer_color"
            )

            LinearProgressIndicator(
                progress = { roundTimeFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = timerColor,
                trackColor = Color(0xFFEEEEEE)
            )
        }
    }
}

/**
 * Real-Time Circular Countdown Timer with animated sweep and urgency pulse
 */
@Composable
fun RealTimeCircularCountdownTimer(
    fraction: Float,
    remainingMs: Long,
    theme: ArenaTheme,
    modifier: Modifier = Modifier
) {
    val secondsLeft = (remainingMs / 1000f)
    val formattedSeconds = String.format("%.1f", secondsLeft.coerceAtLeast(0f))

    // Urgency pulse animation when time is critical (< 2.5s)
    val isCritical = fraction < 0.25f
    val infiniteTransition = rememberInfiniteTransition(label = "urgent_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCritical) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val timerColor by animateColorAsState(
        targetValue = when {
            fraction > 0.5f -> Color(0xFF00C853)
            fraction > 0.25f -> Color(0xFFFF9100)
            else -> Color(0xFFFF1744)
        },
        label = "arc_color"
    )

    Box(
        modifier = modifier
            .scale(pulseScale)
            .size(58.dp),
        contentAlignment = Alignment.Center
    ) {
        // Circular Progress Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 5.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Background Track
            drawArc(
                color = Color(0xFFE8E8EE),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active Sweep Progress
            val sweep = (fraction * 360f).coerceIn(0f, 360f)
            drawArc(
                color = timerColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Central Countdown Display
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formattedSeconds,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = if (isCritical) Color(0xFFFF1744) else Color(0xFF1E1E24),
                fontSize = 12.sp
            )
            Text(
                text = "SEC",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 7.sp
            )
        }
    }
}

/**
 * Dedicated Speed-Based Numeric Input Keypad
 * Engineered for lightning-fast mental math speed entry
 */
@Composable
fun AnzanNumericKeypad(
    theme: ArenaTheme,
    canSubmit: Boolean,
    onDigitClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onToggleNegative: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("anzan_numeric_keypad"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Rows 1-3: Standard 1 to 9 digits
        val digitRows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9")
        )

        digitRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { digit ->
                    KeypadButton(
                        text = digit,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("duel_key_$digit"),
                        backgroundColor = Color.White,
                        textColor = Color(0xFF1E1E24),
                        borderColor = Color(0xFFE4E4EE),
                        onClick = { onDigitClick(digit) }
                    )
                }
            }
        }

        // Row 4: Negative toggle ±, Digit 0, Backspace ⌫
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Negative / Plus-Minus toggle
            KeypadButton(
                text = "±",
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("duel_key_toggle_sign"),
                backgroundColor = Color(0xFFF3F3F7),
                textColor = Color(0xFF455A64),
                borderColor = Color(0xFFE0E0EA),
                onClick = onToggleNegative
            )

            // Digit 0
            KeypadButton(
                text = "0",
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("duel_key_0"),
                backgroundColor = Color.White,
                textColor = Color(0xFF1E1E24),
                borderColor = Color(0xFFE4E4EE),
                onClick = { onDigitClick("0") }
            )

            // Backspace ⌫
            KeypadIconButton(
                icon = Icons.Default.Backspace,
                contentDescription = "Backspace",
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("duel_key_backspace"),
                backgroundColor = Color(0xFFF3F3F7),
                tint = Color(0xFFC62828),
                borderColor = Color(0xFFE0E0EA),
                onClick = onBackspace
            )
        }

        // Row 5: Action Row: CLEAR (C) and Prominent SUBMIT (✓ GO)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Clear button
            KeypadButton(
                text = "CLEAR",
                modifier = Modifier
                    .weight(1.1f)
                    .height(54.dp)
                    .testTag("duel_key_clear")
                    .testTag("duel_key_C"),
                backgroundColor = Color(0xFFFFEBEE),
                textColor = Color(0xFFC62828),
                borderColor = Color(0xFFFFCDD2),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                onClick = onClear
            )

            // Submit Button (High-visibility speed action)
            Button(
                onClick = onSubmit,
                enabled = canSubmit,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.primary,
                    disabledContainerColor = Color(0xFFD0D0DC)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .weight(2f)
                    .height(54.dp)
                    .testTag("duel_key_submit")
                    .testTag("duel_key_GO")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SUBMIT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

/**
 * Standard tactile keypad text button
 */
@Composable
private fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    textColor: Color = Color(0xFF1E1E24),
    borderColor: Color = Color(0xFFE4E4EE),
    fontSize: androidx.compose.ui.unit.TextUnit = 22.sp,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor),
        shadowElevation = 2.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = fontWeight,
                fontSize = fontSize,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Keypad icon button (e.g. Backspace)
 */
@Composable
private fun KeypadIconButton(
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFF3F3F7),
    tint: Color = Color(0xFFC62828),
    borderColor: Color = Color(0xFFE0E0EA),
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor),
        shadowElevation = 2.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * Anzan Duel Lobby
 */
@Composable
private fun AnzanDuelLobby(
    theme: ArenaTheme,
    onStartMatch: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECECF2)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(theme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = theme.primary,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "1-v-1 Live Anzan Duel",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E1E24)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Compete against peer scholars in real-time speed mental arithmetic. Submit rapid calculations before the countdown expires to multiply your combo streak!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666677),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Rules & Rewards strip
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFFF8E1),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Victory Stake: +25 Tokens • +75 XP",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5D4037)
                        )
                        Text(
                            text = "5 Rounds • 10s Timer • Speed Latency Bonus",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Color(0xFF795548)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartMatch,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_matchmaking_button")
            ) {
                Icon(Icons.Default.SportsEsports, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ENTER DUEL MATCHMAKING",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

/**
 * Matchmaking Animation State
 */
@Composable
private fun AnzanDuelMatchmaking(theme: ArenaTheme) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A24)),
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF00E5FF),
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Pinging Thane Kopri East Arena...",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Matching with Diamond & Platinum league scholars",
                    color = Color(0xFFB0BEC5),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Match Found Notification Card
 */
@Composable
private fun AnzanDuelMatchFound(opponent: OpponentPlayer?, theme: ArenaTheme) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    text = "⚡ OPPONENT MATCHED!",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            opponent?.let { opp ->
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1565C0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opp.avatarInitials,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = opp.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E1E24)
                )

                Text(
                    text = "${opp.className} • ${opp.tier} Tier • Avg Latency: ${opp.avgLatencyMs}ms",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

/**
 * Match Start Countdown (3, 2, 1, GO!)
 */
@Composable
private fun AnzanDuelCountdown(countdown: Int, theme: ArenaTheme) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A24)),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "DUEL BEGINS IN",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$countdown",
                    color = Color.White,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    fontSize = 92.sp
                )
            }
        }
    }
}

/**
 * Game Over & Victory Telemetry Screen
 */
@Composable
private fun AnzanDuelGameOver(
    playerScore: Int,
    opponentScore: Int,
    opponent: OpponentPlayer?,
    correctAnswersCount: Int,
    totalRounds: Int,
    maxCombo: Int,
    playerLatencyMs: Long,
    theme: ArenaTheme,
    onMatchWin: (tokens: Int, xp: Int) -> Unit,
    onDuelFinished: ((opponentName: String, opponentTier: String, playerScore: Int, opponentScore: Int, totalRounds: Int, correctAnswers: Int, playerAccuracy: Float, avgLatencyMs: Long, maxCombo: Int, isWin: Boolean) -> Unit)? = null,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    val isWin = playerScore > opponentScore
    val isTie = playerScore == opponentScore

    LaunchedEffect(isWin) {
        if (isWin) {
            onMatchWin(25, 75)
        }
        val acc = if (totalRounds > 0) (correctAnswersCount.toFloat() / totalRounds) * 100f else 0f
        onDuelFinished?.invoke(
            opponent?.name ?: "Challenger",
            opponent?.tier ?: "Diamond",
            playerScore,
            opponentScore,
            totalRounds,
            correctAnswersCount,
            acc,
            playerLatencyMs,
            maxCombo,
            isWin
        )
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isWin) Icons.Default.EmojiEvents else Icons.Default.SportsScore,
                contentDescription = null,
                tint = if (isWin) Color(0xFFFFD700) else Color(0xFF0288D1),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when {
                    isWin -> "MATCH VICTORY!"
                    isTie -> "TIED BATTLE!"
                    else -> "MATCH COMPLETE"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = if (isWin) Color(0xFF2E7D32) else Color(0xFF1E1E24)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Final Score: $playerScore vs $opponentScore (${opponent?.name ?: "Rival"})",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF555566)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Match Telemetry Stats Grid
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF7F8FA),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4E4EC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PERFORMANCE TELEMETRY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Accuracy", fontSize = 11.sp, color = Color.Gray)
                            Text("$correctAnswersCount / $totalRounds", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Max Combo", fontSize = 11.sp, color = Color.Gray)
                            Text("x$maxCombo Streak", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE65100))
                        }
                        Column {
                            Text("Speed Latency", fontSize = 11.sp, color = Color.Gray)
                            Text("${playerLatencyMs}ms", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = theme.primary)
                        }
                    }
                }
            }

            if (isWin) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF8E1),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFA000))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+25 Tokens • +75 XP Credited",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onExit,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("duel_exit_button")
                ) {
                    Text("Arena Hub", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("duel_play_again_button")
                ) {
                    Text("Rematch", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
