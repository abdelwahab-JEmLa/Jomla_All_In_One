package Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository.Extension

import Z_CodePartageEntreApps.Model.B_ClientsDataBaseRepo.Repository.B_ClientsDataBaseRepository
import Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository.I_CategoriesProduitsNewProtoRepository
import kotlinx.coroutines.flow.MutableStateFlow

class InitializeCalculateur__B_ClientsDataBase(
    val repositery: B_ClientsDataBaseRepository,
    val progressRepo: MutableStateFlow<Float>,
    val i_CategoriesProduitsNewProtoRepository: I_CategoriesProduitsNewProtoRepository,
) {

}
