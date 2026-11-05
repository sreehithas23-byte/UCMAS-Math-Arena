package com.example.ui.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaTheme
import com.example.util.NumberFormatter
import java.math.BigDecimal

enum class CalcTab {
    CUMULATIVE,
    TWENTY_DIGIT_BIGINT
}

@Composable
fun CalculatorSuiteScreen(
    theme: ArenaTheme,
    onBeadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(CalcTab.CUMULATIVE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tab Selector
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedTab == CalcTab.CUMULATIVE) theme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = CalcTab.CUMULATIVE }
                        .testTag("tab_cumulative_calc")
                ) {
                    Text(
                        text = "Cumulative Running",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == CalcTab.CUMULATIVE) Color.White else Color(0xFF555566),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedTab == CalcTab.TWENTY_DIGIT_BIGINT) theme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = CalcTab.TWENTY_DIGIT_BIGINT }
                        .testTag("tab_twenty_digit_calc")
                ) {
                    Text(
                        text = "20-Digit BigInt (Indian)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == CalcTab.TWENTY_DIGIT_BIGINT) Color.White else Color(0xFF555566),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            CalcTab.CUMULATIVE -> CumulativeCalculatorView(theme = theme, onBeadClick = onBeadClick)
            CalcTab.TWENTY_DIGIT_BIGINT -> TwentyDigitBigIntCalculatorView(theme = theme, onBeadClick = onBeadClick)
        }
    }
}

@Composable
private fun CumulativeCalculatorView(
    theme: ArenaTheme,
    onBeadClick: () -> Unit
) {
    val context = LocalContext.current
    var runningTotal by remember { mutableStateOf(BigDecimal.ZERO) }
    var currentInput by remember { mutableStateOf("") }
    var pendingOperator by remember { mutableStateOf("+") }
    val historyLog = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()

    fun appendInput(char: String) {
        onBeadClick()
        if (char == "." && currentInput.contains(".")) return
        if (currentInput.length < 15) {
            currentInput += char
        }
    }

    fun applyOperation(newOp: String) {
        onBeadClick()
        val operand = currentInput.ifBlank { "0" }.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val oldTotal = runningTotal

        val newTotal = when (pendingOperator) {
            "+" -> oldTotal.add(operand)
            "-" -> oldTotal.subtract(operand)
            "*" -> if (historyLog.isEmpty() && oldTotal == BigDecimal.ZERO && currentInput.isNotBlank()) operand else oldTotal.multiply(operand)
            "/" -> if (operand.compareTo(BigDecimal.ZERO) == 0) oldTotal else oldTotal.divide(operand, 4, java.math.RoundingMode.HALF_UP)
            else -> operand
        }

        val stepNum = historyLog.size + 1
        val stepEntry = "Step $stepNum: ${NumberFormatter.formatIndianNumber(oldTotal.toPlainString())} $pendingOperator ${NumberFormatter.formatIndianNumber(operand.toPlainString())} = ${NumberFormatter.formatIndianNumber(newTotal.toPlainString())}"
        historyLog.add(stepEntry)

        runningTotal = newTotal
        pendingOperator = newOp
        currentInput = ""
    }

    fun onEquals() {
        onBeadClick()
        if (currentInput.isNotBlank()) {
            val operand = currentInput.toBigDecimalOrNull() ?: BigDecimal.ZERO
            val oldTotal = runningTotal
            val newTotal = when (pendingOperator) {
                "+" -> oldTotal.add(operand)
                "-" -> oldTotal.subtract(operand)
                "*" -> oldTotal.multiply(operand)
                "/" -> if (operand.compareTo(BigDecimal.ZERO) == 0) oldTotal else oldTotal.divide(operand, 4, java.math.RoundingMode.HALF_UP)
                else -> operand
            }
            val stepNum = historyLog.size + 1
            historyLog.add("Step $stepNum: ${NumberFormatter.formatIndianNumber(oldTotal.toPlainString())} $pendingOperator ${NumberFormatter.formatIndianNumber(operand.toPlainString())} = ${NumberFormatter.formatIndianNumber(newTotal.toPlainString())}")
            runningTotal = newTotal
            currentInput = ""
        }
    }

    fun resetRunningTotal() {
        onBeadClick()
        runningTotal = BigDecimal.ZERO
        currentInput = ""
        pendingOperator = "+"
        historyLog.clear()
        Toast.makeText(context, "Running total reset to 0", Toast.LENGTH_SHORT).show()
    }

    fun copyHistory() {
        val text = historyLog.joinToString("\n") + "\nFinal Running Total: ${NumberFormatter.formatIndianNumber(runningTotal.toPlainString())}"
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UCMAS Cumulative History", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Calculation history copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Live Running Total Banner
            Surface(
                color = theme.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.primary.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "LIVE RUNNING TOTAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = theme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = NumberFormatter.formatIndianNumber(runningTotal.stripTrailingZeros().toPlainString()),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E1E24),
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Next: $pendingOperator  ",
                            style = MaterialTheme.typography.bodySmall,
                            color = theme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentInput.isEmpty()) "0" else NumberFormatter.formatIndianNumber(currentInput),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF555566)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons: Reset & Copy History
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { resetRunningTotal() },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("reset_running_total_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Total", fontSize = 12.sp)
                }

                Button(
                    onClick = { copyHistory() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    modifier = Modifier.weight(1f).testTag("copy_history_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy History", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // History Log Box
            Surface(
                color = Color(0xFFFBFBFC),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4E4E8)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                if (historyLog.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Calculation steps will log here step-by-step",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(historyLog) { log ->
                            Text(
                                text = log,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF333344)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Keypad
            val buttons = listOf(
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf("0", ".", "=", "+")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { btn ->
                        val isOperator = btn in listOf("+", "-", "*", "/", "=")
                        val isEquals = btn == "="
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                isEquals -> theme.primary
                                isOperator -> theme.primary.copy(alpha = 0.15f)
                                else -> Color(0xFFF2F2F5)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clickable {
                                    when (btn) {
                                        "=" -> onEquals()
                                        "+", "-", "*", "/" -> applyOperation(btn)
                                        else -> appendInput(btn)
                                    }
                                }
                                .testTag("calc_btn_$btn")
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (btn == "*") "×" else if (btn == "/") "÷" else btn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isEquals -> Color.White
                                        isOperator -> theme.primary
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

@Composable
private fun TwentyDigitBigIntCalculatorView(
    theme: ArenaTheme,
    onBeadClick: () -> Unit
) {
    var displayString by remember { mutableStateOf("0") }
    var storedOperand by remember { mutableStateOf<String?>(null) }
    var activeOp by remember { mutableStateOf<String?>(null) }
    var isNewEntry by remember { mutableStateOf(true) }
    var statusNote by remember { mutableStateOf("Exact 20-digit precision • No scientific notation") }

    fun onDigit(d: String) {
        onBeadClick()
        if (isNewEntry) {
            displayString = d
            isNewEntry = false
        } else {
            val raw = NumberFormatter.stripCommas(displayString)
            if (raw.length < 24) { // allow up to 24 digits!
                val newRaw = if (raw == "0") d else raw + d
                displayString = newRaw
            }
        }
    }

    fun onDot() {
        onBeadClick()
        if (isNewEntry) {
            displayString = "0."
            isNewEntry = false
        } else {
            val raw = NumberFormatter.stripCommas(displayString)
            if (!raw.contains(".")) {
                displayString = "$raw."
            }
        }
    }

    fun onBackspace() {
        onBeadClick()
        val raw = NumberFormatter.stripCommas(displayString)
        if (raw.length > 1) {
            displayString = raw.dropLast(1)
        } else {
            displayString = "0"
            isNewEntry = true
        }
    }

    fun onClear() {
        onBeadClick()
        displayString = "0"
        storedOperand = null
        activeOp = null
        isNewEntry = true
        statusNote = "Cleared. Ready for exact 20-digit calculations."
    }

    fun onOperator(op: String) {
        onBeadClick()
        storedOperand = NumberFormatter.stripCommas(displayString)
        activeOp = op
        isNewEntry = true
        statusNote = "Stored $op. Enter next number."
    }

    fun onCalculate() {
        onBeadClick()
        val op1 = storedOperand
        val current = NumberFormatter.stripCommas(displayString)
        val op = activeOp
        if (op1 != null && op != null) {
            val result = when (op) {
                "+" -> NumberFormatter.addBigInt(op1, current)
                "-" -> NumberFormatter.subtractBigInt(op1, current)
                "×" -> NumberFormatter.multiplyBigInt(op1, current)
                "÷" -> NumberFormatter.divideBigInt(op1, current)
                else -> current
            }
            displayString = result
            statusNote = "Exact BigInteger: $op1 $op $current = $result"
            storedOperand = null
            activeOp = null
            isNewEntry = true
        }
    }

    fun onSquare() {
        onBeadClick()
        val current = NumberFormatter.stripCommas(displayString)
        val result = NumberFormatter.squareBigInt(current)
        displayString = result
        statusNote = "BigInteger Square: ($current)² = $result"
        isNewEntry = true
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // BigInt Display with Indian number grouping
            Surface(
                color = Color(0xFF1E1E28),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = statusNote,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = NumberFormatter.formatIndianNumber(displayString),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${NumberFormatter.stripCommas(displayString).replace(".", "").length} digits",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFFD54F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Keypad
            val bigButtons = listOf(
                listOf("C", "⌫", "x²", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("0", ".", "=")
            )

            bigButtons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { key ->
                        val weight = if (key == "=") 2f else 1f
                        val isOp = key in listOf("÷", "×", "-", "+", "=", "x²")
                        val isClear = key in listOf("C", "⌫")
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                key == "=" -> theme.primary
                                isOp -> theme.primary.copy(alpha = 0.15f)
                                isClear -> Color(0xFFFFEBEE)
                                else -> Color(0xFFF2F2F5)
                            },
                            modifier = Modifier
                                .weight(weight)
                                .height(46.dp)
                                .clickable {
                                    when (key) {
                                        "C" -> onClear()
                                        "⌫" -> onBackspace()
                                        "x²" -> onSquare()
                                        "." -> onDot()
                                        "=" -> onCalculate()
                                        "÷", "×", "-", "+" -> onOperator(key)
                                        else -> onDigit(key)
                                    }
                                }
                                .testTag("bigint_btn_$key")
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        key == "=" -> Color.White
                                        isOp -> theme.primary
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
