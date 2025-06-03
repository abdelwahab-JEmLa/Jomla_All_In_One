package Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Extensions

import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.A.Main.C_CategorieProduitInfosRepository
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models.C_CategorieProduitInfos

fun C_CategorieProduitInfosRepository.getFirebaseData(onSuccess: (List<C_CategorieProduitInfos>) -> Unit) {
    updateProgress(0.1f)
    ref.get()
        .addOnSuccessListener { snapshot ->
            val dataList = mutableListOf<C_CategorieProduitInfos>()
            snapshot.children.forEach { child ->
                child.getValue(C_CategorieProduitInfos::class.java)?.let { item ->
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


