package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Modules.Ui.A.TransactionItem
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.filtersAndSorts_Central.calculateClientSalesSummary
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import java.util.Date

@Composable
fun View_M14VentPeriod(
    viewModel: ViewModel_M14VentPeriod,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    relative_M14VentPeriode: M14VentPeriode,
    relative_M9AppCompt: Z_AppCompt?,
    onCalculatedAchatClick: () -> Unit = {}
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
// Function to save edited value (Updated)
    fun saveEditedValue() {
        val newValue = editingValue.toDoubleOrNull() ?: 0.0
        val updatedPeriode = when (editingField) {
            "credit_vents" -> relative_M14VentPeriode.copy(credit_Vents_Totale = newValue)
            "cash_vents" -> relative_M14VentPeriode.copy(cash_Vents_Totale = newValue)
            "credit_achats" -> relative_M14VentPeriode.copy(credit_achats_Totale = newValue)
            "cash_achats" -> relative_M14VentPeriode.copy(cash_achats_Totale = newValue)
            "credit_produits_depot" -> relative_M14VentPeriode.copy(credit_produitsAuDepot = newValue)
            "acheter_produits_depot" -> relative_M14VentPeriode.copy(acheter_produitsAuDepot = newValue)
            "ancien_produits" -> relative_M14VentPeriode.copy(valeur_Produits_depuit_Ancien_Vent_Period = newValue) // Fixed to use correct field
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

    val heurDebutInString = remember(relative_M14VentPeriode.creationTimestamp) {
        DatesHandler.formatDateWithAmPm(Date(relative_M14VentPeriode.creationTimestamp))
    }

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

            IconButton(
                onClick = { showDeleteDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            val key =relative_M14VentPeriode.keyID.takeLast(3)
            Text(
                text = "m14VentPeriode: key =  ${key}- ${relative_M14VentPeriode.get_DebugInfos()}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }

        Coupe_Colle_Buttons(
            relative_Period = relative_M14VentPeriode
        )

        Text(
            text = "Date:$heurDebutInString",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Calculate totals
        val totalVentes =
            relative_M14VentPeriode.credit_Vents_Totale + relative_M14VentPeriode.cash_Vents_Totale
        val totalAchats =
            relative_M14VentPeriode.credit_achats_Totale + relative_M14VentPeriode.cash_achats_Totale
        val totalProduitsDepot_stagne_Cette_Period =
            relative_M14VentPeriode.credit_produitsAuDepot + relative_M14VentPeriode.acheter_produitsAuDepot

        // FIXED: Complete the total_supplies calculation by adding products from depot
        val total_supplies = totalAchats + totalProduitsDepot_stagne_Cette_Period
        val balance = totalVentes - total_supplies + relative_M14VentPeriode.valeur_Produits_depuit_Ancien_Vent_Period

        val sum_Bon_Vents = remember(
            aCentralFacade.repositorysMainGetter.repo2Client.datasValue,
            aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
            aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
            aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
            relative_M14VentPeriode.keyID
        ) {
            val clientsSalesSummary =
                calculateClientSalesSummary(aCentralFacade, relative_M14VentPeriode)
            clientsSalesSummary.totalSalesValue
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ventes Section
            VentesSection(
                relative_M14VentPeriode = relative_M14VentPeriode,
                editingField = editingField,
                editingValue = editingValue,
                sum_Bon_Vents = sum_Bon_Vents,
                totalVentes = totalVentes,
                onStartEditing = ::startEditing,
                onEditingValueChange = { editingValue = it },
                onSaveEditedValue = ::saveEditedValue,
                onCalculatedClick = {
                    focusedValuesGetter.update_activeCentralValues(
                        focusedValuesGetter.active_Central_Values.copy(
                            show_Dialog_filter_AChats_Par_Client_Acheteur = true,
                            vent_Au_Dialog_filter_AChats_Par_Client_Acheteur = relative_M14VentPeriode
                        )
                    )
                },
                focusRequester = focusRequester
            )

            // Achats Section
            AchatsSection(
                relative_M14VentPeriode = relative_M14VentPeriode,
                editingField = editingField,
                editingValue = editingValue,
                isLoadingCalculatedAchat = isLoadingCalculatedAchat,
                calculatedAchatTotal = calculatedAchatTotal,
                totalAchats = totalAchats,
                onStartEditing = ::startEditing,
                onEditingValueChange = { editingValue = it },
                onSaveEditedValue = ::saveEditedValue,
                focusRequester = focusRequester,
            )

            Produits_Ancien_Period(
                relative_M14VentPeriode = relative_M14VentPeriode,
                editingField = editingField,
                editingValue = editingValue,
                onStartEditing = ::startEditing,
                onEditingValueChange = { editingValue = it },
                onSaveEditedValue = ::saveEditedValue,
                focusRequester = focusRequester
            )

            // Produits au Dépôt Section
            ProduitsDepotSection(
                relative_M14VentPeriode = relative_M14VentPeriode,
                editingField = editingField,
                editingValue = editingValue,
                totalProduitsDepot = totalProduitsDepot_stagne_Cette_Period,
                onStartEditing = ::startEditing,
                onEditingValueChange = { editingValue = it },
                onSaveEditedValue = ::saveEditedValue,
                focusRequester = focusRequester
            )

            // Balance Section (Updated call)
            BalanceSection(
                balance = balance,
                totalVentes = totalVentes,
                totalAchats = totalAchats,
                totalProduitsDepot = totalProduitsDepot_stagne_Cette_Period,
                sum_Bon_Vents = sum_Bon_Vents,
                calculatedAchatTotal = calculatedAchatTotal,
                isLoadingCalculatedAchat = isLoadingCalculatedAchat,
                relative_M14VentPeriode = relative_M14VentPeriode // Pass the period object
            )
        }
    }

}

// Helper function to check if a grossist has operations in a specific period
fun isGrossistActiveInPeriod(
    grossistKeyID: String,
    ventPeriodKeyID: String,
    repositorysMainGetter: RepositorysMainGetter
): Boolean {
    return repositorysMainGetter.repo11AchatOperation.datasValue.any { achatOperation ->
        achatOperation.parent_M15Grossist_KeyID == grossistKeyID &&
                achatOperation.parent_M14VentPeriod_KeyID == ventPeriodKeyID
    }
}
@Composable
fun Produits_Ancien_Period(
    relative_M14VentPeriode: M14VentPeriode,
    editingField: String?,
    editingValue: String,
    onStartEditing: (String, Double) -> Unit,
    onEditingValueChange: (String) -> Unit,
    onSaveEditedValue: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "📦 Produits Ancien Période",
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                text = "Produits restants des périodes précédentes",
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Use the correct field for ancient products
            if (editingField == "ancien_produits") {
                OutlinedTextField(
                    value = editingValue,
                    onValueChange = onEditingValueChange,
                    label = { Text("Valeur des produits anciens") },
                    placeholder = { Text("0.0") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSaveEditedValue() }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    suffix = { Text("DA") }
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onStartEditing(
                                "ancien_produits",
                                relative_M14VentPeriode.valeur_Produits_depuit_Ancien_Vent_Period // Use the correct field
                            )
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "💼 Valeur totale",
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "Cliquez pour modifier",
                                fontSize = 11.sp,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Text(
                            text = "${relative_M14VentPeriode.valeur_Produits_depuit_Ancien_Vent_Period} DA", // Use the correct field
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Cette valeur sera ajoutée aux calculs automatiquement",
                fontSize = 10.sp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
            )
        }
    }
}

// Updated loadCalculatedAchatTotals function with timestamp filtering
fun loadCalculatedAchatTotals(
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
fun getPeriodTimestampRange(
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

@Composable
fun Coupe_Colle_Buttons(
    relative_Period: M14VentPeriode
) {
    Text(
        text = "Copy/Paste buttons placeholder",
        fontSize = 12.sp,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}
