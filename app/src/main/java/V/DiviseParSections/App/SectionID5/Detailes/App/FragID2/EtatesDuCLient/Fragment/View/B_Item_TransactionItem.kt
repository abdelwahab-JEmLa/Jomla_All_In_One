package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun B_Item_TransactionItem(
    audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler = koinInject(),
    transaction: C3_BonAchate,
    viewModel: ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient,
) {
    val datesHandler = DatesHandler()
    val etateActuellementEst = transaction.etateActuellementEst
    val activeTransactionId by viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.activeVId_C3_BonAchate_Repository.collectAsState()

    // For blinking effect when not active
    val blinkState = remember { mutableStateOf(false) }

    // Audio player states using the centralized handler
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val playbackProgress by audioRecorderAndPlayHandler.playbackProgress.collectAsState()

    // Has voice message
    val hasVoiceMessage = transaction.vocaleKeyID.isNotEmpty()

    // Check if this specific transaction's voice message is currently playing
    val isCurrentlyPlaying = remember(playbackProgress.isPlaying, audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID == transaction.vid && playbackProgress.isPlaying
    }

    // Check if this message is currently downloading
    val isCurrentlyDownloading = remember(playbackProgress.isDownloading, audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID) {
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID == transaction.vid && playbackProgress.isDownloading
    }

    // Update progress periodically while playing
    LaunchedEffect(transaction.vid, isCurrentlyPlaying) {
        if (isCurrentlyPlaying) {
            try {
                Log.d("TransactionItem", "Starting progress update loop for transaction ${transaction.vid}")
                while (isCurrentlyPlaying && audioRecorderAndPlayHandler.isPlaying()) {
                    audioRecorderAndPlayHandler.updatePlaybackProgress()
                    delay(100)

                    // Safety check: break if session is no longer for this transaction
                    if (audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID != transaction.vid) {
                        Log.d("TransactionItem", "Breaking progress loop - session changed for transaction ${transaction.vid}")
                        break
                    }
                }
                Log.d("TransactionItem", "Progress update loop ended for transaction ${transaction.vid}")
            } catch (e: Exception) {
                Log.e("TransactionItem", "Error in progress update loop for transaction ${transaction.vid}", e)
                e.printStackTrace()
            }
        }
    }

    // Cleanup when leaving composition
    DisposableEffect(transaction.vid) {
        onDispose {
            try {
                // Only cleanup if this transaction is currently playing
                val currentSession = audioRecorderAndPlayHandler.getCurrentPlaybackSession()
                if (currentSession?.parentMessageVID == transaction.vid) {
                    Log.d("TransactionItem", "Cleaning up playback session for transaction ${transaction.vid}")
                    audioRecorderAndPlayHandler.stopPlayback()
                }
            } catch (e: Exception) {
                Log.e("TransactionItem", "Error during cleanup for transaction ${transaction.vid}", e)
                e.printStackTrace()
            }
        }
    }

    // Blinking effect using LaunchedEffect when not active and in ON_MODE_COMMEND_ACTUELLEMENT state
    if (etateActuellementEst == C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        && activeTransactionId != transaction.vid
    ) {
        LaunchedEffect(key1 = Unit) {
            while (true) {
                blinkState.value = !blinkState.value
                delay(500) // 500ms blink interval
            }
        }
    }

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
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Delete button at the top start
            IconButton(
                onClick = {
                    Log.d("TransactionItem", "Delete button clicked for transaction ${transaction.vid}")
                    // Check if there's a voice recording to delete
                    if (transaction.vocaleKeyID.isNotEmpty()) {
                        Log.d("TransactionItem", "Deleting voice recording ${transaction.vocaleKeyID} for transaction ${transaction.vid}")
                        // Delete voice recording from storage first
                        viewModel.deleteVoiceRecordingFromStorage(transaction.vocaleKeyID) { success ->
                            if (success) {
                                Log.d("TransactionItem", "Voice recording deleted successfully, now deleting transaction ${transaction.vid}")
                                // Then delete the transaction from database
                                viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.deleteData(
                                    transaction
                                )
                            } else {
                                Log.e("TransactionItem", "Failed to delete voice recording ${transaction.vocaleKeyID}")
                                // Show error message if voice deletion failed
                                Toast.makeText(
                                    context,
                                    "Erreur lors de la suppression du message vocal",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Log.d("TransactionItem", "No voice recording to delete, deleting transaction ${transaction.vid} directly")
                        // No voice recording to delete, just delete the transaction
                        viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.deleteData(transaction)
                    }
                },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer la transaction",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(top = 32.dp) // Add padding to avoid overlap with the delete button
            ) {
                // Top row with transaction details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shopping cart icon moved inside the Row
                    if (etateActuellementEst == C3_BonAchate.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT) {
                        IconButton(
                            onClick = {
                                Log.d("TransactionItem", "Shopping cart clicked for transaction ${transaction.vid}")
                                viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.upsertUneDataEtReturnVID(
                                    transaction.copy(
                                        ouvert = !transaction.ouvert
                                    )
                                ) {
                                    viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.activeVId_C3_BonAchate_Repository.value =
                                        transaction.vid

                                    // Navigate to the cart screen after selecting a transaction
                                    viewModel.navigateToCartScreen()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Select Transaction",
                                tint = if (activeTransactionId == transaction.vid) {
                                    Color.White
                                } else {
                                    // Blinking effect - alternate between Red and Gray
                                    if (blinkState.value) Color.Red else Color.Gray
                                }
                            )
                        }
                    }

                    Text(
                        text = " الوقت: ${datesHandler.getDateAndTimString(transaction.timestamps).time}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = etateActuellementEst.nomArabe,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End  // Right-aligned for Arabic
                    )
                }

                // Audio player section (only show if there's a voice message)
                if (hasVoiceMessage) {
                    Log.d("TransactionItem", "Displaying audio player for transaction ${transaction.vid} with vocaleKeyID: ${transaction.vocaleKeyID}")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Stop Button using centralized handler
                        IconButton(
                            onClick = {
                                Log.d("TransactionItem", "Audio play/stop button clicked for transaction ${transaction.vid}")
                                coroutineScope.launch {
                                    when {
                                        isCurrentlyPlaying -> {
                                            Log.d("TransactionItem", "Stopping playback for transaction ${transaction.vid}")
                                            // Stop current playback using the handler
                                            val stopResult = audioRecorderAndPlayHandler.stopPlayback()
                                            if (stopResult.isFailure) {
                                                val errorMessage = "Erreur lors de l'arrêt: ${stopResult.exceptionOrNull()?.message}"
                                                Log.e("TransactionItem", "Failed to stop playback for transaction ${transaction.vid}: ${stopResult.exceptionOrNull()?.message}")
                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Log.d("TransactionItem", "Playback stopped successfully for transaction ${transaction.vid}")
                                            }
                                        }
                                        else -> {
                                            Log.d("TransactionItem", "Starting playback for transaction ${transaction.vid}")
                                            // Start playback using the handler with Firebase URL
                                            val playResult = audioRecorderAndPlayHandler.startPlayback(
                                                context = context,
                                                parentMessageVID = transaction.vid,
                                                firebaseUrl = transaction.vocaleKeyID, // Pass the Firebase URL here
                                                onPlaybackComplete = {
                                                    Log.d("TransactionItem", "Playback completed for transaction ${transaction.vid}")
                                                    // Update the transaction to mark voice message as listened
                                                    if (!transaction.sonVocaleEstEcoute) {
                                                        Log.d("TransactionItem", "Marking voice message as listened for transaction ${transaction.vid}")
                                                        // Get current timestamp
                                                        val currentTimestamp = datesHandler.getCurrentTimestamps()

                                                        viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.upsertUneDataEtReturnVID(
                                                            transaction.copy(
                                                                sonVocaleEstEcoute = true,
                                                                sonEcoutementEstFaitAutimestamps = currentTimestamp
                                                            )
                                                        ) {}
                                                    }
                                                },
                                                onPlaybackError = { errorMessage ->
                                                    Log.e("TransactionItem", "Playback error for transaction ${transaction.vid}: $errorMessage")
                                                    Toast.makeText(
                                                        context,
                                                        "Erreur de lecture du message vocal: $errorMessage",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            )

                                            if (playResult.isFailure) {
                                                val errorMessage = "Erreur lors du démarrage: ${playResult.exceptionOrNull()?.message}"
                                                Log.e("TransactionItem", "Failed to start playback for transaction ${transaction.vid}: ${playResult.exceptionOrNull()?.message}")

                                                // Additional debug information
                                                Log.e("TransactionItem", "Debug info - Transaction VID: ${transaction.vid}")
                                                Log.e("TransactionItem", "Debug info - VocaleKeyID: ${transaction.vocaleKeyID}")
                                                Log.e("TransactionItem", "Debug info - HasVoiceMessage: $hasVoiceMessage")
                                                Log.e("TransactionItem", "Debug info - Exception details: ${playResult.exceptionOrNull()?.stackTrace?.joinToString("\n")}")

                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Log.d("TransactionItem", "Playback started successfully for transaction ${transaction.vid}")
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isCurrentlyDownloading // Disable button while downloading
                        ) {
                                Icon(
                                imageVector = when {
                                    isCurrentlyDownloading -> Icons.Default.PlayArrow // Show play icon while downloading
                                    isCurrentlyPlaying -> Icons.Default.Stop
                                    else -> Icons.Default.PlayArrow
                                },
                                contentDescription = when {
                                    isCurrentlyDownloading -> "Téléchargement en cours"
                                    isCurrentlyPlaying -> "Arrêter la lecture"
                                    else -> "Lecture du message vocal"
                                },
                                tint = Color.White
                            )
                        }

                        // Progress Bar using centralized handler progress
                        when {
                            isCurrentlyDownloading -> {
                                Log.d("TransactionItem", "Showing download progress for transaction ${transaction.vid}")
                                // Show indeterminate progress while downloading
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .padding(horizontal = 8.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.3f)
                                )
                            }
                            isCurrentlyPlaying && playbackProgress.duration > 0 -> {
                                // Show determinate progress while playing
                                LinearProgressIndicator(
                                    progress = { playbackProgress.progress.coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .padding(horizontal = 8.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.3f)
                                )
                            }
                            else -> {
                                // Show empty progress bar when not playing
                                LinearProgressIndicator(
                                    progress = { 0f },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .padding(horizontal = 8.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = Color.White.copy(alpha = 0.5f),
                                    trackColor = Color.White.copy(alpha = 0.2f)
                                )
                            }
                        }

                        // Show status icon and listened time
                        Column(
                            modifier = Modifier
                                .padding(start = 4.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            when {
                                isCurrentlyDownloading -> {
                                    Text(
                                        text = "Téléchargement...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                                isCurrentlyPlaying && playbackProgress.duration > 0 -> {
                                    // Show current time / total time
                                    Text(
                                        text = "${audioRecorderAndPlayHandler.formatTimeFromMillis(playbackProgress.currentPosition)} / ${audioRecorderAndPlayHandler.formatTimeFromMillis(playbackProgress.duration)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                                transaction.sonVocaleEstEcoute -> {
                                    // Show check mark if message has been listened to
                                    Column {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Message écouté",
                                            tint = Color.Green,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        // Show when the message was listened to
                                        if (transaction.sonEcoutementEstFaitAutimestamps > 0) {
                                            Text(
                                                text = datesHandler.getDateAndTimString(transaction.sonEcoutementEstFaitAutimestamps).time,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                                else -> {
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
                    }
                } else {
                    Log.d("TransactionItem", "No voice message to display for transaction ${transaction.vid}")
                }
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}
