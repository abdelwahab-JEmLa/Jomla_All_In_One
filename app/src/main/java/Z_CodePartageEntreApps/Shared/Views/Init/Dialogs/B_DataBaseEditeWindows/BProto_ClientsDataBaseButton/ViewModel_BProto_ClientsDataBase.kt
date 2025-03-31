package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows.BProto_ClientsDataBaseButton

import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.B_ClientDataBaseRepository
import androidx.lifecycle.ViewModel

class ViewModel_BProto_ClientsDataBase(
    val mainRepo: B_ClientDataBaseRepository
) : ViewModel() {
    val mainModel=mainRepo.modelDatas

    fun importDeFireBaseAuRoom() {
    }

}
