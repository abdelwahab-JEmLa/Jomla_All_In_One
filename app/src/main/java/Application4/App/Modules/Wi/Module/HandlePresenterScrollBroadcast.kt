package Application4.App.Modules.Wi.Module

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val TAG = "FragID4GridScroll"

@Composable
fun HandlePresenterScrollBroadcast(
    isHostPhone: Boolean,
    isConnected: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: A_ViewModel_NewProtoPatterns,
    onScrollHostChange: (Int) -> Unit = {}
) {                         //<--

    &
//TODO(1): pk le scroll ne marche pas
    var lastSentPosition by remember { mutableIntStateOf(-1) }

    LaunchedEffect(isHostPhone, isConnected) {
        if (!isHostPhone || !isConnected) {
            Log.d(TAG, "HandlePresenterScrollBroadcast: inactive — isHost=$isHostPhone, isConnected=$isConnected")
            return@LaunchedEffect
        }

        Log.d(TAG, "HandlePresenterScrollBroadcast: starting scroll observation")

        snapshotFlow {
            Triple(
                gridState.firstVisibleItemIndex,
                gridState.firstVisibleItemScrollOffset,
                gridState.isScrollInProgress   // replaces the manual isDragging heuristic
            )
        }
            .distinctUntilChanged()
            .collect { (position, offset, isScrolling) ->
                Log.d(
                    TAG, "Scroll snapshot — position=$position, offset=$offset, " +
                            "isScrollInProgress=$isScrolling, lastSent=$lastSentPosition"
                )

                if (position != lastSentPosition) {
                    lastSentPosition = position
                    onScrollHostChange(position)
                    Log.d(TAG, "Sending scroll position=$position to client (offset=$offset, scrolling=$isScrolling)")
                    viewModel.sendOrderToClientDisplayerT(
                        WifiUpdateClientDisplayerStats_NewProto.ClientMainGridScrollPosition,
                        position
                    )
                } else {
                    Log.v(TAG, "Position unchanged ($position), skipping send")
                }
            }
    }
}

/**
 * Handle scroll receiving on CLIENT from HOST for FragID4 Presenter Grid.
 *
 * Tracks the latest requested position so rapid updates from the host never
 * cause the client to fall behind: even if a scroll is in progress, the next
 * emission will always catch up to the most recent position.
 */
@Composable
fun HandlePresenterClientScroll(
    isHostPhone: Boolean,
    scrollPosition: Int,
    gridState: LazyStaggeredGridState,
    tag: String = TAG
) {
    val scope = rememberCoroutineScope()
    var lastAppliedPosition by remember { mutableIntStateOf(-1) }
    // Always holds the freshest position requested by the host.
    var pendingPosition by remember { mutableIntStateOf(-1) }
    // True while a coroutine is driving the grid; it will drain pendingPosition before exiting.
    var isScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(scrollPosition) {
        if (isHostPhone) return@LaunchedEffect
        if (scrollPosition == lastAppliedPosition) {
            Log.v(tag, "Client: position $scrollPosition already applied, skipping")
            return@LaunchedEffect
        }

        Log.d(tag, "Client: received position=$scrollPosition (last applied=$lastAppliedPosition, scrolling=$isScrolling)")

        // Always update the target — the running coroutine reads this before exiting.
        pendingPosition = scrollPosition

        if (isScrolling) {
            Log.d(tag, "Client: scroll already in progress, updated pendingPosition=$pendingPosition")
            return@LaunchedEffect
        }

        isScrolling = true
        scope.launch {
            try {
                // Loop until we've caught up with every position that arrived
                // while the coroutine was running (prevents falling behind on fast scrolls).
                while (pendingPosition != lastAppliedPosition) {
                    val target = pendingPosition
                    Log.d(tag, "Client: scrolling to target=$target")
                    gridState.scrollToItem(target, scrollOffset = 0)
                    lastAppliedPosition = target
                    Log.d(tag, "Client: reached target=$target")
                }
            } catch (e: Exception) {
                Log.e(tag, "Client: error scrolling to $pendingPosition — ${e.message}", e)
                try {
                    gridState.scrollToItem(pendingPosition)
                    lastAppliedPosition = pendingPosition
                } catch (e2: Exception) {
                    Log.e(tag, "Client: fallback scroll also failed — ${e2.message}", e2)
                }
            } finally {
                isScrolling = false
            }
        }
    }
}
