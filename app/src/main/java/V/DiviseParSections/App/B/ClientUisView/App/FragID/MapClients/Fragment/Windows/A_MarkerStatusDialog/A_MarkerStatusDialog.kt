package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View.A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
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
import androidx.compose.runtime.collectAsState
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
    _0_0_HeadSQLRepositorys: _0_0_HeadSQLRepositorys = koinInject(),
) {            //<--
    val ceTelephoneEstDeAbdelwahab = _0_0_HeadSQLRepositorys
        .repositorys_Model
        .activeIdDe_1_5_Vendeur == 2L
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val relatedClients = viewModel.bProto_ClientsDataBase.find {
        it.id == (selectedMarker?.id?.toLong() ?: 0)
    }
    var clientTypeMode by remember { mutableStateOf(relatedClients?.clientTypeMode) }
    val repositorysModel =
        _0_0_HeadSQLRepositorys.repositorys_Model

    val ceComptVendeurInsertBonsAchatAuPeriodID =
        repositorysModel.repository_1_5_Vendeur.modelDatasSnapList
            .find { it.vid == repositorysModel.activeIdDe_1_5_Vendeur }
            ?.ceComptVendeurInsertBonsAchatAuPeriodID

    val clientId = relatedClients?.id ?: 0L
    // Check if a BonAchat already exists for this client in the active period
    val existingBonAchat = viewModel.modelDatasSnapList_1_3_BonAchat.find {
        it.clientAcheteurID == clientId
                && it.parentVID_1_4_PeriodeVent == ceComptVendeurInsertBonsAchatAuPeriodID
    }

    val activeTransactionId by viewModel.repo_0_0_HeadSQLRepositorys.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState()

    // Initialize editedName and editedPhone with current values
    if (editedName.isEmpty() && relatedClients != null) {
        editedName = relatedClients.nom ?: ""
        editedPhone = relatedClients.numTelephone ?: ""
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
            // Replaced Column with LazyColumn for scrollable content
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // First show client info regardless of transaction status
                item {
                    ClientEdites(
                        onClickToEditeMarquerPosition = onClickToEditeMarquerPosition,
                        selectedMarker = selectedMarker,
                        onDismiss = onDismiss,
                        clientTypeMode = clientTypeMode,
                        relatedClients = relatedClients,
                        viewModel = viewModel,
                        onShowDeleteConfirmationChange = { showDeleteConfirmationDialog = it },
                        onClientTypeModeChange = { clientTypeMode = it },
                        onShowEditDialogChange = { showEditDialog = it },
                        onShowPhoneDialogChange = { showPhoneDialog = it },
                        coroutineScope = coroutineScope,
                        existingBonAchat = existingBonAchat,
                        repositorysModel = repositorysModel,
                        clientId = clientId,
                        ceComptVendeurInsertBonsAchatAuPeriodID = ceComptVendeurInsertBonsAchatAuPeriodID
                    )
                }

                // Inside the LazyColumn
                item {
                    AfficheurRegleOuvert(
                        repositorysModel = repositorysModel,
                        clientId = clientId,
                        activeTransactionId = activeTransactionId,
                        viewModel = viewModel,
                        relatedClients = relatedClients,
                        coroutineScope = coroutineScope,
                        context = context
                    )
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
                            // Use a LazyVerticalGrid with 2 columns
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
                                    _1_3_TransactionCommercial.EtateActuellementEst.AVEC_MARCHANDISE
                                        .Button(
                                            coroutineScope = coroutineScope,
                                            viewModel = viewModel,
                                            clientId = clientId,
                                            context = context
                                        )
                                }

                                item {
                                    CommandButton(
                                        modifier = Modifier.height(60.dp),
                                        coroutineScope = coroutineScope,
                                        clientId = clientId,
                                        selectedMarker = selectedMarker,
                                        viewModel = viewModel,
                                        onUpdateLongAppSetting = onUpdateLongAppSetting,
                                        onDismiss = onDismiss,
                                        context = context,
                                        etateActuellementEst1 = _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                                    )
                                }

                                item {
                                    _1_3_TransactionCommercial.EtateActuellementEst.FERME
                                        .Button(
                                            coroutineScope = coroutineScope,
                                            viewModel = viewModel,
                                            clientId = clientId,
                                            context = context
                                        )
                                }
                                item {
                                    _1_3_TransactionCommercial.EtateActuellementEst.ACHETEUR_NON_DISPO
                                        .Button(
                                            coroutineScope = coroutineScope,
                                            viewModel = viewModel,
                                            clientId = clientId,
                                            context = context
                                        )
                                }
                                if (ceTelephoneEstDeAbdelwahab) {
                                    item {
                                        _1_3_TransactionCommercial.EtateActuellementEst.Cible
                                            .Button(
                                                coroutineScope = coroutineScope,
                                                viewModel = viewModel,
                                                clientId = clientId,
                                                context = context
                                            )
                                    }
                                    item {
                                        _1_3_TransactionCommercial.EtateActuellementEst.CIBLE_POUR_2
                                            .Button(
                                                coroutineScope = coroutineScope,
                                                viewModel = viewModel,
                                                clientId = clientId,
                                                context = context
                                            )
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
}

