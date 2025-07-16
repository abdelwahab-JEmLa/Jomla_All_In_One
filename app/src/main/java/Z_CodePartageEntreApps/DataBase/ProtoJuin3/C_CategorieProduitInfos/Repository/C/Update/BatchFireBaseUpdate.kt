package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update

import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import kotlinx.coroutines.tasks.await

suspend fun Boolean.batchFireBaseUpdate(datas: List<CategoriesTabelle>) {
    val updates = mutableMapOf<String, Any>()
    if (this) {
        datas.forEach { data ->
            updates[data.id.toString()] = data
        }
        val firebaseRef = CategoriesTabelle.caRef
        firebaseRef.updateChildren(updates).await()
    }
}
