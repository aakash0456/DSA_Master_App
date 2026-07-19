package com.example.dsamaster.feature.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.database.StudyDayEntity
import com.example.dsamaster.core.designsystem.StatPill
import com.example.dsamaster.core.designsystem.accentAt
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

data class ProgressUiState(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lessonsCompleted: Int = 0,
    val solvedProblems: Int = 0,
    val masteredCards: Int = 0,
    val totalCards: Int = 0,
    val quizAccuracy: Int = 0,
    val weekly: List<Pair<String, Int>> = emptyList(),
    /** 12 weeks × 7 days activity intensity 0..3, oldest first. */
    val heatmap: List<Int> = emptyList(),
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    activity: ActivityRepository,
    content: ContentRepository,
    flashcards: FlashcardRepository,
) : ViewModel() {
    val uiState = combine(
        activity.studyDays(),
        content.completedLessonCount(),
        content.solvedProblemCount(),
        combine(flashcards.masteredCount(), flashcards.cardCount()) { m, t -> m to t },
        content.attempts(),
    ) { days, lessons, solved, cards, attempts ->
        val today = LocalDate.now().toEpochDay()
        val byDay = days.associateBy(StudyDayEntity::epochDay)
        val active = days.filter { it.lessonsCompleted + it.cardsReviewed + it.questionsAnswered > 0 }
        val streak = StreakCalculator.fromActiveDays(active.map { it.epochDay }, today)

        val weekly = (6 downTo 0).map { offset ->
            val date = LocalDate.ofEpochDay(today - offset)
            val row = byDay[today - offset]
            val count = (row?.cardsReviewed ?: 0) + (row?.lessonsCompleted ?: 0) + (row?.questionsAnswered ?: 0)
            date.dayOfWeek.name.take(2) to count
        }

        val heatmap = (83 downTo 0).map { offset ->
            val row = byDay[today - offset]
            val count = (row?.cardsReviewed ?: 0) + (row?.lessonsCompleted ?: 0) + (row?.questionsAnswered ?: 0)
            when { count == 0 -> 0; count < 5 -> 1; count < 15 -> 2; else -> 3 }
        }

        val answered = attempts.sumOf { it.total }
        val correct = attempts.sumOf { it.score }
        ProgressUiState(
            currentStreak = streak.current,
            longestStreak = streak.longest,
            lessonsCompleted = lessons,
            solvedProblems = solved,
            masteredCards = cards.first,
            totalCards = cards.second,
            quizAccuracy = if (answered == 0) 0 else correct * 100 / answered,
            weekly = weekly,
            heatmap = heatmap,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())
}

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = hiltViewModel()) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Progress") }) }) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatPill("${ui.currentStreak}", "Streak", Modifier.weight(1f), tint = accentAt(3))
                    StatPill("${ui.longestStreak}", "Best streak", Modifier.weight(1f), tint = accentAt(2))
                    StatPill("${ui.quizAccuracy}%", "Quiz acc.", Modifier.weight(1f), tint = accentAt(4))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatPill("${ui.lessonsCompleted}", "Lessons", Modifier.weight(1f), tint = accentAt(0))
                    StatPill("${ui.solvedProblems}", "Problems", Modifier.weight(1f), tint = accentAt(5))
                    StatPill("${ui.masteredCards}/${ui.totalCards}", "Mastered", Modifier.weight(1f), tint = accentAt(1))
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Last 7 days", style = MaterialTheme.typography.titleMedium)
                        val max = (ui.weekly.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1)
                        Row(
                            Modifier.fillMaxWidth().height(120.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            ui.weekly.forEach { (label, count) ->
                                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height((100 * count / max).coerceAtLeast(4).dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Text(label, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
            item {
                Card {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Review heatmap · last 12 weeks", style = MaterialTheme.typography.titleMedium)
                        val colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.secondary,
                        )
                        // 12 columns (weeks) × 7 rows (days), oldest week on the left.
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            (0 until 12).forEach { week ->
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    (0 until 7).forEach { day ->
                                        val level = ui.heatmap.getOrNull(week * 7 + day) ?: 0
                                        Box(
                                            Modifier.size(14.dp).clip(RoundedCornerShape(3.dp))
                                                .background(colors[level])
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
