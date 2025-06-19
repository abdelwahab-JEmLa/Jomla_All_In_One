package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.B.Init

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Extensions.getFirebaseData
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Extensions.isRoomEmpty
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun C_CategorieProduitInfosRepository.initializeDataReturn(): List<CategoriesTabelle> {
    return if (isRoomEmpty()) {
        val hasInternetConnection = isInternetAvailable(context)
        if (hasInternetConnection) {
            val firebaseData = suspendCancellableCoroutine { continuation ->
                getFirebaseData { dataFB ->
                    continuation.resume(dataFB)
                }
            }

            if (firebaseData.isNotEmpty()) {
                dao.insertAll(firebaseData)
                firebaseData
            } else {
                val csvData = loadCategoriesFromCsv()
                if (csvData.isNotEmpty()) {
                    dao.insertAll(csvData)
                }
                csvData
            }
        } else {
            val csvData = loadCategoriesFromCsv()
            if (csvData.isNotEmpty()) {
                dao.insertAll(csvData)
            }
            csvData
        }
    } else {
        dao.getAll()
    }
}
