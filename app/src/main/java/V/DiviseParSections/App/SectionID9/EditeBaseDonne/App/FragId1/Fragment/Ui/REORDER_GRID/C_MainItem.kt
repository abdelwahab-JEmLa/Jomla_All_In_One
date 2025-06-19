package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
internal fun MainItem(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    category: CategoriesTabelle,
    productsByCategory: Map<Long, List<ArticlesBasesStatsTable>>,
) {
    val height = 45.dp

    val categoryProducts = productsByCategory[category.id] ?: emptyList()
    val displayProducts = categoryProducts.take(3)

    val selectedCategoryIds = viewModel.getSelectedCategoryIds()

    Box {
        Card(
            modifier = Modifier
                .aspectRatio(0.7f)
                .clickable {
                    viewModel.updateCate_cSelectionePourDeplace(category)
                },
            colors = CardDefaults.cardColors(
                containerColor = if (category.cSelectionePourDeplace)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (category.cSelectionePourDeplace) 6.dp else 2.dp
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
                // Move Before Button
                if (selectedCategoryIds.isNotEmpty() && !category.cSelectionePourDeplace) {
                    IconButton(
                        onClick = {
                            viewModel.moveSelectedCategoriesRelativeToTarget(
                                targetCategoryId = category.id,
                                moveBefore = true
                            )
                        },
                        modifier = Modifier.size(height)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Move selected categories before this one",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(height))
                }

                RowProduitImages(displayProducts)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    val id = category.id
                    val text = category.nom
                    Text(
                        text ="$text-$id",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (category.cSelectionePourDeplace)
                            FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = if (category.cSelectionePourDeplace)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (category.cSelectionePourDeplace) {
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

                // Empty spacer to maintain card structure
                Spacer(modifier = Modifier.height(height))
            }
        }

        // Floating Move After Button
        if (selectedCategoryIds.isNotEmpty() && !category.cSelectionePourDeplace) {
            IconButton(
                onClick = {
                    viewModel.moveSelectedCategoriesRelativeToTarget(
                        targetCategoryId = category.id,
                        moveBefore = false
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 16.dp) // Offset to appear below the card
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        CircleShape
                    )
                    .zIndex(1f) // Ensure it appears above other elements
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Move selected categories after this one",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
