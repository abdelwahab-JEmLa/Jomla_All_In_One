package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun MainFilter(
    tarificationList: List<M13TarificationInfos>,
    bonAchatList: List<M8BonVent>,
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
    val filteredBonAchat = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID } ?: M8BonVent(
            parent_M2Client_KeyID = RepositorysMainSetter.getListDesParentKeys("null")[M8BonVent.keyModel] ?: "",
            parent_M14VentPeriod_KeyId = RepositorysMainSetter.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent] ?: "",
            parentID8C2TypeTransactionKeyByParent = RepositorysMainSetter.getListDesParentKeys("null")[M8BonVent.EtateActuellementEst.keyModel] ?: ""
        )
    }

    val filteredProduit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.id.toInt() == filterProduitID } ?: ArticlesBasesStatsTable()
    }

    val idClientFiltruer = remember(bonAchatList, filterBonID) {
        bonAchatList.find { it.vid == filterBonID }?.parent_M2Client_KeyID ?: 0L
    }

    val maxPrixArriveDuProduit = remember(tarificationList, filteredProduit) {
        produitAcheteOperationList
            .filter { it.parentProduitInfosOldId == filteredProduit.id }
            .maxOfOrNull { it.provisoireMonPrix }
    }

    val clientDefiniTariffs = remember(tarificationList, filteredProduit, filteredBonAchat) {
        tarificationList.filter {
            it.idParentProduit == filteredProduit.id &&
                    it.parentIdClient == idClientFiltruer
        }
    }

    val lastOrNull_produitAcheteOperationList = produitAcheteOperationList.lastOrNull { operation ->
        operation.parentProduitInfosOldId == filteredProduit.id &&
                operation.parentClientInfosKeyID == idClientFiltruer
    }

    val clientLastHistoricalPrice =
        remember(produitAcheteOperationList, filteredProduit, idClientFiltruer) {
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
