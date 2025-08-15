package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.ViewList_M14VentPeriod
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_Client.Dialog_Filter_Client
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import org.koin.compose.koinInject

open class ViewModel_M14VentPeriod(val aCentralFacade: ACentralFacade) : ViewModel()

@Composable
fun ScreenM14VentPeriod(
    modifier: Modifier = Modifier,
    viewModel: ViewModel_M14VentPeriod = koinInject(),
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    list_M14VentPeriode: List<M14VentPeriode> = aCentralFacade.repositorysMainGetter.repo14VentPeriode.datasValue,
    relative_M9AppCompt: Z_AppCompt? =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Périodes de Vente de ${relative_M9AppCompt?.get_DebugInfos()}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Count: ${list_M14VentPeriode.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    ViewList_M14VentPeriod(
                        viewModel,
                        list_M14VentPeriode = list_M14VentPeriode,
                        relative_M9AppCompt=relative_M9AppCompt
                    )
                }
            }
        }
        val active_Central_Values = focusedValuesGetter.currentActiveFocuced_M14VentPeriode

        if (focusedValuesGetter.active_Central_Values.show_Dialog_filter_AChats_Par_Client_Acheteur == true) {
            Dialog_Filter_Client(onDismiss = {
                focusedValuesGetter.update_activeCentralValues(
                    focusedValuesGetter.active_Central_Values.copy(
                        show_Dialog_filter_AChats_Par_Client_Acheteur = false
                    )
                )
            }, activePeriod = active_Central_Values)
        }
    }
}
