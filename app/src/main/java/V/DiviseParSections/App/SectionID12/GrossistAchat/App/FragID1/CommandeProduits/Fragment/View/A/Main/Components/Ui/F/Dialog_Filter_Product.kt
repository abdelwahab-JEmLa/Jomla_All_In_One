package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.F

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M2Client
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.koinInject

@Composable
fun Dialog_Filter_Product(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    activeClient: M2Client?,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    onDismiss: (M01Produit?) -> Unit
) {
    val active_Central_Values = focusedValuesGetter.active_Central_Values
    val activePeriod = active_Central_Values.active_M14VentPeriode_AuFilterAchats
    val activeGrossist = active_Central_Values.active_M15Grossist_AuFilterAchats

    // Calculate products that correspond to the selected client's purchases
    val productsForClient = remember(
        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activeClient?.keyID,
        activePeriod?.keyID,
        activeGrossist?.keyID
    ) {
        val allProducts = viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue
        val allBonVents = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        val allVentOperations = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
        var allAchatOperations = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue

        // Filter achat operations by active filters
        activePeriod?.let { period ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M14VentPeriod_KeyID == period.keyID
            }
        }

        activeGrossist?.let { grossist ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
        }

        // If client is selected, filter by client's purchases
        if (activeClient != null) {
            val clientBonVents = allBonVents.filter { it.parent_M2Client_KeyID == activeClient.keyID }
            val clientBonVentIds = clientBonVents.map { it.keyID }.toSet()
            val clientVentOperations = allVentOperations.filter {
                it.parent_M8BonVent_KeyId in clientBonVentIds
            }

            // Get product IDs that this client has purchased
            val productIdsForClient = clientVentOperations.mapNotNull { ventOperation ->
                // Find achat operations that contain this vent operation
                val relatedAchatOperations = allAchatOperations.filter { achatOperation ->
                    achatOperation.get_list_v_Depuit_joinedStringKeys(listOf(ventOperation)).isNotEmpty()
                }
                relatedAchatOperations.map { it.parent_M1Produit_KeyID }
            }.flatten().toSet()

            allProducts.filter { it.keyID in productIdsForClient }
        } else {
            // If no client selected, show all products from achat operations
            val productIds = allAchatOperations.map { it.parent_M1Produit_KeyID }.toSet()
            allProducts.filter { it.keyID in productIds }
        }
    }

    Dialog(
        onDismissRequest = { onDismiss(null) },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header - Show active filters context
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (activeClient != null) 
                                "Produits de ${activeClient.nom}" 
                            else "Sélectionner un Produit",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Show active filters for context
                        if (activePeriod != null || activeGrossist != null || activeClient != null) {
                            val filterTexts = mutableListOf<String>()
                            activePeriod?.let { filterTexts.add("Période: ${it.get_DebugInfos()}") }
                            activeGrossist?.let { filterTexts.add("Grossiste: ${it.nom}") }
                            activeClient?.let { filterTexts.add("Client: ${it.nom}") }

                            Text(
                                text = "Filtres actifs: ${filterTexts.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(onClick = { onDismiss(null) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Products summary
                if (productsForClient.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Inventory,
                                contentDescription = "Résumé des produits",
                                tint = MaterialTheme.colorScheme.secondary
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Produits Disponibles",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Text(
                                    text = "${productsForClient.size} produits trouvés",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Clear Filter Option
                Card(
                    modifier = Modifier
                        .clickable {
                            focusedValuesGetter.removeProductFilter()
                            onDismiss(null)
                        }
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Supprimer le filtre produit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Supprimer le filtre produit",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Product List
                LazyColumn_Product(
                    viewModel = viewModel,
                    products = productsForClient,
                    activeClient = activeClient,
                    onProductSelected = { product ->
                        focusedValuesGetter.addProductFilter(product)
                        onDismiss(product)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun LazyColumn_Product(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    products: List<M01Produit>,
    activeClient: M2Client?,
    onProductSelected: (M01Produit) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (products.isEmpty()) {
            item {
                val message = if (activeClient != null) {
                    "Aucun produit trouvé pour ce client"
                } else {
                    "Aucun produit trouvé"
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(products) { product ->
                Item_Product(
                    product = product,
                    viewModel = viewModel,
                    activeClient = activeClient,
                    onProductSelected = onProductSelected
                )
            }
        }
    }
}

@Composable
fun Item_Product(
    product: M01Produit,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    activeClient: M2Client?,
    onProductSelected: (M01Produit) -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onProductSelected(product) }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Inventory,
                contentDescription = "Produit",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nom,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (product.nom.isNotBlank()) {
                    Text(
                        text = product.nom,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
