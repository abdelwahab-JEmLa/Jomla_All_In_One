package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.M3.Actions.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import androidx.compose.runtime.Composable
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M3.Actions.PendingAction_M03
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun But3_Import_M03Csv_To_Room(
    vm: FeatureID1_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    action_definition: PendingAction_M03,
) {
    AvertissementDialog(
        title        = action_definition.name,
        message      = "سيتم استيراد بيانات M3CouleurProduitInfos.csv إلى قاعدة البيانات المحلية.\n" +
                "الصفوف الموجودة ستُحدَّث والجديدة ستُضاف.\n" +
                "هل تريد المتابعة؟",
        confirmLabel = "استيراد",
        onConfirm    = {
            onPendingClear()
            coroutineScope.launch(Dispatchers.IO) {
                vm.setter_LongOperations.import_M03Csv_To_Room(M3CouleurProduitInfos.csv_test)
                vm.reload()
                withContext(Dispatchers.Main) { onDismiss() }
            }
        },
        onDismiss    = onPendingClear,
    )
}
