package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Produit
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Vendeur
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository._01_PeriodesVent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

private const val TAG = "PeriodeVenteScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodeVenteScreen(
    viewModel: PeriodeVenteViewModel = koinViewModel()
) {
    val produitKeyALog = "2025_04_19->11:00->2(Vendeur 2)->2(Produit 2)"

    // Observe UI state to force recomposition
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val periodesVente = viewModel.periodesVente
    val selectedPeriode by viewModel.selectedPeriode.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    // Track monitored product data changes
    val monitoredProduct by remember(uiState) {
        derivedStateOf {
            findProductByKey(periodesVente, produitKeyALog)
        }
    }

    // Log when monitored product changes
    LaunchedEffect(monitoredProduct?.quantity, uiState) {
        if (monitoredProduct != null) {
            Log.d(TAG, "Monitored product updated: $produitKeyALog")
            Log.d(TAG, "Current quantity: ${monitoredProduct?.quantity}")
            Log.d(TAG, "Current name: ${monitoredProduct?.nom}")
        } else {
            Log.d(TAG, "Monitored product not found: $produitKeyALog")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshData()
        Log.d(TAG, "Initial search for product: $produitKeyALog")
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Périodes de Vente") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Périodes selector
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(periodesVente) { periode ->
                        PeriodeItem(
                            periode = periode,
                            isSelected = periode.keyID == selectedPeriode?.keyID,
                            onClick = { viewModel.selectPeriode(periode) }
                        )
                    }
                }

                // Detail view
                selectedPeriode?.let { periode ->
                    PeriodeDetail(periode = periode)
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sélectionnez une période",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// Helper function to find a product by its key in the list of periods
fun findProductByKey(
    periodes: List<_01_PeriodesVent>,
    productKey: String
): Produit? {
    for (periode in periodes) {
        for (vendeur in periode.vendeurs) {
            for (produit in vendeur.produits) {
                if (produit.keyID == productKey) {
                    return produit
                }
            }
        }
    }
    return null
}

// Rest of the code remains unchanged...
@Composable
fun PeriodeItem(
    periode: _01_PeriodesVent,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
                MaterialTheme.shapes.small
            )
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Date: ${formatDate(periode.dateDebutDeCettePeriode)}",
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Heure: ${periode.tempDebutDeCettePeriode}",
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "${periode.vendeurs.size} vendeurs",
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PeriodeDetail(periode: _01_PeriodesVent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Période header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Détails de la période",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Date: ${formatDate(periode.dateDebutDeCettePeriode)}",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Heure: ${periode.tempDebutDeCettePeriode}",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Vendeurs: ${periode.vendeurs.size}",
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Vendeurs list
        Text(
            text = "Vendeurs",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(periode.vendeurs) { vendeur ->
                VendeurCard(vendeur = vendeur)
            }
        }
    }
}

@Composable
fun VendeurCard(
    vendeur: Vendeur,
    viewModel: PeriodeVenteViewModel = koinViewModel()
) {
    // Observe the monitored product quantity to force recomposition
    val monitoredProductQuantity by viewModel.monitoredProductQuantity.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = vendeur.nom,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Products table header
            if (vendeur.produits.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Produit",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = "Quantité",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }

                vendeur.produits.forEach { produit ->
                    ProduitRow(
                        produit = produit,
                        monitoredProductKey = "2025_04_19->11:00->2(Vendeur 2)->2(Produit 2)",
                        monitoredQuantity = if (produit.keyID == "2025_04_19->11:00->2(Vendeur 2)->2(Produit 2)")
                            monitoredProductQuantity else 0
                    )
                }
            } else {
                Text(
                    text = "Aucun produit",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

// Helper function to format date from yyyy_MM_dd to a more readable format
private fun formatDate(date: String): String {
    // Existing code...
    if (!date.matches(Regex("\\d{4}_\\d{2}_\\d{2}"))) {
        return date
    }

    val parts = date.split("_")
    return "${parts[2]}.${parts[1]}.${parts[0]}"
}
