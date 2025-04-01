package Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository.Extension

import Z_CodePartageEntreApps.Model.A_ProduitModel
import Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository.A_ProduitModelRepository
import Z_CodePartageEntreApps.Model.A_ProduitModelNewProto.Repository.A_ProduitModelRepositoryImpl
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object FirebaseUtilsA_ProduitModelNewProto {
    private const val DEBOUNCE_INTERVAL = 500L


    fun startDatabaseListener(
        repository: A_ProduitModelRepositoryImpl,
        onValueEventListenerCreated: (ValueEventListener) -> Unit = {}
    ) {
        createValueEventListener(repository)?.let { listener ->
            A_ProduitModelRepository.caReference.addValueEventListener(listener)
            onValueEventListenerCreated(listener)
        }
    }

    private fun createValueEventListener(repository: A_ProduitModelRepositoryImpl): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (repository.isUpdating) {
                    return
                }

                val currentTime = System.currentTimeMillis()
                if (currentTime - repository.lastUpdateTimestamp < DEBOUNCE_INTERVAL) {
                    return
                }

                try {
                    repository.isUpdating = true
                    repository.lastUpdateTimestamp = currentTime
                    val totalItems = snapshot.childrenCount.toInt()
                    var processedItems = 0

                    repository.modelDatas.clear()
                    repository.progressRepo.value = 0f

                    if (totalItems == 0) {
                        repository.progressRepo.value = 1.0f
                        repository.initialDataLoaded = true
                        return
                    }

                    snapshot.children.forEach { dataSnapshot ->
                        try {
                            val data = A_ProduitModel.syncData(dataSnapshot = dataSnapshot) as A_ProduitModel
                            repository.modelDatas.add(data)
                            processedItems++
                            repository.progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                        } catch (e: Exception) {
                            // Silently handle exception
                        }
                    }

                    repository.initialDataLoaded = true
                    repository.progressRepo.value = 1.0f

                } catch (e: Exception) {
                    repository.progressRepo.value = 0f
                } finally {
                    repository.isUpdating = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                repository.progressRepo.value = 0f
            }
        }
    }

    fun stopDatabaseListener(listener: ValueEventListener?) {
        listener?.let {
            try {
                A_ProduitModelRepository.caReference.removeEventListener(it)
            } catch (e: Exception) {
                // Silently handle exception
            }
        }
    }
}
