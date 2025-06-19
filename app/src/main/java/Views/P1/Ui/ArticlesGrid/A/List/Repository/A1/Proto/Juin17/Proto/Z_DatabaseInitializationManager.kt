package Views.P1.Ui.ArticlesGrid.A.List.Repository.A1.Proto.Juin17.Proto

import Views.P1.Ui.ArticlesGrid.A.List.Repository.A1.Proto.Juin17.Proto.Z_AppCompt.Repository.Z_AppComptComposeRepositoryProtoJuin17
import Z_CodePartageEntreApps.DataBase.Juin17.Proto.D_AchatOperationRepository.Base.D_AchatOperationDataBaseProtoJuin17
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Z_DatabaseInitializationManager(
    private val achatOperationRepository: D_AchatOperationDataBaseProtoJuin17,
    val appComptComposeRepositoryPJ17: Z_AppComptComposeRepositoryProtoJuin17,
) {
    private val mutex = Mutex()
    private val repositories = mutableMapOf<String, Float>()
    private val scope = CoroutineScope(Dispatchers.Main)

    enum class Repository {
        A_PRODUIT_INFOS,
        D_ACHAT_OPERATION,
        Z_AppComptEntity
    }

    suspend fun initializeAllRepositories(context: Context) {
        val repoNames = Repository.entries.map { it.name }

        mutex.withLock { repoNames.forEach { repositories[it] = 0f } }

        val jobs = listOf(
            scope.launch {
                initRepo(Repository.D_ACHAT_OPERATION.name, context) {
                    achatOperationRepository.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                    }
                }
            },
            scope.launch {
                initRepo(Repository.Z_AppComptEntity.name, context) {
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

    private suspend fun initAppCompt() {
        updateRepoProgress(Repository.Z_AppComptEntity.name, 0.5f)
        updateRepoProgress(Repository.Z_AppComptEntity.name, 1.0f)
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
            updateMainInitDataBaseProgressEtate(progress)
        }
    }

    fun updateMainInitDataBaseProgressEtate(loadingProgress: Float) {
        appComptComposeRepositoryPJ17.currentAppCompt?.let { appCompt ->
            val updatedAppCompt = appCompt.copy(
                mainInitDataBaseProgressEtate = loadingProgress
            )

            appComptComposeRepositoryPJ17.addOrUpdateData(updatedAppCompt)
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
