package V.DiviseParSections.App.SectionID6.Messager.App.FragID.Test

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Module.AudioRecorderAndPlayHandler
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.Models.D_EtateMessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorys
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update.addOrUpdateDatas
import Z_CodePartageEntreApps.Modules.DatesHandler
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun ButtonAjouteHistoriqueC3_BonAchate(
    masterRepositorys: A_MasterRepositorys = koinInject(),
    audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler = koinInject(),
    modifier: Modifier = Modifier,
    clientId: Long? = null,
    onVoiceMessageUploaded: (String) -> Unit = {},
    currentC3_BonAchate: C3_BonAchate?,
    findActiveComptVendeur: _1_5_Vendeur?
) {
    val d_EtateMessageVocaleRepository = masterRepositorys.d_EtateMessageVocaleRepository
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // States
    var isRecording by remember { mutableStateOf(false) }
    var recordingTimeSeconds by remember { mutableStateOf(0) }
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Request permission launcher
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

    // Clean up on dispose
    DisposableEffect(Unit) {
        onDispose {
            if (isRecording) {
                audioRecorderAndPlayHandler.forceCleanup()
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Recording timer display
        if (isRecording) {
            Text(
                text = audioRecorderAndPlayHandler.formatTime(recordingTimeSeconds),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }

        // Record/Stop button
        FilledTonalButton(
            onClick = {
                if (!hasRecordPermission) {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    return@FilledTonalButton
                }

                coroutineScope.launch {
                    if (isRecording) {
                        // Stop recording
                        try {
                            val stopResult = audioRecorderAndPlayHandler.stopRecording()

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

                            // Upload the recorded file
                            val parentMessageVID = clientId ?: System.currentTimeMillis()
                            val uploadResult = audioRecorderAndPlayHandler.uploadAudioFile(
                                recordedFile,
                                parentMessageVID
                            )

                            if (uploadResult.isSuccess) {
                                val downloadUrl = uploadResult.getOrThrow()

                                // Create and save the voice message record to database
                                val voiceMessageRecord = D_EtateMessageVocale(
                                    idParent_1_5_Vendeur = findActiveComptVendeur!!.vid,
                                    nomParent_1_5_Vendeur = findActiveComptVendeur.nom,
                                    parentMessageVID = parentMessageVID,
                                    nom = D_EtateMessageVocale.Nom.ENVOYER,
                                    timestamps = DatesHandler().getCurrentTimestamps(),
                                    relativeAuDataBase = if (currentC3_BonAchate != null) {
                                        D_EtateMessageVocale.RelativeAuDataBase.C3_BonAchate
                                    } else {
                                        D_EtateMessageVocale.RelativeAuDataBase.NONE
                                    },
                                    parentC3_BonAchateVID = currentC3_BonAchate?.vid ?: 0
                                )

                                d_EtateMessageVocaleRepository.addOrUpdateDatas(
                                    listOf(
                                        voiceMessageRecord
                                    )
                                )

                                onVoiceMessageUploaded(downloadUrl)
                                Toast.makeText(
                                    context,
                                    "Message vocal envoyé avec succès!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erreur lors de l'envoi: ${uploadResult.exceptionOrNull()?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Erreur: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            isRecording = false
                        }
                    } else {
                        // Start recording
                        try {
                            val parentMessageVID = clientId ?: System.currentTimeMillis()
                            val startResult = audioRecorderAndPlayHandler.startRecording(
                                context,
                                parentMessageVID,
                                currentTransaction = currentC3_BonAchate // Pass the transaction for this component
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

                            // Start the timer
                            recordingTimeSeconds = 0
                            while (isRecording) {
                                delay(1000)
                                recordingTimeSeconds++
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Erreur lors du démarrage: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            isRecording = false
                        }
                    }
                }
            },
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording) "Arrêter l'enregistrement" else "Commencer l'enregistrement"
            )
            Text(
                text = if (isRecording) "Arrêter l'enregistrement" else "Message vocal",
                fontSize = 10.sp
            )
        }
    }
}
