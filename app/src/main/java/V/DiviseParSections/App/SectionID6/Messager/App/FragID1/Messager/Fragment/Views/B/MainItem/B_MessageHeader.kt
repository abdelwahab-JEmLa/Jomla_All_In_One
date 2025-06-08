package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun MessageHeader(
    clientName: String,
    vendorName: String,
    messageVID: Long,
    timestamp: Long,
    datesHandler: DatesHandler,
    // Added parameters for additional display
    parentD_EtateMessageVocale: D_EtateMessageVocale,
    etatesChildKeyIDsList: List<D_EtateMessageVocale>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.GraphicEq,
                    contentDescription = "Message vocal",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = clientName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Vendeur: $vendorName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Message vocal #$messageVID",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (parentD_EtateMessageVocale.nomDeSonOriginaleFichie.isNotEmpty()) {
                    Text(
                        text = "Fichier: ${parentD_EtateMessageVocale.nomDeSonOriginaleFichie}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Display current state information
                val currentState = etatesChildKeyIDsList.maxByOrNull { it.timestamps }
                currentState?.let { state ->
                    val stateText = when (state.nom) {
                        D_EtateMessageVocale.Nom.EN_COURT_ENREGESTREMENT -> "⏺️ En cours d'enregistrement"
                        D_EtateMessageVocale.Nom.ENVOYER -> "📤 Envoyé"
                        D_EtateMessageVocale.Nom.VUE -> "👁️ Vu"
                        D_EtateMessageVocale.Nom.ECOUTE -> "🎧 Écouté"
                    }

                    Text(
                        text = stateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = when (state.nom) {
                            D_EtateMessageVocale.Nom.EN_COURT_ENREGESTREMENT -> MaterialTheme.colorScheme.error
                            D_EtateMessageVocale.Nom.ENVOYER -> MaterialTheme.colorScheme.primary
                            D_EtateMessageVocale.Nom.VUE -> MaterialTheme.colorScheme.secondary
                            D_EtateMessageVocale.Nom.ECOUTE -> Color.Green
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Display Firebase sync status if available
                if (parentD_EtateMessageVocale.keyFireBase.isNotEmpty()) {
                    val syncStatus =
                        if (parentD_EtateMessageVocale.dernierFireBaseUpdateTimestamps > 0) {
                            val timeDiff =
                                System.currentTimeMillis() - parentD_EtateMessageVocale.dernierFireBaseUpdateTimestamps
                            when {
                                timeDiff < 60000 -> "🟢 Synchronisé"
                                timeDiff < 300000 -> "🟡 Sync récente"
                                else -> "🔴 Sync ancienne"
                            }
                        } else {
                            "⚪ Non synchronisé"
                        }

                    Text(
                        text = syncStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = datesHandler.getDateAndTimString(timestamp).time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Display total states count
            if (etatesChildKeyIDsList.isNotEmpty()) {
                Text(
                    text = "${etatesChildKeyIDsList.size} état(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
