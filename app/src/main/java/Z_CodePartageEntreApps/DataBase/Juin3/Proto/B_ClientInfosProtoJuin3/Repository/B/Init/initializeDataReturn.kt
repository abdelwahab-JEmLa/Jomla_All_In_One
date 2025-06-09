package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.B.Init

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.C.Update.addOrUpdateDatas
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.FireBase.getFirebaseData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.SQL.isRoomEmpty
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun B_ClientInfosProtoJuin3Repository.initializeDataReturn(): List<B_ClientInfosProtoJuin3> {
    return if (isRoomEmpty()) {
        val firebaseData = suspendCancellableCoroutine { continuation ->
            getFirebaseData { dataFB ->
                continuation.resume(dataFB)
            }
        }

        if (firebaseData.isEmpty()) {
            val itsTestDataFlow = false
            if (itsTestDataFlow) {
                val testData = B_ClientInfosProtoJuin3.createTestInstance()
                addOrUpdateDatas(testData)
                testData
            } else emptyList()
        } else {
            dao.insertAll(firebaseData)
            firebaseData
        }
    } else {
        dao.getAll()
    }
}
