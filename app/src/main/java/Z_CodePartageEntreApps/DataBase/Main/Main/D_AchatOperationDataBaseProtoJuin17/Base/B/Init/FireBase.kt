package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun onLoadFromFireBaseD_AchatOperation(): MutableList<M10OperationVentCouleur> {
    return suspendCancellableCoroutine { continuation ->
        M10OperationVentCouleur.ref.get()
            .addOnSuccessListener { snapshot ->
                val dataList = mutableListOf<M10OperationVentCouleur>()
                snapshot.children.forEach { child ->
                    child.getValue(M10OperationVentCouleur::class.java)?.let { item ->
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
