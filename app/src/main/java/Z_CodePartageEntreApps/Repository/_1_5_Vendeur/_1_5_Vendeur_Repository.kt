package Z_CodePartageEntreApps.Repository._1_5_Vendeur

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_5_Vendeur_Repository{
    var modelDatasSnapList: SnapshotStateList<_1_5_Vendeur>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    val activeId: MutableStateFlow<Long>

    fun addDataAndReturneItVID(data: _1_5_Vendeur, onAddSuccess: (Long) -> Unit)

    suspend fun ensureDataIsInitialized()

    fun getIdParNomModel(nomModel:String): Long

    fun addData(data: _1_5_Vendeur)
    fun updateUnSeulData(data: _1_5_Vendeur)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_1_5_Vendeur>)
    fun deleteUnSeulData(data: _1_5_Vendeur)

    companion object {
        const val TAG = "_1_5_Vendeur"

        val sonDataBaseRef = Firebase.database
            .getReference("00_DataPrototype-04-02")
            .child("_1_5_")
    }

}
