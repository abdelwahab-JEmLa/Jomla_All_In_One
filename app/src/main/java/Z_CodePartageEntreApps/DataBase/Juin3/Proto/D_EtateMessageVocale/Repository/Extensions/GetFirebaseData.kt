package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions

import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale

fun Repo17MessageVocale.getFirebaseData(onSuccess: (List<M17MessageVocale>) -> Unit) {
    updateProgress(0.1f)
    repoRef.get()
        .addOnSuccessListener { snapshot ->
            val dataList = mutableListOf<M17MessageVocale>()
            snapshot.children.forEach { child ->
                child.getValue(M17MessageVocale::class.java)?.let { item ->
                    item.keyID = child.key ?: ""
                    dataList.add(item)
                }
            }
            updateProgress(1.0f)
            onSuccess(dataList)
        }
        .addOnFailureListener { exception ->
            updateProgress(0f)
            // Log the error for debugging
            android.util.Log.e("FirebaseError", "Failed to fetch data", exception)
            onSuccess(emptyList())
        }
}
