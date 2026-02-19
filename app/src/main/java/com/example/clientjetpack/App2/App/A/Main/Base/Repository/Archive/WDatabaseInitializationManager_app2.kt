package com.example.clientjetpack.App2.App.A.Main.Base.Repository.Archive

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.DataBaseInitFactory_M3CouleurProduitInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory.DataBaseInitFactory_2ClientProtoJuil28
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase16.Factory.DataBaseInitFactory_16CategorieProduit
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager
import android.content.Context
import android.net.ConnectivityManager
import com.example.clientjetpack.App2.App.A.Main.Base.Repository.FocusedValuesGetter_app2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WDatabaseInitializationManager_app2(
    val context: Context,
    val focusedValuesGetter_app2: FocusedValuesGetter_app2,
    val appComptComposeRepositoryPJ17: Repo9AppCompt,
    val dataBaseFactory_B1CouleurOuGoutProduitDataBase: DataBaseInitFactory_M3CouleurProduitInfos,
    val dataBaseInitFactory_16CategorieProduit: DataBaseInitFactory_16CategorieProduit,
    val dataBaseInitFactory_2ClientProtoJuil28: DataBaseInitFactory_2ClientProtoJuil28,
) {
    private val mutex = Mutex()
    private val repositories = mutableMapOf<String, Float>()
    private val scope = CoroutineScope(Dispatchers.Main)
    val repoScope = CoroutineScope(Dispatchers.IO)

    init {
        repoScope.launch {
            initializeAllRepositories(context)
        }
    }

    enum class Repository {
        M3CouleurProduitInfos_Entit,
        A_PRODUIT_INFOS,
        Entity_16CategorieProduit,
    }

    suspend fun initializeAllRepositories(context: Context) {
        val repoNames = Repository.entries.map { it.name }

        mutex.withLock { repoNames.forEach { repositories[it] = 0f } }

        val jobs = listOf(

            scope.launch {
                val factory =dataBaseInitFactory_2ClientProtoJuil28
                initRepo(WDatabaseInitializationManager.Repository.Entity_2Client.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        factory.triggerUpdateFbParTimestampsListener()
                    }
                }
            } ,
            scope.launch {
                initRepo(Repository.M3CouleurProduitInfos_Entit.name, context) {
                    dataBaseFactory_B1CouleurOuGoutProduitDataBase.init(
                        isInternetAvailable = isInternetAvailable(
                            context
                        )
                    ) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        dataBaseFactory_B1CouleurOuGoutProduitDataBase.triggerUpdateFbParTimestampsListener()
                    }
                }
            },
            scope.launch {
                val factory = dataBaseInitFactory_16CategorieProduit
                initRepo(Repository.Entity_16CategorieProduit.name, context) {
                    factory.init(isInternetAvailable = isInternetAvailable(context)) { name, progress ->
                        scope.launch {
                            updateRepoProgress(name, progress)
                        }
                        factory.triggerUpdateFbParTimestampsListener()
                    }
                }
            },
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
        focusedValuesGetter_app2.active_Central_Values.let { active_Central_Values ->
            val updatedAppCompt = active_Central_Values.copy(
                mainInitDataBaseProgressEtate = loadingProgress
            )
            focusedValuesGetter_app2.update_ActiveCentralValues_app2(updatedAppCompt)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            connectivityManager.activeNetworkInfo?.isConnected == true
        } catch (e: Exception) {
            false
        }
    }
}
