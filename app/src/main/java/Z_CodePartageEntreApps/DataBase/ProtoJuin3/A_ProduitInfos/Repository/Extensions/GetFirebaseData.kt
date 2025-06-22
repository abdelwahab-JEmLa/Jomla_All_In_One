package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Proto.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Proto.ViewModel.Repository.DisponibilityEtates
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A_ProduitInfosRepository
import android.annotation.SuppressLint
import com.google.firebase.database.core.utilities.encoding.CustomClassMapper

fun A_ProduitInfosRepository.getFirebaseData(onSuccess: (List<ArticlesBasesStatsTable>) -> Unit) {
    updateProgress(0.1f)
    ref.get()
        .addOnSuccessListener { snapshot ->
            val dataList = mutableListOf<ArticlesBasesStatsTable>()
            snapshot.children.forEach { child ->
                try {
                    // First, get the raw data as a Map
                    val rawData = child.value as? Map<String, Any>

                    if (rawData != null) {
                        // Create ArticlesBasesStatsTable with safe type conversion
                        val item = convertToArticlesBasesStatsTable(rawData)
                        item.keyFireBase = child.key ?: ""
                        dataList.add(item)
                    } else {
                        // Fallback to direct conversion if rawData is null
                        child.getValue(ArticlesBasesStatsTable::class.java)?.let { item ->
                            item.keyFireBase = child.key ?: ""
                            dataList.add(item)
                        }
                    }
                } catch (e: Exception) {
                    // Log the error and continue with other items
                    android.util.Log.w("FirebaseConversion", "Failed to convert item: ${child.key}", e)
                }
            }
            updateProgress(1.0f)
            onSuccess(dataList)
        }
        .addOnFailureListener { exception ->
            updateProgress(0f)
            android.util.Log.e("FirebaseError", "Failed to fetch data", exception)
            onSuccess(emptyList())
        }
}

@SuppressLint("RestrictedApi")
private fun convertToArticlesBasesStatsTable(data: Map<String, Any>): ArticlesBasesStatsTable {
    // Create a base object using Firebase's automatic conversion
    val baseItem = ArticlesBasesStatsTable()

    // Apply Firebase's automatic deserialization for all fields
    val tempItem = try {
        CustomClassMapper.convertToCustomClass(data, ArticlesBasesStatsTable::class.java)
    } catch (e: Exception) {
        baseItem
    }

    // Only override the problematic disponibilityEtates field
    val safeDisponibilityEtates = when (val dispValue = data["disponibilityEtates"]) {
        is String -> DisponibilityEtates.fromString(dispValue)
        is Boolean -> if (dispValue) DisponibilityEtates.DISPO else DisponibilityEtates.NON_DISPO
        else -> DisponibilityEtates.DISPO
    }

    return tempItem.copy(disponibilityEtates = safeDisponibilityEtates)
}
