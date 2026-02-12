package com.example.clientjetpack.App2.App.A.Main.Init

import Z_CodePartageEntreApps.Model.Z.Archive.ProductDisplayController
import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun HandleFullscreenMode(productDisplayController: ProductDisplayController) {
    val context = LocalContext.current
    val view = LocalView.current
    val window = (context as? Activity)?.window

    LaunchedEffect(productDisplayController.isConnected) {
        window?.let { currentWindow ->
            if (productDisplayController.isConnected && !productDisplayController.isHostPhone) {
                WindowCompat.setDecorFitsSystemWindows(currentWindow, false)
                currentWindow.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                WindowInsetsControllerCompat(currentWindow, view).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                WindowCompat.setDecorFitsSystemWindows(currentWindow, true)
                currentWindow.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                WindowInsetsControllerCompat(currentWindow, view)
                    .show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
}
