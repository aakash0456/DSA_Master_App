package com.example.dsamaster.feature.patterns

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.designsystem.CodeBlock
import com.example.dsamaster.core.designsystem.TagChip
import com.example.dsamaster.core.designsystem.difficultyColor
import com.example.dsamaster.core.designsystem.difficultyLabel
import com.example.dsamaster.domain.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PatternDetailViewModel @Inject constructor(
    content: ContentRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val patternId: Long = checkNotNull(savedStateHandle.get<String>("patternId")).toLong()

    val pattern = content.pattern(patternId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val practiceProblems = pattern
        .flatMapLatest { p ->
            if (p == null) flowOf(emptyList()) else content.problemsByPattern(p.name)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun PatternDetailScreen(
    patternId: Long,
    onBack: () -> Unit,
    onOpenProblem: (Long) -> Unit,
    viewModel: PatternDetailViewModel = hiltViewModel(),
) {
    val pattern by viewModel.pattern.collectAsStateWithLifecycle()
    val problems by viewModel.practiceProblems.collectAsStateWithLifecycle()
    val p = pattern ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(p.name) },
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
            Text(
                p.tagline,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(p.description, style = MaterialTheme.typography.bodyLarge)

            val signalColor = Color(0xFFF59E0B)
            Surface(color = signalColor.copy(alpha = 0.12f), shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(14.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "\uD83D\uDD0D  When you read these signals — think ${p.name}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = signalColor,
                    )
                    p.signals.split("|").filter { it.isNotBlank() }.forEach {
                        Text("\u2022  $it", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            val antiColor = Color(0xFFEF4444)
            val antis = p.antiSignals.split("|").filter { it.isNotBlank() }
            if (antis.isNotEmpty()) {
                Surface(color = antiColor.copy(alpha = 0.10f), shape = MaterialTheme.shapes.medium) {
                    Column(Modifier.padding(14.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "\u26A0\uFE0F  Probably NOT this pattern when...",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = antiColor,
                        )
                        antis.forEach {
                            Text("\u2022  $it", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Text("Template — the shape of the technique", style = MaterialTheme.typography.titleMedium)
            CodeBlock(p.template)

            if (problems.isNotEmpty()) {
                Text("Practice this pattern", style = MaterialTheme.typography.titleMedium)
                problems.forEach { problem ->
                    Card(onClick = { onOpenProblem(problem.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(problem.title, style = MaterialTheme.typography.titleSmall)
                                TagChip(difficultyLabel(problem.difficulty), difficultyColor(problem.difficulty))
                            }
                            if (problem.isSolved) Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "Solved",
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                }
            } else {
                Text(
                    "Practice problems for this pattern are coming in a future update — for now, hunt for its signals in the Problems tab.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
