package Z_CodePartageEntreApps.DataBase.Main.Main

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseInitFactory_M3CouleurProduitInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.DataBaseCreationFactory13TarificationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory.DataBaseInitFactory_2ClientProtoJuil28
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase11.Factory.DataBaseInitFactory_11AchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase14VentPeriode.Factory.DataBaseInitFactory_14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.DataBaseInitFactory_15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory.DataBaseInitFactory_16CategorieProduit
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase19.Factory.DataBaseInitFactory_19Etudiant
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase20.Factory.DataBaseInitFactory_M20ObsarvationEtudion
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.DataBaseInitFactory_8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.DataBaseInit_Z_AppCompt
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WDatabaseInitializationManager(
    private val achatOperationRepository: DataBaseFactoryDCouleurAchatOperation,
    val appComptComposeRepositoryPJ17: Repo9AppCompt,
    val dataBaseInitZ_AppCompt: DataBaseInit_Z_AppCompt,
    val dataBaseFactory_B1CouleurOuGoutProduitDataBase: DataBaseInitFactory_M3CouleurProduitInfos,
    val dataBaseCreationFactory13TarificationInfos: DataBaseCreationFactory13TarificationInfos,
    val dataBaseInitFactory_14VentPeriode: DataBaseInitFactory_14VentPeriode,
    val dataBaseInitFactory_15Grossist: DataBaseInitFactory_15Grossist,
    val dataBaseInitFactory_11AchatOperation: DataBaseInitFactory_11AchatOperation,
    val dataBaseInitFactory_8BonVent: DataBaseInitFactory_8BonVent,
    val dataBaseInitFactory_16CategorieProduit: DataBaseInitFactory_16CategorieProduit,
    val dataBaseInitFactory_2ClientProtoJuil28: DataBaseInitFactory_2ClientProtoJuil28,
    val dataBaseInitFactory_19Etudiant: DataBaseInitFactory_19Etudiant,
    val dataBaseInitFactory_M_20ObsarvationEtudion: DataBaseInitFactory_M20ObsarvationEtudion,
) {
    private val mutex = Mutex()
    private val repositories = mutableMapOf<String, Float>()
    private val scope = CoroutineScope(Dispatchers.Main)

    enum class Repository {
        FCouleurVentOperation,
        Z_AppComptEntity,
        M3CouleurProduitInfos_Entit,
        M13TarificationInfosEntity,
        A_PRODUIT_INFOS,
        M14VentPeriode_Entity,
        M15Grossist_Entity,
        M11AchatOperation_Entity,
        Entity_8BonVent,
        Entity_16CategorieProduit,
        Entity_2Client,
        Entity_19Etudiant,
        Entity_M20ObsarvationEtudion,
    }

    suspend fun initializeAllRepositories(context: Context) {
        val repoNames = Repository.entries.map { it.name }

        mutex.withLock { repoNames.forEach { repositories[it] = 0f } }

        val jobs = listOf(
            scope.launch {
                initRepo(Repository.Z_AppComptEntity.name, context) {
                    dataBaseInitZ_AppCompt.init(
                        isInternetAvailable = isInternetAvailable(
                            context
                        )
                    ) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                    }
                    dataBaseInitZ_AppCompt.triggerUpdateFbParTimestampsListener()
                }
            },
            scope.launch {
                initRepo(Repository.M3CouleurProduitInfos_Entit.name, context) {
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
                initRepo(Repository.M14VentPeriode_Entity.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                        factory.triggerUpdateFbParTimestampsListener()}
                    }
                }
            } ,
            scope.launch {
               val factory =dataBaseInitFactory_15Grossist
                initRepo(Repository.M15Grossist_Entity.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        factory.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
            scope.launch {
               val factory =dataBaseInitFactory_11AchatOperation
                initRepo(Repository.M11AchatOperation_Entity.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                        factory.triggerUpdateFbParTimestampsListener()}
                    }
                }
            } ,
            scope.launch {
               val factory =dataBaseInitFactory_8BonVent
                initRepo(Repository.Entity_8BonVent.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                      //  factory.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
            scope.launch {
               val factory =dataBaseInitFactory_16CategorieProduit
                initRepo(Repository.Entity_16CategorieProduit.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        factory.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
            scope.launch {
               val factory =dataBaseInitFactory_2ClientProtoJuil28
                initRepo(Repository.Entity_2Client.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        factory.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
            scope.launch {
               val factory =dataBaseInitFactory_19Etudiant
                initRepo(Repository.Entity_19Etudiant.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        factory.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
            scope.launch {
               val factory =dataBaseInitFactory_M_20ObsarvationEtudion
                initRepo(Repository.Entity_M20ObsarvationEtudion.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        factory.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
        )

        jobs.joinAll()
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
