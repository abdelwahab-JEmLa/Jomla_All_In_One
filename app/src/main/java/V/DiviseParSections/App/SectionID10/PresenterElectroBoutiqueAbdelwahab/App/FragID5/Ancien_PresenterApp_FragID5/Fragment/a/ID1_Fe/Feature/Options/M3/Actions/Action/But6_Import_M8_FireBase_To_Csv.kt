package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M3.Actions.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import android.util.Log
import androidx.compose.runtime.Composable
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M3.Actions.PendingAction_M03
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "But6_FireBaseToCsv"

@Composable
fun But6_Import_M03_FireBase_To_Csv(
    vm: FeatureID1_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    onCsvWritten: () -> Unit,
    action_definition: PendingAction_M03,
) {
    AvertissementDialog(
        title        = action_definition.name,
        message      = "سيتم استيراد بيانات M3CouleurProduitInfos من Firebase Realtime Database إلى\nM3CouleurProduitInfos.csv\n" +
                "الصفوف الموجودة ستُحدَّث والجديدة ستُضاف.\n" +
                "هل تريد المتابعة؟",
        confirmLabel = "استيراد",
        onConfirm    = {
            onPendingClear()
            coroutineScope.launch(Dispatchers.IO) {
                runCatching {
                    vm.setter_LongOperations.import_M03_FireBase_To_Csv(
                        refDataBase = M3CouleurProduitInfos.ref_Test,
                        csvFile     = M3CouleurProduitInfos.csv_test,
                    )
                }.onFailure { err ->
                    Log.e(TAG, "Échec Firebase → CSV | raison=${err.message ?: "inconnue"}", err)
                }.onSuccess {
                    Log.d(TAG, "Données Firebase importées avec succès vers CSV.")
                    withContext(Dispatchers.Main) { onCsvWritten() }
                }
                withContext(Dispatchers.Main) { onDismiss() }
            }
        },
        onDismiss    = onPendingClear,
    )
}
