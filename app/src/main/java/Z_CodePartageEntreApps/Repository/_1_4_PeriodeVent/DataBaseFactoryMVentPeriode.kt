package Z_CodePartageEntreApps.Repository._1_4_PeriodeVent

import V.DiviseParSections.App.Shared.Repository.MVentPeriode
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface DataBaseFactoryMVentPeriode {
    fun addData(data: MVentPeriode)
    fun updateUnSeulData(data: MVentPeriode)

    companion object {
        const val TAG = "MVentPeriode"

        val sonDataBaseRef = MVentPeriode.ref
    }

    var modelDatasSnapList: SnapshotStateList<MVentPeriode>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)
    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()


    fun addDataAndReturneItVID(
        data: MVentPeriode,
        onAddSuccess:(Long) -> Unit = {}
    )

    suspend fun updateMultiDatas(datas: SnapshotStateList<MVentPeriode>)
    fun deleteUnSeulData(data: MVentPeriode)
    fun getByMainVAl(): Long


    fun addOrUpdatedDataBase(existingIndex: Int, dataAvecTigerUpdate: MVentPeriode)
}
