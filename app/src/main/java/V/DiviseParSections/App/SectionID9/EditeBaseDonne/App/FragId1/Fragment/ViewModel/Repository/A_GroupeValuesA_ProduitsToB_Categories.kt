package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.A_ProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

@Stable
class A_GroupeValuesA_ProduitsToB_Categories(
    val a_ProduitDataBaseComposeRepositoryPJ17: A_ProduitDataBaseComposeRepositoryPJ17,
    val b3CategoriesCompoRepository: C_CategoriesCompoRepository,
) {
    val categoryGroupedSortedProducts: List<ArticlesBasesStatsTable> by derivedStateOf {
        val categoryMap = b3CategoriesCompoRepository.datasValue.associateBy { it.id }
        val catalogues = B4CatalogueCategoriesRepository().associateBy { it.id.toLong() }

        val (regularProducts, orphanProducts) = a_ProduitDataBaseComposeRepositoryPJ17.datasValue.partition { product ->
            val categoryId = product.idParentCategorie ?: 0L
            val category = categoryMap[categoryId]
            val catalogueId = category?.catalogueParentId ?: 4L

            category != null &&
                    catalogueId != 4L &&
                    !category.nom.equals("NONE", ignoreCase = true)
        }

        val sortedRegular = regularProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                val catalogueId = category?.catalogueParentId ?: 4L
                catalogues[catalogueId]?.position ?: Int.MAX_VALUE
            }.thenBy { product ->
                val categoryId = product.idParentCategorie ?: 0L
                categoryMap[categoryId]?.position ?: Int.MAX_VALUE
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        val sortedOrphan = orphanProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                category?.nom?.takeIf { !it.equals("NONE", ignoreCase = true) }
                    ?: "ZZZZZ_NO_CATEGORY"
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        sortedRegular + sortedOrphan
    }
}
