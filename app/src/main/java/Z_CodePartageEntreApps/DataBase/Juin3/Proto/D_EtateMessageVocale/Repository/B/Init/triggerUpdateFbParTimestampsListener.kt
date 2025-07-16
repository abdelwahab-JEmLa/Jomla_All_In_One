package Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.B.Init

import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocaleRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun D_EtateMessageVocaleRepository.triggerUpdateFbParTimestampsListener() {
    if (isListenerRegistered) return
    isListenerRegistered = true

    repoRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var updateCount = 0
                    for (child in snapshot.children) {
                        try {
                            child.getValue(M17MessageVocale::class.java)?.let { entity ->
                                val entityWithKey = entity.copy(keyID = child.key ?: "")
                                val shouldUpdate = try {
                                    val localEntity = dao.getAll().find { it.keyID == entityWithKey.keyID }
                                    if (localEntity == null) {
                                        true
                                    } else {
                                        entityWithKey.dernierTimeTampsSynchronisationAvecFireBase > localEntity.dernierTimeTampsSynchronisationAvecFireBase
                                    }
                                } catch (e: Exception) {
                                    true
                                }

                                if (shouldUpdate) {
                                    dao.upsert(entityWithKey)
                                    updateCount++
                                }
                            }
                        } catch (e: Exception) {}
                    }

                    if (updateCount > 0) {
                        val allData = dao.getAll()
                        withContext(Dispatchers.Main) {
                            val newRepoState = D_EtateMessageVocaleRepository.RepoState(
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
    })
}
