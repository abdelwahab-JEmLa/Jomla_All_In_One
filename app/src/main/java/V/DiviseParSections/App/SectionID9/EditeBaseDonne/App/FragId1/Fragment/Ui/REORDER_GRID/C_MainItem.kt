package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.RepoM16CategorieProduit
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
internal fun MainItem(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    repoM16CategorieProduit: RepoM16CategorieProduit = aCentralFacade.repositorysMainGetter.repoM16CategorieProduit,
    relative_category: CategoriesTabelle,
    productsByCategory: Map<Long, List<ArticlesBasesStatsTable>>,
) {
    val list_repoM16CategorieProduit = repoM16CategorieProduit.datasValue
    val uiState by viewModel.uiState.collectAsState()

    val categoryProducts = productsByCategory[relative_category.id] ?: emptyList()

    val selectionePourDeplacement_Categorie = uiState.selectionePourDeplacement_Categorie
    val selected_Cate_Key = selectionePourDeplacement_Categorie?.keyID ?: ""
    val isSelected =
        selected_Cate_Key == relative_category.keyID

    val hasSelections = selectionePourDeplacement_Categorie != null

    val old_Index = list_repoM16CategorieProduit.indexOf(selectionePourDeplacement_Categorie)
    val targeted_Index = list_repoM16CategorieProduit.indexOf(relative_category)
    val newAdd_Index = maxOf(0, targeted_Index - 1)

    fun performCategoryReorder(): List<CategoriesTabelle> {
        if (old_Index == -1) return emptyList()
        val newList = list_repoM16CategorieProduit.toMutableList()
        val categoryToMove = newList.removeAt(old_Index)
        newList.add(newAdd_Index, categoryToMove)
        return newList.mapIndexed { index, category ->
            category.copy(
                position = index + 1,
            )
        }
    }


    Box {
        Card(
            modifier = Modifier
                .semantics(mergeDescendants = true) {
                    set(
                        value = selectionePourDeplacement_Categorie,
                        key = SemanticsPropertyKey("selectionePourDeplacement_Categorie")
                    )
                }
                .semantics(mergeDescendants = true) {
                    set(
                        value = relative_category,
                        key = SemanticsPropertyKey("relative_category")
                    )
                }
                .getSemanticsTag(relative_category, "")
                .height(150.dp)
                .fillMaxWidth()
                .clickable {
                    viewModel.updateCate_cSelectionePourDeplace(relative_category)
                },
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (hasSelections) {
                    IconButton(
                        modifier = Modifier
                            .size(25.dp),
                        onClick = {
                            val positionDouble_SC =
                                relative_category.positionDouble

                            if (selectionePourDeplacement_Categorie != null) {
                                repositorysMainSetter.upsert_M16CategorieProduit(
                                    selectionePourDeplacement_Categorie.copy(
                                        positionDouble = positionDouble_SC
                                    )
                                )
                            }

                            viewModel.updateCate_cSelectionePourDeplace(null)
                        }
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            modifier = Modifier
                                .semantics(mergeDescendants = true) {
                                    set(
                                        value = selectionePourDeplacement_Categorie,
                                        key = SemanticsPropertyKey("selectionePourDeplacement_Categorie")
                                    )
                                }
                                .semantics(mergeDescendants = true) {
                                    set(
                                        value = relative_category,
                                        key = SemanticsPropertyKey("relative_category")
                                    )
                                }
                                .size(25.dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                RowProduitImages(categoryProducts.take(3))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    val positionDouble = relative_category.positionDouble
                    Text(
                        modifier = Modifier
                            .semantics(mergeDescendants = true) {
                                set(SemanticsPropertyKey(""), relative_category.position)
                            }
                            .fillMaxWidth(),
                        text = "${relative_category.nom}-",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )

                    if (isSelected) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(6.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
