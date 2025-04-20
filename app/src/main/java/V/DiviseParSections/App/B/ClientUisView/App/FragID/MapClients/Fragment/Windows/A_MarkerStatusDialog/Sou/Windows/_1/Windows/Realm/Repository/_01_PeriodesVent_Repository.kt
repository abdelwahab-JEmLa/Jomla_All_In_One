package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing Periods of Sales data
 */

interface _01_PeriodesVent_Repository {
    val modelDatasSnapList: SnapshotStateList<_01_PeriodesVent>
    val progressRepo: StateFlow<Float>
    val dataChangedEvent: StateFlow<Long> // New StateFlow to notify data changes

    suspend fun refreshData()
    fun notifieDataChange()

    companion object {
        // Static Firebase reference
        val sonDataBaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("periodesVente")
    }
}
