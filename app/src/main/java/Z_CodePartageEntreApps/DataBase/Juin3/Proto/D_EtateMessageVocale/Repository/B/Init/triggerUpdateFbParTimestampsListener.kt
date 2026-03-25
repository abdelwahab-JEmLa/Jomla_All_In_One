package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.B.Init

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun Repo17MessageVocale.triggerUpdateFbParTimestampsListener() {
    if (isListenerRegistered) return
    isListenerRegistered = true
    M00CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

    repoRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val localData = dao.getAll()
                    val localKeyIds = localData.map { it.keyID }.toSet()
                    val firebaseKeyIds = mutableSetOf<String>()

                    var updateCount = 0
                    var addCount = 0

                    for (child in snapshot.children) {
                        try {
                            child.getValue(M17MessageVocale::class.java)?.let { entity ->
                                val entityWithKey = entity.copy(keyID = child.key ?: "")
                                firebaseKeyIds.add(entityWithKey.keyID)

                                val localEntity = localData.find { it.keyID == entityWithKey.keyID }

                                val shouldUpdate = if (localEntity == null) {
                                    addCount++
                                    true
                                } else {
                                    entityWithKey.dernierTimeTampsSynchronisationAvecFireBase > localEntity.dernierTimeTampsSynchronisationAvecFireBase
                                }

                                if (shouldUpdate) {
                                    dao.upsert(entityWithKey)
                                    updateCount++
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }

                    val itemsToDelete = localKeyIds - firebaseKeyIds
                    var deleteCount = 0

                    for (keyToDelete in itemsToDelete) {
                        try {
                            dao.deleteByKeyId(keyToDelete)
                            deleteCount++
                        } catch (e: Exception) {
                        }
                    }

                    if (updateCount > 0 || deleteCount > 0 || addCount > 0) {
                        val allData = dao.getAll()
                        withContext(Dispatchers.Main) {
                            val newRepoState = Repo17MessageVocale.RepoState(
                                modelListFlow = allData,
                                mainProgressRepo = 1.0f
                            )
                            _repoState.value = newRepoState
                        }

                        println("$repoTAG: Changes detected - Added: $addCount, Updated: ${updateCount - addCount}, Deleted: $deleteCount")
                    }
                } catch (e: Exception) {
                    println("$repoTAG: Error in Firebase listener: ${e.message}")
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            isListenerRegistered = false
            println("$repoTAG: Firebase listener cancelled: ${error.message}")
        }
    })}
}
