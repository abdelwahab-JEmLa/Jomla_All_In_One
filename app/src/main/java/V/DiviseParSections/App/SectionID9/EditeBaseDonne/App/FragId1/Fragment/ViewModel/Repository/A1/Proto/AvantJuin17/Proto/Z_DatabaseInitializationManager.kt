package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.AvantJuin17.Proto

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.D_AchatOperation.Repository.D_AchatOperation
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Repository.B.Init.onLoadCategoriesFromCsvD_AchatOperation
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Repository.B.Init.onLoadFromFireBaseD_AchatOperation
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Repository.D_AchatOperationRepository
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Z_DatabaseInitializationManager(
    private val appComptComposeRepository: Z_AppComptComposeRepository,
    private val achatOperationRepository: D_AchatOperationRepository,
) {
    private val mutex = Mutex()
    private val repositories = mutableMapOf<String, Float>()
    private val scope = CoroutineScope(Dispatchers.Main)

    enum class Repository {
        A_PRODUIT_INFOS,
        D_ACHAT_OPERATION,
        Z_APP_COMPT
    }

    suspend fun initializeAllRepositories(context: Context, appDatabase: AppDatabase) {
        val repoNames = Repository.entries.map { it.name }

        mutex.withLock { repoNames.forEach { repositories[it] = 0f } }

        val jobs = listOf(
            scope.launch {
                initRepo(Repository.A_PRODUIT_INFOS.name, context) {
                    achatOperationRepository.initProduitInfos(it, appDatabase) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                    }
                }
            },
            scope.launch {
                initRepo(Repository.D_ACHAT_OPERATION.name, context) {
                    initAchatOperation(it, appDatabase)
                }
            },
            scope.launch {
                initRepo(Repository.Z_APP_COMPT.name, context) {
                    initAppCompt()
                }
            }
        )

        jobs.forEach { it.join() }
        updateProgress(1.0f)
    }

    private suspend fun initRepo(
        name: String,
        context: Context,
        initializer: suspend (Context) -> Unit
    ) {
        try {
            updateRepoProgress(name, 0.2f)
            initializer(context)
            markComplete(name)
        } catch (e: Exception) {
            markComplete(name)
        }
    }


    private suspend fun initAchatOperation(context: Context, appDatabase: AppDatabase) {
        val dao = appDatabase.D_AchatOperationDao()
        if (!dao.isTableEmpty()) return

        updateRepoProgress(Repository.D_ACHAT_OPERATION.name, 0.4f)
        val data: List<D_AchatOperation> = if (isInternetAvailable(context)) {
            updateRepoProgress(Repository.D_ACHAT_OPERATION.name, 0.6f)
            onLoadFromFireBaseD_AchatOperation()
        } else {
            onLoadCategoriesFromCsvD_AchatOperation()
        }
        updateRepoProgress(Repository.D_ACHAT_OPERATION.name, 0.8f)
        dao.insertAll(data)
    }

    private suspend fun initAppCompt() {
        updateRepoProgress(Repository.Z_APP_COMPT.name, 0.5f)
        updateRepoProgress(Repository.Z_APP_COMPT.name, 1.0f)
    }

    private suspend fun updateRepoProgress(name: String, progress: Float) {
        mutex.withLock {
            repositories[name] = progress
            updateProgress()
        }
    }

    private suspend fun markComplete(name: String) {
        mutex.withLock {
            repositories[name] = 1.0f
            updateProgress()
        }
    }

    private fun updateProgress(finalProgress: Float? = null) {
        val progress =
            finalProgress ?: if (repositories.isEmpty()) 0f else repositories.values.average()
                .toFloat()
        scope.launch {
            appComptComposeRepository.updateMainInitDataBaseProgressEtate(progress)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as android.net.ConnectivityManager
            connectivityManager.activeNetworkInfo?.isConnected == true
        } catch (e: Exception) {
            false
        }
    }
}
