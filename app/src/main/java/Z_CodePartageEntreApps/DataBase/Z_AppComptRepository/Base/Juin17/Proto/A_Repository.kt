package Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto

import Views.P1._ArticlesStartFacade.B.View.B.List.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager.Repository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.B.Init.isInternetAvailable
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.A.Model.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.Init.onLoadCategoriesFromCsv
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.Init.onLoadFromFireBase
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.SQL.Z_AppComptDao
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Z_AppComptRepositoryProtoJuin17(
    val dao: Z_AppComptDao,
) {
    val repoEntityName = Repository.Z_AppComptEntity.name
    val repoTAG = repoEntityName

    val repoRef =
        Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/Z_AppCompt"
        )

    private val composScope = CoroutineScope(Dispatchers.IO)

    suspend fun init(
        context: Context,
        updateRepoProgress: (String, Float) -> Unit
    ) {
        if (!dao.isTableEmpty()) return

        updateRepoProgress(Repository.Z_AppComptEntity.name, 0.4f)
        val data: List<Z_AppCompt> = if (isInternetAvailable(context)) {
            updateRepoProgress(Repository.Z_AppComptEntity.name, 0.6f)
            onLoadFromFireBase()
        } else {
            onLoadCategoriesFromCsv()
        }
        updateRepoProgress(Repository.Z_AppComptEntity.name, 0.8f)
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
