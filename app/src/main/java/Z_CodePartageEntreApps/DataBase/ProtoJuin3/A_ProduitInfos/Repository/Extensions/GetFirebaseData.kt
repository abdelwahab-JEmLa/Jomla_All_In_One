package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.ArticlesBasesStatsTable

fun A_ProduitInfosRepository.getFirebaseData(onSuccess: (List<ArticlesBasesStatsTable>) -> Unit) {
    updateProgress(0.1f)
    ref.get()
        .addOnSuccessListener { snapshot ->
            val dataList = mutableListOf<ArticlesBasesStatsTable>()
            snapshot.children.forEach { child ->
                child.getValue(ArticlesBasesStatsTable::class.java)?.let { item ->
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
