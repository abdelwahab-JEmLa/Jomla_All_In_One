package Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository

import Z_CodePartageEntreApps.Model.A_ProduitModel
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface A_ProduitModelNewProtoRepository{
    var modelDatas: SnapshotStateList<A_ProduitModel>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<A_ProduitModel>, Flow<Float>>
    fun stopDatabaseListener()
    fun checkConnectivityAndSync()
    suspend fun updateDatas(datas: SnapshotStateList<A_ProduitModel>)
    fun updateData(data: A_ProduitModel? = null,)

    companion object {
        val caReference = Firebase.database.getReference("produits")
    }
}



