package Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository.Extension

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import Z_CodePartageEntreApps.Model.I_CategoriesProduits
import Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository.I_CategoriesProduitsNewProtoRepository
import Z_CodePartageEntreApps.Model.I_CategoriesProduitsRepositery.Repository.I_CategoriesProduitsNewProtoRepositoryImpl
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


object FirebaseUtilsI_CategoriesProduitsNewProto {
    private const val DEBOUNCE_INTERVAL = 500L


    fun sanitizeFirebaseKey(key: String): String {
        return key.replace(Regex("[/.#$\\[\\]]"), "_")
    }

    fun startDatabaseListener(
        repository: I_CategoriesProduitsNewProtoRepositoryImpl,
        onValueEventListenerCreated: (ValueEventListener) -> Unit
    ) {
        createValueEventListener(repository)?.let {
            M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

            I_CategoriesProduitsNewProtoRepository.caReference.addValueEventListener(it)}
            onValueEventListenerCreated(it)
        }
    }


    private fun createValueEventListener(repository: I_CategoriesProduitsNewProtoRepositoryImpl): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (repository.isUpdating) return

                val currentTime = System.currentTimeMillis()
                if (currentTime - repository.lastUpdateTimestamp < DEBOUNCE_INTERVAL) return

                try {
                    repository.isUpdating = true
                    val totalItems = snapshot.childrenCount.toInt()
                    var processedItems = 0

                    repository.modelDatas.clear()
                    repository.progressRepo.value = 0f // Reset progress at start

                    if (totalItems == 0) {
                        repository.progressRepo.value = 1.0f
                        repository.initialDataLoaded = true
                        return
                    }

                    snapshot.children.forEach { dataSnapshot ->
                        try {
                            val data =
                                I_CategoriesProduits.syncData(dataSnapshot = dataSnapshot) as I_CategoriesProduits
                            if (data.id > 0) {
                                repository.modelDatas.add(data)
                                processedItems++
                                // Update progress after each item is processed
                                repository.progressRepo.value =
                                    processedItems.toFloat() / totalItems.toFloat()
                            }
                        } catch (e: Exception) {
                        }
                    }

                    repository.initialDataLoaded = true
                    repository.progressRepo.value = 1.0f // Ensure progress reaches 100% when done

                } catch (e: Exception) {
                    repository.progressRepo.value = 0f // Reset on error
                } finally {
                    repository.isUpdating = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                repository.progressRepo.value = 0f // Reset on cancellation
            }
        }
    }


}

