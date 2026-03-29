package Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Init

import EntreApps.Shared.Models.M09AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.DataBaseInit_Z_AppCompt
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun DataBaseInit_Z_AppCompt.onLoadFromFireBase(): MutableList<M09AppCompt> {
    return suspendCancellableCoroutine { continuation ->
        repoRef.get()
            .addOnSuccessListener { snapshot ->
                val dataList = mutableListOf<M09AppCompt>()
                snapshot.children.forEach { child ->
                    child.getValue(M09AppCompt::class.java)?.let { item ->
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
