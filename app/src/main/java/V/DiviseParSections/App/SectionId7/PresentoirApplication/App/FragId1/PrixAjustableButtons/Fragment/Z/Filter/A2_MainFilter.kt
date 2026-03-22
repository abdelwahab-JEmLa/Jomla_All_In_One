package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ItsLancedDepuit
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.A3_MainList_ItsWorckChezGros
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App._0.Navigation.Screen
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics

@Composable
fun MainFilter(
    viewModel: TariffsButtonsViewModelSec7ID2,
    list_M8BonVent: List<M8BonVent>,
    tarificationList: List<M13TarificationInfos>,
    produitInfosList: List<M01Produit>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    filterProduitID: Int,
    filterBonID: Long,
    onClickPrixButton: (M13TarificationInfos.TypeChoisi, M13TarificationInfos, Context) -> Unit,
    onClickAnulationButton: (() -> Unit)? = null,
    lancedDepuitAffiche: ItsLancedDepuit?,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val relative_M1Produit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.id.toInt() == filterProduitID } ?: M01Produit()
    }

    val relative_M8BonVent = remember(list_M8BonVent, filterBonID) {
        list_M8BonVent.find { it.vid == filterBonID } ?: M8BonVent()
    }

    val relative_M2Client = remember(list_M8BonVent, filterBonID) {
        list_M8BonVent.find { it.vid == filterBonID }
    }

    val clientDefiniTariffs = remember(tarificationList, relative_M1Produit, relative_M8BonVent) {
        tarificationList.filter {
            it.parent_M1Produit_KeyId == relative_M1Produit.keyID &&
                    it.parent_M2Client_KeyId == (relative_M2Client?.parent_M2Client_KeyID ?: "null")
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val activeFragment by viewModel.fragmentNavigationHandler.currentFragment.collectAsState()
    val activeFragment_Its_not_FragmentProduitFastSearchDialog =
        activeFragment == Screen.FragmentProduitFastSearchDialog
    val currentApp_ItsWorkChezGrossisst =
        focusedValuesGetter.currentApp_ItsWorkChezGrossisst &&
                activeFragment_Its_not_FragmentProduitFastSearchDialog

    Column(modifier = modifier
        .semantics(mergeDescendants = true) {
            set(
                value = activeFragment_Its_not_FragmentProduitFastSearchDialog,
                key = SemanticsPropertyKey("activeFragment_Its_not_FragmentProduitFastSearchDialog")
            )
        }
        .semantics(mergeDescendants = true) {
            set(
                value = uiState.activeFragment_Its_not_FragmentProduitFastSearchDialog,
                key = SemanticsPropertyKey("uiState.activeFragment_Its_not_FragmentProduitFastSearchDialog")
            )
        }) {
        when (currentApp_ItsWorkChezGrossisst) {
            true -> A3_MainList_ItsWorckChezGros(
                relative_M1Produit = relative_M1Produit,
                viewModel = viewModel,
                clientDefiniTariffs = clientDefiniTariffs,
                itsLancedDepuitComposeParent = lancedDepuitAffiche,
                onClickAnulationButton = onClickAnulationButton
            )

            false ->
                MainList(
                    itsLancedDepuitComposeParent = lancedDepuitAffiche,
                    viewModel = viewModel,
                    relative_M1Produit = relative_M1Produit,
                    showLabels = showLabels,
                    clientDefiniTariffs = clientDefiniTariffs,
                    onClickPrixButton = onClickPrixButton,
                    onClickAnulationButton = onClickAnulationButton
                )
        }
    }
}
