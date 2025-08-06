package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options.FabButtonsMessageurMainScreen
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.A.VideoDownloadManager
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.B_ItemMessagesVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.D_Video_Message
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.MessageHeader
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.clientjetpack.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MessageurTelegram_MainScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur = koinViewModel(),
    repo17MessageVocale: Repo17MessageVocale = viewModel.aCentralFacade.repositorysMainGetter.repo17MessageVocale,
    onDismiss: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showDialog = true
    }

    Box(modifier = modifier.fillMaxSize()) {

        if (showDialog) {
            Dialog(
                onDismissRequest = {
                    showDialog = false
                    onDismiss()
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true,
                    usePlatformDefaultWidth = false
                )
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFADD8E6)) // Bleu clair
                    ) {
                        // Background image
                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
                            painter = painterResource(id = R.drawable.background_mess),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )

                        // Content overlay
                        Box(modifier = Modifier.padding(16.dp)) {
                            MessageurDialogContent(
                                viewModel = viewModel,
                                onDismiss = {
                                    showDialog = false
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageurDialogContent(
    viewModel: ViewModelMessageur,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box {
        MainFilter(uiState = uiState, viewModel = viewModel)
        // Display FAB buttons at the bottom
        FabButtonsMessageurMainScreen(viewModel)
    }
}

@Composable
fun MainFilter(
    modifier: Modifier = Modifier,
    uiState: UiState,
    viewModel: ViewModelMessageur
) {
    val sortedUiState = remember(uiState.d_EtateMessageVocaleList) {
        uiState.copy(
            d_EtateMessageVocaleList = uiState.d_EtateMessageVocaleList.sortedBy { it.keyID }
        )
    }

    MainList(uiState = sortedUiState, viewModel = viewModel)
}

@Composable
fun MainList(
    modifier: Modifier = Modifier,
    uiState: UiState,
    viewModel: ViewModelMessageur
) {
    val listState = rememberLazyListState()

    val groupedD_EtateMessageVocaleParParentMessage by remember(uiState.d_EtateMessageVocaleList) {
        derivedStateOf {
            uiState.d_EtateMessageVocaleList.groupBy { it.parentMessageVID }
        }
    }

    val latestStatesForEachMessage by remember(
        groupedD_EtateMessageVocaleParParentMessage
    ) {
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

    // Scroll to the last item when the list changes and at startup
    LaunchedEffect(latestStatesForEachMessage.size) {
        if (latestStatesForEachMessage.isNotEmpty()) {
            listState.scrollToItem(latestStatesForEachMessage.size - 1)
        }
    }

    // Scroll to bottom at startup
    LaunchedEffect(Unit) {
        if (latestStatesForEachMessage.isNotEmpty()) {
            listState.scrollToItem(latestStatesForEachMessage.size - 1)
        }
    }

    List_Messages(listState, latestStatesForEachMessage, viewModel, uiState)
}
@Composable
private fun List_Messages(
    listState: LazyListState,
    latestStatesForEachMessage: List<Pair<M17MessageVocale, List<M17MessageVocale>>>,
    viewModel: ViewModelMessageur,
    uiState: UiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp),
        state = listState
    ) {
        items(latestStatesForEachMessage) { (latestEtate, allEtatesForMessage) ->
            when {
                latestEtate.its_Text_Message -> {
                    C_Text_Message(
                        list_D_EtateMessageVocale = allEtatesForMessage,
                        relative_M17MessageVocale = latestEtate,
                    )
                }
                latestEtate.its_Video_Message -> {
                    // Check if video download is in progress
                    val isDownloading = checkVideoDownloadState(latestEtate, viewModel)
                    if (isDownloading) {
                        VideoLoadingMessage(
                            list_D_EtateMessageVocale = allEtatesForMessage,
                            relative_M17MessageVocale = latestEtate,
                            viewModel = viewModel
                        )
                    } else {
                        D_Video_Message(
                            list_D_EtateMessageVocale = allEtatesForMessage,
                            relative_M17MessageVocale = latestEtate,
                            viewModel = viewModel
                        )
                    }
                }
                else -> {
                    // Audio message - check if download is in progress
                    val isDownloading = checkAudioDownloadState(latestEtate, viewModel)
                    if (isDownloading) {
                        AudioLoadingMessage(
                            list_D_EtateMessageVocale = allEtatesForMessage,
                            relative_M17MessageVocale = latestEtate,
                            viewModel = viewModel,
                            uiState = uiState
                        )
                    } else {
                        B_ItemMessagesVocale(
                            list_D_EtateMessageVocale = allEtatesForMessage,
                            viewModel = viewModel,
                            relative_M17MessageVocale = latestEtate,
                            uiState = uiState
                        )
                    }
                }
            }
        }
    }
}

// Helper function to check video download state
@Composable
private fun checkVideoDownloadState(
    message: M17MessageVocale,
    viewModel: ViewModelMessageur
): Boolean {
    val context = LocalContext.current
    val videoManager = remember { VideoDownloadManager(context) }
    val videoFileName = message.text_Inputted

    return !videoManager.isVideoDownloaded(videoFileName) &&
            message.etate == M17MessageVocale.Etate.ENVOYER
}

// Helper function to check audio download state
@Composable
private fun checkAudioDownloadState(
    message: M17MessageVocale,
    viewModel: ViewModelMessageur
): Boolean {
    val audioHandler = viewModel.audioRecorderAndPlayHandler
    val playbackProgress by audioHandler.playbackProgress.collectAsState()

    // Check if this message is currently downloading
    return audioHandler.getCurrentPlaybackSession()?.parentMessageVID == message.parentMessageVID &&
            playbackProgress.isDownloading
}

// Loading component for video messages
@Composable
private fun VideoLoadingMessage(
    list_D_EtateMessageVocale: List<M17MessageVocale>,
    relative_M17MessageVocale: M17MessageVocale,
    viewModel: ViewModelMessageur
) {
    val activeCurrent_M9AppCompt = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt
    val relative_M9AppCompt = viewModel.aCentralFacade.repositorysMainGetter.find_M9AppCompt_By_KeyID(relative_M17MessageVocale.parent_M9AppCompt_KeyID)
    val its_ViewMessage_Du_Active_M9AppCompt = relative_M9AppCompt?.keyID == activeCurrent_M9AppCompt?.keyID
    val its_Admin_Message = relative_M9AppCompt?.its_Admin ?: false

    Column {
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
                        .wrapContentWidth()
                        .padding(
                            start = if (its_ViewMessage_Du_Active_M9AppCompt) 40.dp else 0.dp,
                            end = if (its_ViewMessage_Du_Active_M9AppCompt) 0.dp else 40.dp
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            its_Admin_Message -> MaterialTheme.colorScheme.error
                            its_ViewMessage_Du_Active_M9AppCompt -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
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
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = when {
                                its_Admin_Message -> MaterialTheme.colorScheme.onError
                                its_ViewMessage_Du_Active_M9AppCompt -> MaterialTheme.colorScheme.onPrimary
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Téléchargement de la vidéo...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                its_Admin_Message -> MaterialTheme.colorScheme.onError
                                its_ViewMessage_Du_Active_M9AppCompt -> MaterialTheme.colorScheme.onPrimary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
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

// Loading component for audio messages
@Composable
private fun AudioLoadingMessage(
    list_D_EtateMessageVocale: List<M17MessageVocale>,
    relative_M17MessageVocale: M17MessageVocale,
    viewModel: ViewModelMessageur,
    uiState: UiState
) {
    val activeCurrent_M9AppCompt = viewModel.aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.currentActive_M9AppCompt
    val relative_M9AppCompt = viewModel.aCentralFacade.repositorysMainGetter.find_M9AppCompt_By_KeyID(relative_M17MessageVocale.parent_M9AppCompt_KeyID)
    val its_ViewMessage_Du_Active_M9AppCompt = relative_M9AppCompt?.keyID == activeCurrent_M9AppCompt?.keyID
    val its_Admin_Message = relative_M9AppCompt?.its_Admin ?: false

    Column {
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
                        .wrapContentWidth()
                        .padding(
                            start = if (its_ViewMessage_Du_Active_M9AppCompt) 40.dp else 0.dp,
                            end = if (its_ViewMessage_Du_Active_M9AppCompt) 0.dp else 40.dp
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            its_Admin_Message -> MaterialTheme.colorScheme.error
                            its_ViewMessage_Du_Active_M9AppCompt -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
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
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = when {
                                its_Admin_Message -> MaterialTheme.colorScheme.onError
                                its_ViewMessage_Du_Active_M9AppCompt -> MaterialTheme.colorScheme.onPrimary
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Téléchargement de l'audio...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                its_Admin_Message -> MaterialTheme.colorScheme.onError
                                its_ViewMessage_Du_Active_M9AppCompt -> MaterialTheme.colorScheme.onPrimary
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
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
fun C_Text_Message(
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
                            set(value = list_D_EtateMessageVocale
                                .maxOf { it.parent_M9AppCompt_Nom }
                                , key = SemanticsPropertyKey("maxBy"))
                        }
                        .semantics(mergeDescendants = true) {
                            set(
                                value = list_D_EtateMessageVocale.map {
                                    it.parent_M9AppCompt_Nom  + "->"+it.etate
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

                        // Text Message Content
                        if (currentState != null) {
                            TextMessageContent(
                                textMessage = relative_M17MessageVocale.text_Inputted,
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
private fun TextMessageContent(
    textMessage: String,
    currentState: M17MessageVocale.Etate,
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean
) {
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
            if (textMessage.isNotEmpty()) {
                Text(
                    fontSize = 40.sp,
                    text = textMessage,
                    color = when {
                        isAdminMessage -> MaterialTheme.colorScheme.onError
                        isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    fontSize = 40.sp,
                    text = "👍",
                    color = when {
                        isAdminMessage -> MaterialTheme.colorScheme.onError
                        isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Show typing indicator for messages being composed
            if (currentState == M17MessageVocale.Etate.EN_COURT_ENREGESTREMENT) {
                Spacer(modifier = Modifier.height(8.dp))
                TypingIndicator(
                    isFromActiveAccount = isFromActiveAccount,
                    isAdminMessage = isAdminMessage
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator(
    isFromActiveAccount: Boolean,
    isAdminMessage: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = when {
            isAdminMessage -> MaterialTheme.colorScheme.onError.copy(alpha = 0.2f)
            isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
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
                imageVector = Icons.Default.Edit,
                contentDescription = "Écriture en cours",
                tint = when {
                    isAdminMessage -> MaterialTheme.colorScheme.onError
                    isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Écriture en cours...",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = when {
                    isAdminMessage -> MaterialTheme.colorScheme.onError
                    isFromActiveAccount -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}
