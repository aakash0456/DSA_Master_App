package com.example.dsamaster.feature.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.database.QuizAttemptEntity
import com.example.dsamaster.core.database.QuizQuestionEntity
import com.example.dsamaster.core.designsystem.CodeBlock
import com.example.dsamaster.domain.ActivityRepository
import com.example.dsamaster.domain.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val title: String = "",
    val questions: List<QuizQuestionEntity> = emptyList(),
    val index: Int = 0,
    val selected: Int? = null,
    val score: Int = 0,
    val finished: Boolean = false,
) {
    val current: QuizQuestionEntity? get() = questions.getOrNull(index)
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val content: ContentRepository,
    private val activity: ActivityRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val quizId: Long = checkNotNull(savedStateHandle.get<String>("quizId")).toLong()
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val quiz = content.quiz(quizId)
            val questions = content.questions(quizId).shuffled()
            _uiState.value = QuizUiState(title = quiz?.title ?: "Quiz", questions = questions)
        }
    }

    fun select(option: Int) {
        val s = _uiState.value
        if (s.selected != null) return
        val correct = s.current?.correctIndex == option
        _uiState.value = s.copy(selected = option, score = s.score + if (correct) 1 else 0)
    }

    fun next() {
        val s = _uiState.value
        val nextIndex = s.index + 1
        if (nextIndex >= s.questions.size) {
            _uiState.value = s.copy(finished = true)
            viewModelScope.launch {
                content.recordAttempt(
                    QuizAttemptEntity(quizId = quizId, score = s.score, total = s.questions.size,
                        timestampMillis = System.currentTimeMillis())
                )
                activity.recordQuestionsAnswered(s.questions.size)
            }
        } else {
            _uiState.value = s.copy(index = nextIndex, selected = null)
        }
    }

    fun retry() = load()
}

@Composable
fun QuizScreen(quizId: Long, onClose: () -> Unit, viewModel: QuizViewModel = hiltViewModel()) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ui.title) },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Close") }
                },
            )
        }
    ) { padding ->
        if (ui.finished) {
            Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Score: ${ui.score} / ${ui.questions.size}",
                    style = MaterialTheme.typography.headlineSmall)
                val pct = if (ui.questions.isEmpty()) 0 else ui.score * 100 / ui.questions.size
                Text(
                    when {
                        pct >= 80 -> "Excellent — you know this topic well."
                        pct >= 50 -> "Good progress. Revisit the lesson for the ones you missed."
                        else -> "Keep going — re-read the lesson and try again."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
                Button(onClick = viewModel::retry, modifier = Modifier.fillMaxWidth()) { Text("Retry quiz") }
                OutlinedButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) { Text("Done") }
            }
            return@Scaffold
        }
        val q = ui.current ?: return@Scaffold
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LinearProgressIndicator(
                progress = { ui.index.toFloat() / ui.questions.size },
                modifier = Modifier.fillMaxWidth(),
            )
            Text("Question ${ui.index + 1} of ${ui.questions.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(q.prompt, style = MaterialTheme.typography.titleMedium)
            q.codeSnippet?.let { CodeBlock(it) }

            val options = q.options.split("|")
            options.forEachIndexed { i, option ->
                val isCorrect = i == q.correctIndex
                val isSelected = ui.selected == i
                val container = when {
                    ui.selected == null -> MaterialTheme.colorScheme.surfaceVariant
                    isCorrect -> MaterialTheme.colorScheme.secondaryContainer
                    isSelected -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                Card(
                    onClick = { viewModel.select(i) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = container),
                ) {
                    Text(option, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }

            if (ui.selected != null) {
                Surface(color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.medium) {
                    Text(q.explanation, Modifier.padding(12.dp).fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer)
                }
                Button(onClick = viewModel::next, modifier = Modifier.fillMaxWidth()) {
                    Text(if (ui.index + 1 >= ui.questions.size) "Finish" else "Next question")
                }
            }
        }
    }
}
