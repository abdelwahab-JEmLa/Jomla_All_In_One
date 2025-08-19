package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.TransactionItem
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun View_M14VentPeriod(
    viewModel: ViewModel_M14VentPeriod,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    relative_M14VentPeriode: M14VentPeriode,
    relative_M9AppCompt: Z_AppCompt?,
) {
    // State for showing delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    // State for editing fields
    var editingField by remember { mutableStateOf<String?>(null) }
    var editingValue by remember { mutableStateOf("") }

    // State for calculated achat totals
    var calculatedAchatTotal by remember { mutableStateOf(0.0) }
    var isLoadingCalculatedAchat by remember { mutableStateOf(true) }

    // Focus requester for auto-focus
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun M14VentPeriode.delete(): Unit {
        repositorysMainSetter.delete(this)
    }

    fun M14VentPeriode.handel_Clavie_Donne(): Unit {
        repositorysMainSetter.update_M14VentPeriode(this)
    }

    // Function to start editing a field
    fun startEditing(fieldName: String, currentValue: Double) {
        editingField = fieldName
        editingValue = currentValue.toString()
    }

    // Function to save edited value
    fun saveEditedValue() {
        val newValue = editingValue.toDoubleOrNull() ?: 0.0
        val updatedPeriode = when (editingField) {
            "credit_vents" -> relative_M14VentPeriode.copy(credit_Vents_Totale = newValue)
            "cash_vents" -> relative_M14VentPeriode.copy(cash_Vents_Totale = newValue)
            "credit_achats" -> relative_M14VentPeriode.copy(credit_achats_Totale = newValue)
            "cash_achats" -> relative_M14VentPeriode.copy(cash_achats_Totale = newValue)
            "credit_produits_depot" -> relative_M14VentPeriode.copy(credit_produitsAuDepot = newValue)
            "acheter_produits_depot" -> relative_M14VentPeriode.copy(acheter_produitsAuDepot = newValue)
            else -> relative_M14VentPeriode
        }
        updatedPeriode.handel_Clavie_Donne()
        editingField = null
        keyboardController?.hide()
    }

    LaunchedEffect(Unit) {
        loadCalculatedAchatTotals(relative_M14VentPeriode.keyID, repositorysMainGetter) { total ->
            calculatedAchatTotal = total
            isLoadingCalculatedAchat = false
        }
    }

    LaunchedEffect(editingField) {
        if (editingField != null) {
            focusRequester.requestFocus()
        }
    }

    val active_M14VentPeriode = relative_M9AppCompt?.current_OnVent_M14VentPeriode_KeyID
    val active = (active_M14VentPeriode ?: "") == relative_M14VentPeriode.keyID

    val backgroundColor = when {
        active -> Color.Red
        else -> MaterialTheme.colorScheme.surface
    }

    val heurDebutInString = "Now Test HH:mm"

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Êtes-vous sûr de vouloir supprimer cette période de vente ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        relative_M14VentPeriode.delete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (relative_M9AppCompt != null) {
                    aCentralFacade.repositorysMainSetter.update_M9AppCompt(
                        relative_M9AppCompt.copy(
                            current_OnVent_M14VentPeriode_KeyID = relative_M14VentPeriode.keyID,
                            current_OnVent_M14VentPeriode_DebugInfos = relative_M14VentPeriode.get_DebugInfos()
                        )
                    )
                }
            }
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(8.dp)
    ) {
        if (active) {
            Text(
                text = "Selected Periode",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "m14VentPeriode: ${relative_M14VentPeriode.get_DebugInfos()}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Delete button with icon
            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Text(
            text = "Heure de début: $heurDebutInString",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Calculate totals
        val totalVentes =
            relative_M14VentPeriode.credit_Vents_Totale + relative_M14VentPeriode.cash_Vents_Totale
        val totalAchats =
            relative_M14VentPeriode.credit_achats_Totale + relative_M14VentPeriode.cash_achats_Totale
        val totalProduitsDepot =
            relative_M14VentPeriode.credit_produitsAuDepot + relative_M14VentPeriode.acheter_produitsAuDepot
        val adjustedTotalAchats = totalAchats - totalProduitsDepot
        val balance = totalVentes - totalAchats + totalProduitsDepot


        val sum_Bon_Vents = run {
            // Obtenir les filtres actifs comme dans Dialog_Filter_Client
            val active_Central_Values = focusedValuesGetter.active_Central_Values
            val activePeriod = focusedValuesGetter.currentActiveFocuced_M14VentPeriode
            val activeGrossist = active_Central_Values.active_M15Grossist_AuFilterAchats
            val activeClient = active_Central_Values.active_M2Client_AuFilterAchats

            val allBonVents = repositorysMainGetter.repo8BonVent.datasValue
            val allVentOperations = repositorysMainGetter.repo10OperationVentCouleur.datasValue
            var allAchatOperations = repositorysMainGetter.repo11AchatOperation.datasValue

            // Appliquer la même logique de filtrage que Dialog_Filter_Client
            // 1. Filtrer les opérations d'achat par période active
            activePeriod.let { period ->
                if (period != null) {
                    allAchatOperations = allAchatOperations.filter {
                        it.parent_M14VentPeriod_KeyID == period.keyID
                    }
                }
            }

            // 2. Filtrer les opérations d'achat par grossiste actif
            activeGrossist?.let { grossist ->
                allAchatOperations = allAchatOperations.filter {
                    it.parent_M15Grossist_KeyID == grossist.keyID
                }
            }

            val relevantVentOperations = if (allAchatOperations.isNotEmpty()) {
                allVentOperations.filter { ventOperation ->
                    allAchatOperations.any { achatOperation ->
                        achatOperation.get_list_v_Depuit_joinedStringKeys(listOf(ventOperation))
                            .isNotEmpty()
                    }
                }
            } else {
                allVentOperations.filter {
                    it.parent_M14VentPeriod_KeyId == relative_M14VentPeriode.keyID
                }
            }

            // 4. Appliquer le filtre client si actif
            val filteredVentOperations = activeClient?.let { client ->
                val clientBonVentIds = allBonVents
                    .filter { it.parent_M2Client_KeyID == client.keyID }
                    .map { it.keyID }
                    .toSet()

                relevantVentOperations.filter {
                    it.parent_M8BonVent_KeyId in clientBonVentIds
                }
            } ?: relevantVentOperations

            filteredVentOperations.filter {
                it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
            }.sumOf { ventOperation ->
                val parentM13TarificationPrix = repositorysMainGetter
                    .find_M13Tarification_By_KeyID(ventOperation.parentM13TarificationKeyID)?.prixCurrency
                    ?: 0.0
                ventOperation.quantity * parentM13TarificationPrix
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.weight(2f),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "💰 VENTES (Manual)",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Credit Ventes
                            Column(modifier = Modifier.weight(1f)) {
                                if (editingField == "credit_vents") {
                                    OutlinedTextField(
                                        value = editingValue,
                                        onValueChange = { editingValue = it },
                                        label = { Text("Crédit") },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Decimal,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = { saveEditedValue() }
                                        ),
                                        modifier = Modifier.focusRequester(focusRequester)
                                    )
                                } else {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                startEditing(
                                                    "credit_vents",
                                                    relative_M14VentPeriode.credit_Vents_Totale
                                                )
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "💳 Crédit",
                                                fontSize = 12.sp,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                            Text(
                                                text = "${relative_M14VentPeriode.credit_Vents_Totale}",
                                                fontSize = 14.sp,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Cash Ventes
                            Column(modifier = Modifier.weight(1f)) {
                                if (editingField == "cash_vents") {
                                    OutlinedTextField(
                                        value = editingValue,
                                        onValueChange = { editingValue = it },
                                        label = { Text("Cash") },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Decimal,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = { saveEditedValue() }
                                        ),
                                        modifier = Modifier.focusRequester(focusRequester)
                                    )
                                } else {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                startEditing(
                                                    "cash_vents",
                                                    relative_M14VentPeriode.cash_Vents_Totale
                                                )
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "💵 Cash",
                                                fontSize = 12.sp,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                            Text(
                                                text = "${relative_M14VentPeriode.cash_Vents_Totale}",
                                                fontSize = 14.sp,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Text(
                            text = "Total: $totalVentes",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                // Calculated Ventes Card
                ElevatedCard(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📊 Calculated",
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ventes",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            modifier = Modifier.clickable {
                                focusedValuesGetter.update_activeCentralValues(
                                    focusedValuesGetter.active_Central_Values.copy(
                                        show_Dialog_filter_AChats_Par_Client_Acheteur = true,
                                        vent_Au_Dialog_filter_AChats_Par_Client_Acheteur = relative_M14VentPeriode
                                    )

                                )

                            },
                            text = "$sum_Bon_Vents",
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            // Achats Section - Row with Manual and Calculated Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Manual Achats Card
                ElevatedCard(
                    modifier = Modifier.weight(2f),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "🛒 ACHATS (Manual)",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Credit Achats
                            Column(modifier = Modifier.weight(1f)) {
                                if (editingField == "credit_achats") {
                                    OutlinedTextField(
                                        value = editingValue,
                                        onValueChange = { editingValue = it },
                                        label = { Text("Crédit") },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Decimal,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = { saveEditedValue() }
                                        ),
                                        modifier = Modifier.focusRequester(focusRequester)
                                    )
                                } else {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                startEditing(
                                                    "credit_achats",
                                                    relative_M14VentPeriode.credit_achats_Totale
                                                )
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "💳 Crédit",
                                                fontSize = 12.sp,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                            Text(
                                                text = "${relative_M14VentPeriode.credit_achats_Totale}",
                                                fontSize = 14.sp,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Cash Achats
                            Column(modifier = Modifier.weight(1f)) {
                                if (editingField == "cash_achats") {
                                    OutlinedTextField(
                                        value = editingValue,
                                        onValueChange = { editingValue = it },
                                        label = { Text("Cash") },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Decimal,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = { saveEditedValue() }
                                        ),
                                        modifier = Modifier.focusRequester(focusRequester)
                                    )
                                } else {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                startEditing(
                                                    "cash_achats",
                                                    relative_M14VentPeriode.cash_achats_Totale
                                                )
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "💵 Cash",
                                                fontSize = 12.sp,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                            Text(
                                                text = "${relative_M14VentPeriode.cash_achats_Totale}",
                                                fontSize = 14.sp,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Text(
                            text = "Total: $totalAchats",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📊 Calculated",
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Achats De period Vent ",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (isLoadingCalculatedAchat) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        } else {
                            Text(
                                text = String.format("%.2f", calculatedAchatTotal),
                                fontSize = 18.sp,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            // Produits au Dépôt Section - New Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "📦 PRODUITS AU DÉPÔT",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Credit Produits Dépôt
                        Column(modifier = Modifier.weight(1f)) {
                            if (editingField == "credit_produits_depot") {
                                OutlinedTextField(
                                    value = editingValue,
                                    onValueChange = { editingValue = it },
                                    label = { Text("Crédit") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = { saveEditedValue() }
                                    ),
                                    modifier = Modifier.focusRequester(focusRequester)
                                )
                            } else {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            startEditing(
                                                "credit_produits_depot",
                                                relative_M14VentPeriode.credit_produitsAuDepot
                                            )
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface.copy(
                                            alpha = 0.5f
                                        )
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "💳 Crédit",
                                            fontSize = 12.sp,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Text(
                                            text = "${relative_M14VentPeriode.acheter_produitsAuDepot}",
                                            fontSize = 14.sp,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: $totalProduitsDepot",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "(Réduit achats)",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Manual Balance Card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = when {
                            balance > 0 -> MaterialTheme.colorScheme.primaryContainer
                            balance < 0 -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surfaceContainer
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "⚖️ BALANCE",
                                    fontSize = 18.sp,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "(Manual)",
                                    fontSize = 12.sp,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = String.format("%.2f", balance),
                                fontSize = 22.sp,
                                style = MaterialTheme.typography.headlineSmall,
                                color = when {
                                    balance > 0 -> MaterialTheme.colorScheme.primary
                                    balance < 0 -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }

                        Text(
                            text = "Ventes ($totalVentes) - Achats ($totalAchats) + Dépôt ($totalProduitsDepot)",
                            fontSize = 11.sp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                // Calculated Balance Card - Now positioned below
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = when {
                            !isLoadingCalculatedAchat -> {
                                val calculatedBalance = sum_Bon_Vents - calculatedAchatTotal
                                when {
                                    calculatedBalance > 0 -> MaterialTheme.colorScheme.tertiaryContainer
                                    calculatedBalance < 0 -> MaterialTheme.colorScheme.errorContainer.copy(
                                        alpha = 0.8f
                                    )

                                    else -> MaterialTheme.colorScheme.surfaceContainer
                                }
                            }

                            else -> MaterialTheme.colorScheme.surfaceContainer
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "📊 BALANCE من غير تكاليف فائدة محسوبة ",
                                    fontSize = 18.sp,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "(Calculated)",
                                    fontSize = 12.sp,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            if (isLoadingCalculatedAchat) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = "Calcul...",
                                        fontSize = 10.sp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            } else {
                                val calculatedBalance = sum_Bon_Vents - calculatedAchatTotal
                                Text(
                                    text = String.format("%.2f", calculatedBalance),
                                    fontSize = 22.sp,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = when {
                                        calculatedBalance > 0 -> MaterialTheme.colorScheme.tertiary
                                        calculatedBalance < 0 -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }

                        if (!isLoadingCalculatedAchat) {
                            // Show calculated breakdown
                            Text(
                                text = "Ventes calc. (${
                                    String.format(
                                        "%.2f",
                                        sum_Bon_Vents
                                    )
                                }) - Achats calc. (${String.format("%.2f", calculatedAchatTotal)})",
                                fontSize = 10.sp,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to check if a grossist has operations in a specific period
private fun isGrossistActiveInPeriod(
    grossistKeyID: String,
    ventPeriodKeyID: String,
    repositorysMainGetter: RepositorysMainGetter
): Boolean {
    return repositorysMainGetter.repo11AchatOperation.datasValue.any { achatOperation ->
        achatOperation.parent_M15Grossist_KeyID == grossistKeyID &&
                achatOperation.parent_M14VentPeriod_KeyID == ventPeriodKeyID
    }
}

// Updated loadCalculatedAchatTotals function with timestamp filtering
private fun loadCalculatedAchatTotals(
    ventPeriodKeyID: String,
    repositorysMainGetter: RepositorysMainGetter,
    onTotalLoaded: (Double) -> Unit
) {
    var totalCredits = 0.0
    var totalVersements = 0.0
    var completedQueries = 0
    val totalQueries = 2

    // Find the current and next vent periods to determine timestamp range
    val currentPeriod = repositorysMainGetter.repo14VentPeriode.datasValue
        .firstOrNull { it.keyID == ventPeriodKeyID }

    if (currentPeriod == null) {
        onTotalLoaded(0.0)
        return
    }

    // Get all periods for the same parent, sorted by creation timestamp
    val allPeriodsForSameParent = repositorysMainGetter.repo14VentPeriode.datasValue
        .filter { it.parent_M9AppCompt_KeyID == currentPeriod.parent_M9AppCompt_KeyID }
        .sortedBy { it.creationTimestamp }

    // Find the timestamp range for this period
    val currentPeriodIndex = allPeriodsForSameParent.indexOfFirst { it.keyID == ventPeriodKeyID }
    val periodStartTimestamp = currentPeriod.creationTimestamp
    val periodEndTimestamp = if (currentPeriodIndex < allPeriodsForSameParent.size - 1) {
        // Not the last period, use next period's start timestamp
        allPeriodsForSameParent[currentPeriodIndex + 1].creationTimestamp
    } else {
        // Last period, use current timestamp
        System.currentTimeMillis()
    }

    fun checkComplete() {
        completedQueries++
        if (completedQueries == totalQueries) {
            onTotalLoaded(totalCredits - totalVersements)
        }
    }

    // Helper function to check if a timestamp is within the period range
    fun isTimestampInPeriod(timestamp: Long): Boolean {
        return timestamp >= periodStartTimestamp && timestamp < periodEndTimestamp
    }

    // Load TransactionItems filtered by timestamp and active grossists
    val transactionListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            totalCredits = 0.0
            for (child in snapshot.children) {
                try {
                    val transaction = child.getValue(TransactionItem::class.java)
                    transaction?.let {
                        // Filter by timestamp first, then check if grossist is active in this period
                        if (isTimestampInPeriod(it.timestamp) &&
                            isGrossistActiveInPeriod(
                                it.parent_GrossistKeyID,
                                ventPeriodKeyID,
                                repositorysMainGetter
                            )
                        ) {
                            totalCredits += it.credit
                        }
                    }
                } catch (e: Exception) {
                    // Handle parsing error silently
                }
            }
            checkComplete()
        }

        override fun onCancelled(error: DatabaseError) {
            checkComplete()
        }
    }

    TransactionItem.ref.addListenerForSingleValueEvent(transactionListener)

    // Load VersementItems filtered by timestamp and active grossists
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val versementRef = TransactionItem.ref.parent?.child("VersementItem")
            val versementListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    totalVersements = 0.0
                    for (child in snapshot.children) {
                        try {
                            val versement = child.child("versement").getValue(Double::class.java)
                            val grossistKeyID =
                                child.child("parent_GrossistKeyID").getValue(String::class.java)
                            val timestamp =
                                child.child("timestamp").getValue(Long::class.java) ?: 0L

                            if (versement != null && grossistKeyID != null) {
                                // Filter by timestamp first, then check if grossist is active in this period
                                if (isTimestampInPeriod(timestamp) &&
                                    isGrossistActiveInPeriod(
                                        grossistKeyID,
                                        ventPeriodKeyID,
                                        repositorysMainGetter
                                    )
                                ) {
                                    totalVersements += versement
                                }
                            }
                        } catch (e: Exception) {
                            // Handle parsing error silently
                        }
                    }
                    checkComplete()
                }

                override fun onCancelled(error: DatabaseError) {
                    checkComplete()
                }
            }

            versementRef?.addListenerForSingleValueEvent(versementListener)
        } catch (e: Exception) {
            // If VersementItem loading fails, just complete with transaction total
            checkComplete()
        }
    }
}

// Alternative approach: If you want to create a more robust timestamp filtering system
// You can add this helper function to get precise period boundaries:

private fun getPeriodTimestampRange(
    ventPeriodKeyID: String,
    repositorysMainGetter: RepositorysMainGetter
): Pair<Long, Long>? {
    val currentPeriod = repositorysMainGetter.repo14VentPeriode.datasValue
        .firstOrNull { it.keyID == ventPeriodKeyID } ?: return null

    // Get all periods for the same parent, sorted by creation timestamp
    val allPeriodsForSameParent = repositorysMainGetter.repo14VentPeriode.datasValue
        .filter { it.parent_M9AppCompt_KeyID == currentPeriod.parent_M9AppCompt_KeyID }
        .sortedBy { it.creationTimestamp }

    val currentPeriodIndex = allPeriodsForSameParent.indexOfFirst { it.keyID == ventPeriodKeyID }
    if (currentPeriodIndex == -1) return null

    val startTimestamp = currentPeriod.creationTimestamp
    val endTimestamp = if (currentPeriodIndex < allPeriodsForSameParent.size - 1) {
        allPeriodsForSameParent[currentPeriodIndex + 1].creationTimestamp
    } else {
        System.currentTimeMillis()
    }

    return Pair(startTimestamp, endTimestamp)
}
