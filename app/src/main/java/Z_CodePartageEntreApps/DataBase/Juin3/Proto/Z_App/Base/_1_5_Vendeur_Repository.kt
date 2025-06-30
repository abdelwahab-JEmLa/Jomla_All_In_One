package Z_CodePartageEntreApps.DataBase.Juin3.Proto.Z_App.Base

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.Z_AppCompt
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow

interface _1_5_Vendeur_Repository {
    var modelDatasSnapList: SnapshotStateList<Z_AppCompt>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)


    fun addDataAndReturneItVID(
        data: Z_AppCompt,
        onAddSuccess: (Long) -> Unit = {},
    )

    suspend fun ensureDataIsInitialized()

    fun getIdParNomModel(nomModel: String): Long

    fun addData(data: Z_AppCompt)
    fun updateUnSeulData(data: Z_AppCompt)
    suspend fun updateMultiDatas(datas: SnapshotStateList<Z_AppCompt>)
    fun deleteUnSeulData(data: Z_AppCompt)

    companion object {
        const val TAG = "Z_AppCompt"

        val sonDataBaseRef: DatabaseReference =Z_AppCompt.ref
    }
}
