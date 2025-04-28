package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadSQLRepositorys.Companion._0_0_HeadOfRepositorys_RepositoryRef
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow

interface E1SecteurDeClientsRepository {
    var listState: SnapshotStateList<E1SecteurDeClients>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun ensureDataIsInitialized()

    companion object {
        const val TAG = "E1SecteurDeClients"

        val sonDataBaseRef = _0_0_HeadOfRepositorys_RepositoryRef
            .child("1")
            .child("3")
    }


    fun getOuvertE1SecteurDeClients(): E1SecteurDeClients?
}
