package Z_CodePartageEntreApps.Model.I_CategorieProduits.A.Repository

import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow

interface I_CategorieProduitsRepository {
    var modelDatas: SnapshotStateList<I_CategorieProduits>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)


    fun addData(data: I_CategorieProduits)
    fun updateUnSeulData(data: I_CategorieProduits? = null)
    suspend fun updateMultiDatas(datas: SnapshotStateList<I_CategorieProduits>)

    companion object {
        private val sonHeadRef = Firebase.database.getReference("I_CategorieProduits")
        val caReference = sonHeadRef.child("DataBase")
        val iDsDatasFlowUpdateRef = sonHeadRef.child("IDsDatasFlowUpdate")
    }

    fun deleteUnSeulData(data: I_CategorieProduits)
}
