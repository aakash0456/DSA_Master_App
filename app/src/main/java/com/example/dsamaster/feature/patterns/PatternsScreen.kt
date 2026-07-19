package com.example.dsamaster.feature.patterns

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.dsamaster.core.designsystem.accentAt
import com.example.dsamaster.domain.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PatternsViewModel @Inject constructor(content: ContentRepository) : ViewModel() {
    val patterns = content.patterns()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

private val pickSteps = listOf(
    "Is the data SORTED, or monotonic in some property? → Two Pointers or Binary Search.",
    "Contiguous subarray/substring with a rule about its contents? → Sliding Window (Prefix Sum if negatives break it).",
    "\u201CHave I seen this?\u201D, pairs on unsorted data, grouping by key? → Hashing.",
    "Most recent unresolved thing matters, or \u201Cnext greater element\u201D? → Stack.",
    "Linked list + cycles, middles, or O(1)-space rewiring? → Fast & Slow Pointers or In-place Reversal.",
    "Shortest path / fewest steps, unweighted? → BFS. Explore everything or all combinations? → DFS / Backtracking.",
    "A list of INTERVALS to merge, book, or schedule? → Merge Intervals.",
    "Values limited to 1..n plus O(1) space? → Cyclic Sort. \u201CNext greater element\u201D? → Monotonic Stack.",
    "\u201CTop K\u201D or a running min/max over changing data? → Heap. Running MEDIAN of a stream? → Two Heaps.",
    "Prerequisites / build order? → Topological Sort. \u201CAre these two connected?\u201D as edges arrive? → Union-Find.",
    "The word \u201Cprefix\u201D, autocomplete, or a big word dictionary? → Trie.",
    "Counting ways or optimizing a sequence of interacting choices? → DP. Locally-safe choices with a one-line proof? → Greedy.",
    "Space capped at O(1) where a hash set feels natural? → Bit Manipulation.",
)

@Composable
fun PatternsScreen(
    onBack: () -> Unit,
    onOpenPattern: (Long) -> Unit,
    viewModel: PatternsViewModel = hiltViewModel(),
) {
    val patterns by viewModel.patterns.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pattern playbook") },
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
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Column(Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "\uD83E\uDDED  How to pick a pattern",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            "Run through these questions in order — the first \u201Cyes\u201D usually names your pattern:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        pickSteps.forEachIndexed { i, step ->
                            Text(
                                "${i + 1}. $step",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
            itemsIndexed(patterns, key = { _, p -> p.id }) { index, pattern ->
                val accent = accentAt(index)
                Card(onClick = { onOpenPattern(pattern.id) }, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(40.dp).clip(CircleShape).background(accent.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                pattern.name.take(1),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = accent,
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                pattern.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = accent,
                            )
                            Text(
                                pattern.tagline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
