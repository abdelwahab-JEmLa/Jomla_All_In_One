package V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App._0.Navigation.Screen
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class Repo18CentralParametresOfAllApps {
    val repoTAG = "Repo18CentralParametresOfAllApps"

    private val composScope = CoroutineScope(Dispatchers.IO)
    private val refRepo = M18CentralParametresOfAllApps.ref
    private val _data = mutableStateOf<M18CentralParametresOfAllApps?>(null)
    val dataValue by derivedStateOf { _data.value }

    init {
        composScope.launch {
            refRepo.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val data = snapshot.getValue(M18CentralParametresOfAllApps::class.java)
                            ?: M18CentralParametresOfAllApps.get_Default()
                        _data.value = data
                        Log.d(repoTAG, "Data loaded successfully")
                    } catch (e: Exception) {
                        Log.e(repoTAG, "Error parsing data from Firebase", e)
                        throw RuntimeException("Failed to parse central parameters from Firebase", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(repoTAG, "Firebase listener cancelled: ${error.message}", error.toException())
                    throw RuntimeException("Firebase connection cancelled: ${error.message}", error.toException())
                }
            })
        }
    }
}

data class M18CentralParametresOfAllApps(
    val itsDevMode: Boolean = true,
    val abdelwahabCompt_KeyId: String = "-OV9dYujH9cA3yEx8AYT",
    val abdelwahabCompt_KeyId_DPL: String = "-OV9edQZecDczbx-ndPl",
    val abdelmomen_Compt_KeyId: String = "-OTmoNn0cljrRuhVR2s4",

    val au_Lence_Set_Compt_Ac_KeyId: String = "",
    val currentActiveFocucedM9AppComptDebugInfos: String = "",

    val activeWindowsSearchProduit: Boolean = false,
    val devStartUpScree: Screen = Screen.FacadePresentoireProduits,
    var enablePerformAutoClickImageDisplayer: Boolean = false,
    val isControleFabVisible: Boolean = false,
) {
    companion object {
        val ref = RepositorysMainGetter.centralRef
            .child(
                "Datas" +
                        "18CentralParametresOfAllApps"
            )

        fun get_Default(): M18CentralParametresOfAllApps {
            return M18CentralParametresOfAllApps()
        }
    }
}
