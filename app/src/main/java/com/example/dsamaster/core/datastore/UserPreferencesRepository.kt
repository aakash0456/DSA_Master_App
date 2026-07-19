package com.example.dsamaster.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class UserPreferences(
    val onboardingDone: Boolean = false,
    val userName: String = "",
    val dailyGoalCards: Int = 20,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val remindersEnabled: Boolean = false,
    val reminderHour: Int = 19,
)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ONBOARDING = booleanPreferencesKey("onboarding_done")
        val NAME = stringPreferencesKey("user_name")
        val GOAL = intPreferencesKey("daily_goal_cards")
        val THEME = stringPreferencesKey("theme_mode")
        val DYNAMIC = booleanPreferencesKey("dynamic_color")
        val REMINDERS = booleanPreferencesKey("reminders_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { p ->
        UserPreferences(
            onboardingDone = p[Keys.ONBOARDING] ?: false,
            userName = p[Keys.NAME] ?: "",
            dailyGoalCards = p[Keys.GOAL] ?: 20,
            themeMode = runCatching { ThemeMode.valueOf(p[Keys.THEME] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM),
            dynamicColor = p[Keys.DYNAMIC] ?: true,
            remindersEnabled = p[Keys.REMINDERS] ?: false,
            reminderHour = p[Keys.REMINDER_HOUR] ?: 19,
        )
    }

    suspend fun setOnboardingDone() = context.dataStore.edit { it[Keys.ONBOARDING] = true }
    suspend fun setUserName(name: String) = context.dataStore.edit { it[Keys.NAME] = name }
    suspend fun setDailyGoal(goal: Int) = context.dataStore.edit { it[Keys.GOAL] = goal.coerceIn(5, 200) }
    suspend fun setThemeMode(mode: ThemeMode) = context.dataStore.edit { it[Keys.THEME] = mode.name }
    suspend fun setDynamicColor(enabled: Boolean) = context.dataStore.edit { it[Keys.DYNAMIC] = enabled }
    suspend fun setRemindersEnabled(enabled: Boolean) = context.dataStore.edit { it[Keys.REMINDERS] = enabled }
    suspend fun setReminderHour(hour: Int) = context.dataStore.edit { it[Keys.REMINDER_HOUR] = hour.coerceIn(0, 23) }
}
