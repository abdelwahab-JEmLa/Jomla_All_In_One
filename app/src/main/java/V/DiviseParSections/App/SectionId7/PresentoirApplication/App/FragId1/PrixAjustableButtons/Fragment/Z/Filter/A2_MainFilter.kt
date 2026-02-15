package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ItsLancedDepuit
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.A3_MainList_ItsWorckChezGros
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(
    viewModel: TariffsButtonsViewModelSec7ID2,
    list_M8BonVent: List<M8BonVent>,
    tarificationList: List<M13TarificationInfos>,
    produitInfosList: List<ArticlesBasesStatsTable>,
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
        produitInfosList.find { it.id.toInt() == filterProduitID } ?: ArticlesBasesStatsTable()
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
    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

    Column(modifier = modifier) {
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
