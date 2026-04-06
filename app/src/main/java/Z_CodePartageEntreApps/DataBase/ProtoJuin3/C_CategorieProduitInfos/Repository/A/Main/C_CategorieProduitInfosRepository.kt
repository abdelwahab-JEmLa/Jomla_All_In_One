package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main

import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Modules.Base.AppDatabase
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.B.Init.initializeDataReturn
import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class C_CategorieProduitInfosRepository(
    val context: Context,
    appDatabase: AppDatabase,
) {
    val repoTAG = "C_CategorieProduitInfosRepository"

    val _repoState = MutableStateFlow<RepoState?>(null)
    val repoState: StateFlow<RepoState?> = _repoState.asStateFlow()

    data class RepoState(
        val modelListFlow: List<M16CategorieProduit>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.dao_16CategorieProduit()
    val repoRef = M16CategorieProduit.ref

    init {


        CoroutineScope(Dispatchers.IO).launch {
            val initializedData = initializeDataReturn()
            updateRepoState(initializedData)


        }

    }

    suspend fun updateRepoState(data: List<M16CategorieProduit>) {
        withContext(Dispatchers.Main) {
            val newRepoState = RepoState(
                modelListFlow = data,
                mainProgressRepo = 1.0f
            )
            _repoState.value = newRepoState

            data.map {
                if (it.id == 89L) {
                    Log.d("Test", "cate ${it.nom}keyID ${it.id} pos = ${it.position}")
                }
            }
        }
    }
}
