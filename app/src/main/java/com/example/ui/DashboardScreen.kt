package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hydration", fontWeight = FontWeight.Bold) },
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Streak",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streak Days",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                CircularProgressSection(
                    current = todayTotal,
                    goal = settingsState.dailyGoalMl
                )
                Spacer(modifier = Modifier.height(48.dp))
            }

            item {
                Text(
                    "Quick Add",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocalDrink,
                            name = "Small Glass",
                            amount = 100,
                            onClick = { viewModel.addDrink(100, "Water") }
                        )
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocalDrink,
                            name = "Medium Glass",
                            amount = 200,
                            onClick = { viewModel.addDrink(200, "Water") }
                        )
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocalDrink,
                            name = "Standard Glass",
                            amount = 250,
                            onClick = { viewModel.addDrink(250, "Water") }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocalDrink,
                            name = "Large Glass",
                            amount = 300,
                            onClick = { viewModel.addDrink(300, "Water") }
                        )
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocalDrink,
                            name = "Bottle",
                            amount = 500,
                            onClick = { viewModel.addDrink(500, "Water") }
                        )
                        QuickAddCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.LocalDrink,
                            name = "Big Bottle",
                            amount = 1000,
                            onClick = { viewModel.addDrink(1000, "Water") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    "Weekly Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
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
    val progress = (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "progress"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(240.dp)
    ) {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, primaryColor.copy(alpha = 0.6f))
                ),
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$current / $goal ml",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickAddCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    amount: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${amount}ml",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WeeklyChartSection(records: List<DrinkRecord>, dailyGoal: Int) {
    val barColor = MaterialTheme.colorScheme.primary
    val bgBarColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

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

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            val barWidth = 16.dp.toPx()
            val cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            val availableWidth = size.width
            val availableHeight = size.height
            val spacing = (availableWidth - (7 * barWidth)) / 6
            val maxData = maxOf(dailyGoal.toFloat(), daysData.maxOrNull() ?: 0f)

            for (i in 0..6) {
                val x = i * (barWidth + spacing)
                val fillHeight = (daysData[i] / maxData) * availableHeight
                val yOffset = availableHeight - fillHeight

                // Draw background bar
                drawRoundRect(
                    color = bgBarColor,
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, availableHeight),
                    cornerRadius = cornerRadius
                )

                // Draw filled bar
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, yOffset),
                    size = Size(barWidth, fillHeight),
                    cornerRadius = cornerRadius
                )
            }
        }
    }
}
