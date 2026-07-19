package com.example.dsamaster.feature.flashcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.dsamaster.core.database.FlashcardEntity
import com.example.dsamaster.domain.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardEditorUiState(
    val question: String = "",
    val answer: String = "",
    val explanation: String = "",
    val codeSnippet: String = "",
    val loaded: Boolean = false,
)

@HiltViewModel
class CardEditorViewModel @Inject constructor(
    private val repo: FlashcardRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val deckId: Long = checkNotNull(savedStateHandle.get<String>("deckId")).toLong()
    private val cardId: Long = checkNotNull(savedStateHandle.get<String>("cardId")).toLong()

    private val _uiState = MutableStateFlow(CardEditorUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val card = if (cardId != 0L) repo.card(cardId) else null
            _uiState.value = CardEditorUiState(
                question = card?.question.orEmpty(),
                answer = card?.answer.orEmpty(),
                explanation = card?.explanation.orEmpty(),
                codeSnippet = card?.codeSnippet.orEmpty(),
                loaded = true,
            )
        }
    }

    fun onQuestion(v: String) = _uiState.value.let { _uiState.value = it.copy(question = v) }
    fun onAnswer(v: String) = _uiState.value.let { _uiState.value = it.copy(answer = v) }
    fun onExplanation(v: String) = _uiState.value.let { _uiState.value = it.copy(explanation = v) }
    fun onCode(v: String) = _uiState.value.let { _uiState.value = it.copy(codeSnippet = v) }

    fun save(onDone: () -> Unit) {
        val s = _uiState.value
        if (s.question.isBlank() || s.answer.isBlank()) return
        viewModelScope.launch {
            val existing = if (cardId != 0L) repo.card(cardId) else null
            repo.saveCard(
                (existing ?: FlashcardEntity(deckId = deckId, topicSlug = null, question = "", answer = ""))
                    .copy(
                        question = s.question.trim(),
                        answer = s.answer.trim(),
                        explanation = s.explanation.trim(),
                        codeSnippet = s.codeSnippet.ifBlank { null },
                    )
            )
            onDone()
        }
    }
}

@Composable
fun CardEditorScreen(
    deckId: Long,
    cardId: Long,
    onDone: () -> Unit,
    viewModel: CardEditorViewModel = hiltViewModel(),
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (cardId == 0L) "New card" else "Edit card") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(ui.question, viewModel::onQuestion, label = { Text("Question *") },
                modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.answer, viewModel::onAnswer, label = { Text("Answer *") },
                modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.explanation, viewModel::onExplanation, label = { Text("Explanation") },
                modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.codeSnippet, viewModel::onCode, label = { Text("Code snippet (optional)") },
                modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(
                onClick = { viewModel.save(onDone) },
                enabled = ui.question.isNotBlank() && ui.answer.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Save card") }
        }
    }
}
