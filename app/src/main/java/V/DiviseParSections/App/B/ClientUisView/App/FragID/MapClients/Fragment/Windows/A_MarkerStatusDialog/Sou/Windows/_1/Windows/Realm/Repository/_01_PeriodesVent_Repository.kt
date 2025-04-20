package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing Periods of Sales data
 */

interface _01_PeriodesVent_Repository {
    val modelDatasSnapList: SnapshotStateList<_01_PeriodesVent>
    val progressRepo: StateFlow<Float>
    val dataChangedEvent: StateFlow<Long> // New StateFlow to notify data changes

    fun notifieDataChange()

    companion object {
        private val _01_HeadOfRepositorys_RepositoryRef = Firebase.database
            .getReference("01_DataPrototype-04-19")

        val sonDataBaseRef = _01_HeadOfRepositorys_RepositoryRef
            .child("_01_PeriodesVent")
    }
}
