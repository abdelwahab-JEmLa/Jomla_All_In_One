package Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository

import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.BProto_ClientsDataBase
import Z_MasterOfApps.Kotlin._WorkingON.WO_1.SectionApp.A_LocationGpsClients.App.ViewModel.Repository.Extension.FirebaseUtilsBProto_ClientsDataBaseNewProto
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
    private var valueEventListener: ValueEventListener? = null

    init {
        repositoryScope.launch {
            initializeRepository()
        }
    }

    private suspend fun initializeRepository() {
        FirebaseUtilsBProto_ClientsDataBaseNewProto.initializeFirebaseOfflineCapability()
        checkDataConsistency()
        setUpDataChangeListener()
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val clientsList = appDatabase.bProtoClientsDataBaseDao().getAll()
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
                appDatabase.bProtoClientsDataBaseDao().getCount()
            }
            val firebaseSnapshot = withContext(Dispatchers.IO) {
                val task = BProto_ClientsDataBaseRepository.caReference.get()
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
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
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
                                    appDatabase.bProtoClientsDataBaseDao().insert(newData)
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
        BProto_ClientsDataBaseRepository.caReference.addValueEventListener(valueEventListener!!)
    }

    private fun removeDataChangeListener() {
        valueEventListener?.let {
            BProto_ClientsDataBaseRepository.caReference.removeEventListener(it)
        }
        valueEventListener = null
    }

    private fun hasRelevantChanges(oldData: BProto_ClientsDataBase, newData: BProto_ClientsDataBase): Boolean {
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
                val task = BProto_ClientsDataBaseRepository.caReference.get()
                val snapshot = Tasks.await(task)
                appDatabase.bProtoClientsDataBaseDao().deleteAll()
                val clientsList = mutableListOf<BProto_ClientsDataBase>()

                for (dataSnapshot in snapshot.children) {
                    try {
                        val clientData = dataSnapshot.getValue(BProto_ClientsDataBase::class.java)
                        clientData?.let {
                            clientsList.add(it)
                        }
                    } catch (e: Exception) {
                        // Error handling
                    }
                }

                if (clientsList.isNotEmpty()) {
                    appDatabase.bProtoClientsDataBaseDao().insertAll(clientsList)
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

    override fun addData(data: BProto_ClientsDataBase) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                modelDatas.add(data)
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    BProto_ClientsDataBaseRepository.caReference.child(data.id.toString()).setValue(data).await()
                } catch (e: Exception) {
                    // Error handling
                }

                try {
                    appDatabase.bProtoClientsDataBaseDao().insert(data)
                } catch (e: Exception) {
                    // Error handling
                }
            }
        } catch (e: Exception) {
            // Error handling
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
                firebaseUpdateData(data)
                appDatabase.bProtoClientsDataBaseDao().insert(data)
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
            val datasList = datas.toList()

            withContext(Dispatchers.IO) {
                appDatabase.bProtoClientsDataBaseDao().deleteAll()
                appDatabase.bProtoClientsDataBaseDao().insertAll(datasList)
            }

            withContext(Dispatchers.IO) {
                datas.forEach { data ->
                    try {
                        BProto_ClientsDataBaseRepository.caReference.child(data.id.toString())
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
