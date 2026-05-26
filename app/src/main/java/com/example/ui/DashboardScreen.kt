package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DrinkRecord
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val todayTotal by viewModel.todayTotal.collectAsStateWithLifecycle()
    val streak by viewModel.streak.collectAsStateWithLifecycle()
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()
    val allRecords by viewModel.allRecords.collectAsStateWithLifecycle()

    var showSettings by remember { mutableStateOf(false) }

    // Space / Ambient deep navy visual theme for premium design direction
    val spaceBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF070B14),
            Color(0xFF0F172A),
            Color(0xFF1E293B)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Daily Hydration",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Nourish your body & mind",
                            fontSize = 12.sp,
                            color = Color(0xFFA5B4FC),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    // Vibrant streak badge
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
                                )
                            )
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Streak",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streak Days",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(spaceBackground)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressSection(
                    current = todayTotal,
                    goal = settingsState.dailyGoalMl
                )
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Quick Add",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Quick Log Water",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            name = "Sip",
                            amount = 100,
                            gradientColors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5)),
                            glowColor = Color(0x666366F1),
                            onClick = { viewModel.addDrink(100, "Water") }
                        )
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            name = "Cup",
                            amount = 200,
                            gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB)),
                            glowColor = Color(0x663B82F6),
                            onClick = { viewModel.addDrink(200, "Water") }
                        )
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            name = "Glass",
                            amount = 250,
                            gradientColors = listOf(Color(0xFF0EA5E9), Color(0xFF0284C7)),
                            glowColor = Color(0x660EA5E9),
                            onClick = { viewModel.addDrink(250, "Water") }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            name = "Tumbler",
                            amount = 300,
                            gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0891B2)),
                            glowColor = Color(0x6606B6D4),
                            onClick = { viewModel.addDrink(300, "Water") }
                        )
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            name = "Bottle",
                            amount = 500,
                            gradientColors = listOf(Color(0xFF14B8A6), Color(0xFF0D9488)),
                            glowColor = Color(0x6614B8A6),
                            onClick = { viewModel.addDrink(500, "Water") }
                        )
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            name = "Carafe",
                            amount = 1000,
                            gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669)),
                            glowColor = Color(0x6610B981),
                            onClick = { viewModel.addDrink(1000, "Water") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(36.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Analytics",
                        tint = Color(0xFFFB7185),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Weekly Tracker",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                WeeklyChartSection(allRecords, settingsState.dailyGoalMl)
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    if (showSettings) {
        SettingsDialog(
            settingsState = settingsState,
            onDismiss = { showSettings = false },
            onSave = { active, sh, sm, eh, em, interval, goal ->
                viewModel.updateSettings(active, sh, sm, eh, em, interval, goal)
                showSettings = false
            }
        )
    }
}

@Composable
fun CircularProgressSection(current: Int, goal: Int) {
    val progress = if (goal > 0) (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200),
        label = "progress"
    )

    // Vibrant glow circular gradients
    val neonBlue = Color(0xFF38BDF8)
    val neonTeal = Color(0xFF06B6D4)
    val neonPurple = Color(0xFF6366F1)

    // Encourage messages
    val encouragement = when {
        progress >= 1f -> "Target Achieved! 🏆"
        progress >= 0.75f -> "Almost there, stay strong! ⚡"
        progress >= 0.5f -> "Halfway to optimal hydration! 🥤"
        progress >= 0.25f -> "Off to a fantastic start! 🌱"
        else -> "Let's drink some water! 💧"
    }

    Surface(
        color = Color.White.copy(alpha = 0.04f),
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(32.dp))
            .drawBehind {
                drawIntoCanvas {
                    // Soft light ambient background behind card
                }
            },
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                // Background Track Glow Canvas
                Canvas(modifier = Modifier.size(210.dp)) {
                    // Light atmospheric background ring
                    drawArc(
                        color = Color.White.copy(alpha = 0.05f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Glow effect
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(neonPurple, neonBlue, neonTeal, neonPurple)
                        ),
                        startAngle = 135f,
                        sweepAngle = 270f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$current / $goal ml",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = encouragement,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun QuickAddCard(
    modifier: Modifier = Modifier,
    name: String,
    amount: Int,
    gradientColors: List<Color>,
    glowColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(125.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = glowColor
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    )
                )
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxSize()
            ) {
                // Soft elegant icon container
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = name,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "${amount}ml",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            lineHeight = 18.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyChartSection(records: List<DrinkRecord>, dailyGoal: Int) {
    val calendar = Calendar.getInstance()
    calendar.firstDayOfWeek = Calendar.MONDAY
    
    // Calculate last 7 days data
    val daysData = remember(records) {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val data = FloatArray(7)
        for (i in 6 downTo 0) {
            val dayStart = todayStart - (i * 86400000L)
            val dayEnd = dayStart + 86400000L
            val total = records.filter { it.timestamp in dayStart until dayEnd }.sumOf { it.amountMl }
            data[6 - i] = total.toFloat()
        }
        data
    }

    // Weekdays labels (Mo, Tu, We, Th, Fr, Sa, Su)
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Surface(
        color = Color.White.copy(alpha = 0.04f),
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Chart metrics legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Strength",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Goal threshold indicated by dashed marker",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // The custom visual chart element
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val barWidth = 14.dp.toPx()
                    val cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                    val availableWidth = size.width
                    val availableHeight = size.height
                    
                    val maxData = maxOf(dailyGoal.toFloat(), daysData.maxOrNull() ?: 0f, 1000f)
                    val spacing = (availableWidth - (7 * barWidth)) / 6

                    // Draw goal line marker helper
                    val goalY = availableHeight - ((dailyGoal.toFloat() / maxData) * availableHeight)
                    if (goalY in 0f..availableHeight) {
                        drawLine(
                            color = Color(0x6638BDF8),
                            start = Offset(0f, goalY),
                            end = Offset(availableWidth, goalY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    for (i in 0..6) {
                        val x = i * (barWidth + spacing)
                        val totalVal = daysData[i]
                        val fillHeight = (totalVal / maxData) * availableHeight
                        val yOffset = availableHeight - fillHeight

                        // Background pillar rail
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.06f),
                            topLeft = Offset(x, 0f),
                            size = Size(barWidth, availableHeight),
                            cornerRadius = cornerRadius
                        )

                        // Achieved/progress pillar colors
                        val gradientBrush = if (totalVal >= dailyGoal && dailyGoal > 0) {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF10B981), Color(0xFF059669))
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF38BDF8), Color(0xFF3B82F6))
                            )
                        }

                        // Filled bar
                        if (fillHeight > 0f) {
                            drawRoundRect(
                                brush = gradientBrush,
                                topLeft = Offset(x, yOffset),
                                size = Size(barWidth, fillHeight),
                                cornerRadius = cornerRadius
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weekday text row with achieved dot indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0..6) {
                    val achieved = daysData[i] >= dailyGoal && dailyGoal > 0
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = dayLabels[i],
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (achieved) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (achieved) Color(0xFF10B981) else Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (achieved) Color(0xFF10B981) else Color.Transparent
                                )
                        )
                    }
                }
            }
        }
    }
}
