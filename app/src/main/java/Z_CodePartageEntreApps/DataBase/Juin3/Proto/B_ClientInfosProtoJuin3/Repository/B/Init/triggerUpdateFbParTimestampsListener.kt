package Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.B.Init

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.A.Main.B_ClientInfosProtoJuin3Repository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun B_ClientInfosProtoJuin3Repository.triggerUpdateFbParTimestampsListener() {
    if (isListenerRegistered) return
    isListenerRegistered = true

    repoRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    var updateCount = 0
                    for (child in snapshot.children) {
                        try {
                            child.getValue(B_ClientInfosProtoJuin3::class.java)?.let { entity ->
                                val entityWithKey = entity.copy(keyFireBase = child.key ?: "")
                                val shouldUpdate = try {
                                    val localEntity = dao.getAll().find { it.keyFireBase == entityWithKey.keyFireBase }
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
                            val newRepoState = B_ClientInfosProtoJuin3Repository.RepoState(
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
