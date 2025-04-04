package Z_CodePartageEntreApps.Model.A_Produit.Z.Repository

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface A_ProduitRepository {
    var modelDatas: SnapshotStateList<A_Produit>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun addData(data: A_Produit)
    fun updateUnSeulData(data: A_Produit)
    suspend fun updateMultiDatas(datas: SnapshotStateList<A_Produit>)

    companion object {
        private val sonHeadRef = Firebase.database.getReference("A_ProduitDataBase")
        val sonDataBaseRef = sonHeadRef.child("DataBase")
        val iDsDatasFlowUpdateRef = sonHeadRef.child("IDsDatasFlowUpdate")
    }

    fun deleteUnSeulData(data: A_Produit)
}
