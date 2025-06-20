package Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager.Repository
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.Init.onLoadCategoriesFromCsv
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.Init.onLoadFromFireBase
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.SQL.Z_AppComptDao
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Z_AppComptRepositoryProtoJuin17(
    val dao: Z_AppComptDao,
) {
    val repoEntityName ="Z_AppComptRepositoryProtoJuin17"
    val repoTAG = repoEntityName

    val repoRef =Z_AppCompt.caRef


    private val composScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        isInternetAvailable: Boolean,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(Repository.Z_AppComptEntity.name, 0.4f)

        val data: List<Z_AppCompt> = if (isInternetAvailable) {

            updateRepoProgress(Repository.Z_AppComptEntity.name, 0.6f)

            onLoadFromFireBase()
        } else {
            onLoadCategoriesFromCsv()
        }

        updateRepoProgress(Repository.Z_AppComptEntity.name, 0.8f)

        Log.d(
            repoTAG,
            "${data.map { it.nom }}"
        )

        dao.insertAll(data)
    }

    fun addOrUpdatedDataBase(
        existingIndex: Int,
        dataAvecTigerUpdate: Z_AppCompt
    ) {
        composScope.launch {
            if (existingIndex >= 0) {
                dao.update(dataAvecTigerUpdate)
                batchFireBaseUpdateZ_AppCompt(listOf(dataAvecTigerUpdate))
            } else {
                dao.insert(dataAvecTigerUpdate)
                batchFireBaseUpdateZ_AppCompt(listOf(dataAvecTigerUpdate))
            }
        }
    }


    private suspend fun batchFireBaseUpdateZ_AppCompt(datas: List<Z_AppCompt>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.bsonObjectId] = data
        }
        repoRef.updateChildren(updates).await()
    }
}
