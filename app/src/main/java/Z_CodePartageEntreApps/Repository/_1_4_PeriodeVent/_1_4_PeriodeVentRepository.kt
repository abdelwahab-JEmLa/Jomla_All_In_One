package Z_CodePartageEntreApps.Repository._1_4_PeriodeVent

import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_4_PeriodeVentRepository {
    var modelDatasSnapList: SnapshotStateList<_1_4_PeriodeVent>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun ensureDataIsInitialized()

    fun addData(data: _1_4_PeriodeVent)
    fun updateUnSeulData(data: _1_4_PeriodeVent)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_1_4_PeriodeVent>)
    fun deleteUnSeulData(data: _1_4_PeriodeVent)

    companion object {
        const val TAG = "_1_4_PeriodeVent"

        val sonDataBaseRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
            .child("_1_4_")
    }
}
