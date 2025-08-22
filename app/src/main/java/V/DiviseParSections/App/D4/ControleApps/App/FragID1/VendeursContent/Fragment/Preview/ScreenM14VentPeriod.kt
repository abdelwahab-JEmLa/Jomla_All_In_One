package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod.Dialog_Period_Credits
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // State for dialog and selected period
    var showPeriodCreditsDialog by remember { mutableStateOf(false) }

    var active_M14VentPeriode_AuFilterAchats by remember {
        mutableStateOf(
            aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.active_Central_Values.active_M14VentPeriode_AuFilterAchats
        )
    }

    val sortedList_M14VentPeriod = list_M14VentPeriode.sortedByDescending {
        it.keyID
    }

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
                        text = "Count: ${sortedList_M14VentPeriod.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    ViewList_M14VentPeriod(
                        viewModel,
                        list_M14VentPeriode = sortedList_M14VentPeriod,
                        relative_M9AppCompt = relative_M9AppCompt,
                        onCalculatedAchatClick = {
                            showPeriodCreditsDialog = true
                        }
                    )
                }
            }
        }

        val active_Central_Values = focusedValuesGetter.currentActiveFocuced_M14VentPeriode

        if (focusedValuesGetter.active_Central_Values.vent_Au_Dialog_filter_AChats_Par_Client_Acheteur != null) {
            Dialog_Filter_Client(
                onDismiss = {
                    focusedValuesGetter.update_activeCentralValues(
                        focusedValuesGetter.active_Central_Values.copy(
                            show_Dialog_filter_AChats_Par_Client_Acheteur = false,
                            vent_Au_Dialog_filter_AChats_Par_Client_Acheteur = null
                        )
                    )
                },
                activePeriod = active_Central_Values,
                active_M14VentPeriode_AuFilterAchats = active_M14VentPeriode_AuFilterAchats
            )
        }

        val dialog_achats_ventPeriod =
            focusedValuesGetter.active_Central_Values.dialog_achats_ventPeriod

        if (dialog_achats_ventPeriod != null) {
            Dialog_Period_Credits(
                ventPeriod = dialog_achats_ventPeriod,
                repositorysMainGetter = aCentralFacade.repositorysMainGetter,
                onDismiss = {
                    focusedValuesGetter.update_activeCentralValues(
                        focusedValuesGetter.active_Central_Values.copy(
                            dialog_achats_ventPeriod = null
                        )
                    )
                }
            )
        }
    }
}
