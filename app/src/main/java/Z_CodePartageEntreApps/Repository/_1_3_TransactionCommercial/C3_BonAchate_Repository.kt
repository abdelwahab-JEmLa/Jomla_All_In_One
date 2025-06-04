package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.D.NonTermineDisplayer.Windows.Test.C3_BonAchate
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3Model
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
            GroupeRepositorysProtoAvJuin3Model.getHeadSqlDataBaseRef()
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
