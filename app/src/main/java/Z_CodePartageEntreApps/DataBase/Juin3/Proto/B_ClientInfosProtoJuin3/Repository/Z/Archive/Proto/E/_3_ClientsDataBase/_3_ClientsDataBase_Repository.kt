package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.E._3_ClientsDataBase

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow

interface _3_ClientsDataBase_Repository {
    var modelDatasSnapList: SnapshotStateList<_3_ClientsDataBaseProtoE>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()


    fun addData(data: _3_ClientsDataBaseProtoE)
    fun updateUnSeulData(data: _3_ClientsDataBaseProtoE)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_3_ClientsDataBaseProtoE>)
    fun deleteUnSeulData(data: _3_ClientsDataBaseProtoE)

    companion object {
        const val TAG = "_3_ClientsDataBaseProtoE"

        val sonDataBaseRef: DatabaseReference =
            GroupeRepositorysProtoAvJuin3Model.getHeadSqlDataBaseRef()
                .child("D_Clients")
                .child("A_MainDataBase")
    }


    fun addMultiDATAsEtReturnVIDsList(
        dataList: List<_3_ClientsDataBaseProtoE>,
        onAddSuccess: (List<Long>) -> Unit,
    )
}
