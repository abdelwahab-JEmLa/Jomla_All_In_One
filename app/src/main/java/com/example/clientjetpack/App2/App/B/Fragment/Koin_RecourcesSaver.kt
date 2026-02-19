package com.example.clientjetpack.App2.App.B.Fragment

import Z_CodePartageEntreApps.Apps.Manager.Module.A.Koin.appModule
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import org.koin.core.context.GlobalContext

@Composable
fun A_Koin_RecourcesSaver(modifier: Modifier = Modifier.Companion) {
    LaunchedEffect(Unit) {
        runCatching {
            GlobalContext.get().unloadModules(listOf(appModule))
        }
    }
    Compact_Presentoire_App_Produits_App2()
}
