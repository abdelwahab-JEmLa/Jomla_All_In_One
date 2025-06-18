package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun RowProduitImages(displayProducts: List<ArticlesBasesStatsTable>) {
    if (displayProducts.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            // Show actual products (up to 3)
            items(displayProducts.take(3)) { product ->
                A_GlideDisplayImageByKeyId_Proto_5(
                    produitVID = product.id,
                    modifier = Modifier.size(35.dp),
                    produitNom = product.nom,
                    size = 35.dp,
                    product = product,
                    qualityImage = 3,
                    refreshImage = product.actualiseSonImageTest2,
                    enableAutoScroll = false
                )
            }

            // Fill remaining slots with placeholder boxes (only if we have less than 3 products)
            val remainingSlots = maxOf(0, 3 - displayProducts.size)
            if (remainingSlots > 0) {
                items(remainingSlots) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Vide",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
