package Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP.Views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ColorDetails_APP2_ID_2(
    couleurtId: Long,
    totaleQuantityDesCouleurs: Int,
    clientsAchteurDeCeProduit: List<Long> = emptyList(),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "couleurId (${couleurtId})",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$totaleQuantityDesCouleurs",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )

        // Display client count if available
        if (clientsAchteurDeCeProduit.isNotEmpty()) {
            Text(
                text = "(${clientsAchteurDeCeProduit.size} clients)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
