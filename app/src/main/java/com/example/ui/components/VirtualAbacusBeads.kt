package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BeadSkinConfig(
    val frameColor: Color,
    val beamColor: Color,
    val rodColor: Color,
    val beadBrush: Brush,
    val beadBorder: Color,
    val textColor: Color
)

object AbacusSkins {
    fun getSkin(skinName: String): BeadSkinConfig {
        return when (skinName) {
            "Neon Cyberpunk" -> BeadSkinConfig(
                frameColor = Color(0xFF12121F),
                beamColor = Color(0xFF00E5FF),
                rodColor = Color(0xFF00E5FF).copy(alpha = 0.5f),
                beadBrush = Brush.linearGradient(listOf(Color(0xFFFF007F), Color(0xFFD500F9))),
                beadBorder = Color(0xFF00E5FF),
                textColor = Color(0xFF00E5FF)
            )
            "Ruby Flame" -> BeadSkinConfig(
                frameColor = Color(0xFF4A0E17),
                beamColor = Color(0xFFC62828),
                rodColor = Color(0xFFFFCDD2),
                beadBrush = Brush.linearGradient(listOf(Color(0xFFFF1744), Color(0xFFB71C1C))),
                beadBorder = Color(0xFFFF8A80),
                textColor = Color(0xFFFF1744)
            )
            "Jade Lotus" -> BeadSkinConfig(
                frameColor = Color(0xFF103318),
                beamColor = Color(0xFF2E7D32),
                rodColor = Color(0xFFC8E6C9),
                beadBrush = Brush.linearGradient(listOf(Color(0xFF00E676), Color(0xFF1B5E20))),
                beadBorder = Color(0xFFB9F6CA),
                textColor = Color(0xFF00C853)
            )
            "Obsidian Shadow" -> BeadSkinConfig(
                frameColor = Color(0xFF1A1A1A),
                beamColor = Color(0xFF424242),
                rodColor = Color(0xFF9E9E9E),
                beadBrush = Brush.linearGradient(listOf(Color(0xFFB0BEC5), Color(0xFF37474F))),
                beadBorder = Color(0xFFECEFF1),
                textColor = Color(0xFFECEFF1)
            )
            "Classic Wooden" -> BeadSkinConfig(
                frameColor = Color(0xFF5D4037),
                beamColor = Color(0xFF3E2723),
                rodColor = Color(0xFFBCAAA4),
                beadBrush = Brush.linearGradient(listOf(Color(0xFFFFB300), Color(0xFF8D6E63))),
                beadBorder = Color(0xFFFFE082),
                textColor = Color(0xFF4E342E)
            )
            else -> BeadSkinConfig( // Gold Imperial
                frameColor = Color(0xFF3E2723),
                beamColor = Color(0xFFFFB300),
                rodColor = Color(0xFFFFE082),
                beadBrush = Brush.linearGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))),
                beadBorder = Color(0xFFFFF9C4),
                textColor = Color(0xFFE65100)
            )
        }
    }
}

@Composable
fun VirtualAbacus(
    skinName: String = "Gold Imperial",
    onBeadClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val skin = AbacusSkins.getSkin(skinName)
    // 4 rods (Thousands, Hundreds, Tens, Units)
    var rodUpper by remember { mutableStateOf(listOf(false, false, true, false)) }
    var rodLower by remember { mutableStateOf(listOf(2, 4, 1, 3)) }

    val calculatedValue = remember(rodUpper, rodLower) {
        val uVal = rodUpper.map { if (it) 5 else 0 }
        val thousands = (uVal[0] + rodLower[0]) * 1000
        val hundreds = (uVal[1] + rodLower[1]) * 100
        val tens = (uVal[2] + rodLower[2]) * 10
        val units = (uVal[3] + rodLower[3])
        thousands + hundreds + tens + units
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = skin.frameColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, skin.beamColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ABACUS: $skinName",
                    style = MaterialTheme.typography.labelSmall,
                    color = skin.textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Display: $calculatedValue",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Abacus Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Th", "H", "T", "U").forEachIndexed { rodIndex, label ->
                        AbacusRod(
                            label = label,
                            skin = skin,
                            upperActive = rodUpper[rodIndex],
                            lowerActiveCount = rodLower[rodIndex],
                            onUpperClick = {
                                rodUpper = rodUpper.toMutableList().also { it[rodIndex] = !it[rodIndex] }
                                onBeadClicked()
                            },
                            onLowerClick = { beadPos ->
                                rodLower = rodLower.toMutableList().also {
                                    it[rodIndex] = if (it[rodIndex] == beadPos + 1) beadPos else beadPos + 1
                                }
                                onBeadClicked()
                            }
                        )
                    }
                }

                // Central Beam Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 52.dp)
                        .background(skin.beamColor)
                        .shadow(2.dp)
                )
            }
        }
    }
}

@Composable
private fun AbacusRod(
    label: String,
    skin: BeadSkinConfig,
    upperActive: Boolean,
    lowerActiveCount: Int,
    onUpperClick: () -> Unit,
    onLowerClick: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp).fillMaxHeight()
    ) {
        // Upper Deck (5s bead)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(48.dp)
                .background(skin.rodColor),
            contentAlignment = if (upperActive) Alignment.BottomCenter else Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(skin.beadBrush)
                    .border(1.dp, skin.beadBorder, RoundedCornerShape(8.dp))
                    .clickable { onUpperClick() }
            )
        }

        // Space for beam divider
        Spacer(modifier = Modifier.height(12.dp))

        // Lower Deck (1s beads - 4 beads)
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(96.dp)
                .background(skin.rodColor)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Lower active beads pushed UP to beam
                for (b in 0 until lowerActiveCount) {
                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 18.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(skin.beadBrush)
                            .border(1.dp, skin.beadBorder, RoundedCornerShape(8.dp))
                            .clickable { onLowerClick(b) }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Inactive beads resting at bottom
                for (b in lowerActiveCount until 4) {
                    Box(
                        modifier = Modifier
                            .size(width = 44.dp, height = 18.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(skin.beadBrush.let { skin.beadBrush })
                            .border(1.dp, skin.beadBorder, RoundedCornerShape(8.dp))
                            .clickable { onLowerClick(b) }
                    )
                }
            }
        }

        Text(
            text = label,
            color = skin.textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
