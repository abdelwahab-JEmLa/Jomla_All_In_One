package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.D_EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun B_ItemMessagesVocale(
    parentD_EtateMessageVocale: D_EtateMessageVocale,
    etatesChildKeyIDsList: List<D_EtateMessageVocale>,
    viewModel: ViewModelMessageur,
) {
    // For audio playback
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val datesHandler = remember { DatesHandler() }
    val firebaseAudioHelper = remember { FirebaseAudioStorageHelper() }

    // Check message states
    val isListened = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.ECOUTE }
    val isViewed = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.VUE }
    val isBeingRecorded = etatesChildKeyIDsList.any {
        it.nom == D_EtateMessageVocale.Nom.EN_COURT_ENREGESTREMENT
    }
    val isSent = etatesChildKeyIDsList.any { it.nom == D_EtateMessageVocale.Nom.ENVOYER }

    // Get the latest state timestamp
    val latestTimestamp = etatesChildKeyIDsList.maxByOrNull { it.timestamps }?.timestamps ?: 0L

    // Cleanup MediaPlayer when leaving composition
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isListened -> MaterialTheme.colorScheme.primaryContainer
                isViewed -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.tertiaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Message header with message info and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Message vocal #${parentD_EtateMessageVocale.parentMessageVID}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "الوقت: ${datesHandler.getDateAndTimString(parentD_EtateMessageVocale.timestamps).time}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Show different UI based on message state
            if (isBeingRecorded && !isSent) {
                // Display "Recording in progress" text instead of player controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enregistrement en cours...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else if (isSent) {
                // Audio player controls - only show for sent messages
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play/Stop Button
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                // Stop playing
                                mediaPlayer?.apply {
                                    stop()
                                    release()
                                }
                                mediaPlayer = null
                                isPlaying = false
                                playbackProgress = 0f
                            } else {
                                // Start playing - with download if file doesn't exist
                                coroutineScope.launch {
                                    try {
                                        // Create audio file path
                                        val audioFileKey = "voice_${parentD_EtateMessageVocale.parentMessageVID}"
                                        val audioFile = File(context.filesDir, "$audioFileKey.3gp")

                                        // Check if file exists locally, if not download from Firebase
                                        if (!audioFile.exists()) {
                                            isDownloading = true
                                            Toast.makeText(
                                                context,
                                                "Téléchargement du message vocal...",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val downloadResult = firebaseAudioHelper.downloadAudioFile(
                                                context,
                                                parentD_EtateMessageVocale.parentMessageVID
                                            )

                                            isDownloading = false

                                            if (downloadResult.isFailure) {
                                                Toast.makeText(
                                                    context,
                                                    "Erreur lors du téléchargement: ${downloadResult.exceptionOrNull()?.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                return@launch
                                            }
                                        }

                                        // Now play the audio file
                                        if (audioFile.exists() && audioFile.length() > 0) {
                                            mediaPlayer = MediaPlayer().apply {
                                                setDataSource(audioFile.absolutePath)
                                                prepare()
                                                start()

                                                setOnCompletionListener {
                                                    isPlaying = false
                                                    playbackProgress = 0f
                                                    release()
                                                    mediaPlayer = null

                                                    // Update message state to ECOUTE if not already
                                                    if (!isListened) {
                                                        coroutineScope.launch {
                                                            try {
                                                                // Create a new D_EtateMessageVocale with ECOUTE state
                                                                val newEtate = D_EtateMessageVocale(
                                                                    parentMessageVID = parentD_EtateMessageVocale.parentMessageVID,
                                                                    nom = D_EtateMessageVocale.Nom.ECOUTE,
                                                                    timestamps = datesHandler.getCurrentTimestamps()
                                                                )
                                                                // Add the ECOUTE state to Firebase and local database
                                                                viewModel.addOrUpdateData(newEtate)
                                                            } catch (e: Exception) {
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                    }
                                                }

                                                setOnErrorListener { _, _, _ ->
                                                    isPlaying = false
                                                    playbackProgress = 0f
                                                    Toast.makeText(
                                                        context,
                                                        "Erreur de lecture du message vocal",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    true
                                                }
                                            }

                                            isPlaying = true

                                            // Update progress while playing
                                            coroutineScope.launch {
                                                while (isPlaying && mediaPlayer != null) {
                                                    try {
                                                        val duration = mediaPlayer?.duration ?: 1
                                                        val currentPosition = mediaPlayer?.currentPosition ?: 0
                                                        playbackProgress = currentPosition.toFloat() / duration.toFloat()
                                                        delay(100)
                                                    } catch (e: Exception) {
                                                        break
                                                    }
                                                }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Fichier audio non trouvé ou vide",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        isDownloading = false
                                        Toast.makeText(
                                            context,
                                            "Erreur lors du démarrage de la lecture: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        },
                        enabled = !isDownloading // Disable button while downloading
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Arrêter la lecture" else "Lecture du message vocal",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    // Progress Bar (shows download progress or playback progress)
                    LinearProgressIndicator(
                        progress = { if (isDownloading) 0f else playbackProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (isDownloading) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        trackColor = (if (isDownloading) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary).copy(alpha = 0.3f)
                    )

                    // Status icon and download indicator
                    Column(
                        modifier = Modifier.padding(start = 4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (isDownloading) {
                            Text(
                                text = "Téléchargement...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        } else if (isListened) {
                            // Show check mark if message has been listened to
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Message écouté",
                                tint = Color.Green,
                                modifier = Modifier.size(24.dp)
                            )
                            // Show when the message was listened to
                            if (latestTimestamp > 0) {
                                Text(
                                    text = datesHandler.getDateAndTimString(latestTimestamp).time,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        } else {
                            // Show warning if message has not been listened to
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Message non écouté",
                                tint = Color.Yellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            } else {
                // Message is in an unknown state
                Text(
                    text = "État du message inconnu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(8.dp)
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
