package com.example.dsamaster.feature.problems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.database.CodingProblemEntity
import com.example.dsamaster.core.designsystem.TagChip
import com.example.dsamaster.core.designsystem.accentAt
import com.example.dsamaster.core.designsystem.difficultyColor
import com.example.dsamaster.core.designsystem.difficultyLabel
import com.example.dsamaster.domain.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class ProblemFilter { ALL, UNSOLVED, SOLVED, BOOKMARKED }

/** Problems bucketed by the algorithmic pattern they train. */
data class PatternGroup(val pattern: String, val problems: List<CodingProblemEntity>)

@HiltViewModel
class ProblemsViewModel @Inject constructor(content: ContentRepository) : ViewModel() {
    val filter = MutableStateFlow(ProblemFilter.ALL)

    val groups = combine(content.problems(), filter) { list, f ->
        val filtered = when (f) {
            ProblemFilter.ALL -> list
            ProblemFilter.UNSOLVED -> list.filter { !it.isSolved }
            ProblemFilter.SOLVED -> list.filter { it.isSolved }
            ProblemFilter.BOOKMARKED -> list.filter { it.isBookmarked }
        }
        filtered.groupBy { it.pattern }.map { (pattern, problems) -> PatternGroup(pattern, problems) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

@Composable
fun ProblemsScreen(
    onOpenProblem: (Long) -> Unit,
    onOpenPatterns: () -> Unit,
    viewModel: ProblemsViewModel = hiltViewModel(),
) {
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Problems by pattern") }) }) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(
                    onClick = onOpenPatterns,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "\uD83D\uDCD6  Pattern playbook",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                            Text(
                                "Learn to RECOGNIZE which technique a problem wants — signals, anti-signals and templates for 21 patterns.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProblemFilter.entries.forEach { f ->
                        FilterChip(
                            selected = filter == f,
                            onClick = { viewModel.filter.value = f },
                            label = { Text(f.name.lowercase().replaceFirstChar(Char::uppercase)) },
                        )
                    }
                }
            }
            groups.forEachIndexed { groupIndex, group ->
                val accent = accentAt(groupIndex)
                item(key = "header-${group.pattern}") {
                    Row(
                        Modifier.padding(top = if (groupIndex == 0) 0.dp else 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.size(10.dp).clip(CircleShape).background(accent))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            group.pattern,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = accent,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${group.problems.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                items(group.problems, key = { it.id }) { p ->
                    Card(onClick = { onOpenProblem(p.id) }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(p.title, style = MaterialTheme.typography.titleMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TagChip(difficultyLabel(p.difficulty), difficultyColor(p.difficulty))
                                    TagChip(p.pattern, accent)
                                }
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
