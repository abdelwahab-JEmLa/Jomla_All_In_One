package Application4.App.Modules.Wi.Module

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
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

@Composable
fun HandlePresenterScrollBroadcast(
    isHostPhone: Boolean,
    isConnected: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: A_ViewModel_NewProtoPatterns,
    onScrollHostChange: (Int) -> Unit = {}
) {
    var lastSentPosition by remember { mutableIntStateOf(-1) }

    LaunchedEffect(isHostPhone, isConnected) {
        if (!isHostPhone || !isConnected) return@LaunchedEffect

        snapshotFlow {
            Triple(
                gridState.firstVisibleItemIndex,
                gridState.firstVisibleItemScrollOffset,
                gridState.isScrollInProgress
            )
        }
            .distinctUntilChanged()
            .collect { (position, _, _) ->
                if (position != lastSentPosition) {
                    lastSentPosition = position
                    onScrollHostChange(position)
                    viewModel.sendOrderToClientDisplayerT(
                        WifiUpdateClientDisplayerStats_NewProto.ClientMainGridScrollPosition,
                        position
                    )
                }
            }
    }
}

@Composable
fun HandlePresenterClientScroll(
    isHostPhone: Boolean,
    scrollPosition: Int,
    gridState: LazyStaggeredGridState,
) {
    val scope = rememberCoroutineScope()
    var lastAppliedPosition by remember { mutableIntStateOf(-1) }
    var pendingPosition by remember { mutableIntStateOf(-1) }
    var isScrolling by remember { mutableStateOf(false) }

    LaunchedEffect(scrollPosition) {
        if (isHostPhone) return@LaunchedEffect
        if (scrollPosition == lastAppliedPosition) return@LaunchedEffect

        pendingPosition = scrollPosition

        if (isScrolling) return@LaunchedEffect

        isScrolling = true
        scope.launch {
            try {
                while (pendingPosition != lastAppliedPosition) {
                    val target = pendingPosition
                    gridState.scrollToItem(target, scrollOffset = 0)
                    lastAppliedPosition = target
                }
            } catch (e: Exception) {
                try {
                    gridState.scrollToItem(pendingPosition)
                    lastAppliedPosition = pendingPosition
                } catch (_: Exception) {
                }
            } finally {
                isScrolling = false
            }
        }
    }
}
