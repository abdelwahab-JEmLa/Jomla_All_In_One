package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.FireBase

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Function.updateProgress

fun B_ClientInfosProtoJuin3Repository.getFirebaseData(onSuccess: (List<B_ClientInfosProtoJuin3>) -> Unit) {
    updateProgress(0.1f)
    repoRef.get()
        .addOnSuccessListener { snapshot ->
            val dataList = mutableListOf<B_ClientInfosProtoJuin3>()
            snapshot.children.forEach { child ->
                child.getValue(B_ClientInfosProtoJuin3::class.java)?.let { item ->
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
