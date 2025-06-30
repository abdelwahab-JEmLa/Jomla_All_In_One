package Z_CodePartageEntreApps.DataBase.Main.Main.G.BonVent.Base.Init

import V.DiviseParSections.App.Shared.Repository.GBonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.G.BonVent.Base.DataBaseCreationFactoryGBonVent
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun DataBaseCreationFactoryGBonVent.onLoadFromFireBase(): MutableList<GBonVent> {
    return suspendCancellableCoroutine { continuation ->
        repoRef.get()
            .addOnSuccessListener { snapshot ->
                val dataList = mutableListOf<GBonVent>()
                snapshot.children.forEach { child ->
                    child.getValue(GBonVent::class.java)?.let { item ->
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
