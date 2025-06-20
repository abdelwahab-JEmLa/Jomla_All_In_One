package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
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
        val modelListFlow: List<CategoriesTabelle>,
        val mainProgressRepo: Float
    )

    val dao = appDatabase.categoriesModelDao()
    val repoRef = CategoriesTabelle.caRef

    init {


        CoroutineScope(Dispatchers.IO).launch {
            val initializedData = initializeDataReturn()
            updateRepoState(initializedData)


        }

    }

    suspend fun updateRepoState(data: List<CategoriesTabelle>) {
        withContext(Dispatchers.Main) {
            val newRepoState = RepoState(
                modelListFlow = data,
                mainProgressRepo = 1.0f
            )
            _repoState.value = newRepoState

            data.map {
                if (it.id == 89L) {
                    Log.d("Test", "cate ${it.nom}id ${it.id} pos = ${it.position}")
                }
            }
        }
    }
}
