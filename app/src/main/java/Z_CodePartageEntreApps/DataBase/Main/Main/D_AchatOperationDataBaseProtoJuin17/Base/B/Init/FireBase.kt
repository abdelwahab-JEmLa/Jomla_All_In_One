package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Base.B.Init

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.D_CouleurVentOperation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun onLoadFromFireBaseD_AchatOperation(): MutableList<D_CouleurVentOperation> {
    return suspendCancellableCoroutine { continuation ->
        D_CouleurVentOperation.caRef.get()
            .addOnSuccessListener { snapshot ->
                val dataList = mutableListOf<D_CouleurVentOperation>()
                snapshot.children.forEach { child ->
                    child.getValue(D_CouleurVentOperation::class.java)?.let { item ->
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
