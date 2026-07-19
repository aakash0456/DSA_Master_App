package com.example.dsamaster.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.database.StudyDayEntity
import com.example.dsamaster.core.database.TopicEntity
import com.example.dsamaster.core.datastore.UserPreferencesRepository
import com.example.dsamaster.core.designsystem.StatPill
import com.example.dsamaster.domain.ActivityRepository
import com.example.dsamaster.domain.ContentRepository
import com.example.dsamaster.domain.FlashcardRepository
import com.example.dsamaster.domain.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val streak: Int = 0,
    val dueCards: Int = 0,
    val reviewedToday: Int = 0,
    val dailyGoal: Int = 20,
    val lessonsCompleted: Int = 0,
    val totalLessons: Int = 0,
    val solvedProblems: Int = 0,
    val recommendedTopic: TopicEntity? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    content: ContentRepository,
    flashcards: FlashcardRepository,
    activity: ActivityRepository,
    prefs: UserPreferencesRepository,
) : ViewModel() {

    private data class Counts(val done: Int, val total: Int, val solved: Int, val due: Int)

    private val counts = combine(
        content.completedLessonCount(), content.totalLessonCount(),
        content.solvedProblemCount(), flashcards.dueCount(),
    ) { done, total, solved, due -> Counts(done, total, solved, due) }

    val uiState = combine(
        counts, activity.studyDays(), content.topics(), content.allLessons(), prefs.preferences,
    ) { c, days, topics, lessons, p ->
        val today = LocalDate.now().toEpochDay()
        val active = days.filter { it.lessonsCompleted + it.cardsReviewed + it.questionsAnswered > 0 }
        val streak = StreakCalculator.fromActiveDays(active.map(StudyDayEntity::epochDay), today)
        val completedTopicIds = lessons.groupBy { it.topicId }
            .filterValues { list -> list.all { it.isCompleted } }.keys
        HomeUiState(
            userName = p.userName,
            streak = streak.current,
            dueCards = c.due,
            reviewedToday = days.firstOrNull { it.epochDay == today }?.cardsReviewed ?: 0,
            dailyGoal = p.dailyGoalCards,
            lessonsCompleted = c.done,
            totalLessons = c.total,
            solvedProblems = c.solved,
            recommendedTopic = topics.firstOrNull { it.id !in completedTopicIds } ?: topics.firstOrNull(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}

@Composable
fun HomeScreen(
    onOpenTopic: (Long) -> Unit,
    onStartReview: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ui.userName.isBlank()) "DSA Master" else "Hi, ${ui.userName}") },
                actions = {
                    IconButton(onClick = onOpenSearch) { Icon(Icons.Filled.Search, contentDescription = "Search") }
                    IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, contentDescription = "Settings") }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatPill("${ui.streak} 🔥", "Day streak", Modifier.weight(1f))
                    StatPill("${ui.dueCards}", "Cards due", Modifier.weight(1f))
                    StatPill("${ui.solvedProblems}", "Solved", Modifier.weight(1f))
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary)
                            Text("  Daily goal", style = MaterialTheme.typography.titleMedium)
                        }
                        val progress = (ui.reviewedToday.toFloat() / ui.dailyGoal).coerceIn(0f, 1f)
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        Text(
                            "${ui.reviewedToday} of ${ui.dailyGoal} cards reviewed today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(onClick = onStartReview, enabled = ui.dueCards > 0) {
                            Text(if (ui.dueCards > 0) "Review ${ui.dueCards} due cards" else "All caught up 🎉")
                        }
                    }
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Lesson progress", style = MaterialTheme.typography.titleMedium)
                        val progress = if (ui.totalLessons == 0) 0f
                        else ui.lessonsCompleted.toFloat() / ui.totalLessons
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                        Text(
                            "${ui.lessonsCompleted} of ${ui.totalLessons} lessons completed " +
                                "(${(progress * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            ui.recommendedTopic?.let { topic ->
                item {
                    Card(onClick = { onOpenTopic(topic.id) }) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Continue learning", style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary)
                            Text(topic.title, style = MaterialTheme.typography.titleLarge)
                            Text(topic.subtitle, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
