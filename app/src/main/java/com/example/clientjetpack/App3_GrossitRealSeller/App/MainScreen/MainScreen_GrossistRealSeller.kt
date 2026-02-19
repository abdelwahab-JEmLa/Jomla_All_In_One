package com.example.clientjetpack.App3_GrossitRealSeller.App.MainScreen

import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.MainFastSearchProduitPourVent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.ViewModel.HeadViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainScreen_GrossistRealSeller(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val headViewModel: HeadViewModel = koinViewModel(parameters = { parametersOf(context) })
    val uiState by headViewModel.uiState.collectAsState()
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MainFastSearchProduitPourVent()
            PressistatntMainActivityButtons_Sec8FWinID1()
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { uiState.loadingProgress / 100f },
                        modifier = Modifier.size(48.dp),
                        trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
