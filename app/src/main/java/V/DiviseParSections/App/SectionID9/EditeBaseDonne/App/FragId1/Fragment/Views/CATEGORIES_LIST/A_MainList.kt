package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.CATEGORIES_LIST

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.A_ProduitInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun EditeCategoriesMainList(
    categoriesList: List<C_CategorieProduitInfos>,
    produitList: List<A_ProduitInfosProtoJuin3>,
    onProductCategoryChanged: (A_ProduitInfosProtoJuin3) -> Unit,
    modifier: Modifier = Modifier,
    onCategoriesEdite: ((List<C_CategorieProduitInfos>) -> Unit)? = null,
) {
    var categoriesListLocal by remember(categoriesList) { mutableStateOf(categoriesList) }

    val onHeldPourDeplacement: (Long, Boolean) -> Unit = { id, held ->
        categoriesListLocal = categoriesListLocal.map { it.copy(itsHeldPourDeplacement = it.id == id && held) }
    }

    val onClickPourChangeDeplaceApre: (Long, Boolean) -> Unit = { targetId, after ->
        val held = categoriesListLocal.find { it.itsHeldPourDeplacement }
        val target = categoriesListLocal.find { it.id == targetId }
        if (held != null && target != null && held.id != targetId) {
            val newPos = if (after) target.position + 1 else target.position
            categoriesListLocal = categoriesListLocal.map { cat ->
                when {
                    cat.id == held.id -> cat.copy(position = newPos, itsHeldPourDeplacement = false)
                    cat.position >= newPos && cat.id != held.id -> cat.copy(position = cat.position + 1)
                    else -> cat
                }
            }.sortedBy { it.position }.mapIndexed { i, cat -> cat.copy(position = i + 1) }
            onCategoriesEdite?.invoke(categoriesListLocal)
        }
    }


    val handleAddCategory: (String) -> Unit = { name ->
        val newId = (categoriesListLocal.maxOfOrNull { it.id } ?: 0L) + 1

        categoriesListLocal = categoriesListLocal.map { cat ->
            cat.copy(position = cat.position + 1)
        } + C_CategorieProduitInfos(newId, name, 1)

        categoriesListLocal = categoriesListLocal.sortedBy { it.position }

        onCategoriesEdite?.invoke(categoriesListLocal)
    }

    val handleUpdateCategory: (Long, String) -> Unit = { id, name ->
        categoriesListLocal = categoriesListLocal.map { if (it.id == id) it.copy(nom = name) else it }
        onCategoriesEdite?.invoke(categoriesListLocal)
    }

    val categoryMap = remember(categoriesListLocal) { categoriesListLocal.associateBy { it.id } }
    val groupedProducts = remember(produitList, categoriesListLocal) {
        produitList.groupBy { it.idParentCategorie ?: 0L }
            .toList().sortedBy { (id, _) ->
                if (id == 0L) Int.MIN_VALUE else categoryMap[id]?.position ?: Int.MIN_VALUE
            }.toMap()
    }
    val availableCategories = remember(produitList) {
        produitList.mapNotNull { it.idParentCategorie }.distinct().sorted()
    }

    Box(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categorieSection(
                groupedProducts,
                availableCategories,
                onProductCategoryChanged
                , categoryMap,
                onHeldPourDeplacement, onClickPourChangeDeplaceApre, handleAddCategory, handleUpdateCategory
            )
        }
    }
}
