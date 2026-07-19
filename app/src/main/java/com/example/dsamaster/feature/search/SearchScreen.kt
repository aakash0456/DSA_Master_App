package com.example.dsamaster.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.dsamaster.domain.ContentRepository
import com.example.dsamaster.domain.SearchResults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(private val content: ContentRepository) : ViewModel() {
    val query = MutableStateFlow("")
    val results = query
        .debounce(250)
        .mapLatest { content.search(it.trim()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchResults())
}

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenTopic: (Long) -> Unit,
    onOpenLesson: (Long) -> Unit,
    onOpenProblem: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
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
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.query.value = it },
                    label = { Text("Search topics, lessons, cards, problems") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (results.topics.isNotEmpty()) {
                item { Text("Topics", style = MaterialTheme.typography.titleMedium) }
                items(results.topics, key = { "t${it.id}" }) { t ->
                    Card(onClick = { onOpenTopic(t.id) }, modifier = Modifier.fillMaxWidth()) {
                        Text(t.title, Modifier.padding(16.dp))
                    }
                }
            }
            if (results.lessons.isNotEmpty()) {
                item { Text("Lessons", style = MaterialTheme.typography.titleMedium) }
                items(results.lessons, key = { "l${it.id}" }) { l ->
                    Card(onClick = { onOpenLesson(l.id) }, modifier = Modifier.fillMaxWidth()) {
                        Text(l.title, Modifier.padding(16.dp))
                    }
                }
            }
            if (results.problems.isNotEmpty()) {
                item { Text("Problems", style = MaterialTheme.typography.titleMedium) }
                items(results.problems, key = { "p${it.id}" }) { p ->
                    Card(onClick = { onOpenProblem(p.id) }, modifier = Modifier.fillMaxWidth()) {
                        Text(p.title, Modifier.padding(16.dp))
                    }
                }
            }
            if (results.cards.isNotEmpty()) {
                item { Text("Flashcards", style = MaterialTheme.typography.titleMedium) }
                items(results.cards, key = { "c${it.id}" }) { c ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(c.question, style = MaterialTheme.typography.bodyLarge)
                            Text(c.answer, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
