package com.example.ui.student

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvatarItem
import com.example.data.model.UserProfile
import com.example.ui.components.RobloxAvatarFigure
import com.example.ui.theme.ArenaTheme

enum class AvatarCategoryTab(val label: String) {
    SHIRT("Shirts"),
    PANTS("Pants"),
    ACCESSORY("Accessories")
}

@Composable
fun AvatarCustomizerScreen(
    profile: UserProfile,
    avatarItems: List<AvatarItem>,
    theme: ArenaTheme,
    onEquipItem: (category: String, itemName: String) -> Unit,
    onPurchaseItem: (cost: Int, item: AvatarItem) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(AvatarCategoryTab.SHIRT) }

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
                text = "AVATAR WARDROBE",
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
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${profile.tokens}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF5D4037))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large 3D Avatar Wardrobe Preview Stage
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${profile.name}'s Avatar Wardrobe (Tap to Jump!)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E24)
                )

                Spacer(modifier = Modifier.height(6.dp))

                RobloxAvatarFigure(
                    shirtName = profile.equippedShirt,
                    pantsName = profile.equippedPants,
                    accessoryName = profile.equippedAccessory,
                    scale = 1.15f
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEDE7F6)) {
                        Text(profile.equippedShirt, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF512DA8), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE0F2F1)) {
                        Text(profile.equippedPants, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF00695C), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFFFF8E1)) {
                        Text(profile.equippedAccessory, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE65100), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Category Tab Bar
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                AvatarCategoryTab.entries.forEach { tab ->
                    val isSel = selectedCategory == tab
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) theme.primary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedCategory = tab }
                            .testTag("avatar_tab_${tab.name.lowercase()}")
                    ) {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else Color(0xFF555566),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Items in selected category
        val categoryFilter = selectedCategory.name
        val filteredItems = avatarItems.filter { it.category == categoryFilter }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredItems) { item ->
                val isEquipped = when (item.category) {
                    "SHIRT" -> profile.equippedShirt == item.name
                    "PANTS" -> profile.equippedPants == item.name
                    "ACCESSORY" -> profile.equippedAccessory == item.name
                    else -> false
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isEquipped) 2.dp else 1.dp,
                        if (isEquipped) theme.primary else Color(0xFFEEEEF2)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("avatar_item_${item.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(item.previewColorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (item.category) {
                                    "SHIRT" -> Icons.Default.Checkroom
                                    "PANTS" -> Icons.Default.AirlineSeatLegroomNormal
                                    else -> Icons.Default.AutoAwesome
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(item.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 10.sp)
                            if (item.isTeacherExclusive) {
                                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEDE7F6)) {
                                    Text("Teacher Exclusive (Conferred by Teacher)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF512DA8), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (isEquipped) {
                            FilledTonalButton(
                                onClick = {},
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = theme.primary.copy(alpha = 0.12f), contentColor = theme.primary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Equipped", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (item.isUnlocked) {
                            Button(
                                onClick = {
                                    onEquipItem(item.category, item.name)
                                    Toast.makeText(context, "Equipped ${item.name}!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (item.isTeacherExclusive) {
                            Text(
                                text = "Ask Teacher",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6A1B9A)
                            )
                        } else {
                            Button(
                                onClick = {
                                    if (profile.tokens >= item.costTokens) {
                                        onPurchaseItem(item.costTokens, item)
                                        Toast.makeText(context, "Unlocked ${item.name}!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Need ${item.costTokens - profile.tokens} more tokens!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("${item.costTokens} T", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
