package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.zIndex
import org.koin.compose.koinInject

@Composable
internal fun MainItem(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    aCentralFacade: ACentralFacade = koinInject(),
    repoM16CategorieProduit: RepoM16CategorieProduit = aCentralFacade.repositorysMainGetter.repoM16CategorieProduit,
    relative_category: CategoriesTabelle,
    productsByCategory: Map<Long, List<ArticlesBasesStatsTable>>,
) {
    val uiState by viewModel.uiState.collectAsState()

    val categoryProducts = productsByCategory[relative_category.id] ?: emptyList()
    val selectedCategoryIds = viewModel.getSelectedCategoryIds()

    val isSelected =
        (uiState.selectionePourDeplacement_Categorie?.keyID ?: "") == relative_category.keyID

    val hasSelections = uiState.selectionePourDeplacement_Categorie!=null

    fun performCategoryReorder(
        targetId: Long,
        moveBefore: Boolean
    ): List<CategoriesTabelle> {
        val categories = repoM16CategorieProduit.datasValue
        val selectedCategories = repoM16CategorieProduit.datasValue
            .filter { it.cSelectionePourDeplace }
        val selectedIds = selectedCategories.map { it.id }.toSet()

        if (selectedIds.isEmpty() || selectedIds.contains(targetId)) return categories

        val selected = categories.filter { selectedIds.contains(it.id) }
        val remaining = categories.filter { !selectedIds.contains(it.id) }
        val targetIndex = remaining.indexOfFirst { it.id == targetId }

        if (targetIndex == -1) return categories

        val insertIndex = if (moveBefore) targetIndex else targetIndex + 1
        val newList = remaining.toMutableList()

        selected.forEachIndexed { i, cat ->
            newList.add(insertIndex + i, cat)
        }

        // Return new classification with updated positions for all categories
        return newList.mapIndexed { index, category ->
            category.copy(position = index + 1)
        }
    }

    // Calculate reordered categories for semantics (preview of the move operation)
    val reorderedCategories = performCategoryReorder(
        targetId = relative_category.id,
        moveBefore = true
    )

    Box {
        Card(
            modifier = Modifier
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
                            viewModel.moveSelectedCategoriesRelativeToTarget(
                                relative_category.id,
                                true
                            )
                            viewModel.updateCate_cSelectionePourDeplace(null)
                        }
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            modifier = Modifier
                               /* .semantics(mergeDescendants = true) {
                                    set(
                                        SemanticsPropertyKey("reorderedCategories"),
                                        reorderedCategories.filter {
                                            it.position in 2..7
                                        }
                                            .map { it.nom + " to " + it.position }
                                    )
                                }   */
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
                    Text(
                        modifier = Modifier
                            .semantics(mergeDescendants = true) {
                                set(SemanticsPropertyKey(""), relative_category.position)
                            }
                            .fillMaxWidth(),
                        text = "${relative_category.position}_${relative_category.nom}-${relative_category.id}",
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

        if (hasSelections) {
            IconButton(
                onClick = {
                    viewModel.moveSelectedCategoriesRelativeToTarget(relative_category.id, false)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(28.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
                    .zIndex(1f)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
