package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Modules.Ui.B.UI.DebugKey
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.IRecordingHandler
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import android.annotation.SuppressLint
import androidx.compose.ui.Modifier

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
    @SuppressLint("ModifierFactoryUnreferencedReceiver")
    fun Modifier.getSemanticsTagFocucedVars(getter: FocusedValuesGetter): Modifier {
        val map = buildMap {
            with(getter) {
                put(
                    "onVentM2ClientInfos",
                    activeOnVent_M2Client?.let {
                        with(it) {
                            nom
                        }
                    } ?: "null"
                )
                put(
                    "onVentM8BonVent",
                    activeonVent_M8BonVent?.let {
                        with(it) {
                            "$parent_M2Client_DebugInfos/$etateActuellementEst"
                        }
                    } ?: "null"
                )
                put(
                    "focused_M1ProduitInfos_Pour_PrixDifineur", getter
                        .focused_M1ProduitInfos_Pour_PrixDifineur?.let {
                            it.nom + it.keyID
                        } ?: "null")

                put(
                    "onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent",
                    getter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.map {
                        "${it.parent_M1Produit_DebugInfos} / ${it.parent_M1Produit_KeyId}"
                    }
                )
                put(
                    "focused_ListM10OpeVentCouleur_Par_PD_M1Produit",
                    getter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit.map { it.getDebugInfos() }
                )
            }
        }

        return map.entries.foldIndexed(this) { index, modifier, (key, value) ->
            modifier.getSemanticsTag_By_datas_A_Affiche_Au_Nom(index + 6, key, value)
        }
    }

    fun throw_Runtime_Erreur(parent_Lenceur: String?=null): Nothing {
        throw RuntimeException(
            "RuntimeException $parent_Lenceur"
        )
    }
}
