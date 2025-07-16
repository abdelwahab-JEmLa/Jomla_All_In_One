package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.Modules.DatesHandler
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
import androidx.compose.material.icons.filled.MoreVert
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
fun View_MainItem(
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    relative_M8BonVent: M8BonVent,
    repositorysMainGetter: RepositorysMainGetter=viewModel.aCentralFacade.repositorysMainGetter
) {
    val relative_M17Message = repositorysMainGetter.find_By_KeyID_M17MessageVocale(relative_M8BonVent.parent_M17Message_KeyID)

    // Check if M17Message has an original file to play
    val hasVoiceMessage = relative_M17Message?.nomDeSonOriginaleFichie != null &&
            relative_M17Message.nomDeSonOriginaleFichie != "null"

    val audioRecorderAndPlayHandler = viewModel.audioRecorderAndPlayHandler
    val datesHandler = DatesHandler()
    val etateActuellementEst = relative_M8BonVent.etateActuellementEst
    val activeM8BonVentId =
        viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.activeonVent_M8BonVent?.vid
    val blinkState = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val playbackProgress by audioRecorderAndPlayHandler.playbackProgress.collectAsState()

    // State for dropdown menu
    var showDropdownMenu by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf<M8BonVent.EtateActuellementEst?>(null) }

    val isCurrentlyPlaying = remember(
        playbackProgress.isPlaying,
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID
    ) {
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID == relative_M8BonVent.vid && playbackProgress.isPlaying
    }

    val isCurrentlyDownloading = remember(
        playbackProgress.isDownloading,
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID
    ) {
        audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID == relative_M8BonVent.vid && playbackProgress.isDownloading
    }

    LaunchedEffect(relative_M8BonVent.vid, isCurrentlyPlaying) {
        if (isCurrentlyPlaying) {
            try {
                while (isCurrentlyPlaying && audioRecorderAndPlayHandler.isPlaying()) {
                    audioRecorderAndPlayHandler.updatePlaybackProgress()
                    delay(100)
                    if (audioRecorderAndPlayHandler.getCurrentPlaybackSession()?.parentMessageVID != relative_M8BonVent.vid) {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(relative_M8BonVent.vid) {
        onDispose {
            try {
                val currentSession = audioRecorderAndPlayHandler.getCurrentPlaybackSession()
                if (currentSession?.parentMessageVID == relative_M8BonVent.vid) {
                    audioRecorderAndPlayHandler.stopPlayback()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
        && activeM8BonVentId != relative_M8BonVent.vid
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
            modifier = Modifier
                .getSemanticsTag(relative_M17Message,"")
                .fillMaxWidth()
        ) {
            // Delete button at top start
            IconButton(
                onClick = {
                    viewModel.aCentralFacade.repositorysMainSetter.delete_M8BonVent(
                        relative_M8BonVent
                    )

                    // Delete voice recording using the original file name if available
                    val audioKeyToDelete = if (hasVoiceMessage) {
                        relative_M17Message?.nomDeSonOriginaleFichie ?: ""
                    } else {
                        relative_M8BonVent.vocaleKeyID
                    }

                    viewModel.deleteVoiceRecordingFromStorage(audioKeyToDelete) { success ->
                        if (success) {
                            viewModel.getter.repo8BonVent.delete(relative_M8BonVent)
                        } else {
                            Toast.makeText(
                                context,
                                "Erreur lors de la suppression du message vocal",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

            // Status dropdown menu at top end
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display selected status next to the button
                selectedStatus?.let { status ->
                    Text(
                        text = status.nomArabe,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showDropdownMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options de statut",
                            tint = Color.White
                        )
                    }

                    StatusDropdownMenu(
                        relative_M8BonVent = relative_M8BonVent,
                        viewModel = viewModel,
                        expanded = showDropdownMenu,
                        onDismissRequest = { showDropdownMenu = false },
                        onStatusSelected = { status ->
                            selectedStatus = status
                            val updatedBonVent = relative_M8BonVent.copy(
                                etateActuellementEst = status
                            )
                            viewModel.getter.repo8BonVent.upsert(updatedBonVent)
                        }
                    )
                }
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
                    if (etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT) {
                        IconButton(
                            onClick = {
                                viewModel.openTransaction(relative_M8BonVent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Select Transaction",
                                tint = if (activeM8BonVentId == relative_M8BonVent.vid) {
                                    Color.White
                                } else {
                                    if (blinkState.value) Color.Red else Color.Gray
                                }
                            )
                        }
                    }

                    Text(
                        text = " الوقت: ${
                            datesHandler.getDateAndTimStringAvecSeconds(
                                relative_M8BonVent.creationTimestamps
                            ).time
                        }",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = etateActuellementEst.nomArabe,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )

                    Text(
                        text = relative_M8BonVent.keyID.takeLast(4),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }

                // Voice message player section - NOW USES M17Message original file
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
                                            val stopResult = audioRecorderAndPlayHandler.stopPlayback()
                                            if (stopResult.isFailure) {
                                                val errorMessage = "Erreur lors de l'arrêt: ${stopResult.exceptionOrNull()?.message}"
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        else -> {
                                            // Use the original file name from M17Message instead of vocaleKeyID
                                            val audioSource = relative_M17Message?.nomDeSonOriginaleFichie ?: ""
                                            val playResult = audioRecorderAndPlayHandler.startPlayback(
                                                context = context,
                                                parentMessageVID = relative_M8BonVent.vid,
                                                firebaseUrl = audioSource, // Use the original file name/path
                                                onPlaybackComplete = {
                                                    if (!relative_M8BonVent.sonVocaleEstEcoute) {
                                                        val currentTimestamp = datesHandler.getCurrentTimestamps()
                                                        viewModel.r_0_0_HeadOfRepositorys_SQL_Repository.upsertUneDataEtReturnVID(
                                                            relative_M8BonVent.copy(
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
                                                val errorMessage = "Erreur lors du démarrage: ${playResult.exceptionOrNull()?.message}"
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
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

                        // Show original file name
                        Text(
                            text = relative_M17Message?.nomDeSonOriginaleFichie?.let { fileName ->
                                if (fileName.length > 20) fileName.take(17) + "..." else fileName
                            } ?: "Fichier audio",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Progress indicator
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

                        // Status and time display
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

                                relative_M8BonVent.sonVocaleEstEcoute -> {
                                    Column {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Message écouté",
                                            tint = Color.Green,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        if (relative_M8BonVent.sonEcoutementEstFaitAutimestamps > 0) {
                                            Text(
                                                text = datesHandler.getDateAndTimString(
                                                    relative_M8BonVent.sonEcoutementEstFaitAutimestamps
                                                ).time,
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
