package Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository.Extension

import Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository.B_ClientsDataBaseRepository
import kotlinx.coroutines.flow.MutableStateFlow

class InitializeCalculateur__B_ClientsDataBase(
    val repositery: B_ClientsDataBaseRepository,
    val progressRepo: MutableStateFlow<Float>,
) {

}
