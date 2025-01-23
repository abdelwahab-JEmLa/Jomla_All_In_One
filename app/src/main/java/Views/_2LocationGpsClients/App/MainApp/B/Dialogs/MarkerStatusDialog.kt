package Views._2LocationGpsClients.App.MainApp.B.Dialogs

import Views._2LocationGpsClients.App.MainApp.ViewModel.Extension.Utils.updateA_AppSettingsSaverModel
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.ProduitModel.ClientBonVentModel.ClientInformations.GpsLocation.DernierEtatAAffiche
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import org.osmdroid.views.overlay.Marker

@Composable
fun MarkerStatusDialog(
    viewModel: ViewModelInitApp,
    selectedMarker: Marker?,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    if (selectedMarker == null) return

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedMarker.title ?: "Client",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // New Command Mode Button
                StatusButton(
                    text = "Mode Commande",
                    icon = Icons.Default.ShoppingCart,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.mapsHandler.updateStatueClient(
                                selectedMarker,
                                DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT
                            )
                            selectedMarker.updateA_AppSettingsSaverModel()
                            onDismiss()
                        }
                    }
                )
                StatusButton(
                    text = "Client Absent",
                    icon = Icons.Default.Person,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.mapsHandler.updateStatueClient(
                                selectedMarker,
                                DernierEtatAAffiche.CLIENT_ABSENT
                            )
                            onDismiss()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusButton(
                    text = "Avec Marchandise",
                    icon = Icons.Default.ShoppingCart,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.mapsHandler.updateStatueClient(
                                selectedMarker,
                                DernierEtatAAffiche.AVEC_MARCHANDISE
                            )
                            onDismiss()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusButton(
                    text = "Fermé",
                    icon = Icons.Default.Lock,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.mapsHandler.updateStatueClient(
                                selectedMarker,
                                DernierEtatAAffiche.FERME
                            )
                            onDismiss()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Fermer")
                }
            }
        }
    }
}

@Composable
private fun StatusButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text)
        }
    }
}
