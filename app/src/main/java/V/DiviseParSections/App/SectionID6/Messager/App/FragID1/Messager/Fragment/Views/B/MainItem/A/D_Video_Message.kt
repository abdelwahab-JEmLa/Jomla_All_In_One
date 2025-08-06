package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.A.VideoDownloadManager
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.A.VideoPlaybackState
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.Modules.DatesHandler
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel
import java.io.File

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
                    Arrangement.End // Messages from active account align to the right
                } else {
                    Arrangement.Start // Messages from others align to the left
                }
            ) {
                Card(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(
                                value = list_D_EtateMessageVocale
                                    .maxOf { it.parent_M9AppCompt_Nom },
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
                            // Admin messages get red background
                            its_Admin_Message -> MaterialTheme.colorScheme.error
                            // Regular styling for sent/received messages
                            its_ViewMessage_Du_Active_M9AppCompt -> {
                                // Green bubble for sent messages (like Telegram)
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
                                // Light gray bubble for received messages (like Telegram)
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
                        MessageHeader(
                            list_D_EtateMessageVocale=list_D_EtateMessageVocale,
                            relative_M9AppCompt = relative_M9AppCompt,
                            relative_M17MessageVocale = relative_M17MessageVocale,
                            viewModel = viewModel,
                            clientName = clientName,
                            vendorName = vendorName,
                            messageVID = relative_M17MessageVocale.parentMessageVID,
                            timestamp = relative_M17MessageVocale.creationTimestamps,
                            datesHandler = datesHandler,
                            etatesChildKeyIDsList = list_D_EtateMessageVocale,
                            isFromActiveAccount = its_ViewMessage_Du_Active_M9AppCompt,
                            isAdminMessage = its_Admin_Message,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Video Message Content
                        if (currentState != null) {
                            VideoMessageContent(
                                videoFileName = relative_M17MessageVocale.text_Inputted, // Assuming video filename is stored here
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
private fun VideoDownloadPrompt(
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
            text = "Erreur de téléchargement\nAppuyez pour réessayer",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
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

    // LOG: Video playback state debugging
    LaunchedEffect(playbackState) {
        android.util.Log.d("VideoPlayback", """
            |=== VIDEO PLAYBACK STATE ===
            |isPlaying: ${playbackState.isPlaying}
            |currentPosition: ${playbackState.currentPosition}ms (${playbackState.formattedCurrentTime})
            |duration: ${playbackState.duration}ms (${playbackState.formattedDuration})
            |progress: ${playbackState.progress}
            |isBuffering: ${playbackState.isBuffering}
            |hasError: ${playbackState.hasError}
            |errorMessage: ${playbackState.errorMessage}
            |showControls: $showControls
            |============================
        """.trimMargin())

        if (playbackState.hasError) {
            android.util.Log.e("VideoPlayback", "PLAYBACK ERROR: ${playbackState.errorMessage}")
        }

        if (playbackState.duration == 0L && !playbackState.isBuffering && !playbackState.hasError) {
            android.util.Log.w("VideoPlayback", "WARNING: Video duration is 0, media might not be loaded properly")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                showControls = !showControls
                android.util.Log.d("VideoControls", "Controls visibility toggled: $showControls")
            }
    ) {
        if (showControls) {
            // Play/Pause button in center
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                IconButton(
                    onClick = {
                        android.util.Log.d("VideoControls", "Play/Pause clicked - Current state: isPlaying=${playbackState.isPlaying}")
                        onPlayPauseClick()
                    },
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

            // Error indicator
            if (playbackState.hasError) {
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Erreur de lecture",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Progress bar at bottom
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
                    LinearProgressIndicator(
                        progress = { playbackState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .clickable { position ->       //->
                                //TODO(FIXME):Fix erreur Type mismatch.
                                //Required:
                                //() → Unit
                                //Found:
                                //(Any?) → Uni
                                // Allow seeking by clicking on progress bar
                                android.util.Log.d(
                                    "VideoControls",
                                    "Progress bar clicked for seeking"
                                )
                                // Note: This is simplified - you'd need to calculate the exact position based on click
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
                    // Show that duration is not available
                    Text(
                        text = "Loading video...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
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

// FIXED: Simplified File constructor usage
private suspend fun downloadVideoFromFirebase(
    videoFileName: String,
    context: Context,
    onProgressUpdate: (Float) -> Unit,
    onDownloadStart: () -> Unit,
    onDownloadComplete: (File) -> Unit,
    onDownloadError: (String) -> Unit
) {
    try {
        onDownloadStart()

        val storage = FirebaseStorage.getInstance()
        val videoRef = storage.reference.child("VideosMessages/$videoFileName")

        // Create local file - FIXED: Use explicit parent directory path
        val localDir = File(context.filesDir, "downloaded_videos")
        if (!localDir.exists()) {
            localDir.mkdirs()
        }
        val videoFile = File(localDir, videoFileName)

        // Download with progress tracking
        val downloadTask = videoRef.getFile(videoFile)

        downloadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat() / 100f
            onProgressUpdate(progress)
        }

        downloadTask.await()
        onDownloadComplete(videoFile)

    } catch (e: Exception) {
        onDownloadError(e.message ?: "Unknown error")
    }
}

private fun formatVideoTime(timeMs: Long): String {
    val seconds = (timeMs / 1000) % 60
    val minutes = (timeMs / (1000 * 60)) % 60
    val hours = (timeMs / (1000 * 60 * 60))

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

private enum class VideoDownloadState {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    ERROR
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

    // Initialize VideoDownloadManager
    val videoManager = remember { VideoDownloadManager(context) }

    var downloadState by remember {
        mutableStateOf(
            if (videoManager.isVideoDownloaded(videoFileName))
                VideoDownloadState.DOWNLOADED
            else
                VideoDownloadState.NOT_DOWNLOADED
        )
    }
    var downloadProgress by remember { mutableStateOf(0f) }
    var localVideoFile by remember { mutableStateOf(videoManager.getLocalVideoFile(videoFileName)) }
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var playbackState by remember { mutableStateOf(VideoPlaybackState()) }

    // LOG: Initial state
    LaunchedEffect(videoFileName) {
        android.util.Log.d("VideoMessage", """
            |=== INITIALIZING VIDEO MESSAGE ===
            |videoFileName: $videoFileName
            |currentState: $currentState
            |isFromActiveAccount: $isFromActiveAccount
            |isAdminMessage: $isAdminMessage
            |downloadState: $downloadState
            |localVideoFile exists: ${localVideoFile?.exists()}
            |localVideoFile path: ${localVideoFile?.absolutePath}
            |===================================
        """.trimMargin())
    }

    // Initialize ExoPlayer
    LaunchedEffect(Unit) {
        android.util.Log.d("VideoMessage", "Initializing ExoPlayer...")

        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    val stateString = when (state) {
                        Player.STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        Player.STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN($state)"
                    }

                    android.util.Log.d("VideoPlayback", """
                        |onPlaybackStateChanged: $stateString
                        |playWhenReady: $playWhenReady
                        |duration: $duration
                        |currentPosition: $currentPosition
                    """.trimMargin())

                    val newPlaybackState = when (state) {
                        Player.STATE_READY -> {
                            android.util.Log.i("VideoPlayback", "Video is ready to play - Duration: ${duration}ms")
                            playbackState.copy(
                                duration = duration,
                                isPlaying = playWhenReady,
                                isBuffering = false,
                                hasError = false
                            )
                        }
                        Player.STATE_ENDED -> {
                            android.util.Log.i("VideoPlayback", "Video playback ended")
                            playbackState.copy(
                                isPlaying = false,
                                currentPosition = 0L,
                                isBuffering = false
                            )
                        }
                        Player.STATE_BUFFERING -> {
                            android.util.Log.i("VideoPlayback", "Video is buffering...")
                            playbackState.copy(
                                isBuffering = true,
                                hasError = false
                            )
                        }
                        Player.STATE_IDLE -> {
                            val hasError = playWhenReady // Error if it was supposed to be playing
                            if (hasError) {
                                android.util.Log.e("VideoPlayback", "Player is IDLE but should be playing - likely an error")
                            }
                            playbackState.copy(
                                isBuffering = false,
                                hasError = hasError
                            )
                        }
                        else -> playbackState
                    }
                    playbackState = newPlaybackState
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    android.util.Log.d("VideoPlayback", "onIsPlayingChanged: $playing")
                    playbackState = playbackState.copy(isPlaying = playing)
                }

                override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                    android.util.Log.e("VideoPlayback", """
                        |PLAYER ERROR OCCURRED!
                        |Error type: ${error.errorCode}
                        |Error message: ${error.message}
                        |Cause: ${error.cause?.message}
                    """.trimMargin(), error)

                    playbackState = playbackState.copy(
                        hasError = true,
                        errorMessage = error.message,
                        isPlaying = false
                    )
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    android.util.Log.d("VideoPlayback", "onMediaItemTransition - mediaItem: ${mediaItem?.localConfiguration?.uri}")
                }

                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    val reasonString = when (reason) {
                        Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST -> "USER_REQUEST"
                        Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS -> "AUDIO_FOCUS_LOSS"
                        Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY -> "AUDIO_BECOMING_NOISY"
                        Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE -> "REMOTE"
                        else -> "UNKNOWN($reason)"
                    }
                    android.util.Log.d("VideoPlayback", "onPlayWhenReadyChanged: $playWhenReady, reason: $reasonString")
                }
            })
        }

        android.util.Log.i("VideoMessage", "ExoPlayer initialized successfully")
    }

    // Load video when file becomes available
    LaunchedEffect(localVideoFile, exoPlayer) {
        if (localVideoFile != null && exoPlayer != null) {
            try {
                android.util.Log.i("VideoMessage", "Loading video file: ${localVideoFile!!.absolutePath}")
                android.util.Log.d("VideoMessage", "File size: ${localVideoFile!!.length()} bytes")
                android.util.Log.d("VideoMessage", "File exists: ${localVideoFile.exists()}")
                android.util.Log.d("VideoMessage", "File can read: ${localVideoFile.canRead()}")

                val mediaItem = MediaItem.fromUri(localVideoFile.toURI().toString())
                android.util.Log.d("VideoMessage", "Created MediaItem with URI: ${mediaItem.localConfiguration?.uri}")

                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()

                android.util.Log.i("VideoMessage", "Video prepared successfully")

            } catch (e: Exception) {
                android.util.Log.e("VideoMessage", "Error loading video file", e)
                playbackState = playbackState.copy(
                    hasError = true,
                    errorMessage = "Failed to load video: ${e.message}"
                )
            }
        } else {
            android.util.Log.w("VideoMessage", "Cannot load video - localVideoFile: $localVideoFile, exoPlayer: $exoPlayer")
        }
    }

    // Update position periodically when playing
    LaunchedEffect(playbackState.isPlaying) {
        if (playbackState.isPlaying) {
            android.util.Log.d("VideoMessage", "Starting position update loop")
            while (playbackState.isPlaying && exoPlayer != null) {
                val currentPos = exoPlayer?.currentPosition ?: 0L
                playbackState = playbackState.copy(currentPosition = currentPos)
                delay(500) // Update every 500ms for smoother progress
            }
            android.util.Log.d("VideoMessage", "Position update loop stopped")
        }
    }

    // Clean up ExoPlayer
    DisposableEffect(Unit) {
        onDispose {
            android.util.Log.i("VideoMessage", "Disposing ExoPlayer")
            exoPlayer?.release()
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
                    VideoDownloadState.NOT_DOWNLOADED -> {
                        VideoDownloadPrompt(
                            onDownloadClick = {
                                android.util.Log.i("VideoMessage", "Download button clicked for: $videoFileName")
                                coroutineScope.launch {
                                    downloadState = VideoDownloadState.DOWNLOADING
                                    videoManager.downloadVideo(
                                        videoFileName = videoFileName,
                                        onSuccess = { file ->
                                            android.util.Log.i("VideoMessage", "Download successful: ${file.absolutePath}")
                                            localVideoFile = file
                                            downloadState = VideoDownloadState.DOWNLOADED
                                        },
                                        onError = { error ->
                                            android.util.Log.e("VideoMessage", "Download failed: $error")
                                            downloadState = VideoDownloadState.ERROR
                                        }
                                    )
                                }
                            },
                            isAdminMessage = isAdminMessage,
                            isFromActiveAccount = isFromActiveAccount
                        )
                    }

                    VideoDownloadState.DOWNLOADING -> {
                        VideoDownloadingIndicator(
                            progress = downloadProgress,
                            isAdminMessage = isAdminMessage,
                            isFromActiveAccount = isFromActiveAccount
                        )
                    }

                    VideoDownloadState.DOWNLOADED -> {
                        if (exoPlayer != null) {
                            android.util.Log.d("VideoMessage", "Rendering AndroidView for ExoPlayer")
                            AndroidView(
                                factory = { ctx ->
                                    android.util.Log.d("VideoMessage", "Creating PlayerView")
                                    PlayerView(ctx).apply {
                                        player = exoPlayer
                                        useController = false // We'll create custom controls
                                        android.util.Log.d("VideoMessage", "PlayerView configured")
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            android.util.Log.w("VideoMessage", "ExoPlayer is null, showing error")
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Player Error",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Player not initialized",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    VideoDownloadState.ERROR -> {
                        VideoErrorIndicator(
                            onRetryClick = {
                                android.util.Log.i("VideoMessage", "Retry button clicked")
                                downloadState = VideoDownloadState.NOT_DOWNLOADED
                            },
                            isAdminMessage = isAdminMessage,
                            isFromActiveAccount = isFromActiveAccount
                        )
                    }
                }

                // Custom video controls overlay (when video is downloaded)
                if (downloadState == VideoDownloadState.DOWNLOADED) {
                    VideoControlsOverlay(
                        playbackState = playbackState,
                        onPlayPauseClick = {
                            android.util.Log.d("VideoMessage", "Play/Pause clicked - current isPlaying: ${playbackState.isPlaying}")
                            if (playbackState.isPlaying) {
                                android.util.Log.d("VideoMessage", "Pausing video")
                                exoPlayer?.pause()
                            } else {
                                android.util.Log.d("VideoMessage", "Playing video")
                                exoPlayer?.play()
                            }
                        },
                        onSeek = { position ->
                            android.util.Log.d("VideoMessage", "Seeking to position: $position")
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

// Additional debugging and diagnostic functions for video playback issues

/**
 * Comprehensive video file validation
 */
fun validateVideoFile(file: File): VideoFileValidation {
    val validation = VideoFileValidation()

    try {
        android.util.Log.d("VideoValidation", "=== VALIDATING VIDEO FILE ===")
        android.util.Log.d("VideoValidation", "File path: ${file.absolutePath}")

        // Basic file checks
        validation.exists = file.exists()
        validation.canRead = file.canRead()
        validation.size = file.length()
        validation.isFile = file.isFile
        validation.parentExists = file.parentFile?.exists() ?: false

        android.util.Log.d("VideoValidation", "File exists: ${validation.exists}")
        android.util.Log.d("VideoValidation", "Can read: ${validation.canRead}")
        android.util.Log.d("VideoValidation", "Size: ${validation.size} bytes")
        android.util.Log.d("VideoValidation", "Is file: ${validation.isFile}")
        android.util.Log.d("VideoValidation", "Parent exists: ${validation.parentExists}")

        if (validation.exists && validation.size > 0) {
            // Try to determine file type
            val fileName = file.name.lowercase()
            validation.hasVideoExtension = fileName.endsWith(".mp4") ||
                    fileName.endsWith(".avi") ||
                    fileName.endsWith(".mov") ||
                    fileName.endsWith(".mkv") ||
                    fileName.endsWith(".webm")

            android.util.Log.d("VideoValidation", "Has video extension: ${validation.hasVideoExtension}")

            // Try to read first few bytes to check file header
            try {
                file.inputStream().use { input ->
                    val header = ByteArray(16)
                    val bytesRead = input.read(header)
                    validation.canReadContent = bytesRead > 0

                    // Check for common video file signatures
                    val headerHex = header.take(8).joinToString(" ") { "%02x".format(it) }
                    android.util.Log.d("VideoValidation", "File header (hex): $headerHex")

                    // MP4 signature check (ftyp)
                    val headerString = String(header, Charsets.ISO_8859_1)
                    validation.hasValidHeader = headerString.contains("ftyp") ||
                            headerString.contains("moov") ||
                            headerHex.startsWith("00 00 00") // Common MP4 start

                    android.util.Log.d("VideoValidation", "Can read content: ${validation.canReadContent}")
                    android.util.Log.d("VideoValidation", "Has valid header: ${validation.hasValidHeader}")
                }
            } catch (e: Exception) {
                android.util.Log.e("VideoValidation", "Error reading file content", e)
                validation.contentError = e.message
            }
        }

        validation.isValid = validation.exists &&
                validation.canRead &&
                validation.size > 0 &&
                validation.isFile &&
                validation.hasVideoExtension

        android.util.Log.d("VideoValidation", "Overall valid: ${validation.isValid}")
        android.util.Log.d("VideoValidation", "=============================")

    } catch (e: Exception) {
        android.util.Log.e("VideoValidation", "Error during validation", e)
        validation.validationError = e.message
    }

    return validation
}

data class VideoFileValidation(
    var exists: Boolean = false,
    var canRead: Boolean = false,
    var size: Long = 0L,
    var isFile: Boolean = false,
    var parentExists: Boolean = false,
    var hasVideoExtension: Boolean = false,
    var canReadContent: Boolean = false,
    var hasValidHeader: Boolean = false,
    var isValid: Boolean = false,
    var contentError: String? = null,
    var validationError: String? = null
)

/**
 * ExoPlayer diagnostic information
 */
fun logExoPlayerDiagnostics(player: ExoPlayer?) {
    if (player == null) {
        android.util.Log.w("ExoPlayerDiag", "Player is null!")
        return
    }

    android.util.Log.d("ExoPlayerDiag", """
        |=== EXOPLAYER DIAGNOSTICS ===
        |Player state: ${getPlayerStateString(player.playbackState)}
        |Play when ready: ${player.playWhenReady}
        |Is playing: ${player.isPlaying}
        |Current position: ${player.currentPosition}ms
        |Duration: ${player.duration}ms
        |Buffered position: ${player.bufferedPosition}ms
        |Buffered percentage: ${player.bufferedPercentage}%
        |Current media item: ${player.currentMediaItem?.localConfiguration?.uri}
        |Media items count: ${player.mediaItemCount}
        |Current window index: ${player.currentMediaItemIndex}
        |Playback parameters: speed=${player.playbackParameters.speed}, pitch=${player.playbackParameters.pitch}
        |Audio attributes: ${player.audioAttributes}
        |Video size: ${player.videoSize.width}x${player.videoSize.height}
        |Is loading: ${player.isLoading}
        |==============================
    """.trimMargin())

    // Log track information
    val trackGroups = player.currentTracks
    android.util.Log.d("ExoPlayerDiag", "Available tracks:")
    for (i in 0 until trackGroups.groups.size) {
        val group = trackGroups.groups[i]
        android.util.Log.d("ExoPlayerDiag", "  Group $i: type=${group.type}, length=${group.length}")
        for (j in 0 until group.length) {
            val format = group.getTrackFormat(j)
            android.util.Log.d("ExoPlayerDiag", "    Track $j: ${format.sampleMimeType}, ${format.width}x${format.height}")
        }
    }
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

/**
 * Firebase Storage diagnostic
 */
suspend fun diagnoseFirebaseStorage(videoFileName: String) {
    try {
        android.util.Log.d("FirebaseDiag", "=== FIREBASE STORAGE DIAGNOSTICS ===")
        android.util.Log.d("FirebaseDiag", "Video filename: $videoFileName")

        val storage = FirebaseStorage.getInstance()
        val videoRef = storage.reference.child("VideosMessages/$videoFileName")

        android.util.Log.d("FirebaseDiag", "Storage reference: ${videoRef.path}")
        android.util.Log.d("FirebaseDiag", "Full path: ${videoRef.toString()}")

        // Try to get metadata
        try {
            val metadata = videoRef.metadata.await()
            android.util.Log.d("FirebaseDiag", """
                |File metadata:
                |  Name: ${metadata.name}
                |  Size: ${metadata.sizeBytes} bytes
                |  Content type: ${metadata.contentType}
                |  Created: ${metadata.creationTimeMillis}
                |  Updated: ${metadata.updatedTimeMillis}
                |  MD5: ${metadata.md5Hash}
            """.trimMargin())
        } catch (e: Exception) {
            android.util.Log.e("FirebaseDiag", "Failed to get metadata", e)
        }

        // Try to get download URL
        try {
            val downloadUrl = videoRef.downloadUrl.await()
            android.util.Log.d("FirebaseDiag", "Download URL available: $downloadUrl")
        } catch (e: Exception) {
            android.util.Log.e("FirebaseDiag", "Failed to get download URL", e)
        }

        android.util.Log.d("FirebaseDiag", "====================================")

    } catch (e: Exception) {
        android.util.Log.e("FirebaseDiag", "Firebase diagnostics failed", e)
    }
}

/**
 * System diagnostics for video playback
 */
fun logSystemDiagnostics(context: Context) {
    android.util.Log.d("SystemDiag", """
        |=== SYSTEM DIAGNOSTICS ===
        |Android version: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})
        |Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}
        |Available memory: ${getAvailableMemory(context)}
        |Storage space: ${getAvailableStorage(context)}
        |Network connected: ${isNetworkConnected(context)}
        |===========================
    """.trimMargin())
}

private fun getAvailableMemory(context: Context): String {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    val memoryInfo = android.app.ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    return "${memoryInfo.availMem / (1024 * 1024)} MB"
}

private fun getAvailableStorage(context: Context): String {
    val stat = android.os.StatFs(context.filesDir.path)
    val availableBytes = stat.availableBytes
    return "${availableBytes / (1024 * 1024)} MB"
}

private fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
            networkCapabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
}

/**
 * Complete diagnostic function to call when video won't play
 */
suspend fun runCompleteVideoDiagnostics(
    context: Context,
    videoFileName: String,
    localVideoFile: File?,
    exoPlayer: ExoPlayer?
) {
    android.util.Log.i("VideoDiagnostics", "🔍 RUNNING COMPLETE VIDEO DIAGNOSTICS 🔍")

    // System diagnostics
    logSystemDiagnostics(context)

    // Firebase diagnostics
    diagnoseFirebaseStorage(videoFileName)

    // File validation
    localVideoFile?.let { file ->
        val validation = validateVideoFile(file)
        android.util.Log.i("VideoDiagnostics", "File validation result: $validation")
    } ?: android.util.Log.w("VideoDiagnostics", "No local video file to validate")

    // ExoPlayer diagnostics
    logExoPlayerDiagnostics(exoPlayer)

    android.util.Log.i("VideoDiagnostics", "🏁 DIAGNOSTICS COMPLETE 🏁")
}
