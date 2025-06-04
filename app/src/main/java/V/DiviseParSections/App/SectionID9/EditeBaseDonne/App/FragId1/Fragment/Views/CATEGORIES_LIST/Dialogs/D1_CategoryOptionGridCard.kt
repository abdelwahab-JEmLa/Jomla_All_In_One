package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Views.CATEGORIES_LIST.Dialogs

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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

data class CataloguesCaegorie(
    val id: Long = 0,
    val nom: String = "",
    val premierCategorieId: Long = 0,
)

fun startupeDatas(): List<CataloguesCaegorie> {
    return listOf(
        CataloguesCaegorie(
            id = 1,
            nom = "Confiserie",
            premierCategorieId = 20
        ),
        CataloguesCaegorie(
            id = 2,
            nom = "Cosmétique",
            premierCategorieId = 1
        ),
        CataloguesCaegorie(
            id = 3,
            nom = "Teenager",
            premierCategorieId = 100
        )
    )
}

@Composable
fun CatalogHeaderCard(
    catalogue: CataloguesCaegorie,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = catalogue.nom,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CategoryOptionGridCard(
    categoryId: Long?,
    categoryName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditName: ((String) -> Unit)?,
    categoryProducts: List<ArticlesBasesStatsTable> = emptyList()
) {
    var catalogueParentList by remember { mutableStateOf(startupeDatas()) }
    var showEditDialog by remember { mutableStateOf(false) }

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
                verticalArrangement = Arrangement.Center
            ) {
                // Product images row (similar to RowProduitImages but adapted for smaller space)
                if (categoryProducts.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        categoryProducts.take(2).forEach { product -> // Show max 2 products due to space constraints
                            A_GlideDisplayImageByKeyId_Proto_5( //<--
                            //TODO(1): pk ca ne s affiche pas augment le height de item pour que s affiche 
                                produitVID = product.id,
                                modifier = Modifier.size(16.dp),
                                produitNom = product.nom,
                                size = 16.dp,
                                product = product,
                                qualityImage = 3,
                                refreshImage = product.actualiseSonImageTest2,
                                enableAutoScroll = false
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
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
