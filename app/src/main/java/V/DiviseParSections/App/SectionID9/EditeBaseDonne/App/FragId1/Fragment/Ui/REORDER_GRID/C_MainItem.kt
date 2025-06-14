package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun MainItem(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    category: CategoriesTabelle,
    selectedCategories: Set<Long>,
    productsByCategory: Map<Long, List<ArticlesBasesStatsTable>>,
    categoriesListLocal: List<CategoriesTabelle>,
    onCategoriesReordered: (List<CategoriesTabelle>) -> Unit
) {
    val categoryProducts = productsByCategory[category.id] ?: emptyList()
    val displayProducts = categoryProducts.take(3)

    Card(
        modifier = Modifier
            .aspectRatio(0.7f)
            .clickable {
                // Toggle selection state in ViewModel
                val isCurrentlySelected = selectedCategories.contains(category.id)
                viewModel.updateCate_cSelectionePourDeplace(category, !isCurrentlySelected)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (selectedCategories.contains(category.id))
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selectedCategories.contains(category.id)) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (selectedCategories.isNotEmpty() && !selectedCategories.contains(category.id)) {
                IconButton(
                    onClick = {
                        val updated = moveSelectedCategories(
                            categoriesListLocal,
                            selectedCategories,
                            category.id,
                            true
                        )
                        onCategoriesReordered(updated)

                        // Clear selection state for moved categories using ViewModel
                        selectedCategories.forEach { selectedId ->
                            categoriesListLocal.find { it.id == selectedId }?.let { cat ->
                                viewModel.updateCate_cSelectionePourDeplace(cat, false)
                            }
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Move selected categories before this one",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(28.dp))
            }

            RowProduitImages(displayProducts)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = category.nom,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (selectedCategories.contains(category.id))
                        FontWeight.Bold else FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = if (selectedCategories.contains(category.id))
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedCategories.contains(category.id)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(8.dp)
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

            if (selectedCategories.isNotEmpty() && !selectedCategories.contains(category.id)) {
                IconButton(
                    onClick = {
                        val updated = moveSelectedCategories(
                            categoriesListLocal,
                            selectedCategories,
                            category.id,
                            false
                        )
                        onCategoriesReordered(updated)

                        // Clear selection state for moved categories using ViewModel
                        selectedCategories.forEach { selectedId ->
                            categoriesListLocal.find { it.id == selectedId }?.let { cat ->
                                viewModel.updateCate_cSelectionePourDeplace(cat, false)
                            }
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Move selected categories after this one",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
