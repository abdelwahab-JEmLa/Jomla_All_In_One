package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.ViewModel.Repository

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.DataBaseInit_Z_AppCompt
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class W_DatabaseInitializationManager_SeparatedDataBasesCodingPattern(
    val appComptComposeRepositoryPJ17: Repo9AppCompt,
    val dataBaseInitZ_AppCompt: DataBaseInit_Z_AppCompt,
    val dataBaseInitFactory_19Etudiant: DataBaseInitFactory_19Etudiant,
) {
    private val mutex = Mutex()
    private val repositories = mutableMapOf<String, Float>()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    fun cancel() = scope.cancel()

    enum class Repository {
        Z_AppComptEntity,
        Entity_19Etudiant,
        Entity_M20ObsarvationEtudion,
    }

    suspend fun initializeAllRepositories(context: Context) {
        val repoNames = Repository.entries.map { it.name }

        mutex.withLock { repoNames.forEach { repositories[it] = 0f } }

        val internet = isInternetAvailable(context)
        val allInits: List<suspend () -> Unit> = listOf(
            {
                initRepo(Repository.Z_AppComptEntity.name, context) {
                    dataBaseInitZ_AppCompt.init(isInternetAvailable = internet) { name, progress -> scope.launch { updateRepoProgress(name, progress) } }
                    dataBaseInitZ_AppCompt.triggerUpdateFbParTimestampsListener()
                }
            },

            {
                val factory = dataBaseInitFactory_19Etudiant
                initRepo(Repository.Entity_19Etudiant.name, context) {
                    factory.init(isInternetAvailable = internet) { name, progress -> scope.launch { updateRepoProgress(name, progress) } }
                    factory.triggerUpdateFbParTimestampsListener()
                }
            },
        )

        allInits.chunked(3).forEach { chunk ->
            chunk.map { scope.launch { it() } }.joinAll()
        }
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
            Log.d("DBInit", "initialized ")
        } catch (e: Exception) {
            markComplete(name)
            Log.e("DBInit", "Erreur init $name: ${e.message}", e) // ← ajouter
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
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }
}
