package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.StringEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.UnitEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.A_ProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.DisponibilityEtates
import Z_CodePartageEntreApps.Modules.CameraHandler.ProductImageCaptureButton
import Z_CodePartageEntreApps.Modules.Glide.A_GlideDisplayImageByKeyId_Proto_5
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.round

@Composable
fun ProductItem(
    modifier: Modifier = Modifier,
    mainComposRepository: A_ProduitDataBaseComposeRepositoryPJ17,
    produit: ArticlesBasesStatsTable,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showNameEditor by remember { mutableStateOf(false) }
    var showDetailsExpanded by remember { mutableStateOf(false) }

    fun updateProduct(updatedProduct: ArticlesBasesStatsTable) {
        // Calculer automatiquement le prix de vente unitaire basé sur prix de vente et prix d'achat
        val finalProduct = if (updatedProduct.nombreUniteInt > 0) {
            val prixVenteUnitaire =
                kotlin.math.round((updatedProduct.prixVent / updatedProduct.nombreUniteInt) * 100.0) / 100.0
            updatedProduct.copy(clientPrixVentUnite = prixVenteUnitaire)
        } else {
            updatedProduct.copy(clientPrixVentUnite = 0.0)
        }

        mainComposRepository.addOrUpdateData(finalProduct)
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
                        mainComposRepository.deleteData(produit)
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

    // Name editor dialog
    if (showNameEditor) {
        AlertDialog(
            onDismissRequest = { showNameEditor = false },
            title = { Text("Modifier le nom du produit") },
            text = {
                StringEditor(
                    currentValue = produit.nom,
                    label = "Nom du produit",
                    onValueUpdate = { newName ->
                        updateProduct(produit.copy(nom = newName))
                        showNameEditor = false
                    },
                    onCancel = { showNameEditor = false }
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Section with Image, Name, Status and Delete
            HeaderSection(
                produit = produit,
                showNameEditor = showNameEditor,
                showDeleteDialog = showDeleteDialog,
                onShowNameEditorChange = { showNameEditor = it },
                onShowDeleteDialogChange = { showDeleteDialog = it },
                updateProduct = ::updateProduct
            )

            // Quick Info Section
            QuickInfoSection(produit)

            // Details Section
            DetailleSection(
                showDetailsExpanded = showDetailsExpanded,
                produit = produit,
                onShowDetailsExpandedChange = { showDetailsExpanded = it },
                updateProduct = ::updateProduct
            )
        }
    }
}

@Composable
private fun DetailleSection(
    showDetailsExpanded: Boolean,
    produit: ArticlesBasesStatsTable,
    onShowDetailsExpandedChange: (Boolean) -> Unit,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {
    // Expandable Details Section
    FilledTonalButton(
        onClick = { onShowDetailsExpandedChange(!showDetailsExpanded) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = if (showDetailsExpanded) "Masquer les détails" else "Afficher les détails",
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = if (showDetailsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }

    // Detailed Editing Section (Expandable)
    if (showDetailsExpanded) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Prix Section
                Text(
                    text = "💰 Prix et Calculs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

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

                        if (produit.nombreUniteInt > 0) {
                            val prixUnitVente =
                                round((produit.prixVent / produit.nombreUniteInt) * 100.0) / 100.0
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

                        PriceEditor(
                            currentPrice = produit.prixAchat,
                            label = "Prix Achat Total",
                            onPriceUpdate = { newPrix ->
                                val newPrd = produit.copy(
                                    prixAchat = newPrix,
                                    prixAchatDernierTimeTempUpdate = System.currentTimeMillis()
                                )
                                updateProduct(newPrd)
                            },
                            showOnlyWhenPositive = true,
                            textColor = MaterialTheme.colorScheme.tertiary
                        )
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

                        if (produit.nombreUniteInt > 0) {
                            PriceEditor(
                                currentPrice = produit.clientPrixVentUnite,
                                label = "Prix Client/unité",
                                onPriceUpdate = { newClientPrixUnite ->
                                    updateProduct(produit.copy(clientPrixVentUnite = newClientPrixUnite))
                                },
                                textColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        }

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

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            val updatedProduct =
                                produit.copy(cachePrixVent = !produit.cachePrixVent)
                            updateProduct(updatedProduct)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (produit.cachePrixVent)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = if (produit.cachePrixVent) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (produit.cachePrixVent) "Prix Caché" else "Prix Visible",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            val updatedProduct =
                                produit.copy(heldPrioriteDemandAuGrossist = !produit.heldPrioriteDemandAuGrossist)
                            updateProduct(updatedProduct)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (produit.heldPrioriteDemandAuGrossist)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = if (produit.heldPrioriteDemandAuGrossist) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Priorité",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    produit: ArticlesBasesStatsTable,
    showNameEditor: Boolean,
    showDeleteDialog: Boolean,
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

@Composable
private fun QuickInfoSection(produit: ArticlesBasesStatsTable) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Prix de Vente
            QuickInfoCard(
                title = "Prix Vente",
                value = "${produit.prixVent} DA",
                icon = "💰",
                color = MaterialTheme.colorScheme.primary
            )

            // Unités
            if (produit.nombreUniteInt > 0) {
                QuickInfoCard(
                    title = "Unités",
                    value = "${produit.nombreUniteInt}",
                    icon = "📦",
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Bénéfice
            val benefice = produit.prixVent - produit.prixAchat
            if (benefice != 0.0) {
                QuickInfoCard(
                    title = "Bénéfice",
                    value = "${benefice} DA",
                    icon = if (benefice > 0) "📈" else "📉",
                    color = if (benefice > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun QuickInfoCard(
    title: String,
    value: String,
    icon: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier.width(100.dp),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
