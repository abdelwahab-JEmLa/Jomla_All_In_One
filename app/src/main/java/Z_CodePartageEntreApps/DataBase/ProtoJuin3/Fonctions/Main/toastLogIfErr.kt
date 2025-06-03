package A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun toastLogIfErr(message: String, repoTAG: String, context: Context, isError: Boolean = false) {
        val fullMessage = "$repoTAG: $message"

        if (isError) {
            Log.e(repoTAG, fullMessage)
        } else {
            Log.d(repoTAG, fullMessage)
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val toastMessage = if (message.length > 100) message.take(100) + "..." else message
                val icon = if (isError) "❌" else "📱"
                val duration = if (isError) Toast.LENGTH_SHORT else Toast.LENGTH_SHORT
                Toast.makeText(context, "$icon $toastMessage", duration).show()
            } catch (e: Exception) {
                Log.e(repoTAG, "Toast error: ${e.message}")
            }
        }
    }
