package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.B.Models.C3_BonAchate
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow

interface C3_BonAchate_Repository {
    var modelDatasSnapList: SnapshotStateList<C3_BonAchate>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)
    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()

    companion object {
        const val TAG = "C3_BonAchate"

        val sonDataBaseRef: DatabaseReference =
            _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
                .child("C_AchatsDataBases")
                .child(
                    "D" +
                            "_" +
                            "TransactionCommercial"
                            + "DataBAse"

                )
    }


    fun getOuvert_1_3_TransactionCommercial(): C3_BonAchate?
}
