package Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_1_CouleurAcheteOperation_Repository {
    var modelDatasSnapList: SnapshotStateList<_1_1_CouleurAcheteOperation>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    // Added ensureDataIsInitialized function to the interface
    suspend fun ensureDataIsInitialized()

    fun addData(data: _1_1_CouleurAcheteOperation)
    fun updateUnSeulData(data: _1_1_CouleurAcheteOperation)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_1_1_CouleurAcheteOperation>)
    fun deleteUnSeulData(data: _1_1_CouleurAcheteOperation)

    // Added notifyDataChanged method for consistency with _1_2_ProduitAcheteOperation_Repository
    fun notifyDataChanged()

    companion object {
        const val TAG = "_1_1_CouleurAcheteOperation"

        val sonDataBaseRef: DatabaseReference =
            GroupeRepositorysProtoAvJuin3Model.getHeadSqlDataBaseRef()
                .child("C_AchatsDataBases")
                .child("A_CouleurAcheteOperation"
                        + "DataBAse"
                )

    }

    fun addDataAndReturnItVID(data: _1_1_CouleurAcheteOperation, onAddSuccess: (Long) -> Unit = {})
    fun upsertUneDataEtReturnVID(data: _1_1_CouleurAcheteOperation, onSuccess: (Long) -> Unit = {})
}
