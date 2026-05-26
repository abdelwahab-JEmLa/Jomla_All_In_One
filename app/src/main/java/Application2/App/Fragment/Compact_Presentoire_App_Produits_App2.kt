package Application2.App.Fragment

import Application2.App.App.ViewModel.Feature.ViewModel_MainFragment
import Application2.App.Base.Modules.ConnexionCard_App2
import Application2.App.Base.Repository.RepositorysMainGetter_app2
import Application4.App.Fragment.ID1.Fragment.PubAbdelwahabElectroGroStore
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.ifTrue
import EntreApps.Shared.Modules.Base.AppDatabase
import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.clientjetpack.R
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Compact_Presentoire_App_Produits_App2(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase = koinInject(),
    repositorysMainGetter_app2: RepositorysMainGetter_app2 = koinInject()
) {
    val context = LocalContext.current
    val vm: ViewModel_MainFragment = viewModel(
        factory = viewModelFactory {
            initializer {
                ViewModel_MainFragment(
                    context = context.applicationContext,
                    appDatabase = appDatabase,
                    repositorysMainGetter_app2 = repositorysMainGetter_app2,
                )
            }
        }
    )
    val uiState by vm.uiState.collectAsState()
    val wifiState by vm.wifiState.collectAsState()
    val isInitDone = uiState.initDatasProgressEtate >= 1f

    val view = LocalView.current
    val window = (context as? Activity)?.window
    var affiche_pub_abdelwahab_electro_gro_store = wifiState.affiche_pub_abdelwahab_electro_gro_store

    LaunchedEffect(wifiState.isConnected) {
        window?.let { w ->
            if (wifiState.isConnected && !wifiState.isHostPhone) {
                WindowCompat.setDecorFitsSystemWindows(w, false)
                w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                WindowInsetsControllerCompat(w, view).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                WindowCompat.setDecorFitsSystemWindows(w, true)
                w.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                WindowInsetsControllerCompat(w, view).show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    if (!isInitDone) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { uiState.initDatasProgressEtate },
                modifier = Modifier.size(48.dp),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Column(modifier = modifier.fillMaxSize()) {
            (!wifiState.isConnected).ifTrue {
                ConnexionCard_App2(vm = vm)
            }

            if (affiche_pub_abdelwahab_electro_gro_store) {
                val allImageIds = listOf(
                    R.drawable.imgs__1_,
                    R.drawable.imgs__2_,
                    R.drawable.imgs__3_,
                    R.drawable.imgs__4_,
                    R.drawable.imgs__5_,
                )
                val landscapeImages = remember(allImageIds) {
                    allImageIds.filter { resId ->
                        val opts = android.graphics.BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeResource(context.resources, resId, opts)
                        opts.outWidth > opts.outHeight
                    }
                }
                PubAbdelwahabElectroGroStore(
                    affiche = true,
                    images  = landscapeImages.ifEmpty { allImageIds },  // fallback si tout est portrait
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
            MainLazyList_App2(viewModel = vm)
        }
        }
    }
}
