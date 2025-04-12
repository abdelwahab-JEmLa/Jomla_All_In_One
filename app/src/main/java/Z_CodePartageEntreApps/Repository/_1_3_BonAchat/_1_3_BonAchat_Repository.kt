package Z_CodePartageEntreApps.Repository._1_3_BonAchat

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository.Companion._0_0_HeadOfRepositorys_RepositoryRef
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_3_BonAchat_Repository {
    var modelDatasSnapList: SnapshotStateList<_1_3_BonAchat>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)
    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()

    fun addData(data: _1_3_BonAchat)
    fun updateUnSeulData(data: _1_3_BonAchat)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_1_3_BonAchat>)
    fun deleteUnSeulData(data: _1_3_BonAchat)

    companion object {
        const val TAG = "_1_3_BonAchat"

        val sonDataBaseRef = _0_0_HeadOfRepositorys_RepositoryRef
            .child("_1")
            .child("_" +
                    "3" +
                    "_")
    }

    fun addDataAndReturneItVID(data: _1_3_BonAchat, onAddSuccess: (Long) -> Unit = {})
    fun upsertUneDataEtReturnVID(data: _1_3_BonAchat, onSuccess: (Long) -> Unit = {})
}
