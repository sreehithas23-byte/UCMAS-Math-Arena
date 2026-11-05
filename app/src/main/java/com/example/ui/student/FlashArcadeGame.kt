package com.example.ui.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class FlashGameState {
    CONFIG,
    COUNTDOWN,
    FLASHING,
    INPUT,
    RESULT
}

@Composable
fun FlashArcadeGame(
    theme: ArenaTheme,
    onDrillSuccess: (tokens: Int, xp: Int) -> Unit,
    onBack: () -> Unit,
    onFlashTick: () -> Unit,
    onCorrectChime: () -> Unit,
    onErrorBuzz: () -> Unit,
    onBeadClick: () -> Unit
) {
    var gameState by remember { mutableStateOf(FlashGameState.CONFIG) }
    var flashSpeedMs by remember { mutableLongStateOf(500L) }
    var digitLength by remember { mutableIntStateOf(2) } // 1, 2, or 3 digits
    var numbersCount by remember { mutableIntStateOf(5) } // 3 to 8 numbers

    var generatedSequence by remember { mutableStateOf(listOf<Int>()) }
    var currentFlashIndex by remember { mutableIntStateOf(-1) }
    var countdownValue by remember { mutableIntStateOf(3) }
    var userSumInput by remember { mutableStateOf("") }
    var actualSum by remember { mutableIntStateOf(0) }
    var isCorrectResult by remember { mutableStateOf(false) }
    var streakCount by remember { mutableIntStateOf(0) }

    fun startFlashSequence() {
        // Generate sequence
        val list = mutableListOf<Int>()
        var sum = 0
        for (i in 0 until numbersCount) {
            val num = when (digitLength) {
                1 -> Random.nextInt(1, 9)
                2 -> Random.nextInt(10, 99)
                else -> Random.nextInt(100, 999)
            }
            list.add(num)
            sum += num
        }
        generatedSequence = list
        actualSum = sum
        userSumInput = ""
        gameState = FlashGameState.COUNTDOWN
    }

    // Countdown and Flashing Effect
    LaunchedEffect(gameState) {
        if (gameState == FlashGameState.COUNTDOWN) {
            countdownValue = 3
            while (countdownValue > 0) {
                onFlashTick()
                delay(800)
                countdownValue--
            }
            gameState = FlashGameState.FLASHING
        } else if (gameState == FlashGameState.FLASHING) {
            currentFlashIndex = 0
            for (idx in generatedSequence.indices) {
                currentFlashIndex = idx
                onFlashTick()
                delay(flashSpeedMs)
                // Blank interval between flashes
                currentFlashIndex = -1
                delay(flashSpeedMs / 3)
            }
            gameState = FlashGameState.INPUT
        }
    }

    fun submitAnswer() {
        val entered = userSumInput.toIntOrNull()
        if (entered == actualSum) {
            onCorrectChime()
            isCorrectResult = true
            streakCount++
            val comboBonus = if (streakCount > 1) streakCount * 5 else 0
            onDrillSuccess(10 + comboBonus, 25 + comboBonus)
        } else {
            onErrorBuzz()
            isCorrectResult = false
            streakCount = 0
        }
        gameState = FlashGameState.RESULT
    }

    fun appendInput(d: String) {
        onBeadClick()
        if (userSumInput.length < 6) userSumInput += d
    }

    fun clearInput() {
        onBeadClick()
        if (userSumInput.isNotEmpty()) userSumInput = userSumInput.dropLast(1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = "FLASH CALCULATION ARCADE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = theme.primary
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFEBEE),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
            ) {
                Text(
                    text = "Streak: $streakCount",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (gameState) {
            FlashGameState.CONFIG -> {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Arcade Drill Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E24)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Flash Speed Selector
                        Text("Flashing Speed:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(250L to "250ms (Lightning)", 500L to "500ms (Pro)", 800L to "800ms (Mid)", 1200L to "1.2s (Practice)").forEach { (ms, lbl) ->
                                val selected = flashSpeedMs == ms
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) theme.primary else Color(0xFFF2F2F5),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { flashSpeedMs = ms }
                                ) {
                                    Text(
                                        text = lbl.split(" ")[0],
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else Color(0xFF333344),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Digit length
                        Text("Digits per Number:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(1 to "1 Digit", 2 to "2 Digits", 3 to "3 Digits").forEach { (d, label) ->
                                val selected = digitLength == d
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) theme.primary else Color(0xFFF2F2F5),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { digitLength = d }
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else Color(0xFF333344),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Numbers in sequence
                        Text("Sequence Count: $numbersCount numbers", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                        Slider(
                            value = numbersCount.toFloat(),
                            onValueChange = { numbersCount = it.toInt() },
                            valueRange = 3f..8f,
                            steps = 4,
                            colors = SliderDefaults.colors(thumbColor = theme.primary, activeTrackColor = theme.primary)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { startFlashSequence() },
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("start_flash_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("START FLASH DRILL", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            FlashGameState.COUNTDOWN -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("GET READY", color = Color(0xFFFFD54F), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "$countdownValue",
                                color = Color.White,
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 84.sp
                            )
                        }
                    }
                }
            }

            FlashGameState.FLASHING -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (currentFlashIndex >= 0 && currentFlashIndex < generatedSequence.size) {
                            Text(
                                text = "${generatedSequence[currentFlashIndex]}",
                                color = Color.White,
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 88.sp
                            )
                        }
                    }
                }
            }

            FlashGameState.INPUT -> {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ENTER CALCULATED TOTAL",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = theme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF5F5FA),
                            border = androidx.compose.foundation.BorderStroke(2.dp, theme.primary),
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = userSumInput.ifEmpty { "Sum" },
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (userSumInput.isEmpty()) Color.LightGray else Color(0xFF1E1E24)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Keypad
                        val keypad = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("C", "0", "OK")
                        )

                        keypad.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                row.forEach { key ->
                                    val isOk = key == "OK"
                                    val isClear = key == "C"
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = when {
                                            isOk -> theme.primary
                                            isClear -> Color(0xFFFFEBEE)
                                            else -> Color(0xFFF5F5FA)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(46.dp)
                                            .clickable {
                                                when (key) {
                                                    "OK" -> submitAnswer()
                                                    "C" -> clearInput()
                                                    else -> appendInput(key)
                                                }
                                            }
                                            .testTag("flash_key_$key")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = key,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    isOk -> Color.White
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

            FlashGameState.RESULT -> {
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
                        Icon(
                            imageVector = if (isCorrectResult) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrectResult) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isCorrectResult) "EXCELLENT ANZAN!" else "MISSED SUM",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isCorrectResult) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sequence: ${generatedSequence.joinToString(" + ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF555566),
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Actual Sum = $actualSum | Your Answer = ${userSumInput.ifEmpty { "None" }}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1E24)
                        )

                        if (isCorrectResult) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFF8E1),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
                            ) {
                                Text(
                                    text = "+10 Tokens  •  +25 XP (Combo Multiplier Active!)",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5D4037),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { gameState = FlashGameState.CONFIG }, modifier = Modifier.weight(1f)) {
                                Text("Settings")
                            }
                            Button(
                                onClick = { startFlashSequence() },
                                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Next Drill")
                            }
                        }
                    }
                }
            }
        }
    }
}
