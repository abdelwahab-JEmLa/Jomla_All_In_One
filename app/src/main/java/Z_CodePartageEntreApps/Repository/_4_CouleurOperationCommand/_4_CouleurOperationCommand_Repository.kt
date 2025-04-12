package Z_CodePartageEntreApps.Repository._4_CouleurOperationCommand

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository.Companion._0_0_HeadOfRepositorys_RepositoryRef
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface _4_CouleurOperationCommand_Repository {
    var modelDatasSnapList: SnapshotStateList<_4_CouleurOperationCommand>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun ensureDataIsInitialized()

    fun addDataAndReturnItVID(
        data: _4_CouleurOperationCommand,
        onAddSuccess: (Long) -> Unit = {},
    )
    fun addMultiDATAsEtReturnVIDsList(
        dataList: List<_4_CouleurOperationCommand>,
        onAddSuccess: (List<Long>) -> Unit
    )

    fun upsertUneDataEtReturnVID(data: _4_CouleurOperationCommand, onSuccess: (Long) -> Unit)

    fun addData(data: _4_CouleurOperationCommand)
    fun updateUnSeulData(data: _4_CouleurOperationCommand)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_4_CouleurOperationCommand>)
    fun deleteUnSeulData(data: _4_CouleurOperationCommand)

    companion object {
        const val TAG = "_4_CouleurOperationCommand"

        val sonDataBaseRef = _0_0_HeadOfRepositorys_RepositoryRef
            .child("_2_")
            .child("1_ProduitsDataBase")
    }
}
