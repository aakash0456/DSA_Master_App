package com.example.dsamaster.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dsamaster.feature.flashcards.CardEditorScreen
import com.example.dsamaster.feature.flashcards.DeckDetailScreen
import com.example.dsamaster.feature.flashcards.DecksScreen
import com.example.dsamaster.feature.home.HomeScreen
import com.example.dsamaster.feature.lessons.LessonScreen
import com.example.dsamaster.feature.onboarding.OnboardingScreen
import com.example.dsamaster.feature.patterns.PatternDetailScreen
import com.example.dsamaster.feature.patterns.PatternsScreen
import com.example.dsamaster.feature.problems.ProblemDetailScreen
import com.example.dsamaster.feature.problems.ProblemsScreen
import com.example.dsamaster.feature.progress.ProgressScreen
import com.example.dsamaster.feature.quiz.QuizScreen
import com.example.dsamaster.feature.review.ReviewScreen
import com.example.dsamaster.feature.search.SearchScreen
import com.example.dsamaster.feature.settings.SettingsScreen
import com.example.dsamaster.feature.topics.TopicDetailScreen
import com.example.dsamaster.feature.topics.TopicsScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val TOPICS = "topics"
    const val DECKS = "decks"
    const val PROBLEMS = "problems"
    const val PROGRESS = "progress"
    const val REVIEW = "review"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val PATTERNS = "patterns"
    fun topic(id: Long) = "topic/$id"
    fun lesson(id: Long) = "lesson/$id"
    fun deck(id: Long) = "deck/$id"
    fun quiz(id: Long) = "quiz/$id"
    fun problem(id: Long) = "problem/$id"
    fun pattern(id: Long) = "pattern/$id"
    fun cardEditor(deckId: Long, cardId: Long) = "card_editor/$deckId/$cardId"
}

private data class TopLevel(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
fun DsaNavHost(showOnboarding: Boolean) {
    val navController: NavHostController = rememberNavController()
    val topLevel = listOf(
        TopLevel(Routes.HOME, "Home") { Icon(Icons.Filled.Home, contentDescription = null) },
        TopLevel(Routes.TOPICS, "Topics") { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
        TopLevel(Routes.DECKS, "Cards") { Icon(Icons.Filled.Style, contentDescription = null) },
        TopLevel(Routes.PROBLEMS, "Problems") { Icon(Icons.Filled.Code, contentDescription = null) },
        TopLevel(Routes.PROGRESS, "Progress") { Icon(Icons.Filled.Insights, contentDescription = null) },
    )
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = topLevel.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) NavigationBar {
                topLevel.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Routes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = item.icon,
                        label = { Text(item.label) },
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (showOnboarding) Routes.ONBOARDING else Routes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(onFinished = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                })
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenTopic = { navController.navigate(Routes.topic(it)) },
                    onStartReview = { navController.navigate(Routes.REVIEW) },
                    onOpenSearch = { navController.navigate(Routes.SEARCH) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                )
            }
            composable(Routes.TOPICS) {
                TopicsScreen(onOpenTopic = { navController.navigate(Routes.topic(it)) })
            }
            composable("topic/{topicId}") { entry ->
                val id = entry.arguments?.getString("topicId")?.toLongOrNull() ?: return@composable
                TopicDetailScreen(
                    topicId = id,
                    onBack = { navController.popBackStack() },
                    onOpenLesson = { navController.navigate(Routes.lesson(it)) },
                    onOpenQuiz = { navController.navigate(Routes.quiz(it)) },
                    onOpenProblem = { navController.navigate(Routes.problem(it)) },
                )
            }
            composable("lesson/{lessonId}") { entry ->
                val id = entry.arguments?.getString("lessonId")?.toLongOrNull() ?: return@composable
                LessonScreen(lessonId = id, onBack = { navController.popBackStack() },
                    onOpenLesson = { navController.navigate(Routes.lesson(it)) { popUpTo("lesson/{lessonId}") { inclusive = true } } })
            }
            composable(Routes.DECKS) {
                DecksScreen(
                    onOpenDeck = { navController.navigate(Routes.deck(it)) },
                    onStartReview = { navController.navigate(Routes.REVIEW) },
                )
            }
            composable("deck/{deckId}") { entry ->
                val id = entry.arguments?.getString("deckId")?.toLongOrNull() ?: return@composable
                DeckDetailScreen(
                    deckId = id,
                    onBack = { navController.popBackStack() },
                    onEditCard = { cardId -> navController.navigate(Routes.cardEditor(id, cardId)) },
                )
            }
            composable("card_editor/{deckId}/{cardId}") { entry ->
                val deckId = entry.arguments?.getString("deckId")?.toLongOrNull() ?: return@composable
                val cardId = entry.arguments?.getString("cardId")?.toLongOrNull() ?: 0L
                CardEditorScreen(deckId = deckId, cardId = cardId, onDone = { navController.popBackStack() })
            }
            composable(Routes.REVIEW) {
                ReviewScreen(onClose = { navController.popBackStack() })
            }
            composable("quiz/{quizId}") { entry ->
                val id = entry.arguments?.getString("quizId")?.toLongOrNull() ?: return@composable
                QuizScreen(quizId = id, onClose = { navController.popBackStack() })
            }
            composable(Routes.PROBLEMS) {
                ProblemsScreen(
                    onOpenProblem = { navController.navigate(Routes.problem(it)) },
                    onOpenPatterns = { navController.navigate(Routes.PATTERNS) },
                )
            }
            composable(Routes.PATTERNS) {
                PatternsScreen(
                    onBack = { navController.popBackStack() },
                    onOpenPattern = { navController.navigate(Routes.pattern(it)) },
                )
            }
            composable("pattern/{patternId}") { entry ->
                val id = entry.arguments?.getString("patternId")?.toLongOrNull() ?: return@composable
                PatternDetailScreen(
                    patternId = id,
                    onBack = { navController.popBackStack() },
                    onOpenProblem = { navController.navigate(Routes.problem(it)) },
                )
            }
            composable("problem/{problemId}") { entry ->
                val id = entry.arguments?.getString("problemId")?.toLongOrNull() ?: return@composable
                ProblemDetailScreen(problemId = id, onBack = { navController.popBackStack() })
            }
            composable(Routes.PROGRESS) { ProgressScreen() }
            composable(Routes.SEARCH) {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onOpenTopic = { navController.navigate(Routes.topic(it)) },
                    onOpenLesson = { navController.navigate(Routes.lesson(it)) },
                    onOpenProblem = { navController.navigate(Routes.problem(it)) },
                )
            }
            composable(Routes.SETTINGS) { SettingsScreen(onBack = { navController.popBackStack() }) }
        }
    }
}
