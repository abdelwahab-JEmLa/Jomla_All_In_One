package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.clientjetpack.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ButtonMessageVocale(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
) {
    val relative_M8BonVent = focusedValuesGetter.active_Central_Values.replay_Passed_AuMessage_M8BonVent

    val active_Current_M9AppCompt = aCentralFacade.focusedActiveValuesFacade
        .focusedValuesGetter
        .active_Current_M9AppCompt

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val datesHandler = remember { DatesHandler() }
    val audioHandler = viewModel.audioRecorderAndPlayHandler

    var isRecording by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableStateOf(0) }
    var currentRecordingEtate by remember { mutableStateOf<M17MessageVocale?>(null) }
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordPermission = isGranted
        if (!isGranted) {
            Toast.makeText(
                context,
                "Permission d'enregistrement nécessaire pour cette fonctionnalité",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isRecording) {
                audioHandler.forceCleanup()
            }
        }
    }

    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isRecording) {
            Text(
                text = audioHandler.formatTime(recordingTimeSeconds),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        if (isUploading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "Envoi vers Telegram...",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        // TODO(1) Solution: Replay card for M8BonVent
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Replay Card for M8BonVent (left side)
            relative_M8BonVent?.let { bonVent ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(bonVent.etateActuellementEst.color).copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        // Header with status and debug info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = bonVent.get_DebugInfos(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )

                            // Status indicator
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        color = Color(bonVent.etateActuellementEst.color),
                                        shape = CircleShape
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Status name in Arabic
                        Text(
                            text = bonVent.etateActuellementEst.nomArabe,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(bonVent.etateActuellementEst.color),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Time info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Début: ${bonVent.heurDebutInString}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )

                            Text(
                                text = "Fin: ${bonVent.heurFinInString}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // Voice recording indicator if present
                        if (bonVent.vocaleKeyID.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_telegram_mic),
                                    contentDescription = "Voice message",
                                    tint = if (bonVent.sonVocaleEstEcoute) Color.Green else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = if (bonVent.sonVocaleEstEcoute) "Écouté" else "Non écouté",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (bonVent.sonVocaleEstEcoute) Color.Green else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // FAB (right side)
            FloatingActionButton(
                onClick = {
                    if (!hasRecordPermission) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        return@FloatingActionButton
                    }

                    coroutineScope.launch {
                        if (isRecording) {
                            try {
                                val stopResult = audioHandler.stopRecording()

                                if (stopResult.isFailure) {
                                    Toast.makeText(
                                        context,
                                        "Erreur lors de l'arrêt: ${stopResult.exceptionOrNull()?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
                                }

                                isRecording = false
                                recordingTimeSeconds = 0

                                val recordedFile = stopResult.getOrThrow()

                                Toast.makeText(
                                    context,
                                    "Enregistrement sauvegardé localement",
                                    Toast.LENGTH_SHORT
                                ).show()

                                currentRecordingEtate?.let { etate ->
                                    isUploading = true

                                    val uploadResult = audioHandler.uploadAudioFile(
                                        recordedFile,
                                        etate.parentMessageVID
                                    )

                                    isUploading = false

                                    if (uploadResult.isSuccess) {
                                        val updatedEtate = etate.copy(
                                            etate = M17MessageVocale.Etate.ENVOYER,
                                            timestamps = datesHandler.getCurrentTimestamps()
                                        )
                                        viewModel.addOrUpdateData(updatedEtate)

                                        Toast.makeText(
                                            context,
                                            "Message vocal envoyé via Telegram!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Erreur lors de l'envoi: ${uploadResult.exceptionOrNull()?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                currentRecordingEtate = null

                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Erreur lors de l'arrêt de l'enregistrement: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isRecording = false
                                currentRecordingEtate = null
                            }

                        } else {
                            try {
                                val active_Current_M9AppCompt_KeyId = active_Current_M9AppCompt?.keyID ?: "null"
                                val parentMessageVID = System.currentTimeMillis()
                                val originalFileName = "voice_${parentMessageVID}.3gp"

                                val newEtate = M17MessageVocale(
                                    nomDeSonOriginaleFichie = originalFileName,
                                    parent_M9AppCompt_KeyID = active_Current_M9AppCompt_KeyId,
                                    parent_M9AppCompt_DebugInfos = "Non Definie",
                                    parentMessageVID = parentMessageVID,
                                    etate = M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT,
                                    timestamps = datesHandler.getCurrentTimestamps()
                                )

                                viewModel.addOrUpdateData(newEtate)

                                currentRecordingEtate = newEtate

                                val startResult = newEtate?.let {
                                    audioHandler.startRecording(
                                        context,
                                        it.parentMessageVID,
                                        currentTransaction = null
                                    )
                                }

                                if (startResult != null) {
                                    if (startResult.isFailure) {
                                        Toast.makeText(
                                            context,
                                            "Erreur lors du démarrage: ${startResult.exceptionOrNull()?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@launch
                                    }
                                }

                                isRecording = true

                                recordingTimeSeconds = 0
                                while (isRecording) {
                                    delay(1000)
                                    recordingTimeSeconds++
                                }

                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Erreur lors du démarrage de l'enregistrement: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isRecording = false
                                currentRecordingEtate = null
                            }
                        }
                    }
                },
                modifier = Modifier.size(56.dp),
                containerColor = when {
                    isUploading -> Color(0xFF4CAF50)
                    isRecording -> Color(0xFFFF5722)
                    else -> Color(0xFF0088CC)
                },
            ) {
                when {
                    isUploading -> Icon(
                        painter = painterResource(id = R.drawable.ic_telegram_send),
                        contentDescription = "Envoi en cours via Telegram",
                        tint = Color.White
                    )
                    isRecording -> Icon(
                        painter = painterResource(id = R.drawable.ic_telegram_mic),
                        contentDescription = "Enregistrement en cours - Appuyer pour arrêter",
                        tint = Color.White
                    )
                    else -> Icon(
                        painter = painterResource(id = R.drawable.ic_telegram_mic),
                        contentDescription = "Commencer l'enregistrement vocal",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
