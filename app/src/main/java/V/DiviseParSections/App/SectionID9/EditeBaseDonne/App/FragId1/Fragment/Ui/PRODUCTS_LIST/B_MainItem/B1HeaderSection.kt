package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A_ProduitDataBase.Repository.DisponibilityEtates
import Z_CodePartageEntreApps.Modules.CameraHandler.ProductImageCaptureButton
import Z_CodePartageEntreApps.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun HeaderSection(
    produit: ArticlesBasesStatsTable,
    onShowNameEditorChange: (Boolean) -> Unit,
    onShowDeleteDialogChange: (Boolean) -> Unit,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                // Top Row: Image and Product Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 48.dp), // Space for delete button
                    verticalAlignment = Alignment.Top
                ) {
                    // Product Image with Camera Overlay
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 6.dp
                        ) {
                            A_GlideDisplayImageByKeyId_Proto_5(
                                product = produit,
                                produitVID = produit.id,
                                refreshImage = produit.actualiseSonImageTest2,
                                size = 90.dp,
                            )
                        }

                        // Camera button overlay
                        ProductImageCaptureButton(
                            product = produit,
                            onImageCaptured = updateProduct,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(35.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Product Info Column
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // Product Name (Primary)
                        Surface(
                            onClick = { onShowNameEditorChange(true) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        ) {
                            Text(
                                text = produit.nom,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = if (produit.nom.any { it.code > 127 }) TextAlign.End else TextAlign.Start
                            )
                        }

                        // Arabic Name (if different and available)
                        if (produit.nomArab.isNotEmpty() && produit.nomArab != produit.nom) {
                            Text(
                                text = produit.nomArab,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

            }
        }


        // Floating Delete Button - positioned below the availability badge
        Surface(
            onClick = { onShowDeleteDialogChange(true) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 0.dp, end = 12.dp)
                .size(25.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.errorContainer,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer le produit",
                    modifier = Modifier.size(25.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Clickable Availability Status Badge - positioned above delete button
        Surface(
            onClick = {
                // Toggle availability state and update product
                val updatedProduct = produit.toggleDisponibilityEtates()
                updateProduct(updatedProduct)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = 8.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = when (produit.disponibilityEtates) {
                DisponibilityEtates.DISPO -> MaterialTheme.colorScheme.primaryContainer
                DisponibilityEtates.NON_DISPO -> MaterialTheme.colorScheme.errorContainer
                DisponibilityEtates.PETITE_PROBABILITY -> MaterialTheme.colorScheme.tertiaryContainer
            },
            shadowElevation = 3.dp
        ) {
            Text(
                text = produit.disponibilityEtates.nomArabe,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = when (produit.disponibilityEtates) {
                    DisponibilityEtates.DISPO -> MaterialTheme.colorScheme.onPrimaryContainer
                    DisponibilityEtates.NON_DISPO -> MaterialTheme.colorScheme.onErrorContainer
                    DisponibilityEtates.PETITE_PROBABILITY -> MaterialTheme.colorScheme.onTertiaryContainer
                },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

    }
}
