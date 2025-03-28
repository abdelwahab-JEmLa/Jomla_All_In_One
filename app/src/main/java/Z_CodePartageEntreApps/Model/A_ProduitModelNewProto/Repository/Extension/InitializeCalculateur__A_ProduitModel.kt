package Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository.Extension

import Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository.A_ProduitModelRepository
import Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository.I_CategoriesProduitsNewProtoRepository
import kotlinx.coroutines.flow.MutableStateFlow

class InitializeCalculateur__A_ProduitModel(
    val repositery: A_ProduitModelRepository,
    val progressRepo: MutableStateFlow<Float>,
    val i_CategoriesProduitsNewProtoRepository: I_CategoriesProduitsNewProtoRepository,
) {

}
