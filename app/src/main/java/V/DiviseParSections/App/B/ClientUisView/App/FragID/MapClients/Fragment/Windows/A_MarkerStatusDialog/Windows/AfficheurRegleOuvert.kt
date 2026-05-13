package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.ButtonAutreEtates
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.Buttons.View.But2_Add_Rapide
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.Buttons.View.Button_StockOptions_SubtractFromDepot
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun AfficheurRegleOuvert(
    uiState: UiState,
    viewModel: MapClientsViewModel,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    relative_Client: M2Client?,
    onPourEdite_Gps_Client: (M2Client) -> Unit,
    extracted: (M8BonVent) -> Unit,
    extracted_2: () -> Unit,
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp?,
) {
    val clientId = relative_Client?.id ?: 0L

    // Local state for credit payment dialog

    fun getLatestTransactionForClient(clientId: Long): M8BonVent? {
        return uiState
            .c3_TransactionCommercialList
            .filter { it.parent_M2Client_OldLongID == clientId }
            .maxByOrNull { it.creationTimestamps }
    }

    val latestTransaction = relative_Client?.let { getLatestTransactionForClient(it.id) }
    var showAddToStockDialog by remember { mutableStateOf(false) }
    var showChangeDispoDialog by remember { mutableStateOf(false) }
    var showSaveDispoDialog by remember { mutableStateOf(false) }
    var showStockOptionsDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("test")
        ) {
            val activeCompt = viewModel.getter.repo9AppCompt.currentAppCompt

            activeCompt?.let { activeCompt ->
                val relatedClientactiveTransaction =
                    viewModel.bProto_ClientsDataBase.find {
                        it.id == activeCompt.vid
                    }
                Text(
                    text = "ماهو تقرير الزبون السابق ${
                        relative_Client?.nom
                            ?: relatedClientactiveTransaction?.nom
                            ?: ""
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "وقت البدء: ${latestTransaction?.heurDebutInString ?: ""}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "الحالة الحالية: ${latestTransaction?.etateActuellementEst?.nomArabe ?: ""}",
                    style = MaterialTheme.typography.bodyMedium
                )

                M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME
                    .ButtonAutreEtates(
                        viewModel = viewModel,
                        clickedClient = clientId,
                    ) { relative_M8BonVent ->
                    }
                val context = LocalContext.current
                val datasValue_repo8BonVent = repositorysMainGetter.repo8BonVent.datasValue
                val filtered by remember(
                    datasValue_repo8BonVent.map { it.dernierTimeTampsSynchronisationAvecFireBase },
                    relative_Client?.keyID
                ) {
                    derivedStateOf {
                        datasValue_repo8BonVent.filter {
                            it.parent_M2Client_KeyID == (relative_Client?.keyID ?: "")
                        }.sortedByDescending { it.creationTimestamps }
                    }
                }

                val relative_M8BonVent =
                    filtered.find { it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT }

                relative_M8BonVent?.let {
                    Button_StockOptions_SubtractFromDepot(
                        onDismiss = { showStockOptionsDialog = false },
                        repositorysMainGetter = repositorysMainGetter,
                        repositorysMainSetter = aCentralFacade.repositorysMainSetter,
                        relative_M8BonVent = it,
                        context = context,
                        wifiTransferDatas_ControllerApp=wifiTransferDatas_ControllerApp,
                        couleurs = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent,
                    )
                }

                relative_M8BonVent?.let {
                    But2_Add_Rapide(
                        onDismiss = { showStockOptionsDialog = false },
                        repositorysMainGetter = repositorysMainGetter,
                        repositorysMainSetter = aCentralFacade.repositorysMainSetter,
                        relative_M8BonVent = it,
                        context = context,
                    )
                }

                val lastCommande_Transaction =
                    repositorysMainGetter.repo8BonVent.datasValue.lastOrNull {
                        it.parent_M2Client_KeyID == relative_Client?.keyID
                                && it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
                    }

                Card(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(value = lastCommande_Transaction, key = SemanticsPropertyKey(""))
                        },
                ) {
                    M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI
                        .ButtonAutreEtates(
                            viewModel = viewModel,
                            clickedClient = clientId,
                        ) { relative_M8BonVent ->
                        }
                }

                M8BonVent.EtateActuellementEst.Passed_Sans_Livre
                    .ButtonAutreEtates(
                        viewModel = viewModel,
                        clickedClient = clientId,
                    ) { relative_M8BonVent ->
                    }

                TextButton(
                    onClick = {
                        viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق الفاتورة مع عدم وضع اي تقرير")
                }
            }
        }
    }


}
