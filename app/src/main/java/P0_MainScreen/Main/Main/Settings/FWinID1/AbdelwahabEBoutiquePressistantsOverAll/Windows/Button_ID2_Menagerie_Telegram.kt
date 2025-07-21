package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientjetpack.R
import org.koin.compose.koinInject

@Composable
fun Button_ID2_Menagerie_Telegram(
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    showLabels: Boolean,
) {
    val repo17MessageVocaleData by aCentralFacade.repositorysMainGetter.repo17MessageVocale.datasValue.collectAsState()
    val context = LocalContext.current

    val groupedD_EtateMessageVocaleParParentMessage by remember(repo17MessageVocaleData) {
        derivedStateOf {
            repo17MessageVocaleData.groupBy { it.parentMessageVID }
        }
    }

    val latestStatesForEachMessage by remember(groupedD_EtateMessageVocaleParParentMessage) {
        derivedStateOf {
            groupedD_EtateMessageVocaleParParentMessage.mapNotNull { (parentMessageVID, etatesList) ->
                val sortedEtates = etatesList.sortedBy { it.creationTimestamps }
                val latestEtate = sortedEtates.firstOrNull()

                if (latestEtate != null) {
                    Pair(latestEtate, etatesList)
                } else null
            }.sortedBy { it.first.creationTimestamps }
        }
    }

    val non_Lu_Messages_Size by remember(latestStatesForEachMessage) {
        derivedStateOf {
            latestStatesForEachMessage.count { (_, etatesList) ->
                // Check if none of the messages in this group have "ECOUTE" state
                etatesList.none {
                    it.etate == M17MessageVocale.Etate.ECOUTE
                            && it.parent_M9AppCompt_KeyID == aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
                        .active_Current_M9AppCompt?.keyID
                }
            }
        }
    }

    var previousMessageCount by remember { mutableIntStateOf(non_Lu_Messages_Size) }

    @SuppressLint("ObsoleteSdkInt")
    fun playNotificationSound() {
        // Debug logs to understand the issue
        println("DEBUG: previousMessageCount = $previousMessageCount, non_Lu_Messages_Size = $non_Lu_Messages_Size")

        // Only play sound if the count has INCREASED (new messages arrived)
        if (non_Lu_Messages_Size <= previousMessageCount) {
            println("DEBUG: No sound played - count didn't increase")
            return
        }

        // Calculate sound duration: 700ms * number of new messages
        val numberOfNewMessages = non_Lu_Messages_Size - previousMessageCount
        val soundDuration = 700 * numberOfNewMessages

        println("DEBUG: Playing sound for $numberOfNewMessages new messages, duration: ${soundDuration}ms")

        // Add vibration with duration proportional to new messages
        try {
            val vibrator =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    val vibratorManager =
                        context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
                }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Dynamic vibration pattern based on number of messages
                val basePattern = longArrayOf(0, 200, 100, 300, 100, 200)
                val repeatedPattern = mutableListOf<Long>().apply {
                    add(0) // Initial delay
                    repeat(numberOfNewMessages) { index ->
                        if (index > 0) add(150) // Pause between repetitions
                        addAll(basePattern.drop(1)) // Add pattern without initial delay
                    }
                }
                val effect = VibrationEffect.createWaveform(repeatedPattern.toLongArray(), -1)
                vibrator.vibrate(effect)
            } else {
                // Legacy vibration - repeat pattern for each message
                val basePattern = longArrayOf(0, 200, 100, 300, 100, 200)
                val repeatedPattern = mutableListOf<Long>().apply {
                    add(0) // Initial delay
                    repeat(numberOfNewMessages) { index ->
                        if (index > 0) add(150) // Pause between repetitions
                        addAll(basePattern.drop(1)) // Add pattern without initial delay
                    }
                }
                @Suppress("DEPRECATION")
                vibrator.vibrate(repeatedPattern.toLongArray(), -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            // Use custom forest bird sound from res/raw/forest_bird.mp3
            val mediaPlayer = MediaPlayer.create(context, R.raw.forest_bird)
            mediaPlayer?.let { player ->
                player.start()

                // Stop playback after completion
                player.setOnCompletionListener { mp ->
                    mp.release()
                }

                // Schedule stop after calculated duration (700ms * number of new messages)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    try {
                        if (player.isPlaying) {
                            player.stop()
                        }
                        player.release()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, soundDuration.toLong())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to default notification sound if custom sound fails
            try {
                val fallbackPlayer = MediaPlayer.create(
                    context,
                    android.provider.Settings.System.DEFAULT_NOTIFICATION_URI
                )
                fallbackPlayer?.let { player ->
                    player.start()
                    player.setOnCompletionListener { mp ->
                        mp.release()
                    }
                }
            } catch (fallbackException: Exception) {
                fallbackException.printStackTrace()
            }
        }
    }

    LaunchedEffect(non_Lu_Messages_Size) {
        println("DEBUG: LaunchedEffect triggered - non_Lu_Messages_Size changed to $non_Lu_Messages_Size")

        // Play sound only when count increases
        if (non_Lu_Messages_Size > previousMessageCount) {
            playNotificationSound()
        }

        // Always update the previous count after checking
        previousMessageCount = non_Lu_Messages_Size
    }

    val infiniteTransition = rememberInfiniteTransition(label = "badge_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_animation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box {
            FloatingActionButton(
                modifier = Modifier
                    .getSemanticsTag(latestStatesForEachMessage, "latestStatesForEachMessage")
                    .getSemanticsTag(
                        repo17MessageVocaleData
                            .map { it.keyID.takeLast(4) }, "map"
                    )
                    .size(40.dp),
                onClick = {
                    focusedValuesGetter.update_activeCentralValues(
                        focusedValuesGetter.active_Central_Values.copy(
                            active_OpnerDialog_M17MessageVocale =
                                M17MessageVocale.get_default()
                        )
                    )
                },
                containerColor = Color(0xFF0088CC),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_telegram),
                    contentDescription = "Ouvrir Messager",
                    tint = Color.White
                )
            }

            if (non_Lu_Messages_Size > 0) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .offset(x = (-8).dp, y = (-10).dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = alpha)) // Animation d'opacité
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (non_Lu_Messages_Size > 99) "99+" else non_Lu_Messages_Size.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (showLabels) {
            Text(
                "Telegram Abdelwahab",
                modifier = Modifier
                    .background(Color(0xFF0088CC))
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
