package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Mode_EditePrixs

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.PriceAndUnitSection
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ItsLancedDepuit
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.TariffsButtonsSec7ID2
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun AffichageDuMode_EditePrix(
    viewModel: Sec9FragId1ViewId2ViewModel = koinViewModel(),
    relative_produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit,
    onShowDeleteDialogChange: (Boolean) -> Unit,
    onShowNameEditorChange: (Boolean) -> Unit, // Added name editor parameter
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
                            product = relative_produit,
                            produitVID = relative_produit.id,
                            refreshImage = relative_produit.actualiseSonImageTest2,
                            size = 70.dp,
                        )
                    }

                    // Camera button overlay
                    ProductImageCaptureButton(
                        product = relative_produit,
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
                    // Product Name (Primary) - Now clickable for editing
                    Surface(
                        onClick = { onShowNameEditorChange(true) }, // FIXED: Made clickable
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    ) {
                        val bsonObjectId = relative_produit.bsonObjectId.takeLast(4).uppercase()
                        val nom = relative_produit.nom
                        Text(
                            text = "$nom->$bsonObjectId",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(paddingDefaulte),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = if (relative_produit.nom.any { it.code > 127 }) TextAlign.End else TextAlign.Start
                        )
                    }

                    // Arabic Name (if different and available)
                    if (relative_produit.nomArab.isNotEmpty() && relative_produit.nomArab != relative_produit.nom) {
                        Text(
                            text = relative_produit.nomArab,
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(paddingDefaulte),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Delete button (on the right)
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
                        // Availability status button (on the left - priority)
                        Surface(
                            onClick = {
                                val updatedProduct = relative_produit.toggleDisponibilityEtates()
                                updateProduct(updatedProduct)
                            },
                            modifier = Modifier.weight(1f),
                            color = when (relative_produit.disponibilityEtates) {
                                DisponibilityEtates.DISPO -> MaterialTheme.colorScheme.primaryContainer
                                DisponibilityEtates.NON_DISPO -> MaterialTheme.colorScheme.errorContainer
                                DisponibilityEtates.PETITE_PROBABILITY -> MaterialTheme.colorScheme.tertiaryContainer
                            },
                            shape = RoundedCornerShape(10.dp),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = relative_produit.disponibilityEtates.nomArabe,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (relative_produit.disponibilityEtates) {
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

            PriceAndUnitSection(
                produit = relative_produit,
                updateProduct = updateProduct
            )
            TariffsButtonsSec7ID2(
                lancedDepuitAffiche= ItsLancedDepuit.EditeBaseDonne(relative_produit),
                its_ProduitVentsInfosDialog = true
            )
        }
    }
}
