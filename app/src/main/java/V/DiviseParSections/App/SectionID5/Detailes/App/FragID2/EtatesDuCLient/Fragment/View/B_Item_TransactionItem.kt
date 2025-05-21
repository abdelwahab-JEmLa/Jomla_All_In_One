package V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.View

// Add these imports at the top of your file
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.ViewModel.ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.media.MediaPlayer
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
import androidx.compose.runtime.setValue
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

@Composable
fun B_Item_TransactionItem(
    transaction: C3_BonAchate,
    viewModel: ViewModel_AffichageHistoriquesTransactionsDeCetteJourParIdClient,
) {
    val datesHandler = DatesHandler()
    val etateActuellementEst = transaction.etateActuellementEst
    val activeTransactionId by viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState()

    // For blinking effect when not active
    val blinkState = remember { mutableStateOf(false) }

    // Audio player states
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableStateOf(0f) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Has voice message
    val hasVoiceMessage = transaction.vocaleKeyID.isNotEmpty()

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
                    // Check if there's a voice recording to delete
                    if (transaction.vocaleKeyID.isNotEmpty()) {
                        // Delete voice recording from storage first
                        viewModel.deleteVoiceRecordingFromStorage(transaction.vocaleKeyID) { success ->
                            if (success) {
                                // Then delete the transaction from database
                                viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.deleteData(
                                    transaction
                                )
                            } else {
                                // Show error message if voice deletion failed
                                Toast.makeText(
                                    context,
                                    "Erreur lors de la suppression du message vocal",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
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
                                viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.upsertUneDataEtReturnVID(
                                    transaction.copy(
                                        ouvert = !transaction.ouvert
                                    )
                                ) {
                                    viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.activeVId_1_3_TransactionCommercial.value =
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

                // Audio player sectionSqlRepository (only show if there's a voice message)
                if (hasVoiceMessage) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause Button
                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    // Stop playing
                                    mediaPlayer?.apply {
                                        if (isPlaying) {
                                            stop()
                                        }
                                        release()
                                    }
                                    mediaPlayer = null
                                    isPlaying = false
                                    playbackProgress = 0f
                                } else {
                                    // Start playing
                                    coroutineScope.launch {
                                        playVoiceMessage(
                                            transaction.vocaleKeyID,
                                            context,
                                            onPrepared = { player ->
                                                mediaPlayer = player
                                                isPlaying = true

                                                // Update progress every 100ms while playing
                                                coroutineScope.launch {
                                                    while (isPlaying && mediaPlayer != null) {
                                                        val duration = mediaPlayer?.duration ?: 1
                                                        val currentPosition =
                                                            mediaPlayer?.currentPosition ?: 0
                                                        playbackProgress =
                                                            currentPosition.toFloat() / duration.toFloat()
                                                        delay(100)
                                                    }
                                                }
                                            },
                                            onCompletion = {
                                                isPlaying = false
                                                playbackProgress = 0f
                                                mediaPlayer?.release()
                                                mediaPlayer = null

                                                // Update the transaction to mark voice message as listened
                                                if (!transaction.sonVocaleEstEcoute) {
                                                    // Get current timestamp
                                                    val currentTimestamp =
                                                        datesHandler.getCurrentTimestamps()

                                                    viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.upsertUneDataEtReturnVID(
                                                        transaction.copy(
                                                            sonVocaleEstEcoute = true,
                                                            sonEcoutementEstFaitAutimestamps = currentTimestamp
                                                        )
                                                    ) {}
                                                }
                                            },
                                            onError = {
                                                isPlaying = false
                                                playbackProgress = 0f
                                                Toast.makeText(
                                                    context,
                                                    "Erreur de lecture du message vocal",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Arrêter la lecture" else "Lecture du message vocal",
                                tint = Color.White
                            )
                        }

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { playbackProgress },
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )

                        // Show status icon and listened time
                        Column(
                            modifier = Modifier
                                .padding(start = 4.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            if (transaction.sonVocaleEstEcoute) {
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
