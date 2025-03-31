package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository

import Z_CodePartageEntreApps.Modules.ConnectivityMonitorNewProto
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.Extension.FirebaseUtilsBProto_ClientsDataBaseNewProto
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.clientjetpack.Modules.AppDatabase
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BProto_ClientsDataBaseRepositoryImpl(
    private val appDatabase: AppDatabase
) : BProto_ClientsDataBaseRepository {
    val connectivityMonitor = ConnectivityMonitorNewProto(CoroutineScope(Dispatchers.Default))
    private val TAG = "BProto_ClientsDataBase"

    override var modelDatas: SnapshotStateList<BProto_ClientsDataBase> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        FirebaseUtilsBProto_ClientsDataBaseNewProto.initializeFirebaseOfflineCapability()
        repositoryScope.launch {
            checkDataConsistency()
        }
        onDataChangeListnerlatitudeEtlongitude()
    }

    // Make this method safer
    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f

            withContext(Dispatchers.IO) {
                val clientsList = appDatabase.bProtoClientsDataBaseDao().getAll()

                withContext(Dispatchers.Main) {
                    // Safely update the list
                    modelDatas.clear()
                    if (clientsList.isNotEmpty()) {
                        modelDatas.addAll(clientsList)
                    }

                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load data from Room", e)
            progressRepo.value = 0f
        }
    }

    private suspend fun checkDataConsistency() {
        try {
            // Get count from Room database
            val roomCount = withContext(Dispatchers.IO) {
                appDatabase.bProtoClientsDataBaseDao().getCount()
            }

            // Get count from Firebase
            val firebaseSnapshot = withContext(Dispatchers.IO) {
                val task = BProto_ClientsDataBaseRepository.caReference.get()
                Tasks.await(task)
            }

            val firebaseCount = firebaseSnapshot.childrenCount.toInt()

            if (roomCount != firebaseCount || roomCount == 0) {
                // Room and Firebase are out of sync, import from Firebase
                importDeFireBaseAuRoom(repositoryScope)
            } else {
                // Data is in sync, load from Room for performance
                loadDepuitRoom()
            }
        } catch (e: Exception) {
            // If there's any error, default to loading from Room
            loadDepuitRoom()
        }
    }

    private fun onDataChangeListnerlatitudeEtlongitude() {
        BProto_ClientsDataBaseRepository.caReference.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                        clientData?.let { newData ->
                            val existingIndex = modelDatas.indexOfFirst { it.id == newData.id }
                            if (existingIndex != -1) {
                                repositoryScope.launch {
                                    updateData(newData)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Error handling
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Error handling
            }
        })
    }


    override fun deleteUnSeulData(data: BProto_ClientsDataBase) {
        try {
            val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
            if (recordIndex != -1) {
                modelDatas.removeAt(recordIndex)
            }

            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).removeValue()

            CoroutineScope(Dispatchers.IO).launch {
                appDatabase.bProtoClientsDataBaseDao().delete(data)
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    private fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
        try {
            progressRepo.value = 0f
            modelDatas.clear()

            viewModelScope.launch(Dispatchers.IO) {
                val task = BProto_ClientsDataBaseRepository.caReference.get()
                val snapshot = Tasks.await(task)

                appDatabase.bProtoClientsDataBaseDao().deleteAll()

                val clientsList = mutableListOf<BProto_ClientsDataBase>()

                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                        clientData?.let {
                            clientsList.add(it)
                            modelDatas.add(it)
                        }
                    } catch (e: Exception) {
                        // Error handling
                    }
                }

                if (clientsList.isNotEmpty()) {
                    appDatabase.bProtoClientsDataBaseDao().insertAll(clientsList)
                }

                initialDataLoaded = true
                progressRepo.value = 1.0f
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
        }
    }


    override fun addData(data: BProto_ClientsDataBase) {
        modelDatas.add(data)

        try {
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)

            CoroutineScope(Dispatchers.IO).launch {
                appDatabase.bProtoClientsDataBaseDao().insert(data)
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    override fun checkConnectivity() {
        connectivityMonitor.checkConnectivityAndSync(
            BProto_ClientsDataBaseRepository.caReference,
        )
    }

    override fun updateData(data: BProto_ClientsDataBase?) {
        if (data == null) {
            return
        }

        val recordIndex = modelDatas.indexOfFirst { it.id == data.id }

        if (recordIndex != -1) {
            modelDatas[recordIndex] = data

            try {
                firebaseUpdateData(data)

                CoroutineScope(Dispatchers.IO).launch {
                    appDatabase.bProtoClientsDataBaseDao().insert(data)
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    private fun firebaseUpdateData(data: BProto_ClientsDataBase) {
        try {
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
        } catch (e: Exception) {
            // Error handling
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<BProto_ClientsDataBase>) {
        if (isUpdating) {
            return
        }

        isUpdating = true

        try {
            checkConnectivity()

            withContext(Dispatchers.IO) {
                appDatabase.bProtoClientsDataBaseDao().deleteAll()

                val datasList = datas.toList()
                appDatabase.bProtoClientsDataBaseDao().insertAll(datasList)
            }

            // Update Firebase after database update
            withContext(Dispatchers.IO) {
                datas.forEach { data ->
                    try {
                        BProto_ClientsDataBaseRepository.caReference.child(data.id.toString())
                            .setValue(data)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating Firebase for client ${data.id}", e)
                    }
                }
            }

            // Update modelDatas on the main thread
            withContext(Dispatchers.Main) {
                modelDatas.clear()
                modelDatas.addAll(datas)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating data", e)
        } finally {
            isUpdating = false
        }
    }
}
