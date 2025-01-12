// ParamatersAppsModel.kt
package com.example.Z_AppsFather.Kotlin._1.Model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.database.database

class ParamatersAppsModel {
    // Using proper delegate pattern with non-null initial value
    var telephoneClientParamaters by mutableStateOf(TelephoneClientParamaters())

    var visibilityClientEditePositionDialog by mutableStateOf(false)

    var fabsVisibility by mutableStateOf(true)

    var phoneClientSelectedAcheteur by mutableStateOf<Long?>(null)

    class TelephoneClientParamaters {
        var selectedGrossistForClient by mutableStateOf<Long?>(null)
        var selectedGrossistForServeur by mutableStateOf<Long?>(null)


        var selectedAcheteurForClient by mutableStateOf<Long?>(null)
    }
    enum class DeviceMode {
        SERVER,
        DISPLAY
    }
    companion object {
        private const val SELF_CHEMIN_BASE =
            "0_UiState_3_Host_Package_3_Prototype11Dec/ParamatersAppsModel"
        val refSelfFireBase = Firebase.database.getReference(SELF_CHEMIN_BASE)
    }
}
