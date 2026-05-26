package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    settingsState: SettingsState,
    onDismiss: () -> Unit,
    onSave: (Boolean, Int, Int, Int, Int, Int, Int) -> Unit
) {
    var active by remember { mutableStateOf(settingsState.remindersActive) }
    var sHour by remember { mutableStateOf(settingsState.startHour) }
    var eHour by remember { mutableStateOf(settingsState.endHour) }
    var interval by remember { mutableStateOf(settingsState.intervalMins) }
    var goal by remember { mutableStateOf(settingsState.dailyGoalMl) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "Reminders & Goals",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Enable Reminders", style = MaterialTheme.typography.titleMedium)
                Switch(checked = active, onCheckedChange = { active = it })
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (active) {
                Text("Start Time (Hour: 0-23)", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = sHour.toFloat(),
                    onValueChange = { sHour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )
                Text(text = "$sHour:00", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Text("End Time (Hour: 0-23)", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = eHour.toFloat(),
                    onValueChange = { eHour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )
                Text(text = "$eHour:00", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Interval (Minutes)", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = interval.toFloat(),
                    onValueChange = { interval = it.toInt() },
                    valueRange = 15f..120f,
                    // steps = 6
                )
                Text(text = "$interval mins", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text("Daily Goal (ml)", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = goal.toFloat(),
                onValueChange = { goal = it.toInt() },
                valueRange = 1000f..4000f,
                // steps = 6
            )
            Text(text = "${goal}ml", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSave(active, sHour, 0, eHour, 0, interval, goal) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
