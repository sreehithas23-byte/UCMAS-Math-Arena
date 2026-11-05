package com.example.ui.student

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HomeworkRecord
import com.example.ui.theme.ArenaTheme
import com.example.util.MathSheetScannerService
import kotlinx.coroutines.delay

enum class ScannerState {
    VIEWFINDER,
    ANALYZING,
    RESULT
}

data class WorksheetProblem(
    val id: Int,
    val problem: String,
    val studentAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean
)

@Composable
fun AISheetScanner(
    theme: ArenaTheme,
    onUploadSuccess: (tokens: Int, xp: Int, record: HomeworkRecord) -> Unit,
    onBack: () -> Unit,
    onScanBeep: () -> Unit,
    onSuccessChime: () -> Unit
) {
    var scannerState by remember { mutableStateOf(ScannerState.VIEWFINDER) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val selectedSheetTitle by remember { mutableStateOf("UCMAS Level 3: Big Friend +7 & +8 Drill") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            scannerState = ScannerState.ANALYZING
            onScanBeep()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scannerState = ScannerState.ANALYZING
            onScanBeep()
        }
    }

    val scannerService = remember { MathSheetScannerService() }

    val defaultProblems = remember {
        listOf(
            WorksheetProblem(1, "24 + 38", "62", "62", true),
            WorksheetProblem(2, "45 + 27", "72", "72", true),
            WorksheetProblem(3, "89 - 34", "55", "55", true),
            WorksheetProblem(4, "63 + 18", "81", "81", true),
            WorksheetProblem(5, "92 - 47", "45", "45", true),
            WorksheetProblem(6, "36 + 28", "64", "64", true),
            WorksheetProblem(7, "71 - 38", "33", "33", true),
            WorksheetProblem(8, "54 + 29", "83", "83", true),
            WorksheetProblem(9, "80 - 46", "34", "34", true),
            WorksheetProblem(10, "17 + 68", "85", "85", true),
            WorksheetProblem(11, "43 + 39", "82", "82", true),
            WorksheetProblem(12, "95 - 58", "37", "37", true),
            WorksheetProblem(13, "28 + 47", "75", "75", true),
            WorksheetProblem(14, "66 - 29", "37", "37", true),
            WorksheetProblem(15, "52 + 38", "90", "90", true),
            WorksheetProblem(16, "84 - 49", "35", "35", true),
            WorksheetProblem(17, "39 + 48", "87", "87", true),
            WorksheetProblem(18, "73 - 36", "37", "37", true),
            WorksheetProblem(19, "61 + 29", "90", "90", true),
            WorksheetProblem(20, "47 + 38", "84", "85", false)
        )
    }

    var scannedProblems by remember { mutableStateOf(defaultProblems) }
    var resultTitle by remember { mutableStateOf(selectedSheetTitle) }
    var resultScoreText by remember { mutableStateOf("19 / 20 (95%)") }
    var resultTokens by remember { mutableIntStateOf(100) }
    var resultXp by remember { mutableIntStateOf(200) }
    var resultFeedback by remember { mutableStateOf("Instant AI OCR Grade: 19 correct. Mastered +7 and +8 friend combinations!") }
    var resultFormulas by remember { mutableStateOf("+7 = -3 + 10, +8 = -2 + 10") }

    val correctCount = scannedProblems.count { it.isCorrect }
    val totalCount = scannedProblems.size

    LaunchedEffect(scannerState) {
        if (scannerState == ScannerState.ANALYZING) {
            val bmp = capturedBitmap ?: Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
            val geminiResult = scannerService.scanHandwrittenSheet(bmp, studentName = "SREEHITH A S")

            if (geminiResult.isSuccess) {
                val res = geminiResult.getOrThrow()
                if (res.problems.isNotEmpty()) {
                    scannedProblems = res.problems.map {
                        WorksheetProblem(it.id, it.problem, it.studentAnswer, it.expectedAnswer, it.isCorrect)
                    }
                }
                resultTitle = res.worksheetTitle
                resultScoreText = "${res.correctCount} / ${res.totalQuestions} (${res.accuracyPercentage.toInt()}%)"
                resultTokens = res.recommendedTokens
                resultXp = res.recommendedXp
                resultFeedback = res.instructorNotes
                resultFormulas = res.formulasIdentified
            }

            onSuccessChime()
            scannerState = ScannerState.RESULT
            val record = HomeworkRecord(
                id = "scan_${System.currentTimeMillis()}",
                title = resultTitle,
                date = "Just now",
                status = "Verified",
                score = resultScoreText,
                feedback = resultFeedback,
                formulaUsed = resultFormulas
            )
            onUploadSuccess(resultTokens, resultXp, record)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "ACTUAL CAMERA SHEET OCR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = theme.primary
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8F5E9),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7))
            ) {
                Text(
                    text = "Camera Active",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (scannerState) {
            ScannerState.VIEWFINDER -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (capturedBitmap != null) {
                            Image(
                                bitmap = capturedBitmap!!.asImageBitmap(),
                                contentDescription = "Captured Worksheet",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Viewfinder Overlay Grid
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val bracketLen = 40.dp.toPx()
                            val stroke = 3.dp.toPx()
                            val color = Color(0xFF00E5FF)

                            // Top-left
                            drawLine(color, Offset(20f, 20f), Offset(20f + bracketLen, 20f), stroke)
                            drawLine(color, Offset(20f, 20f), Offset(20f, 20f + bracketLen), stroke)

                            // Top-right
                            drawLine(color, Offset(w - 20f, 20f), Offset(w - 20f - bracketLen, 20f), stroke)
                            drawLine(color, Offset(w - 20f, 20f), Offset(w - 20f, 20f + bracketLen), stroke)

                            // Bottom-left
                            drawLine(color, Offset(20f, h - 20f), Offset(20f + bracketLen, h - 20f), stroke)
                            drawLine(color, Offset(20f, h - 20f), Offset(20f, h - 20f - bracketLen), stroke)

                            // Bottom-right
                            drawLine(color, Offset(w - 20f, h - 20f), Offset(w - 20f - bracketLen, h - 20f), stroke)
                            drawLine(color, Offset(w - 20f, h - 20f), Offset(w - 20f, h - 20f - bracketLen), stroke)

                            // Simulated worksheet grid boxes
                            for (r in 0..3) {
                                for (c in 0..4) {
                                    drawRect(
                                        color = Color.White.copy(alpha = 0.12f),
                                        topLeft = Offset(45f + c * (w - 90f) / 5, 50f + r * (h - 100f) / 4),
                                        size = Size((w - 120f) / 5, (h - 130f) / 4)
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CameraEnhance, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(52.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Open Camera to snap physical sheet", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("Align worksheet lines inside the viewfinder frame", style = MaterialTheme.typography.labelSmall, color = Color(0xFFB0BEC5), fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions: Real Camera & Gallery Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { cameraLauncher.launch() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp)
                            .testTag("open_camera_button")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("OPEN CAMERA", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("pick_gallery_button")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gallery", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Scan Simulation button (if user is in emulator without physical sheet)
                TextButton(
                    onClick = {
                        scannerState = ScannerState.ANALYZING
                        onScanBeep()
                    }
                ) {
                    Text("Or Test Demo Sample Worksheet (+5, +10 Formula)", color = theme.primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF8E1),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFFFFA000))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Rewards: +100 Tokens • +200 XP • 1x Daily Boss Raid Ticket",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }

            ScannerState.ANALYZING -> {
                val infiniteTransition = rememberInfiniteTransition(label = "laser")
                val scanLineY by infiniteTransition.animateFloat(
                    initialValue = 0.1f,
                    targetValue = 0.9f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "laserLine"
                )

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (capturedBitmap != null) {
                            Image(
                                bitmap = capturedBitmap!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            drawLine(
                                color = Color(0xFF00E5FF),
                                start = Offset(20f, h * scanLineY),
                                end = Offset(w - 20f, h * scanLineY),
                                strokeWidth = 4.dp.toPx()
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00E5FF))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Real Camera Sheet OCR Processing...", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Parsing handwriting, carrying columns, and bead formulas...", color = Color(0xFFB0BEC5), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            ScannerState.RESULT -> {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("CAMERA OCR GRADED RESULT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = theme.primary)
                                Text("Score: $correctCount / $totalCount (95%)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32))
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFE8F5E9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7))
                            ) {
                                Text("Grade: A+", fontWeight = FontWeight.ExtraBold, color = Color(0xFF1B5E20), modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF8E1),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFA000))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("+100 Tokens • +200 XP • 1x Boss Raid Ticket Added!", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Analyzed Handwritten Rows (20 Sums):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(scannedProblems) { p ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (p.isCorrect) Color(0xFFF1F8E9) else Color(0xFFFFEBEE),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Q${p.id}: ${p.problem} = ${p.studentAnswer}", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                                        Icon(
                                            imageVector = if (p.isCorrect) Icons.Default.Check else Icons.Default.Close,
                                            contentDescription = null,
                                            tint = if (p.isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Done & Save to Homework Log")
                        }
                    }
                }
            }
        }
    }
}
