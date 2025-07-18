package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.Init

import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.DataBaseInitFactory_8BonVent
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun DataBaseInitFactory_8BonVent.onLoadFromFireBase(): MutableList<M8BonVent> {
    return suspendCancellableCoroutine { continuation ->
        repoRef.get()
            .addOnSuccessListener { snapshot ->
                val dataList = mutableListOf<M8BonVent>()
                snapshot.children.forEach { child ->
                    child.getValue(M8BonVent::class.java)?.let { item ->
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
