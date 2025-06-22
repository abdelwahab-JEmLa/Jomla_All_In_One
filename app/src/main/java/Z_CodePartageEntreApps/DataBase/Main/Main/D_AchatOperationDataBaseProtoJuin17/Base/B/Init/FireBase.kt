package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Base.B.Init

import V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package.Juin22.Proto.Repository.D_AchatOperation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun onLoadFromFireBaseD_AchatOperation(): MutableList<D_AchatOperation> {
    return suspendCancellableCoroutine { continuation ->
        D_AchatOperation.caRef.get()
            .addOnSuccessListener { snapshot ->
                val dataList = mutableListOf<D_AchatOperation>()
                snapshot.children.forEach { child ->
                    child.getValue(D_AchatOperation::class.java)?.let { item ->
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
