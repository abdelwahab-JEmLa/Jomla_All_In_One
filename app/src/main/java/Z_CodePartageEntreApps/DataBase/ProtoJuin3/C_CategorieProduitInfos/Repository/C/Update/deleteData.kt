package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.C.Update

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun C_CategorieProduitInfosRepository.deleteData(data: CategoriesTabelle) {
    CoroutineScope(Dispatchers.IO).launch {

        dao.deleteData(data)

       // C_CategorieProduitInfosRepository.re(getKeyFireBaseReplaceInvalidCarcters(data.keyID, data.nom) )

        val allData = dao.getAll()
        updateRepoState(allData)
    }
}
