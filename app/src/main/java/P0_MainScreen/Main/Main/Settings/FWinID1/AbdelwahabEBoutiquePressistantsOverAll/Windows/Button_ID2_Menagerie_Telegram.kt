package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
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
import androidx.compose.runtime.mutableStateOf
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
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    showLabels: Boolean,
) {
    // Track first display to prevent initial notifications
    var hasInitializedNotifications by remember { mutableStateOf(false) }

    val current_Compt_Et_Admin = focusedValuesGetter.currentApp_Est_Admin
    val currentAppComptKeyID = focusedValuesGetter.currentActive_M9AppCompt?.keyID
    val isDevMode = repositorysMainGetter.repo18CentralParametresOfAllApps.dataValue?.itsDevMode == false

    android.util.Log.d(
        "TelegramButton", """
        🏗️ Button Init:
        - Current Account KeyID: ${currentAppComptKeyID?.takeLast(4) ?: "null"}
        - Is Admin: $current_Compt_Et_Admin
        - Dev Mode: $isDevMode
    """.trimIndent()
    )

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
                etatesList.none {
                    it.etate == M17MessageVocale.Etate.ECOUTE
                }
            }
        }
    }

    // Separate count for messages from other accounts
    val messages_From_Other_Accounts_Size by remember(
        latestStatesForEachMessage,
        currentAppComptKeyID
    ) {
        derivedStateOf {
            val result = latestStatesForEachMessage.count { (latestMessage, etatesList) ->
                val isFromOtherAccount =
                    latestMessage.parent_M9AppCompt_KeyID != currentAppComptKeyID
                val isUnread = etatesList.none { it.etate == M17MessageVocale.Etate.ECOUTE }

                // Debug logs
                android.util.Log.d(
                    "TelegramButton", """
                    Message Debug:
                    - Message KeyID: ${latestMessage.keyID.takeLast(4)}
                    - Message Account ID: ${latestMessage.parent_M9AppCompt_KeyID?.takeLast(4) ?: "null"}
                    - Current Account ID: ${currentAppComptKeyID?.takeLast(4) ?: "null"}
                    - Is From Other Account: $isFromOtherAccount
                    - Is Unread: $isUnread
                    - Will Count: ${isFromOtherAccount && isUnread}
                """.trimIndent()
                )

                isFromOtherAccount && isUnread
            }

            android.util.Log.d("TelegramButton", "Messages from other accounts count: $result")
            result
        }
    }

    var previousMessageCount by remember { mutableIntStateOf(0) }
    var previousOtherAccountsCount by remember { mutableIntStateOf(0) }

    // Initialize previous counts on first composition to prevent initial notifications
    LaunchedEffect(Unit) {
        if (!hasInitializedNotifications) {
            previousMessageCount = non_Lu_Messages_Size
            previousOtherAccountsCount = messages_From_Other_Accounts_Size
            hasInitializedNotifications = true

            android.util.Log.d(
                "TelegramButton",
                "Initial setup - Previous counts set to: messages=$non_Lu_Messages_Size, others=$messages_From_Other_Accounts_Size"
            )
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun playNotificationSound(messageCount: Int) {
        android.util.Log.d(
            "TelegramButton",
            "🔊 Playing notification sound for $messageCount messages"
        )
        val soundDuration = 1200
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
                val basePattern = longArrayOf(0, 200, 100, 300, 100, 200)
                val repeatedPattern = mutableListOf<Long>().apply {
                    add(0)
                    repeat(messageCount) { index ->
                        if (index > 0) add(150)
                        addAll(basePattern.drop(1))
                    }
                }
                val effect = VibrationEffect.createWaveform(repeatedPattern.toLongArray(), -1)
                vibrator.vibrate(effect)
            } else {
                val basePattern = longArrayOf(0, 200, 100, 300, 100, 200)
                val repeatedPattern = mutableListOf<Long>().apply {
                    add(0)
                    repeat(messageCount) { index ->
                        if (index > 0) add(150)
                        addAll(basePattern.drop(1))
                    }
                }
                @Suppress("DEPRECATION")
                vibrator.vibrate(repeatedPattern.toLongArray(), -1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val mediaPlayer = MediaPlayer.create(context, R.raw.forest_bird)
            mediaPlayer?.let { player ->
                player.start()
                player.setOnCompletionListener { mp ->
                    mp.release()
                }
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

    // Handle notifications for current account (non-admin only) - only after initialization
    LaunchedEffect(non_Lu_Messages_Size, hasInitializedNotifications) {
        if (!hasInitializedNotifications) return@LaunchedEffect

        android.util.Log.d(
            "TelegramButton", """
            Current Account Notification Check:
            - Is Admin: $current_Compt_Et_Admin
            - New Message Count: $non_Lu_Messages_Size
            - Previous Count: $previousMessageCount
            - Should Play: ${!current_Compt_Et_Admin && non_Lu_Messages_Size > previousMessageCount}
        """.trimIndent()
        )

        if (!current_Compt_Et_Admin && non_Lu_Messages_Size > previousMessageCount) {
            val newMessages = non_Lu_Messages_Size - previousMessageCount
            android.util.Log.d(
                "TelegramButton",
                "Playing notification for $newMessages current account messages"
            )
            playNotificationSound(newMessages)
        }
        previousMessageCount = non_Lu_Messages_Size
    }

    // Handle notifications for messages from other accounts (always play) - only after initialization
    LaunchedEffect(messages_From_Other_Accounts_Size, hasInitializedNotifications) {
        if (!hasInitializedNotifications) return@LaunchedEffect

        android.util.Log.d(
            "TelegramButton", """
            Other Accounts Notification Check:
            - Messages from others: $messages_From_Other_Accounts_Size
            - Previous count: $previousOtherAccountsCount
            - Should Play: ${messages_From_Other_Accounts_Size > previousOtherAccountsCount}
        """.trimIndent()
        )

        if (messages_From_Other_Accounts_Size > previousOtherAccountsCount) {
            val newMessagesFromOthers =
                messages_From_Other_Accounts_Size - previousOtherAccountsCount
            android.util.Log.d(
                "TelegramButton",
                "Playing notification for $newMessagesFromOthers messages from other accounts"
            )
            playNotificationSound(newMessagesFromOthers)
        }
        previousOtherAccountsCount = messages_From_Other_Accounts_Size
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

    // Display total unread count (including messages from other accounts)
    val totalUnreadCount = non_Lu_Messages_Size + messages_From_Other_Accounts_Size

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box {
            FloatingActionButton(
                modifier = Modifier
                    .getSemanticsTag(current_Compt_Et_Admin, "current_Compt_Et_Admin")
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

            if (totalUnreadCount > 0 && !isDevMode ) {
                Box(
                    modifier = Modifier
                        .getSemanticsTag(isDevMode, "devMode")
                        .size(25.dp)
                        .offset(x = (-8).dp, y = (-10).dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = alpha))
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (totalUnreadCount > 99) "99+" else totalUnreadCount.toString(),
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
