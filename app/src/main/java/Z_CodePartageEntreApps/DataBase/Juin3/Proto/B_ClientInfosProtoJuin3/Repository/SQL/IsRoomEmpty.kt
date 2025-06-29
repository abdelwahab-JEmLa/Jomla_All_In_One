package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.SQL

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.DataBaseFactoryFClient

suspend fun DataBaseFactoryFClient.isRoomEmpty(): Boolean {
    return dao.getCount() == 0
}


