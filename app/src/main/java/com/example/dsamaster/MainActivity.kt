package com.example.dsamaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.datastore.ThemeMode
import com.example.dsamaster.core.datastore.UserPreferencesRepository
import com.example.dsamaster.core.designsystem.DsaTheme
import com.example.dsamaster.core.navigation.DsaNavHost
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: MainViewModel = hiltViewModel()
            val ui by vm.uiState.collectAsStateWithLifecycle()
            ui?.let { state ->
                val dark = when (state.themeMode) {
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                }
                DsaTheme(darkTheme = dark, dynamicColor = state.dynamicColor) {
                    DsaNavHost(showOnboarding = !state.onboardingDone)
                }
            }
        }
    }
}

data class MainUiState(
    val onboardingDone: Boolean,
    val themeMode: ThemeMode,
    val dynamicColor: Boolean,
)

@HiltViewModel
class MainViewModel @Inject constructor(prefs: UserPreferencesRepository) : ViewModel() {
    val uiState = prefs.preferences
        .map { MainUiState(it.onboardingDone, it.themeMode, it.dynamicColor) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
