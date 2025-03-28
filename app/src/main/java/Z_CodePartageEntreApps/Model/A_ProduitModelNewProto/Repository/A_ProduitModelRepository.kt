package Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository

import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.firebaseDatabase
import Z_CodePartageEntreApps.Model._ModelAppsFather.Companion.ref_HeadOfModels
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface A_ProduitModelRepository{
    var modelDatas: SnapshotStateList<A_ProduitModel>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    fun checkConnectivity()
    fun checkConnectivityAndSync()

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<A_ProduitModel>, Flow<Float>>
    fun restartDatabaseListener()
    fun stopDatabaseListener()

    fun updateData(data: A_ProduitModel? = null,)
    suspend fun updateDatas(datas: SnapshotStateList<A_ProduitModel>)

    companion object {
        val caReference = ref_HeadOfModels.child("A_ProduitModel")
        val caReferenceProtoDecembre = ref_HeadOfModels.child("produits")
        val autreRef = firebaseDatabase
            .getReference("1_SQL_PROTO_MARS").child("A_ProduitModel")
    }
}



