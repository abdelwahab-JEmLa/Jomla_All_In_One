package Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository

import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.A_Produit
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface AProto_ProduitDataBaseRepository {
    var modelDatas: SnapshotStateList<A_Produit>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun addData(data: A_Produit)
    fun updateUnSeulData(data: A_Produit? = null)
    suspend fun updateMultiDatas(datas: SnapshotStateList<A_Produit>)

    companion object {
        val caReference = Firebase.database.getReference("A_ProduitDataBase")
    }

    fun deleteUnSeulData(data: A_Produit)
}
