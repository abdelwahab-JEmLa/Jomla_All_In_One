package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.B.Dialogs

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model._1_3_BonAchat
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository
import Z_CodePartageEntreApps.Windows.B.Windows.UI.LoadingContent
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.ViewModel_App2FragID1
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.collectAsState
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
import org.koin.compose.koinInject
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MarkerStatusDialog(
    viewModel: ViewModel_App2FragID1,
    viewModelInitApp: ViewModelInitApp,
    selectedMarker: Marker?,
    onDismiss: () -> Unit,
    onUpdateLongAppSetting: () -> Unit = {},
    onClickToEditeMarquerPosition: (Long) -> Unit,
    onRemoveMark: (Marker?) -> Unit,
    _0_0_HeadOfRepositorys_Repository: _0_0_HeadOfRepositorys_Repository = koinInject(),
) {
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
    var clientTypeMode = relatedClients?.clientTypeMode

    val uiStateviewModelFragment_APP2_ID_1 by viewModelInitApp.viewModelFragment_APP2_ID_1.uiStateFlow.collectAsState()
    val isDataLoading = uiStateviewModelFragment_APP2_ID_1.isDataLoading

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
            if (isDataLoading) {
                // Show loading content when data is loading
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingContent(
                        message = "Loading client data...",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                // Show the normal content when data is loaded
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
                                        B_ClientDataBase.ClientTypeMode.ANCIEN -> B_ClientDataBase.ClientTypeMode.NEVEAU
                                        B_ClientDataBase.ClientTypeMode.NEVEAU -> B_ClientDataBase.ClientTypeMode.EVITE
                                        B_ClientDataBase.ClientTypeMode.EVITE -> B_ClientDataBase.ClientTypeMode.ANCIEN
                                        null -> B_ClientDataBase.ClientTypeMode.NEVEAU
                                    }

                                    // Update the client's type mode
                                    relatedClients?.let { client ->
                                        client.clientTypeMode = clientTypeMode!!
                                        viewModel.updateData(client)
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

                    Card(
                        modifier = Modifier
                            .clickable { showEditDialog = true }
                            .fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = selectedMarker.title ?: "Client",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            if (!relatedClients?.numTelephone.isNullOrEmpty()) {
                                Text(
                                    text = relatedClients?.numTelephone ?: "",
                                    modifier = Modifier.clickable { showPhoneDialog = true },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }


                    StatusButton(
                        text = "Mode Commande",
                        icon = Icons.Default.ShoppingCart,
                        color = Color(
                            ContextCompat.getColor(
                                context,
                                B_ClientDataBase.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT.color
                            )
                        ),
                        onClick = {
                            coroutineScope.launch {
                                val repositorysModel =
                                    _0_0_HeadOfRepositorys_Repository.repositorys_Model

                                val currentVendeur = _0_0_HeadOfRepositorys_Repository.currentVendeur
                                val activePeriod = _0_0_HeadOfRepositorys_Repository.activePeriod

// Log the current state to diagnose the issue
                                Log.d("MarkerStatusDialog", "Current vendeur: $currentVendeur")
                                Log.d("MarkerStatusDialog", "Active period: $activePeriod")
                                Log.d("MarkerStatusDialog", "Periods available: ${_0_0_HeadOfRepositorys_Repository.repositorys_Model._1_4_PeriodeVent_Repository.modelDatasSnapList.size}")

// Check if there's a valid period or create one if needed
                                val parentVidPeriode = if (activePeriod != null) {
                                    Log.d("MarkerStatusDialog", "Using existing period with VID: ${activePeriod.vid}")
                                    activePeriod.vid
                                } else {
                                    Log.w("MarkerStatusDialog", "No active period found. This may be because currentVendeur is null or no matching period exists.")

                                    // You could either:
                                    // 1. Create a new period (if that's appropriate for your application)
                                    // 2. Use a fallback period VID
                                    // 3. Show an error message to the user

                                    // For now, returning 0L but you should replace with appropriate logic
                                    0L
                                }

// Then use this parentVidPeriode in your BonAchat creation:
                                repositorysModel._1_3_BonAchat_Repository.addDataAndReturneItVID(
                                    _1_3_BonAchat(
                                        clientAcheteurID = relatedClients?.id ?: 0L,
                                        parentVID_1_4_PeriodeVent = parentVidPeriode
                                    )
                                )


                                val selectedMarkerid = selectedMarker.id.toLong()
                                viewModel.updateLongAppSetting(selectedMarkerid)
                                val maxVid =
                                    uiStateviewModelFragment_APP2_ID_1._1_3_BonAchatList.maxOfOrNull { it.vid }
                                        ?: 0
                                Log.d(
                                    "MarkerStatusDialog",
                                    "Current max vid: $maxVid, list size: ${uiStateviewModelFragment_APP2_ID_1._1_3_BonAchatList.size}"
                                )
                                val newVid = maxVid + 1

                                val newData = _1_3_BonAchat(
                                    vid = newVid,
                                    clientAcheteurID = relatedClients?.id!!,
                                    // parent_1_4_PeriodeVentVid = uiStateviewModelFragment_APP2_ID_1._1_4_PeriodeVentList.maxOf { it.vid },
                                    heurDebutInString = SimpleDateFormat(
                                        "HH:mm",
                                        Locale.getDefault()
                                    ).format(Date())
                                )

                                viewModelInitApp.viewModelFragment_APP2_ID_1.addData_1_3_BonAchat_Repository(
                                    newData
                                )

                                onUpdateLongAppSetting()
                                onDismiss()
                            }
                        }
                    )

                    val CLIENT_ABSENT =
                        B_ClientDataBase.DernierEtatAAffiche.CLIENT_ABSENT

                    StatusButton(
                        text = CLIENT_ABSENT.nomArabe,
                        icon = Icons.Default.Person,
                        color = Color(
                            ContextCompat.getColor(
                                context,
                                CLIENT_ABSENT.color
                            )
                        ),
                        onClick = {
                            coroutineScope.launch {
                                Log.d(
                                    "MarkerStatusDialog",
                                    "Setting client ${selectedMarker?.id} to state: CLIENT_ABSENT"
                                )
                                relatedClients?.actuelleEtat = CLIENT_ABSENT
                                viewModel.updateData(relatedClients!!)
                                onDismiss()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val AVEC_MARCHANDISE =
                        B_ClientDataBase.DernierEtatAAffiche.AVEC_MARCHANDISE
                    StatusButton(
                        text = AVEC_MARCHANDISE.nomArabe,
                        icon = Icons.Default.Person,
                        color = Color(
                            ContextCompat.getColor(
                                context,
                                AVEC_MARCHANDISE.color
                            )
                        ),
                        onClick = {
                            coroutineScope.launch {
                                relatedClients?.actuelleEtat = AVEC_MARCHANDISE
                                viewModel.updateData(relatedClients!!)
                                onDismiss()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val FERME =
                        B_ClientDataBase.DernierEtatAAffiche.FERME
                    StatusButton(
                        text = FERME.nomArabe,
                        icon = Icons.Default.Person,
                        color = Color(
                            ContextCompat.getColor(
                                context,
                                FERME.color
                            )
                        ),
                        onClick = {
                            coroutineScope.launch {
                                relatedClients?.actuelleEtat = FERME
                                viewModel.updateData(relatedClients!!)
                                onDismiss()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val Cible = B_ClientDataBase.DernierEtatAAffiche.Cible
                    StatusButton(
                        text = Cible.nomArabe,
                        icon = Icons.Default.Person,
                        color = Color(
                            ContextCompat.getColor(
                                context,
                                Cible.color
                            )
                        ),
                        onClick = {
                            coroutineScope.launch {
                                relatedClients?.actuelleEtat = Cible
                                viewModel.updateData(relatedClients!!)

                                onDismiss()
                            }
                        }
                    )

                    val CIBLE_PRIORITE_2 =
                        B_ClientDataBase.DernierEtatAAffiche.CIBLE_PRIORITE_2

                    StatusButton(
                        text = CIBLE_PRIORITE_2.nomArabe,
                        icon = Icons.Default.Person,
                        color = Color(
                            ContextCompat.getColor(
                                context,
                                CIBLE_PRIORITE_2.color
                            )
                        ),
                        onClick = {
                            coroutineScope.launch {

                                relatedClients?.actuelleEtat = CIBLE_PRIORITE_2

                                viewModel.updateData(relatedClients!!)

                                onDismiss()
                            }
                        }
                    )


                    val CIBLE_POUR_2 =
                        B_ClientDataBase.DernierEtatAAffiche.CIBLE_POUR_2

                    StatusButton(
                        text = CIBLE_POUR_2.nomArabe,
                        icon = Icons.Default.Person,
                        color = Color(
                            ContextCompat.getColor(
                                context,
                                CIBLE_POUR_2.color
                            )
                        ),
                        onClick = {
                            coroutineScope.launch {

                                relatedClients?.actuelleEtat = CIBLE_POUR_2

                                viewModel.updateData(relatedClients!!)

                                onDismiss()
                            }
                        }
                    )


                    val A_EVITE =
                        B_ClientDataBase.DernierEtatAAffiche.A_EVITE

                    StatusButton(
                        text = A_EVITE.nomArabe,
                        icon = Icons.Default.Person,
                        color = Color(
                            ContextCompat.getColor(
                                context,
                                A_EVITE.color
                            )
                        ),
                        onClick = {
                            coroutineScope.launch {

                                relatedClients?.actuelleEtat = A_EVITE

                                viewModel.updateData(relatedClients!!)

                                onDismiss()
                            }
                        }
                    )
                }
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
                            relatedClients?.apply {
                                nom = editedName
                                numTelephone = editedPhone
                            }

                            viewModel.updateData(relatedClients!!)

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

@Composable
private fun StatusButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
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
