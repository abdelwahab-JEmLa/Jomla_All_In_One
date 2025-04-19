package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Repository.Companion._0_0_HeadOfRepositorys_RepositoryRef
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface _01_PeriodesVent_Repository {
    var modelDatasSnapList: SnapshotStateList<_01_PeriodesVentNoSQl>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    companion object {

        val sonDataBaseRef = _0_0_HeadOfRepositorys_RepositoryRef
            .child("_2_")
            .child("1_ProduitsDataBase")
    }
}

class MainModelsRepository{
    var modelDatasSnapList: SnapshotStateList<_01_PeriodesVentNoSQl>

}
