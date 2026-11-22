package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.local.PortalDatabase
import com.example.data.local.PortalRepository
import com.example.data.local.UserProgressData
import com.example.data.local.UserProgressStore
import com.example.data.remote.LeaderboardRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.GoogleSignInHelper
import com.example.ui.auth.authStateFlow
import com.example.ui.components.PortalHeader
import com.example.ui.components.PotionInfoSheet
import com.example.ui.components.QuizResultDialog
import com.example.ui.components.QuizSetupModal
import com.example.ui.screens.CoursesScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudyPlanScreen
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MaarifPortalTheme
import com.example.ui.theme.stringsForLanguage
import com.example.viewmodel.PortalTab
import com.example.viewmodel.PortalViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            MaarifPortalRoot()
        }
    }
}

@Composable
fun MaarifPortalRoot() {
    val context = LocalContext.current
    val progressStore = remember(context) { UserProgressStore(context.applicationContext) }
    val rootProgress by progressStore.progressFlow.collectAsStateWithLifecycle(initialValue = UserProgressData())
    val language = AppLanguage.fromCode(rootProgress.appLanguage)
    val strings = remember(language) { stringsForLanguage(language) }

    val auth = Firebase.auth
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)

    CompositionLocalProvider(LocalAppStrings provides strings) {
        MaarifPortalTheme(darkTheme = rootProgress.darkTheme) {
            val user = currentUser
            if (user == null) {
                // Auth Gate: Feature screens and Firestore snapshot listeners are NOT composed while signed out
                AuthScreen()
            } else {
                // Key composition and ViewModel by uid so sign-out / account switching cleanly disposes listeners
                key(user.uid) {
                    MaarifPortalAuthenticatedApp(
                        currentUserId = user.uid,
                        userDisplayName = user.displayName,
                        userEmail = user.email
                    )
                }
            }
        }
    }
}

@Composable
fun MaarifPortalAuthenticatedApp(
    currentUserId: String,
    userDisplayName: String?,
    userEmail: String?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val strings = LocalAppStrings.current

    val viewModel: PortalViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val firestore = FirebaseFirestore.getInstance(databaseId)
                val localDb = PortalDatabase.getInstance(app)
                val portalRepository = PortalRepository(
                    progressStore = UserProgressStore(app),
                    quizHistoryDao = localDb.quizHistoryDao(),
                    studyGoalDao = localDb.studyGoalDao()
                )
                PortalViewModel(
                    repository = portalRepository,
                    leaderboardRepository = LeaderboardRepository(firestore),
                    currentUserId = currentUserId,
                    initialGoogleDisplayName = userDisplayName
                )
            }
        }
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val leaderboardState by viewModel.leaderboardState.collectAsStateWithLifecycle()

    // Full-screen distraction-free Quiz Mode when active
    val activeQuiz = uiState.activeQuiz
    if (activeQuiz != null) {
        QuizScreen(
            session = activeQuiz,
            activeMultiplier = uiState.activeMultiplier,
            onSelectOption = viewModel::updateMultipleChoiceAnswer,
            onTextChange = viewModel::updateTextInputAnswer,
            onTrueFalseSelect = viewModel::updateTrueFalseAnswer,
            onFlipFlashcard = viewModel::flipFlashcard,
            onSubmitQuiz = viewModel::submitActiveQuiz,
            onExitQuiz = viewModel::exitQuiz
        )

        uiState.resultSummary?.let { summary ->
            QuizResultDialog(
                summary = summary,
                onReturnHome = viewModel::dismissResultAndReturnHome,
                onReviewAnswers = viewModel::dismissResultToReviewAnswers
            )
        }
        return
    }

    val navItems = listOf(
        Triple(PortalTab.COURSES, strings.navCourses, Icons.Rounded.School to Icons.Outlined.School),
        Triple(PortalTab.STUDY_PLAN, strings.navPlan, Icons.Rounded.CalendarMonth to Icons.Outlined.CalendarMonth),
        Triple(PortalTab.LEADERBOARD, strings.navLeaderboard, Icons.Rounded.Leaderboard to Icons.Outlined.Leaderboard),
        Triple(PortalTab.PROFILE, strings.navProfile, Icons.Rounded.Person to Icons.Outlined.Person),
        Triple(PortalTab.SETTINGS, strings.navSettings, Icons.Rounded.Settings to Icons.Outlined.Settings)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 700.dp

        Scaffold(
            topBar = {
                PortalHeader(
                    streakCount = uiState.progress.streakCount,
                    totalPoints = uiState.progress.totalPoints,
                    activeMultiplier = uiState.activeMultiplier,
                    multiplierRemainingMillis = uiState.multiplierRemainingMillis,
                    onPotionPillClick = { viewModel.togglePotionSheet(true) }
                )
            },
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        navItems.forEach { (tab, label, icons) ->
                            val selected = uiState.activeTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) icons.first else icons.second,
                                        contentDescription = label
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.testTag("nav_tab_${tab.name}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        navItems.forEach { (tab, label, icons) ->
                            val selected = uiState.activeTab == tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) icons.first else icons.second,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (uiState.activeTab) {
                        PortalTab.COURSES -> CoursesScreen(
                            selectedGrade = uiState.selectedGrade,
                            selectedCategory = uiState.selectedCategory,
                            subjects = uiState.subjectsForFilter,
                            subjectMasteryMap = uiState.subjectMasteryMap,
                            topRecommendation = uiState.recommendations.firstOrNull(),
                            onSelectGrade = viewModel::selectGrade,
                            onSelectCategory = viewModel::selectCategory,
                            onSubjectClick = { subject -> viewModel.openQuizSetup(subject) },
                            onQuickRecommendationClick = viewModel::launchRecommendedQuiz
                        )

                        PortalTab.STUDY_PLAN -> StudyPlanScreen(
                            selectedGrade = uiState.selectedGrade,
                            goalsWithProgress = uiState.goalsWithProgress,
                            recommendations = uiState.recommendations,
                            last7DaysCounts = uiState.last7DaysQuizCounts,
                            onAddGoal = viewModel::addStudyGoal,
                            onAdjustGoalTarget = viewModel::adjustGoalTarget,
                            onToggleGoalActive = viewModel::toggleGoalActive,
                            onDeleteGoal = viewModel::deleteStudyGoal,
                            onLaunchRecommendation = viewModel::launchRecommendedQuiz,
                            onPracticeGoal = { subject, topics ->
                                viewModel.openQuizSetup(subject, preselectedTopics = topics)
                            }
                        )

                        PortalTab.LEADERBOARD -> LeaderboardScreen(
                            state = leaderboardState,
                            onSyncNow = viewModel::syncLeaderboardNow,
                            onRetry = viewModel::retryLeaderboard
                        )

                        PortalTab.PROFILE -> ProfileScreen(
                            progress = uiState.progress,
                            activeMultiplier = uiState.activeMultiplier,
                            multiplierRemainingMillis = uiState.multiplierRemainingMillis,
                            studentRank = uiState.studentRank,
                            achievements = uiState.achievements,
                            history = uiState.quizHistory,
                            onUpdateProfile = viewModel::updateStudentProfile,
                            onOpenPotionSheet = { viewModel.togglePotionSheet(true) },
                            onNavigateToCourses = { viewModel.selectTab(PortalTab.COURSES) }
                        )

                        PortalTab.SETTINGS -> SettingsScreen(
                            progress = uiState.progress,
                            userEmail = userEmail,
                            onSaveProfile = viewModel::saveSettingsProfile,
                            onSelectLanguage = viewModel::setAppLanguage,
                            onToggleDarkTheme = viewModel::setDarkTheme,
                            onSyncLeaderboard = viewModel::syncLeaderboardNow,
                            onSignOut = {
                                scope.launch {
                                    GoogleSignInHelper.signOut(context)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // 2-Step Interactive Quiz Setup Modal
    uiState.setupModal?.let { modalState ->
        QuizSetupModal(
            state = modalState,
            onDismiss = viewModel::dismissQuizSetup,
            onToggleTopic = viewModel::toggleTopicSelection,
            onSelectAllTopics = viewModel::selectAllTopics,
            onClearAllTopics = viewModel::clearAllTopics,
            onGoToStep = viewModel::goToSetupStep,
            onToggleFormat = viewModel::toggleFormatSelection,
            onSelectQuestionCount = viewModel::selectQuestionCount,
            onStartQuiz = viewModel::startConfiguredQuiz,
            onStartSpecialExamMode = viewModel::startSpecialExamMode
        )
    }

    // Loot Drop & Active Potion Info Sheet
    if (uiState.showPotionSheet) {
        PotionInfoSheet(
            totalPoints = uiState.progress.totalPoints,
            activeMultiplier = uiState.activeMultiplier,
            remainingMillis = uiState.multiplierRemainingMillis,
            highestPotionWon = uiState.progress.highestPotionWon,
            perfectQuizCount = uiState.progress.perfectQuizCount,
            onDismiss = { viewModel.togglePotionSheet(false) }
        )
    }
}
