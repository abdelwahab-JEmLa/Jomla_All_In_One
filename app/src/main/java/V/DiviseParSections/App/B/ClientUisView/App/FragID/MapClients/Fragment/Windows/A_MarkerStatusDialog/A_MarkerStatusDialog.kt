package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View.A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.osmdroid.views.overlay.Marker

@Composable
fun MarkerStatusDialog(
    viewModel: ViewModel_MapClients_App2FragID1,
    viewModelInitApp: ViewModelInitApp,
    selectedMarker: Marker?,
    onDismiss: () -> Unit,
    onUpdateLongAppSetting: () -> Unit = {},
    onClickToEditeMarquerPosition: (Long) -> Unit,
    onRemoveMark: (Marker?) -> Unit,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
) {
    val ceTelephoneEstDeAbdelwahab = _0_0_HeadOfRepositorys_Repository
        .repositorys_Model
        .activeIdDe_1_5_Vendeur == 2L
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val relatedClients = viewModel.bProto_ClientsDataBase.find {
        it.id == (selectedMarker?.id?.toLong() ?: 0)
    }
    var clientTypeMode by remember { mutableStateOf(relatedClients?.clientTypeMode) }
    val repositorysModel =
        _0_0_HeadOfRepositorys_Repository.repositorys_Model

    val ceComptVendeurInsertBonsAchatAuPeriodID =
        repositorysModel.repository_1_5_Vendeur.modelDatasSnapList
            .find { it.vid == repositorysModel.activeIdDe_1_5_Vendeur }
            ?.ceComptVendeurInsertBonsAchatAuPeriodID

    val clientId = relatedClients?.id ?: 0L

    // Initialize editedName and editedPhone with current values
    if (editedName.isEmpty() && relatedClients != null) {
        editedName = relatedClients.nom ?: ""
        editedPhone = relatedClients.numTelephone ?: ""
    }

    // Check if a BonAchat already exists for this client in the active period
    val existingBonAchat = viewModel.modelDatasSnapList_1_3_BonAchat.find {
        it.clientAcheteurID == clientId && it.parentVID_1_4_PeriodeVent == ceComptVendeurInsertBonsAchatAuPeriodID
    }

    if (selectedMarker == null) return

    Dialog(
        onDismissRequest = onDismiss,
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
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ClientEdites(
                    showDeleteConfirmationDialog = showDeleteConfirmationDialog,
                    onClickToEditeMarquerPosition = onClickToEditeMarquerPosition,
                    selectedMarker = selectedMarker,
                    onDismiss = onDismiss,
                    clientTypeMode = clientTypeMode,
                    relatedClients = relatedClients,
                    viewModel = viewModel,
                    showEditDialog = showEditDialog,
                    showPhoneDialog = showPhoneDialog,
                    onShowDeleteConfirmationChange = { showDeleteConfirmationDialog = it },
                    onClientTypeModeChange = { clientTypeMode = it },
                    onShowEditDialogChange = { showEditDialog = it },
                    onShowPhoneDialogChange = { showPhoneDialog = it }
                )
                // In the MarkerStatusDialog composable, replace the linear button section with this:
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {


                        // Use a LazyVerticalGrid with 2 columns
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            item {
                                ACHETEUR_NON_DISPO(
                                    coroutineScope = coroutineScope,
                                    selectedMarker = selectedMarker,
                                    relatedClients = relatedClients,
                                    viewModel = viewModel,
                                    onDismiss = onDismiss,
                                    repositorysModel = repositorysModel,
                                    clientId = clientId,
                                    context = context,
                                )
                            }
                            item {
                                CommandButton(
                                    modifier = Modifier.height(60.dp),
                                    coroutineScope = coroutineScope,
                                    existingBonAchat = existingBonAchat,
                                    repositorysModel = repositorysModel,
                                    clientId = clientId,
                                    ceComptVendeurInsertBonsAchatAuPeriodID = ceComptVendeurInsertBonsAchatAuPeriodID,
                                    selectedMarker = selectedMarker,
                                    viewModel = viewModel,
                                    onUpdateLongAppSetting = onUpdateLongAppSetting,
                                    onDismiss = onDismiss,
                                    relatedClients = relatedClients,
                                    context = context,
                                )
                            }
                            item {
                                AVEC_MARCHANDISE(
                                    coroutineScope = coroutineScope,
                                    relatedClients = relatedClients,
                                    viewModel = viewModel,
                                    onDismiss = onDismiss,
                                    repositorysModel = repositorysModel,
                                    clientId = clientId,
                                    context = context,
                                )
                            }
                            item {
                                FERME(
                                    coroutineScope = coroutineScope,
                                    relatedClients = relatedClients,
                                    viewModel = viewModel,
                                    onDismiss = onDismiss,
                                    repositorysModel = repositorysModel,
                                    clientId = clientId,
                                    context = context,
                                )
                            }
                        }
                    }
                }

                if (ceTelephoneEstDeAbdelwahab) {
                    Spacer(modifier = Modifier.height(16.dp))

                    CeTelephoneEstDeAbdelwahabButtons(
                        coroutineScope = coroutineScope,
                        relatedClients = relatedClients,
                        viewModel = viewModel,
                        onDismiss = onDismiss,
                        context = context
                    )
                }

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

    // Edit Dialog
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
                            relatedClients?.apply {
                                nom = editedName
                                numTelephone = editedPhone
                            }

                            relatedClients?.let { client ->
                                viewModel.updateData(client)
                            }

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

    // Phone Dialog
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

    // Delete confirmation dialog
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
                                // Find and delete the corresponding B_ClientDataBase object
                                val relatedClient = viewModel.bProto_ClientsDataBase.find {
                                    it.id == client.id
                                }

                                // Delete the client from repository
                                relatedClient?.let {
                                    viewModel.deleteUnSeulData(it)
                                }

                                // Remove the marker from the map
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
