package Application2.App.Fragment

import android.util.Log
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "App2GridScroll"


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
