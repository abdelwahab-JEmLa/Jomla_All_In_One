package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel

import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository.B_ClientsDataBaseRepository
import androidx.lifecycle.ViewModel

class ViewModel_App2FragID1(
    val mainRepositery: B_ClientsDataBaseRepository,
) : ViewModel() {
    val b_ClientsDataBase= mainRepositery.modelDatas

    fun updateClient(client: B_ClientsDataBase): Unit {
        mainRepositery.updateData(client)
    }

}
