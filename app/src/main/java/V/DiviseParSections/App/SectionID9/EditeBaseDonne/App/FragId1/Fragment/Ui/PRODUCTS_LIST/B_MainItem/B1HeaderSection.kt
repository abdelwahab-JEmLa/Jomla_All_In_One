package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.DisponibilityEtates
import Z_CodePartageEntreApps.Modules.CameraHandler.ProductImageCaptureButton
import Z_CodePartageEntreApps.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
 fun HeaderSection(
    produit: ArticlesBasesStatsTable,
    onShowNameEditorChange: (Boolean) -> Unit,
    onShowDeleteDialogChange: (Boolean) -> Unit,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image with Camera Overlay
            Box(
                modifier = Modifier
                    .size(80.dp)
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
                        size = 80.dp,
                    )
                }

                // Camera button overlay
                ProductImageCaptureButton(
                    product = produit,
                    onImageCaptured = updateProduct,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Product Name and Status
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Clickable Product Name
                Surface(
                    onClick = { onShowNameEditorChange(true) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = produit.nom,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (produit.nomArab.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = produit.nomArab,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Availability Status Badge
                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                    color = when (produit.disponibilityEtates) {
                        DisponibilityEtates.DISPO -> MaterialTheme.colorScheme.primaryContainer
                        DisponibilityEtates.NON_DISPO -> MaterialTheme.colorScheme.errorContainer
                        DisponibilityEtates.PETITE_PROBABILITY -> MaterialTheme.colorScheme.tertiaryContainer
                    },
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = produit.disponibilityEtates.nomArabe,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = when (produit.disponibilityEtates) {
                            DisponibilityEtates.DISPO -> MaterialTheme.colorScheme.onPrimaryContainer
                            DisponibilityEtates.NON_DISPO -> MaterialTheme.colorScheme.onErrorContainer
                            DisponibilityEtates.PETITE_PROBABILITY -> MaterialTheme.colorScheme.onTertiaryContainer
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Delete Button
            IconButton(
                onClick = { onShowDeleteDialogChange(true) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer le produit",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
