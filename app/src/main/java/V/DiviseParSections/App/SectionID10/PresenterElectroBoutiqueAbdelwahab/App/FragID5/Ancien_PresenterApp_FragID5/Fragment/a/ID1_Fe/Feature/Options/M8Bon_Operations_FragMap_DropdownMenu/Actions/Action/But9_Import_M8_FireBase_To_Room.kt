package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import android.util.Log
import androidx.compose.runtime.Composable
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.PendingAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "But6_FireBaseToRoom"

@Composable
fun But9_Import_M8_FireBase_To_Room(
    vm: FeatureID1_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    action_definition: PendingAction,
) {
    AvertissementDialog(
        title        = action_definition.name,
        message      = "سيتم استيراد بيانات M8BonVent من Firebase Realtime Database مباشرةً إلى قاعدة البيانات المحلية.\n" +
                "الصفوف الموجودة ستُحدَّث والجديدة ستُضاف.\n" +
                "هل تريد المتابعة؟",
        confirmLabel = "استيراد",
        onConfirm    = {
            onPendingClear()
            coroutineScope.launch(Dispatchers.IO) {
                runCatching {
                    vm.setter_LongOperations.import_M8_FireBase_To_Room(
                        refDataBase = M8BonVent.ref_Test,
                    )
                    vm.reload()
                }.onFailure { err ->
                    Log.e(TAG, "Échec Firebase → Room | raison=${err.message ?: "inconnue"}", err)
                }.onSuccess {
                    Log.d(TAG, "Données Firebase importées avec succès vers Room.")
                }
                withContext(Dispatchers.Main) { onDismiss() }
            }
        },
        onDismiss    = onPendingClear,
    )
}
