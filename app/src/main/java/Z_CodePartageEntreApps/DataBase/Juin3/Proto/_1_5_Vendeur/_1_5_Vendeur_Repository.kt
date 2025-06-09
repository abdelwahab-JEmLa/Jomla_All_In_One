package Z_CodePartageEntreApps.DataBase.Juin3.Proto._1_5_Vendeur

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_5_Vendeur_Repository {
    var modelDatasSnapList: SnapshotStateList<_1_5_Vendeur>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)


    fun addDataAndReturneItVID(
        data: _1_5_Vendeur,
        onAddSuccess: (Long) -> Unit = {},
    )

    suspend fun ensureDataIsInitialized()

    fun getIdParNomModel(nomModel: String): Long

    fun addData(data: _1_5_Vendeur)
    fun updateUnSeulData(data: _1_5_Vendeur)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_1_5_Vendeur>)
    fun deleteUnSeulData(data: _1_5_Vendeur)

    companion object {
        const val TAG = "_1_5_Vendeur"

        val sonDataBaseRef: DatabaseReference =
            GroupeRepositorysProtoAvJuin3Model.getHeadSqlDataBaseRef()
                .child("C_AchatsDataBases")
                .child(
                    "F" +
                            "_" +
                            "ComptsVendeurs"
                            + "DataBAse"
                )
    }
}
