package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Models._00_VentsHistoriquesDataBase
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository interface for managing Periods of Sales data
 */

interface _00_VentsHistoriquesDataBase_Repository {
    val modelDatasSnapList: SnapshotStateList<_00_VentsHistoriquesDataBase>
    val progressRepo: StateFlow<Float>
    val dataChangedEvent: StateFlow<Long> // New StateFlow to notify data changes

    fun notifieDataChange()

    companion object {
        private val _01_HeadOfRepositorys_RepositoryRef = Firebase.database
            .getReference("01_DataPrototype-04-19")

        val sonDataBaseRef = _01_HeadOfRepositorys_RepositoryRef
            .child("_00_VentsHistoriquesDataBase")
    }
}
