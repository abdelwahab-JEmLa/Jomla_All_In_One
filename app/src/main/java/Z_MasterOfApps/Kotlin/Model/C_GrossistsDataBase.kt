package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@IgnoreExtraProperties
data class C_GrossistsDataBase(
    var id: Long = 1,
    var nom: String = "Non Defini",
    var statueDeBase: StatueDeBase = StatueDeBase(),
) {
    @IgnoreExtraProperties
    data class StatueDeBase(
        var couleur: String = "#FFFFFF",
        var itPositionInParentList: Int = 0,
        var caRefDonAncienDataBase: String = "",
        var cUnClientTemporaire: Boolean = true,
        var auFilterFAB: Boolean = false,
        var actuelleEtat: DernierEtateAAffiche? = null
    )  {
        @IgnoreExtraProperties
        enum class DernierEtateAAffiche(val color: Int, val nomEtateArabe: String)
    }

    // B_ClientsDataBase.kt - Updated companion object
    companion object {
        val refClientsDataBase = Firebase.database
            .getReference("0_UiState_3_Host_Package_3_Prototype11Dec")
            .child("C_GrossistsDataBase")

        fun C_GrossistsDataBase.updateGrossistDataBase(
            viewModel: ViewModelInitApp
        ) {
            viewModel.viewModelScope.launch {
                try {
                    // Create a snapshot of the current state
                    val currentState = this@updateGrossistDataBase.copy()

                    // Update local state
                    val clientsList = viewModel._modelAppsFather.grossistsDataBase
                    val index = clientsList.indexOfFirst { it.id == currentState.id }

                    if (index != -1) {
                        clientsList[index] = currentState
                    } else {
                        // If client doesn't exist, add them
                        clientsList.add(currentState)
                    }

                    // Update Firebase with error handling
                    try {
                        refClientsDataBase.child(currentState.id.toString())
                            .setValue(currentState)
                            .await()
                    } catch (e: Exception) {
                        // Revert local state if Firebase update fails
                        if (index != -1) {
                            clientsList[index] = this@updateGrossistDataBase
                        } else {
                            clientsList.removeAt(clientsList.lastIndex)
                        }
                        throw e
                    }

                } catch (e: Exception) {
                    Log.e("", "Failed to update client", e)

                }
            }
        }
    }

}
