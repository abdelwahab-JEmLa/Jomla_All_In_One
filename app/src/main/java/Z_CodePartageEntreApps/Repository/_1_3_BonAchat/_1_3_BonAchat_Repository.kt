package Z_CodePartageEntreApps.Repository._1_3_BonAchat

import Z_CodePartageEntreApps.Proto.B.Sectiones.Fragment.A.AchatsManager.App.B.CommendsGrossistManager.APP._OnWorkingOn.Models._1_3_BonAchat
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
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

        val sonDataBaseRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
            .child("_1_3_")
    }

    fun addDataAndReturneItVID(data: _1_3_BonAchat, onAddSuccess: (Long) -> Unit = {})
}
