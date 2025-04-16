package Z_CodePartageEntreApps.Proto.Test.FragID1.DemiNoSQL.Fragment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Composable to display the CommandesUiState
@Composable
fun CommandesUiStateDisplay(viewModel: CommandesViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = "Périodes de Vente",
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(uiState.periodesVent) { periode ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Période: ${periode.keyID}",
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vendeurs: ${periode.vendeursActiveDonsCettePeriode.size}",
                    )

                    periode.vendeursActiveDonsCettePeriode.forEach { vendeur ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = vendeur.keyID,
                                )

                                vendeur.produitsVenduParLui.forEach { produit ->
                                    Text(
                                        text = "${produit.nom} - Quantité: ${produit.quantity}",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
