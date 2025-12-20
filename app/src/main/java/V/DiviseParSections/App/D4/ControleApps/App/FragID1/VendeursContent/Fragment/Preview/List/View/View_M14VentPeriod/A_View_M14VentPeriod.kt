package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            "ancien_produits" -> relative_M14VentPeriode.copy(valeur_Produits_depuit_Ancien_Vent_Period = newValue)
            "pre_fraits" -> relative_M14VentPeriode.copy(pre_fraits_voiture_essance_marche_et_paprasse = newValue)  // ADD THIS LINE
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
        // Add yellow highlight card for "Ici" state
        if (relative_M14VentPeriode.abdelmounen_Doit_Etre_Ici) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF59D)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏠 Abdelmounen Doit etre au entre ici",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF827717)
                    )
                }
            }
        }

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
            val key = relative_M14VentPeriode.keyID.takeLast(3)
            Text(
                text = "m14VentPeriode: key =  ${key}- ${relative_M14VentPeriode.get_DebugInfos()}",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = {
                    repositorysMainGetter.repo14VentPeriode.datasValue.forEach {
                        aCentralFacade.repositorysMainSetter.update_M14VentPeriode(
                            it.copy(
                                abdelmounen_Doit_Etre_Ici = relative_M14VentPeriode.keyID == it.keyID
                            )
                        )
                    }
                },
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                    containerColor = if (relative_M14VentPeriode.abdelmounen_Doit_Etre_Ici)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = if (relative_M14VentPeriode.abdelmounen_Doit_Etre_Ici) "✓ Ici" else "Ici",
                    fontSize = 14.sp,
                    color = if (relative_M14VentPeriode.abdelmounen_Doit_Etre_Ici)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
                onSyncCalculatedToManual = {
                    // Sync calculated ventes to manual ventes
                    val updatedPeriode = relative_M14VentPeriode.copy(
                        credit_Vents_Totale = sum_Bon_Vents,
                        cash_Vents_Totale = 0.0
                    )
                    updatedPeriode.handel_Clavie_Donne()
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

            Section_Edit_Fraitspre_fraits_voiture_essance_marche_et_paprasse(
                relative_M14VentPeriode = relative_M14VentPeriode,
                onUpdate = { updatedPeriode ->
                    updatedPeriode.handel_Clavie_Donne()
                }
            )

            /*  Produits_Ancien_Period(
                relative_M14VentPeriode = relative_M14VentPeriode,
                editingField = editingField,
                editingValue = editingValue,
                onStartEditing = ::startEditing,
                onEditingValueChange = { editingValue = it },
                onSaveEditedValue = ::saveEditedValue,
                focusRequester = focusRequester
            )          */

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

            // Balance Section with sync button
            BalanceSection(
                balance = balance,
                totalVentes = totalVentes,
                totalAchats = totalAchats,
                totalProduitsDepot = totalProduitsDepot_stagne_Cette_Period,
                sum_Bon_Vents = sum_Bon_Vents,
                calculatedAchatTotal = calculatedAchatTotal,
                isLoadingCalculatedAchat = isLoadingCalculatedAchat,
                relative_M14VentPeriode = relative_M14VentPeriode,
                onSyncManualBalanceToSaved = {
                    // Sync manual balance to saved_balance
                    val updatedPeriode = relative_M14VentPeriode.copy(
                        saved_balance = balance
                    )
                    updatedPeriode.handel_Clavie_Donne()
                }
            )
        }
    }
}

