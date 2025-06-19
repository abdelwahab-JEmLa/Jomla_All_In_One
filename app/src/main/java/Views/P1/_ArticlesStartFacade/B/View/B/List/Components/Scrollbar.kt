package Views.P1._ArticlesStartFacade.B.View.B.List.Components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Scrollbar(
    state: LazyStaggeredGridState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val isScrollInProgress = state.isScrollInProgress
    var isDragging by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }
    var expandJob by remember { mutableStateOf<Job?>(null) }

    // Get layout info
    val layoutInfo = state.layoutInfo
    val totalItems = layoutInfo.totalItemsCount
    val firstVisibleItem = state.firstVisibleItemIndex
    val visibleItems = layoutInfo.visibleItemsInfo.size

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val scrollbarHeight = 50.dp

    // Animate width changes
    val width by animateDpAsState(
        targetValue = if (isExpanded) 16.dp else 8.dp,
        label = "scrollbar width"
    )

    // Calculate maxOffset
    val maxOffset = remember(density, configuration.screenHeightDp) {
        with(density) {
            (configuration.screenHeightDp.dp - scrollbarHeight).toPx()
        }
    }

    if (totalItems > 0) {
        // Calculate scroll progress
        val scrollProgress = if (totalItems > visibleItems) {
            firstVisibleItem.toFloat() / (totalItems - visibleItems).coerceAtLeast(1)
        } else 0f

        val scrollbarOffset = maxOffset * scrollProgress.coerceIn(0f, 1f)

        // Container box that positions the scrollbar
        Box(
            modifier = modifier,
            contentAlignment = Alignment.CenterEnd
        ) {
            // Track
            Box(
                modifier = Modifier
                    .padding(end = 2.dp)  // Add some padding from the edge
                    .width(width)
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    )
            )

            // Thumb
            Box(
                modifier = Modifier
                    .padding(end = 2.dp)  // Match track padding
                    .offset { IntOffset(0, scrollbarOffset.roundToInt()) }
                    .width(width)
                    .height(scrollbarHeight)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = if (isScrollInProgress || isDragging || isExpanded) 0.8f else 0.5f
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                expandJob?.cancel()
                                isExpanded = true
                                expandJob = scope.launch {
                                    delay(300)
                                    isExpanded = false
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                isExpanded = true
                                dragOffset = 0f
                                expandJob?.cancel()
                            },
                            onDragEnd = {
                                isDragging = false
                                expandJob = scope.launch {
                                    delay(300)
                                    isExpanded = false
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                expandJob = scope.launch {
                                    delay(300)
                                    isExpanded = false
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount.y

                                // Calculate new scroll position
                                val newProgress = ((scrollbarOffset + dragOffset) / maxOffset)
                                    .coerceIn(0f, 1f)
                                val targetItem = (newProgress * (totalItems - visibleItems))
                                    .roundToInt()
                                    .coerceIn(0, totalItems - 1)

                                // Scroll immediately to the new position
                                scope.launch {
                                    state.scrollToItem(targetItem)
                                }
                            }
                        )
                    }
            )
        }
    }
}
