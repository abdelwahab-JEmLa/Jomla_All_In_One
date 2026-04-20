package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.DataBaseFactoryDCouleurAchatOperation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun DataBaseFactoryDCouleurAchatOperation.onLoadFromFireBase(): MutableList<M10OperationVentCouleur> {
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
