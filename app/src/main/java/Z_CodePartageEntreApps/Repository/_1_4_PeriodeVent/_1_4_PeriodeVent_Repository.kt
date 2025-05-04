package Z_CodePartageEntreApps.Repository._1_4_PeriodeVent

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_4_PeriodeVent_Repository {
    var modelDatasSnapList: SnapshotStateList<_1_4_PeriodeVent>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)
    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()

    fun addData(data: _1_4_PeriodeVent)

    fun addDataAndReturneItVID(
        data: _1_4_PeriodeVent,
        onAddSuccess:(Long) -> Unit = {}
    )

    fun updateUnSeulData(data: _1_4_PeriodeVent)
    suspend fun updateMultiDatas(datas: SnapshotStateList<_1_4_PeriodeVent>)
    fun deleteUnSeulData(data: _1_4_PeriodeVent)
    fun getByMainVAl(): Long

    companion object {
        const val TAG = "_1_4_PeriodeVent"

        val sonDataBaseRef: DatabaseReference =
            _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
                .child("C_AchatsDataBases")
                .child(
                    "E" +
                            "_" +
                            "PeriodeVent"
                            + "DataBAse"

                )
    }
}
