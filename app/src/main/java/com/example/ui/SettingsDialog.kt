package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape

private fun Context.findActivity(): Activity? {
    var cur = this
    while (cur is ContextWrapper) {
        if (cur is Activity) return cur
        cur = cur.baseContext
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    settingsState: SettingsState,
    onDismiss: () -> Unit,
    onSave: (Boolean, Int, Int, Int, Int, Int, Int) -> Unit
) {
    val context = LocalContext.current

    var active by remember { mutableStateOf(settingsState.remindersActive) }
    
    var sHour by remember { mutableStateOf(settingsState.startHour) }
    var sMin by remember { mutableStateOf(settingsState.startMin) }
    var eHour by remember { mutableStateOf(settingsState.endHour) }
    var eMin by remember { mutableStateOf(settingsState.endMin) }
    
    var intervalStr by remember { mutableStateOf(settingsState.intervalMins.toString()) }
    var goalStr by remember { mutableStateOf(settingsState.dailyGoalMl.toString()) }

    // Validation
    val parsedInterval = intervalStr.toIntOrNull()?.coerceIn(1, 1440)
    val parsedGoal = goalStr.toIntOrNull()?.coerceIn(100, 10000)

    val isInputValid = parsedGoal != null && (!active || parsedInterval != null)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Reminders & Goals",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Personalize your daily drinking triggers and volume goals.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Reminder Toggle Container
            Surface(
                color = if (active) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                border = AssistChipDefaults.assistChipBorder(enabled = true)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (active) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "Notification Icon",
                            tint = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Drink Reminders",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Get timely push notifications to stay on track",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = active,
                        onCheckedChange = { active = it }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (active) {
                // Interactive Time Range Section Card with Time Pickers
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Schedule",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Active Notification Hours",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Start Time Selector
                            TimeSelectorCard(
                                modifier = Modifier.weight(1f),
                                label = "Start Alert At",
                                hour = sHour,
                                minute = sMin,
                                onClick = {
                                    TimePickerDialog(
                                        context.findActivity() ?: context,
                                        { _, selectedHour, selectedMinute ->
                                            sHour = selectedHour
                                            sMin = selectedMinute
                                        },
                                        sHour,
                                        sMin,
                                        false // Show AM/PM selector
                                    ).show()
                                }
                            )

                            // End Time Selector
                            TimeSelectorCard(
                                modifier = Modifier.weight(1f),
                                label = "End Alert At",
                                hour = eHour,
                                minute = eMin,
                                onClick = {
                                    TimePickerDialog(
                                        context.findActivity() ?: context,
                                        { _, selectedHour, selectedMinute ->
                                            eHour = selectedHour
                                            eMin = selectedMinute
                                        },
                                        eHour,
                                        eMin,
                                        false // Show AM/PM selector
                                    ).show()
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Interval Section Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Interval",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Reminder Frequency",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = intervalStr,
                            onValueChange = { intervalStr = it.filter { char -> char.isDigit() } },
                            label = { Text("Alert Period") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { Text("mins", modifier = Modifier.padding(end = 12.dp), style = MaterialTheme.typography.bodyMedium) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Daily Target Section Card
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Goal",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Daily Target Plan",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = goalStr,
                        onValueChange = { goalStr = it.filter { char -> char.isDigit() } },
                        label = { Text("Daily Water Limit") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Text("ml", modifier = Modifier.padding(end = 12.dp), style = MaterialTheme.typography.bodyMedium) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (!isInputValid) {
                Text(
                    text = "❗ Please enter a valid Target Goal and Interval Period.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (isInputValid) {
                        onSave(
                            active,
                            sHour,
                            sMin,
                            eHour,
                            eMin,
                            parsedInterval ?: settingsState.intervalMins,
                            parsedGoal ?: settingsState.dailyGoalMl
                        )
                    }
                },
                enabled = isInputValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Confirm Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TimeSelectorCard(
    modifier: Modifier = Modifier,
    label: String,
    hour: Int,
    minute: Int,
    onClick: () -> Unit
) {
    val h12 = if (hour == 0 || hour == 12) 12 else hour % 12
    val amPm = if (hour >= 12) "PM" else "AM"
    val hStr = h12.toString().padStart(2, '0')
    val mStr = minute.toString().padStart(2, '0')

    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$hStr:$mStr",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = amPm,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
