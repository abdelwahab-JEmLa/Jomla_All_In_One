package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel.UiState.Mode_Edites
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.MainFilter
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastType
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.content.Context
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
    viewModel_Sec9FragId1ViewId2ViewModel: Sec9FragId1ViewId2ViewModel = koinViewModel(),
    relative_Produit: ArticlesBasesStatsTable? = null,
    viewModel: TariffsButtonsViewModelSec7ID2 = koinViewModel(),
    showLabels: Boolean = true,
    fermeDialog: (M13TarificationInfos) -> Unit = {},
    onFermDialogeAvecAnllation: () -> Unit = {},
    its_ProduitVentsInfosDialog: Boolean = false,
) {

    val bonVentComQuiFilterButtons =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVent_M8BonVent
            ?: M8BonVent.get_default2()

    var afficheButtons by remember { mutableStateOf(its_ProduitVentsInfosDialog) }
    var currentToast by remember { mutableStateOf<ToastData?>(null) }

    val bonVentList = viewModel.getter.repo8BonVent.datasValue
    val repo13TarificationInfos = viewModel.getter.repo13TarificationInfos
    val tarificationList = repo13TarificationInfos.datasValue
    val datasValueDeM1ProduitInfos =
        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue

    val focusedProduct by remember {
        derivedStateOf {
            relative_Produit
                ?: viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.focused_M1ProduitInfos_Pour_PrixDifineur
        }
    }

    LaunchedEffect(focusedProduct) {
        if (focusedProduct != null) {
            afficheButtons = true
        }
    }

    LaunchedEffect(its_ProduitVentsInfosDialog) {
        if (its_ProduitVentsInfosDialog) {
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

    LaunchedEffect(
        datasValueDeM1ProduitInfos.size,
        suspendFunction1(datasValueDeM1ProduitInfos, viewModel)
    )

    val uiStateSec9FragId1ViewId2ViewModel by viewModel_Sec9FragId1ViewId2ViewModel.uiState.collectAsState()

    val isInPriceEditingMode =
        uiStateSec9FragId1ViewId2ViewModel.mode_Edites == Mode_Edites.its_Mode_Regle_Prixs

    val onClickPrixButton: (M13TarificationInfos.TypeChoisi, M13TarificationInfos, Context) -> Unit =
        { typeTarification, latestTariffLocalData, _ ->
            val typeName = typeTarification.name
            val message = "$typeName: ${latestTariffLocalData.prixCurrency}"
            viewModel.updateListRelativeVentCouleurPrixVent(
                listFocusedM10OpeVentCouleurParPrixDifineur = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                m1produitInfos = m1produitInfos,
                newPrix = latestTariffLocalData.prixCurrency
            )

            // FIXED: Now only hide buttons when NOT in price editing mode
            if (!isInPriceEditingMode) {
                afficheButtons = false
                fermeDialog(latestTariffLocalData)
                currentToast = ToastData(
                    message = message,
                    type = ToastType.SUCCESS,
                    duration = 1500L
                )
            } else {
                // In price editing mode, just show success toast but keep buttons visible
                currentToast = ToastData(
                    message = message,
                    type = ToastType.SUCCESS,
                    duration = 1500L
                )
            }
        }

    val onClickAnulationButton: () -> Unit = {
        if (filterProductId != null) {
            viewModel.deleteVents(
                parentProduitOldId = filterProductId,
            )
        }

        // FIXED: Same logic for cancellation button
        if (!isInPriceEditingMode) {
            onFermDialogeAvecAnllation()
            afficheButtons = false
            currentToast = ToastData(
                message = "تم الإلغاء",
                type = ToastType.INFO,
                duration = 1500L
            )
        } else {
            // In price editing mode, just show cancellation toast but keep buttons visible
            currentToast = ToastData(
                message = "تم الإلغاء",
                type = ToastType.INFO,
                duration = 1500L
            )
        }
    }

    if (afficheButtons) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (filterProductId != null) {
                    MainFilter(
                        viewModel = viewModel,
                        list_M8BonVent = bonVentList,
                        tarificationList = tarificationList,
                        produitInfosList = datasValueDeM1ProduitInfos,
                        showLabels = showLabels,
                        filterProduitID = filterProductId.toInt(),
                        filterBonID = bonVentComQuiFilterButtons.vid,
                        onClickPrixButton = onClickPrixButton,
                        onClickAnulationButton = onClickAnulationButton
                    )
                } else {
                }
            }
        }
    }

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
