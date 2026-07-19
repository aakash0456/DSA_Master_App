package com.example.dsamaster.feature.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.datastore.ThemeMode
import com.example.dsamaster.core.datastore.UserPreferences
import com.example.dsamaster.core.datastore.UserPreferencesRepository
import com.example.dsamaster.core.notifications.ReminderScheduler
import com.example.dsamaster.domain.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferencesRepository,
    private val activity: ActivityRepository,
    private val reminders: ReminderScheduler,
) : ViewModel() {
    val preferences = prefs.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPreferences())

    fun setName(name: String) = viewModelScope.launch { prefs.setUserName(name) }
    fun setGoal(goal: Int) = viewModelScope.launch { prefs.setDailyGoal(goal) }
    fun setTheme(mode: ThemeMode) = viewModelScope.launch { prefs.setThemeMode(mode) }
    fun setDynamicColor(enabled: Boolean) = viewModelScope.launch { prefs.setDynamicColor(enabled) }

    fun setReminders(enabled: Boolean) = viewModelScope.launch {
        prefs.setRemindersEnabled(enabled)
        if (enabled) reminders.schedule(preferences.value.reminderHour) else reminders.cancel()
    }

    fun setReminderHour(hour: Int) = viewModelScope.launch {
        prefs.setReminderHour(hour)
        if (preferences.value.remindersEnabled) reminders.schedule(hour)
    }

    fun resetProgress() = viewModelScope.launch { activity.resetAllProgress() }
}

@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    var showReset by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable(prefs.userName) { mutableStateOf(prefs.userName) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.setReminders(granted) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Profile", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; viewModel.setName(it) },
                label = { Text("Your name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Daily goal: ${prefs.dailyGoalCards} cards", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = prefs.dailyGoalCards.toFloat(),
                onValueChange = { viewModel.setGoal(it.toInt()) },
                valueRange = 5f..100f,
            )

            HorizontalDivider()
            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = prefs.themeMode == mode,
                        onClick = { viewModel.setTheme(mode) },
                        label = { Text(mode.name.lowercase().replaceFirstChar(Char::uppercase)) },
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Dynamic color (Android 12+)", Modifier.weight(1f))
                Switch(checked = prefs.dynamicColor, onCheckedChange = viewModel::setDynamicColor)
            }

            HorizontalDivider()
            Text("Reminders", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Daily study reminder", Modifier.weight(1f))
                Switch(
                    checked = prefs.remindersEnabled,
                    onCheckedChange = { enable ->
                        if (enable && Build.VERSION.SDK_INT >= 33) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else viewModel.setReminders(enable)
                    },
                )
            }
            if (prefs.remindersEnabled) {
                Text("Reminder time: ${prefs.reminderHour}:00")
                Slider(
                    value = prefs.reminderHour.toFloat(),
                    onValueChange = { viewModel.setReminderHour(it.toInt()) },
                    valueRange = 0f..23f,
                    steps = 22,
                )
            }

            HorizontalDivider()
            Text("Data", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(onClick = { showReset = true }) { Text("Reset all progress") }

            HorizontalDivider()
            Text("About", style = MaterialTheme.typography.titleMedium)
            Text("DSA Master 1.0.0 — a free, offline, ad-free app for learning data structures " +
                "and algorithms. No data leaves your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            title = { Text("Reset all progress?") },
            text = { Text("Streaks, review schedules, quiz history, solved problems and lesson " +
                "completion will be cleared. Your custom decks and cards are kept.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetProgress(); showReset = false }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { showReset = false }) { Text("Cancel") } },
        )
    }
}
