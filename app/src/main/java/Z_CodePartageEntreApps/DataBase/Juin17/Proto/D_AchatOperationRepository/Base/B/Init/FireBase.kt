package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_AchatOperationRepository.Base.B.Init

import Views.P1.Ui.ArticlesGrid.A.List.Repository.A1.Proto.Juin17.Proto.D_AchatOperation.Repository.D_AchatOperation
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
