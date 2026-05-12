package A_Main.Shared.Init

import android.content.Context
import android.net.ConnectivityManager

fun isInternetAvailable(context: Context): Boolean = try {
    @Suppress("DEPRECATION")
    (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        .activeNetworkInfo?.isConnected == true
} catch (_: Exception) { false }
