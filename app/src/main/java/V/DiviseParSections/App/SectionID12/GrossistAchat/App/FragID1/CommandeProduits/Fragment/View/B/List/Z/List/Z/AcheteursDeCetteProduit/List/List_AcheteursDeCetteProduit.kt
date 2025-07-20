package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun List_AcheteursDeCetteProduit(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    achatCouleur: M11AchatOperation
) {
    // Get sales operations data
    val repo10OperationVentCouleur = viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
    val listFCouleurVentOperation = achatCouleur.get_list_v_Depuit_joinedStringKeys(repo10OperationVentCouleur)

    // Separate direct sales from linked sales
    val directSales = listFCouleurVentOperation.filter { it.linked_To_M10OperationVent_KeyID.isEmpty() }
    val linkedSales = listFCouleurVentOperation.filter { it.linked_To_M10OperationVent_KeyID.isNotEmpty() }

    // Group direct sales by client
    val directSalesByClient = directSales.groupBy { ventOperation ->
        val gBonVent = viewModel.getter.repo8BonVent.datasValue.find {
            it.keyID == ventOperation.parent_M8BonVent_KeyId
        }
        gBonVent?.parent_M2Client_KeyID
    }.filterKeys { it != null }

    // Group linked sales by client
    val linkedSalesByClient = linkedSales.groupBy { ventOperation ->
        val gBonVent = viewModel.getter.repo8BonVent.datasValue.find {
            it.keyID == ventOperation.parent_M8BonVent_KeyId
        }
        gBonVent?.parent_M2Client_KeyID
    }.filterKeys { it != null }

    Column(
        modifier = Modifier
            .getSemanticsTag(nomVal = "listFCouleurVentOperation", data = listFCouleurVentOperation)
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Direct Sales Section
        if (directSalesByClient.isNotEmpty()) {
            Text(
                text = "Ventes Directes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
            )

            directSalesByClient.forEach { (clientKeyID, ventOperations) ->
                if (clientKeyID != null) {
                    ClientSalesCard(
                        viewModel = viewModel,
                        clientKeyID = clientKeyID,
                        ventOperations = ventOperations,
                        cardColor = MaterialTheme.colorScheme.surfaceVariant,
                        isLinked = false
                    )
                }
            }
        }

        // Linked Sales Section (in separate cards as requested)
        if (linkedSalesByClient.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ventes Liées (Alternatives)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
            )

            linkedSalesByClient.forEach { (clientKeyID, ventOperations) ->
                if (clientKeyID != null) {
                    ClientSalesCard(
                        viewModel = viewModel,
                        clientKeyID = clientKeyID,
                        ventOperations = ventOperations,
                        cardColor = MaterialTheme.colorScheme.secondaryContainer,
                        isLinked = true
                    )
                }
            }
        }

        // No sales message
        if (directSalesByClient.isEmpty() && linkedSalesByClient.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "Aucun client trouvé pour ce produit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ClientSalesCard(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    clientKeyID: String,
    ventOperations: List<M10OperationVentCouleur>, // Replace with proper type
    cardColor: Color,
    isLinked: Boolean
) {
    val client = viewModel.getter.repo2Client.datasValue.find {
        it.keyID == clientKeyID
    }

    if (client != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isLinked) 3.dp else 1.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Client header with linked indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = client.nom,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isLinked) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(
                                text = "Lié",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sales details
                ventOperations.forEach { ventOperation ->
                    SalesOperationRow(
                        viewModel = viewModel,
                        ventOperation = ventOperation,
                        isLinked = isLinked
                    )
                }

                // Total quantity for this client
                val totalQuantity = ventOperations.sumOf { it.quantity }
                if (ventOperations.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total pour ce client:",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$totalQuantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isLinked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun SalesOperationRow(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    ventOperation: M10OperationVentCouleur, // Replace with proper type
    isLinked: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isLinked) "→" else "•",
                fontSize = 16.sp,
                color = if (isLinked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                text = "Qté: ${ventOperation.quantity}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // BonVent info
        val bonVent = viewModel.getter.repo8BonVent.datasValue.find {
            it.keyID == ventOperation.parent_M8BonVent_KeyId
        }
        bonVent?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bon: ${it.keyID.takeLast(6)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                if (isLinked) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔗",
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
