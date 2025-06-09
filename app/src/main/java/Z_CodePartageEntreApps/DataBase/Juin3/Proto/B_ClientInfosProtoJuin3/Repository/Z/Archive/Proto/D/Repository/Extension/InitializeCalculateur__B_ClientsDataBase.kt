package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.D.Repository.Extension

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.D.Repository.B_ClientsDataBaseRepository
import kotlinx.coroutines.flow.MutableStateFlow

class InitializeCalculateur__B_ClientsDataBase(
    val repositery: B_ClientsDataBaseRepository,
    val progressRepo: MutableStateFlow<Float>,
) {

}
