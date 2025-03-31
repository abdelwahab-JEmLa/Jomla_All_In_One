package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows.BProto_ClientsDataBaseButton

import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.BProto_ClientsDataBaseRepository
import androidx.lifecycle.ViewModel

class ViewModel_BProto_ClientsDataBase(
    val mainRepo: BProto_ClientsDataBaseRepository
) : ViewModel() {
    val mainModel=mainRepo.modelDatas

    fun importDeFireBaseAuRoom() {
    }

}
