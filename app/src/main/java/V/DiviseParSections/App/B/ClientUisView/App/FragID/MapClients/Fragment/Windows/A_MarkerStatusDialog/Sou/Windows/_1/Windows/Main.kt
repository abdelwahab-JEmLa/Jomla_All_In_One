package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01._01_PeriodesVentNoSQl
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01.ProduitsVenduParLui
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01.VendeursActiveDonsCettePeriode
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    MainScreen(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
fun MainScreen(
    uiState: PeriodesUiState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            MainList(
                uiState = uiState,
            )
        }
    }
}

@Composable
private fun MainList(
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
private fun PeriodeItem(periode: _01_PeriodesVentNoSQl) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Période de vente",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display each vendeur in the periode
        periode.vendeursActiveDonsCettePeriode.forEach { (vendeurId, vendeur) ->
            VendeurItem(vendeurId, vendeur)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Divider()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VendeurItem(vendeurId: String, vendeur: VendeursActiveDonsCettePeriode) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp)
    ) {
        Text(
            text = "Vendeur: ${vendeurId.substringAfter("->").trim('(', ')')}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

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
            text = produitId.substringAfter("->").trim('(', ')'),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Quantité: ${produit.quantity}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
