package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Modules.D.Glide.Proto.A_GlideDisplayImageByKeyId_Proto_5
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProductImagesRow(
    displayProducts: List<ArticlesBasesStatsTable>,
    categoryName: String,
    modifier: Modifier = Modifier
) {
    // Enhanced logging for debugging
    LaunchedEffect(displayProducts) {
        Log.d("ProductImagesRow", "=== ProductImagesRow for category: $categoryName ===")
        Log.d("ProductImagesRow", "Total products received: ${displayProducts.size}")
        displayProducts.take(3).forEachIndexed { index, product ->
            Log.d("ProductImagesRow", "Product $index details:")
            Log.d("ProductImagesRow", "  - Name: ${product.nom}")
            Log.d("ProductImagesRow", "  - ID: ${product.id}")
            Log.d("ProductImagesRow", "  - Parent Category ID: ${product.idParentCategorie}")
            Log.d("ProductImagesRow", "  - Image refresh: ${product.actualiseSonImageTest2}")
            Log.d("ProductImagesRow", "  - Product object: $product")
        }
    }

    if (displayProducts.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            // Show actual products (up to 3, but smaller for compact layout)
            items(displayProducts.take(2)) { product -> // Reduced to 2 items for 40dp height
                Log.d(
                    "ProductImagesRow",
                    "Rendering image for product: ${product.nom} (ID: ${product.id})"
                )

                A_GlideDisplayImageByKeyId_Proto_5(
                    produitVID = product.id,
                    modifier = Modifier.size(20.dp), // Smaller size for 40dp height
                    produitNom = product.nom,
                    size = 20.dp,
                    product = product,
                    qualityImage = 3,
                    refreshImage = product.actualiseSonImageTest2,
                    enableAutoScroll = false
                )
            }

            // Fill remaining slots with placeholder boxes (only if we have less than 2 products)
            val remainingSlots = maxOf(0, 2 - displayProducts.size)
            if (remainingSlots > 0) {
                Log.d("ProductImagesRow", "Adding $remainingSlots placeholder slots")
                items(remainingSlots) { index ->
                    Log.d("ProductImagesRow", "Rendering placeholder $index")
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp, // Smaller font for compact layout
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    } else {
        Log.d("ProductImagesRow", "No products for category: $categoryName - showing empty state")
        // Empty state for compact layout
        Box(
            modifier = modifier
                .size(20.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "∅",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
