package com.luuzr.jielv.app

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.luuzr.jielv.app.navigation.NoteFlowNavHost
import com.luuzr.jielv.app.navigation.RootRoutes
import com.luuzr.jielv.app.navigation.TopLevelDestination
import com.luuzr.jielv.core.designsystem.theme.MonetColorTokens
import com.luuzr.jielv.core.designsystem.theme.NoteFlowDesignTokens
import com.luuzr.jielv.core.ui.ModuleVisualStyle
import com.luuzr.jielv.core.ui.MotionTokens
import com.luuzr.jielv.core.ui.ProvideRadialExpansionController
import com.luuzr.jielv.core.ui.RadialExpansionController
import com.luuzr.jielv.core.ui.RadialExpansionOverlay
import com.luuzr.jielv.core.ui.rememberRadialExpansionController
import com.luuzr.jielv.feature.habits.HabitRoutes
import com.luuzr.jielv.feature.notes.NoteRoutes
import com.luuzr.jielv.feature.settings.SettingsRoutes
import com.luuzr.jielv.feature.tasks.TaskRoutes

@Composable
fun NoteFlowApp(
    pendingTaskDetailId: String? = null,
    pendingHabitDetailId: String? = null,
    onPendingTaskDetailConsumed: () -> Unit = {},
    onPendingHabitDetailConsumed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val designTokens = NoteFlowDesignTokens.colors
    val navController = rememberNavController()
    val radialExpansionController = rememberRadialExpansionController()
    var selectedTopLevelRoute by rememberSaveable { mutableStateOf(TopLevelDestination.TODAY.route) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentTopLevel = TopLevelDestination.entries.firstOrNull { it.route == selectedTopLevelRoute }
        ?: TopLevelDestination.TODAY
    val currentVisualStyle = resolveVisualStyle(currentDestination?.route, currentTopLevel)
    val isTopLevelRoute = currentDestination?.route == RootRoutes.TopLevelCanvas

    val animatedAmbientColor by animateColorAsState(
        targetValue = currentVisualStyle.ambientColor,
        animationSpec = tween(
            durationMillis = MotionTokens.DurationMedium,
            easing = MotionTokens.EasingEmphasized,
        ),
        label = "module_ambient_color",
    )
    val animatedOverlayColor by animateColorAsState(
        targetValue = currentVisualStyle.overlayColor,
        animationSpec = tween(
            durationMillis = MotionTokens.DurationMedium,
            easing = MotionTokens.EasingEmphasized,
        ),
        label = "module_overlay_color",
    )
    val targetOverlayAlpha = if (isTopLevelRoute) 0.004f else 0.010f
    val targetAmbientAlpha = if (isTopLevelRoute) 0.018f else 0.030f
    val targetTopGlowAlpha = if (isTopLevelRoute) 0.040f else 0.140f
    val targetLowerGlowAlpha = if (isTopLevelRoute) 0.010f else 0.055f
    val overlayAlpha by animateFloatAsState(
        targetValue = targetOverlayAlpha,
        animationSpec = MotionTokens.SpringSmooth,
        label = "module_overlay_alpha",
    )
    val ambientAlpha by animateFloatAsState(
        targetValue = targetAmbientAlpha,
        animationSpec = MotionTokens.SpringSmooth,
        label = "module_ambient_alpha",
    )
    val topGlowAlpha by animateFloatAsState(
        targetValue = targetTopGlowAlpha,
        animationSpec = MotionTokens.SpringSmooth,
        label = "module_top_glow_alpha",
    )
    val lowerGlowAlpha by animateFloatAsState(
        targetValue = targetLowerGlowAlpha,
        animationSpec = MotionTokens.SpringSmooth,
        label = "module_lower_glow_alpha",
    )

    LaunchedEffect(pendingTaskDetailId, pendingHabitDetailId) {
        if (!pendingTaskDetailId.isNullOrBlank()) {
            navController.navigate(TaskRoutes.detailRoute(pendingTaskDetailId)) {
                launchSingleTop = true
            }
            onPendingTaskDetailConsumed()
        }
        if (!pendingHabitDetailId.isNullOrBlank()) {
            navController.navigate(HabitRoutes.detailRoute(pendingHabitDetailId)) {
                launchSingleTop = true
            }
            onPendingHabitDetailConsumed()
        }
    }

    ProvideRadialExpansionController(radialExpansionController) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MonetColorTokens.canvas)
                .drawWithCache {
                    val baseLayer = Brush.verticalGradient(
                        colors = listOf(
                            designTokens.canvasRaised,
                            designTokens.background,
                            designTokens.background,
                        ),
                    )
                    val topGlow = Brush.radialGradient(
                        colors = listOf(
                            animatedAmbientColor.copy(alpha = topGlowAlpha),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.46f, size.height * 0.03f),
                        radius = size.width * 0.74f,
                    )
                    val lowerGlow = Brush.radialGradient(
                        colors = listOf(
                            currentVisualStyle.glassTintColor.copy(alpha = lowerGlowAlpha),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.92f, size.height * 0.88f),
                        radius = size.width * 0.42f,
                    )
                    onDrawBehind {
                        drawRect(baseLayer)
                        if (topGlowAlpha > 0.001f) drawRect(topGlow)
                        if (lowerGlowAlpha > 0.001f) drawRect(lowerGlow)
                        drawRect(animatedOverlayColor.copy(alpha = overlayAlpha))
                        drawRect(designTokens.overlayAmbient.copy(alpha = ambientAlpha))
                    }
                },
        ) {
            NoteFlowNavHost(
                navController = navController,
                selectedTopLevelDestination = currentTopLevel,
                onSelectedTopLevelDestinationChange = { destination ->
                    selectedTopLevelRoute = destination.route
                },
                radialExpansionController = radialExpansionController,
                modifier = Modifier.fillMaxSize(),
            )

            RadialExpansionOverlay(
                controller = radialExpansionController,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private fun resolveVisualStyle(
    route: String?,
    currentTopLevel: TopLevelDestination,
): ModuleVisualStyle {
    return when {
        route == RootRoutes.TopLevelCanvas || route == null -> currentTopLevel.visualStyle
        route.startsWith(TaskRoutes.listRoute) || route.startsWith("task/") -> TopLevelDestination.TASKS.visualStyle
        route.startsWith(HabitRoutes.listRoute) || route.startsWith("habit/") -> TopLevelDestination.HABITS.visualStyle
        route.startsWith(NoteRoutes.listRoute) || route.startsWith("note/") -> TopLevelDestination.NOTES.visualStyle
        route == SettingsRoutes.settingsRoute || route == SettingsRoutes.trashRoute || route == SettingsRoutes.backupRoute -> {
            TopLevelDestination.TODAY.visualStyle
        }
        else -> TopLevelDestination.TODAY.visualStyle
    }
}
