package com.luuzr.jielv.app.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import com.luuzr.jielv.core.designsystem.theme.NoteFlowOverlayAmbient
import com.luuzr.jielv.core.ui.ModuleVisualStyle
import com.luuzr.jielv.core.ui.MotionTokens
import com.luuzr.jielv.core.ui.TopModuleTabBar
import com.luuzr.jielv.feature.habits.HabitsRoute
import com.luuzr.jielv.feature.notes.NotesRoute
import com.luuzr.jielv.feature.tasks.TasksRoute
import com.luuzr.jielv.feature.today.TodayRoute
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.min

internal object RootRoutes {
    const val TopLevelCanvas = "top_level_canvas"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopLevelCanvasRoute(
    selectedDestination: TopLevelDestination,
    onDestinationChanged: (TopLevelDestination) -> Unit,
    onCreateTask: () -> Unit,
    onOpenTask: (String) -> Unit,
    onEditTask: (String) -> Unit,
    onOpenTasks: () -> Unit,
    onCreateHabit: () -> Unit,
    onOpenHabit: (String) -> Unit,
    onEditHabit: (String) -> Unit,
    onOpenHabits: () -> Unit,
    onCreateNote: () -> Unit,
    onOpenNote: (String) -> Unit,
    onEditNote: (String) -> Unit,
    onOpenNotes: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val destinations = TopLevelDestination.entries
    val selectedIndex = destinations.indexOf(selectedDestination).coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        pageCount = { destinations.size },
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedIndex) {
        if (pagerState.currentPage != selectedIndex) {
            pagerState.animateScrollToPage(selectedIndex)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .map { page -> destinations[page] }
            .distinctUntilChanged()
            .filter { destination -> destination != selectedDestination }
            .collect { destination ->
                onDestinationChanged(destination)
            }
    }

    val canvasPosition = (pagerState.currentPage + pagerState.currentPageOffsetFraction)
        .coerceIn(0f, (destinations.size - 1).toFloat())
    val lowerIndex = canvasPosition.toInt()
    val upperIndex = min(lowerIndex + 1, destinations.lastIndex)
    val motionFraction = (canvasPosition - lowerIndex).coerceIn(0f, 1f)
    val motionStyle = lerpModuleStyle(
        start = destinations[lowerIndex].visualStyle,
        end = destinations[upperIndex].visualStyle,
        fraction = motionFraction,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .drawWithCache {
                val canvasGlow = Brush.radialGradient(
                    colors = listOf(
                        motionStyle.ambientColor.copy(alpha = 0.16f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.46f, size.height * 0.08f),
                    radius = size.width * 0.80f,
                )
                onDrawBehind {
                    drawRect(canvasGlow)
                    drawRect(motionStyle.overlayColor.copy(alpha = 0.010f))
                    drawRect(NoteFlowOverlayAmbient.copy(alpha = 0.016f))
                }
            },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            TopModuleTabBar(
                destinations = destinations.toList(),
                selectedDestination = destinations[pagerState.currentPage],
                onDestinationSelected = { destination ->
                    val targetIndex = destinations.indexOf(destination)
                    if (targetIndex != -1) {
                        scope.launch {
                            pagerState.animateScrollToPage(targetIndex)
                        }
                    }
                },
                selectionPosition = canvasPosition,
                motionStyle = motionStyle,
            )

            Box(
                modifier = Modifier.weight(1f),
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 1,
                ) { page ->
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                    val normalizedOffset = pageOffset.absoluteValue.coerceIn(0f, 1f)
                    val pageAlpha = MotionTokens.CanvasAdjacentAlpha +
                        ((1f - MotionTokens.CanvasAdjacentAlpha) * (1f - normalizedOffset))
                    val pageScale = MotionTokens.CanvasAdjacentScale +
                        ((1f - MotionTokens.CanvasAdjacentScale) * (1f - normalizedOffset))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = -size.width * pageOffset * MotionTokens.CanvasParallaxFactor
                                alpha = pageAlpha
                                scaleX = pageScale
                                scaleY = pageScale
                            },
                    ) {
                        when (destinations[page]) {
                            TopLevelDestination.TODAY -> TodayRoute(
                                onCreateTask = onCreateTask,
                                onOpenTask = onOpenTask,
                                onEditTask = onEditTask,
                                onOpenTasks = onOpenTasks,
                                onCreateHabit = onCreateHabit,
                                onOpenHabit = onOpenHabit,
                                onEditHabit = onEditHabit,
                                onOpenHabits = onOpenHabits,
                                onOpenSettings = onOpenSettings,
                            )

                            TopLevelDestination.TASKS -> TasksRoute(
                                onCreateTask = onCreateTask,
                                onOpenTask = onOpenTask,
                                onEditTask = onEditTask,
                            )

                            TopLevelDestination.HABITS -> HabitsRoute(
                                onCreateHabit = onCreateHabit,
                                onOpenHabit = onOpenHabit,
                                onEditHabit = onEditHabit,
                            )

                            TopLevelDestination.NOTES -> NotesRoute(
                                onCreateNote = onCreateNote,
                                onOpenNote = onOpenNote,
                                onEditNote = onEditNote,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun lerpModuleStyle(
    start: ModuleVisualStyle,
    end: ModuleVisualStyle,
    fraction: Float,
): ModuleVisualStyle {
    return ModuleVisualStyle(
        accentColor = lerp(start.accentColor, end.accentColor, fraction),
        accentSoftColor = lerp(start.accentSoftColor, end.accentSoftColor, fraction),
        accentGlowColor = lerp(start.accentGlowColor, end.accentGlowColor, fraction),
        ambientColor = lerp(start.ambientColor, end.ambientColor, fraction),
        overlayColor = lerp(start.overlayColor, end.overlayColor, fraction),
        glassTintColor = lerp(start.glassTintColor, end.glassTintColor, fraction),
    )
}
