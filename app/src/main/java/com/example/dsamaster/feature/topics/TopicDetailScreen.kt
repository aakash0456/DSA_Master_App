package com.example.dsamaster.feature.topics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.database.CodingProblemEntity
import com.example.dsamaster.core.database.LessonEntity
import com.example.dsamaster.core.database.QuizEntity
import com.example.dsamaster.core.database.TopicEntity
import com.example.dsamaster.core.designsystem.difficultyLabel
import com.example.dsamaster.domain.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TopicDetailUiState(
    val topic: TopicEntity? = null,
    val lessons: List<LessonEntity> = emptyList(),
    val quizzes: List<QuizEntity> = emptyList(),
    val problems: List<CodingProblemEntity> = emptyList(),
)

@HiltViewModel
class TopicDetailViewModel @Inject constructor(
    content: ContentRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val topicId: Long = checkNotNull(savedStateHandle.get<String>("topicId")).toLong()

    val uiState = combine(
        content.topic(topicId),
        content.lessonsForTopic(topicId),
        content.quizzesForTopic(topicId),
        content.problemsForTopic(topicId),
    ) { topic, lessons, quizzes, problems ->
        TopicDetailUiState(topic, lessons, quizzes, problems)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TopicDetailUiState())
}

@Composable
fun TopicDetailScreen(
    topicId: Long,
    onBack: () -> Unit,
    onOpenLesson: (Long) -> Unit,
    onOpenQuiz: (Long) -> Unit,
    onOpenProblem: (Long) -> Unit,
    viewModel: TopicDetailViewModel = hiltViewModel(),
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val topic = ui.topic

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topic?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (topic != null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(topic.description, style = MaterialTheme.typography.bodyLarge)
                        Surface(color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.medium) {
                            Text("💡 ${topic.analogy}", Modifier.padding(12.dp).fillMaxWidth(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                    }
                }
            }
            if (ui.lessons.isNotEmpty()) {
                item { Text("Lessons", style = MaterialTheme.typography.titleMedium) }
                items(ui.lessons, key = { "l${it.id}" }) { lesson ->
                    Card(onClick = { onOpenLesson(lesson.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (lesson.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                                contentDescription = if (lesson.isCompleted) "Completed" else "Not completed",
                                tint = if (lesson.isCompleted) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text("  ${lesson.title}", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
            if (ui.quizzes.isNotEmpty()) {
                item { Text("Quizzes", style = MaterialTheme.typography.titleMedium) }
                items(ui.quizzes, key = { "q${it.id}" }) { quiz ->
                    Card(onClick = { onOpenQuiz(quiz.id) }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(quiz.title, style = MaterialTheme.typography.bodyLarge)
                            Text(difficultyLabel(quiz.difficulty),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            if (ui.problems.isNotEmpty()) {
                item { Text("Coding problems", style = MaterialTheme.typography.titleMedium) }
                items(ui.problems, key = { "p${it.id}" }) { p ->
                    Card(onClick = { onOpenProblem(p.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(p.title, style = MaterialTheme.typography.bodyLarge)
                                Text(difficultyLabel(p.difficulty),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (p.isSolved) Icon(Icons.Filled.CheckCircle, contentDescription = "Solved",
                                tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}
