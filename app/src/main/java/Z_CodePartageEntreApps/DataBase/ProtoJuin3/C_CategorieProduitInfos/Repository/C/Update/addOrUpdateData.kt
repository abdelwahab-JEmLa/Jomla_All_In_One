package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

fun C_CategorieProduitInfosRepository.addOrUpdateData(data: CategoriesTabelle) {
    CoroutineScope(Dispatchers.IO).launch {

        val preparedData = data.withDernierTimeTampsSynchronisationAvecFireBase()

        dao.upsert(preparedData)

        val allData = dao.getAll()
        updateRepoState(allData)

        batchFireBaseUpdate(listOf(preparedData))
    }
}

suspend fun batchFireBaseUpdate(datas: List<CategoriesTabelle>) {
    val updates = mutableMapOf<String, Any>()
    datas.forEach { data ->
        updates[data.id.toString()] = data
    }
    val firebaseRef = CategoriesTabelle.caRef
    firebaseRef.updateChildren(updates).await()
}
