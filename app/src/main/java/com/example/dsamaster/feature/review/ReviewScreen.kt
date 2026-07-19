package com.example.dsamaster.feature.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.database.FlashcardEntity
import com.example.dsamaster.core.designsystem.CodeBlock
import com.example.dsamaster.core.designsystem.EmptyState
import com.example.dsamaster.domain.ActivityRepository
import com.example.dsamaster.domain.FlashcardRepository
import com.example.dsamaster.domain.Sm2Scheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val loading: Boolean = true,
    val queue: List<FlashcardEntity> = emptyList(),
    val index: Int = 0,
    val answerRevealed: Boolean = false,
    val reviewed: Int = 0,
    val againCount: Int = 0,
    val startMillis: Long = System.currentTimeMillis(),
    val finished: Boolean = false,
) {
    val current: FlashcardEntity? get() = queue.getOrNull(index)
}

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val flashcards: FlashcardRepository,
    private val activity: ActivityRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val due = flashcards.dueCards()
            _uiState.value = _uiState.value.copy(loading = false, queue = due, finished = due.isEmpty())
        }
    }

    fun reveal() { _uiState.value = _uiState.value.copy(answerRevealed = true) }

    fun rate(quality: Sm2Scheduler.Quality) {
        val state = _uiState.value
        val card = state.current ?: return
        viewModelScope.launch {
            flashcards.rate(card.id, quality)
            activity.recordCardsReviewed(1)
            val nextIndex = state.index + 1
            _uiState.value = state.copy(
                index = nextIndex,
                answerRevealed = false,
                reviewed = state.reviewed + 1,
                againCount = state.againCount + if (quality == Sm2Scheduler.Quality.AGAIN) 1 else 0,
                finished = nextIndex >= state.queue.size,
            )
        }
    }
}

@Composable
fun ReviewScreen(onClose: () -> Unit, viewModel: ReviewViewModel = hiltViewModel()) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review") },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Close") }
                },
            )
        }
    ) { padding ->
        when {
            ui.loading -> Unit
            ui.finished -> Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (ui.reviewed == 0) {
                    EmptyState("Nothing due", "Come back later, or add more flashcards.")
                } else {
                    val minutes = ((System.currentTimeMillis() - ui.startMillis) / 60_000).coerceAtLeast(0)
                    Text("Session complete 🎉", style = MaterialTheme.typography.headlineSmall)
                    Text("Cards reviewed: ${ui.reviewed}")
                    Text("Marked \"Again\": ${ui.againCount}")
                    Text("Duration: ${if (minutes == 0L) "under a minute" else "$minutes min"}")
                }
                Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) { Text("Done") }
            }
            else -> {
                val card = ui.current ?: return@Scaffold
                Column(
                    Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    LinearProgressIndicator(
                        progress = { if (ui.queue.isEmpty()) 0f else ui.index.toFloat() / ui.queue.size },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text("Card ${ui.index + 1} of ${ui.queue.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(card.question, style = MaterialTheme.typography.titleLarge)
                            card.codeSnippet?.let { CodeBlock(it) }
                            if (ui.answerRevealed) {
                                Text(card.answer, style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary)
                                if (card.explanation.isNotBlank()) {
                                    Text(card.explanation, style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                    if (!ui.answerRevealed) {
                        Button(onClick = viewModel::reveal, modifier = Modifier.fillMaxWidth()) {
                            Text("Show answer")
                        }
                    } else {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Sm2Scheduler.Quality.entries.forEach { q ->
                                FilledTonalButton(
                                    onClick = { viewModel.rate(q) },
                                    modifier = Modifier.weight(1f),
                                ) { Text(q.label, style = MaterialTheme.typography.labelMedium) }
                            }
                        }
                    }
                }
            }
        }
    }
}
