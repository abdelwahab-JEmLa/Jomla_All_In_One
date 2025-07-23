package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Options.FabButtonsMessageurMainScreen
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.UiState
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.ViewModelMessageur
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.B.MainItem.B_ItemMessagesVocale
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.clientjetpack.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun A_MessageurTelegram_MainScreen(
    modifier: Modifier = Modifier,
    viewModel: ViewModelMessageur = koinViewModel(),
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
                            painter = painterResource(id = R.drawable.background_mess),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
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
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 80.dp)
    ) {
        items(latestStatesForEachMessage) { (latestEtate, allEtatesForMessage) ->
            B_ItemMessagesVocale(
                list_D_EtateMessageVocale = allEtatesForMessage,
                viewModel = viewModel,
                relative_M17MessageVocale = latestEtate,
                uiState = uiState
            )
        }
    }
}
