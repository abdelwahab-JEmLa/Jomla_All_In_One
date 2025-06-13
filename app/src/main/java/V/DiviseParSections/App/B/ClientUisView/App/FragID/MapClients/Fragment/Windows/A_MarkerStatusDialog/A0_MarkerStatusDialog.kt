package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.ButtonAddVocale.ButtonAjouteRecordVoiceHistoriqueC3_BonAchate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.View.A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MarkerStatusDialog(
    viewModel: MapClientsViewModel,
    clientOuCaMarqueGpsEstOuvert: B_ClientInfosProtoJuin3?,
    mapView: MapView,
    uiState: UiState,
    onUpdateLongAppSetting: () -> Unit = {},
    onClickToEditeMarquerPosition: (Long) -> Unit,
    onRemoveMark: (Marker?) -> Unit,
) {
    val marqueClick = mapView.overlays
        .filterIsInstance<Marker>()
        .find { marker ->
            marker.id == clientOuCaMarqueGpsEstOuvert?.id.toString()
        } ?: return

    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val clientId = clientOuCaMarqueGpsEstOuvert?.id ?: 0L
    var clientTypeMode by remember { mutableStateOf(clientOuCaMarqueGpsEstOuvert?.clientTypeMode) }

    if (editedName.isEmpty() && clientOuCaMarqueGpsEstOuvert != null) {
        editedName = clientOuCaMarqueGpsEstOuvert.nom ?: ""
        editedPhone = clientOuCaMarqueGpsEstOuvert.numTelephone ?: ""
    }

    Dialog(
        onDismissRequest = {
            viewModel
                .updateActiveComptIdClientOuSonMarqueMapEstOuvert(0L)

        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    ClientEdites(
                        viewModel = viewModel,
                        marqueClick = marqueClick,
                        marqueClickRelativeClient = clientOuCaMarqueGpsEstOuvert,
                        onDismiss = {
                            viewModel
                                .updateActiveComptIdClientOuSonMarqueMapEstOuvert(0L)
                        },
                        onClickToEditeMarquerPosition = onClickToEditeMarquerPosition,
                        onShowDeleteConfirmationChange = { showDeleteConfirmationDialog = it },
                        onClientTypeModeChange = { clientTypeMode = it },
                        onShowEditDialogChange = { showEditDialog = it },
                        onShowPhoneDialogChange = { showPhoneDialog = it },
                    )
                }

                uiState.activeCompt?.let { activeCompt ->
                    if (activeCompt.idClientOuSonMarqueMapEstOuvert != 0L) {
                        item {
                            AfficheurRegleOuvert(
                                uiState = uiState,
                                viewModel = viewModel,
                                relatedClients = clientOuCaMarqueGpsEstOuvert,
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 600.dp)
                            ) {

                                item {
                                    C3_TransactionCommercial.EtateActuellementEst.AVEC_MARCHANDISE
                                        .ButtonAutreEtates(
                                            uiState = uiState,
                                            viewModel = viewModel,
                                            clickedClient = clientId,
                                        )
                                }

                                item {
                                    CommandButton(
                                        modifier = Modifier.height(60.dp),
                                        viewModel = viewModel,
                                        clientOuCaMarqueGpsEstOuvert = clientOuCaMarqueGpsEstOuvert,
                                        uiState = uiState,
                                        etateActuellementEst1 = C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                                        clientId = clientId,
                                        selectedMarker = marqueClick,
                                        onUpdateLongAppSetting = onUpdateLongAppSetting,
                                        onDismiss = {
                                            viewModel
                                                .updateActiveComptIdClientOuSonMarqueMapEstOuvert(0L)
                                        },
                                        context = context
                                    )
                                }

                                item {
                                    C3_TransactionCommercial.EtateActuellementEst.FERME
                                        .ButtonAutreEtates(
                                            uiState = uiState,
                                            viewModel = viewModel,
                                            clickedClient = clientId,
                                        )
                                }
                                item {
                                    C3_TransactionCommercial.EtateActuellementEst.ACHETEUR_NON_DISPO
                                        .ButtonAutreEtates(
                                            uiState = uiState,
                                            viewModel = viewModel,
                                            clickedClient = clientId,
                                        )

                                }

                                item {
                                    ButtonAjouteRecordVoiceHistoriqueC3_BonAchate(
                                        uiState = uiState,
                                        viewModel = viewModel,
                                        clientId = clientId,
                                    )
                                }

                                item {
                                    C3_TransactionCommercial.EtateActuellementEst.Cible
                                        .ButtonAutreEtates(
                                            uiState = uiState,
                                            viewModel = viewModel,
                                            clickedClient = clientId,
                                        )
                                }

                                // Add null check for activeCompt before accessing vid property
                                uiState.activeCompt?.let { activeCompt ->
                                    if (activeCompt.vid == 2L) {
                                        item {
                                            C3_TransactionCommercial.EtateActuellementEst.CIBLE_POUR_2
                                                .ButtonAutreEtates(
                                                    uiState = uiState,
                                                    viewModel = viewModel,
                                                    clickedClient = clientId,
                                                )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Always show transaction history
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "سجل المعاملات",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
                        modifier = Modifier.fillMaxWidth(),
                        idClient = clientId
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
                            clientOuCaMarqueGpsEstOuvert?.apply {
                                nom = editedName
                                numTelephone = editedPhone
                            }

                            clientOuCaMarqueGpsEstOuvert?.let { client ->
                                viewModel.updateData(client)
                            }

                            showEditDialog = false
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
            val client = uiState.b_ClientInfosProtoJuin3List.find {
                it.id.toString() == marqueClick.id
            }
            AlertDialog(
                onDismissRequest = { showPhoneDialog = false },
                title = { Text("Numéro de téléphone") },
                text = {
                    Text(
                        text = client?.numTelephone ?: "",
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
                            val clientToDelete =
                                uiState.b_ClientInfosProtoJuin3List.find {
                                    it.id.toString() == marqueClick.id
                                }

                            clientToDelete?.let { client ->
                                // Find and delete the corresponding B_ClientInfosProtoJuin3 object
                                val relatedClient = viewModel.bProto_ClientsDataBase.find {
                                    it.id == client.id
                                }

                                // Delete the client from repository
                                relatedClient?.let {
                                    viewModel.deleteUnSeulData(it)
                                }

                                // Remove the marker from the map
                                onRemoveMark(marqueClick)

                                viewModel
                                    .updateActiveComptIdClientOuSonMarqueMapEstOuvert(0L)

                            }
                            showDeleteConfirmationDialog = false
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
}
