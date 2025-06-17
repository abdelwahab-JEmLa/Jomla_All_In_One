package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.AvantJuin17.Proto

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.content.Context
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class A_CentralDatasHandlerProtoJuin9(
    private val appDatabase: AppDatabase,
    private val databaseInitializationManager: Z_DatabaseInitializationManager,
    val appComptComposeRepository: Z_AppComptComposeRepository,
    val produitInfosComposeRepository: A_ProduitInfosComposeRepository,
    val achatOperationComposeRepository: D_AchatOperationComposeRepository,
    private val context: Context
) {
    private val initScope = CoroutineScope(Dispatchers.IO)

    init {
        initScope.launch {
            try {
                databaseInitializationManager.initializeAllRepositories(context)
            } catch (e: Exception) {
                appComptComposeRepository.updateMainInitDataBaseProgressEtate(1.0f)
            }
        }
    }
}
