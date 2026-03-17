package com.luuzr.jielv.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.luuzr.jielv.app.navigation.TopLevelDestination
import com.luuzr.jielv.core.designsystem.theme.NoteFlowGlassBorder
import com.luuzr.jielv.core.designsystem.theme.NoteFlowGlassSurface
import com.luuzr.jielv.core.designsystem.theme.NoteFlowTextSecondary
import com.luuzr.jielv.core.designsystem.theme.NoteFlowTextTertiary
import kotlin.math.abs

@Composable
fun TopModuleTabBar(
    destinations: List<TopLevelDestination>,
    selectedDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    selectionPosition: Float = destinations.indexOf(selectedDestination).coerceAtLeast(0).toFloat(),
    motionStyle: ModuleVisualStyle = selectedDestination.visualStyle,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        val tabCount = destinations.size.coerceAtLeast(1)
        val normalizedSelectionPosition = selectionPosition.coerceIn(0f, (tabCount - 1).toFloat())
        val slotWidth = maxWidth / tabCount
        val highlightWidth = slotWidth - 10.dp
        val highlightOffset = slotWidth * normalizedSelectionPosition + 5.dp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(NoteFlowGlassSurface.copy(alpha = 0.56f))
                .drawWithCache {
                    val overlay = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.30f),
                            motionStyle.glassTintColor.copy(alpha = 0.16f),
                            Color.Transparent,
                        ),
                    )
                    onDrawBehind {
                        drawRoundRect(
                            brush = overlay,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(64f, 64f),
                        )
                        drawRoundRect(
                            color = NoteFlowGlassBorder.copy(alpha = 0.36f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(64f, 64f),
                            style = Stroke(width = 1.dp.toPx()),
                        )
                    }
                },
        ) {
            TopTabHighlight(
                modifier = Modifier
                    .offset(x = highlightOffset, y = 7.dp)
                    .height(56.dp),
                width = highlightWidth,
                style = motionStyle,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                destinations.forEachIndexed { index, destination ->
                    val selectionProgress = (1f - abs(normalizedSelectionPosition - index)).coerceIn(0f, 1f)
                    val interactionSource = rememberPressInteractionSource()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(70.dp)
                            .noteFlowPressScale(interactionSource = interactionSource)
                            .graphicsLayer {
                                val scale = 0.96f + (selectionProgress * 0.04f)
                                scaleX = scale
                                scaleY = scale
                                translationY = (-2.dp.toPx()) * selectionProgress
                            }
                            .clip(RoundedCornerShape(28.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                            ) { onDestinationSelected(destination) }
                            .testTag("nav_${destination.route}"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = destination.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = lerp(
                                start = NoteFlowTextTertiary,
                                stop = MaterialTheme.colorScheme.onPrimary,
                                fraction = selectionProgress,
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopTabHighlight(
    width: androidx.compose.ui.unit.Dp,
    style: ModuleVisualStyle,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(width)
            .drawWithCache {
                val topGlow = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.14f),
                        Color.Transparent,
                    ),
                )
                val coreGlow = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.18f),
                        style.accentGlowColor.copy(alpha = 0.24f),
                        Color.Transparent,
                    ),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.52f, size.height * 0.38f),
                    radius = size.width * 0.56f,
                )
                val bodyGradient = Brush.verticalGradient(
                    colors = listOf(
                        style.accentGlowColor.copy(alpha = 0.66f),
                        style.accentColor.copy(alpha = 0.82f),
                    ),
                )
                onDrawBehind {
                    drawRoundRect(
                        color = style.accentColor.copy(alpha = 0.22f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f, size.height / 2f),
                    )
                    drawRoundRect(
                        brush = bodyGradient,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f, size.height / 2f),
                    )
                    drawRoundRect(
                        brush = coreGlow,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f, size.height / 2f),
                    )
                    drawRoundRect(
                        brush = topGlow,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f, size.height / 2f),
                    )
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.12f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.height / 2f, size.height / 2f),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }
            }
            .clip(RoundedCornerShape(28.dp)),
    )
}
