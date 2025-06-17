package Z_CodePartageEntreApps.DataBase.Juin17.Proto.D_AchatOperationRepository.Base

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.D_AchatOperation.Repository.D_AchatOperation
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager.Repository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Base.B.Init.onLoadCategoriesFromCsvD_AchatOperation
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Base.B.Init.onLoadFromFireBaseD_AchatOperation
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Base.C.SQL.D_AchatOperationDao
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.B.Init.isInternetAvailable
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class D_AchatOperationDataBasePJ17(
    val dao: D_AchatOperationDao,
) {
    val repoTAG = "D_AchatOperation"
    val repoRef = D_AchatOperation.caRef
    private val composScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        context: Context,
        updateRepoProgress: (String,Float) -> Unit
    ) {
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

    fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        dataAvecTigerUpdate: D_AchatOperation
    ) {
        composScope.launch {
            if (existingIndex >= 0) {
                dao.update(dataAvecTigerUpdate)
                batchFireBaseUpdateD_AchatOperation(listOf(dataAvecTigerUpdate))
            } else {
                dao.insert(dataAvecTigerUpdate)
                batchFireBaseUpdateD_AchatOperation(listOf(dataAvecTigerUpdate))
            }
        }
    }


    private suspend fun batchFireBaseUpdateD_AchatOperation(datas: List<D_AchatOperation>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            updates[data.bsonObjectId] = data
        }
        val firebaseRef = D_AchatOperation.caRef
        firebaseRef.updateChildren(updates).await()
    }
}
