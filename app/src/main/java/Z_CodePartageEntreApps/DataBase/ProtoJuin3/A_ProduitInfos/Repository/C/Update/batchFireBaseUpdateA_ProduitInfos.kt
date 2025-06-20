package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.C.Update

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.ArticlesBasesStatsTable
import kotlinx.coroutines.tasks.await

suspend fun batchFireBaseUpdateA_ProduitInfos(datas: List<ArticlesBasesStatsTable>) {
    val updates = mutableMapOf<String, Any>()
    datas.forEach { data ->
        updates[data.bsonObjectId] = data
    }
    val firebaseRef = ArticlesBasesStatsTable.ref
    firebaseRef.updateChildren(updates).await()
}
