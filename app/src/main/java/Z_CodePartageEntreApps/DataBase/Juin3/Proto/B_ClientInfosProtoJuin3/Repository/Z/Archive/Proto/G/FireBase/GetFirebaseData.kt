package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.FireBase

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Function.updateProgress
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository

fun dataBaseCreationFactoryMID2ClientRepository.getFirebaseData(onSuccess: (List<M2Client>) -> Unit) {
    updateProgress(0.1f)
    repoRef.get()
        .addOnSuccessListener { snapshot ->
            val dataList = mutableListOf<M2Client>()
            snapshot.children.forEach { child ->
                child.getValue(M2Client::class.java)?.let { item ->
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
