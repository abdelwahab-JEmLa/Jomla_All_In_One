package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.Item_Produit_FragID3
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Compact_Presentoir_Echantilliants_FragID3(
    modifier: Modifier = Modifier,
    repositorysMainGetter: RepositorysMainGetter = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit = { _, _ -> }
) {
    val developement_test = false

    val lastBonVentAbdelwahab = remember(
        repositorysMainGetter.repo8BonVent.datasValue,
        repositorysMainGetter.repo2Client.datasValue
    ) {
        repositorysMainGetter.getLastBonVentForClient(
            clientKeyID = RepositorysMainGetter.Jomla_Clients.ECHATILLANTS_KEY_ID,
            etateFilter = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        )
    }

    // Get all M10 operations for the last bon vent
    val operationsFromLastBon = remember(
        lastBonVentAbdelwahab,
        repositorysMainGetter.repo10OperationVentCouleur.datasValue
    ) {
        lastBonVentAbdelwahab?.let { bonVent ->
            repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter { operation ->
                operation.parent_M8BonVent_KeyId == bonVent.keyID
            }
        } ?: emptyList()
    }

    val list_M3couleur = remember(
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue,
        operationsFromLastBon
    ) {
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue.filter { couleur ->
            val hasStock = couleur.count_Don_Depot > 0

            val produit = repositorysMainGetter.repoM1Produit.datasValue.find {
                it.keyID == couleur.parentBProduitInfosKeyID
            }

            val isAvailable = if (developement_test) {
                operationsFromLastBon.any { operation ->
                    operation.parent_M3CouleurProduit_KeyID == couleur.keyID
                }
            } else {
                // In production: check product availability
                produit?.disponibilityEtates == DisponibilityEtates.DISPO
            }

            hasStock && isAvailable
        }.sortedByDescending { couleur ->
            // Sort by creation timestamp from operations
            operationsFromLastBon.find { operation ->
                operation.parent_M3CouleurProduit_KeyID == couleur.keyID
            }?.creationTimestamps ?: 0L
        }
    }

    // Group colors by their parent product
    val groupe_Couleur_Par_Produit = remember(list_M3couleur) {
        list_M3couleur.groupBy { it.parentBProduitInfosKeyID }
            .mapNotNull { (productKeyID, colors) ->
                repositorysMainGetter.repoM1Produit.datasValue.find {
                    it.keyID == productKeyID
                }?.let { product -> product to colors }
            }
            .sortedBy { (product, _) -> product.nom }
    }

    // Group products by category
    val groupe_Par_Categorie = remember(groupe_Couleur_Par_Produit) {
        groupe_Couleur_Par_Produit.groupBy { (product, _) -> product.idParentCategorie }
            .mapNotNull { (categoryId, productColorPairs) ->
                repositorysMainGetter.repoM16CategorieProduit.datasValue.find {
                    it.id == categoryId
                }?.let { category -> category to productColorPairs }
            }
            .sortedBy { (category, _) -> category.positionDouble }
    }

    Etager_LazyColumn_FragID3(
        modifier = modifier,
        categoriesWithProducts = groupe_Par_Categorie,
        on_pour_send_data = on_pour_send_data
    )
}

@Composable
fun Etager_LazyColumn_FragID3(
    modifier: Modifier = Modifier,
    categoriesWithProducts: List<Pair<CategoriesTabelle, List<Pair<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>>>>>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit
) {
    // FIXED: Use expanded_M1Produit instead of expanded_M3CouleurProduitInfos
    val gridState = rememberLazyStaggeredGridState()

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(4),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)), // Rose clair (lavender blush)
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        categoriesWithProducts.forEach { (category, productColorPairs) ->
            // Display sticky header for each category
            item(
                key = "header_${category.id}",
                span = StaggeredGridItemSpan.FullLine
            ) {
                CategoryStickyHeader(category = category)
            }

            // Display each product with its colors
            productColorPairs.forEach { (product, colors) ->
                // FIXED: Check if THIS product is expanded using expanded_M1Produit
                val isExpanded = focusedValuesGetter.active_Central_Values
                    .expanded_M1Produit?.keyID == product.keyID

                item(
                    key = "product_${product.keyID}",
                    span = if (isExpanded) {
                        StaggeredGridItemSpan.FullLine
                    } else {
                        StaggeredGridItemSpan.SingleLane
                    }
                ) {
                    LazyStigerList_Produits_FragID3(
                        product = product,
                        colors = colors,
                        on_pour_send_data = on_pour_send_data
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryStickyHeader(
    category: CategoriesTabelle,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = category.nom,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun LazyStigerList_Produits_FragID3(
    modifier: Modifier = Modifier,
    product: ArticlesBasesStatsTable,
    colors: List<M3CouleurProduitInfos>,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    on_pour_send_data: (String, String) -> Unit
) {
    // FIXED: Check expansion state using expanded_M1Produit
    val isExpanded = focusedValuesGetter.active_Central_Values
        .expanded_M1Produit?.keyID == product.keyID

    Item_Produit_FragID3(
        relative_M1produit = product,
        on_pour_send_data = on_pour_send_data,
        modifier = modifier
    )
}
