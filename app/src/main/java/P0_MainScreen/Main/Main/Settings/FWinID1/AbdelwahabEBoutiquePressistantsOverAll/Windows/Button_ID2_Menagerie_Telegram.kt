package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
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
    var hasInitializedNotifications by remember { mutableStateOf(false) }

    val current_Compt_Et_Admin = focusedValuesGetter.currentApp_Est_Admin
    val currentAppComptKeyID = focusedValuesGetter.currentActive_M9AppCompt?.keyID
    val isDevMode = M18CentralParametresOfAllApps.get_Default().itsDevMode
    val active_Notifications = aCentralFacade.repositorysMainGetter.repo18CentralParametresOfAllApps.getNotificationSettings()

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

    val non_Lu_Messages_Current_Account_Size by remember(
        latestStatesForEachMessage,
        currentAppComptKeyID
    ) {
        derivedStateOf {
            latestStatesForEachMessage.count { (latestMessage, etatesList) ->
                val isFromCurrentAccount = latestMessage.parent_M9AppCompt_KeyID == currentAppComptKeyID
                val isUnread = etatesList.none { it.etate == M17MessageVocale.Etate.ECOUTE }

                isFromCurrentAccount && isUnread
            }
        }
    }

    val messages_From_Other_Accounts_Size by remember(
        latestStatesForEachMessage,
        currentAppComptKeyID
    ) {
        derivedStateOf {
            latestStatesForEachMessage.count { (latestMessage, etatesList) ->
                val isFromOtherAccount = latestMessage.parent_M9AppCompt_KeyID != currentAppComptKeyID
                val isUnread = etatesList.none { it.etate == M17MessageVocale.Etate.ECOUTE }

                isFromOtherAccount && isUnread
            }
        }
    }

    val hasImportantUnreadMessagesFromOtherAccounts by remember(
        latestStatesForEachMessage,
        currentAppComptKeyID
    ) {
        derivedStateOf {
            latestStatesForEachMessage.any { (latestMessage, etatesList) ->
                val isFromOtherAccount = latestMessage.parent_M9AppCompt_KeyID != currentAppComptKeyID
                val isUnread = etatesList.none { it.etate == M17MessageVocale.Etate.ECOUTE }
                val isImportant = latestMessage.ceMessage_Est_Important_Au_Ecoute

                isFromOtherAccount && isUnread && isImportant
            }
        }
    }

    var previousCurrentAccountCount by remember { mutableIntStateOf(0) }
    var previousOtherAccountsCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        if (!hasInitializedNotifications) {
            previousCurrentAccountCount = non_Lu_Messages_Current_Account_Size
            previousOtherAccountsCount = messages_From_Other_Accounts_Size
            hasInitializedNotifications = true
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun playNotificationSound(messageCount: Int) {
        if (!active_Notifications) return

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
                val fixedPattern = longArrayOf(0, 200, 100, 300, 100, 200, 100, 200)
                val effect = VibrationEffect.createWaveform(fixedPattern, -1)
                vibrator.vibrate(effect)
            } else {
                val fixedPattern = longArrayOf(0, 200, 100, 300, 100, 200, 100, 200)
                @Suppress("DEPRECATION")
                vibrator.vibrate(fixedPattern, -1)
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

    LaunchedEffect(non_Lu_Messages_Current_Account_Size, hasInitializedNotifications) {
        if (!hasInitializedNotifications) return@LaunchedEffect

        if (!current_Compt_Et_Admin && non_Lu_Messages_Current_Account_Size > previousCurrentAccountCount) {
            val newMessages = non_Lu_Messages_Current_Account_Size - previousCurrentAccountCount
            playNotificationSound(newMessages)
        }
        previousCurrentAccountCount = non_Lu_Messages_Current_Account_Size
    }

    LaunchedEffect(messages_From_Other_Accounts_Size, hasInitializedNotifications) {
        if (!hasInitializedNotifications) return@LaunchedEffect

        if (messages_From_Other_Accounts_Size > previousOtherAccountsCount) {
            val newMessagesFromOthers =
                messages_From_Other_Accounts_Size - previousOtherAccountsCount
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

    val importantAlertTransition = rememberInfiniteTransition(label = "important_alert_animation")
    val alertOffsetX by importantAlertTransition.animateFloat(
        initialValue = 15f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_offset_animation"
    )
    val alertScale by importantAlertTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_scale_animation"
    )
    val alertAlpha by importantAlertTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_alpha_animation"
    )

    val shouldShowBadge = messages_From_Other_Accounts_Size > 0 && !isDevMode
    val shouldShowImportantAlert = hasImportantUnreadMessagesFromOtherAccounts && !isDevMode

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

            if (shouldShowBadge) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .offset(x = (-8).dp, y = (-10).dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(Color.Red.copy(alpha = alpha))
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (messages_From_Other_Accounts_Size > 99) "99+" else messages_From_Other_Accounts_Size.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (shouldShowImportantAlert) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = (20 + alertOffsetX).dp,
                            y = (-8).dp
                        )
                        .scale(alertScale)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🖏️",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = alertAlpha),
                        modifier = Modifier
                            .background(
                                Color.Red.copy(alpha = 0.8f),
                                CircleShape
                            )
                            .padding(2.dp)
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
