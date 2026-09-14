package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.functions_central.runtime_throw_Erreur_Pour_Regle_Le_Real_Bug
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ClientEdites(
    viewModel: MapClientsViewModel,
    focusedValuesSetter: FocusedValuesSetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter,
    repo2Client: Repo2Client = viewModel.aCentralFacade.repositorysMainGetter.repo2Client,
    relative_Client: M2Client?,
    onDismiss: () -> Unit,
    onClickToEditeMarquerPosition: (M2Client) -> Unit,
    onShowDeleteConfirmationChange: (Boolean) -> Unit = {},
    onClientTypeModeChange: (M2Client.ClientTypeMode?) -> Unit = {},
    onShowEditDialogChange: (Boolean) -> Unit = {},
    onShowPhoneDialogChange: (Boolean) -> Unit = {},
    onTriggerCreditCapture: () -> Unit = {},
    isEditMode: Boolean = false,
) {

    val context = LocalContext.current
    val clientTypeMode = relative_Client?.clientTypeMode
    // Slot worker (2..6) en cours d'édition dans le dialog ci-dessous, ou
    // null si aucun dialog n'est ouvert. On garde juste l'index : le
    // nom/numéro affichés dans le dialog sont relus depuis relative_Client
    // à chaque ouverture, donc si le slot était vide les champs partent
    // bien de "" ; s'il avait déjà un nom/numéro, ils sont préremplis avec
    // l'ancienne valeur pour permettre de la modifier sans la retaper.
    var editingWorkerIdx by remember { mutableStateOf<Int?>(null) }
    val hasPhoneNumber = !relative_Client?.numTelephone.isNullOrEmpty() &&
            relative_Client?.numTelephone != "null"
    val hasValidLocation = relative_Client?.latitude != null &&
            relative_Client?.latitude != 0.0 &&
            relative_Client?.longitude != null
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Delete Icon
        item {
            Card(
                modifier = Modifier
                    .background(color = Color.Red)
                    .clickable {
                        onShowDeleteConfirmationChange(true)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete client",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // WhatsApp Send Credit Items Icon - visible if phone number exists
        if (hasPhoneNumber) {
            item {
                Card(
                    modifier = Modifier
                        .clickable { onTriggerCreditCapture() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Send credit items via WhatsApp",
                        tint = Color(0xFF25D366),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Phone Call Icon - only visible if phone number exists
        if (hasPhoneNumber) {
            item {
                Card(
                    modifier = Modifier
                        .clickable {
                            val phoneNumber = relative_Client?.numTelephone ?: ""
                            try {
                                // Try Truecaller first
                                val truecallerIntent = Intent(
                                    Intent.ACTION_DIAL,
                                    Uri.fromParts("tel", phoneNumber, null)
                                ).apply {
                                    setPackage("com.truecaller")
                                }

                                val packageManager = context.packageManager
                                val isTruecallerInstalled = truecallerIntent.resolveActivity(packageManager) != null

                                if (isTruecallerInstalled) {
                                    context.startActivity(truecallerIntent)
                                    Toast.makeText(
                                        context,
                                        "Appel vers ${relative_Client?.nom} via Truecaller",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // Fallback to default dialer
                                    val defaultDialerIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:$phoneNumber")
                                    }
                                    context.startActivity(defaultDialerIntent)
                                    Toast.makeText(
                                        context,
                                        "Appel vers ${relative_Client?.nom}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Impossible de lancer l'appel",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call client",
                        tint = Color(0xFF4CAF50), // Green color for call
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Navigation Icon - only visible if GPS coordinates exist
        if (hasValidLocation) {
            item {
                Card(
                    modifier = Modifier
                        .clickable {
                            val latitude = relative_Client?.latitude ?: 0.0
                            val longitude = relative_Client?.longitude ?: 0.0
                            val clientName = relative_Client?.nom ?: ""

                            try {
                                // Try Google Maps first
                                val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }

                                context.startActivity(mapIntent)
                                Toast.makeText(
                                    context,
                                    "Navigation vers $clientName",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                // Fallback to generic geo intent
                                try {
                                    val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($clientName)")
                                    val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
                                    context.startActivity(fallbackIntent)
                                } catch (e2: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Aucune application de navigation disponible",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Navigate to client",
                        tint = Color(0xFF2196F3), // Blue color for navigation
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Edit Location Icon
        item {
            Card(
                modifier = Modifier
                    .clickable {
                        relative_Client?.let { onClickToEditeMarquerPosition(relative_Client) }
                        onDismiss()
                        viewModel.clear_UiState_MarkerStatusDialog_Active_M2Client()
                        focusedValuesSetter.desactive_CurrentApp_ActiveOnCourDeVent_M8BonVent()
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Edit location",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Client Type Mode Toggle Icon
        item {
            Card(
                modifier = Modifier
                    .clickable {
                        val newClientTypeMode = when (clientTypeMode) {
                            M2Client.ClientTypeMode.ANCIEN -> M2Client.ClientTypeMode.NEVEAU
                            M2Client.ClientTypeMode.NEVEAU -> M2Client.ClientTypeMode.EVITE
                            M2Client.ClientTypeMode.EVITE -> M2Client.ClientTypeMode.ANCIEN
                            null -> M2Client.ClientTypeMode.NEVEAU
                        }

                        // Update the client's type mode
                        relative_Client?.let { client ->
                            client.clientTypeMode = newClientTypeMode
                            viewModel.updateData(client)
                        }

                        onClientTypeModeChange(newClientTypeMode)
                    }
            ) {
                clientTypeMode?.let {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = "Toggle Client Type",
                        tint = it.color,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // 5 boutons pour changer le statut crédit du client : Client/Fournisseur
        // x court/long terme, plus le toggle "Client de Jamale". Mêmes flags que
        // ceux posés par Set_Client_Court_Terme / Set_Fournisseur_Court_Terme /
        // Toggle_Client_De_Jamale dans A_B_MarkersHandler.kt, pour que le statut
        // posé ici depuis la fiche client corresponde exactement aux filtres
        // crédit de la carte.
        if (relative_Client != null) {
            item {
                Card(
                    modifier = Modifier
                        .background(
                            color = if (!relative_Client.its_Fournisseur_Grossisst_A_Jomla && !relative_Client.ces_credits_son_a_long_term)
                                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                        .clickable {
                            val updated = relative_Client.copy(
                                its_Fournisseur_Grossisst_A_Jomla = false,
                                ces_credits_son_a_long_term = false,
                            )
                            viewModel.updateData(updated)
                            Toast.makeText(context, "${relative_Client.nom} : Client, crédit court terme", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Text(
                        text = "Client\ncourt terme",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .background(
                            color = if (!relative_Client.its_Fournisseur_Grossisst_A_Jomla && relative_Client.ces_credits_son_a_long_term)
                                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                        .clickable {
                            val updated = relative_Client.copy(
                                its_Fournisseur_Grossisst_A_Jomla = false,
                                ces_credits_son_a_long_term = true,
                            )
                            viewModel.updateData(updated)
                            Toast.makeText(context, "${relative_Client.nom} : Client, crédit long terme", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Text(
                        text = "Client\nlong terme",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            item {
                // Le highlight de ce bouton (et des 3 autres boutons de statut
                // crédit) dépend de relative_Client, qui vient de
                // uiState.markerStatusDialogActiveM2Client. Ce champ ne se
                // rafraîchissait pas après un clic (viewModel.updateData ne
                // touchait que b_ClientInfosProtoJuin3List), donc le highlight
                // restait sur l'ancien statut même une fois le nouveau bien
                // enregistré — corrigé dans MapClientsViewModel.updateData.
                Card(
                    modifier = Modifier
                        .background(
                            color = if (relative_Client.its_Fournisseur_Grossisst_A_Jomla && !relative_Client.ces_credits_son_a_long_term)
                                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                        .clickable {
                            val updated = relative_Client.copy(
                                its_Fournisseur_Grossisst_A_Jomla = true,
                                ces_credits_son_a_long_term = false,
                            )
                            viewModel.updateData(updated)
                            Toast.makeText(context, "${relative_Client.nom} : Fournisseur/Grossiste, crédit court terme", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Text(
                        text = "Fournisseur\ncourt terme",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .background(
                            color = if (relative_Client.its_Fournisseur_Grossisst_A_Jomla && relative_Client.ces_credits_son_a_long_term)
                                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                        .clickable {
                            val updated = relative_Client.copy(
                                its_Fournisseur_Grossisst_A_Jomla = true,
                                ces_credits_son_a_long_term = true,
                            )
                            viewModel.updateData(updated)
                            Toast.makeText(context, "${relative_Client.nom} : Fournisseur/Grossiste, crédit long terme", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Text(
                        text = "Fournisseur\nlong terme",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .background(
                            color = if (relative_Client.its_Client_De_Jamale)
                                MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
                        )
                        .clickable {
                            val newStatut = !relative_Client.its_Client_De_Jamale
                            val updated = relative_Client.copy(its_Client_De_Jamale = newStatut)
                            viewModel.updateData(updated)
                            Toast.makeText(
                                context,
                                "${relative_Client.nom} : ${if (newStatut) "Client de Jamale activé" else "Client de Jamale désactivé"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                ) {
                    Text(
                        text = "Client\nJamale",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .clickable { onShowEditDialogChange(true) }
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                modifier = Modifier.getSemanticsTag(relative_Client, ""),
                text = relative_Client?.nom ?: runtime_throw_Erreur_Pour_Regle_Le_Real_Bug("relative_Client?.nom"),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (hasPhoneNumber) {
                Text(
                    text = relative_Client.numTelephone ?: "",
                    modifier = Modifier.clickable { onShowPhoneDialogChange(true) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (relative_Client != null) {
                // Liste des workers 2..6, avec surlignage de celui pointé par
                // active_worker_actullement_idx (index 1 = worker principal /
                // numTelephone déjà affiché ci-dessus, 2..6 = nom_worker_N/telep_worker_N).
                //
                // En mode édition (isEditMode = true) on affiche les 6 slots,
                // y compris ceux qui sont encore vides, pour permettre d'en
                // ajouter un nouveau (icône "+") ou de modifier un slot déjà
                // rempli (icône crayon) via un dialog d'édition. Hors édition on
                // ne garde que le comportement historique : seuls les workers
                // déjà renseignés sont listés, en lecture seule + toggle actif.
                val allWorkerSlots = listOf(
                    2 to (relative_Client.nom_worker_2 to relative_Client.telep_worker_2),
                    3 to (relative_Client.nom_worker_3 to relative_Client.telep_worker_3),
                    4 to (relative_Client.nom_worker_4 to relative_Client.telep_worker_4),
                    5 to (relative_Client.nom_worker_5 to relative_Client.telep_worker_5),
                    6 to (relative_Client.nom_worker_6 to relative_Client.telep_worker_6),
                )
                val workers = if (isEditMode) {
                    allWorkerSlots
                } else {
                    allWorkerSlots.filter { (_, pair) -> pair.first.isNotBlank() || pair.second.isNotBlank() }
                }

                if (workers.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        workers.forEach { (workerIdx, nomTelep) ->
                            val (nomWorker, telepWorker) = nomTelep
                            val isActive = relative_Client.active_worker_actullement_idx == workerIdx
                            val isEmptySlot = nomWorker.isBlank() && telepWorker.isBlank()

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isActive)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            Color.Transparent
                                    )
                                    .clickable {
                                        if (isEditMode && isEmptySlot) {
                                            // Slot vide en mode édition : on ouvre
                                            // directement l'édition pour l'ajouter,
                                            // plutôt que de l'activer (il n'y a rien
                                            // à activer tant qu'il est vide).
                                            editingWorkerIdx = workerIdx
                                        } else if (!isEmptySlot) {
                                            // Toggle : si ce worker est déjà actif,
                                            // on le désactive en revenant au
                                            // principal (idx 0 / numTelephone).
                                            // Sinon on l'active.
                                            val newIdx = if (isActive) 0 else workerIdx
                                            val updated = relative_Client.copy(
                                                active_worker_actullement_idx = newIdx
                                            )
                                            viewModel.updateData(updated)
                                        }
                                    }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isEditMode && isEmptySlot) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add worker $workerIdx",
                                        modifier = Modifier.padding(end = 2.dp)
                                    )
                                    Text(
                                        text = "Ajouter worker $workerIdx",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        text = nomWorker.ifBlank { "Worker $workerIdx" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (isEditMode) {
                                        // Bouton d'activation séparé du reste de la
                                        // Row : en mode édition, tap ici pour
                                        // choisir ce worker comme
                                        // active_worker_actullement_idx. Vert
                                        // quand c'est déjà le choix actif, gris
                                        // sinon — indépendant du clic sur le nom
                                        // ou le numéro juste à côté.
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Activate worker $workerIdx",
                                            tint = if (isActive) Color(0xFF4CAF50) else Color.Gray,
                                            modifier = Modifier
                                                .padding(horizontal = 2.dp)
                                                .clickable {
                                                    // Toggle : re-tap sur un worker déjà
                                                    // actif désactive et revient au
                                                    // principal (idx 0 / numTelephone).
                                                    val newIdx = if (isActive) 0 else workerIdx
                                                    val updated = relative_Client.copy(
                                                        active_worker_actullement_idx = newIdx
                                                    )
                                                    viewModel.updateData(updated)
                                                }
                                        )
                                    }
                                    if (telepWorker.isNotBlank()) {
                                        Text(
                                            text = telepWorker,
                                            modifier = Modifier.clickable { onShowPhoneDialogChange(true) },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (isEditMode) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit worker $workerIdx",
                                            modifier = Modifier
                                                .padding(start = 4.dp)
                                                .clickable { editingWorkerIdx = workerIdx }
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

    // Dialog d'ajout/édition d'un worker (2..6). Les champs sont
    // initialisés avec l'ancien nom/numéro du slot (relevés depuis
    // relative_Client), ou "" si le slot était vide — donc "créer" un
    // worker vide revient juste à ouvrir ce dialog avec des champs vides,
    // et "éditer" un worker existant l'ouvre avec l'ancien nom/numéro déjà
    // en place pour pouvoir les modifier.
    val workerIdxBeingEdited = editingWorkerIdx
    if (workerIdxBeingEdited != null && relative_Client != null) {
        val (initialNom, initialTelep) = when (workerIdxBeingEdited) {
            2 -> relative_Client.nom_worker_2 to relative_Client.telep_worker_2
            3 -> relative_Client.nom_worker_3 to relative_Client.telep_worker_3
            4 -> relative_Client.nom_worker_4 to relative_Client.telep_worker_4
            5 -> relative_Client.nom_worker_5 to relative_Client.telep_worker_5
            6 -> relative_Client.nom_worker_6 to relative_Client.telep_worker_6
            else -> "" to ""
        }
        var nomWorkerInput by remember(workerIdxBeingEdited) { mutableStateOf(initialNom) }
        var telepWorkerInput by remember(workerIdxBeingEdited) { mutableStateOf(initialTelep) }

        AlertDialog(
            onDismissRequest = { editingWorkerIdx = null },
            title = { Text(text = "Worker $workerIdxBeingEdited") },
            text = {
                Column {
                    OutlinedTextField(
                        value = nomWorkerInput,
                        onValueChange = { nomWorkerInput = it },
                        label = { Text("Nom") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = telepWorkerInput,
                        onValueChange = { telepWorkerInput = it },
                        label = { Text("Téléphone") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val updated = when (workerIdxBeingEdited) {
                        2 -> relative_Client.copy(nom_worker_2 = nomWorkerInput, telep_worker_2 = telepWorkerInput)
                        3 -> relative_Client.copy(nom_worker_3 = nomWorkerInput, telep_worker_3 = telepWorkerInput)
                        4 -> relative_Client.copy(nom_worker_4 = nomWorkerInput, telep_worker_4 = telepWorkerInput)
                        5 -> relative_Client.copy(nom_worker_5 = nomWorkerInput, telep_worker_5 = telepWorkerInput)
                        6 -> relative_Client.copy(nom_worker_6 = nomWorkerInput, telep_worker_6 = telepWorkerInput)
                        else -> relative_Client
                    }
                    viewModel.updateData(updated)
                    editingWorkerIdx = null
                }) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingWorkerIdx = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}
