package Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface _0_0_HeadOfRepositorys_Repository {
    var repositorys_Model: _0_0_HeadOfRepositorys_Model

    // Added properties
    val currentVendeur get() = repositorys_Model.repository_1_5_Vendeur.modelDatasSnapList.firstOrNull()

    val activePeriod get() = currentVendeur?.let {
        repositorys_Model.repository_1_4_PeriodeVent.modelDatasSnapList
            .find { it.vendeur_ParentVID == currentVendeur!!.vid }
    }

    val progressRepo: MutableStateFlow<Float>
        get() { return MutableStateFlow(0f)}


    companion object {
        const val TAG = "_0_0_HeadOfRepositorys"

        val _0_0_HeadOfRepositorys_RepositoryRef = Firebase.database
            .getReference("00_DataPrototype-04-02")

        val sonDataBaseRef = _0_0_HeadOfRepositorys_RepositoryRef
            .child("_3_")
            .child("_Main")
    }
    fun updateActiveIdDe_1_5_Vendeur(id: Long = -1L)
    fun notifyDataChanged_2_1_ProduitsDataBase_Repository()
    fun notifyDataChanged_1_3_TransactionCommercial_Repository()
    fun upsertUneDataEtReturnVID(data: _1_5_Vendeur, onSuccess: (Long) -> Unit={})


    fun upsertUneDataEtReturnVID_1_4_PeriodeVent(data: _1_4_PeriodeVent,
                                                 onSuccess: (Long) -> Unit ={}
    )

    //  _1_3_TransactionCommercial
    fun upsertUneDataEtReturnVID_1_3_TransactionCommercial(
        data: _1_3_TransactionCommercial,
        onSuccess: (Long) -> Unit={}
    )
    fun deleteUnSeulData_1_3_TransactionCommercial(data: _1_3_TransactionCommercial)

}
