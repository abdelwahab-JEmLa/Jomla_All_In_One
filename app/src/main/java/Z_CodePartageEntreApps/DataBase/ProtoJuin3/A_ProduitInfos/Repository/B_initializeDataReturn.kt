package A.AtelierMobile.Test.ID1.Test.Shared.DataBase.A_ProduitInfos.Repository

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions.getFirebaseData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions.isRoomEmpty
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.A_ProduitInfosProtoJuin3
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun A_ProduitInfosRepository.initializeDataReturn(): List<A_ProduitInfosProtoJuin3> {
    return if (isRoomEmpty()) {
        val firebaseData = suspendCancellableCoroutine { continuation ->
            getFirebaseData { dataFB ->
                continuation.resume(dataFB)
            }
        }

        if (firebaseData.isEmpty()) {
            emptyList()
        } else {
            dao.insertAll(firebaseData)
            firebaseData
        }
    } else {
        dao.getAll()
    }
}
