package Z_CodePartageEntreApps.DataBase.Main.Main.A.Base

import EntreApps.Shared.Models.M01Produit
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions.H.Dao.ArticlesBasesStatsModelDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class A_ProduitDataBaseProtoJuin17(
    val dao: ArticlesBasesStatsModelDao,
) {
    val repoTAG = "A_ProduitDataBase"
    val repoRef = M01Produit.ref
    private val composScope = CoroutineScope(Dispatchers.IO)

    fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        dataAvecTigerUpdate: M01Produit
    ) {
        composScope.launch {
            if (existingIndex >= 0) {
                dao.update(dataAvecTigerUpdate)
                batchFireBaseUpdateArticlesBasesStatsTable(listOf(dataAvecTigerUpdate))
            } else {
                dao.insert(dataAvecTigerUpdate)
                batchFireBaseUpdateArticlesBasesStatsTable(listOf(dataAvecTigerUpdate))
            }
        }
    }

    fun deleteDataAncienRepo(data: M01Produit) {
        composScope.launch {
            dao.delete(data)
            deleteFromFireBase(data)
        }
    }

    // Méthode mise à jour pour utiliser toFirebaseMap()
    suspend fun batchFireBaseUpdateArticlesBasesStatsTable(datas: List<M01Produit>) {
        val updates = mutableMapOf<String, Any>()
        datas.forEach { data ->
            // Utilise toFirebaseMap() pour éviter les caractères invalides
            updates[data.keyID] = data.toFirebaseMap()
        }
        val firebaseRef = M01Produit.ref
        firebaseRef.updateChildren(updates).await()
    }

    private suspend fun deleteFromFireBase(data: M01Produit) {
        val keyToDelete = data.keyFireBase.ifEmpty {
            data.bsonObjectId
        }
        repoRef.child(keyToDelete).removeValue().await()
    }
}
