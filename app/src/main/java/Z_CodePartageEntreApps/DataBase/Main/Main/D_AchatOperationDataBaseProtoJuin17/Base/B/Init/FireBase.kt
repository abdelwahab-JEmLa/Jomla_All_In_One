package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.FCouleurVentOperationInfos
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun onLoadFromFireBaseD_AchatOperation(): MutableList<FCouleurVentOperationInfos> {
    return suspendCancellableCoroutine { continuation ->
        FCouleurVentOperationInfos.ref.get()
            .addOnSuccessListener { snapshot ->
                val dataList = mutableListOf<FCouleurVentOperationInfos>()
                snapshot.children.forEach { child ->
                    child.getValue(FCouleurVentOperationInfos::class.java)?.let { item ->
                        dataList.add(item)
                    }
                }
                continuation.resume(dataList)
            }
            .addOnFailureListener {
                throw IllegalStateException("No data available from Firebase or CSV")
            }
    }
}
