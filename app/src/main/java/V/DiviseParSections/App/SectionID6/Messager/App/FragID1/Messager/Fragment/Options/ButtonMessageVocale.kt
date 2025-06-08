package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val datesHandler = remember { DatesHandler() }
    val audioHandler = viewModel.audioRecorderAndPlayHandler

    var isRecording by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableStateOf(0) }
    var currentRecordingEtate by remember { mutableStateOf<D_EtateMessageVocale?>(null) }
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

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                                            nom = D_EtateMessageVocale.Nom.ENVOYER,
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
                                val idParent_1_5_Vendeur =
                                    viewModel.masterRepositorys.e_GroupedDataBasesRepositoryProtoAvant3Juin.repositorys_Model.activeIdDe_1_5_Vendeur
                                val parentMessageVID = System.currentTimeMillis()
                                val originalFileName = "voice_${parentMessageVID}.3gp"

                                val newEtate = D_EtateMessageVocale(
                                    nomDeSonOriginaleFichie = originalFileName,
                                    idParent_1_5_Vendeur = idParent_1_5_Vendeur,
                                    nomParent_1_5_Vendeur = viewModel.masterRepositorys.e_GroupedDataBasesRepositoryProtoAvant3Juin.repositorys_Model.repository_1_5_Vendeur.modelDatasSnapList
                                        .find { it.vid == idParent_1_5_Vendeur }?.nom ?: "Non Trouve",
                                    parentMessageVID = parentMessageVID,
                                    nom = D_EtateMessageVocale.Nom.EN_COURT_ENREGESTREMENT,
                                    timestamps = datesHandler.getCurrentTimestamps()
                                )

                                viewModel.addOrUpdateData(newEtate)
                                currentRecordingEtate = newEtate

                                val startResult = audioHandler.startRecording(
                                    context,
                                    newEtate.parentMessageVID,
                                    currentTransaction = null
                                )

                                if (startResult.isFailure) {
                                    Toast.makeText(
                                        context,
                                        "Erreur lors du démarrage: ${startResult.exceptionOrNull()?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@launch
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
