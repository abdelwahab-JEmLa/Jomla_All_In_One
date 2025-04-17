package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

// Composable to display the CommandesUiState
@Composable
fun CommandesUiStateDisplay(viewModel: CommandesViewModel) {
    val uiState by viewModel.uiState.collectAsState()

          /*
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
  }             */

}
