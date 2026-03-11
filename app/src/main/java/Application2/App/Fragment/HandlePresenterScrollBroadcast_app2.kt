package Application2.App.Fragment

import Application2.App.App.ViewModel.ViewModel_MainFragment
import EntreApps.Shared.Modules.Utils.M1.Module.WifiUpdateClientDisplayerStats_app2
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

private const val TAG = "App2GridScroll"

/**
 * HOST side — observes the grid scroll position and broadcasts it to the client
 * via WifiUpdateClientDisplayerStats_app2.ClientMainGridScrollPosition.
 * Only active when isHostPhone == true && isConnected == true.
 */
@Composable
fun HandlePresenterScrollBroadcast_app2(
    isHostPhone: Boolean,
    isConnected: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: ViewModel_MainFragment,
) {
    var lastScrollPosition by remember { mutableStateOf(0) }
    var isScrollInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(isHostPhone, isConnected) {
        if (!isHostPhone || !isConnected) {
            Log.d(TAG, "Broadcast skip — isHost=$isHostPhone isConnected=$isConnected")
            return@LaunchedEffect
        }

        snapshotFlow { gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { (position, offset) ->
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
                        Log.d(TAG, "▶ Sending scroll pos to client: $position")
                        viewModel.sendOrderToClientDisplayer(
                            WifiUpdateClientDisplayerStats_app2.ClientMainGridScrollPosition.prefix,
                            position
                        )
                    }
                } else if (isScrollInProgress) {
                    isScrollInProgress = false
                    Log.d(TAG, "■ Final scroll pos to client: $position")
                    viewModel.sendOrderToClientDisplayer(
                        WifiUpdateClientDisplayerStats_app2.ClientMainGridScrollPosition.prefix,
                        position
                    )
                }
            }
    }
}

/**
 * CLIENT side — receives a scroll position from the host and animates the grid to it.
 * Only active when isHostPhone == false.
 */
@Composable
fun HandlePresenterClientScroll_app2(
    isHostPhone: Boolean,
    scrollPosition: Int,
    gridState: LazyStaggeredGridState,
) {
    val scope = rememberCoroutineScope()
    var lastReceivedPosition by remember { mutableStateOf(-1) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(scrollPosition) {
        if (isHostPhone) return@LaunchedEffect
        if (scrollPosition == lastReceivedPosition) return@LaunchedEffect

        Log.d(TAG, "◀ Client received scroll pos: $scrollPosition (last: $lastReceivedPosition)")
        lastReceivedPosition = scrollPosition

        try {
            if (!isAnimating) {
                isAnimating = true
                scope.launch {
                    gridState.scrollToItem(scrollPosition, 0)
                    delay(50)
                    isAnimating = false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scrolling to $scrollPosition", e)
            isAnimating = false
            try { gridState.scrollToItem(scrollPosition) }
            catch (e2: Exception) { Log.e(TAG, "Fallback scroll also failed", e2) }
        }
    }
}
