package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.CategoriesTabelle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
    val height = 150.dp

    val categoryProducts = productsByCategory[category.id] ?: emptyList()
    val displayProducts = categoryProducts.take(3)

    val selectedCategoryIds = viewModel.getSelectedCategoryIds()

    Box {
        Card(
            modifier = Modifier
                .height(height) // Use fixed height instead of aspectRatio
                .fillMaxWidth()
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
                    .padding(2.dp),
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
                        modifier = Modifier.size(25.dp) // Reduced size to fit better in 55.dp height
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Move selected categories before this one",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(25.dp) // Reduced icon size
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp)) // Reduced spacer height
                }

                RowProduitImages(displayProducts)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 2.dp) // Reduced padding
                ) {
                    val id = category.id
                    val text = category.nom
                    Text(
                        text ="$text-$id",
                        style = MaterialTheme.typography.bodySmall, // Smaller text for compact layout
                        fontWeight = if (category.cSelectionePourDeplace)
                            FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = if (category.cSelectionePourDeplace)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1, // Reduced to 1 line for compact layout
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (category.cSelectionePourDeplace) {
                        Spacer(modifier = Modifier.height(2.dp)) // Reduced spacer
                        Box(
                            modifier = Modifier
                                .size(12.dp) // Smaller checkmark box
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

                // Empty spacer to maintain card structure
                Spacer(modifier = Modifier.height(4.dp)) // Reduced spacer height
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
                    .offset(y = 0.dp) // Reduced offset to match smaller card height
                    .size(28.dp) // Slightly smaller button
                    .background(
                        MaterialTheme.colorScheme.secondary,
                        CircleShape
                    )
                    .zIndex(1f)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Move selected categories after this one",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(16.dp) // Reduced icon size
                )
            }
        }
    }
}
