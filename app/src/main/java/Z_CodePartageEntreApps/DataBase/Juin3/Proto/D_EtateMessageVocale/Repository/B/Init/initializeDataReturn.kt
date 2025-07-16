package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.B.Init

import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.C.Update.addOrUpdateDatas
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions.getFirebaseData
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions.isRoomEmpty
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun Repo17MessageVocale.initializeDataReturn(): List<M17MessageVocale> {
    return if (isRoomEmpty()) {
        val firebaseData = suspendCancellableCoroutine { continuation ->
            getFirebaseData { dataFB ->
                continuation.resume(dataFB)
            }
        }

        if (firebaseData.isEmpty()) {
            val itsTestDataFlow = false
            if (itsTestDataFlow) {
                val testData = M17MessageVocale.createTestInstance()
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
