package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.SQL

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.dataBaseCreationFactoryMID2ClientRepository

suspend fun dataBaseCreationFactoryMID2ClientRepository.isRoomEmpty(): Boolean {
    return dao.getCount() == 0
}


