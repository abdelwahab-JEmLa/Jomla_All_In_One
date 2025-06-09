package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository

fun D_EtateMessageVocaleRepository.getFirebaseData(onSuccess: (List<D_EtateMessageVocale>) -> Unit) {
    updateProgress(0.1f)
    repoRef.get()
        .addOnSuccessListener { snapshot ->
            val dataList = mutableListOf<D_EtateMessageVocale>()
            snapshot.children.forEach { child ->
                child.getValue(D_EtateMessageVocale::class.java)?.let { item ->
                    item.keyFireBase = child.key ?: ""
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
