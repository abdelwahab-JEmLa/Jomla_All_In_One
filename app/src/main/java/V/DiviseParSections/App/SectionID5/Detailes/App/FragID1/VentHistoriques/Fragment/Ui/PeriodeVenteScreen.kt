package V.DiviseParSections.App.SectionID5.Detailes.App.FragID1.VentHistoriques.Fragment.Ui

import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models._013_ClientTransaction
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models._015_Produits
import Z_CodePartageEntreApps.DataBase._01_VentsHistoriques.Models._01_PeriodVentHistorique
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID1.VentHistoriques.Fragment.ViewModel.PeriodeVenteViewModel
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID1.VentHistoriques.Fragment.ViewModel.ViewMode
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

private const val TAG = "PeriodeVenteScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodeVenteScreen(
    viewModel: PeriodeVenteViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Périodes de Vente") },
                actions = {
                    IconButton(onClick = { viewModel.notifyDataChanged() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir")
                    }
                    if (uiState.viewMode == ViewMode.DETAIL) {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            if (uiState.viewMode != ViewMode.DETAIL) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Rechercher...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                )

            }

            // View mode tabs
            if (uiState.viewMode != ViewMode.DETAIL) {
                val viewModes = listOf(ViewMode.LIST, ViewMode.CALENDAR, ViewMode.ANALYTICS)
                // And upsertLenceCommandeRepoGroupedProtoAvanJuin3 the TabRow in PeriodeVenteScreen.kt:
                TabRow(
                    selectedTabIndex = viewModes.indexOf(uiState.viewMode)
                ) {
                    viewModes.forEachIndexed { index, mode ->
                        Tab(
                            selected = uiState.viewMode == mode,
                            onClick = { viewModel.setViewMode(mode) },
                            text = { Text(mode.name) },
                            icon = {
                                when (mode) {
                                    ViewMode.LIST -> Icon(Icons.Default.ViewList, null)
                                    ViewMode.CALENDAR -> Icon(Icons.Default.DateRange, null)
                                    else -> Icon(Icons.Default.ViewList, null)
                                }
                            }
                        )
                    }
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                // Error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                // Content based on view mode
                when (uiState.viewMode) {
                    ViewMode.DETAIL -> {
                        // Detail view for selected period
                        uiState.selectedPeriode?.let { periode ->
                            PeriodeDetailScreen(
                                periode = periode,
                                onBack = { viewModel.clearSelection() }
                            )
                        }
                    }
                    ViewMode.LIST -> {

                        LazyColumn {
                            items(uiState.filteredPeriodes.ifEmpty { viewModel.periodesVente }) { periode ->
                                PeriodeListItem(
                                    periode = periode,
                                    onClick = { viewModel.selectPeriode(periode) }
                                )
                            }
                        }
                    }
                    else -> {
                        // Other view modes (calendar, analytics)
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Mode de vue ${uiState.viewMode.name} en développement")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PeriodeListItem(
    periode: _01_PeriodVentHistorique,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {


            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ID: ${periode.fireBaseKeyID}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${periode.child_012_Compts_Vendeurs.size} vendeurs",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(16.dp))

                val totalAcheteurs = periode.child_012_Compts_Vendeurs.sumOf { it.child_013_Acheteurs.size }
                Text(
                    text = "$totalAcheteurs acheteurs",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun PeriodeDetailScreen(
    periode: _01_PeriodVentHistorique,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Period header
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Période de Vente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ID: ${periode.fireBaseKeyID}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vendeurs sectionSqlRepository
        Text(
            text = "Vendeurs (${periode.child_012_Compts_Vendeurs.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(periode.child_012_Compts_Vendeurs) { vendeur ->
                VendeurItem(vendeur = vendeur)
            }
        }
    }
}

@Composable
fun VendeurItem(vendeur: _012_ComptsVendeurs) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = vendeur.startDesignation,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Acheteurs (${vendeur.child_013_Acheteurs.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    vendeur.child_013_Acheteurs.forEach { acheteur ->
                        AcheteurItem(acheteur = acheteur)
                    }
                }
            }
        }
    }
}

@Composable
fun AcheteurItem(acheteur: _013_ClientTransaction) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { expanded = !expanded },
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = acheteur.startDesignation,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Créé le: ${acheteur.tempDateCreationStr}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${acheteur.child_14Produits.size} produits",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color.White.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Produits",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    acheteur.child_14Produits.forEach { produit ->
                        ProduitItem(produit = produit)
                    }
                }
            }
        }
    }
}

@Composable
fun ProduitItem(produit: _015_Produits) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = produit.startDesignation,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "ID: ${produit.bsonObjectId}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = "Qté: ${produit.quantity}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}
