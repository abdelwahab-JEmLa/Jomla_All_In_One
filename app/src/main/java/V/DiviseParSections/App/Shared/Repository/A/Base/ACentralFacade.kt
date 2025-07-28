package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Modules.Ui.B.UI.DebugKey
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.IRecordingHandler
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay

class ACentralFacade(
    val repositorysMainGetter: RepositorysMainGetter,
    val repositorysMainSetter: RepositorysMainSetter,
    val focusedActiveValuesFacade: FocusedActiveValuesFacade,
    val modulesCentral: ModulesCentral
)

class FocusedActiveValuesFacade(val focusedValuesGetter: FocusedValuesGetter, val focusedValuesSetter: FocusedValuesSetter)

class ModulesCentral(
    val printReceiptHandler: PrintReceiptHandler_Juil,
    val recordingHandler: IRecordingHandler,
    val fragmentNavigationHandler: FragmentNavigationHandler,
    val audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler,
    val debugKey: DebugKey
)

object functions_central{
    fun runtime_throw_Erreur_Pour_Regle_Le_Real_Bug(parent_Lenceur: String?=null): Nothing {
        throw RuntimeException(
            "RuntimeException $parent_Lenceur"
        )
    }
}

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
        log: Boolean = false
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
            delay(200)

            focusRequester.requestFocus()

            onSearchTextChange(searchQuery)

        } catch (e: IllegalStateException) {
            println("Focus request failed in DebugTestsPerformInitialSearch: ${e.message}")
            onSearchTextChange(searchQuery)
        }
    }


}
