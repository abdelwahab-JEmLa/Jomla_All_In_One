package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun B_Item_TransactionItem(transaction: _1_3_TransactionCommercial) {
    val datesHandler = DatesHandler()

    val etateActuellementEst =
        if (
            transaction.cJustPourVoirPanie) _1_3_TransactionCommercial.EtateActuellementEst
                .COMMANDE_LIVRAI
        else
            transaction.etateActuellementEst

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
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding( 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

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

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}
