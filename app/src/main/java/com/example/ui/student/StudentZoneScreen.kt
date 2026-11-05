package com.example.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AvatarItem
import com.example.data.model.HomeworkRecord
import com.example.data.model.SpeedLeagueEntry
import com.example.data.model.UserProfile
import com.example.ui.components.RobloxAvatarFigure
import com.example.ui.components.VirtualAbacus
import com.example.ui.theme.ArenaTheme

enum class StudentSubScreen {
    HUB,
    PVP_DUEL,
    BOSS_RAID,
    SPEED_LEAGUE,
    FLASH_ARCADE,
    AI_SCANNER,
    SHOP,
    AVATAR_CUSTOMIZER,
    PERFORMANCE_ANALYTICS,
    MENTAL_MATH_TRAINER
}

@Composable
fun StudentZoneScreen(
    profile: UserProfile,
    avatarItems: List<AvatarItem>,
    theme: ArenaTheme,
    speedLeague: List<SpeedLeagueEntry>,
    analytics: com.example.data.model.AnalyticsRecord? = null,
    onAddReward: (title: String, tokens: Int, xp: Int, category: String, addTicket: Boolean) -> Unit,
    onSubmitHomework: (HomeworkRecord) -> Unit,
    onEquipSkin: (String) -> Unit,
    onEquipAvatarItem: (category: String, name: String) -> Unit,
    onPurchaseAvatarItem: (cost: Int, item: AvatarItem) -> Unit,
    onSubScreenChange: (isSubScreenOpen: Boolean) -> Unit,
    onBeadClick: () -> Unit,
    onCorrectChime: () -> Unit,
    onErrorBuzz: () -> Unit,
    onFlashTick: () -> Unit,
    onCoinSound: () -> Unit,
    onTriggerVictoryParticles: () -> Unit,
    modifier: Modifier = Modifier,
    onRecordDuel: ((opponentName: String, opponentTier: String, playerScore: Int, opponentScore: Int, totalRounds: Int, correctAnswers: Int, playerAccuracy: Float, avgLatencyMs: Long, maxCombo: Int, isWin: Boolean) -> Unit)? = null,
    onOpenICardDetails: (() -> Unit)? = null,
    onOpenAttendanceSheet: (() -> Unit)? = null,
    onOpenParentPortal: (() -> Unit)? = null,
    onRecordTrainingSession: ((sessionType: String, drillName: String, formulaCategory: String, digitCount: Int, rowCount: Int, flashSpeedMs: Int, durationSeconds: Int, attempted: Int, correct: Int, accuracy: Float, avgResponseMs: Long, tokens: Int, xp: Int, highestCombo: Int, score: Int, rating: String) -> Unit)? = null
) {
    var subScreen by remember { mutableStateOf(StudentSubScreen.HUB) }
    var selectedPillTab by remember { mutableStateOf("Holiday") }

    LaunchedEffect(subScreen) {
        onSubScreenChange(subScreen != StudentSubScreen.HUB)
    }

    androidx.activity.compose.BackHandler(enabled = subScreen != StudentSubScreen.HUB) {
        subScreen = StudentSubScreen.HUB
    }

    when (subScreen) {
        StudentSubScreen.HUB -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 8-Circle School Action Grid matching Screenshot 2
                item(key = "school_quick_action_grid") {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3E5E5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("school_8_circle_grid")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Row 1: Holiday, Userapplication, ICard Details, Attendance
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                SchoolCircleActionItem(
                                    label = "Holiday",
                                    badgeText = "H",
                                    onClick = { selectedPillTab = "Holiday" }
                                )
                                SchoolCircleActionItem(
                                    label = "Userapplication",
                                    icon = Icons.Default.Assignment,
                                    onClick = { subScreen = StudentSubScreen.AI_SCANNER }
                                )
                                SchoolCircleActionItem(
                                    label = "ICard Details",
                                    icon = Icons.Default.FormatListBulleted,
                                    onClick = { onOpenICardDetails?.invoke() }
                                )
                                SchoolCircleActionItem(
                                    label = "Attendance",
                                    icon = Icons.Default.Checklist,
                                    onClick = {
                                        selectedPillTab = "Attendance"
                                        onOpenAttendanceSheet?.invoke()
                                    }
                                )
                            }

                            // Row 2: Time Table, Ecampus, Event Calendar, Parent Profile
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                SchoolCircleActionItem(
                                    label = "Time Table",
                                    icon = Icons.Default.AccessTime,
                                    onClick = { selectedPillTab = "Class Work" }
                                )
                                SchoolCircleActionItem(
                                    label = "Ecampus",
                                    icon = Icons.Default.Psychology,
                                    onClick = { subScreen = StudentSubScreen.MENTAL_MATH_TRAINER }
                                )
                                SchoolCircleActionItem(
                                    label = "Event Calendar",
                                    icon = Icons.Default.CalendarMonth,
                                    onClick = { selectedPillTab = "Events" }
                                )
                                SchoolCircleActionItem(
                                    label = "Parent Profile",
                                    icon = Icons.Default.Group,
                                    onClick = { onOpenParentPortal?.invoke() }
                                )
                            }
                        }
                    }
                }

                // Segmented Pill Tab Bar matching Screenshot 2: [ Holiday ] [ Events ] [ Attendance ] [ Class Work ]
                item(key = "school_segmented_pill_tabs") {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E0E0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("school_segmented_pill_bar")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("Holiday", "Events", "Attendance", "Class Work").forEach { tabName ->
                                val isSelected = selectedPillTab == tabName
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) Color(0xFFE53935) else Color.Transparent)
                                        .clickable { selectedPillTab = tabName }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tabName,
                                        color = if (isSelected) Color.White else Color(0xFF555566),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Attached Red Date Ribbon Cards matching Screenshot 2
                when (selectedPillTab) {
                    "Holiday" -> {
                        item {
                            AttachedRibbonCard(
                                dateText = "Tue 09 Jun",
                                tagText = "Holiday",
                                title = "vacation",
                                subtitle = "09 Jun To 13 Jun",
                                icon = Icons.Default.BeachAccess
                            )
                        }
                        item {
                            AttachedRibbonCard(
                                dateText = "Sat 20 Jun",
                                tagText = "Holiday",
                                title = "school Event",
                                subtitle = "Abacus Summer Fest & Anzan Championship",
                                icon = Icons.Default.Celebration
                            )
                        }
                        item {
                            AttachedRibbonCard(
                                dateText = "Sun 21 Jun",
                                tagText = "Holiday",
                                title = "Public holiday",
                                subtitle = "International Yoga & Solstice Day",
                                icon = Icons.Default.SelfImprovement
                            )
                        }
                    }
                    "Events" -> {
                        item {
                            AttachedRibbonCard(
                                dateText = "Sat 04 Jul",
                                tagText = "Event",
                                title = "National Anzan Duel Open",
                                subtitle = "Multi-arena speed arithmetic tournament",
                                icon = Icons.Default.SportsEsports
                            )
                        }
                        item {
                            AttachedRibbonCard(
                                dateText = "Sun 12 Jul",
                                tagText = "Event",
                                title = "Faculty & Parent Conclave",
                                subtitle = "Interactive student evaluation review",
                                icon = Icons.Default.Groups
                            )
                        }
                    }
                    "Attendance" -> {
                        item {
                            AttachedRibbonCard(
                                dateText = "Today",
                                tagText = "Attendance",
                                title = "Weekly Batch Attendance: 98.4%",
                                subtitle = "Current continuous streak: 14 sessions",
                                icon = Icons.Default.CheckCircle
                            )
                        }
                        item {
                            AttachedRibbonCard(
                                dateText = "Recent",
                                tagText = "Attendance",
                                title = "Saturday Batch - Present",
                                subtitle = "Arrived on time • 2h session logged",
                                icon = Icons.Default.EventAvailable
                            )
                        }
                    }
                    "Class Work" -> {
                        item {
                            AttachedRibbonCard(
                                dateText = "Active",
                                tagText = "Class Work",
                                title = "Two-Digit Mental Addition",
                                subtitle = "Module 4 • Speed Drills Sheet #12",
                                icon = Icons.Default.MenuBook
                            )
                        }
                        item {
                            AttachedRibbonCard(
                                dateText = "Practice",
                                tagText = "Class Work",
                                title = "Flash Anzan 3-Digit Series",
                                subtitle = "Target: 5 rounds under 600ms latency",
                                icon = Icons.Default.Bolt
                            )
                        }
                    }
                }

                // Interactive Virtual Abacus
                item {
                    VirtualAbacus(
                        skinName = profile.activeSkin,
                        onBeadClicked = onBeadClick
                    )
                }

                // Roblox Figure Preview Banner
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F4)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { subScreen = StudentSubScreen.AVATAR_CUSTOMIZER }
                            .testTag("open_avatar_wardrobe_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(theme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                RobloxAvatarFigure(
                                    shirtName = profile.equippedShirt,
                                    pantsName = profile.equippedPants,
                                    accessoryName = profile.equippedAccessory,
                                    scale = 0.4f
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Avatar Wardrobe", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Customize shirts, pants & teacher awards", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Equipped: ${profile.equippedShirt}", fontSize = 10.sp, color = theme.primary, fontWeight = FontWeight.SemiBold)
                            }

                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }

                // Section Title
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(theme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GAME MODES & PRACTICE",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = theme.primary,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Mental Math Trainer (Featured Premier Mode)
                item {
                    GameModeCard(
                        title = "Mental Math Trainer",
                        subtitle = "Progressive difficulty stages, bead technique rules & flame streak multiplier",
                        badge = "🔥 Premier Arena",
                        icon = Icons.Default.Psychology,
                        iconBg = Color(0xFFD32F2F),
                        testTag = "mode_mental_math_trainer",
                        onClick = { subScreen = StudentSubScreen.MENTAL_MATH_TRAINER }
                    )
                }

                // 1-v-1 Anzan Duels (PvP)
                item {
                    GameModeCard(
                        title = "1-v-1 Anzan Duels (PvP)",
                        subtitle = "Real-time timed mental arithmetic duel with split screens",
                        badge = "+25 Tokens",
                        icon = Icons.Default.FlashOn,
                        iconBg = Color(0xFFD32F2F),
                        testTag = "mode_pvp_duels",
                        onClick = { subScreen = StudentSubScreen.PVP_DUEL }
                    )
                }

                // Boss Raid Battles (PvE)
                item {
                    GameModeCard(
                        title = "Boss Raid Battles (PvE)",
                        subtitle = "Multi-stage bosses fought with rapid mental math attacks",
                        badge = "Stage 1: Conqueror",
                        icon = Icons.Default.Shield,
                        iconBg = Color(0xFF6A1B9A),
                        testTag = "mode_boss_raid",
                        onClick = { subScreen = StudentSubScreen.BOSS_RAID }
                    )
                }

                // Weekly Speed League
                item {
                    GameModeCard(
                        title = "Weekly Speed League",
                        subtitle = "Ranked ladder system tracking response latency in ms",
                        badge = "Diamond Tier",
                        icon = Icons.Default.Leaderboard,
                        iconBg = Color(0xFF0097A7),
                        testTag = "mode_speed_league",
                        onClick = { subScreen = StudentSubScreen.SPEED_LEAGUE }
                    )
                }

                // Flash Calculation Arcade
                item {
                    GameModeCard(
                        title = "Flash Calculation Arcade",
                        subtitle = "Endless survival mode with flashing millisecond numbers",
                        badge = "Combo Mult.",
                        icon = Icons.Default.Bolt,
                        iconBg = Color(0xFFE65100),
                        testTag = "mode_flash_arcade",
                        onClick = { subScreen = StudentSubScreen.FLASH_ARCADE }
                    )
                }

                // AI Sheet Scanner
                item {
                    GameModeCard(
                        title = "Actual Camera Sheet OCR",
                        subtitle = "Snap physical worksheet or upload from gallery for auto-grading",
                        badge = "+100 Tokens + Boss Ticket",
                        icon = Icons.Default.DocumentScanner,
                        iconBg = Color(0xFF2E7D32),
                        testTag = "mode_ai_scanner",
                        onClick = { subScreen = StudentSubScreen.AI_SCANNER }
                    )
                }

                // Reward Shop
                item {
                    GameModeCard(
                        title = "UCMAS Reward Shop",
                        subtitle = "Trade earned tokens for bead skins, badges & themes",
                        badge = "Active: ${profile.activeSkin}",
                        icon = Icons.Default.Storefront,
                        iconBg = Color(0xFFFFA000),
                        testTag = "mode_reward_shop",
                        onClick = { subScreen = StudentSubScreen.SHOP }
                    )
                }

                // Speed & Accuracy Analytics (Recharts Engine)
                item {
                    GameModeCard(
                        title = "Speed & Accuracy Analytics",
                        subtitle = "Interactive Recharts performance charts with live touch inspection",
                        badge = "Visualizer",
                        icon = Icons.Default.QueryStats,
                        iconBg = Color(0xFF0288D1),
                        testTag = "mode_performance_analytics",
                        onClick = { subScreen = StudentSubScreen.PERFORMANCE_ANALYTICS }
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "v2.0.16 © Copyright 2026 ",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Powered By MICM Net Solutions Pvt. Ltd.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53935)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        StudentSubScreen.PVP_DUEL -> {
            AnzanDuelGame(
                theme = theme,
                onMatchWin = { tokens, xp ->
                    onAddReward("PvP Match Victory", tokens, xp, "PvP Duel", false)
                    onCoinSound()
                    onTriggerVictoryParticles()
                },
                onDuelFinished = onRecordDuel,
                onBack = { subScreen = StudentSubScreen.HUB },
                onBeadClick = onBeadClick,
                onCorrectChime = onCorrectChime,
                onErrorBuzz = onErrorBuzz
            )
        }

        StudentSubScreen.BOSS_RAID -> {
            BossRaidsScreen(
                theme = theme,
                userProfile = profile,
                onAddReward = onAddReward,
                onBack = { subScreen = StudentSubScreen.HUB },
                onBeadClick = onBeadClick,
                onCorrectChime = onCorrectChime,
                onErrorBuzz = onErrorBuzz,
                onCoinSound = onCoinSound,
                onTriggerVictoryParticles = onTriggerVictoryParticles
            )
        }

        StudentSubScreen.SPEED_LEAGUE -> {
            WeeklySpeedLeagueView(
                entries = speedLeague,
                theme = theme,
                onBack = { subScreen = StudentSubScreen.HUB }
            )
        }

        StudentSubScreen.FLASH_ARCADE -> {
            FlashArcadeGame(
                theme = theme,
                onDrillSuccess = { tokens, xp ->
                    onAddReward("Error-Free Anzan Drill", tokens, xp, "Flash Arcade", false)
                    onCoinSound()
                },
                onBack = { subScreen = StudentSubScreen.HUB },
                onFlashTick = onFlashTick,
                onCorrectChime = onCorrectChime,
                onErrorBuzz = onErrorBuzz,
                onBeadClick = onBeadClick
            )
        }

        StudentSubScreen.AI_SCANNER -> {
            AISheetScanner(
                theme = theme,
                onUploadSuccess = { tokens, xp, record ->
                    onAddReward("Abacus Practice Sheet Upload", tokens, xp, "Homework Scan", true)
                    onSubmitHomework(record)
                    onCoinSound()
                    onTriggerVictoryParticles()
                },
                onBack = { subScreen = StudentSubScreen.HUB },
                onScanBeep = onFlashTick,
                onSuccessChime = onCorrectChime
            )
        }

        StudentSubScreen.SHOP -> {
            RewardShopView(
                theme = theme,
                userTokens = profile.tokens,
                currentEquippedSkin = profile.activeSkin,
                onEquipSkin = onEquipSkin,
                onPurchaseItem = { cost, name ->
                    onAddReward(name, -cost, 0, "Shop Purchase", false)
                    onCoinSound()
                },
                onBack = { subScreen = StudentSubScreen.HUB }
            )
        }

        StudentSubScreen.AVATAR_CUSTOMIZER -> {
            AvatarCustomizerScreen(
                profile = profile,
                avatarItems = avatarItems,
                theme = theme,
                onEquipItem = onEquipAvatarItem,
                onPurchaseItem = { cost, item ->
                    onPurchaseAvatarItem(cost, item)
                    onCoinSound()
                },
                onBack = { subScreen = StudentSubScreen.HUB }
            )
        }

        StudentSubScreen.PERFORMANCE_ANALYTICS -> {
            com.example.ui.analytics.PerformanceAnalyticsScreen(
                analytics = analytics,
                theme = theme,
                studentName = profile.name,
                className = profile.className,
                onBack = { subScreen = StudentSubScreen.HUB }
            )
        }

        StudentSubScreen.MENTAL_MATH_TRAINER -> {
            MentalMathTrainerScreen(
                theme = theme,
                userProfile = profile,
                onBack = { subScreen = StudentSubScreen.HUB },
                onBeadClick = onBeadClick,
                onCorrectChime = onCorrectChime,
                onErrorBuzz = onErrorBuzz,
                onCoinSound = onCoinSound,
                onTriggerVictoryParticles = onTriggerVictoryParticles,
                onAddReward = onAddReward,
                onRecordTrainingSession = onRecordTrainingSession
            )
        }
    }
}

@Composable
private fun GameModeCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    iconBg: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F4)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
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
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E24))
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF757588), fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = iconBg.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconBg,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Open", tint = Color(0xFFB0B0C0))
        }
    }
}

@Composable
fun SchoolCircleActionItem(
    label: String,
    icon: ImageVector? = null,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
            .testTag("school_circle_${label.lowercase().replace(" ", "_")}")
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFE53935)),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else if (badgeText != null) {
                Text(
                    text = badgeText,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF222233),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
            lineHeight = 12.sp
        )
    }
}

/**
 * Attached Ribbon Card matching Screenshot 2:
 * - Red date ribbon attached to top-left corner
 * - Red tag attached to top-right corner
 * - Clean title and subtitle with timing icon
 */
@Composable
fun AttachedRibbonCard(
    dateText: String,
    tagText: String,
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0E0E0)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attached_ribbon_card_${title.lowercase().replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Attached Top Ribbons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Top-left attached ribbon
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 18.dp, bottomEnd = 12.dp))
                        .background(Color(0xFFE53935))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = dateText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Top-right attached tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topEnd = 18.dp, bottomStart = 12.dp))
                        .background(Color(0xFFE53935))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tagText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Card Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E1E24)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF555566),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

