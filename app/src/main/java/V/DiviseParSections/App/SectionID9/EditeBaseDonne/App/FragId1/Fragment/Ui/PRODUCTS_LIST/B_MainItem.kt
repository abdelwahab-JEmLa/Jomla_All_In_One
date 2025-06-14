package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.UnitEditor
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3.DisponibilityEtates
import Z_CodePartageEntreApps.Modules.CameraHandler.ProductImageCaptureButton
import Z_CodePartageEntreApps.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ProductItem(
    modifier: Modifier = Modifier,
    produitInit: ArticlesBasesStatsTable,
    onUpdate: (ArticlesBasesStatsTable) -> Unit = {},
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel = koinInject()
) {
    val TAG = "ProductItem"
    var produit by remember(produitInit.id, produitInit.actualiseSonImageTest2, produitInit.dernierFireBaseUpdateTimestamps) {
        mutableStateOf(produitInit)
    }
    var imageRefreshKey by remember(produitInit.id) { mutableIntStateOf(0) }

    // State for delete dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(produitInit.actualiseSonImageTest2, produitInit.dernierFireBaseUpdateTimestamps) {
        if (produitInit.id == produit.id &&
            (produitInit.actualiseSonImageTest2 != produit.actualiseSonImageTest2 ||
                    produitInit.dernierFireBaseUpdateTimestamps != produit.dernierFireBaseUpdateTimestamps)) {
            Log.d(TAG, "Updating product ${produitInit.nom}")
            produit = produitInit
            imageRefreshKey++
        }
    }

    fun updateProduct(updatedProduct: ArticlesBasesStatsTable) {
        produit = updatedProduct
        onUpdate(updatedProduct)
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Supprimer le produit",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Êtes-vous sûr de vouloir supprimer \"${produit.nom}\" ?\n\nCette action est irréversible."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                       viewModel.deleteArticlesBasesStatsTable(produit)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Supprimer",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Annuler")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        ) {
            // Header: Image + Name + Key Info + Delete ButtonAutreEtates
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Image with camera
                Box(modifier = Modifier.size(100.dp)) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ) {
                        A_GlideDisplayImageByKeyId_Proto_5(
                            product = produit,
                            produitVID = produit.id,
                            refreshImage = produit.actualiseSonImageTest2,
                            size = 100.dp,
                        )
                    }
                    ProductImageCaptureButton(
                        product = produit,
                        onImageCaptured = ::updateProduct,
                        modifier = Modifier.align(Alignment.TopEnd),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Product info with key metrics at top
                Column(modifier = Modifier.weight(1f)) {
                    val s = if (false) "id>${produit.id}" else ""
                    Text(
                        text =" ${produit.nom}$s",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (produit.nomArab.isNotEmpty()) {
                        Text(
                            text = produit.nomArab,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Key metrics row: Units + Client Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (produit.nombreUniteInt > 0) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${produit.nombreUniteInt} unités",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (produit.clientPrixVentUnite > 0) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${produit.clientPrixVentUnite} DA/u",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Availability status
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                        color = when (produit.disponibilityEtates) {
                            DisponibilityEtates.DISPO -> MaterialTheme.colorScheme.primaryContainer
                            DisponibilityEtates.NON_DISPO -> MaterialTheme.colorScheme.errorContainer
                            DisponibilityEtates.PETITE_PROBABILITY -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    ) {
                        Text(
                            text = produit.disponibilityEtates.nomArabe,
                            style = MaterialTheme.typography.labelMedium,
                            color = when (produit.disponibilityEtates) {
                                DisponibilityEtates.DISPO -> MaterialTheme.colorScheme.onPrimaryContainer
                                DisponibilityEtates.NON_DISPO -> MaterialTheme.colorScheme.onErrorContainer
                                DisponibilityEtates.PETITE_PROBABILITY -> MaterialTheme.colorScheme.onTertiaryContainer
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Delete button in top-right corner
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.padding(start = 8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing section (rest remains the same...)
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Prix et Calculs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Main prices and units
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left column
                        Column(modifier = Modifier.weight(1f)) {
                            PriceEditor(
                                currentPrice = produit.prixVent,
                                label = "Prix Vente Total",
                                onPriceUpdate = { newPrix ->
                                    updateProduct(produit.copy(prixVent = newPrix))
                                },
                                textColor = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Prix Achat with Quick Update ButtonAutreEtates
                            Column {
                                PriceEditor(
                                    currentPrice = produit.prixAchat,
                                    label = "Prix Achat Total",
                                    onPriceUpdate = { newPrix ->
                                        updateProduct(produit.copy(prixAchat = newPrix))
                                    },
                                    showOnlyWhenPositive = true,
                                    textColor = MaterialTheme.colorScheme.tertiary
                                )

                                // Quick Update ButtonAutreEtates for Prix Achat
                                Spacer(modifier = Modifier.height(4.dp))
                                FilledTonalButton(
                                    onClick = {
                                        val newPrixAchat = 0.1
                                        updateProduct(produit.copy(prixAchat = newPrixAchat))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Ajouter 0.1 DA",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+0.1 DA",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Right column
                        Column(modifier = Modifier.weight(1f)) {
                            UnitEditor(
                                currentUnits = produit.nombreUniteInt,
                                label = "Nombre Unités",
                                onUnitsUpdate = { newUnits ->
                                    updateProduct(produit.copy(nombreUniteInt = newUnits))
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val benefice = produit.prixVent - produit.prixAchat
                            PriceEditor(
                                currentPrice = benefice,
                                label = "Bénéfice Total",
                                onPriceUpdate = { newBenefice ->
                                    val newPrixVent = produit.prixAchat + newBenefice
                                    updateProduct(produit.copy(prixVent = newPrixVent))
                                },
                                textColor = if (benefice > 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    // Unit pricing (only show if units > 0)
                    if (produit.nombreUniteInt > 0) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Prix Unitaires",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Unit sale price
                            Column(modifier = Modifier.weight(1f)) {
                                val prixUnitVente = kotlin.math.round((produit.prixVent / produit.nombreUniteInt) * 100.0) / 100.0
                                PriceEditor(
                                    currentPrice = prixUnitVente,
                                    label = "Vente/unité",
                                    onPriceUpdate = { newPrixUnit ->
                                        val newPrixVent = newPrixUnit * produit.nombreUniteInt
                                        updateProduct(produit.copy(prixVent = newPrixVent))
                                    },
                                    textColor = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Unit purchase price
                            Column(modifier = Modifier.weight(1f)) {
                                val prixUnitAchat = kotlin.math.round((produit.prixAchat / produit.nombreUniteInt) * 100.0) / 100.0
                                PriceEditor(
                                    currentPrice = prixUnitAchat,
                                    label = "Achat/unité",
                                    onPriceUpdate = { newPrixAchatUnit ->
                                        val newPrixAchat = newPrixAchatUnit * produit.nombreUniteInt
                                        updateProduct(produit.copy(prixAchat = newPrixAchat))
                                    },
                                    showOnlyWhenPositive = true,
                                    textColor = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Client unit price with total calculation
                        PriceEditor(
                            currentPrice = produit.clientPrixVentUnite,
                            label = "Prix Client/unité",
                            onPriceUpdate = { newClientPrixUnite ->
                                updateProduct(produit.copy(clientPrixVentUnite = newClientPrixUnite))
                            },
                            textColor = MaterialTheme.colorScheme.inversePrimary,
                            additionalInfo = {
                                val totalClientPrice = produit.clientPrixVentUnite * produit.nombreUniteInt
                                if (totalClientPrice > 0) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "💰 Total Client: ${totalClientPrice} DA",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
