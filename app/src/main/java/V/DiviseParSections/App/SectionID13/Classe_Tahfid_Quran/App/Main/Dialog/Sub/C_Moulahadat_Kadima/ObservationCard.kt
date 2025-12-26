package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.C_Moulahadat_Kadima

import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
 fun ObservationCard(
    observation: M20ObsarvationEtudion,
    onEdit: (M20ObsarvationEtudion) -> Unit,
    onDelete: (M20ObsarvationEtudion) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()) }

    // FIXED: Check if this is an absence observation
    val isAbsence = observation.type == M20ObsarvationEtudion.Type.Raeeb

    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAbsence) {
                // Use error container for absence
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            }
        ),
        border = if (isAbsence) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.error)
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header with absence indicator
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.Companion.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Show absence icon if applicable
                    if (isAbsence) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = "غياب",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.Companion.size(20.dp)
                        )
                        Text(
                            text = "غياب",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Text(
                        text = "التاريخ: ${dateFormat.format(Date(observation.creationTimestamps))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(
                        onClick = { onEdit(observation) },
                        modifier = Modifier.Companion.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.Companion.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onDelete(observation) },
                        modifier = Modifier.Companion.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.Companion.size(18.dp)
                        )
                    }
                }
            }

            Divider()

            // Rest of the card content remains the same...
            // From Sura and Aya
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = "من سورة:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${observation.min_soura.arabicName} (الآية ${observation.min_aya})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // To Sura and Aya
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = "إلى سورة:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${observation.ila_soura.arabicName} (الآية ${observation.ila_aya})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Divider()

            // Takyim (Evaluation)
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                Text(
                    text = "التقييم:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = observation.takyim.arabicName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isAbsence) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.tertiary
                    }
                )
            }
        }
    }
}
