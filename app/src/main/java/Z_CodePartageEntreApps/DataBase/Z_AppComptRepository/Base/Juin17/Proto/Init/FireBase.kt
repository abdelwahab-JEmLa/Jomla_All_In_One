package Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.Init

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel.Repository.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Z_AppComptRepository.Base.Juin17.Proto.Z_AppComptRepositoryProtoJuin17
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun Z_AppComptRepositoryProtoJuin17.onLoadFromFireBase(): MutableList<Z_AppCompt> {
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
