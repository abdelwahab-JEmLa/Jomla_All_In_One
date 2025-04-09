package Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface _2_2_ClientsDataBase_Repository{
    var modelDatasSnapList: SnapshotStateList<_2_2_ClientsDataBase>


    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    val activeId: MutableStateFlow<Long>


    suspend fun ensureDataIsInitialized()

    fun getIdParNomModel(nomModel:String): Long

    fun addData(data: _2_2_ClientsDataBase)
    fun updateUnSeulData(data: _2_2_ClientsDataBase)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_2_2_ClientsDataBase>)
    fun deleteUnSeulData(data: _2_2_ClientsDataBase)

    companion object {
        const val TAG = "_2_2_ClientsDataBase"

        val sonDataBaseRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
            .child("_1_5_")
    }

}
