package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Base.B.Init

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.FCouleurVentOperation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun onLoadFromFireBaseD_AchatOperation(): MutableList<FCouleurVentOperation> {
    return suspendCancellableCoroutine { continuation ->
        FCouleurVentOperation.ref.get()
            .addOnSuccessListener { snapshot ->
                val dataList = mutableListOf<FCouleurVentOperation>()
                snapshot.children.forEach { child ->
                    child.getValue(FCouleurVentOperation::class.java)?.let { item ->
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
