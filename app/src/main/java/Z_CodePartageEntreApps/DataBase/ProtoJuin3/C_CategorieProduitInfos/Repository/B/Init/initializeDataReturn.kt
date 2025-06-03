package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.B.Init

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Extensions.getFirebaseData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Extensions.isRoomEmpty
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun C_CategorieProduitInfosRepository.initializeDataReturn(): List<C_CategorieProduitInfos> {
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
