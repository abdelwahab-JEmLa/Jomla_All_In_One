package Z_CodePartageEntreApps.Model.I_CategorieProduits.A.Repository

import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.firebaseDatabase
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface I_CategorieProduitsRepository {
    var modelDatas: SnapshotStateList<I_CategorieProduits>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)


    fun addData(data: I_CategorieProduits)
    fun updateUnSeulData(data: I_CategorieProduits? = null)
    suspend fun updateMultiDatas(datas: SnapshotStateList<I_CategorieProduits>)

    companion object {
        val caReference = firebaseDatabase.getReference("I_CategorieProduits")
    }

    fun deleteUnSeulData(data: I_CategorieProduits)
}
