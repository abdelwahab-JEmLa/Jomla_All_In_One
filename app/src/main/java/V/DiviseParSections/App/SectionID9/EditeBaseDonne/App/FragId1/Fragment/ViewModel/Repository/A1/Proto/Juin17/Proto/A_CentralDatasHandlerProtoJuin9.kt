package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.content.Context
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class A_CentralDatasHandlerProtoJuin17(
    private val appDatabase: AppDatabase,
    private val databaseInitializationManager: Z_DatabaseInitializationManager,
    private val context: Context
) {
    private val initScope = CoroutineScope(Dispatchers.IO)

    init {
        initScope.launch {
            databaseInitializationManager.initializeAllRepositories(context)
        }
    }
}
