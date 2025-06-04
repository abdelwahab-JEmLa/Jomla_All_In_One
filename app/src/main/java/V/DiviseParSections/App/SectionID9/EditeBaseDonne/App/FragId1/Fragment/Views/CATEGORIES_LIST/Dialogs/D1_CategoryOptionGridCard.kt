package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.CATEGORIES_LIST.Dialogs

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryOptionGridCard(
    categoryId: Long?,
    categoryName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditName: ((String) -> Unit)?,
    categoryProducts: List<ArticlesBasesStatsTable> = emptyList()
) {
    var showEditDialog by remember { mutableStateOf(false) }

    // Add logging to debug image display issues
    LaunchedEffect(categoryProducts, categoryId) {
        Log.d("CategoryOptionGridCard", "Category: $categoryName (ID: $categoryId)")
        Log.d("CategoryOptionGridCard", "Products count: ${categoryProducts.size}")
        categoryProducts.forEachIndexed { index, product ->
            Log.d("CategoryOptionGridCard", "Product $index: ${product.nom} (ID: ${product.id})")
            Log.d("CategoryOptionGridCard", "  - Image refresh flag: ${product.actualiseSonImageTest2}")
            Log.d("CategoryOptionGridCard", "  - Parent category: ${product.idParentCategorie}")
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Product images row - with enhanced logging
                ProductImagesRow(
                    displayProducts = categoryProducts,
                    categoryName = categoryName,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Category name - positioned at bottom
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Action buttons positioned at the corners
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sélectionné",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (onEditName != null && categoryId != null) {
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Show edit dialog when requested
    if (showEditDialog && onEditName != null) {
        EditCategoryDialog(
            currentName = categoryName,
            onCategoryUpdated = { newName ->
                onEditName(newName)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
private fun ProductImagesRow(
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
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            // Show actual products (up to 3, matching main grid)
            items(displayProducts.take(3)) { product ->
                Log.d("ProductImagesRow", "Rendering image for product: ${product.nom} (ID: ${product.id})")

                A_GlideDisplayImageByKeyId_Proto_5(
                    produitVID = product.id,
                    modifier = Modifier.size(28.dp), // Slightly smaller than main grid (35dp) to fit better
                    produitNom = product.nom,
                    size = 28.dp,
                    product = product,
                    qualityImage = 3,
                    refreshImage = product.actualiseSonImageTest2,
                    enableAutoScroll = false
                )
            }

            // Fill remaining slots with placeholder boxes (only if we have less than 3 products)
            val remainingSlots = maxOf(0, 3 - displayProducts.size)
            if (remainingSlots > 0) {
                Log.d("ProductImagesRow", "Adding $remainingSlots placeholder slots")
                items(remainingSlots) { index ->
                    Log.d("ProductImagesRow", "Rendering placeholder $index")
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    } else {
        Log.d("ProductImagesRow", "No products for category: $categoryName - showing empty state")
        // Empty state matching main grid style
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Vide",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
