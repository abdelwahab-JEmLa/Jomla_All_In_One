package Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository

import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.AProto_ProduitDataBase
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface AProto_ProduitDataBaseRepository {
    var modelDatas: SnapshotStateList<AProto_ProduitDataBase>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun addData(data: AProto_ProduitDataBase)
    fun updateUnSeulData(data: AProto_ProduitDataBase? = null)
    suspend fun updateMultiDatas(datas: SnapshotStateList<AProto_ProduitDataBase>)

    companion object {
        val caReference = Firebase.database.getReference("A_ProduitDataBase")
    }

    fun deleteUnSeulData(data: AProto_ProduitDataBase)
}
