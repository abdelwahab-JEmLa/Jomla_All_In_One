package Application4.App.A.Start.Init.Proto

import EntreApps.Shared.Modules.Base.AppDatabase
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clientjetpack.R

@Composable
fun A_LoadingApp4_Init_Screen(
    innerPadding: PaddingValues = PaddingValues(),
    onInitDone: () -> Unit = {},
    appDatabase: AppDatabase,
) {
    val context = LocalContext.current

    val loadingViewModel = remember(appDatabase) {
        A_LoadingViewModel(appDatabase = appDatabase, appContext = context.applicationContext)
    }

    DisposableEffect(loadingViewModel) {
        onDispose { loadingViewModel.onCleared() }
    }

    LaunchedEffect(Unit) {
        loadingViewModel.startIfNeeded(context)
    }

    val uiState by loadingViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.initDone) {
        if (uiState.initDone) onInitDone()
    }

    if (!uiState.initDone) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .semantics(mergeDescendants = true) {
                    set(value = uiState.activeCompt, key = SemanticsPropertyKey("activeCompt"))
                    set(value = uiState.lightDataBasesResult, key = SemanticsPropertyKey("lightDataBasesResult"))
                    set(value = uiState.seedResult, key = SemanticsPropertyKey("seedResult"))
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
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
    }
}
