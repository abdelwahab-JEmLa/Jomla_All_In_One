package A_Main.Shared.Proto

import EntreApps.Shared.Modules.Base.AppDatabase
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.clientjetpack.R

private const val SCR_TAG = "LoadingScreen"

@Composable
fun A_LoadingApp4_Init_Screen(
    innerPadding: PaddingValues = PaddingValues(),
    onInitDone: () -> Unit = {},
    appDatabase: AppDatabase,
) {
    val context = LocalContext.current

    // ── GATE 1: composable entered ────────────────────────────────────────────
    // Runs once per composition instance.  If you see this more than once with
    // the same or a new vmHash it means the composable is being re-created
    // (back-stack pop, NavHost re-entry, Activity restart, etc.).
    val composableId = remember { System.nanoTime() }
    Log.d(SCR_TAG, "COMPOSE ENTER  composableId=$composableId")

    val loadingViewModel: A_LoadingViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                Log.d(SCR_TAG, "ViewModel CREATED  composableId=$composableId")
                A_LoadingViewModel(
                    appDatabase = appDatabase,
                    appContext  = context.applicationContext,
                )
            }
        }
    )

    // ── GATE 2: which ViewModel instance is attached ──────────────────────────
    // If vmHash changes between compositions the old ViewModel was cleared and a
    // brand-new one (initStarted=false) took its place – init should re-run.
    // If vmHash is the same the ViewModel was reused – init runs only once.
    val vmHash = System.identityHashCode(loadingViewModel)
    Log.d(SCR_TAG, "ViewModel instance  vmHash=$vmHash  composableId=$composableId")

    // ── GATE 3: LaunchedEffect(Unit) ─────────────────────────────────────────
    // Runs exactly once per ViewModel lifetime inside this composition.
    // If you NEVER see this log the effect is being cancelled before it fires
    // (e.g. the composable is removed from the tree immediately after entering).
    LaunchedEffect(Unit) {
        Log.d(SCR_TAG, "LaunchedEffect(Unit) FIRED – calling startIfNeeded  vmHash=$vmHash")
        loadingViewModel.startIfNeeded(context)
        Log.d(SCR_TAG, "LaunchedEffect(Unit) – startIfNeeded returned  vmHash=$vmHash")
    }

    val uiState by loadingViewModel.uiState.collectAsState()

    // ── GATE 4: every recomposition – tracks each state change ───────────────
    // SideEffect runs after every successful recomposition.
    // Watch for progress staying at 0 here while the ViewModel logs show work.
    // That would mean the Flow emission is not reaching the Composable
    // (wrong scope, collectAsState on a different lifecycle, etc.).
    SideEffect {
        Log.d(SCR_TAG, "RECOMPOSE  progress=${(uiState.progress * 100).toInt()}%  " +
                "initDone=${uiState.initDone}  " +
                "job='${uiState.currentJobName}'  " +
                "vmHash=$vmHash  composableId=$composableId")
    }

    // ── GATE 5: initDone watcher ──────────────────────────────────────────────
    // Runs whenever initDone changes.
    // If initDone flips to true before the bar visually reaches 100 % it means
    // the progress state and the initDone state are being updated in the wrong
    // order (a race between two separate _uiState.update calls).
    LaunchedEffect(uiState.initDone) {
        Log.d(SCR_TAG, "LaunchedEffect(initDone) FIRED  initDone=${uiState.initDone}  " +
                "progress=${(uiState.progress * 100).toInt()}%  vmHash=$vmHash")
        if (uiState.initDone) {
            Log.d(SCR_TAG, "initDone=true → calling onInitDone()  vmHash=$vmHash")
            onInitDone()
            Log.d(SCR_TAG, "onInitDone() returned  vmHash=$vmHash")
        }
    }

    if (!uiState.initDone) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 32.dp, vertical = 48.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    uiState.currentJobName,
                    fontSize = 13.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LinearProgressIndicator(
                    progress = { uiState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color.White.copy(alpha = 0.25f),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "${(uiState.progress * 100).toInt()} %",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        // ── GATE 6: initDone=true but composable not yet removed from tree ────
        // If you see this log the composable is still visible while onInitDone
        // has already been called.  It is normal for one frame, but if it stays
        // here it means the parent NavHost / caller is not popping this screen.
        Log.w(SCR_TAG, "initDone=true but composable still in tree  vmHash=$vmHash  composableId=$composableId")
    }
}
