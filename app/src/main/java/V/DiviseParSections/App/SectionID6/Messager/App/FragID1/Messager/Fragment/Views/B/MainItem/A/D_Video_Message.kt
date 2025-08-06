package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.A

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun D_Video_Message(
    list_D_EtateMessageVocale: List<M17MessageVocale>,
    relative_M17MessageVocale: M17MessageVocale,
    viewModel: ViewModelMessageur = koinViewModel(),
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
) {
    val activeCurrent_M9AppCompt = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt
    val relative_M9AppCompt = repositorysMainGetter.find_M9AppCompt_By_KeyID(relative_M17MessageVocale.parent_M9AppCompt_KeyID)
    val relative_M8BonVent = repositorysMainGetter.find_M8BonVent(relative_M17MessageVocale.parent_M8BonVent_KeyID)

    val its_ViewMessage_Du_Active_M9AppCompt = relative_M9AppCompt?.keyID == activeCurrent_M9AppCompt?.keyID
    val its_Admin_Message = relative_M9AppCompt?.its_Admin ?: false

    val datesHandler = remember { DatesHandler() }

    val clientName = relative_M8BonVent?.parent_M2Client_DebugInfos ?: "Client inconnu"
    val vendorName = relative_M17MessageVocale.parent_M9AppCompt_DebugInfos.takeIf { it.isNotEmpty() } ?: "Vendeur inconnu"

    val currentState = list_D_EtateMessageVocale.lastOrNull()?.etate

    Column {
        // Main message card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (its_ViewMessage_Du_Active_M9AppCompt) {
                    Arrangement.End
                } else {
                    Arrangement.Start
                }
            ) {
                Card(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(
                                value = list_D_EtateMessageVocale
                                    .maxOfOrNull { it.parent_M9AppCompt_Nom } ?: "",
                                key = SemanticsPropertyKey("maxBy")
                            )
                        }
                        .semantics(mergeDescendants = true) {
                            set(
                                value = list_D_EtateMessageVocale.map {
                                    it.parent_M9AppCompt_Nom + "->" + it.etate
                                },
                                key = SemanticsPropertyKey("list_D_EtateMessageVocale")
                            )
                        }
                        .semantics(mergeDescendants = true) {
                            set(
                                SemanticsPropertyKey("relative_M17MessageVocale"),
                                relative_M17MessageVocale
                            )
                        }
                        .wrapContentWidth()
                        .padding(
                            start = if (its_ViewMessage_Du_Active_M9AppCompt) 40.dp else 0.dp,
                            end = if (its_ViewMessage_Du_Active_M9AppCompt) 0.dp else 40.dp
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            its_Admin_Message -> MaterialTheme.colorScheme.error
                            its_ViewMessage_Du_Active_M9AppCompt -> {
                                when (currentState) {
                                    M17MessageVocale.Etate.ECOUTE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                    M17MessageVocale.Etate.VUE -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                    M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    M17MessageVocale.Etate.ENVOYER -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                    M17MessageVocale.Etate.Premier_Test_Envoi -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    null -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                }
                            }
                            else -> {
                                when (currentState) {
                                    M17MessageVocale.Etate.ECOUTE -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                                    M17MessageVocale.Etate.VUE -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                    M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    M17MessageVocale.Etate.ENVOYER -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                                    M17MessageVocale.Etate.Premier_Test_Envoi -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    null -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                }
                            }
                        }
                    ),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (its_ViewMessage_Du_Active_M9AppCompt) 16.dp else 4.dp,
                        bottomEnd = if (its_ViewMessage_Du_Active_M9AppCompt) 4.dp else 16.dp
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        // Message Header
                        Text(
                            text = if (its_ViewMessage_Du_Active_M9AppCompt) vendorName else clientName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (its_Admin_Message) {
                                MaterialTheme.colorScheme.onError
                            } else if (its_ViewMessage_Du_Active_M9AppCompt) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Video Message Content
                        if (currentState != null) {
                            VideoMessageContent(
                                videoFileName = relative_M17MessageVocale.text_Inputted,
                                currentState = currentState,
                                isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt,
                                isAdminMessage = its_Admin_Message
                            )
                        }
                    }
                }
            }
        }

        // Divider
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun VideoMessageContent(
    videoFileName: String,
    currentState: M17MessageVocale.Etate,
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // FIXED: Enhanced video file name validation and normalization
    val normalizedVideoFileName = remember(videoFileName) {
        val fileName = videoFileName.trim()

        // Log the original filename for debugging
        android.util.Log.d("VideoMessage", "Original filename: '$fileName'")

        // Check if filename is empty or invalid
        if (fileName.isEmpty() || fileName.isBlank()) {
            android.util.Log.e("VideoMessage", "❌ Empty or blank video filename")
            return@remember null
        }

        // Ensure the filename has a proper video extension
        val normalizedName = if (!fileName.lowercase().endsWith(".mp4") &&
            !fileName.lowercase().endsWith(".mov") &&
            !fileName.lowercase().endsWith(".avi") &&
            !fileName.lowercase().endsWith(".webm")) {
            "$fileName.mp4" // Default to mp4 if no extension
        } else {
            fileName
        }

        android.util.Log.d("VideoMessage", "Normalized filename: '$normalizedName'")
        normalizedName
    }

    // Early return if filename is invalid
    if (normalizedVideoFileName == null) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Invalid filename",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nom de fichier vidéo invalide",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    // Initialize VideoDownloadManager
    val videoManager = remember { VideoDownloadManager(context) }

    // FIXED: Proper state management with better initial state detection
    var downloadState by remember {
        mutableStateOf(
            when {
                videoManager.isVideoDownloaded(normalizedVideoFileName) -> "DOWNLOADED"
                else -> "NOT_DOWNLOADED"
            }
        )
    }
    var downloadProgress by remember { mutableStateOf(0f) }
    var localVideoFile by remember {
        mutableStateOf(videoManager.getLocalVideoFile(normalizedVideoFileName))
    }
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var playbackState by remember { mutableStateOf(VideoPlaybackState()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Initialize ExoPlayer with enhanced error handling
    LaunchedEffect(Unit) {
        android.util.Log.d("VideoMessage", "🎬 Initializing ExoPlayer for: $normalizedVideoFileName")

        try {
            val newPlayer = ExoPlayer.Builder(context)
                .setLoadControl(
                    DefaultLoadControl.Builder()
                        .setBufferDurationsMs(
                            DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                            DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                        )
                        .build()
                )
                .build()

            newPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    val newPlaybackState = when (state) {
                        Player.STATE_READY -> {
                            logExoPlayerDiagnostics(newPlayer)
                            playbackState.copy(
                                duration = newPlayer.duration,
                                isPlaying = newPlayer.playWhenReady,
                                isBuffering = false,
                                hasError = false
                            )
                        }
                        Player.STATE_ENDED -> {
                            playbackState.copy(
                                isPlaying = false,
                                currentPosition = newPlayer.duration,
                                isBuffering = false
                            )
                        }
                        Player.STATE_BUFFERING -> {
                            playbackState.copy(
                                isBuffering = true,
                                hasError = false
                            )
                        }
                        Player.STATE_IDLE -> {
                            playbackState.copy(
                                isBuffering = false,
                                hasError = newPlayer.playWhenReady && newPlayer.playerError != null
                            )
                        }
                        else -> playbackState
                    }
                    playbackState = newPlaybackState
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    playbackState = playbackState.copy(isPlaying = playing)
                }

                override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                    android.util.Log.e("VideoPlayback", "❌ Player error: ${error.message}", error)

                    coroutineScope.launch {
                        runCompleteVideoDiagnostics(context, normalizedVideoFileName, localVideoFile, newPlayer)
                    }

                    val friendlyErrorMessage = when {
                        error.message?.contains("Source error") == true -> "Erreur de source vidéo"
                        error.message?.contains("Decoder") == true -> "Erreur de décodage vidéo"
                        error.message?.contains("Network") == true -> "Erreur de réseau"
                        else -> "Erreur de lecture: ${error.message}"
                    }

                    playbackState = playbackState.copy(
                        hasError = true,
                        errorMessage = friendlyErrorMessage,
                        isPlaying = false
                    )
                    errorMessage = friendlyErrorMessage
                }
            })

            exoPlayer = newPlayer
            android.util.Log.i("VideoMessage", "✅ ExoPlayer initialized successfully")

        } catch (e: Exception) {
            android.util.Log.e("VideoMessage", "❌ Failed to initialize ExoPlayer", e)
            errorMessage = "Erreur d'initialisation du lecteur: ${e.message}"
            playbackState = playbackState.copy(
                hasError = true,
                errorMessage = errorMessage
            )
        }
    }

    // FIXED: Enhanced video loading with better file validation
    LaunchedEffect(localVideoFile, exoPlayer) {
        val currentVideoFile = localVideoFile
        val currentPlayer = exoPlayer

        if (currentVideoFile != null && currentPlayer != null) {
            try {
                android.util.Log.i("VideoMessage", "🎬 Loading video file: ${currentVideoFile.absolutePath}")

                // FIXED: Enhanced file validation before loading
                if (!currentVideoFile.exists()) {
                    throw Exception("Le fichier vidéo n'existe pas: ${currentVideoFile.absolutePath}")
                }

                if (!currentVideoFile.isFile) {
                    throw Exception("Le chemin pointe vers un dossier, pas un fichier: ${currentVideoFile.absolutePath}")
                }

                if (currentVideoFile.length() == 0L) {
                    throw Exception("Le fichier vidéo est vide (0 bytes)")
                }

                if (!currentVideoFile.canRead()) {
                    throw Exception("Impossible de lire le fichier vidéo (permissions insuffisantes)")
                }

                // Detailed validation
                val validation = validateVideoFile(currentVideoFile)
                if (!validation.isValid) {
                    throw Exception("Fichier vidéo invalide: ${validation}")
                }


                // Create and set media item
                val fileUri = android.net.Uri.fromFile(currentVideoFile)
                val mediaItem = MediaItem.Builder()
                    .setUri(fileUri)
                    .setMediaId(normalizedVideoFileName)
                    .build()

                currentPlayer.clearMediaItems()
                currentPlayer.setMediaItem(mediaItem)
                currentPlayer.prepare()

                android.util.Log.i("VideoMessage", "✅ Video prepared successfully")
                errorMessage = null

            } catch (e: Exception) {
                android.util.Log.e("VideoMessage", "❌ Error loading video file", e)
                val friendlyError = when {
                    e.message?.contains("n'existe pas") == true -> "Fichier vidéo introuvable"
                    e.message?.contains("dossier") == true -> "Erreur de chemin de fichier"
                    e.message?.contains("vide") == true -> "Fichier vidéo corrompu"
                    e.message?.contains("permissions") == true -> "Accès au fichier refusé"
                    else -> "Erreur de chargement: ${e.message}"
                }

                errorMessage = friendlyError
                playbackState = playbackState.copy(
                    hasError = true,
                    errorMessage = friendlyError
                )
            }
        }
    }

    // Update position periodically when playing
    LaunchedEffect(playbackState.isPlaying, exoPlayer) {
        val currentPlayer = exoPlayer
        if (playbackState.isPlaying && currentPlayer != null) {
            while (playbackState.isPlaying && currentPlayer != null) {
                try {
                    val currentPos = currentPlayer.currentPosition
                    val duration = currentPlayer.duration

                    playbackState = playbackState.copy(
                        currentPosition = currentPos,
                        duration = if (duration > 0) duration else playbackState.duration
                    )

                    delay(500) // Update every 500ms

                } catch (e: Exception) {
                    android.util.Log.w("VideoMessage", "Error updating position: ${e.message}")
                    delay(1000)
                }
            }
        }
    }

    // Clean up ExoPlayer
    DisposableEffect(Unit) {
        onDispose {
            android.util.Log.i("VideoMessage", "🗑️ Disposing ExoPlayer")
            try {
                exoPlayer?.release()
            } catch (e: Exception) {
                android.util.Log.e("VideoMessage", "Error disposing ExoPlayer", e)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = when {
            isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.1f)
            else -> Color.Transparent
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Video player area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                when (downloadState) {
                    "NOT_DOWNLOADED" -> {
                        VideoDownloadPrompt(
                            videoFileName = normalizedVideoFileName,
                            onDownloadClick = {
                                Log.i("VideoMessage", "⬇️ Download button clicked for: $normalizedVideoFileName")
                                coroutineScope.launch {
                                    try {
                                        downloadState = "DOWNLOADING"
                                        errorMessage = null

                                        diagnoseFirebaseStorage(normalizedVideoFileName)

                                        videoManager.downloadVideo(
                                            videoFileName = normalizedVideoFileName,
                                            onSuccess = { file ->
                                                Log.i("VideoMessage", "✅ Download successful: ${file.absolutePath}")

                                                val validation = validateVideoFile(file)
                                                if (validation.isValid) {
                                                    localVideoFile = file
                                                    downloadState = "DOWNLOADED"
                                                    errorMessage = null
                                                } else {
                                                    Log.e("VideoMessage", "❌ Downloaded file validation failed: $validation")
                                                    downloadState = "ERROR"
                                                    errorMessage = "Fichier téléchargé invalide"
                                                }
                                            },
                                            onError = { error ->
                                                Log.e("VideoMessage", "❌ Download failed: $error")
                                                downloadState = "ERROR"
                                                errorMessage = error
                                            }
                                        )
                                    } catch (e: Exception) {
                                        Log.e("VideoMessage", "❌ Download error", e)
                                        downloadState = "ERROR"
                                        errorMessage = "Erreur de téléchargement: ${e.message}"
                                    }
                                }
                            },
                            isAdminMessage = isAdminMessage,
                            isFromActiveAccount = isFromActiveAccount
                        )
                    }

                    "DOWNLOADING" -> {
                        VideoDownloadingIndicator(
                            progress = downloadProgress,
                            isAdminMessage = isAdminMessage,
                            isFromActiveAccount = isFromActiveAccount
                        )
                    }

                    "DOWNLOADED" -> {
                        val currentPlayer = exoPlayer
                        if (currentPlayer != null && errorMessage == null) {
                            AndroidView(
                                factory = { ctx ->
                                    PlayerView(ctx).apply {
                                        player = currentPlayer
                                        useController = false
                                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                                    }
                                },
                                modifier = Modifier.fillMaxSize(),
                                update = { view ->
                                    view.player = currentPlayer
                                }
                            )
                        } else {
                            VideoErrorDisplay(
                                errorMessage = errorMessage ?: "Lecteur non initialisé",
                                onRetryClick = {
                                    Log.i("VideoMessage", "🔄 Retry loading video")
                                    errorMessage = null
                                    // Trigger re-loading by updating the file reference
                                    localVideoFile = videoManager.getLocalVideoFile(normalizedVideoFileName)
                                },
                                isAdminMessage = isAdminMessage,
                                isFromActiveAccount = isFromActiveAccount
                            )
                        }
                    }

                    "ERROR" -> {
                        VideoErrorIndicator(
                            errorMessage = errorMessage ?: "Erreur inconnue",
                            onRetryClick = {
                                Log.i("VideoMessage", "🔄 Retry button clicked")
                                downloadState = "NOT_DOWNLOADED"
                                errorMessage = null
                            },
                            isAdminMessage = isAdminMessage,
                            isFromActiveAccount = isFromActiveAccount
                        )
                    }
                }

                // Custom video controls overlay (when video is downloaded and playable)
                if (downloadState == "DOWNLOADED" && errorMessage == null) {
                    VideoControlsOverlay(
                        playbackState = playbackState,
                        onPlayPauseClick = {
                            val currentPlayer = exoPlayer
                            if (currentPlayer != null) {
                                if (playbackState.isPlaying) {
                                    currentPlayer.pause()
                                } else {
                                    currentPlayer.play()
                                }
                            }
                        },
                        onSeek = { position ->
                            Log.d("VideoMessage", "Seeking to position: $position")
                            exoPlayer?.seekTo(position)
                        },
                        isAdminMessage = isAdminMessage,
                        isFromActiveAccount = isFromActiveAccount
                    )
                }
            }

            // Show recording indicator for messages being composed
            if (currentState == M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT) {
                Spacer(modifier = Modifier.height(8.dp))
                VideoRecordingIndicator(
                    isFromActiveAccount = isFromActiveAccount,
                    isAdminMessage = isAdminMessage
                )
            }
        }
    }
}

@Composable
private fun VideoDownloadPrompt(
    videoFileName: String,
    onDownloadClick: () -> Unit,
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onDownloadClick,
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = when {
                        isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.8f)
                        isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        else -> MaterialTheme.colorScheme.primary
                    },
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download video",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Appuyez pour télécharger la vidéo",
            style = MaterialTheme.typography.bodySmall,
            color = when {
                isAdminMessage -> MaterialTheme.colorScheme.onError
                isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = videoFileName,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.8f
            ),
            color = when {
                isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.7f)
                isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            },
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun VideoDownloadingIndicator(
    progress: Float,
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(64.dp),
            color = when {
                isAdminMessage -> MaterialTheme.colorScheme.onError
                isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.primary
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Téléchargement... ${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = when {
                isAdminMessage -> MaterialTheme.colorScheme.onError
                isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun VideoErrorIndicator(
    errorMessage: String,
    onRetryClick: () -> Unit,
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onRetryClick,
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.error,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Retry download",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Erreur de téléchargement",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.8f
            ),
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Appuyez pour réessayer",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun VideoErrorDisplay(
    errorMessage: String,
    onRetryClick: () -> Unit,
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Video Error",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Erreur de lecture",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = errorMessage,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            onClick = onRetryClick,
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.2f)
        ) {
            Text(
                text = "Réessayer",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun VideoControlsOverlay(
    playbackState: VideoPlaybackState,
    onPlayPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean
) {
    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(showControls) {
        if (showControls && playbackState.isPlaying) {
            delay(3000) // Hide controls after 3 seconds when playing
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                showControls = !showControls
            }
    ) {
        if (showControls || playbackState.hasError) {
            // Play/Pause button in center
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                if (playbackState.hasError) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Playback Error",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = playbackState.errorMessage ?: "Erreur de lecture",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    IconButton(
                        onClick = onPlayPauseClick,
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Buffering indicator
            if (playbackState.isBuffering) {
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color.White
                    )
                }
            }

            // Progress bar at bottom
            if (!playbackState.hasError) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Color.Black.copy(alpha = 0.6f)
                        )
                        .padding(16.dp)
                ) {
                    if (playbackState.duration > 0) {
                        var progressBarSize by remember { mutableStateOf(IntSize.Zero) }

                        LinearProgressIndicator(
                            progress = { playbackState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .onSizeChanged { size ->
                                    progressBarSize = size
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures { offset: Offset ->
                                        if (progressBarSize.width > 0) {
                                            val clickedRatio = (offset.x / progressBarSize.width).coerceIn(0f, 1f)
                                            val seekPosition = (clickedRatio * playbackState.duration).toLong()
                                            onSeek(seekPosition)
                                        }
                                    }
                                },
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = playbackState.formattedCurrentTime,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = playbackState.formattedDuration,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        Text(
                            text = "Chargement de la vidéo...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoRecordingIndicator(
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = when {
            isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.2f)
            isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            else -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Stop,
                contentDescription = "Enregistrement vidéo",
                tint = when {
                    isAdminMessage -> MaterialTheme.colorScheme.onError
                    isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.error
                },
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Enregistrement vidéo en cours...",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = when {
                    isAdminMessage -> MaterialTheme.colorScheme.onError
                    isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

private enum class VideoDownloadState {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    ERROR
}


private fun getPlayerStateString(state: Int): String {
    return when (state) {
        Player.STATE_IDLE -> "IDLE"
        Player.STATE_BUFFERING -> "BUFFERING"
        Player.STATE_READY -> "READY"
        Player.STATE_ENDED -> "ENDED"
        else -> "UNKNOWN($state)"
    }
}
