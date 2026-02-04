package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID3.Compact_Presentoir_Echantilliants.View.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Header component with delete button that requires double-click with 4-second countdown
 * 
 * Usage:
 * DeleteProductHeader(
 *     productName = product.nom,
 *     onDelete = { 
 *         // Your delete logic here
 *         repositorysMainGetter.repoM1Produit.deleteData(product)
 *     }
 * )
 */
@Composable
fun DeleteProductHeader(
    productName: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteClickCount by remember { mutableStateOf(0) }
    var countdownSeconds by remember { mutableStateOf(4) }

    // Reset countdown when it reaches 0
    LaunchedEffect(deleteClickCount) {
        if (deleteClickCount > 0) {
            countdownSeconds = 4
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds--
            }
            // Reset if countdown expires
            deleteClickCount = 0
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                when (deleteClickCount) {
                    0 -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    1 -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.error
                }
            )
            .clickable {
                when (deleteClickCount) {
                    0 -> deleteClickCount = 1
                    1 -> {
                        deleteClickCount = 0
                        onDelete()
                    }
                }
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = when (deleteClickCount) {
                0 -> "Supprimer le produit"
                else -> "Confirmer la suppression"
            },
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = when (deleteClickCount) {
                0 -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onError
            }
        )

        if (deleteClickCount > 0) {
            Text(
                text = "${countdownSeconds}s",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onError
            )
        }
    }
}
