package com.luuzr.jielv.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.luuzr.jielv.core.ui.MotionTokens
import com.luuzr.jielv.core.ui.RadialExpansionController
import com.luuzr.jielv.feature.habits.HabitDetailRoute
import com.luuzr.jielv.feature.habits.HabitEditorRoute
import com.luuzr.jielv.feature.habits.HabitRoutes
import com.luuzr.jielv.feature.habits.HabitsRoute
import com.luuzr.jielv.feature.backup.BackupRestoreRoute
import com.luuzr.jielv.feature.notes.NoteDetailRoute
import com.luuzr.jielv.feature.notes.NoteEditorRoute
import com.luuzr.jielv.feature.notes.NoteRoutes
import com.luuzr.jielv.feature.notes.NotesRoute
import com.luuzr.jielv.feature.settings.SettingsRoute
import com.luuzr.jielv.feature.settings.SettingsRoutes
import com.luuzr.jielv.feature.tasks.TaskDetailRoute
import com.luuzr.jielv.feature.tasks.TaskEditorRoute
import com.luuzr.jielv.feature.tasks.TaskRoutes
import com.luuzr.jielv.feature.tasks.TasksRoute
import com.luuzr.jielv.feature.trash.TrashRoute
import com.luuzr.jielv.feature.today.TodayRoute

@Composable
fun NoteFlowNavHost(
    navController: NavHostController,
    selectedTopLevelDestination: TopLevelDestination,
    onSelectedTopLevelDestinationChange: (TopLevelDestination) -> Unit,
    radialExpansionController: RadialExpansionController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = RootRoutes.TopLevelCanvas,
        modifier = modifier,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthEnter,
                    easing = MotionTokens.EasingEmphasized,
                ),
            ) + slideInVertically(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthEnter,
                    easing = MotionTokens.EasingEmphasized,
                ),
                initialOffsetY = { (it * 0.08f).toInt() },
            ) + scaleIn(
                initialScale = 0.98f,
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthEnter,
                    easing = MotionTokens.EasingEmphasized,
                ),
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthExit,
                    easing = MotionTokens.EasingStandard,
                ),
            ) + scaleOut(
                targetScale = 0.95f,
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthExit,
                    easing = MotionTokens.EasingStandard,
                ),
            ) + slideOutVertically(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthExit,
                    easing = MotionTokens.EasingStandard,
                ),
                targetOffsetY = { (it * 0.04f).toInt() },
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthEnter,
                    easing = MotionTokens.EasingEmphasized,
                ),
            ) + slideInVertically(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthEnter,
                    easing = MotionTokens.EasingEmphasized,
                ),
                initialOffsetY = { -(it * 0.04f).toInt() },
            ) + scaleIn(
                initialScale = 0.985f,
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthEnter,
                    easing = MotionTokens.EasingEmphasized,
                ),
            )
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthExit,
                    easing = MotionTokens.EasingStandard,
                ),
            ) + slideOutVertically(
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthExit,
                    easing = MotionTokens.EasingStandard,
                ),
                targetOffsetY = { (it * 0.06f).toInt() },
            ) + scaleOut(
                targetScale = 0.95f,
                animationSpec = tween(
                    durationMillis = MotionTokens.DurationDepthExit,
                    easing = MotionTokens.EasingStandard,
                ),
            )
        },
    ) {
        composable(RootRoutes.TopLevelCanvas) {
            TopLevelCanvasRoute(
                selectedDestination = selectedTopLevelDestination,
                onDestinationChanged = onSelectedTopLevelDestinationChange,
                onCreateTask = {
                    navController.navigate(TaskRoutes.createRoute)
                },
                onOpenTask = { taskId ->
                    navController.navigate(TaskRoutes.detailRoute(taskId))
                },
                onEditTask = { taskId ->
                    navController.navigate(TaskRoutes.editRoute(taskId))
                },
                onOpenTasks = {
                    onSelectedTopLevelDestinationChange(TopLevelDestination.TASKS)
                },
                onCreateHabit = {
                    navController.navigate(HabitRoutes.createRoute)
                },
                onOpenHabit = { habitId ->
                    navController.navigate(HabitRoutes.detailRoute(habitId))
                },
                onEditHabit = { habitId ->
                    navController.navigate(HabitRoutes.editRoute(habitId))
                },
                onOpenHabits = {
                    onSelectedTopLevelDestinationChange(TopLevelDestination.HABITS)
                },
                onCreateNote = {
                    navController.navigate(NoteRoutes.createRoute)
                },
                onOpenNote = { noteId ->
                    navController.navigate(NoteRoutes.detailRoute(noteId))
                },
                onEditNote = { noteId ->
                    navController.navigate(NoteRoutes.editRoute(noteId))
                },
                onOpenNotes = {
                    onSelectedTopLevelDestinationChange(TopLevelDestination.NOTES)
                },
                onOpenSettings = {
                    navController.navigate(SettingsRoutes.settingsRoute)
                },
            )
        }
        composable(TopLevelDestination.TODAY.route) {
            LaunchedEffect(Unit) {
                onSelectedTopLevelDestinationChange(TopLevelDestination.TODAY)
                navController.navigate(RootRoutes.TopLevelCanvas) {
                    popUpTo(RootRoutes.TopLevelCanvas) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
        composable(TopLevelDestination.TASKS.route) {
            LaunchedEffect(Unit) {
                onSelectedTopLevelDestinationChange(TopLevelDestination.TASKS)
                navController.navigate(RootRoutes.TopLevelCanvas) {
                    popUpTo(RootRoutes.TopLevelCanvas) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
        composable(TaskRoutes.createRoute) {
            TaskEditorRoute(
                onNavigateBack = {
                    radialExpansionController.collapse(
                        onCollapsed = { navController.navigateUp() },
                    )
                },
            )
        }
        composable(
            route = TaskRoutes.editRoute,
            arguments = listOf(
                navArgument(TaskRoutes.taskIdArg) {
                    type = NavType.StringType
                },
            ),
        ) {
            TaskEditorRoute(
                onNavigateBack = { navController.navigateUp() },
            )
        }
        composable(
            route = TaskRoutes.detailRoute,
            arguments = listOf(
                navArgument(TaskRoutes.taskIdArg) {
                    type = NavType.StringType
                },
            ),
        ) {
            TaskDetailRoute(
                onNavigateBack = { navController.navigateUp() },
                onEditTask = { taskId ->
                    navController.navigate(TaskRoutes.editRoute(taskId))
                },
            )
        }
        composable(TopLevelDestination.HABITS.route) {
            LaunchedEffect(Unit) {
                onSelectedTopLevelDestinationChange(TopLevelDestination.HABITS)
                navController.navigate(RootRoutes.TopLevelCanvas) {
                    popUpTo(RootRoutes.TopLevelCanvas) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
        composable(HabitRoutes.createRoute) {
            HabitEditorRoute(
                onNavigateBack = {
                    radialExpansionController.collapse(
                        onCollapsed = { navController.navigateUp() },
                    )
                },
            )
        }
        composable(
            route = HabitRoutes.editRoute,
            arguments = listOf(
                navArgument(HabitRoutes.habitIdArg) {
                    type = NavType.StringType
                },
            ),
        ) {
            HabitEditorRoute(
                onNavigateBack = { navController.navigateUp() },
            )
        }
        composable(
            route = HabitRoutes.detailRoute,
            arguments = listOf(
                navArgument(HabitRoutes.habitIdArg) {
                    type = NavType.StringType
                },
            ),
        ) {
            HabitDetailRoute(
                onNavigateBack = { navController.navigateUp() },
                onEditHabit = { habitId ->
                    navController.navigate(HabitRoutes.editRoute(habitId))
                },
            )
        }
        composable(TopLevelDestination.NOTES.route) {
            LaunchedEffect(Unit) {
                onSelectedTopLevelDestinationChange(TopLevelDestination.NOTES)
                navController.navigate(RootRoutes.TopLevelCanvas) {
                    popUpTo(RootRoutes.TopLevelCanvas) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            }
        }
        composable(NoteRoutes.createRoute) {
            NoteEditorRoute(
                onNavigateBack = {
                    radialExpansionController.collapse(
                        onCollapsed = { navController.navigateUp() },
                    )
                },
            )
        }
        composable(
            route = NoteRoutes.editRoute,
            arguments = listOf(
                navArgument(NoteRoutes.noteIdArg) {
                    type = NavType.StringType
                },
            ),
        ) {
            NoteEditorRoute(
                onNavigateBack = { navController.navigateUp() },
            )
        }
        composable(
            route = NoteRoutes.detailRoute,
            arguments = listOf(
                navArgument(NoteRoutes.noteIdArg) {
                    type = NavType.StringType
                },
            ),
        ) {
            NoteDetailRoute(
                onNavigateBack = { navController.navigateUp() },
                onEditNote = { noteId ->
                    navController.navigate(NoteRoutes.editRoute(noteId))
                },
            )
        }
        composable(SettingsRoutes.settingsRoute) {
            SettingsRoute(
                onNavigateBack = { navController.navigateUp() },
                onOpenTrash = { navController.navigate(SettingsRoutes.trashRoute) },
                onOpenBackup = { navController.navigate(SettingsRoutes.backupRoute) },
            )
        }
        composable(SettingsRoutes.trashRoute) {
            TrashRoute(
                onNavigateBack = { navController.navigateUp() },
            )
        }
        composable(SettingsRoutes.backupRoute) {
            BackupRestoreRoute(
                onNavigateBack = { navController.navigateUp() },
            )
        }
    }
}
