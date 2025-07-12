package V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay

object DebugsTests {
    const val TAG = "DebugsTests"
    fun Modifier.getSemanticsTag_By_datas_A_Affiche_Au_Nom(
        datas_A_Affiche_Au_Nom: Any?=null,
        nomVal: String = "",
        data: Any?=null,
        index: Int = 0,
        log: Boolean = false
    ): Modifier {
        if (log) {
            log(nomVal, index, data)
        }

        return this.semantics(mergeDescendants = true) {
            val old = "${index + 1} TagDebug == [$nomVal]"
            val new = "${index + 1}-SemDeb.$nomVal [${datas_A_Affiche_Au_Nom.toString()}]"
            val name = if (datas_A_Affiche_Au_Nom != null) new else old
            val dataWithNullSafety = data ?: datas_A_Affiche_Au_Nom
            set(SemanticsPropertyKey(name), dataWithNullSafety)
        }
    }


    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.getSemanticsTag(
        data: Any?,
        nomVal: String,
        index: Int = 0,
        log: Boolean = true
    ): Modifier {
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

        Log.d("getSemanticsTag", "[$logTag] $nomVal = $dataString")
    }


    suspend fun DebugTestsPerformInitialSearch(
        enabled: Boolean,
        focusRequester: FocusRequester,
        onSearchTextChange: (String) -> Unit,
        searchQuery: String
    ) {
        if (!enabled) return

        try {
            // Wait for UI to be ready
            delay(200)

            // Check if focusRequester is properly initialized before using it
            // This is the line that was causing the crash (line 57)
            focusRequester.requestFocus()

            // Perform your search logic
            onSearchTextChange(searchQuery)

        } catch (e: IllegalStateException) {
            // Log the error instead of crashing
            println("Focus request failed in DebugTestsPerformInitialSearch: ${e.message}")
            // Still execute the search even if focus fails
            onSearchTextChange(searchQuery)
        }
    }


}
