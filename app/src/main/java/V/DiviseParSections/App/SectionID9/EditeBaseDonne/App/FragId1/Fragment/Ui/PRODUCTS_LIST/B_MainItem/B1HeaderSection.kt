package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import Z_CodePartageEntreApps.Modules.CameraHandler.ProductImageCaptureButton
import Z_CodePartageEntreApps.Modules.D.Glide.Proto.A_GlideDisplayImageByKeyId_Proto_5
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HeaderSection(
    produit: ArticlesBasesStatsTable,
    onShowNameEditorChange: (Boolean) -> Unit,
    onShowDeleteDialogChange: (Boolean) -> Unit,
    updateProduct: (ArticlesBasesStatsTable) -> Unit,
    paddingDefaulte: Dp,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingDefaulte)
        ) {
            // Top Row: Image and Product Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Product Image with Camera Overlay
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp
                    ) {
                        A_GlideDisplayImageByKeyId_Proto_5(
                            product = produit,
                            produitVID = produit.id,
                            refreshImage = produit.actualiseSonImageTest2,
                            size = 70.dp,
                        )
                    }

                    // Camera button overlay
                    ProductImageCaptureButton(
                        product = produit,
                        onImageCaptured = updateProduct,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(26.dp),
                    )
                }

                Spacer(modifier = Modifier.width(paddingDefaulte))

                // Product Info Column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Product Name (Primary) - Clickable
                    Surface(
                        onClick = { onShowNameEditorChange(true) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ) {
                        val bsonObjectId =  produit.bsonObjectId.takeLast(4).uppercase()
                        val nom =  produit.nom
                        Text(
                            text = "$nom->$bsonObjectId",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(paddingDefaulte),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                        )
                    }
                }
            }

            // Action Row - Boutons d'action alignés horizontalement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingDefaulte),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bouton de suppression (à droite)
                Surface(
                    onClick = { onShowDeleteDialogChange(true) },
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(paddingDefaulte),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer le produit",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                // Bouton de statut de disponibilité (à gauche - priorité)
                Surface(
                    onClick = {
                        val updatedProduct = produit.toggleDisponibilityEtates()
                        updateProduct(updatedProduct)
                    },
                    modifier = Modifier.weight(1f),
                    color = when (produit.disponibilityEtates) {
                        DisponibilityEtates.DISPO -> MaterialTheme.colorScheme.primaryContainer
                        DisponibilityEtates.NON_DISPO -> MaterialTheme.colorScheme.errorContainer
                        DisponibilityEtates.PETITE_PROBABILITY -> MaterialTheme.colorScheme.tertiaryContainer
                    },
                    shape = RoundedCornerShape(10.dp),
                    shadowElevation = 2.dp
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
                        modifier = Modifier.padding(paddingDefaulte),
                        textAlign = TextAlign.Center
                    )
                }


            }
        }
    }
}
