package Z_CodePartageEntreApps.DataBase

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo14.Repository.Base.Factory.DataBaseInitFactory_14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseInitFactory_B1CouleurOuGoutProduitDataBase
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.DataBaseCreationFactory13TarificationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Z_AppComptRepositoryProtoJuin17
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WDatabaseInitializationManager(
    private val achatOperationRepository: DataBaseFactoryDCouleurAchatOperation,
    val appComptComposeRepositoryPJ17: Repo9AppCompt,
    val z_AppComptRepositoryProtoJuin17: Z_AppComptRepositoryProtoJuin17,
    val dataBaseFactory_B1CouleurOuGoutProduitDataBase: DataBaseInitFactory_B1CouleurOuGoutProduitDataBase,
    val dataBaseCreationFactory13TarificationInfos: DataBaseCreationFactory13TarificationInfos,
    val dataBaseInitFactory_14VentPeriode: DataBaseInitFactory_14VentPeriode,
) {
    private val mutex = Mutex()
    private val repositories = mutableMapOf<String, Float>()
    private val scope = CoroutineScope(Dispatchers.Main)

    enum class Repository {
        GBonVentEntity,
        FCouleurVentOperation,
        Z_AppComptEntity,
        D_ACHAT_OPERATION,
        M13TarificationInfosEntity,
        A_PRODUIT_INFOS,
        M14VentPeriode,
    }

    suspend fun initializeAllRepositories(context: Context) {
        val repoNames = Repository.entries.map { it.name }

        mutex.withLock { repoNames.forEach { repositories[it] = 0f } }

        val jobs = listOf(
            scope.launch {
                initRepo(Repository.Z_AppComptEntity.name, context) {
                    z_AppComptRepositoryProtoJuin17.init(
                        isInternetAvailable = isInternetAvailable(
                            context
                        )
                    ) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                    }
                    z_AppComptRepositoryProtoJuin17.triggerUpdateFbParTimestampsListener()
                }
            },
            scope.launch {
                initRepo(Repository.D_ACHAT_OPERATION.name, context) {
                    achatOperationRepository.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        achatOperationRepository.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
            scope.launch {
                initRepo(Repository.FCouleurVentOperation.name, context) {
                    dataBaseFactory_B1CouleurOuGoutProduitDataBase.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        dataBaseFactory_B1CouleurOuGoutProduitDataBase.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
            scope.launch {
                initRepo(Repository.M13TarificationInfosEntity.name, context) {
                    dataBaseCreationFactory13TarificationInfos.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        dataBaseCreationFactory13TarificationInfos.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
            scope.launch {
               val factory =dataBaseInitFactory_14VentPeriode
                initRepo(Repository.M14VentPeriode.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        factory.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
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

            appComptComposeRepositoryPJ17.upsert(updatedAppCompt)
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
