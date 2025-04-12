package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface _0_0_HeadOfRepositorys_Repository {
    var repositorys_Model: _0_0_HeadOfRepositorys_Model

    // Added properties
    val currentVendeur get() = repositorys_Model._1_5_Vendeur_Repository.modelDatasSnapList.firstOrNull()

    val activePeriod get() = currentVendeur?.let {
        repositorys_Model._1_4_PeriodeVent_Repository.modelDatasSnapList
            .find { it.vendeur_ParentVID == currentVendeur!!.vid }
    }

    // Change to StateFlow so it's observable
    val activeVID_1_3_BonAchatFlow: StateFlow<Long?>

    // Keep for backward compatibility
    val activeVID_1_3_BonAchat: Long?
        get() = activeVID_1_3_BonAchatFlow.value

    val progressRepo: MutableStateFlow<Float>
        get() { return MutableStateFlow(0f)}

    companion object {
        const val TAG = "_0_0_HeadOfRepositorys"

        val _0_0_HeadOfRepositorys_RepositoryRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
    }
}
