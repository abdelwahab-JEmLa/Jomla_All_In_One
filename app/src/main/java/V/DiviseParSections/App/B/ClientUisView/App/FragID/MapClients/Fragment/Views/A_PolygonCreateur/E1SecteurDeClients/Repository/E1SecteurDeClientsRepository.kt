package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow

interface E1SecteurDeClientsRepository {
    var listCollected: SnapshotStateList<E1SecteurDeClients>

    val progressRepo: MutableStateFlow<Float>
        get() = MutableStateFlow(0f)

    suspend fun ensureDataIsInitialized()

    fun insert(updatedSecteur: E1SecteurDeClients)

    companion object {
        const val TAG = "E1SecteurDeClients"


        val sonDataBaseRef: DatabaseReference =
            _0_0_HeadOfRepositorys_Model.determineRepositoryRef()
            .child("E")
            .child("1")
    }

    suspend fun insertAvecRetureNewVid(secteur1: E1SecteurDeClients): Long
}
