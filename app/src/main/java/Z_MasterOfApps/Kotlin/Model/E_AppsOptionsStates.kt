package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.firebaseDatabase
import android.content.res.Resources
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.IgnoreExtraProperties

class E_AppsOptionsStates {

    @IgnoreExtraProperties
    class ApplicationEstInstalleDonTelephone {
        var id by mutableStateOf(0)
        var nom by mutableStateOf("")
        var widthScreen by mutableStateOf(0)
        var itsReciverTelephone by mutableStateOf(false )

        companion object{
            val metricsWidthPixels = Resources.getSystem().displayMetrics.widthPixels
        }

    }

    companion object {
        val caReference = firebaseDatabase
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("E_AppsOptionsStates")
    }
}
