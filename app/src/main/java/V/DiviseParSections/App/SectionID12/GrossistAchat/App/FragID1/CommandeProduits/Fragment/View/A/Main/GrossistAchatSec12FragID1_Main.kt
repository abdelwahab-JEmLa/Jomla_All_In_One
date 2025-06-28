package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun GrossistAchatSec12FragID1_Main(
    modifier: Modifier = Modifier,

    _0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3 = koinInject(),
) {
    val activeIdDe_1_5_Vendeur = _0_0_HeadSQLRepositorys.repositorys_Model.activeIdDeA5Vendeur

    val periodFilter = _0_0_HeadSQLRepositorys
        .repositorys_Model.repository_1_5_Vendeur
        .modelDatasSnapList.find { it.vid == activeIdDe_1_5_Vendeur }
        ?.ceComptVendeurStartAffichePeriod

    val models = _0_0_HeadSQLRepositorys.repositorys_Model

    var displayableProducts by remember { mutableStateOf<List<_1_2_ProduitAcheteOperation>>(emptyList()) }

    LaunchedEffect(
        models.repositoryC2_ProduitAcheteOperation.modelDatasSnapList,
        models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList,
        models.c3TransactionCommercialRepository.modelDatasSnapList,
        periodFilter
    ) {
        withContext(Dispatchers.Default) {
            val colorsByProductVid = models._1_1_CouleurAcheteOperation_Repository.modelDatasSnapList
                .groupBy { it.parentProduitAchateOperationVID }

            val bonAchatsById = models.c3TransactionCommercialRepository.modelDatasSnapList
                .associateBy { it.vid }

            val confirmedProducts = models.repositoryC2_ProduitAcheteOperation.modelDatasSnapList
                .filter { product ->
                    val hasConfirmedStatus = product.etateActuellementEst == _1_2_ProduitAcheteOperation.EtateActuellementEst.CONFIRME

                    if (!hasConfirmedStatus) {
                        return@filter false
                    }

                    val productColors = colorsByProductVid[product.vid] ?: emptyList()
                    val hasValidColors = productColors.any { color ->
                        color.etateActuellementEst == _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                                && color.totaleQuantity > 0
                    }

                    if (!hasValidColors) {
                        return@filter false
                    }

                    if (periodFilter != null) {
                        val bonAchat = bonAchatsById[product.parent_1_3_TransactionCommercial]
                        val productPeriod = bonAchat?.parentPeriodeVentOldID

                        val allBonAchats = models.repositoryC2_ProduitAcheteOperation.modelDatasSnapList
                            .filter { it.produitAcheterID == product.produitAcheterID }
                            .map { it.parent_1_3_TransactionCommercial }
                            .distinct()

                        val allPeriods = allBonAchats.mapNotNull { bonAchatId ->
                            bonAchatsById[bonAchatId]?.parentPeriodeVentOldID
                        }.distinct()

                        val anyPeriodMatches = allPeriods.any { it == periodFilter }

                        return@filter anyPeriodMatches
                    }

                    true
                }
                .distinctBy { it.produitAcheterID }

            displayableProducts = confirmedProducts
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        LazyColumn {
            items(displayableProducts) { produit ->
                B_ProduitCommande(models, produit)
            }
        }
    }
}
