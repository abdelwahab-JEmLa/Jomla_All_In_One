package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioHandlerInterface
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import java.io.File

// Enhanced audio state to better track audio availability
enum class AudioAvailabilityState {
    UNKNOWN,           // Initial state - need to check if audio exists
    CHECKING,          // Currently checking if audio file exists
    AVAILABLE,         // Audio file exists and ready to play
    NOT_AVAILABLE,     // Audio file doesn't exist, need to download
    DOWNLOADING,       // Currently downloading audio file
    DOWNLOAD_ERROR,    // Error occurred during download
    PLAYING,          // Audio is currently playing
    PLAYBACK_ERROR    // Error occurred during playback
}

@Composable
fun AudioPlayerControls(
    parentD_EtateMessageVocale: M17MessageVocale,
    viewModel: ViewModelMessageur,
    audioHandler: AudioRecorderAndPlayHandler,
    isCurrentlyPlaying: Boolean,
    isCurrentlyDownloading: Boolean,
    playbackProgress: AudioHandlerInterface.PlaybackProgress,
    isListened: Boolean,
    latestTimestamp: Long,
    datesHandler: DatesHandler,
    context: Context,
    coroutineScope: CoroutineScope,
    isFromActiveAccount: Boolean = false,
    isAdminMessage: Boolean = false
) {
    var audioAvailabilityState by remember { mutableStateOf(AudioAvailabilityState.UNKNOWN) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Check audio file availability when component loads
    LaunchedEffect(parentD_EtateMessageVocale.parentMessageVID) {
        audioAvailabilityState = AudioAvailabilityState.CHECKING

        withContext(Dispatchers.IO) {
            try {
                // First check if file exists locally
                val localFile = File(context.filesDir, "voice_${parentD_EtateMessageVocale.parentMessageVID}.3gp")

                if (localFile.exists() && localFile.length() > 0) {
                    audioAvailabilityState = AudioAvailabilityState.AVAILABLE
                } else {
                    // Check if we can download from Firebase (this simulates checking if file exists remotely)
                    val downloadResult = audioHandler.downloadAudioFileIfNeeded(context, parentD_EtateMessageVocale.parentMessageVID)

                    if (downloadResult.isSuccess) {
                        audioAvailabilityState = AudioAvailabilityState.AVAILABLE
                    } else {
                        audioAvailabilityState = AudioAvailabilityState.NOT_AVAILABLE
                        errorMessage = "Audio file not found"
                    }
                }
            } catch (e: Exception) {
                audioAvailabilityState = AudioAvailabilityState.NOT_AVAILABLE
                errorMessage = e.message
            }
        }
    }

    // Update state based on current playback status
    LaunchedEffect(isCurrentlyPlaying, isCurrentlyDownloading) {
        when {
            isCurrentlyPlaying -> audioAvailabilityState = AudioAvailabilityState.PLAYING
            isCurrentlyDownloading -> audioAvailabilityState = AudioAvailabilityState.DOWNLOADING
            else -> {
                // Only change to AVAILABLE if we're not in an error state
                if (audioAvailabilityState == AudioAvailabilityState.PLAYING ||
                    audioAvailabilityState == AudioAvailabilityState.DOWNLOADING) {
                    audioAvailabilityState = AudioAvailabilityState.AVAILABLE
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = when {
            isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.1f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced Play/Pause/Stop Button with better state handling
                EnhancedPlaybackButton(
                    audioAvailabilityState = audioAvailabilityState,
                    isAdminMessage = isAdminMessage,
                    onPlayClick = {
                        when (audioAvailabilityState) {
                            AudioAvailabilityState.AVAILABLE -> {
                                handleEnhancedPlaybackClick(
                                    audioHandler = audioHandler,
                                    parentD_EtateMessageVocale = parentD_EtateMessageVocale,
                                    isListened = isListened,
                                    viewModel = viewModel,
                                    datesHandler = datesHandler,
                                    context = context,
                                    coroutineScope = coroutineScope,
                                    onStateChange = { newState, error ->
                                        audioAvailabilityState = newState
                                        errorMessage = error
                                    }
                                )
                            }
                            AudioAvailabilityState.PLAYING -> {
                                coroutineScope.launch {
                                    val result = audioHandler.stopPlayback()
                                    if (result.isFailure) {
                                        audioAvailabilityState = AudioAvailabilityState.PLAYBACK_ERROR
                                        errorMessage = result.exceptionOrNull()?.message
                                    } else {
                                        audioAvailabilityState = AudioAvailabilityState.AVAILABLE
                                    }
                                }
                            }
                            AudioAvailabilityState.NOT_AVAILABLE -> {
                                // Try to re-check/download the file
                                audioAvailabilityState = AudioAvailabilityState.CHECKING
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        try {
                                            audioAvailabilityState = AudioAvailabilityState.DOWNLOADING
                                            val downloadResult = audioHandler.downloadAudioFileIfNeeded(context, parentD_EtateMessageVocale.parentMessageVID)

                                            if (downloadResult.isSuccess) {
                                                audioAvailabilityState = AudioAvailabilityState.AVAILABLE
                                                errorMessage = null
                                            } else {
                                                audioAvailabilityState = AudioAvailabilityState.DOWNLOAD_ERROR
                                                errorMessage = downloadResult.exceptionOrNull()?.message ?: "Download failed"
                                            }
                                        } catch (e: Exception) {
                                            audioAvailabilityState = AudioAvailabilityState.DOWNLOAD_ERROR
                                            errorMessage = e.message
                                        }
                                    }
                                }
                            }
                            else -> {
                                // For other states, do nothing or show error
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Enhanced Progress Section
                EnhancedProgressSection(
                    audioAvailabilityState = audioAvailabilityState,
                    playbackProgress = playbackProgress,
                    audioHandler = audioHandler,
                    isListened = isListened,
                    latestTimestamp = latestTimestamp,
                    datesHandler = datesHandler,
                    isAdminMessage = isAdminMessage,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

@Composable
private fun EnhancedPlaybackButton(
    audioAvailabilityState: AudioAvailabilityState,
    isAdminMessage: Boolean = false,
    onPlayClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = when {
            isAdminMessage -> when (audioAvailabilityState) {
                AudioAvailabilityState.PLAYING -> MaterialTheme.colorScheme.onError
                AudioAvailabilityState.DOWNLOAD_ERROR, AudioAvailabilityState.PLAYBACK_ERROR -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onError.copy(alpha = 0.9f)
            }
            else -> when (audioAvailabilityState) {
                AudioAvailabilityState.PLAYING -> MaterialTheme.colorScheme.error
                AudioAvailabilityState.DOWNLOADING, AudioAvailabilityState.CHECKING -> MaterialTheme.colorScheme.secondary
                AudioAvailabilityState.DOWNLOAD_ERROR, AudioAvailabilityState.PLAYBACK_ERROR -> MaterialTheme.colorScheme.error
                AudioAvailabilityState.NOT_AVAILABLE -> MaterialTheme.colorScheme.outline
                else -> MaterialTheme.colorScheme.primary
            }
        },
        modifier = Modifier.size(48.dp)
    ) {
        IconButton(
            onClick = onPlayClick,
            enabled = when (audioAvailabilityState) {
                AudioAvailabilityState.CHECKING, AudioAvailabilityState.DOWNLOADING -> false
                AudioAvailabilityState.DOWNLOAD_ERROR, AudioAvailabilityState.PLAYBACK_ERROR -> true // Allow retry
                else -> true
            }
        ) {
            when (audioAvailabilityState) {
                AudioAvailabilityState.CHECKING, AudioAvailabilityState.DOWNLOADING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
                AudioAvailabilityState.PLAYING -> {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Arrêter la lecture",
                        tint = if (isAdminMessage) MaterialTheme.colorScheme.error else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                AudioAvailabilityState.DOWNLOAD_ERROR, AudioAvailabilityState.PLAYBACK_ERROR -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Erreur - Appuyer pour réessayer",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                AudioAvailabilityState.NOT_AVAILABLE -> {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = "Télécharger l'audio",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Lecture du message vocal",
                        tint = if (isAdminMessage) MaterialTheme.colorScheme.error else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedProgressSection(
    audioAvailabilityState: AudioAvailabilityState,
    playbackProgress: AudioHandlerInterface.PlaybackProgress,
    audioHandler: AudioRecorderAndPlayHandler,
    isListened: Boolean,
    latestTimestamp: Long,
    datesHandler: DatesHandler,
    isAdminMessage: Boolean,
    errorMessage: String?
) {
    Column {
        // Progress Bar
        when (audioAvailabilityState) {
            AudioAvailabilityState.CHECKING, AudioAvailabilityState.DOWNLOADING -> {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )
            }
            AudioAvailabilityState.PLAYING -> {
                if (playbackProgress.duration > 0) {
                    LinearProgressIndicator(
                        progress = { playbackProgress.progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                }
            }
            AudioAvailabilityState.DOWNLOAD_ERROR, AudioAvailabilityState.PLAYBACK_ERROR -> {
                LinearProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.error,
                    trackColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                )
            }
            else -> {
                LinearProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Time and status display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Enhanced Time Display
            EnhancedTimeDisplay(
                audioAvailabilityState = audioAvailabilityState,
                playbackProgress = playbackProgress,
                audioHandler = audioHandler,
                errorMessage = errorMessage
            )

            // Status indicator
            StatusIndicator(
                isAdminMessage = isAdminMessage,
                isListened = isListened,
                latestTimestamp = latestTimestamp,
                datesHandler = datesHandler
            )
        }
    }
}

@Composable
private fun EnhancedTimeDisplay(
    audioAvailabilityState: AudioAvailabilityState,
    playbackProgress: AudioHandlerInterface.PlaybackProgress,
    audioHandler: AudioRecorderAndPlayHandler,
    errorMessage: String?
) {
    when (audioAvailabilityState) {
        AudioAvailabilityState.CHECKING -> {
            Text(
                text = "Vérification...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        AudioAvailabilityState.DOWNLOADING -> {
            Text(
                text = "Téléchargement...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        AudioAvailabilityState.PLAYING -> {
            if (playbackProgress.duration > 0) {
                Text(
                    text = "${audioHandler.formatTimeFromMillis(playbackProgress.currentPosition)} / ${
                        audioHandler.formatTimeFromMillis(playbackProgress.duration)
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Lecture en cours...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        AudioAvailabilityState.DOWNLOAD_ERROR, AudioAvailabilityState.PLAYBACK_ERROR -> {
            Text(
                text = errorMessage?.take(30) ?: "Erreur",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        AudioAvailabilityState.NOT_AVAILABLE -> {
            Text(
                text = "Audio non disponible",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        AudioAvailabilityState.AVAILABLE -> {
            Text(
                text = "Prêt à lire",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        AudioAvailabilityState.UNKNOWN -> {
            Text(
                text = "...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun StatusIndicator(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    isListened: Boolean,
    latestTimestamp: Long,
    datesHandler: DatesHandler,
    isAdminMessage: Boolean
) {
    val currentApp_Est_Admin = focusedValuesGetter.currentApp_Est_Admin

    (isAdminMessage || currentApp_Est_Admin).ifTrue {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isListened) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Message écouté",
                    tint = Color.Green,
                    modifier = Modifier.size(16.dp)
                )
                if (latestTimestamp > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = datesHandler.getDateAndTimString(latestTimestamp).time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Message non écouté",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Enhanced playback click handler with better error handling
private fun handleEnhancedPlaybackClick(
    audioHandler: AudioRecorderAndPlayHandler,
    parentD_EtateMessageVocale: M17MessageVocale,
    isListened: Boolean,
    viewModel: ViewModelMessageur,
    datesHandler: DatesHandler,
    context: Context,
    coroutineScope: CoroutineScope,
    onStateChange: (AudioAvailabilityState, String?) -> Unit
) {
    coroutineScope.launch {
        try {
            onStateChange(AudioAvailabilityState.CHECKING, null)

            val playResult = audioHandler.startPlayback(
                context = context,
                parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                onPlaybackComplete = {
                    onStateChange(AudioAvailabilityState.AVAILABLE, null)

                    if (!isListened) {
                        coroutineScope.launch {
                            try {
                                val newEtate = M17MessageVocale(
                                    parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                                    etate = M17MessageVocale.Etate.ECOUTE,
                                    creationTimestamps = datesHandler.getCurrentTimestamps(),
                                    parent_M9AppCompt_KeyID = parentD_EtateMessageVocale.parent_M9AppCompt_KeyID,
                                    parent_M9AppCompt_DebugInfos = parentD_EtateMessageVocale.parent_M9AppCompt_DebugInfos,
                                    relativeAuDataBase = parentD_EtateMessageVocale.relativeAuDataBase,
                                    parent_M8BonVent_KeyID = parentD_EtateMessageVocale.parent_M8BonVent_KeyID
                                )
                                viewModel.addOrUpdateData(newEtate)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                },
                onPlaybackError = { errorMessage ->
                    onStateChange(AudioAvailabilityState.PLAYBACK_ERROR, errorMessage)
                }
            )

            if (playResult.isSuccess) {
                onStateChange(AudioAvailabilityState.PLAYING, null)
            } else {
                val errorMessage = playResult.exceptionOrNull()?.message ?: "Unknown playback error"
                onStateChange(AudioAvailabilityState.PLAYBACK_ERROR, errorMessage)
            }

        } catch (e: Exception) {
            onStateChange(AudioAvailabilityState.PLAYBACK_ERROR, e.message)
        }
    }
}
