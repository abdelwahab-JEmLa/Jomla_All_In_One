package Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface _2_1_ProduitsDataBase_Repository {
    var modelDatasSnapList: SnapshotStateList<_2_1_ProduitsDataBase>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun addDataAndReturnItVID(
        data: _2_1_ProduitsDataBase,
        onAddSuccess: (Long) -> Unit = {},
    )

    suspend fun ensureDataIsInitialized()

    fun addData(data: _2_1_ProduitsDataBase)
    fun updateUnSeulData(data: _2_1_ProduitsDataBase)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_2_1_ProduitsDataBase>)
    fun deleteUnSeulData(data: _2_1_ProduitsDataBase)

    companion object {
        const val TAG = "_2_1_ProduitsDataBase"

        val sonDataBaseRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
            .child("_2_1_")
    }
}
