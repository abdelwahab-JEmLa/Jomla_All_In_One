package Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository

import Z_CodePartageEntreApps.Model.I_CategoriesProduits
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface I_CategoriesProduitsNewProtoRepository{
    var modelDatas: SnapshotStateList<I_CategoriesProduits>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<I_CategoriesProduits>, Flow<Float>>
    fun stopDatabaseListener()
    fun checkConnectivityAndSync()
    suspend fun updateDatas(datas: SnapshotStateList<I_CategoriesProduits>)
    fun updateData(data: I_CategoriesProduits? = null,)

    companion object {
        val caReference = Firebase.database.getReference("produits")
    }
}



