package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.SQL

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository

suspend fun B_ClientInfosProtoJuin3Repository.isRoomEmpty(): Boolean {
    return dao.getCount() == 0
}


