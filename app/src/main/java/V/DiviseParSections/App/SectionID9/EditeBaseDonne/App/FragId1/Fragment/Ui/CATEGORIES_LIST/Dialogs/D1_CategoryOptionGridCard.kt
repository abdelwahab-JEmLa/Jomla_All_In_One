package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.CATEGORIES_LIST.Dialogs

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.Modules.D.Glide.Module.Proto.A_GlideDisplayImageByKeyId_Proto_5
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    categorie: CategoriesTabelle,
    categoryId: Long?,
    categoryName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditName: ((String) -> Unit)?,
) {
    val uiState by viewModel.uiState.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    val productsForCategory by remember(categoryId, uiState.a_ProduitInfosList) {
        derivedStateOf {
            if (categoryId != null) {
                uiState.a_ProduitInfosList
                    .filter { it.idParentCategorie == categoryId }
                    .take(2) // Take only 2 products as requested
            } else {
                // For "Sans Catégorie" option, get products with no category
                uiState.a_ProduitInfosList
                    .filter { it.idParentCategorie == null }
                    .take(2)
            }
        }
    }

    // Get the first product to display its image
    val displayProduct = productsForCategory.firstOrNull()
    val displayProduct2 = productsForCategory.lastOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
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
            // FIXED: Changed from Row to Column for better layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onClick)
                ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Image positioned at the top center
                if (displayProduct != null) {
                    Row {
                    A_GlideDisplayImageByKeyId_Proto_5(
                        produitVID = displayProduct.id,
                        modifier = Modifier.size(40.dp),
                        produitNom = displayProduct.nom,
                        size = 40.dp,
                        product = displayProduct,
                        qualityImage = 3,
                        refreshImage = displayProduct.actualiseSonImage,
                        enableAutoScroll = false
                    )
                        if (displayProduct2 != null) {
                            A_GlideDisplayImageByKeyId_Proto_5(
                                produitVID = displayProduct2.id,
                                modifier = Modifier.size(40.dp),
                                produitNom = displayProduct2.nom,
                                size = 40.dp,
                                product = displayProduct,
                                qualityImage = 3,
                                refreshImage = displayProduct.actualiseSonImage,
                                enableAutoScroll = false
                            )
                        }
                    }
                } else {
                    // Show placeholder when no product is available for this category
                    A_GlideDisplayImageByKeyId_Proto_5(
                        produitVID = null, // This will show the default logo
                        modifier = Modifier.size(20.dp),
                        produitNom = categoryName,
                        size = 20.dp,
                        product = null,
                        qualityImage = 3,
                        refreshImage = 0,
                        enableAutoScroll = false
                    )
                }

                // Category name positioned below the image
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
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
                        .size(12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (onEditName != null && categoryId != null) {
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier",
                        modifier = Modifier.size(10.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Show edit dialog when requested
    if (showEditDialog && onEditName != null) {
        EditCategoryDialog(
            viewModel=viewModel,
            categoryToEdit = categorie,
            onDismiss = { showEditDialog = false }
        )
    }
}
