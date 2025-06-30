package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ViewModel.TariffsButtonsViewModelSec7ID2
import Views.Common.Components.ModernToastMessage
import Views.Common.Components.ToastData
import Views.Common.Components.ToastType
import Z_CodePartageEntreApps.Model.A_ProduitInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.TypeTarificationEnumT2
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun TariffsButtonsSec7ID2(
    viewModel: TariffsButtonsViewModelSec7ID2 = koinViewModel(),
    showLabels: Boolean = true,
    filterProductId: Long = 0,
    fermeDialog: (D_TarificationInfos) -> Unit,
    onFermDialogeAvecAnllation: () -> Unit = {},
    cLenceDepuitDialogeAchate: Boolean = false,
) {
    val bonVentComQuiFilterButtons =
        viewModel.getter.gBonVentRepository.onVentData

    var afficheButtons by remember { mutableStateOf(cLenceDepuitDialogeAchate) }
    var currentToast by remember { mutableStateOf<ToastData?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    val bonAchatList =
        viewModel.getter.gBonVentRepository.datasValue
    val tarificationList = uiState.tariffsList
    val produitAcheteOperationList = uiState.produitAcheteOperationList
    val produitInfosList = uiState.produitInfosList

    LaunchedEffect(produitInfosList.size, suspendFunction1(produitInfosList, viewModel))

    val onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit =
        { typeTarification, latestTariffLocalData, _ ->
            val typeName = typeTarification.name
            val message = "$typeName: ${latestTariffLocalData.prixCurrency}"

            // Execute the main logic first
            afficheButtons = false
            fermeDialog(latestTariffLocalData)
            viewModel.updateListRelativeVentCouleurPrixVent(
                parentProduitOldId=filterProductId,
                newPrix = latestTariffLocalData.prixCurrency
            )

            // Show toast after logic execution
            currentToast = ToastData(
                message = message,
                type = ToastType.SUCCESS,
                duration = 1500L
            )
        }

    // Cancellation callback
    val onClickAnulationButton: () -> Unit = {
        // Execute the main logic first
        afficheButtons = false
        onFermDialogeAvecAnllation()
        viewModel.deleteVents(
            parentProduitOldId=filterProductId,
        )

        // Show toast after logic execution
        currentToast = ToastData(
            message = "تم الإلغاء",
            type = ToastType.INFO,
            duration = 1500L
        )
    }

    if (afficheButtons) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (bonVentComQuiFilterButtons != null) {
                    MainFilter(
                        viewModel=viewModel,
                        tarificationList = tarificationList,
                        bonAchatList = bonAchatList,
                        produitAcheteOperationList = produitAcheteOperationList,
                        produitInfosList = produitInfosList,
                        showLabels = showLabels,
                        filterProduitID = filterProductId.toInt(),
                        filterBonID = bonVentComQuiFilterButtons.vid,
                        onClickPrixButton = onClickPrixButton,
                        onClickAnulationButton = onClickAnulationButton
                    )
                }
            }
        }
    }

    // Toast shown independently of buttons visibility
    if (currentToast != null) {
        ModernToastMessage(
            toastData = currentToast,
            onDismiss = { currentToast = null }
        )
    }
}

@Composable
private fun suspendFunction1(
    produitInfosList: SnapshotStateList<A_ProduitInfos>,
    viewModel: TariffsButtonsViewModelSec7ID2
): suspend CoroutineScope.() -> Unit = {
    if (produitInfosList.isEmpty()) {
        delay(500)
        viewModel.refreshTariffs()
    }
}
