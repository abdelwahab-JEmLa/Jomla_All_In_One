package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.B.Init

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update.addOrUpdateDatas
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions.getFirebaseData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions.isRoomEmpty
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun D_EtateMessageVocaleRepository.initializeDataReturn(): List<D_EtateMessageVocale> {
    return if (isRoomEmpty()) {
        val firebaseData = suspendCancellableCoroutine { continuation ->
            getFirebaseData { dataFB ->
                continuation.resume(dataFB)
            }
        }

        if (firebaseData.isEmpty()) {
            val testData = D_EtateMessageVocale.createTestInstance()
            addOrUpdateDatas(testData)
            testData
        } else {
            dao.insertAll(firebaseData)
            firebaseData
        }
    } else {
        dao.getAll()
    }
}
