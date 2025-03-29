package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.B.Dialogs

import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.B_ClientsDataBase.EtatesMutable.ClientTypeMode
import Z_CodePartageEntreApps.Model._ModelAppsFather
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Extension.Utils.updateLongAppSetting
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Extension.ViewModelExtension_App2_F1
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.ViewModel_App2FragID1
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tornado
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.views.overlay.Marker

@Composable
fun MarkerStatusDialog(
    viewModele: ViewModel_App2FragID1 = koinViewModel(),
    viewModelEXT: ViewModelExtension_App2_F1,
    viewModelInitApp: ViewModelInitApp,
    selectedMarker: Marker?,
    onDismiss: () -> Unit,
    onUpdateLongAppSetting: () -> Unit = {},
    onClickToEditeMarquerPosition: (Long) -> Unit,
    onRemoveMark: (Marker?) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val relatedClients = viewModele.b_ClientsDataBase.find {
        it.id == (selectedMarker?.id?.toLong() ?: 0)
    }
    var clientTypeMode = relatedClients?.etatesMutable?.clientTypeMode

    if (selectedMarker == null) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Delete Icon
                    Card(
                        modifier = Modifier
                            .background(color = Color.Red)
                            .clickable {
                                showDeleteConfirmationDialog = true
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete client",
                        )
                    }
                    // Location Edit Icon
                    Card(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                onClickToEditeMarquerPosition(selectedMarker.id.toLong())
                                onDismiss()
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Edit location"
                        )
                    }
                    // Client Type Mode Toggle
                    Card(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                clientTypeMode = when (clientTypeMode) {
                                    ClientTypeMode.ANCIEN -> ClientTypeMode.NEVEAU
                                    ClientTypeMode.NEVEAU -> ClientTypeMode.EVITE
                                    ClientTypeMode.EVITE -> ClientTypeMode.ANCIEN
                                    null -> ClientTypeMode.NEVEAU
                                }

                                // Update the client's type mode
                                relatedClients?.let { client ->
                                    client.etatesMutable.clientTypeMode = clientTypeMode!!
                                    viewModele.updateClient(client)
                                }
                            }
                    ) {
                        clientTypeMode?.let {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = "Toggle Client Type",
                                tint = it.color
                            )
                        }
                    }
                }

                Card (
                    modifier = Modifier.clickable { showEditDialog = true }
                        .fillMaxWidth()
                ){
                    Column {
                        Text(
                            text = selectedMarker.title ?: "Client",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        if (!relatedClients?.statueDeBase?.numTelephone.isNullOrEmpty()) {
                            Text(
                                text = relatedClients?.statueDeBase?.numTelephone ?: "",
                                modifier = Modifier.clickable { showPhoneDialog = true },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Status buttons remain the same
                StatusButton(
                    text = "Mode Commande",
                    icon = Icons.Default.ShoppingCart,
                    color = Color(
                        ContextCompat.getColor(
                            context,
                            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT.color
                        )
                    ),
                    onClick = {
                        coroutineScope.launch {
                            viewModelEXT.updateLongAppSetting(selectedMarker.id.toLong())
                            onUpdateLongAppSetting()

                            onDismiss()
                        }
                    }
                )


                StatusButton(
                    text = "Client Absent",
                    icon = Icons.Default.Person,
                    color = Color(
                        ContextCompat.getColor(
                            context,
                            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CLIENT_ABSENT.color
                        )
                    ),
                    onClick = {
                        coroutineScope.launch {
                            viewModelEXT.updateStatueClient(
                                selectedMarker,
                                B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CLIENT_ABSENT
                            )
                            onDismiss()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusButton(
                    text = "Avec Marchandise",
                    icon = Icons.Default.ShoppingCart,
                    color = Color(
                        ContextCompat.getColor(
                            context,
                            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.AVEC_MARCHANDISE.color
                        )
                    ),
                    onClick = {
                        coroutineScope.launch {
                            viewModelEXT.updateStatueClient(
                                selectedMarker,
                                B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.AVEC_MARCHANDISE
                            )
                            onDismiss()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusButton(
                    text = "Fermé",
                    icon = Icons.Default.Lock,
                    color = Color(
                        ContextCompat.getColor(
                            context,
                            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.FERME.color
                        )
                    ),
                    onClick = {
                        coroutineScope.launch {
                            viewModelEXT.updateStatueClient(
                                selectedMarker,
                                B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.FERME
                            )
                            onDismiss()
                        }
                    }
                )


                Spacer(modifier = Modifier.height(16.dp))

                StatusButton(
                    text = "Client Cible",
                    icon = Icons.Default.Person,
                    color = Color(
                        ContextCompat.getColor(
                            context,
                            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.Cible.color
                        )
                    ),
                    onClick = {
                        coroutineScope.launch {
                            viewModelEXT.updateStatueClient(
                                selectedMarker,
                                B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.Cible
                            )
                            onDismiss()
                        }
                    }
                )

                val CIBLE_POUR_2 =
                    B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CIBLE_PRIORITE_2

                StatusButton(
                    text = CIBLE_POUR_2.toString(),
                    icon = Icons.Default.Tornado,
                    color = Color(ContextCompat.getColor(context, CIBLE_POUR_2.color)),
                    onClick = {
                        coroutineScope.launch {

                            viewModelEXT.updateStatueClient(selectedMarker, CIBLE_POUR_2)
                            onDismiss()
                        }
                    }
                )

                StatusButton(
                    text = "CIBLE_POUR_2",
                    icon = Icons.Default.Person,
                    color = Color(
                        ContextCompat.getColor(
                            context,
                            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CIBLE_POUR_2.color
                        )
                    ),
                    onClick = {
                        coroutineScope.launch {
                            viewModelEXT.updateStatueClient(
                                selectedMarker,
                                B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.CIBLE_POUR_2
                            )
                            onDismiss()
                        }
                    }
                )

                StatusButton(
                    text = "A_EVITE",
                    icon = Icons.Default.Person,
                    color = Color(
                        ContextCompat.getColor(
                            context,
                            B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.A_EVITE.color
                        )
                    ),
                    onClick = {
                        coroutineScope.launch {
                            viewModelEXT.updateStatueClient(
                                selectedMarker,
                                B_ClientsDataBase.GpsLocation.DernierEtatAAffiche.A_EVITE
                            )
                            onDismiss()
                        }
                    }
                )
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Modifier les informations") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nom du client") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editedPhone,
                        onValueChange = { editedPhone = it },
                        label = { Text("Numéro de téléphone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            selectedMarker.title = editedName
                            val client = viewModelInitApp._modelAppsFather.clientDataBase.find {
                                it.id.toString() == selectedMarker.id
                            }

                            client?.let { foundClient ->
                                foundClient.nom = editedName
                                foundClient.statueDeBase.numTelephone = editedPhone
                                viewModelInitApp.updateClientsDataBase(foundClient)

                                viewModelInitApp._modelAppsFather.produitsMainDataBase
                                    .filter { product ->
                                        product.bonsVentDeCetteCota.any { bonVent ->
                                            bonVent.clientIdChoisi == foundClient.id
                                        }
                                    }
                                    .forEach { product ->
                                        _ModelAppsFather.updateProduit(product, viewModelInitApp)
                                    }
                            }
                            selectedMarker.showInfoWindow()
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    if (showPhoneDialog) {
        val client = viewModelInitApp._modelAppsFather.clientDataBase.find {
            it.id.toString() == selectedMarker.id
        }
        AlertDialog(
            onDismissRequest = { showPhoneDialog = false },
            title = { Text("Numéro de téléphone") },
            text = {
                Text(
                    text = client?.statueDeBase?.numTelephone ?: "",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(onClick = { showPhoneDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

// New delete confirmation dialog
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Confirmer la suppression") },
            text = {
                Text("Êtes-vous sûr de vouloir supprimer ce client ? Cette action est irréversible.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            val clientToDelete =
                                viewModelInitApp._modelAppsFather.clientDataBase.find {
                                    it.id.toString() == selectedMarker.id
                                }

                            clientToDelete?.let { client ->
                                B_ClientsDataBase.refClientsDataBase
                                    .child(client.id.toString())
                                    .removeValue()
                                    .await()

                                viewModelInitApp._modelAppsFather.clientDataBase.remove(client)
                                onRemoveMark(selectedMarker)
                                onDismiss()
                            }
                            showDeleteConfirmationDialog = false
                        }
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun StatusButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color
        )
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
