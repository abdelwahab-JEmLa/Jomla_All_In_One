package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun B_ItemMessagesVocale(
    relative_M17MessageVocale: M17MessageVocale,
    viewModel: ViewModelMessageur,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    list_D_EtateMessageVocale: List<M17MessageVocale>,
    uiState: UiState,
) {
    val activeCurrent_M9AppCompt = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt
    val relative_M9AppCompt = repositorysMainGetter.find_M9AppCompt_By_KeyID(relative_M17MessageVocale.parent_M9AppCompt_KeyID)
    val relative_M8BonVent = repositorysMainGetter.find_M8BonVent(relative_M17MessageVocale.parent_M8BonVent_KeyID)

    val its_ViewMessage_Du_Active_M9AppCompt = relative_M9AppCompt?.keyID == activeCurrent_M9AppCompt?.keyID
    val its_Admin_Message = relative_M9AppCompt?.its_Admin ?:false

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val datesHandler = remember { DatesHandler() }
    val audioHandler = viewModel.audioRecorderAndPlayHandler

    val playbackProgress by audioHandler.playbackProgress.collectAsState()

    val clientName = relative_M8BonVent?.parent_M2Client_DebugInfos ?: "Client inconnu"
    val vendorName = relative_M17MessageVocale.parent_M9AppCompt_DebugInfos.takeIf { it.isNotEmpty() } ?: "Vendeur inconnu"

    val currentState =
        list_D_EtateMessageVocale.maxByOrNull { it.creationTimestamps }?.etate
        ?: relative_M17MessageVocale.etate

    val isListened = currentState == M17MessageVocale.Etate.ECOUTE

    val latestTimestamp = list_D_EtateMessageVocale.maxByOrNull { it.creationTimestamps }?.creationTimestamps ?: 0L

    val isCurrentlyPlaying = remember(playbackProgress.isPlaying, audioHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioHandler.getCurrentPlaybackSession()?.parentMessageVID == relative_M17MessageVocale.parentMessageVID && playbackProgress.isPlaying
    }

    val isCurrentlyDownloading = remember(playbackProgress.isDownloading, audioHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioHandler.getCurrentPlaybackSession()?.parentMessageVID == relative_M17MessageVocale.parentMessageVID && playbackProgress.isDownloading
    }

    LaunchedEffect(relative_M17MessageVocale.parentMessageVID, isCurrentlyPlaying) {
        if (isCurrentlyPlaying) {
            try {
                while (isCurrentlyPlaying && audioHandler.isPlaying()) {
                    audioHandler.updatePlaybackProgress()
                    delay(100)

                    if (audioHandler.getCurrentPlaybackSession()?.parentMessageVID != relative_M17MessageVocale.parentMessageVID) {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(relative_M17MessageVocale.parentMessageVID) {
        onDispose {
            try {
                val currentSession = audioHandler.getCurrentPlaybackSession()
                if (currentSession?.parentMessageVID == relative_M17MessageVocale.parentMessageVID) {
                    audioHandler.stopPlayback()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column {
        // Main message card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (its_ViewMessage_Du_Active_M9AppCompt) {
                    Arrangement.End // Messages from active account align to the right
                } else {
                    Arrangement.Start // Messages from others align to the left
                }
            ) {
                Card(
                    modifier = Modifier
                        .getSemanticsTag(relative_M17MessageVocale,"relative_M17MessageVocale")
                        .wrapContentWidth()
                        .padding(
                            start = if (its_ViewMessage_Du_Active_M9AppCompt) 40.dp else 0.dp,
                            end = if (its_ViewMessage_Du_Active_M9AppCompt) 0.dp else 40.dp
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            // Admin messages get red background
                            its_Admin_Message -> MaterialTheme.colorScheme.error
                            // Regular styling for sent/received messages
                            its_ViewMessage_Du_Active_M9AppCompt -> {
                                // Green bubble for sent messages (like Telegram)
                                when (currentState) {
                                    M17MessageVocale.Etate.ECOUTE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                    M17MessageVocale.Etate.VUE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    M17MessageVocale.Etate.ENVOYER -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    M17MessageVocale.Etate.Premier_Test_Envoi -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                }
                            }
                            else -> {
                                // Light gray bubble for received messages (like Telegram)
                                when (currentState) {
                                    M17MessageVocale.Etate.ECOUTE -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                                    M17MessageVocale.Etate.VUE -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                    M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    M17MessageVocale.Etate.ENVOYER -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                    M17MessageVocale.Etate.Premier_Test_Envoi -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                }
                            }
                        }
                    ),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (its_ViewMessage_Du_Active_M9AppCompt) 16.dp else 4.dp,
                        bottomEnd = if (its_ViewMessage_Du_Active_M9AppCompt) 4.dp else 16.dp
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        MessageHeader(
                            relative_M9AppCompt=relative_M9AppCompt,
                            relative_M17MessageVocale = relative_M17MessageVocale,
                            viewModel = viewModel,
                            clientName = clientName,
                            vendorName = vendorName,
                            messageVID = relative_M17MessageVocale.parentMessageVID,
                            timestamp = relative_M17MessageVocale.creationTimestamps,
                            datesHandler = datesHandler,
                            etatesChildKeyIDsList = list_D_EtateMessageVocale,
                            isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt,
                            isAdminMessage=its_Admin_Message,
                        )

                        // FIXED: BonVentInfoCard moved to the top after MessageHeader
                        relative_M8BonVent?.let { m8BonVent ->
                            Spacer(modifier = Modifier.height(12.dp))
                            BonVentInfoCard(
                                m8BonVent = m8BonVent,
                                isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt ,
                                isAdminMessage = its_Admin_Message,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // FIXED: Show appropriate UI based on current state
                        when (currentState) {
                            M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> {
                                RecordingIndicator(
                                    isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt,
                                    isAdminMessage = its_Admin_Message
                                )
                            }

                            M17MessageVocale.Etate.ENVOYER,
                            M17MessageVocale.Etate.VUE,
                            M17MessageVocale.Etate.ECOUTE -> {
                                AudioPlayerControls(
                                    parentD_EtateMessageVocale = relative_M17MessageVocale,
                                    viewModel = viewModel,
                                    audioHandler = audioHandler,
                                    isCurrentlyPlaying = isCurrentlyPlaying,
                                    isCurrentlyDownloading = isCurrentlyDownloading,
                                    playbackProgress = playbackProgress,
                                    isListened = isListened,
                                    latestTimestamp = latestTimestamp,
                                    datesHandler = datesHandler,
                                    context = context,
                                    coroutineScope = coroutineScope,
                                    isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt,
                                    isAdminMessage = its_Admin_Message
                                )
                            }

                            M17MessageVocale.Etate.Premier_Test_Envoi -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        }
                    }
                }
            }
        }

        // Divider
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

// Update RecordingIndicator function:
@Composable
private fun RecordingIndicator(
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean = false // Add this parameter
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = when {
            isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.2f)
            isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Enregistrement",
                tint = when {
                    isAdminMessage -> MaterialTheme.colorScheme.onError
                    isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.error
                },
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Enregistrement en cours...",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = when {
                    isAdminMessage -> MaterialTheme.colorScheme.onError
                    isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onErrorContainer
                }
            )
        }
    }
}
