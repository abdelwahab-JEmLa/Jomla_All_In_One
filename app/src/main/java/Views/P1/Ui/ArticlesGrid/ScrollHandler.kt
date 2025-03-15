package Views.P1.Ui.ArticlesGrid

import android.util.Log
import androidx.compose.runtime.mutableStateOf

private const val TAG = "id2"

class ScrollHandler {
    val scrollToCategoryId = mutableStateOf<Long?>(null)
    
    fun scrollToCategory(categoryId: Long) {
        Log.d(TAG, "scrollToCategory called with categoryId: $categoryId")
        scrollToCategoryId.value = categoryId
        Log.d(TAG, "scrollToCategoryId.value updated to: ${scrollToCategoryId.value}")
    }
    
    // Add a debug method to check state
    fun logCurrentState() {
        Log.d(TAG, "Current scrollToCategoryId value: ${scrollToCategoryId.value}")
    }
}
