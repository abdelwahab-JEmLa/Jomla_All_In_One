package Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.Init

import EntreApps.Shared.Models.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.DataBaseInit_Z_AppCompt
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun DataBaseInit_Z_AppCompt.onLoadFromFireBase(): MutableList<Z_AppCompt> {
    return suspendCancellableCoroutine { continuation ->
        repoRef.get()
            .addOnSuccessListener { snapshot ->
                val dataList = mutableListOf<Z_AppCompt>()
                snapshot.children.forEach { child ->
                    child.getValue(Z_AppCompt::class.java)?.let { item ->
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
