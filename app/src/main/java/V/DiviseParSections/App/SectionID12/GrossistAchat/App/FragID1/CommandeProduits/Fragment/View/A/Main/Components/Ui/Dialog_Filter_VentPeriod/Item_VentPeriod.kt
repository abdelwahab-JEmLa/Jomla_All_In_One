package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_VentPeriod

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    isCurrentActive: Boolean,
    activeGrossist: M15Grossist? = null,  // UPDATED: Add active grossist parameter
    onPeriodSelected: (M14VentPeriode) -> Unit
) {
    val periodStats = remember(
        relative_Period.keyID,
        viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue,
        activeGrossist
    ) {
        var achatOperations =
            viewModel.aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
                .filter { it.parent_M14VentPeriod_KeyID == relative_Period.keyID }

        // UPDATED: Filter by active grossist if one is selected
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
    val updatedValues =  active_Central_Values.copy(
        active_M14VentPeriode_AuFilterAchats = relative_Period
    )

    focusedValuesGetter.update_activeCentralValues(updatedValues)

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
                    value = active_Central_Values.active_M14VentPeriode_AuFilterAchats?.keyID?:"",
                    key = SemanticsPropertyKey("active_Central_Values")
                )
            }
            .semantics(mergeDescendants = true) {
                set(value = updatedValues, key = SemanticsPropertyKey(""))
            }
            .clickable { onPeriodSelected(relative_Period) }
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
                        text = relative_Period.get_DebugInfos(),
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
        }
    }
}
