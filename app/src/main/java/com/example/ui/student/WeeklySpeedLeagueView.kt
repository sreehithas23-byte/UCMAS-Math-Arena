package com.example.ui.student

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
import com.example.data.model.SpeedLeagueEntry
import com.example.ui.theme.ArenaTheme

@Composable
fun WeeklySpeedLeagueView(
    entries: List<SpeedLeagueEntry>,
    theme: ArenaTheme,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "WEEKLY SPEED LEAGUE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.primary
                )
                Text(
                    text = "Ranked calculation response time & accuracy",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tier Ladder Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E1E28),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ACTIVE TIER: DIAMOND LEAGUE", color = Color(0xFF00E5FF), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("Resets in: 2d 14h", color = Color(0xFFFFD54F), style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Bronze" to Color(0xFFCD7F32), "Silver" to Color(0xFFC0C0C0), "Gold" to Color(0xFFFFD700), "Platinum" to Color(0xFF00E5FF), "Diamond" to Color(0xFFE040FB)).forEach { (t, col) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(col.copy(alpha = 0.2f)).border(1.5.dp, col, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = col, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(t, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("League Leaderboard (Class VI-A)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries) { entry ->
                val isMe = entry.isCurrentUser
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isMe) theme.primary.copy(alpha = 0.1f) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isMe) 2.dp else 1.dp,
                        if (isMe) theme.primary else Color(0xFFE8E8EE)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("league_rank_${entry.rank}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rank Badge
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    when (entry.rank) {
                                        1 -> Color(0xFFFFD700)
                                        2 -> Color(0xFFC0C0C0)
                                        3 -> Color(0xFFCD7F32)
                                        else -> Color(0xFFEEEEF2)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${entry.rank}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = if (entry.rank <= 3) Color(0xFF3E2723) else Color(0xFF555566)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = entry.name + (if (isMe) " (You)" else ""),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isMe) FontWeight.ExtraBold else FontWeight.Bold,
                                    color = if (isMe) theme.primary else Color(0xFF1E1E24)
                                )
                            }
                            Text(
                                text = "${entry.tier} Tier • ${entry.accuracy}% Accuracy",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }

                        // Speed Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "${entry.avgSpeedMs} ms",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
