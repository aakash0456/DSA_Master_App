package com.example.dsamaster.feature.problems

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProblemDetailViewModel @Inject constructor(
    private val content: ContentRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val problemId: Long = checkNotNull(savedStateHandle.get<String>("problemId")).toLong()
    val problem = content.problem(problemId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun toggleSolved() = viewModelScope.launch {
        problem.value?.let { content.setProblemSolved(it.id, !it.isSolved) }
    }
    fun toggleBookmark() = viewModelScope.launch {
        problem.value?.let { content.setProblemBookmarked(it.id, !it.isBookmarked) }
    }
    fun saveNotes(notes: String) = viewModelScope.launch {
        problem.value?.let { content.setProblemNotes(it.id, notes) }
    }
}

@Composable
fun ProblemDetailScreen(
    problemId: Long,
    onBack: () -> Unit,
    viewModel: ProblemDetailViewModel = hiltViewModel(),
) {
    val problem by viewModel.problem.collectAsStateWithLifecycle()
    val p = problem ?: return
    var hintsRevealed by rememberSaveable { mutableIntStateOf(0) }
    var showClues by rememberSaveable { mutableStateOf(false) }
    var showApproach by rememberSaveable { mutableStateOf(false) }
    var showSolution by rememberSaveable { mutableStateOf(false) }
    var notes by rememberSaveable(p.id) { mutableStateOf(p.notes) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(p.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleBookmark) {
                        Icon(
                            if (p.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (p.isBookmarked) "Remove bookmark" else "Bookmark",
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagChip(difficultyLabel(p.difficulty), difficultyColor(p.difficulty))
                if (p.pattern.isNotBlank()) TagChip(p.pattern, Color(0xFF8B5CF6))
            }
            Text(p.statement, style = MaterialTheme.typography.bodyLarge)

            Text("Examples", style = MaterialTheme.typography.titleMedium)
            CodeBlock(p.examples)

            Text("Constraints", style = MaterialTheme.typography.titleMedium)
            Text(p.constraints, style = MaterialTheme.typography.bodyMedium)

            // ---- Pattern recognition training: read the signals before any code ----
            val clues = p.patternClues.split("|").filter { it.isNotBlank() }
            if (clues.isNotEmpty()) {
                if (!showClues) {
                    OutlinedButton(onClick = { showClues = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("\uD83D\uDD0D  How do I recognize the pattern?")
                    }
                } else {
                    val cluesColor = Color(0xFFF59E0B)
                    Surface(color = cluesColor.copy(alpha = 0.12f), shape = MaterialTheme.shapes.medium) {
                        Column(Modifier.padding(14.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("\uD83D\uDD0D  Signals in the statement",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = cluesColor)
                            clues.forEach {
                                Text("\u2022  $it", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text("First ask: what do these signals point to? Then check yourself below.",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            val approach = p.approach.split("|").filter { it.isNotBlank() }
            if (approach.isNotEmpty() && showClues) {
                if (!showApproach) {
                    OutlinedButton(onClick = { showApproach = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("\uD83E\uDDED  Walk me through choosing the algorithm")
                    }
                } else {
                    val approachColor = Color(0xFF14B8A6)
                    Surface(color = approachColor.copy(alpha = 0.12f), shape = MaterialTheme.shapes.medium) {
                        Column(Modifier.padding(14.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("\uD83E\uDDED  From signals to algorithm",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = approachColor)
                            approach.forEachIndexed { i, step ->
                                Text("${i + 1}.  $step", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            val hints = p.hints.split("|").filter { it.isNotBlank() }
            if (hints.isNotEmpty()) {
                Text("Hints", style = MaterialTheme.typography.titleMedium)
                hints.take(hintsRevealed).forEachIndexed { i, hint ->
                    Text("${i + 1}. $hint", style = MaterialTheme.typography.bodyMedium)
                }
                if (hintsRevealed < hints.size) {
                    OutlinedButton(onClick = { hintsRevealed++ }) {
                        Text("Reveal hint ${hintsRevealed + 1} of ${hints.size}")
                    }
                }
            }

            if (!showSolution) {
                Button(onClick = { showSolution = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Show solution")
                }
            } else {
                Text("Reference solution (Kotlin)", style = MaterialTheme.typography.titleMedium)
                CodeBlock(p.solutionKotlin)
                Text(p.explanation, style = MaterialTheme.typography.bodyMedium)
                Text("Time: ${p.timeComplexity} · Space: ${p.spaceComplexity}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Your notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            OutlinedButton(onClick = { viewModel.saveNotes(notes) }) { Text("Save notes") }

            Button(onClick = viewModel::toggleSolved, modifier = Modifier.fillMaxWidth()) {
                Text(if (p.isSolved) "Mark as unsolved" else "Mark as solved")
            }
        }
    }
}
