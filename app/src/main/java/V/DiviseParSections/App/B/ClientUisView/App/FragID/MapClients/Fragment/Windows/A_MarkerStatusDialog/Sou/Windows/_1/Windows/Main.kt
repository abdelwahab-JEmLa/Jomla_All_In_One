package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows
  /*
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00.ProduitsVenduParLui
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00.VendeursActiveDonsCettePeriode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_VentsNoSQl
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject


@Composable
fun Main(
    modifier: Modifier = Modifier,
    viewModel: PeriodesViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val progress by viewModel._01_VentsHistoriquesDataBase_Repository.progressRepo.collectAsState()

    FilterMainScreen(
        uiState = uiState,
        progress = progress,
        modifier = modifier
    )
}

@Composable
fun FilterMainScreen(
    uiState: PeriodesUiState,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            // Show progress indicator when loading
            if (progress < 1.0f) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (uiState.a01PeriodesVent.isEmpty()) {
                    Text(
                        text = "Chargement des périodes...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    EditeInfosMainList(
                        uiState = uiState,
                    )
                }
            }
        }
    }
}

@Composable
private fun EditeInfosMainList(
    uiState: PeriodesUiState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(uiState.a01PeriodesVent) { periode ->
            PeriodeItem(periode)
        }
    }
}

@Composable
private fun PeriodeItem(periode: _01_VentsNoSQl) {
    var showRelatedPeriods by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Période de vente: ${periode.dateDebutDeCettePeriode} à ${periode.tempDebutDeCettePeriode}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Show total quantity
        Text(
            text = "Quantité totale: ${periode.getTotalQuantity()}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Show vendeurs in this period
        Text(
            text = "Vendeurs (${periode.vendeursActiveDonsCettePeriode.size}):",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        periode.vendeursActiveDonsCettePeriode.forEach { (vendeurId, vendeur) ->
            VendeurItem(vendeurId, vendeur)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Button to show/hide related periods
        Button(
            onClick = { showRelatedPeriods = !showRelatedPeriods },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (showRelatedPeriods) "Masquer périodes liées" else "Afficher périodes liées")
        }

        // Show related periods if expanded
        if (showRelatedPeriods) {
            RelatedPeriodsSection(periode)
        }

        Divider()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun RelatedPeriodsSection(periode: _01_VentsNoSQl) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Get related periods from the same day
        val sameDay = periode.getPeriodesFromSameDay()
        if (sameDay.isNotEmpty()) {
            Text(
                text = "Périodes du même jour (${sameDay.size}):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            sameDay.forEach { relatedPeriode ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Période à ${relatedPeriode.tempDebutDeCettePeriode}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Quantité totale: ${relatedPeriode.getTotalQuantity()}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Find shared vendeurs across periods
        val vendeurNames = periode.vendeursActiveDonsCettePeriode.values.map { it.nom }
        for (vendeurNom in vendeurNames) {
            val periodesWithVendeur = periode.getPeriodesWithVendeur(vendeurNom)
                .filter { it.keyID != periode.keyID } // Exclude current period

            if (periodesWithVendeur.isNotEmpty()) {
                Text(
                    text = "Périodes avec le vendeur \"$vendeurNom\" (${periodesWithVendeur.size}):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                periodesWithVendeur.forEach { relatedPeriode ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Période du ${relatedPeriode.dateDebutDeCettePeriode} à ${relatedPeriode.tempDebutDeCettePeriode}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Find the vendeur in this period
                            val vendeur = relatedPeriode.vendeursActiveDonsCettePeriode.values
                                .find { it.nom == vendeurNom }

                            if (vendeur != null) {
                                Text(
                                    text = "Quantité vendue par $vendeurNom: ${vendeur.getTotalQuantity()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun VendeurItem(vendeurId: String, vendeur: VendeursActiveDonsCettePeriode) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp)
    ) {
        Row {
            Text(
                text = vendeurId,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Total: ${vendeur.getTotalQuantity()}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Display each product sold by the vendeur
        vendeur.produitsVenduParLui.forEach { (produitId, produit) ->
            ProduitItem(produitId, produit)
        }
    }
}

@Composable
private fun ProduitItem(produitId: String, produit: ProduitsVenduParLui) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp)
    ) {
        Text(
            text = produitId,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Quantité: ${produit.quantity}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
                               */
