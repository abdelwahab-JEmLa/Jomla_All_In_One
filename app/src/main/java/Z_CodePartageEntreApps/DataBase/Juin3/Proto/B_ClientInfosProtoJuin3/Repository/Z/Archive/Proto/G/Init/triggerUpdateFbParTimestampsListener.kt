package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.Init

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.Z.Archive.Proto.G.dataBaseCreationFactoryMID2ClientRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
fun dataBaseCreationFactoryMID2ClientRepository.triggerUpdateFbParTimestampsListener() {
    if (isListenerRegistered) return
    isListenerRegistered = true
    M00CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

    repoRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var addCount = 0
                    var deleteCount = 0
                    val currentLocalEntities = dao.getAll()
                    val currentLocalKeys = currentLocalEntities.map { it.keyID }.toSet()
                    val firebaseKeys = mutableSetOf<String>()

                    // Process Firebase data and detect additions
                    for (child in snapshot.children) {
                        try {
                            val firebaseKey = child.key ?: ""
                            firebaseKeys.add(firebaseKey)

                            child.getValue(M2Client::class.java)?.let { entity ->
                                val entityWithKey = entity.copy(keyID = firebaseKey)
                                val localEntity = currentLocalEntities.find { it.keyID == firebaseKey }

                                if (localEntity == null) {
                                    // New entity - this is an addition
                                    dao.upsert(entityWithKey)
                                    addCount++
                                } else {
                                    // Existing entity - only update if timestamp is newer (but don't count as add/delete)
                                    val shouldUpdate = entityWithKey.dernierTimeTampsSynchronisationAvecFireBase > localEntity.dernierTimeTampsSynchronisationAvecFireBase
                                    if (shouldUpdate) {
                                        dao.upsert(entityWithKey)
                                    } else {

                                    }
                                }
                            }
                        } catch (e: Exception) {}
                    }

                    // Detect deletions - entities that exist locally but not in Firebase
                    val deletedKeys = currentLocalKeys - firebaseKeys
                    if (deletedKeys.isNotEmpty()) {
                        for (deletedKey in deletedKeys) {
                            dao.deleteByKeyId(deletedKey) // You'll need to implement this method in your DAO
                            deleteCount++
                        }
                    }

                    // Only trigger repository state update if there were actual additions or deletions
                    if (addCount > 0 || deleteCount > 0) {
                        val allData = dao.getAll()
                        withContext(Dispatchers.Main) {
                            val newRepoState = dataBaseCreationFactoryMID2ClientRepository.RepoState(
                                modelListFlow = allData,
                                mainProgressRepo = 1.0f
                            )
                            _repoState.value = newRepoState
                        }
                    }
                } catch (e: Exception) {}
            }
        }

        override fun onCancelled(error: DatabaseError) {
            isListenerRegistered = false
        }
    })}
}
