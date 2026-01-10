package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID4.Presentoire_App_Produits.Modules

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
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val TAG = "FragID4GridScroll"
        //<--
        //TODO(1): fait comme au autre on expand de metre le expanded item premie visble et de lock le scroll 3 sec pourevite aletoire scrolle
/**
 * Handle scroll broadcasting from HOST to CLIENT for FragID4 Presenter Grid
 * Utilise EXACTEMENT la même logique que HandleScrollBroadcast de FragID1
 * Utilise mainGridScrollPosition (pas besoin de nouveau champ)
 */
@Composable
fun HandlePresenterScrollBroadcast(
    isHostPhone: Boolean,
    isConnected: Boolean,
    gridState: LazyStaggeredGridState,
    viewModel: HeadViewModel,
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

                if (isDragging) {
                    isScrollInProgress = true
                    if (position != lastScrollPosition) {
                        lastScrollPosition = position
                        onScrollHostChange(position)
                        Log.d(TAG, "Sending scroll position to client: $position")
                        // Utilise le MÊME message type que FragID1
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
 * Utilise EXACTEMENT la même logique que HandleClientScroll de FragID1
 * Utilise mainGridScrollPosition (pas besoin de nouveau champ)
 */
@Composable
fun HandlePresenterClientScroll(
    isHostPhone: Boolean,
    scrollPosition: Int,
    gridState: LazyStaggeredGridState,
    tag: String = TAG
) {
    val scope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(scrollPosition) {
        if (isHostPhone) return@LaunchedEffect

        try {
            if (!isAnimating) {
                isAnimating = true
                scope.launch {
                    // Smooth scroll to the received position
                    gridState.animateScrollToItem(
                        index = scrollPosition,
                        scrollOffset = 0
                    )
                    delay(100)
                    isAnimating = false
                }
            }
        } catch (e: Exception) {
            isAnimating = false
            // Fallback to instant scroll
            gridState.scrollToItem(scrollPosition)
        }
    }
}
