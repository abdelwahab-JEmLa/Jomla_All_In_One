package com.example.clientjetpack.App2.App.B.Fragment

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Koin_RecourcesSaver(modifier: Modifier = Modifier.Companion) {
   /* LaunchedEffect(Unit) {
        runCatching {
            val koin = GlobalContext.get()
            // Décharge uniquement appModule (le lourd)
            // appDatabase + modulesDonLesDeuAppNeceFemrePas + appModule_App2_ac_app1 restent vivants ✅
            koin.unloadModules(listOf(appModule))
            System.gc()
            Runtime.getRuntime().gc()
        }
    }     */
    Compact_Presentoire_App_Produits_App2()
}
