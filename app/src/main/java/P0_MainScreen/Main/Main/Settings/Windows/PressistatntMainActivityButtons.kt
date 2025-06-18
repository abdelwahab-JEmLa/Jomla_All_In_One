package P0_MainScreen.Main.Main.Settings.Windows

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.RecordingViewModel
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views.A_MessageurMainScreen
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.TariffsButtonsSec7ID2
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.clientjetpack.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun PressistatntMainActivityButtons(
    cLenceDepuitDialogeAchate: Boolean = false,
    viewModel: RecordingViewModel = koinViewModel(),
    onPourFermeWindows: (D_TarificationInfos) -> Unit = {},
    idProduitActuelle: Long = 0,
    onClickAnulationButton: () -> Unit = {},
) {
    val TAG ="PressistatntMainActivityButtons"
    var showButtons by remember { mutableStateOf(true) }
    var showLabels by remember { mutableStateOf(true) }
    var showAlertDialog by remember { mutableStateOf(false) }
    var showMessageurDialog by remember { mutableStateOf(false) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val isRecording by viewModel.isRecording.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val displayTime by viewModel.displayTime.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Cache the client count to avoid multiple reads
    val remainingClients = viewModel.a_CentralDatasHandlerProtoJuin9.nombreClientsOuLeurDernierEtateCible

    // Set up a timer to update_showDetailsExpanded the elapsed time every second when recording
    DisposableEffect(isRecording) {
        var job: Job? = null
        val coroutineScope = CoroutineScope(Dispatchers.Main)

        if (isRecording) {
            job = coroutineScope.launch {
                while (true) {
                    viewModel.updateElapsedTime()
                    delay(1000) // Update every second
                }
            }
        }

        // Monitor lifecycle events to handle pause/resume correctly
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.onLifecycleResume()
                    if (isRecording && job == null) {
                        job = coroutineScope.launch {
                            while (true) {
                                viewModel.updateElapsedTime()
                                delay(1000)
                            }
                        }
                    }
                }

                Lifecycle.Event.ON_PAUSE -> {
                    if (isRecording) {
                        viewModel.onRecordingStopped()
                    }
                    job?.cancel()
                    job = null
                }

                else -> { /* do nothing */
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            job?.cancel()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    WorkCompletionAlertDialog(
        showDialog = showAlertDialog,
        onDismiss = { showAlertDialog = false },
        onConfirm = {
            viewModel.recordingHandler.stopRecording()
        },
        nombreClientAvecCibleCommeLastBonAchat = remainingClients
    )

    // Show Messager Dialog when triggered
    if (showMessageurDialog) {
        A_MessageurMainScreen(
            onDismiss = { showMessageurDialog = false }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showButtons) {
                    // New Row: Messager ButtonAutreEtates with Telegram icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                showMessageurDialog = true
                            },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color(0xFF0088CC), // Telegram brand color
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_telegram),
                                contentDescription = "Ouvrir Messager",
                                tint = Color.White
                            )
                        }

                        if (showLabels) {
                            Text(
                                "Telegrame Abdelwahab",
                                modifier = Modifier
                                    .background(Color(0xFF0088CC))
                                    .padding(4.dp),
                                color = Color.White
                            )
                        }
                    }

                    if (!cLenceDepuitDialogeAchate) {
                        val buttonBackgroundColor =
                            if (isRecording) Color(0xFFFF9800) else Color(0xFF8BC34A)
                        val enable = true
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Labels toggle button
                            FloatingActionButton(
                                onClick = {
                                    if (enable) {
                                        showAlertDialog = true
                                    }
                                },
                                modifier = Modifier.size(40.dp),
                                containerColor = buttonBackgroundColor,
                            ) {
                                val iconColor = Color.Black

                                Icon(
                                    imageVector = if (isRecording) Icons.Default.PlayArrow else Icons.Default.Stop,
                                    contentDescription = null,
                                    tint = iconColor
                                )
                            }

                            if (showLabels) {
                                // Use the pre-cached value
                                Text(
                                    "$displayTime | بقي $remainingClients زبون",
                                    modifier = Modifier
                                        .background(if (enable) buttonBackgroundColor else Color.Gray)
                                        .padding(4.dp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                TariffsButtonsSec7ID2(
                    showLabels = showLabels,
                    filterProductId = idProduitActuelle,
                    fermeDialog = onPourFermeWindows,
                    cLenceDepuitDialogeAchate = cLenceDepuitDialogeAchate,
                    onFermDialogeAvecAnllation = onClickAnulationButton
                )

            }
        }
    }
}
