package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(
    list_M8BonVent: List<M8BonVent>,
    tarificationList: List<M13TarificationInfos>,
    produitAcheteOperationList: List<M10OperationVentCouleur>,
    produitInfosList: List<ArticlesBasesStatsTable>,
    showLabels: Boolean,
    modifier: Modifier = Modifier,
    filterProduitID: Int,
    filterBonID: Long,
    onClickPrixButton: (M13TarificationInfos.TypeChoisi, M13TarificationInfos, Context) -> Unit,
    onClickAnulationButton: (() -> Unit)? = null,
    viewModel: TariffsButtonsViewModelSec7ID2
) {
    val filteredBonAchat = remember(list_M8BonVent, filterBonID) {
        list_M8BonVent.find { it.vid == filterBonID } ?: M8BonVent()
    }

    val filteredProduit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.id.toInt() == filterProduitID } ?: ArticlesBasesStatsTable()
    }

    val relative_M2Client = remember(list_M8BonVent, filterBonID) {
        list_M8BonVent.find { it.vid == filterBonID }
    }


    val maxPrixArriveDuProduit = remember(tarificationList, filteredProduit) {
        produitAcheteOperationList
            .filter { it.parentProduitInfosOldId == filteredProduit.id }
            .maxOfOrNull { it.provisoireMonPrix }
    }

    val clientDefiniTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        tarificationList.filter {
            it.parent_M1Produit_KeyId == filteredProduit.keyID &&
                    it.parent_M2Client_KeyId == (relative_M2Client?.parent_M2Client_KeyID ?:"null")
        }
    }

    val lastOrNull_produitAcheteOperationList = produitAcheteOperationList.lastOrNull { operation ->
        operation.parentProduitInfosOldId == filteredProduit.id &&
                operation.parentClientInfosKeyID ==  (relative_M2Client?.parent_M2Client_KeyID ?:"null")
    }

    val clientLastHistoricalPrice =
        remember(produitAcheteOperationList, filteredProduit, relative_M2Client) {
            lastOrNull_produitAcheteOperationList
                ?.provisoireMonPrix ?: 0.0
        }

    Column(modifier = modifier) {
        MainList(
            viewModel =viewModel,
            produit = filteredProduit,
            showLabels = showLabels,
            clientLastHistoricalPrice = clientLastHistoricalPrice,
            maxPrixArriveDuProduit = maxPrixArriveDuProduit,
            clientDefiniTariffs = clientDefiniTariffs,
            onClickPrixButton = onClickPrixButton,
            onClickAnulationButton = onClickAnulationButton // Pass the cancellation callback
        )
    }
}
