package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun B_Item_TransactionItem(
    transaction: _1_3_TransactionCommercial,
    viewModel: ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient,
) {
    val datesHandler = DatesHandler()
    val etateActuellementEst = transaction.etateActuellementEst
    val activeTransactionId by viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState()

    // For blinking effect when not active
    val blinkState = remember { mutableStateOf(false) }

    // Blinking effect using LaunchedEffect when not active and in ON_MODE_COMMEND_ACTUELLEMENT state
    if (etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        && activeTransactionId != transaction.vid) {
        LaunchedEffect(key1 = Unit) {
            while (true) {
                blinkState.value = !blinkState.value
                delay(500) // 500ms blink interval
            }
        }
    }

    // Card with background color based on transaction state
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = etateActuellementEst.color)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Main content is in a Box to allow floating elements
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Original Row content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shopping cart icon moved inside the Row
                if (etateActuellementEst == _1_3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT) {
                    IconButton(
                        onClick = {
                            viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.upsertUneDataEtReturnVID(
                                transaction.copy(
                                    ouvert = !transaction.ouvert
                                )
                            ) {
                                viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.activeVId_1_3_TransactionCommercial.value =
                                    transaction.vid

                                // Navigate to the cart screen after selecting a transaction
                                viewModel.navigateToCartScreen()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Select Transaction",
                            tint = if (activeTransactionId == transaction.vid) {
                                Color.White
                            } else {
                                // Blinking effect - alternate between Red and Gray
                                if (blinkState.value) Color.Red else Color.Gray
                            }
                        )
                    }
                }

                Text(
                    text = " الوقت: ${datesHandler.formatTimeToArabic(transaction.heurDebutInString)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = etateActuellementEst.nomArabe,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,  // Right-aligned for Arabic
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                if (transaction.heurFinInString != "Non Defini") {
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    Text(
                        text = "Fin: ${datesHandler.formatTimeToArabic(transaction.heurFinInString)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}
