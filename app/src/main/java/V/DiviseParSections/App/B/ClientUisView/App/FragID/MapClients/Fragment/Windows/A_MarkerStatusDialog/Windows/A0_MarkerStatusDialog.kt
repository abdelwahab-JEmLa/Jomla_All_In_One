package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.ButtonAddVocale.ButtonAjouteRecordVoiceHistoriqueC3_BonAchate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
    clientOuCaMarqueGpsEstOuvert: HClientInfos?,
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
    var showExitConfirmationDialog by remember { mutableStateOf(false) }

    val clientId = clientOuCaMarqueGpsEstOuvert?.id ?: 0L
    var clientTypeMode by remember { mutableStateOf(clientOuCaMarqueGpsEstOuvert?.clientTypeMode) }

    if (editedName.isEmpty() && clientOuCaMarqueGpsEstOuvert != null) {
        editedName = clientOuCaMarqueGpsEstOuvert.nom ?: ""
        editedPhone = clientOuCaMarqueGpsEstOuvert.numTelephone ?: ""
    }

    fun handleDismiss() {
        showExitConfirmationDialog = true
    }


    Dialog(
        onDismissRequest = { handleDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        val markerStatusDialogActiveM2Client = uiState.markerStatusDialogActiveM2Client

        Box(
            modifier = Modifier
                .getSemanticsTag(markerStatusDialogActiveM2Client?.nom?:"","markerStatusDialogActiveM2Client")
                .fillMaxSize()
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
                            onDismiss = {  },
                            onClickToEditeMarquerPosition = onClickToEditeMarquerPosition,
                            onShowDeleteConfirmationChange = { showDeleteConfirmationDialog = it },
                            onClientTypeModeChange = { clientTypeMode = it },
                            onShowEditDialogChange = { showEditDialog = it },
                            onShowPhoneDialogChange = { showPhoneDialog = it },
                        )
                    }
                    val activeCompt = viewModel.getter.repo9AppCompt.currentAppCompt
                    val activeClient = viewModel.aCentralFacade.focusedActiveValuesFacade.get.activeOnVentM2ClientInfos

                    activeCompt?.let {
                        if (activeClient != null) {
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
                                        M8BonVent.EtateActuellementEst.AVEC_MARCHANDISE
                                            .ButtonAutreEtates(
                                                viewModel = viewModel,
                                                clickedClient = clientId,
                                            )
                                    }

                                    item {
                                        CommandButton(
                                            m2Client=clientOuCaMarqueGpsEstOuvert!!,
                                            modifier = Modifier.height(60.dp),
                                            viewModel = viewModel,
                                            newEtate = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                                            context = context,
                                            onUpdateLongAppSetting = onUpdateLongAppSetting
                                        )
                                    }

                                    item {
                                        M8BonVent.EtateActuellementEst.FERME
                                            .ButtonAutreEtates(
                                                viewModel = viewModel,
                                                clickedClient = clientId,
                                            )
                                    }
                                    item {
                                        M8BonVent.EtateActuellementEst.ACHETEUR_NON_DISPO
                                            .ButtonAutreEtates(
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
                                        M8BonVent.EtateActuellementEst.Cible
                                            .ButtonAutreEtates(
                                                viewModel = viewModel,
                                                clickedClient = clientId,
                                            )
                                    }

                                    activeCompt?.let { activeCompt ->
                                        if (activeCompt.vid == 2L) {
                                            item {
                                                M8BonVent.EtateActuellementEst.CIBLE_POUR_2
                                                    .ButtonAutreEtates(
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

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient(
                            markerStatusDialogM2Client= clientOuCaMarqueGpsEstOuvert,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    handleDismiss()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(56.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "إغلاق",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (showExitConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showExitConfirmationDialog = false },
                title = { Text("تأكيد الخروج") },
                text = { Text("العميل في وضع الطلب حاليا. هل أنت متأكد من أنك تريد إغلاق هذا الحوار؟") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitConfirmationDialog = false
                            viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                            viewModel.aCentralFacade.focusedActiveValuesFacade.set.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
                        }
                    ) {
                        Text("نعم")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showExitConfirmationDialog = false }
                    ) {
                        Text("إلغاء")
                    }
                }
            )
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
                                val relatedClient = viewModel.bProto_ClientsDataBase.find {
                                    it.id == client.id
                                }

                                relatedClient?.let {
                                    viewModel.deleteUnSeulData(it)
                                }

                                onRemoveMark(marqueClick)
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
