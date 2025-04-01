package Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository

import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.AProto_ProduitDataBase
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.firebaseDatabase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface AProto_ProduitDataBaseRepository {
    var modelDatas: SnapshotStateList<AProto_ProduitDataBase>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)


    fun addData(data: AProto_ProduitDataBase)
    fun updateUnSeulData(data: AProto_ProduitDataBase? = null)
    suspend fun updateMultiDatas(datas: SnapshotStateList<AProto_ProduitDataBase>)

    companion object {
        val caReference = firebaseDatabase.getReference("AProto_ProduitDataBase")
    }

    fun deleteUnSeulData(data: AProto_ProduitDataBase)
}
