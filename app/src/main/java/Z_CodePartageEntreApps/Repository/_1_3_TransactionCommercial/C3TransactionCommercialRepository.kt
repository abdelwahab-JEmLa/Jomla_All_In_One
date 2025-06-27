package Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.TransactionCommercial
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface C3TransactionCommercialRepository {
    var modelDatasSnapList: SnapshotStateList<TransactionCommercial>
    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)
    val activeId: MutableStateFlow<Long>

    suspend fun ensureDataIsInitialized()

    companion object {
        const val TAG = "TransactionCommercial"

        val sonDataBaseRef = TransactionCommercial.caRef

    }

    fun getOuvert_1_3_TransactionCommercial(): TransactionCommercial?
}
