package com.example.ui.student

import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArenaTheme

data class ShopItem(
    val id: String,
    val name: String,
    val category: String, // "Bead Skin", "Badge"
    val cost: Int,
    val description: String,
    val iconColor: Color
)

@Composable
fun RewardShopView(
    theme: ArenaTheme,
    userTokens: Int,
    currentEquippedSkin: String,
    onEquipSkin: (String) -> Unit,
    onPurchaseItem: (cost: Int, name: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val items = remember {
        listOf(
            ShopItem("skin_gold", "Gold Imperial", "Bead Skin", 0, "Dazzling amber beads with golden frame", Color(0xFFFFD700)),
            ShopItem("skin_neon", "Neon Cyberpunk", "Bead Skin", 300, "Vibrant cyan rods with magenta neon beads", Color(0xFF00E5FF)),
            ShopItem("skin_ruby", "Ruby Flame", "Bead Skin", 350, "Crimson flame chassis with ruby crystal beads", Color(0xFFFF1744)),
            ShopItem("skin_jade", "Jade Lotus", "Bead Skin", 250, "Emerald bamboo frame with jade stone beads", Color(0xFF00E676)),
            ShopItem("skin_obsidian", "Obsidian Shadow", "Bead Skin", 400, "Sleek matte obsidian with metallic silver beads", Color(0xFF90A4AE)),
            ShopItem("skin_wood", "Classic Wooden", "Bead Skin", 150, "Authentic traditional mahogany abacus texture", Color(0xFF8D6E63)),
            ShopItem("badge_lightning", "Lightning Mind", "Badge", 200, "Sub-250ms calculation speed profile insignia", Color(0xFFFFEB3B)),
            ShopItem("badge_prodigy", "Formula Prodigy", "Badge", 250, "Certified master of all Friend of 10 combinations", Color(0xFF9C27B0)),
            ShopItem("badge_century", "Century Club", "Badge", 500, "Awarded for 100+ error-free speed drills", Color(0xFFFF9800)),
            ShopItem("badge_speed", "Speed Demon", "Badge", 300, "Exclusive golden aura badge in Weekly Speed League", Color(0xFFF44336))
        )
    }

    var ownedSkins by remember { mutableStateOf(setOf("Gold Imperial")) }
    var ownedBadges by remember { mutableStateOf(setOf<String>()) }

    fun buyOrEquip(item: ShopItem) {
        if (item.category == "Bead Skin") {
            if (ownedSkins.contains(item.name)) {
                onEquipSkin(item.name)
                Toast.makeText(context, "Equipped ${item.name} skin!", Toast.LENGTH_SHORT).show()
            } else {
                if (userTokens >= item.cost) {
                    onPurchaseItem(item.cost, "Purchased Skin: ${item.name}")
                    ownedSkins = ownedSkins + item.name
                    onEquipSkin(item.name)
                    Toast.makeText(context, "Purchased & equipped ${item.name}!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Need ${item.cost - userTokens} more tokens!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            if (!ownedBadges.contains(item.name)) {
                if (userTokens >= item.cost) {
                    onPurchaseItem(item.cost, "Purchased Badge: ${item.name}")
                    ownedBadges = ownedBadges + item.name
                    Toast.makeText(context, "Unlocked ${item.name} badge!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Need ${item.cost - userTokens} more tokens!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "UCMAS REWARD SHOP",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = theme.primary
                    )
                    Text("Unlock skins, badges & themes", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF8E1),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$userTokens", fontWeight = FontWeight.ExtraBold, color = Color(0xFF5D4037))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { item ->
                val isSkin = item.category == "Bead Skin"
                val isOwned = if (isSkin) ownedSkins.contains(item.name) else ownedBadges.contains(item.name)
                val isEquipped = isSkin && currentEquippedSkin == item.name

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isEquipped) 2.dp else 1.dp,
                        if (isEquipped) theme.primary else Color(0xFFEAEAEA)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("shop_item_${item.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(item.iconColor.copy(alpha = 0.2f))
                                .border(1.5.dp, item.iconColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSkin) Icons.Default.Casino else Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = item.iconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1E24)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isSkin) Color(0xFFEDE7F6) else Color(0xFFE0F2F1)
                                ) {
                                    Text(
                                        text = item.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = if (isSkin) Color(0xFF512DA8) else Color(0xFF00695C),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666677),
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Action Button
                        if (isEquipped) {
                            FilledTonalButton(
                                onClick = {},
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = theme.primary.copy(alpha = 0.12f), contentColor = theme.primary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Equipped", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (isOwned && isSkin) {
                            Button(
                                onClick = { buyOrEquip(item) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (isOwned) {
                            Text("Unlocked", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        } else {
                            Button(
                                onClick = { buyOrEquip(item) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("${item.cost} T", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
