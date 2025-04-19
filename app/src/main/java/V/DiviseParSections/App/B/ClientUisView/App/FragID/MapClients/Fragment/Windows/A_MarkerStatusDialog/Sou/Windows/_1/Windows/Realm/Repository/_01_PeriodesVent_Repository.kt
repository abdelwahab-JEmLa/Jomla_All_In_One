package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.StateFlow

interface _01_PeriodesVent_Repository {
    var modelDatasSnapList: SnapshotStateList<_01_PeriodesVent>

    val progressRepo: StateFlow<Float>
    suspend fun refreshData()

    companion object {
        private val _01_HeadOfRepositorys_RepositoryRef = Firebase.database
            .getReference("01_DataPrototype-04-19")

        val sonDataBaseRef = _01_HeadOfRepositorys_RepositoryRef
            .child("_01_PeriodesVent")
    }
}
