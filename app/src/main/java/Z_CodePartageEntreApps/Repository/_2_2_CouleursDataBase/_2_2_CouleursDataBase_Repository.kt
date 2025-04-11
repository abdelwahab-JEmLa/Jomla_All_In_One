package Z_CodePartageEntreApps.Repository._2_2_CouleursDataBase

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository.Companion._0_0_HeadOfRepositorys_RepositoryRef
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface _2_2_CouleursDataBase_Repository {
    var modelDatasSnapList: SnapshotStateList<_2_2_CouleursDataBase>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun ensureDataIsInitialized()

    fun addDataAndReturnItVID(
        data: _2_2_CouleursDataBase,
        onAddSuccess: (Long) -> Unit = {},
    )
    fun addMultiDATAsEtReturnVIDsList(
        dataList: List<_2_2_CouleursDataBase>,
        onAddSuccess: (List<Long>) -> Unit
    )

    fun addData(data: _2_2_CouleursDataBase)
    fun updateUnSeulData(data: _2_2_CouleursDataBase)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_2_2_CouleursDataBase>)
    fun deleteUnSeulData(data: _2_2_CouleursDataBase)

    companion object {
        const val TAG = "_2_2_CouleursDataBase"

        val sonDataBaseRef = _0_0_HeadOfRepositorys_RepositoryRef
            .child("_2_")
            .child("1_ProduitsDataBase")
    }


}
