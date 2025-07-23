package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Update MessageHeader function signature in B_MessageHeader.kt:
@Composable
fun MessageHeader(
    relative_M9AppCompt: Z_AppCompt?,
    relative_M17MessageVocale: M17MessageVocale,
    viewModel: ViewModelMessageur,
    clientName: String,
    vendorName: String,
    messageVID: Long,
    timestamp: Long,
    datesHandler: DatesHandler,
    etatesChildKeyIDsList: List<M17MessageVocale>,
    isFromActiveAccount: Boolean = false,
    isAdminMessage: Boolean = false // Add this parameter
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when {
                    isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.2f)
                    isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.GraphicEq,
                    contentDescription = "Message vocal",
                    tint = when {
                        isAdminMessage -> MaterialTheme.colorScheme.onError
                        isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier
                        .size(16.dp)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = relative_M9AppCompt?.nom?:"???",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        isAdminMessage -> MaterialTheme.colorScheme.onError
                        isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )


                Text(
                    text = "Vendeur: $vendorName",
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.8f)
                        isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Message vocal #$messageVID",
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.6f)
                        isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )



                // Display current state information
                val currentState = etatesChildKeyIDsList.maxByOrNull { it.creationTimestamps }
                currentState?.let { state ->
                    val stateText = when (state.etate) {
                        M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> "⏺️ En cours d'enregistrement"
                        M17MessageVocale.Etate.ENVOYER -> "📤 Envoyé"
                        M17MessageVocale.Etate.VUE -> "👁️ Vu"
                        M17MessageVocale.Etate.ECOUTE -> "🎧 Écouté"
                    }

                    Text(
                        text = stateText,
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            isAdminMessage -> MaterialTheme.colorScheme.onError
                            isFromActiveAccount -> when (state.etate) {
                                M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> MaterialTheme.colorScheme.onPrimary
                                M17MessageVocale.Etate.ENVOYER -> MaterialTheme.colorScheme.onPrimary
                                M17MessageVocale.Etate.VUE -> MaterialTheme.colorScheme.onPrimary
                                M17MessageVocale.Etate.ECOUTE -> MaterialTheme.colorScheme.onPrimary
                            }
                            else -> when (state.etate) {
                                M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> MaterialTheme.colorScheme.error
                                M17MessageVocale.Etate.ENVOYER -> MaterialTheme.colorScheme.primary
                                M17MessageVocale.Etate.VUE -> MaterialTheme.colorScheme.secondary
                                M17MessageVocale.Etate.ECOUTE -> Color.Green
                            }
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (relative_M17MessageVocale.keyID.isNotEmpty()) {
                    val syncStatus =
                        if (relative_M17MessageVocale.dernierTimeTampsSynchronisationAvecFireBase > 0) {
                            val timeDiff =
                                System.currentTimeMillis() - relative_M17MessageVocale.dernierTimeTampsSynchronisationAvecFireBase
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
                        color = when {
                            isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.4f)
                            isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete button
                IconButton(
                    onClick = {
                        showDeleteConfirmation = true
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Supprimer le message",
                        tint = when {
                            isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.8f)
                            isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            else -> MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = datesHandler.getDateAndTimStringAvecSeconds(timestamp).time,
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.7f)
                            isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Display total states count
                    if (etatesChildKeyIDsList.isNotEmpty()) {
                        Text(
                            text = "${etatesChildKeyIDsList.size} état(s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.5f)
                                isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deleteData(relative_M17MessageVocale)
                etatesChildKeyIDsList.forEach { state ->
                    viewModel.deleteData(state)
                }
                showDeleteConfirmation = false
            },
            onDismiss = {
                showDeleteConfirmation = false
            },
            messageInfo = "Message vocal #$messageVID de $clientName"
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    messageInfo: String
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Supprimer le message",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Text(
                text = "Êtes-vous sûr de vouloir supprimer définitivement ce message?\n\n$messageInfo\n\nCette action ne peut pas être annulée.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = "Supprimer",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Annuler",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    )
}
