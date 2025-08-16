package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.clientjetpack.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ButtonMessageVocale(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    focusedValuesGetter: FocusedValuesGetter = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    repo17MessageVocale: Repo17MessageVocale = viewModel.aCentralFacade.repositorysMainGetter.repo17MessageVocale,
) {
    val relative_M17Message =
        focusedValuesGetter.active_Central_Values.active_OpnerDialog_M17MessageVocale

    val relative_M8BonVent = relative_M17Message?.parent_M8BonVent_KeyID?.let {
        repositorysMainGetter.find_M8BonVent(it)
    }

    val active_Current_M9AppCompt = aCentralFacade.focusedActiveValuesFacade
        .focusedValuesGetter
        .currentActive_M9AppCompt

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val datesHandler = remember { DatesHandler() }
    val audioHandler = viewModel.audioRecorderAndPlayHandler

    // Recording state with 30-second limit
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

    // Constants for recording limits
    val MAX_RECORDING_SECONDS = 30
    val WARNING_THRESHOLD_SECONDS = 25 // Start showing red at 25s (5s remaining)

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

    // Auto-stop recording when reaching max time
    LaunchedEffect(recordingTimeSeconds, isRecording) {
        if (isRecording && recordingTimeSeconds >= MAX_RECORDING_SECONDS) {
            // Auto-stop recording
            try {
                val stopResult = audioHandler.stopRecording()

                if (stopResult.isFailure) {
                    Toast.makeText(
                        context,
                        "Erreur lors de l'arrêt automatique: ${stopResult.exceptionOrNull()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@LaunchedEffect
                }

                isRecording = false
                recordingTimeSeconds = 0

                val recordedFile = stopResult.getOrThrow()

                Toast.makeText(
                    context,
                    "Enregistrement terminé automatiquement (30s max)",
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
                        val updatedEtate_Premier_Test_Envoi = etate.copy(
                            etate = M17MessageVocale.Etate.Premier_Test_Envoi,
                            creationTimestamps = datesHandler.getCurrentTimestamps()
                        )

                        repositorysMainSetter.upsert_M17MessageVocale(
                            updatedEtate_Premier_Test_Envoi
                        )

                        Toast.makeText(
                            context,
                            "Message vocal envoyé via Telegram!",
                            Toast.LENGTH_SHORT
                        ).show()

                        delay(2000)

                        val updatedEtate = etate.copy(
                            etate = M17MessageVocale.Etate.ENVOYER,
                            creationTimestamps = datesHandler.getCurrentTimestamps(),
                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                        )

                        repositorysMainSetter.upsert_M17MessageVocale(updatedEtate)
                        repositorysMainSetter.update_M8BonVent(relative_M8BonVent)

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
                    "Erreur lors de l'arrêt automatique: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                isRecording = false
                currentRecordingEtate = null
            }
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
            // Timer display with warning colors
            val timeColor = when {
                recordingTimeSeconds >= WARNING_THRESHOLD_SECONDS -> Color.Red
                recordingTimeSeconds >= 20 -> Color(0xFFFF9800) // Orange
                else -> MaterialTheme.colorScheme.onSurface
            }

            Text(
                text = "${audioHandler.formatTime(recordingTimeSeconds)} / ${audioHandler.formatTime(MAX_RECORDING_SECONDS)}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
                color = timeColor,
                fontWeight = if (recordingTimeSeconds >= WARNING_THRESHOLD_SECONDS) FontWeight.Bold else FontWeight.Normal
            )

            // Warning message when approaching limit
            if (recordingTimeSeconds >= WARNING_THRESHOLD_SECONDS) {
                val remainingSeconds = MAX_RECORDING_SECONDS - recordingTimeSeconds
                Text(
                    text = "Arrêt automatique dans ${remainingSeconds}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
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
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            relative_M8BonVent?.let { bonVent ->
                BonVentReplayCard(
                    bonVent = bonVent,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
            }

            val update_M8BonVent = relative_M8BonVent?.let { bonVent ->
                relative_M17Message.let { message ->
                    bonVent.copy(
                        parent_M17Message_KeyID = message.keyID,
                        parent_M17Message_DebugInfos = message.getDebugInfos(),
                    )
                }
            }

            // FAB with circular progress indicator
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Circular progress indicator when recording
                if (isRecording) {
                    val progress = recordingTimeSeconds.toFloat() / MAX_RECORDING_SECONDS
                    val strokeColor = when {
                        recordingTimeSeconds >= WARNING_THRESHOLD_SECONDS -> Color.Red
                        recordingTimeSeconds >= 20 -> Color(0xFFFF9800)
                        else -> Color(0xFF0088CC)
                    }

                    Canvas(
                        modifier = Modifier.size(72.dp) // Slightly larger than FAB
                    ) {
                        val strokeWidth = 6.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = Offset(size.width / 2, size.height / 2)

                        // Background circle
                        drawCircle(
                            color = strokeColor.copy(alpha = 0.2f),
                            radius = radius,
                            center = center,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )

                        // Progress arc (countdown - starts full and decreases)
                        val sweepAngle = (1f - progress) * 360f
                        drawArc(
                            color = strokeColor,
                            startAngle = -90f, // Start from top
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Round),
                            size = Size(radius * 2, radius * 2),
                            topLeft = Offset(center.x - radius, center.y - radius)
                        )

                        // Warning dots when in final 5 seconds
                        if (recordingTimeSeconds >= WARNING_THRESHOLD_SECONDS) {
                            val dotRadius = 3.dp.toPx()
                            val dotDistance = radius + strokeWidth + 8.dp.toPx()

                            // Animate dots position
                            val animationProgress = (System.currentTimeMillis() / 500L) % 8
                            for (i in 0 until 8) {
                                val angle = (i * 45f - 90f) * Math.PI / 180f // Convert to radians
                                val dotCenter = Offset(
                                    center.x + (dotDistance * cos(angle)).toFloat(),
                                    center.y + (dotDistance * sin(angle)).toFloat()
                                )

                                val alpha = if (i == (animationProgress % 8).toInt()) 1f else 0.3f
                                drawCircle(
                                    color = Color.Red.copy(alpha = alpha),
                                    radius = dotRadius,
                                    center = dotCenter
                                )
                            }
                        }
                    }
                }

                FloatingActionButton(
                    modifier = Modifier
                        .getSemanticsTag(repo17MessageVocale.datasValue, "repo17MessageVocale")
                        .getSemanticsTag(relative_M8BonVent, "relative_M8BonVent")
                        .getSemanticsTag(update_M8BonVent, "update_M8")
                        .getSemanticsTag(
                            relative_M17Message?.getDebugInfos(),
                            "relative_M17Message"
                        )
                        .size(56.dp),
                    onClick = {
                        if (relative_M17Message == null) {
                            Toast.makeText(
                                context,
                                "Aucun message vocal configuré",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@FloatingActionButton
                        }

                        if (!hasRecordPermission) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            return@FloatingActionButton
                        }

                        coroutineScope.launch {
                            if (!isRecording) {
                                try {
                                    val active_Current_M9AppCompt_KeyId =
                                        active_Current_M9AppCompt?.keyID ?: "null"

                                    val parentMessageVID = System.currentTimeMillis()
                                    val originalFileName = "voice_${parentMessageVID}.3gp"

                                    val default_M17Message = relative_M17Message.copy(
                                        nomDeSonOriginaleFichie = originalFileName,
                                        parent_M9AppCompt_KeyID = active_Current_M9AppCompt_KeyId,
                                        parent_M9AppCompt_DebugInfos = "Non Definie",
                                        parentMessageVID = parentMessageVID,
                                        etate = M17MessageVocale.Etate.ENVOYER,
                                        creationTimestamps = datesHandler.getCurrentTimestamps()
                                    )

                                    viewModel.addOrUpdateData(default_M17Message)
                                    currentRecordingEtate = default_M17Message

                                    val startResult = audioHandler.startRecording(
                                        context,
                                        default_M17Message.parentMessageVID,
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

                                    // Timer loop with max limit check
                                    while (isRecording && recordingTimeSeconds < MAX_RECORDING_SECONDS) {
                                        delay(1000)
                                        if (isRecording) {
                                            recordingTimeSeconds++
                                        }
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
                            } else {
                                // Manual stop recording
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
                                            val updatedEtate_Premier_Test_Envoi = etate.copy(
                                                etate = M17MessageVocale.Etate.Premier_Test_Envoi,
                                                creationTimestamps = datesHandler.getCurrentTimestamps()
                                            )

                                            repositorysMainSetter.upsert_M17MessageVocale(
                                                updatedEtate_Premier_Test_Envoi
                                            )

                                            Toast.makeText(
                                                context,
                                                "Message vocal envoyé via Telegram!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            delay(2000)

                                            val updatedEtate = etate.copy(
                                                etate = M17MessageVocale.Etate.ENVOYER,
                                                creationTimestamps = datesHandler.getCurrentTimestamps(),
                                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                            )

                                            repositorysMainSetter.upsert_M17MessageVocale(
                                                updatedEtate
                                            )
                                            repositorysMainSetter.update_M8BonVent(relative_M8BonVent)

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
                            }
                        }
                    },
                    containerColor = when {
                        isUploading -> Color(0xFF4CAF50)
                        isRecording && recordingTimeSeconds >= WARNING_THRESHOLD_SECONDS -> Color(0xFFFF1744) // Bright red for warning
                        isRecording -> Color(0xFFFF5722)
                        else -> Color(0xFF0088CC)
                    },
                    shape = CircleShape
                ) {
                    when {
                        isUploading -> Icon(
                            painter = painterResource(id = R.drawable.ic_telegram),
                            contentDescription = "Envoi en cours via Telegram",
                            tint = Color.White
                        )

                        isRecording -> {
                            if (recordingTimeSeconds >= WARNING_THRESHOLD_SECONDS) {
                                // Show countdown number for final seconds
                                val remainingSeconds = MAX_RECORDING_SECONDS - recordingTimeSeconds
                                Text(
                                    text = remainingSeconds.toString(),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_telegram_mic),
                                    contentDescription = "Enregistrement en cours - Appuyer pour arrêter",
                                    tint = Color.White
                                )
                            }
                        }

                        else -> Icon(
                            painter = painterResource(id = R.drawable.ic_telegram_send),
                            contentDescription = "Commencer l'enregistrement vocal",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BonVentReplayCard(
    bonVent: M8BonVent,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun getColorFromResource(colorRes: Int): Color {
        return try {
            Color(ContextCompat.getColor(context, colorRes))
        } catch (e: Exception) {
            when (colorRes) {
                android.R.color.holo_green_light -> Color(0xFF99CC00)
                android.R.color.holo_purple -> Color(0xFFAA66CC)
                android.R.color.holo_red_light -> Color(0xFFFF4444)
                android.R.color.holo_blue_dark -> Color(0xFF0099CC)
                android.R.color.darker_gray -> Color(0xFF444444)
                android.R.color.black -> Color(0xFF000000)
                android.R.color.holo_orange_dark -> Color(0xFFFF8800)
                R.color.c2 -> Color(0xFF6B73FF)
                R.color.couleur1 -> Color(0xFF9C27B0)
                else -> Color(0xFF6200EE)
            }
        }
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = getColorFromResource(bonVent.etateActuellementEst.color)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = bonVent.get_DebugInfos(),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = bonVent.etateActuellementEst.nomArabe,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "بداية: ${bonVent.heurDebutInString}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = "نهاية: ${bonVent.heurFinInString}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            if (bonVent.vocaleKeyID.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_telegram_mic),
                        contentDescription = "Voice message",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (bonVent.sonVocaleEstEcoute) "تم الاستماع" else "لم يتم الاستماع",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
