package com.example.clientjetpack.App2.App.B.Fragment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.App2.App.B.Fragment.Filter.FilterSortGroupe_Tunnels_app2
import com.example.clientjetpack.App2.App.B.Fragment.ViewModel.ViewModel_MainFragment
import org.koin.androidx.compose.koinViewModel

@Composable
fun Compact_Presentoire_App_Produits_App2(
    viewModel: ViewModel_MainFragment = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val initProgress = uiState.initDatasProgressEtate
    val isInitDone = initProgress >= 1f


    if (!isInitDone) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { initProgress },
                modifier = Modifier.size(48.dp),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        FilterSortGroupe_Tunnels_app2(uiState = uiState, viewModel = viewModel)
    }
}
