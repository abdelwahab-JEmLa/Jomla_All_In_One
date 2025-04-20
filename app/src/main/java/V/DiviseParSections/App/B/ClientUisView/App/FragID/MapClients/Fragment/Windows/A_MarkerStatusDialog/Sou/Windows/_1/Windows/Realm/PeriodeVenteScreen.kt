package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Vendeur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Refresh"
                        )
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
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onClearQuery = { viewModel.updateSearchQuery("") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Content area - Split view with list and details
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // List of periods
                    PeriodesList(
                        periodes = uiState.filteredPeriodes,
                        selectedPeriode = uiState.selectedPeriode,
                        onPeriodeSelected = viewModel::selectPeriode,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                    // Detail view
                    Spacer(modifier = Modifier.width(16.dp))

                    uiState.selectedPeriode?.let { periode ->
                        PeriodeDetail(
                            periode = periode,
                            onClearSelection = viewModel::clearSelection,
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                        )
                    } ?: run {
                        EmptyDetailState(
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Rechercher...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        singleLine = true
    )
}

@Composable
fun PeriodesList(
    periodes: List<_01_PeriodesVent>,
    selectedPeriode: _01_PeriodesVent?,
    onPeriodeSelected: (_01_PeriodesVent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Périodes (${periodes.size})",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(periodes) { periode ->
                    val isSelected = selectedPeriode?.keyID == periode.keyID

                    PeriodeItem(
                        periode = periode,
                        isSelected = isSelected,
                        onClick = { onPeriodeSelected(periode) }
                    )
                }
            }
        }
    }
}

@Composable
fun PeriodeItem(
    periode: _01_PeriodesVent,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Date: ${formatDate(periode.dateDebutDeCettePeriode)}",
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )

            Text(
                text = "Heure: ${periode.tempDebutDeCettePeriode}",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )

            Text(
                text = "Vendeurs: ${periode.vendeurs.size}",
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

@Composable
fun PeriodeDetail(
    periode: _01_PeriodesVent,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Header with clear button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Détails de la période",
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = onClearSelection) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Selection"
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Period details
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Date: ${formatDate(periode.dateDebutDeCettePeriode)}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Heure: ${periode.tempDebutDeCettePeriode}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Vendeurs",
                    style = MaterialTheme.typography.titleSmall
                )

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // List of vendeurs
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(periode.vendeurs) { vendeur ->
                        VendeurItem(vendeur = vendeur)
                    }
                }
            }
        }
    }
}

@Composable
fun VendeurItem(vendeur: Vendeur) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            )
            .padding(16.dp)
    ) {
        Text(
            text = vendeur.keyID,
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Produits:",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        vendeur.produits.forEach { produit ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = produit.nomProduit,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Qté: ${produit.quantity}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun EmptyDetailState(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Sélectionnez une période pour voir les détails",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helper function to format the date from yyyy_MM_dd to a more readable format
private fun formatDate(rawDate: String): String {
    return try {
        val parts = rawDate.split("_")
        if (parts.size == 3) {
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } else {
            rawDate
        }
    } catch (e: Exception) {
        rawDate
    }
}
