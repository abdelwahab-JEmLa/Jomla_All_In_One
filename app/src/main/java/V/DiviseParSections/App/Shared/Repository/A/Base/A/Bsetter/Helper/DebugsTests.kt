package V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay

object DebugsTests {
    const val TAG = "DebugsTests"

    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.getSemanticsTag(nomVal: String, data: Any?, index: Int = 0, log:Boolean= true): Modifier {
         if (log) {
             log(nomVal, index, data)
         }

        return this.semantics(mergeDescendants = true) {
            set(SemanticsPropertyKey("${index + 1} TagDebug == [$nomVal]"), data)
        }
    }

    private fun log(nomVal: String, index: Int, data: Any?) {
        val logTag = "Debug_${nomVal}_${index + 1}"
        val dataString = when (data) {
            null -> "null"
            is String -> data
            is Number -> data.toString()
            is Boolean -> data.toString()
            else -> data.toString()
        }

        Log.d(nomVal, "[$logTag] $nomVal = $dataString")
    }


    @Composable
    fun DebugTestsPerformInitialSearch(
        enabled: Boolean,
        focusRequester: FocusRequester,
        onSearchQueryChange: (String) -> Unit,
        s: String
    ) {
        LaunchedEffect(enabled) {
            if (enabled) {
                delay(2000)
                onSearchQueryChange(s)
                focusRequester.requestFocus()
            }
        }
    }
}
