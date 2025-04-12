package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface _0_0_HeadOfRepositorys_Repository {
    var repositorys_Model: _0_0_HeadOfRepositorys_Model

    // Added properties
    val currentVendeur get() = repositorys_Model._1_5_Vendeur_Repository.modelDatasSnapList.firstOrNull()

    val activePeriod get() = currentVendeur?.let {
        repositorys_Model._1_4_PeriodeVent_Repository.modelDatasSnapList
            .find { it.vendeur_ParentVID == currentVendeur!!.vid }
    }


    val progressRepo: MutableStateFlow<Float>
        get() { return MutableStateFlow(0f)}

    companion object {
        const val TAG = "_0_0_HeadOfRepositorys"

        val _0_0_HeadOfRepositorys_RepositoryRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
    }
}
