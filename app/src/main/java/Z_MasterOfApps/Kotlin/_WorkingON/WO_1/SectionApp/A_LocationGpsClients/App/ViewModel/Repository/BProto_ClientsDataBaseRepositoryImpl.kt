package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository

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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BProto_ClientsDataBaseRepositoryImpl(
    private val appDatabase: AppDatabase
) : BProto_ClientsDataBaseRepository {
    private val TAG = "BProto_ClientsDataBase"

    override var modelDatas: SnapshotStateList<BProto_ClientsDataBase> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Keep track of our value event listener
    private var valueEventListener: ValueEventListener? = null

    init {
        // Initialize Firebase capabilities, but avoid calling keepSynced directly here
        // to prevent the "listen() called twice for same QuerySpec" error
        repositoryScope.launch {
            initializeRepository()
        }
    }

    private suspend fun initializeRepository() {
        // Initialize Firebase offline capabilities first
        FirebaseUtilsBProto_ClientsDataBaseNewProto.initializeFirebaseOfflineCapability()

        // Then check data consistency
        checkDataConsistency()

        // Finally set up the listener
        setUpDataChangeListener()
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
            Log.e(TAG, "Error checking data consistency", e)
            // If there's any error, default to loading from Room
            loadDepuitRoom()
        }
    }

    // Renamed and refactored to clarify purpose
    private fun setUpDataChangeListener() {
        // Remove any existing listener first to prevent duplicates
        removeDataChangeListener()

        // Create and store a new listener
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                        clientData?.let { newData ->
                            val existingIndex = modelDatas.indexOfFirst { it.id == newData.id }
                            if (existingIndex != -1) {
                                // Only update if data has actually changed
                                val existingData = modelDatas[existingIndex]
                                if (hasRelevantChanges(existingData, newData)) {
                                    Log.d(TAG, "Firebase update for client ${newData.id}: Data changed, updating")
                                    repositoryScope.launch {
                                        updateData(newData)
                                    }
                                } else {
                                    Log.d(TAG, "Firebase update for client ${newData.id}: No relevant changes, skipping update")
                                }
                            } else {
                                // New client data not in our list
                                Log.d(TAG, "New client data from Firebase: ${newData.id}")
                                modelDatas.add(newData)
                                repositoryScope.launch {
                                    appDatabase.bProtoClientsDataBaseDao().insert(newData)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing Firebase data update", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase data listener cancelled", error.toException())
            }
        }

        // Add the listener
        BProto_ClientsDataBaseRepository.caReference.addValueEventListener(valueEventListener!!)
        Log.d(TAG, "Firebase data change listener set up")
    }

    private fun removeDataChangeListener() {
        valueEventListener?.let {
            BProto_ClientsDataBaseRepository.caReference.removeEventListener(it)
            Log.d(TAG, "Removed existing Firebase data change listener")
        }
        valueEventListener = null
    }

    // Helper method to determine if relevant data has changed
    private fun hasRelevantChanges(oldData: BProto_ClientsDataBase, newData: BProto_ClientsDataBase): Boolean {
        // Check only fields that would require a map reload
        return oldData.latitude != newData.latitude ||
                oldData.longitude != newData.longitude ||
                oldData.actuelleEtat != newData.actuelleEtat ||
                oldData.nom != newData.nom ||
                oldData.clientTypeMode != newData.clientTypeMode
    }

    override fun deleteUnSeulData(data: BProto_ClientsDataBase) {
        try {
            val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
            if (recordIndex != -1) {
                modelDatas.removeAt(recordIndex)
            }

            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).removeValue()

            repositoryScope.launch {
                appDatabase.bProtoClientsDataBaseDao().delete(data)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting data", e)
        }
    }

    private fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
        try {
            progressRepo.value = 0f

            // Clear modelDatas on the main thread to avoid concurrent modification
            viewModelScope.launch(Dispatchers.Main) {
                modelDatas.clear()
            }

            viewModelScope.launch(Dispatchers.IO) {
                val task = BProto_ClientsDataBaseRepository.caReference.get()
                val snapshot = Tasks.await(task)

                // First delete all existing records
                appDatabase.bProtoClientsDataBaseDao().deleteAll()

                val clientsList = mutableListOf<BProto_ClientsDataBase>()

                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                        clientData?.let {
                            clientsList.add(it)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing data snapshot", e)
                    }
                }

                // Batch insert all clients
                if (clientsList.isNotEmpty()) {
                    appDatabase.bProtoClientsDataBaseDao().insertAll(clientsList)

                    // Update the UI list on the main thread
                    withContext(Dispatchers.Main) {
                        modelDatas.addAll(clientsList)
                    }
                }

                initialDataLoaded = true
                progressRepo.value = 1.0f
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error importing data from Firebase to Room", e)
            progressRepo.value = 0f
        }
    }

    override fun addData(data: BProto_ClientsDataBase) {
        try {
            // Update model list on main thread
            repositoryScope.launch(Dispatchers.Main) {
                modelDatas.add(data)
            }

            // Update Firebase and Room in background
            repositoryScope.launch(Dispatchers.IO) {
                // Firebase update
                try {
                    BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data).await()
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating Firebase for client ${data.id}", e)
                }

                // Room update
                try {
                    appDatabase.bProtoClientsDataBaseDao().insert(data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating Room for client ${data.id}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding data", e)
        }
    }



    override fun updateData(data: BProto_ClientsDataBase?) {
        if (data == null) {
            return
        }

        repositoryScope.launch(Dispatchers.Main) {
            val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
            if (recordIndex != -1) {
                modelDatas[recordIndex] = data
            }
        }

        repositoryScope.launch(Dispatchers.IO) {
            try {
                // Update Firebase
                firebaseUpdateData(data)

                // Update Room
                appDatabase.bProtoClientsDataBaseDao().insert(data)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating data for client ${data.id}", e)
            }
        }
    }

    private fun firebaseUpdateData(data: BProto_ClientsDataBase) {
        try {
            BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating Firebase data for client ${data.id}", e)
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<BProto_ClientsDataBase>) {
        if (isUpdating) {
            Log.d(TAG, "Already updating, skipping this update")
            return
        }

        isUpdating = true

        try {

            val datasList = datas.toList()

            // Update Room database first
            withContext(Dispatchers.IO) {
                appDatabase.bProtoClientsDataBaseDao().deleteAll()
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
            Log.e(TAG, "Error updating data batch", e)
        } finally {
            isUpdating = false
        }
    }

    // This method is called when the ViewModel is cleared to clean up resources
    fun cleanup() {
        removeDataChangeListener()
    }
}
