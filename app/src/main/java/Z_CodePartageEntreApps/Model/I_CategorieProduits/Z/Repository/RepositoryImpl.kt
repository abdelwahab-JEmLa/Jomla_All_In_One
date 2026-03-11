package Z_CodePartageEntreApps.Model.I_CategorieProduits.Z.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import EntreApps.Shared.Modules.Base.AppDatabase
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
import java.util.concurrent.atomic.AtomicBoolean

class I_CategorieProduitsRepositoryImpl(
    private val appDatabase: AppDatabase
) : I_CategorieProduitsRepository {

    private val TAG = "I_CategorieProduits"

    override var modelDatas: SnapshotStateList<I_CategorieProduits> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private val isUpdating = AtomicBoolean(false)
    private val isListenerActive = AtomicBoolean(false)
    private val isFlowListenerActive = AtomicBoolean(false)
    private var flowValueEventListener: ValueEventListener? = null

    internal var lastUpdateTimestamp = 0L
    var initialDataLoaded = false
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var valueEventListener: ValueEventListener? = null
    private val listenerLock = Any()
    private val flowListenerLock = Any()

    init {
        repositoryScope.launch {
            initializeRepository()
        }
    }

    override fun updateUnSeulData(data: I_CategorieProduits) {
        repositoryScope.launch(Dispatchers.Main) {
            val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
            if (recordIndex != -1) {
                modelDatas[recordIndex] = data
            }
        }

        repositoryScope.launch(Dispatchers.IO) {
            try {
                appDatabase.I_CategorieProduitsDao().insert(data)
                firebaseUpdateData(data)
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }

    private suspend fun initializeRepository() {
        try {
            loadDepuitRoom()
            checkDataConsistency()
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    private suspend fun loadDepuitRoom() {
        try {
            progressRepo.value = 0.2f
            withContext(Dispatchers.IO) {
                val dataList = try {
                    appDatabase.I_CategorieProduitsDao().getAll()
                } catch (e: Exception) {
                    emptyList()
                }

                withContext(Dispatchers.Main) {
                    modelDatas.clear()
                    if (dataList.isNotEmpty()) {
                        modelDatas.addAll(dataList)
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
                try {
                    appDatabase.I_CategorieProduitsDao().getCount()
                } catch (e: Exception) {
                    0
                }
            }

            val firebaseSnapshot = try {
                withContext(Dispatchers.IO) {
                    val task = I_CategorieProduitsRepository.sonDataBaseRef.get()
                    Tasks.await(task)
                }
            } catch (e: Exception) {
                null
            }

            val firebaseCount = firebaseSnapshot?.childrenCount?.toInt() ?: 0

            if (roomCount != firebaseCount || roomCount == 0) {
                if (firebaseCount > 0) {
                    importDeFireBaseAuRoom(repositoryScope)
                }
            }

            withContext(Dispatchers.Main) {
                setUpFlowDataListener()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                setUpFlowDataListener()
            }
        }
    }

    private fun setUpFlowDataListener() {
        synchronized(flowListenerLock) {
            removeFlowDataListener()

            if (!isFlowListenerActive.get()) {
                flowValueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.childrenCount > 0) {
                            repositoryScope.launch {
                                try {
                                    for (dataSnapshot in snapshot.children) {
                                        val productId = dataSnapshot.getValue(Long::class.java)
                                        productId?.let { id ->
                                            val productSnapshot = withContext(Dispatchers.IO) {
                                                try {
                                                    val task = I_CategorieProduitsRepository.sonDataBaseRef.child(id.toString()).get()
                                                    Tasks.await(task)
                                                } catch (e: Exception) {
                                                    null
                                                }
                                            }

                                            if (productSnapshot != null) {
                                                val updatedProduct = productSnapshot.getValue(
                                                    I_CategorieProduits::class.java)
                                                if (updatedProduct != null) {
                                                    updateUnSeulData(updatedProduct)
                                                }
                                            }

                                            try {
                                                withContext(Dispatchers.IO) {
                                                    I_CategorieProduitsRepository.iDsDatasFlowUpdateRef.child(dataSnapshot.key!!).removeValue()
                                                }
                                            } catch (e: Exception) {
                                                // Handle error silently
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Handle error silently
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isFlowListenerActive.set(false)
                    }
                }

                isFlowListenerActive.set(true)
                try {
                    M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                    I_CategorieProduitsRepository.iDsDatasFlowUpdateRef.addValueEventListener(flowValueEventListener!!)}
                } catch (e: Exception) {
                    isFlowListenerActive.set(false)
                    flowValueEventListener = null
                }
            }
        }
    }

    private fun removeFlowDataListener() {
        synchronized(flowListenerLock) {
            if (isFlowListenerActive.get() && flowValueEventListener != null) {
                try {
                    I_CategorieProduitsRepository.iDsDatasFlowUpdateRef.removeEventListener(flowValueEventListener!!)
                } catch (e: Exception) {
                    // Handle error silently
                } finally {
                    flowValueEventListener = null
                    isFlowListenerActive.set(false)
                }
            }
        }
    }

    private fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
        try {
            progressRepo.value = 0f
            viewModelScope.launch(Dispatchers.Main) {
                modelDatas.clear()
            }

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val task = I_CategorieProduitsRepository.sonDataBaseRef.get()
                    val snapshot = Tasks.await(task)

                    try {
                        appDatabase.I_CategorieProduitsDao().deleteAll()
                    } catch (e: Exception) {
                        // Handle error silently
                    }

                    val dataList = mutableListOf<I_CategorieProduits>()

                    for (dataSnapshot in snapshot.children) {
                        try {
                            val data = dataSnapshot.getValue(I_CategorieProduits::class.java)
                            data?.let {
                                dataList.add(it)
                            }
                        } catch (e: Exception) {
                            // Handle error silently
                        }
                    }

                    if (dataList.isNotEmpty()) {
                        try {
                            appDatabase.I_CategorieProduitsDao().insertAll(dataList)

                            withContext(Dispatchers.Main) {
                                modelDatas.addAll(dataList)
                            }
                        } catch (e: Exception) {
                            // Handle error silently
                        }
                    }

                    initialDataLoaded = true
                    progressRepo.value = 1.0f
                } catch (e: Exception) {
                    progressRepo.value = 0f
                }
            }
        } catch (e: Exception) {
            progressRepo.value = 0f
        }
    }

    private fun removeDataChangeListener() {
        synchronized(listenerLock) {
            if (isListenerActive.get() && valueEventListener != null) {
                try {
                    I_CategorieProduitsRepository.sonDataBaseRef.removeEventListener(valueEventListener!!)
                } catch (e: Exception) {
                    // Handle error silently
                } finally {
                    valueEventListener = null
                    isListenerActive.set(false)
                }
            }
        }
    }

    override fun deleteUnSeulData(data: I_CategorieProduits) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                val recordIndex = modelDatas.indexOfFirst { it.id == data.id }
                if (recordIndex != -1) {
                    modelDatas.removeAt(recordIndex)
                }
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    I_CategorieProduitsRepository.sonDataBaseRef.child(data.id.toString()).removeValue().await()
                    appDatabase.I_CategorieProduitsDao().delete(data)
                } catch (e: Exception) {
                    // Handle error silently
                }
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    override fun addData(data: I_CategorieProduits) {
        try {
            repositoryScope.launch(Dispatchers.Main) {
                modelDatas.add(data)
            }

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    I_CategorieProduitsRepository.sonDataBaseRef.child(data.id.toString()).setValue(data).await()
                    appDatabase.I_CategorieProduitsDao().insert(data)
                } catch (e: Exception) {
                    // Handle error silently
                }
            }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    private suspend fun firebaseUpdateData(data: I_CategorieProduits) {
        try {
            I_CategorieProduitsRepository.sonDataBaseRef.child(data.id.toString()).setValue(data).await()
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    override suspend fun updateMultiDatas(datas: SnapshotStateList<I_CategorieProduits>) {
        if (isUpdating.getAndSet(true)) {
            return
        }

        try {
            val datasList = datas.toList()

            withContext(Dispatchers.IO) {
                try {
                    appDatabase.I_CategorieProduitsDao().deleteAll()
                    appDatabase.I_CategorieProduitsDao().insertAll(datasList)
                } catch (e: Exception) {
                    // Handle error silently
                }
            }

            withContext(Dispatchers.IO) {
                val tempListener = valueEventListener
                val tempFlowListener = flowValueEventListener

                try {
                    synchronized(listenerLock) {
                        valueEventListener?.let {
                            I_CategorieProduitsRepository.sonDataBaseRef.removeEventListener(it)
                        }
                        valueEventListener = null
                        isListenerActive.set(false)
                    }

                    synchronized(flowListenerLock) {
                        flowValueEventListener?.let {
                            I_CategorieProduitsRepository.iDsDatasFlowUpdateRef.removeEventListener(it)
                        }
                        flowValueEventListener = null
                        isFlowListenerActive.set(false)
                    }

                    batchFireBaseSet(datasList)

                } catch (e: Exception) {
                    // Handle error silently
                } finally {
                    synchronized(listenerLock) {
                        if (!isListenerActive.get() && tempListener != null) {
                            valueEventListener = tempListener
                            M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                            I_CategorieProduitsRepository.sonDataBaseRef.addValueEventListener(tempListener)}
                            isListenerActive.set(true)
                        }
                    }

                    synchronized(flowListenerLock) {
                        if (!isFlowListenerActive.get() && tempFlowListener != null) {
                            flowValueEventListener = tempFlowListener
                            M18CentralParametresOfAllApps().listens_on_data_change_resources_consolation.ifTrue {

                            I_CategorieProduitsRepository.iDsDatasFlowUpdateRef.addValueEventListener(tempFlowListener)}
                            isFlowListenerActive.set(true)
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                modelDatas.clear()
                modelDatas.addAll(datas)
            }
        } catch (e: Exception) {
            // Handle error silently
        } finally {
            isUpdating.set(false)
        }
    }

    private fun batchFireBaseSet(datas: List<I_CategorieProduits>): Unit {
        try {
            val reference = I_CategorieProduitsRepository.sonDataBaseRef
            val batchUpdates = HashMap<String, Any>()

            for (data in datas) {
                batchUpdates[data.id.toString()] = data
            }

            reference.updateChildren(batchUpdates)
                .addOnSuccessListener {
                    // Success handling if needed
                }
                .addOnFailureListener { exception ->
                    // Error handling if needed
                }
        } catch (e: Exception) {
            // Handle error silently
        }
    }

    fun cleanup() {
        repositoryScope.launch {
            removeDataChangeListener()
            removeFlowDataListener()
        }
    }

    fun onDestroy() {
        cleanup()
    }
}
