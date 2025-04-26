package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial

import V.DiviseParSections.App.SectionID5.Detailes.App.FragID2.EtatesDuCLient.Fragment.Models._1_3_TransactionCommercial
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository.Companion._0_0_HeadOfRepositorys_RepositoryRef
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_3_TransactionCommercial_Repository {
    var modelDatasSnapList: SnapshotStateList<_1_3_TransactionCommercial>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)
    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()



    companion object {
        const val TAG = "_1_3_TransactionCommercial"

        val sonDataBaseRef = _0_0_HeadOfRepositorys_RepositoryRef
            .child("1")
            .child("3")
    }


}
