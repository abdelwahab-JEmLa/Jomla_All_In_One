package Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.Extension.FirebaseUtils_B_ClientDataBase
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.AppDatabase
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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

class B_ClientDataBaseRepositoryImpl(
    private val appDatabase: AppDatabase
) : B_ClientDataBaseRepository {
    private val TAG = "B_ClientDataBase"

    override var modelDatas: SnapshotStateList<B_ClientDataBase> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    internal var isUpdating = false
    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var valueEventListener: ValueEventListener? = null

    init {
        repositoryScope.launch {
            initializeRepository()
        }
    }

    private suspend fun initializeRepository() {
        FirebaseUtils_B_ClientDataBase.initializeFirebaseOfflineCapability()
        checkDataConsistency()
        setUpDataChangeListener()
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val clientsList = appDatabase.b_ClientDataBaseDao().getAll()
                withContext(Dispatchers.Main) {
                    modelDatas.clear()
                    if (clientsList.isNotEmpty()) {
                        modelDatas.addAll(clientsList)
                    }
                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
        }
    }

    private suspend fun checkDataConsistency() {
        try {
            val roomCount = withContext(Dispatchers.IO) {
                appDatabase.b_ClientDataBaseDao().getCount()
            }
            val firebaseSnapshot = withContext(Dispatchers.IO) {
                val task = B_ClientDataBaseRepository.caReference.get()
                Tasks.await(task)
            }
            val firebaseCount = firebaseSnapshot.childrenCount.toInt()

            if (roomCount != firebaseCount || roomCount == 0) {
                importDeFireBaseAuRoom(repositoryScope)
            } else {
                loadDepuitRoom()
            }
        } catch (e: Exception) {
            loadDepuitRoom()
        }
    }

    private fun setUpDataChangeListener() {
        removeDataChangeListener()
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(B_ClientDataBase::class.java)
                        clientData?.let { newData ->
                            val existingIndex = modelDatas.indexOfFirst { it.id == newData.id }
                            if (existingIndex != -1) {
                                val existingData = modelDatas[existingIndex]
                                if (hasRelevantChanges(existingData, newData)) {
                                    repositoryScope.launch {
                                        updateData(newData)
                                    }
                                } else {
                                }
                            } else {
                                // New client data not in our list
                                modelDatas.add(newData)
                                repositoryScope.launch {
                                    appDatabase.b_ClientDataBaseDao().insert(newData)
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
        }
        B_ClientDataBaseRepository.caReference.addValueEventListener(valueEventListener!!)
    }

    private fun removeDataChangeListener() {
        valueEventListener?.let {
            B_ClientDataBaseRepository.caReference.removeEventListener(it)
        }
        valueEventListener = null
    }

    private fun hasRelevantChanges(oldData: B_ClientDataBase, newData: B_ClientDataBase): Boolean {
        return oldData.latitude != newData.latitude ||
                oldData.longitude != newData.longitude ||
                oldData.actuelleEtat != newData.actuelleEtat ||
                oldData.nom != newData.nom ||
                oldData.clientTypeMode != newData.clientTypeMode
    }

    override fun deleteUnSeulData(data: B_ClientDataBase) {
        try {
            val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
            if (recordIndex != -1) {
                modelDatas.removeAt(recordIndex)
            }

            B_ClientDataBaseRepository.caReference.child(data.id.toString()).removeValue()

            repositoryScope.launch {
                appDatabase.b_ClientDataBaseDao().delete(data)
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    private fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
        try {
            progressRepo.value = 0f
            viewModelScope.launch(Dispatchers.Main) {
                modelDatas.clear()
            }

            viewModelScope.launch(Dispatchers.IO) {
                val task = B_ClientDataBaseRepository.caReference.get()
                val snapshot = Tasks.await(task)
                appDatabase.b_ClientDataBaseDao().deleteAll()
                val clientsList = mutableListOf<B_ClientDataBase>()

                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(B_ClientDataBase::class.java)
                        clientData?.let {
                            clientsList.add(it)
                        }
                    } catch (e: Exception) {
                        // Error handling
                    }
                }

                if (clientsList.isNotEmpty()) {
                    appDatabase.b_ClientDataBaseDao().insertAll(clientsList)
                    withContext(Dispatchers.Main) {
                        modelDatas.addAll(clientsList)
                    }
                }

                initialDataLoaded = true
                progressRepo.value = 1.0f
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
        }
    }

    override fun addData(data: B_ClientDataBase) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                modelDatas.add(data)
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    B_ClientDataBaseRepository.caReference.child(data.id.toString()).setValue(data).await()
                } catch (e: Exception) {
                    // Error handling
                }

                try {
                    appDatabase.b_ClientDataBaseDao().insert(data)
                } catch (e: Exception) {
                    // Error handling
                }
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    override fun updateData(data: B_ClientDataBase?) {
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
                firebaseUpdateData(data)
                appDatabase.b_ClientDataBaseDao().insert(data)
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    private fun firebaseUpdateData(data: B_ClientDataBase) {
        try {
            B_ClientDataBaseRepository.caReference.child(data.id.toString()).setValue(data)
        } catch (e: Exception) {
            // Error handling
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<B_ClientDataBase>) {
        if (isUpdating) {
            return
        }

        isUpdating = true

        try {
            val datasList = datas.toList()

            withContext(Dispatchers.IO) {
                appDatabase.b_ClientDataBaseDao().deleteAll()
                appDatabase.b_ClientDataBaseDao().insertAll(datasList)
            }

            withContext(Dispatchers.IO) {
                datas.forEach { data ->
                    try {
                        B_ClientDataBaseRepository.caReference.child(data.id.toString())
                            .setValue(data)
                    } catch (e: Exception) {
                        // Error handling
                    }
                }
            }

            withContext(Dispatchers.Main) {
                modelDatas.clear()
                modelDatas.addAll(datas)
            }
        } catch (e: Exception) {
            // Error handling
        } finally {
            isUpdating = false
        }
    }

    fun cleanup() {
        removeDataChangeListener()
    }
}
