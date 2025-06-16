package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Extensions

import Z_CodePartageEntreApps.Repository.Repository.CategoriesTabelle
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import android.util.Log

// Get all Firebase data
fun C_CategorieProduitInfosRepository.getFirebaseData(onSuccess: (List<CategoriesTabelle>) -> Unit) {
    updateProgress(0.1f)
    repoRef.get()
        .addOnSuccessListener { snapshot ->
            val dataList = mutableListOf<CategoriesTabelle>()
            snapshot.children.forEach { child ->
                child.getValue(CategoriesTabelle::class.java)?.let { item ->
                    dataList.add(item)
                }
            }
            updateProgress(1.0f)
            onSuccess(dataList)
        }
        .addOnFailureListener { exception ->
            updateProgress(0f)
            Log.e("FirebaseError", "Failed to fetch data", exception)
            onSuccess(emptyList())
        }
}

suspend fun C_CategorieProduitInfosRepository.isRoomEmpty(): Boolean {
    return dao.getCount() == 0
}

private fun C_CategorieProduitInfosRepository.updateProgress(progress: Float) {
    val currentState = _repoState.value
    if (currentState != null) {
        val newState = currentState.copy(mainProgressRepo = progress)
        _repoState.value = newState
    }
}
