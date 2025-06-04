package Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow

interface _2_1_ProduitsDataBase_Repository {
    var modelDatasSnapList: SnapshotStateList<_2_1_ProduitsDataBase>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun ensureDataIsInitialized()

    fun addDataAndReturnItVID(
        data: _2_1_ProduitsDataBase,
        onAddSuccess: (Long) -> Unit = {},
    )

    fun addMultiDATAsEtReturnVIDsList(
        dataList: List<_2_1_ProduitsDataBase>,
        onAddSuccess: (List<Long>) -> Unit,
    )

    fun upsertUneDataEtReturnVID(data: _2_1_ProduitsDataBase, onSuccess: (Long) -> Unit)

    fun addData(data: _2_1_ProduitsDataBase)
    fun updateUnSeulData(data: _2_1_ProduitsDataBase)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_2_1_ProduitsDataBase>)
    fun deleteUnSeulData(data: _2_1_ProduitsDataBase)

    companion object {
        const val TAG = "_2_1_ProduitsDataBase"

        val sonDataBaseRef: DatabaseReference =
            GroupeRepositorysProtoAvJuin3Model.getHeadSqlDataBaseRef()
                .child("A_ProduitsDataBase")
                .child("A_MainDataBase")
    }
}
