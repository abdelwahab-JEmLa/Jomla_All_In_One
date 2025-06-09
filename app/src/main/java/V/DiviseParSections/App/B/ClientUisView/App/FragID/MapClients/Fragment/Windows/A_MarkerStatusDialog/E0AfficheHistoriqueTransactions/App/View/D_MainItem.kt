package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.E0AfficheHistoriqueTransactions.App.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.DatesHandler
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
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
fun MainItem(
    audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler = koinInject(),
    transaction: C3_TransactionCommercial,
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    onClickToOpenTransaction: (C3_TransactionCommercial) -> Unit,
) {
    val datesHandler = DatesHandler()
    val etateActuellementEst = transaction.etateActuellementEst
    val activeTransactionId by viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.repositorys_Model.activeVId_C3_BonAchate_Repository.collectAsState()
    val blinkState = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val playbackProgress by audioRecorderAndPlayHandler.playbackProgress.collectAsState()
    val hasVoiceMessage = transaction.vocaleKeyID.isNotEmpty()

    val isCurrentlyPlaying = remember(
        playbackProgress.isPlaying,
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID
    ) {
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID == transaction.vid && playbackProgress.isPlaying
    }

    val isCurrentlyDownloading = remember(
        playbackProgress.isDownloading,
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID
    ) {
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID == transaction.vid && playbackProgress.isDownloading
    }

    LaunchedEffect(transaction.vid, isCurrentlyPlaying) {
        if (isCurrentlyPlaying) {
            try {
                while (isCurrentlyPlaying && audioRecorderAndPlayHandler.isPlaying()) {
                    audioRecorderAndPlayHandler.updatePlaybackProgress()
                    delay(100)
                    if (audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID != transaction.vid) {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(transaction.vid) {
        onDispose {
            try {
                val currentSession = audioRecorderAndPlayHandler.getCurrentPlaybackSession()
                if (currentSession?.parentMessageVID == transaction.vid) {
                    audioRecorderAndPlayHandler.stopPlayback()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        && activeTransactionId != transaction.vid
    ) {
        LaunchedEffect(key1 = Unit) {
            while (true) {
                blinkState.value = !blinkState.value
                delay(500)
            }
        }
    }

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
            IconButton(
                onClick = {
                    if (transaction.vocaleKeyID.isNotEmpty()) {
                        viewModel.deleteVoiceRecordingFromStorage(transaction.vocaleKeyID) { success ->
                            if (success) {
                                viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.deleteData(
                                    transaction
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erreur lors de la suppression du message vocal",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
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
                    .padding(top = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (etateActuellementEst == C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT) {
                        IconButton(
                            onClick = {
                                onClickToOpenTransaction(transaction)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Select Transaction",
                                tint = if (activeTransactionId == transaction.vid) {
                                    Color.White
                                } else {
                                    if (blinkState.value) Color.Red else Color.Gray
                                }
                            )
                        }
                    }

                    Text(
                        text = " الوقت: ${datesHandler.getDateAndTimStringAvecSeconds(transaction.timestamps).time}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = etateActuellementEst.nomArabe,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }

                if (hasVoiceMessage) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    when {
                                        isCurrentlyPlaying -> {
                                            val stopResult =
                                                audioRecorderAndPlayHandler.stopPlayback()
                                            if (stopResult.isFailure) {
                                                val errorMessage =
                                                    "Erreur lors de l'arrêt: ${stopResult.exceptionOrNull()?.message}"
                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                        else -> {
                                            val playResult =
                                                audioRecorderAndPlayHandler.startPlayback(
                                                    context = context,
                                                    parentMessageVID = transaction.vid,
                                                    firebaseUrl = transaction.vocaleKeyID,
                                                    onPlaybackComplete = {
                                                        if (!transaction.sonVocaleEstEcoute) {
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
                                                    onPlaybackError = { errorMessage ->
                                                        Toast.makeText(
                                                            context,
                                                            "Erreur de lecture du message vocal: $errorMessage",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                )

                                            if (playResult.isFailure) {
                                                val errorMessage =
                                                    "Erreur lors du démarrage: ${playResult.exceptionOrNull()?.message}"
                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !isCurrentlyDownloading
                        ) {
                            Icon(
                                imageVector = when {
                                    isCurrentlyDownloading -> Icons.Default.PlayArrow
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

                        when {
                            isCurrentlyDownloading -> {
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

                        Column(
                            modifier = Modifier.padding(start = 4.dp),
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
                                    Text(
                                        text = "${
                                            audioRecorderAndPlayHandler.formatTimeFromMillis(
                                                playbackProgress.currentPosition
                                            )
                                        } / ${
                                            audioRecorderAndPlayHandler.formatTimeFromMillis(
                                                playbackProgress.duration
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }

                                transaction.sonVocaleEstEcoute -> {
                                    Column {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Message écouté",
                                            tint = Color.Green,
                                            modifier = Modifier.size(24.dp)
                                        )
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
    }

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}
