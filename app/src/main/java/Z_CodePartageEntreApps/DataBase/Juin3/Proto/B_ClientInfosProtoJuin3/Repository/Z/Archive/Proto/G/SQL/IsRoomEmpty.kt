package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.SQL

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository

suspend fun dataBaseCreationFactoryMID2ClientRepository.isRoomEmpty(): Boolean {
    return dao.getCount() == 0
}


