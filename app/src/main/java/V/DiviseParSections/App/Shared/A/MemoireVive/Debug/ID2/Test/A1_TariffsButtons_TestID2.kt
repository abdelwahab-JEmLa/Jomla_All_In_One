package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import Views.Common.Components.ModernToastMessage
import Views.Common.Components.ToastData
import Views.Common.Components.ToastType
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.TypeTarificationEnumT2
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun TariffsButtonsSec7ID2(
    viewModel: TariffsButtonsViewModelSec7ID2 = koinViewModel(),
    showLabels: Boolean = true,
    fermeDialog: (D_TarificationInfos) -> Unit,
    onFermDialogeAvecAnllation: () -> Unit = {},
    cLenceDepuitFragmentsSepecialicteDeVents: Boolean = false,
) {
    val bonVentComQuiFilterButtons =
        viewModel.getter.id8BonVentRepository.onVentId8BonVent

    var afficheButtons by remember { mutableStateOf(cLenceDepuitFragmentsSepecialicteDeVents) }
    var currentToast by remember { mutableStateOf<ToastData?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    val bonAchatList = viewModel.getter.id8BonVentRepository.datasValue
    val tarificationList = uiState.tariffsList
    val produitAcheteOperationList = uiState.produitAcheteOperationList
    val datasValueDeM1ProduitInfos = viewModel.aCentral.getter.repoM1ProduitInfos.datasValue

    val focusedProduct by remember {
        derivedStateOf {
            viewModel.aCentral.focusedVarsHandlerFacade.getter.focusedM1ProduitInfosAuPrixDifineur
        }
    }

    // React to focused product changes
    LaunchedEffect(focusedProduct) {
        Log.d("TariffsButtons", "focusedProduct changed: ${focusedProduct?.nom}")
        if (focusedProduct != null) {
            afficheButtons = true
        }
    }

    // Also react to cLenceDepuitFragmentsSepecialicteDeVents changes
    LaunchedEffect(cLenceDepuitFragmentsSepecialicteDeVents) {
        Log.d("TariffsButtons", "cLenceDepuitFragmentsSepecialicteDeVents changed: $cLenceDepuitFragmentsSepecialicteDeVents")
        if (cLenceDepuitFragmentsSepecialicteDeVents) {
            afficheButtons = true
        }
    }

    val keyID = focusedProduct?.keyID
    val filterProductId = datasValueDeM1ProduitInfos.find { it.keyID == keyID }?.id
    val m1produitInfos by remember {
        derivedStateOf {
            datasValueDeM1ProduitInfos.find { it.id.toInt().toLong() == filterProductId }
        }
    }

    LaunchedEffect(datasValueDeM1ProduitInfos.size, suspendFunction1(datasValueDeM1ProduitInfos, viewModel))

    val onClickPrixButton: (TypeTarificationEnumT2, D_TarificationInfos, Context) -> Unit =
        { typeTarification, latestTariffLocalData, _ ->
            val typeName = typeTarification.name
            val message = "$typeName: ${latestTariffLocalData.prixCurrency}"

            afficheButtons = false
            fermeDialog(latestTariffLocalData)

            viewModel.updateListRelativeVentCouleurPrixVent(
                listFocusedM10OpeVentCouleurParPrixDifineur = viewModel.aCentral.focusedVarsHandlerFacade.getter.listFocusedM10OpeVentCouleurParPrixDifineur,
                m1produitInfos = m1produitInfos,
                newPrix = latestTariffLocalData.prixCurrency
            )

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
        if (filterProductId != null) {
            viewModel.deleteVents(
                parentProduitOldId = filterProductId,
            )
        }

        // Show toast after logic execution
        currentToast = ToastData(
            message = "تم الإلغاء",
            type = ToastType.INFO,
            duration = 1500L
        )
    }

    Log.d("TariffsButtons", "Rendering - afficheButtons: $afficheButtons, filterProductId: $filterProductId")

    if (afficheButtons) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (filterProductId != null) {
                    MainFilter(
                        viewModel = viewModel,
                        tarificationList = tarificationList,
                        bonAchatList = bonAchatList,
                        produitAcheteOperationList = produitAcheteOperationList,
                        produitInfosList = datasValueDeM1ProduitInfos,
                        showLabels = showLabels,
                        filterProduitID = filterProductId.toInt(),
                        filterBonID = bonVentComQuiFilterButtons.vid,
                        onClickPrixButton = onClickPrixButton,
                        onClickAnulationButton = onClickAnulationButton
                    )
                } else {
                    Log.d("TariffsButtons", "filterProductId is null, not showing MainFilter")
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
    produitInfosList: List<ArticlesBasesStatsTable>,
    viewModel: TariffsButtonsViewModelSec7ID2
): suspend CoroutineScope.() -> Unit = {
    if (produitInfosList.isEmpty()) {
        delay(500)
        viewModel.refreshTariffs()
    }
}
