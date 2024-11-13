package B2_StartupAppDisplayerOfNewArticles.Ui

import B2_StartupAppDisplayerOfNewArticles.Main.StartUpNewArticlesViewModels
import B2_StartupAppDisplayerOfNewArticles.Main.UiState
import a_RoomDB.ArticlesBasesStatsTable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ArticleGridWithScrollbar(
    uiState: UiState,
    gridColumns: Int,
    filterText: String,
    showFilter: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: StartUpNewArticlesViewModels,
    reloadTrigger: Int,
    modifier: Modifier = Modifier,
    onClickToOpenWindos: (ArticlesBasesStatsTable, Int) -> Unit,
) {
    Box(modifier = modifier) {
        ArticleGrid(
            uiState = uiState,
            gridColumns = gridColumns,
            filterText = filterText,
            showFilter = showFilter,
            gridState = gridState,
            viewModel = viewModel,
            reloadTrigger = reloadTrigger,
            onClickToOpenWindos = onClickToOpenWindos,
            modifier = Modifier.fillMaxSize()
        )

        Scrollbar(
            state = gridState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 2.dp)
                .alpha(0.8f)
        )
    }
}

@Composable
fun Scrollbar(
    state: LazyStaggeredGridState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val isScrollInProgress = state.isScrollInProgress
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(0f) }

    // Get layout info
    val layoutInfo = state.layoutInfo
    val totalItems = layoutInfo.totalItemsCount
    val firstVisibleItem = state.firstVisibleItemIndex
    val visibleItems = layoutInfo.visibleItemsInfo.size

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val scrollbarHeight = 50.dp

    // Calculate maxOffset outside of the Box
    val maxOffset = remember(density, configuration.screenHeightDp) {
        with(density) {
            (configuration.screenHeightDp.dp - scrollbarHeight).toPx()
        }
    }

    Box(
        modifier = modifier
            .width(8.dp)
            .fillMaxHeight()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        if (totalItems > 0) {
            // Calculate scroll progress
            val scrollProgress = if (totalItems > visibleItems) {
                firstVisibleItem.toFloat() / (totalItems - visibleItems).coerceAtLeast(1)
            } else 0f

            val scrollbarOffset = maxOffset * scrollProgress.coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .offset { IntOffset(0, scrollbarOffset.roundToInt()) }
                    .width(8.dp)
                    .height(scrollbarHeight)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = if (isScrollInProgress || isDragging) 0.8f else 0.5f
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                dragOffset = 0f
                            },
                            onDragEnd = { isDragging = false },
                            onDragCancel = { isDragging = false },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount.y

                                // Calculate new scroll position
                                val newProgress = ((scrollbarOffset + dragOffset) / maxOffset)
                                    .coerceIn(0f, 1f)
                                val targetItem = (newProgress * (totalItems - visibleItems))
                                    .roundToInt()
                                    .coerceIn(0, totalItems - 1)

                                // Animate to the new scroll position
                                scope.launch {
                                    state.animateScrollToItem(targetItem)
                                }
                            }
                        )
                    }
            )
        }
    }
}
