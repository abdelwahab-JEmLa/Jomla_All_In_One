package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.P.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiState
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.ButtonAutreEtates
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.CommandButton
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Bottons.View.get_Found_Or_Default_M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.A_Main_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.koinInject
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
private fun CustomStatusDropdownMenu(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    relative_M2Client: M2Client?,
) {
    val currentApp_ItsWorkChezGrossisst= focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        @Composable
        fun StatusDropdownItem(
            status: M8BonVent.EtateActuellementEst,
            text: String
        ) {
            DropdownMenuItem(
                text = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorResource(id = status.color)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = text,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                onClick = {
                    val foundOrDefaultResult = relative_M2Client?.let {
                        get_Found_Or_Default_M8BonVent(
                            aCentralFacade,
                            it,
                            etateActuellementEst = status,
                        )
                    }

                    val relative_M8BonVent =
                        foundOrDefaultResult?.found ?: foundOrDefaultResult?.default_If_No_Found

                    val new_M17MessageVocale = relative_M8BonVent?.let {
                        M17MessageVocale
                            .get_default()
                            .copy(
                                parent_M8BonVent_KeyID = it.keyID,
                                parent_M9AppCompt_DebugInfos = relative_M8BonVent.get_DebugInfos(),
                            )
                    }

                    val updatedBonVent = new_M17MessageVocale?.let {
                        relative_M8BonVent
                            .copy(
                                etateActuellementEst = status,
                                parent_M17Message_KeyID = it.keyID,
                                parent_M17Message_DebugInfos = new_M17MessageVocale.getDebugInfos(),
                            )
                    }

                    if (updatedBonVent != null) {
                        aCentralFacade.repositorysMainSetter.update_M8BonVent(updatedBonVent)
                    }

                    val updated_active_Central_Values =
                        focusedValuesGetter.active_Central_Values.copy(
                            active_OpnerDialog_M17MessageVocale = new_M17MessageVocale,
                        )

                    focusedValuesGetter.update_activeCentralValues(
                        updated_active_Central_Values
                    )
                    onDismissRequest()
                }
            )
        }



        StatusDropdownItem(
            status = M8BonVent.EtateActuellementEst.AVEC_MARCHANDISE,
            text = "عندو سلعة"
        )

        StatusDropdownItem(
            status = M8BonVent.EtateActuellementEst.ACHETEUR_NON_DISPO,
            text = "الشاري غائب"
        )

        StatusDropdownItem(
            status = M8BonVent.EtateActuellementEst.FERME,
            text = "مغلق"
        )
        // Added CommantaireSpeciale status item
        val statusCommantaire = M8BonVent.EtateActuellementEst.CommantaireSpeciale
        StatusDropdownItem(
            status = statusCommantaire,
            text = statusCommantaire.nomArabe
        )

        focusedValuesGetter.currentApp_Est_Admin.ifTrue {


            val status = M8BonVent.EtateActuellementEst.PASSE
            StatusDropdownItem(
                status = status,
                text = status.nomArabe
            )

            StatusDropdownItem(
                status = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                text = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT.nomArabe
            )

            StatusDropdownItem(
                status = M8BonVent.EtateActuellementEst.Ordre_Gerant,
                text = M8BonVent.EtateActuellementEst.Ordre_Gerant.nomArabe
            )

            StatusDropdownItem(
                status = M8BonVent.EtateActuellementEst.Credit,
                text = M8BonVent.EtateActuellementEst.Credit.name
            )

            StatusDropdownItem(
                status = M8BonVent.EtateActuellementEst.Versemment,
                text = M8BonVent.EtateActuellementEst.Versemment.name
            )

        }
    }
}

@Composable
fun MarkerStatusDialog(
    viewModel: MapClientsViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    relative_M8: M8BonVent? =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
            .activeOnVent_M8BonVent,
    relative_M2Client: M2Client?,
    mapView: MapView,
    uiState: UiState,
    onUpdateLongAppSetting: () -> Unit = {},
    onClickToEditeMarquerPosition: (M2Client) -> Unit,
    onRemoveMark: (Marker?) -> Unit,
    onPourEdite_Gps_Client: (M2Client) -> Unit = {},
) {
    var showCreditDialog by remember { mutableStateOf(false) }
    var currentCreditTransaction by remember { mutableStateOf<M8BonVent?>(null) }

    val marqueClick = mapView.overlays
        .filterIsInstance<Marker>()
        .find { marker ->
            marker.id == relative_M2Client?.id.toString()
        } ?: return

    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedPhone by remember { mutableStateOf("") }
    var showPhoneDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var showExitConfirmationDialog by remember { mutableStateOf(false) }
    var showStatusDropdown by remember { mutableStateOf(false) }

    val clientId = relative_M2Client?.id ?: 0L
    var clientTypeMode by remember { mutableStateOf(relative_M2Client?.clientTypeMode) }

    if (editedName.isEmpty() && relative_M2Client != null) {
        editedName = relative_M2Client.nom
        editedPhone = relative_M2Client.numTelephone
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
                .getSemanticsTag(
                    nomVal = "markerStatusDialogActiveM2Client",
                    data = markerStatusDialogActiveM2Client?.nom ?: ""
                )
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
                            relative_Client = relative_M2Client,
                            onDismiss = { },
                            onClickToEditeMarquerPosition = onClickToEditeMarquerPosition,
                            onShowDeleteConfirmationChange = { showDeleteConfirmationDialog = it },
                            onClientTypeModeChange = { clientTypeMode = it },
                            onShowEditDialogChange = { showEditDialog = it },
                            onShowPhoneDialogChange = { showPhoneDialog = it },
                        )
                    }
                    val activeCompt = viewModel.getter.repo9AppCompt.currentAppCompt
                    val activeClient =
                        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeOnVentM2ClientInfos

                    activeCompt?.let {
                        if (activeClient != null) {
                            item {
                                AfficheurRegleOuvert(
                                    uiState = uiState,
                                    viewModel = viewModel,
                                    relative_Client = relative_M2Client,
                                    onPourEdite_Gps_Client = {
                                        onPourEdite_Gps_Client(it)
                                    },
                                    extracted = { relative_M8BonVent ->
                                        currentCreditTransaction = relative_M8BonVent
                                    },
                                    extracted_2 = { showCreditDialog = true },
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
                                        CommandButton(
                                            relative_M2Client = relative_M2Client!!,
                                            modifier = Modifier,
                                            viewModel = viewModel,
                                            relative_Etate = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                                            context = context,
                                            onUpdateLongAppSetting = onUpdateLongAppSetting
                                        )
                                    }

                                    item {
                                        Box {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { showStatusDropdown = true },
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color.Red
                                                ),
                                                elevation = CardDefaults.cardElevation(
                                                    defaultElevation = 4.dp
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(12.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.DeveloperMode,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text = "تقرير الدخول معه في حالة انسداد في التجارة بسبب:",
                                                        color = Color.White,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        textAlign = TextAlign.Center,
                                                        lineHeight = 16.sp,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                            }

                                            CustomStatusDropdownMenu(
                                                expanded = showStatusDropdown,
                                                onDismissRequest = { showStatusDropdown = false },
                                                relative_M2Client = relative_M2Client,
                                            )
                                        }
                                    }

                                    item {
                                        M8BonVent.EtateActuellementEst.CommantaireSpeciale
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
                            relative_Client = relative_M2Client,
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
                    if (relative_M8 != null) {
                        showExitConfirmationDialog = true
                    } else {
                        viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
                    }
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
                            viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
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
                            label = { Text("Etate du client") },
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
                            relative_M2Client?.apply {
                                nom = editedName
                                numTelephone = editedPhone
                            }

                            relative_M2Client?.let { client ->
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

        if(!M18CentralParametresOfAllApps.get_Default().itsDevMode) {
            PressistatntMainActivityButtons_Sec8FWinID1()
        }
    }
}
