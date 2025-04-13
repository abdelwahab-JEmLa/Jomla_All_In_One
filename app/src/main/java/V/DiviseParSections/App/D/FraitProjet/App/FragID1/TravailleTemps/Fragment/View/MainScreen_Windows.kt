package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.A_OptionsControlsButtons_FragId_
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.ViewModel.Windows__ViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen_Windows(
    modifier: Modifier = Modifier,
    viewModel: Windows__ViewModel = koinViewModel(),
    fabsVisibility: Boolean,
) {
    // Collect recording state
    val isRecording by viewModel.isRecording.collectAsState()

    // Lifecycle observer to log state changes
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onLifecycleResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            viewModel.updateElapsedTime()
        }
    }
    // Timer effect for recording session
    LaunchedEffect(isRecording) {
        if (isRecording) {
            // Reset session timer when starting a new recording
            viewModel.resetSessionTimer()
        } else {
            // When stopping, recalculate total time to include the newly completed interval
            viewModel.onRecordingStopped()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = androidx.compose.ui.graphics.Color(0xFFFCE4EC), // Light pink color
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            MainList_Windows(viewModel = viewModel)

            if (fabsVisibility) {
                A_OptionsControlsButtons_FragId_( viewModel = viewModel)
            }
        }
    }
}
