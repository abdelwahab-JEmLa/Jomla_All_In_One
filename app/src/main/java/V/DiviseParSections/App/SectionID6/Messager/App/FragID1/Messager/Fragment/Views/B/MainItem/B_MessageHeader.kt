package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    clientName: String,
    vendorName: String,
    messageVID: Long,
    timestamp: Long,
    datesHandler: DatesHandler,
    etatesChildKeyIDsList: List<M17MessageVocale>,
    isFromActiveAccount: Boolean = false,
    isAdminMessage: Boolean = false // Add this parameter
) {
    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin
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
            Column {
                val arabNom = relative_M9AppCompt
                    ?.getList_autres_Noms_SepareParComma()
                    ?.firstOrNull { nom ->
                        nom.any { char -> char in '\u0600'..'\u06FF' || char in '\u0750'..'\u077F' }
                    }
                    ?: relative_M9AppCompt?.nom
                    ?: "???"

                Text(
                    modifier = Modifier
                        .getSemanticsTag(
                            relative_M9AppCompt, ""
                        ),
                    text = arabNom,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        isAdminMessage -> MaterialTheme.colorScheme.onError
                        isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )

                val currentState = etatesChildKeyIDsList.maxByOrNull { it.creationTimestamps }
                currentState?.let { state ->

                    val stateText = when (currentApp_Est_Admin) {
                        false -> {
                            if (isFromActiveAccount) {
                                "📤 Envoyé"
                            } else {
                                when (state.etate) {
                                    M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> "⏺️ En cours d'enregistrement"
                                    M17MessageVocale.Etate.ENVOYER -> "📤 Envoyé"
                                    M17MessageVocale.Etate.VUE -> "👁️ Vu"
                                    M17MessageVocale.Etate.ECOUTE -> "🎧 Écouté"
                                    M17MessageVocale.Etate.Premier_Test_Envoi ->"📤 Envoyé"
                                }
                            }
                        }

                        true -> when (state.etate) {
                            M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> "⏺️ En cours d'enregistrement"
                            M17MessageVocale.Etate.ENVOYER -> "📤 Envoyé"
                            M17MessageVocale.Etate.VUE -> "👁️ Vu"
                            M17MessageVocale.Etate.ECOUTE -> "🎧 Écouté"
                            M17MessageVocale.Etate.Premier_Test_Envoi ->"📤 Envoyé"

                        }
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
                                else -> {MaterialTheme.colorScheme.onPrimary}
                            }

                            else -> when (state.etate) {
                                M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> MaterialTheme.colorScheme.error
                                M17MessageVocale.Etate.ENVOYER -> MaterialTheme.colorScheme.primary
                                M17MessageVocale.Etate.VUE -> MaterialTheme.colorScheme.secondary
                                M17MessageVocale.Etate.ECOUTE -> Color.Green
                                else -> {MaterialTheme.colorScheme.onPrimary}
                            }
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
                focusedValuesGetter.currentApp_Est_Admin.ifTrue {
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
                                isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.8f
                                )

                                else -> MaterialTheme.colorScheme.error
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }
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
                                isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.5f
                                )

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
