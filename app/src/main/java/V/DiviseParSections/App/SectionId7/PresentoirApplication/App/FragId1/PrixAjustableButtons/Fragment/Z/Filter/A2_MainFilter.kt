package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.Z.Filter

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.A.ViewModel.TariffsButtonsViewModelSec7ID2
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
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
    onClickAnulationButton: (() -> Unit)? = null
) {
    val relative_List_M10Vent = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue

    val relative_M1Produit = remember(produitInfosList, filterProduitID) {
        produitInfosList.find { it.id.toInt() == filterProduitID } ?: ArticlesBasesStatsTable()
    }

    val relative_M8BonVent = remember(list_M8BonVent, filterBonID) {
        list_M8BonVent.find { it.vid == filterBonID } ?: M8BonVent()
    }

    val relative_M2Client = remember(list_M8BonVent, filterBonID) {
        list_M8BonVent.find { it.vid == filterBonID }
    }

    val maxPrixArriveDuProduit = remember(tarificationList, relative_M1Produit) {
        relative_List_M10Vent
            .filter { it.parentProduitInfosOldId == relative_M1Produit.id }
            .maxOfOrNull { it.provisoireMonPrix }
    }

    val clientDefiniTariffs = remember(tarificationList, relative_M1Produit, relative_M8BonVent) {
        tarificationList.filter {
            it.parent_M1Produit_KeyId == relative_M1Produit.keyID &&
                    it.parent_M2Client_KeyId == (relative_M2Client?.parent_M2Client_KeyID ?: "null")
        }
    }

    val last_M10Vent = relative_List_M10Vent.lastOrNull { operation ->
        operation.parent_M9AppCompt_KeyID == relative_M1Produit.keyID &&
                operation.parentClientInfosKeyID == (relative_M2Client?.parent_M2Client_KeyID ?: "null")
    }

    val relative_Tariff_Historique = M13TarificationInfos.get_default().copy(
        typeChoisi = M13TarificationInfos.TypeChoisi.Historique,
        prixCurrency = last_M10Vent?.provisoireMonPrix?: 0.0,
        parent_M1Produit_KeyId = relative_M1Produit.keyID,
        parent_M1Produit_DebugInfos = relative_M1Produit.getDebugInfos(),
        parent_M2Client_KeyId = relative_M2Client?.keyID ?: "null",
        parent_M2Client_DebugInfos = relative_M2Client?.get_DebugInfos() ?: "null",
        parent_M8BonVent_KeyId = relative_M8BonVent.keyID ,
        parent_M8BonVent_DebugInfos = relative_M8BonVent.get_DebugInfos() ?: "null",
        creationTimestamps = System.currentTimeMillis()
    )

    Column(modifier = modifier) {
        MainList(
            viewModel = viewModel,
            relative_M1Produit = relative_M1Produit,
            showLabels = showLabels,
            maxPrixArriveDuProduit = maxPrixArriveDuProduit,
            clientDefiniTariffs = clientDefiniTariffs,
            onClickPrixButton = onClickPrixButton,
            onClickAnulationButton = onClickAnulationButton // Pass the cancellation callback
        )
    }
}
