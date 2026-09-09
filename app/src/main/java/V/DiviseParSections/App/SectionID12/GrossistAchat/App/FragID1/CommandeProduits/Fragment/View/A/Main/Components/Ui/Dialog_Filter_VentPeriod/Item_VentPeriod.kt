package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_VentPeriod

import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import EntreApps.Shared.Models.Relative_Vents.Models.M15Grossist
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Item_VentPeriod(
    relative_Period: M14VentPeriode,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    activeGrossist: M15Grossist? = null,
    onPeriodSelected_To_onDismiss: (M14VentPeriode) -> Unit,
) {
    val currentActiveFocuced_M14VentPeriode =
        focusedValuesGetter.active_Central_Values.active_M14VentPeriode_AuFilterAchats

    val isCurrentActive = relative_Period.keyID == currentActiveFocuced_M14VentPeriode?.keyID

    val periodStats = remember(
        relative_Period.keyID,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activeGrossist
    ) {
        var achatOperations =
            viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
                .filter { it.parent_M14VentPeriod_KeyID == relative_Period.keyID }

        activeGrossist?.let { grossist ->
            achatOperations = achatOperations.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
        }

        val totalOperations = achatOperations.size
        val totalQuantity = achatOperations.sumOf { it.sumAchatQantity }
        val uniqueProducts = achatOperations.map { it.parent_M1Produit_KeyID }.toSet().size

        Triple(totalOperations, totalQuantity, uniqueProducts)
    }

    // Format date
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val formattedDate = remember(relative_Period.creationTimestamp) {
        dateFormatter.format(Date(relative_Period.creationTimestamp))
    }

    val active_Central_Values = focusedValuesGetter.active_Central_Values
    val updatedValues = active_Central_Values.copy(
        active_M14VentPeriode_AuFilterAchats = relative_Period
    )

    var showOnCommandBonsDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(
                    value = relative_Period.keyID,
                    key = SemanticsPropertyKey("relative_Periodrelative_Period")
                )
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = active_Central_Values.active_M14VentPeriode_AuFilterAchats?.keyID ?: "",
                    key = SemanticsPropertyKey("active_Central_Values")
                )
            }
            .semantics(mergeDescendants = true) {
                set(value = updatedValues, key = SemanticsPropertyKey(""))
            }
            .clickable {
                focusedValuesGetter.update_activeCentralValues(updatedValues)
                onPeriodSelected_To_onDismiss(relative_Period)
            }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrentActive) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Period icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isCurrentActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCurrentActive) Icons.Default.CheckCircle else Icons.Default.DateRange,
                    contentDescription = "Période",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Period info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = DatesHandler.get_PersonaleDateFormatArab(relative_Period.creationTimestamp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isCurrentActive)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isCurrentActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(Actuelle)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Créée le: $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrentActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // UPDATED: Show statistics with grossist context
                val statsText = if (activeGrossist != null) {
                    "${periodStats.first} opérations • ${periodStats.second} articles • ${periodStats.third} produits (${activeGrossist.nom})"
                } else {
                    "${periodStats.first} opérations • ${periodStats.second} articles • ${periodStats.third} produits"
                }

                Text(
                    text = statsText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrentActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(
                onClick = { showOnCommandBonsDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter طلبيات قيد التنفيذ",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showOnCommandBonsDialog) {
        val onCommandBons = remember {
            viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
                .filter { it.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT }
                .sortedByDescending { it.creationTimestamps }
        }
        var searchClientText by remember { mutableStateOf("") }
        val filteredOnCommandBons = remember(searchClientText, onCommandBons) {
            if (searchClientText.trim().length >= 3) {
                val query = searchClientText.trim()
                onCommandBons.filter { bon ->
                    val clientObj = viewModel.aCentralFacade.repositorysMainGetter.repo2Client.datasValue
                        .find { it.keyID == bon.parent_M2Client_KeyID }
                    val clientName = clientObj?.nom ?: bon.parent_M2Client_DebugInfos
                    clientName.contains(query, ignoreCase = true)
                }
            } else {
                onCommandBons
            }
        }
        val selectedBons = remember { mutableStateListOf<M8BonVent>() }

        AlertDialog(
            onDismissRequest = { showOnCommandBonsDialog = false },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            title = { Text("طلبيات قيد التنفيذ / Bons On Command") },
            text = {
                Column(modifier = Modifier.fillMaxHeight()) {
                    Text("اختر بونات الطلبية لتوليد مشترياتها لهذه الفترة:")
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = searchClientText,
                        onValueChange = { searchClientText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("بحث باسم العميل (3 أحرف على الأقل)...") },
                        singleLine = true,
                        trailingIcon = {
                            if (searchClientText.isNotEmpty()) {
                                IconButton(onClick = { searchClientText = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (filteredOnCommandBons.isEmpty()) {
                        Text("لا توجد طلبيات مطابقة للبحث")
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(filteredOnCommandBons) { bon ->
                                val isSelected = selectedBons.any { it.keyID == bon.keyID }
                                val dateObj = DatesHandler().getDateAndTimString(bon.creationTimestamps)
                                val clientObj = viewModel.aCentralFacade.repositorysMainGetter.repo2Client.datasValue
                                    .find { it.keyID == bon.parent_M2Client_KeyID }
                                val clientName = clientObj?.nom ?: bon.parent_M2Client_DebugInfos
                                val bonLabel = "عميل: $clientName | بون: ${bon.get_DebugInfos()} (${dateObj.date} ${dateObj.time})"

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (isSelected) {
                                                selectedBons.removeAll { it.keyID == bon.keyID }
                                            } else {
                                                selectedBons.add(bon)
                                            }
                                        },
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = { checked ->
                                                if (checked) {
                                                    if (!isSelected) selectedBons.add(bon)
                                                } else {
                                                    selectedBons.removeAll { it.keyID == bon.keyID }
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = bonLabel,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = selectedBons.isNotEmpty(),
                    onClick = {
                        val bonVents = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
                            .filter { op -> selectedBons.any { it.keyID == op.parent_M8BonVent_KeyId } }

                        val generatedAchats = viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation
                            .genere_Achats_Depuit_M11AchatOperation_List(
                                relative_Period,
                                bonVents,
                                produits = viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.datasValue,
                                bonVents = selectedBons.toList()
                            )

                        generatedAchats.forEach {
                            viewModel.aCentralFacade.repositorysMainSetter.repo11AchatOperation_add_New(it)
                        }

                        showOnCommandBonsDialog = false
                    }
                ) {
                    Text("إضافة / Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOnCommandBonsDialog = false }) {
                    Text("إلغاء / Annuler")
                }
            }
        )
    }
}
