package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastType
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
import androidx.compose.ui.platform.LocalContext
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
    val transactionComQuiFilterButtons =
        viewModel.aCentralDatasHandlerProtoJuin9.ouvertTransactionCommercial

    val context = LocalContext.current
    var afficheButtons by remember { mutableStateOf(cLenceDepuitDialogeAchate) }
    var currentToast by remember { mutableStateOf<ToastData?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    val bonAchatList =
        viewModel.aCentralDatasHandlerProtoJuin9.gBonVentRepository.datasValue
    val tarificationList = uiState.tariffsList
    val produitAcheteOperationList = uiState.produitAcheteOperationList
    val produitInfosList = uiState.produitInfosList

    LaunchedEffect(produitInfosList.size, suspendFunction1(produitInfosList, viewModel))

    val onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit =
        { typeTarification, latestTariffLocalData, _ ->
            val typeName = typeTarification.name
            val message = "$typeName: ${latestTariffLocalData.prixCurrency}"

            currentToast = ToastData(
                message = message,
                type = ToastType.SUCCESS,
                duration = 2000L
            )

            afficheButtons = false
            fermeDialog(latestTariffLocalData)
            viewModel.updateListRelativeVentCouleurPrixVent(
                parentProduitOldId=filterProductId,
                newPrix = latestTariffLocalData.prixCurrency
            )
        }

    // Cancellation callback
    val onClickAnulationButton: () -> Unit = {
        currentToast = ToastData(
            message = "تم الإلغاء",
            type = ToastType.INFO,
            duration = 2000L
        )

        afficheButtons = false
        onFermDialogeAvecAnllation()
        viewModel.deleteVents(
            parentProduitOldId=filterProductId,
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        // Modern Toast Component
        ModernToastMessage(
            toastData = currentToast,
            onDismiss = { currentToast = null }
        )

        if (afficheButtons) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (transactionComQuiFilterButtons != null) {
                    MainFilter(
                        viewModel=viewModel,
                        tarificationList = tarificationList,
                        bonAchatList = bonAchatList,
                        produitAcheteOperationList = produitAcheteOperationList,
                        produitInfosList = produitInfosList,
                        showLabels = showLabels,
                        filterProduitID = filterProductId.toInt(),
                        filterBonID = transactionComQuiFilterButtons.vid,
                        onClickPrixButton = onClickPrixButton,
                        onClickAnulationButton = onClickAnulationButton
                    )
                }
            }
        }
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
