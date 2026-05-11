package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import androidx.compose.runtime.Composable
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.PendingAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun But1_Export_M8_Room_To_Csv(
    vm: FeatureID1_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    onCsvWritten: () -> Unit,
    action_definition: PendingAction,
) {
    AvertissementDialog(
        title        = action_definition.name,
        message      = "سيتم تصدير جميع بيانات M8BonVent إلى\nM8BonVent.csv\n" +
                "إذا كان الملف موجوداً سيتم تحديث الصفوف الموجودة وإضافة الجديدة.\n" +
                "هل تريد المتابعة؟",
        confirmLabel = "تصدير",
        onConfirm    = {
            onPendingClear()
            coroutineScope.launch(Dispatchers.IO) {
                vm.setter_LongOperations.export_M8_Room_To_Csv(
                    csv = M8BonVent.csv_test,
                )
                withContext(Dispatchers.Main) {
                    onCsvWritten()
                    onDismiss()
                }
            }
        },
        onDismiss    = onPendingClear,
    )
}
