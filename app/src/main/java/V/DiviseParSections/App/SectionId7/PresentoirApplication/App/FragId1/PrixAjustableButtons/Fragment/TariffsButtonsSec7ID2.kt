package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter.MainFilter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ModernToastMessage
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastData
import V.DiviseParSections.App.Shared.Modules.Ui.A.UI.ToastType
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    fermeDialog: (M13TarificationInfos) -> Unit,
    onFermDialogeAvecAnllation: () -> Unit = {},
    cLenceDepuitFragmentsSepecialicteDeVents: Boolean = false,
) {
    val bonVentComQuiFilterButtons =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.focuced_active_onVent_M8BonVent

    var afficheButtons by remember { mutableStateOf(cLenceDepuitFragmentsSepecialicteDeVents) }
    var currentToast by remember { mutableStateOf<ToastData?>(null) }

    val bonVentList = viewModel.getter.repo8BonVent.datasValue
    val repo13TarificationInfos = viewModel.getter.repo13TarificationInfos
    val tarificationList = repo13TarificationInfos.datasValue
    val repo10OperationVentCouleur = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur
    val operationVentCouleurList = repo10OperationVentCouleur.datasValue
    val datasValueDeM1ProduitInfos = viewModel.aCentralFacade.repositorysMainGetter.repoM1ProduitInfos.datasValue

    val focusedProduct by remember {
        derivedStateOf {
            viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.focused_M1ProduitInfos_Pour_PrixDifineur
        }
    }

    LaunchedEffect(focusedProduct) {
        if (focusedProduct != null) {
            afficheButtons = true
        }
    }

    LaunchedEffect(cLenceDepuitFragmentsSepecialicteDeVents) {
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

    val onClickPrixButton: (M13TarificationInfos.TypeChoisi, M13TarificationInfos, Context) -> Unit =
        { typeTarification, latestTariffLocalData, _ ->
            val typeName = typeTarification.name
            val message = "$typeName: ${latestTariffLocalData.prixCurrency}"

            afficheButtons = false
            fermeDialog(latestTariffLocalData)

            viewModel.updateListRelativeVentCouleurPrixVent(
                listFocusedM10OpeVentCouleurParPrixDifineur = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                m1produitInfos = m1produitInfos,
                newPrix = latestTariffLocalData.prixCurrency
            )

            currentToast = ToastData(
                message = message,
                type = ToastType.SUCCESS,
                duration = 1500L
            )
        }

    val onClickAnulationButton: () -> Unit = {
        afficheButtons = false
        onFermDialogeAvecAnllation()
        if (filterProductId != null) {
            viewModel.deleteVents(
                parentProduitOldId = filterProductId,
            )
        }

        currentToast = ToastData(
            message = "تم الإلغاء",
            type = ToastType.INFO,
            duration = 1500L
        )
    }


    if (afficheButtons) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (filterProductId != null) {
                    if (bonVentComQuiFilterButtons != null) {
                        MainFilter(
                            viewModel = viewModel,
                            tarificationList = tarificationList,
                            bonAchatList = bonVentList,
                            produitAcheteOperationList = operationVentCouleurList,
                            produitInfosList = datasValueDeM1ProduitInfos,
                            showLabels = showLabels,
                            filterProduitID = filterProductId.toInt(),
                            filterBonID = bonVentComQuiFilterButtons.vid,
                            onClickPrixButton = onClickPrixButton,
                            onClickAnulationButton = onClickAnulationButton
                        )
                    }
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
