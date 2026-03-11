package Application4.App.Fragment.Z.Components.Modules

import Application4.App.Fragment.ID1.Fragment.ViewModel.ViewModel_NewProtoPatterns
import Z_CodePartageEntreApps.Modules.ModuleID1.WifiTransferDatas.Module.WifiUpdateClientDisplayerStats
import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val TAG = "FragID4GridScroll"

@Composable
fun HandlePresenterScrollBroadcast(
    isHostPhone: Boolean,
    isConnected: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: ViewModel_NewProtoPatterns,
    onScrollHostChange: (Int) -> Unit = {}
) {
    var lastScrollPosition by remember { mutableStateOf(0) }
    var isScrollInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(isHostPhone, isConnected) {
        if (!isHostPhone || !isConnected) {
            Log.d(
                TAG,
                "HandlePresenterScrollBroadcast: Not handling scroll - isHost: $isHostPhone, isConnected: $isConnected"
            )
            return@LaunchedEffect
        }

        snapshotFlow {
            gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset
        }
            .distinctUntilChanged()
            .collect { (position, offset) ->
                Log.d(
                    TAG, """
                    Scroll Update:
                    - Position: $position
                    - Offset: $offset
                    - Last Position: $lastScrollPosition
                    - Is Scrolling: $isScrollInProgress
                """.trimIndent()
                )

                val isDragging = when {
                    gridState.layoutInfo.visibleItemsInfo.isEmpty() -> false
                    offset > 0 -> true
                    position != lastScrollPosition -> true
                    else -> false
                }

                if (isDragging || position != lastScrollPosition) {
                    isScrollInProgress = true
                    if (position != lastScrollPosition) {
                        lastScrollPosition = position
                        onScrollHostChange(position)
                        Log.d(TAG, "Sending scroll position to client: $position")
                        viewModel.sendOrderToClientDisplayer(
                            WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition.prefix,
                            position
                        )
                    }
                } else if (isScrollInProgress) {
                    isScrollInProgress = false
                    Log.d(TAG, "Final scroll position sent to client: $position")
                    viewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats.ClientMainGridScrollPosition.prefix,
                        position
                    )
                }
            }
    }
}

/**
 * Handle scroll receiving on CLIENT from HOST for FragID4 Presenter Grid
 * Now handles scroll updates more reliably after expand operations
 */
@Composable
fun HandlePresenterClientScroll(
    isHostPhone: Boolean,
    scrollPosition: Int,
    gridState: LazyStaggeredGridState,
    tag: String = TAG
) {
    val scope = rememberCoroutineScope()
    var lastReceivedPosition by remember { mutableStateOf(-1) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(scrollPosition) {
        if (isHostPhone) return@LaunchedEffect

        // Only scroll if position actually changed
        if (scrollPosition == lastReceivedPosition) return@LaunchedEffect

        Log.d(tag, "Client received scroll position: $scrollPosition (last: $lastReceivedPosition)")

        lastReceivedPosition = scrollPosition

        try {
            if (!isAnimating) {
                isAnimating = true
                scope.launch {
                    // Cancel any ongoing animation first
                    gridState.scrollToItem(scrollPosition, 0)
                    delay(50)
                    isAnimating = false
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error scrolling to position $scrollPosition", e)
            isAnimating = false
            try {
                gridState.scrollToItem(scrollPosition)
            } catch (e2: Exception) {
                Log.e(tag, "Fallback scroll also failed", e2)
            }
        }
    }
}
