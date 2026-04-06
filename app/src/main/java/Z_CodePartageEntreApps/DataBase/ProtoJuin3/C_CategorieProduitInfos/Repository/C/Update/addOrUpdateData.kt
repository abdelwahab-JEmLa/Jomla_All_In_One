package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update

import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun C_CategorieProduitInfosRepository.addOrUpdateData(
    data: M16CategorieProduit,
    avecFireBase: Boolean = false
) {
    CoroutineScope(Dispatchers.IO).launch {

        val preparedData = data.withDernierTimeTampsSynchronisationAvecFireBase()

        dao.upsert(preparedData)

        val allData = dao.getAll()
        updateRepoState(allData)

        avecFireBase.batchFireBaseUpdate(listOf(preparedData))
    }
}

