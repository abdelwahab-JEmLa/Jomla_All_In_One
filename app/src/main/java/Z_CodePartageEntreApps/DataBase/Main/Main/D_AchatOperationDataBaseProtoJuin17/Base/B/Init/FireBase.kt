package Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.B.Init

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.FCouleurVentOperation
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
