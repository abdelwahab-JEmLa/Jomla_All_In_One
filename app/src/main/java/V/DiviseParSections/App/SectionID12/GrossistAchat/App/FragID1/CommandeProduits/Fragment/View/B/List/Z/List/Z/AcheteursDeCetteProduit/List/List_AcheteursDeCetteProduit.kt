package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun List_AcheteursDeCetteProduit(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    achatCouleur: M11AchatOperation
) {
    // Get sales operations data
    val repo10OperationVentCouleur = viewModel.aCentralFacade.repoMainGetter.repo10OperationVentCouleur.datasValue
    val listFCouleurVentOperation = achatCouleur.get_list_v_Depuit_joinedStringKeys(repo10OperationVentCouleur)

    // Group sales operations by client (through BonVent)
    val salesByClient = listFCouleurVentOperation.groupBy { ventOperation ->
        val gBonVent = viewModel.getter.repo8BonVent.datasValue.find {
            it.keyID == ventOperation.parentM8BonVentKeyId
        }
        gBonVent?.parent_M2Client_KeyID
    }.filterKeys { it != null } // Remove null client keys

    Column(
        modifier = Modifier
            .getSemanticsTag(nomVal = "listFCouleurVentOperation", data = listFCouleurVentOperation)
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (salesByClient.isEmpty()) {
            Text(
                text = "Aucun client trouvé pour ce produit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            salesByClient.forEach { (clientKeyID, ventOperations) ->
                if (clientKeyID != null) {
                    val client = viewModel.getter.repo2Client.datasValue.find {
                        it.keyID == clientKeyID
                    }

                    if (client != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                // Client name
                                Text(
                                    text = client.nom,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Sales details for this client
                                ventOperations.forEach { ventOperation ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "• Qté: ${ventOperation.quantity}",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        // Add BonVent info if needed
                                        val bonVent = viewModel.getter.repo8BonVent.datasValue.find {
                                            it.keyID == ventOperation.parentM8BonVentKeyId
                                        }
                                        bonVent?.let {
                                            Text(
                                                text = "Bon: ${it.keyID.takeLast(6)}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }

                                // Total quantity for this client
                                val totalQuantity = ventOperations.sumOf { it.quantity }
                                if (ventOperations.size > 1) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Total: $totalQuantity",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.primary
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
