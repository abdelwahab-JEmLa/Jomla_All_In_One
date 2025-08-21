package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main.Components.Ui.Dialog_Filter_Client

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.TrendingUp
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

data class ClientPurchaseInfo(
    val uniqueProducts: Int,
    val totalQuantity: Int,
    val totalSalesValue: Double,
    val deliveredOperations: List<M10OperationVentCouleur>
)

@SuppressLint("DefaultLocale")
@Composable
fun Item_Client(
    focusedValuesGetter: FocusedValuesGetter = koinInject(),
    relative_client: M2Client,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    activePeriod: M14VentPeriode?,
    activeGrossist: M15Grossist?,
    on_Pour_Dissmiss: () -> Unit,
) {
    val clientPurchaseInfo = remember(
        relative_client.keyID,
        viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue,
        viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue,
        activePeriod?.keyID,
        activeGrossist?.keyID
    ) {
        val allBonVents = viewModel.aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        val allVentOperations =
            viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue

        var clientBonVents =
            allBonVents.filter { it.parent_M2Client_KeyID == relative_client.keyID }

        activePeriod?.let { period ->
            clientBonVents = clientBonVents.filter {
                true
            }
        }

        activeGrossist?.let { grossist ->
            clientBonVents = clientBonVents.filter {
                true
            }
        }

        val clientBonVentIds = clientBonVents.map { it.keyID }.toSet()

        val clientVentOperations = allVentOperations.filter {
            it.parent_M8BonVent_KeyId in clientBonVentIds
        }

        val uniqueProducts = clientVentOperations.map { it.parent_M1Produit_KeyId }.toSet().size
        val totalQuantity = clientVentOperations.sumOf { it.quantity }

        val deliveredOperations = clientVentOperations.filter {
            (it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
                    && it.parent_M14VentPeriod_KeyId == (
                    focusedValuesGetter.active_Central_Values.vent_Au_Dialog_filter_AChats_Par_Client_Acheteur?.keyID
                        ?: ""))
        }

        val totalSalesValue = deliveredOperations.sumOf { ventOperation ->
            val parentM13TarificationPrix = viewModel.aCentralFacade.repositorysMainGetter
                .find_M13Tarification_By_KeyID(ventOperation.parentM13TarificationKeyID)?.prixCurrency
                ?: 0.0
            ventOperation.quantity * parentM13TarificationPrix
        }

        ClientPurchaseInfo(
            uniqueProducts = uniqueProducts,
            totalQuantity = totalQuantity,
            totalSalesValue = totalSalesValue,
            deliveredOperations = deliveredOperations
        )
    }

    val contextText = when {
        activePeriod != null && activeGrossist != null -> "(période + grossiste actifs)"
        activePeriod != null -> "(période active)"
        activeGrossist != null -> "(grossiste actif)"
        else -> null
    }
    val updatedValues =
        focusedValuesGetter.active_Central_Values.copy(active_M2Client_AuFilterAchats = relative_client)

    Card(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(value = updatedValues, key = SemanticsPropertyKey("updatedValues"))
            }
            .semantics(mergeDescendants = true) {
                set(
                    value = clientPurchaseInfo.deliveredOperations,
                    key = SemanticsPropertyKey("clientPurchaseInfo")
                )
            }
            .clickable {

                focusedValuesGetter.update_activeCentralValues(updatedValues)
                on_Pour_Dissmiss()
            }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = relative_client.nom.take(2).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = relative_client.nom,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${clientPurchaseInfo.uniqueProducts} produits • ${clientPurchaseInfo.totalQuantity} articles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (clientPurchaseInfo.totalSalesValue > 0) Icons.Default.TrendingUp else Icons.Default.AccountBalance,
                        contentDescription = "Ventes totales",
                        modifier = Modifier.size(14.dp),
                        tint = if (clientPurchaseInfo.totalSalesValue > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Ventes: ${
                            String.format(
                                "%.2f",
                                clientPurchaseInfo.totalSalesValue
                            )
                        } DA",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (clientPurchaseInfo.totalSalesValue > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        fontWeight = if (clientPurchaseInfo.totalSalesValue > 0) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    contextText?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
