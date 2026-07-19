package com.example.dsamaster.feature.lessons

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import com.example.dsamaster.core.database.LessonEntity
import com.example.dsamaster.core.designsystem.LessonBodyView
import com.example.dsamaster.domain.ActivityRepository
import com.example.dsamaster.domain.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonUiState(
    val lesson: LessonEntity? = null,
    val previousId: Long? = null,
    val nextId: Long? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LessonViewModel @Inject constructor(
    private val content: ContentRepository,
    private val activity: ActivityRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val lessonId: Long = checkNotNull(savedStateHandle.get<String>("lessonId")).toLong()

    val uiState = content.lesson(lessonId).flatMapLatest { lesson ->
        if (lesson == null) flowOf(LessonUiState())
        else content.lessonsForTopic(lesson.topicId).combine(flowOf(lesson)) { siblings, l ->
            val index = siblings.indexOfFirst { it.id == l.id }
            LessonUiState(
                lesson = l,
                previousId = siblings.getOrNull(index - 1)?.id,
                nextId = siblings.getOrNull(index + 1)?.id,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LessonUiState())

    fun toggleBookmark() {
        val lesson = uiState.value.lesson ?: return
        viewModelScope.launch { content.setLessonBookmarked(lesson.id, !lesson.isBookmarked) }
    }

    fun markCompleted() {
        val lesson = uiState.value.lesson ?: return
        if (lesson.isCompleted) return
        viewModelScope.launch {
            content.setLessonCompleted(lesson.id, true)
            activity.recordLessonCompleted()
        }
    }
}

@Composable
fun LessonScreen(
    lessonId: Long,
    onBack: () -> Unit,
    onOpenLesson: (Long) -> Unit,
    viewModel: LessonViewModel = hiltViewModel(),
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val lesson = ui.lesson ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleBookmark) {
                        Icon(
                            if (lesson.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (lesson.isBookmarked) "Remove bookmark" else "Bookmark lesson",
                        )
                    }
                },
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LessonBodyView(lesson.body)
            Button(
                onClick = viewModel::markCompleted,
                enabled = !lesson.isCompleted,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (lesson.isCompleted) "Completed ✓" else "Mark as completed") }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(onClick = { ui.previousId?.let(onOpenLesson) }, enabled = ui.previousId != null) {
                    Text("Previous")
                }
                OutlinedButton(onClick = { ui.nextId?.let(onOpenLesson) }, enabled = ui.nextId != null) {
                    Text("Next")
                }
            }
        }
    }
}
