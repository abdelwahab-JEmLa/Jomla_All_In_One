package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test._A.View

import android.util.Log

// Function to handle debug logging with a consistent tag
fun logDebug(message: String) {
    val TAG = "PresentoirApp"
    try {
        Log.d(TAG, message)
    } catch (e: Exception) {
        // Fallback for preview environment where Log might not be available
        println("$TAG: $message")
    }
}
