package V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.List.View.View_M14VentPeriod

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import V.DiviseParSections.App.D4.ControleApps.App.FragID1.VendeursContent.Fragment.Preview.ViewModel_M14VentPeriod
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.filtersAndSorts_Central.calculateClientSalesSummary
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    relative_M9AppCompt: M09AppCompt?,
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
            "pre_fraits" -> relative_M14VentPeriode.copy(pre_fraits_voiture_essance_marche_et_paprasse = newValue)
            "saved_depot" -> relative_M14VentPeriode.copy(saved_produits_au_depot = newValue)
            "saved_clients_credit" -> relative_M14VentPeriode.copy(saved_totale_credits_clients = newValue)
            "saved_fournisseurs_credit" -> relative_M14VentPeriode.copy(saved_sums_fournisseurs_Short_Term = newValue)
            "saved_cache_au_coffre" -> relative_M14VentPeriode.copy(saved_cache_au_coffre = newValue)
            "saved_balance_par_chiffre" -> relative_M14VentPeriode.copy(save_balence_par_chiffre = newValue)
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



    val totalDepotStockValue = remember(
        repositorysMainGetter.repo03CouleurProduitInfos.datasValue,
        repositorysMainGetter.repo13TarificationInfos.datasValue
    ) {
        val colors = repositorysMainGetter.repo03CouleurProduitInfos.datasValue
        val tariffs = repositorysMainGetter.repo13TarificationInfos.datasValue
        val purchaseTariffsByProduct = tariffs
            .filter { it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros && it.prixCurrency != 0.0 }
            .groupBy { it.parent_M1Produit_KeyId }
            .mapValues { (_, tList) -> tList.maxByOrNull { it.creationTimestamps }?.prixCurrency ?: 0.0 }

        colors.fold(0.0) { acc, col ->
            if (col.count_Don_Depot > 0) {
                val purchasePrice = purchaseTariffsByProduct[col.parentBProduitInfosKeyID] ?: 0.0
                acc + (col.count_Don_Depot * purchasePrice)
            } else acc
        }
    }

    // Dynamic credit sum for all non-fournisseur clients
    val totalClientsCredit = remember(
        repositorysMainGetter.repo2Client.datasValue,
        repositorysMainGetter.repo8BonVent.datasValue
    ) {
        M2Client.calculateTotalCredit(
            clients = repositorysMainGetter.repo2Client.datasValue,
            bons = repositorysMainGetter.repo8BonVent.datasValue,
            forFournisseurs = false
        )
    }

    // Dynamic credit sum for fournisseur grossiste clients
    val totalFournisseursCredit = remember(
        repositorysMainGetter.repo2Client.datasValue,
        repositorysMainGetter.repo8BonVent.datasValue
    ) {
        M2Client.calculateTotalCredit(
            clients = repositorysMainGetter.repo2Client.datasValue,
            bons = repositorysMainGetter.repo8BonVent.datasValue,
            forFournisseurs = true
        )
    }

    val dynamicBalance = totalDepotStockValue + totalClientsCredit - totalFournisseursCredit


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
                text = "${key}}",
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

        // Section résumé Chiffre d'Affaires, Bénéfices et Valeur Stock Dépôt
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Résumé Dépôt Et Credite Et Balence Entre Eux",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                @Composable
                fun ResumeRow(label: String, dynamic: Double, savedKey: String, savedValue: Double, dynamicColor: Color = MaterialTheme.colorScheme.tertiary) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1.6f)
                        )
                        Text(
                            text = "%.0f".format(dynamic),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = dynamicColor,
                            modifier = Modifier.weight(1f)
                        )
                        if (editingField == savedKey) {
                            OutlinedTextField(
                                value = editingValue,
                                onValueChange = { editingValue = it },
                                label = { Text("%.0f".format(savedValue)) },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { saveEditedValue() }),
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester)
                            )
                        } else {
                            Text(
                                text = "💾 %.0f".format(savedValue),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { startEditing(savedKey, savedValue) }
                            )
                        }
                    }
                }

                // Depot row
                ResumeRow(
                    label = "Dépôt (prods par p achat):",
                    dynamic = totalDepotStockValue,
                    savedKey = "saved_depot",
                    savedValue = relative_M14VentPeriode.saved_produits_au_depot
                )

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Clients credit row
                ResumeRow(
                    label = "Crédit clients:",      //<--
                    //TODO(1): fait que ca est Set_Client_Court_Terme
                    dynamic = totalClientsCredit,
                    savedKey = "saved_clients_credit",
                    savedValue = relative_M14VentPeriode.saved_totale_credits_clients,
                    dynamicColor = Color(0xFFE64A19)
                )
                // Fournisseurs credit row
                ResumeRow(
                    label = "Crédit fournisseurs:",       //<--
                    //TODO(1): et ca c Set_Fournisseur_Court_Terme
                    dynamic = totalFournisseursCredit,
                    savedKey = "saved_fournisseurs_credit",
                    savedValue = relative_M14VentPeriode.saved_sums_fournisseurs_Short_Term,
                    dynamicColor = Color(0xFF1565C0)
                )
                // Cache au coffre row
                ResumeRow(
                    label = "Cash au coffre:",
                    dynamic = 0.0,
                    savedKey = "saved_cache_au_coffre",
                    savedValue = relative_M14VentPeriode.saved_cache_au_coffre,
                    dynamicColor = MaterialTheme.colorScheme.secondary
                )

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Dynamic balance row + save_balence_par_chiffre
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚖ Balance dyn:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.6f)
                    )
                    Text(
                        text = "%.0f".format(dynamicBalance),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (dynamicBalance >= 0) Color(0xFF388E3C) else Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f)
                    )
                    if (editingField == "saved_balance_par_chiffre") {
                        OutlinedTextField(
                            value = editingValue,
                            onValueChange = { editingValue = it },
                            label = { Text("%.0f".format(relative_M14VentPeriode.save_balence_par_chiffre)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { saveEditedValue() }),
                            modifier = Modifier.weight(1f).focusRequester(focusRequester)
                        )
                    } else {
                        Text(
                            text = "💾 %.0f".format(relative_M14VentPeriode.save_balence_par_chiffre),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.weight(1f).clickable {
                                startEditing("saved_balance_par_chiffre", relative_M14VentPeriode.save_balence_par_chiffre)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Copy all dynamic values to saved fields
                IconButton(
                    onClick = {
                        repositorysMainSetter.update_M14VentPeriode(
                            relative_M14VentPeriode.copy(
                                saved_produits_au_depot = totalDepotStockValue,
                                saved_totale_credits_clients = totalClientsCredit,
                                saved_sums_fournisseurs_Short_Term = totalFournisseursCredit,
                                save_balence_par_chiffre = dynamicBalance
                            )
                        )
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copier les valeurs dynamiques dans les saved",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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

