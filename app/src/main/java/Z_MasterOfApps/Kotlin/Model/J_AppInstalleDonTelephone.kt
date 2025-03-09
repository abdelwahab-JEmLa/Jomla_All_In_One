package Z_MasterOfApps.Kotlin.Model

import android.content.res.Resources
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class J_AppInstalleDonTelephone(
    var id: Long = 0,
) {
    var infosDeBase by mutableStateOf(InfosDeBase())
    @IgnoreExtraProperties
    class InfosDeBase{
        var nom by mutableStateOf("Non Defini")
        var widthScreen by mutableIntStateOf(0)
    }

    var etatesMutable by mutableStateOf(EtatesMutable())
    @IgnoreExtraProperties
    class EtatesMutable {
        var itsReciverTelephone by mutableStateOf(false)
        var indexDonsParentList by mutableLongStateOf(0)
        var nearbyWifiAdressIpConexion by mutableStateOf("")
    }
}

interface J_AppInstalleDonTelephoneRepository {
    var modelDatas: SnapshotStateList<J_AppInstalleDonTelephone>
    val progressRepo: MutableStateFlow<Float>  // Initialize progressRepo
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<J_AppInstalleDonTelephone>, Flow<Float>>
    suspend fun updateDatas(datas: SnapshotStateList<J_AppInstalleDonTelephone>)
    fun updatePhones()

    companion object {
        val metricsWidthPixels = Resources.getSystem().displayMetrics.widthPixels
        val caReference = _ModelAppsFather.ref_HeadOfModels.child("J_AppInstalleDonTelephone")
    }
}

class J_AppInstalleDonTelephoneRepositoryImpl : J_AppInstalleDonTelephoneRepository {
    private val TAG = "J_AppInstalleDonTelephoneRepo" // Tag for logging
    override var modelDatas: SnapshotStateList<J_AppInstalleDonTelephone> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f) // Added progressRepo

    private var listener: ValueEventListener? = null

    init {
        startDatabaseListener()
        // Verify and add the phone
        verifyAndAddPhone("${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}",
            J_AppInstalleDonTelephoneRepository.metricsWidthPixels
        )
    }

    private fun verifyAndAddPhone(phoneName: String, screenWidth: Int) {
        // Logging the start of the function and input parameters
        Log.d(TAG, "verifyAndAddPhone: Starting with phoneName=$phoneName, screenWidth=$screenWidth")

        // Obtenir tous les téléphones et vérifier localement
        J_AppInstalleDonTelephoneRepository.caReference.get().addOnSuccessListener { snapshot ->
            var phoneExists = false
            var maxId = 0L

            Log.d(TAG, "Firebase snapshot received with ${snapshot.childrenCount} items")

            // Parcourir tous les téléphones pour trouver celui avec le même nom
            snapshot.children.forEach { phoneSnapshot ->
                try {
                    val id = phoneSnapshot.child("id").getValue(Long::class.java) ?: 0
                    val nom = phoneSnapshot.child("infosDeBase/nom").getValue(String::class.java)
                        ?: phoneSnapshot.child("infosDeBase").child("nom").getValue(String::class.java)
                        ?: ""

                    Log.d(TAG, "Checking phone: id=$id, nom=$nom")

                    // Mettre à jour l'ID maximum
                    if (id > maxId) {
                        maxId = id
                        Log.d(TAG, "Updated maxId to $maxId")
                    }

                    // Vérifier si le téléphone existe déjà
                    if (nom == phoneName) {
                        phoneExists = true
                        Log.d(TAG, "Phone with name $phoneName already exists with id=$id")

                        // Créer l'objet téléphone à partir des données Firebase
                        val phone = J_AppInstalleDonTelephone().apply {
                            this.id = id
                            infosDeBase.nom = nom
                            infosDeBase.widthScreen = phoneSnapshot.child("infosDeBase/widthScreen").getValue(Int::class.java)
                                ?: phoneSnapshot.child("infosDeBase").child("widthScreen").getValue(Int::class.java)
                                        ?: screenWidth
                            etatesMutable.itsReciverTelephone = phoneSnapshot.child("etatesMutable/itsReciverTelephone").getValue(Boolean::class.java)
                                ?: phoneSnapshot.child("etatesMutable").child("itsReciverTelephone").getValue(Boolean::class.java)
                                        ?: false
                        }

                        // Vérifier si le téléphone est déjà dans la liste locale
                        if (modelDatas.none { it.id == id }) {
                            modelDatas.add(phone)
                            Log.d(TAG, "Added existing phone to local modelDatas")
                        } else {
                            Log.d(TAG, "Phone already exists in local modelDatas")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing phone snapshot: ${e.message}", e)
                    e.printStackTrace()
                }
            }

            // Si le téléphone n'existe pas, l'ajouter
            if (!phoneExists) {
                val newId = maxId + 1
                Log.d(TAG, "Phone with name $phoneName does not exist. Creating new phone with id=$newId")

                val newPhone = J_AppInstalleDonTelephone().apply {
                    id = newId
                    infosDeBase.nom = phoneName
                    infosDeBase.widthScreen = screenWidth
                    etatesMutable.itsReciverTelephone = false
                }

                // Ajouter à la liste locale
                if (modelDatas.none { it.id == newId }) {
                    modelDatas.add(newPhone)
                    Log.d(TAG, "Added new phone to local modelDatas")

                    // Ajouter à Firebase
                    J_AppInstalleDonTelephoneRepository.caReference
                        .child(newId.toString())
                        .setValue(newPhone)
                        .addOnSuccessListener {
                            Log.d(TAG, "Successfully saved new phone to Firebase with id=$newId")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Failed to save new phone to Firebase: ${e.message}", e)
                        }
                } else {
                    Log.w(TAG, "Phone with id=$newId already exists in local modelDatas, which is unexpected")
                }
            } else {
                Log.d(TAG, "Phone already exists, no need to add")
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Failed to get phones from Firebase: ${exception.message}", exception)
            exception.printStackTrace()
        }
    }

    private fun startDatabaseListener() {
        Log.d(TAG, "Starting database listener")
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val totalItems = snapshot.childrenCount.toInt()
                    var processedItems = 0

                    Log.d(TAG, "Database changed, processing $totalItems items")
                    modelDatas.clear()
                    progressRepo.value = 0f // Reset progress

                    for (dataSnapshot in snapshot.children) {
                        val category = dataSnapshot.getValue(J_AppInstalleDonTelephone::class.java)
                        category?.let { cat ->
                            modelDatas.add(cat)
                        }

                        processedItems++
                        progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                    }

                    // Sort categories by position (classmentDonsParentList)
                    modelDatas.sortBy { it.etatesMutable.indexDonsParentList }

                    progressRepo.value = 1.0f // Complete progress
                    Log.d(TAG, "Finished processing database changes, loaded ${modelDatas.size} items")
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading data: ${e.message}", e)
                    progressRepo.value = 0f // Reset progress on error
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
                progressRepo.value = 0f // Reset progress on cancellation
            }
        }

        // Attach the listener to the Firebase reference
        listener?.let {
            J_AppInstalleDonTelephoneRepository.caReference.addValueEventListener(it)
            Log.d(TAG, "Database listener attached")
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<J_AppInstalleDonTelephone>, Flow<Float>> {
        Log.d(TAG, "onDataBaseChangeListnerAndLoad started")
        val progressFlow = MutableStateFlow(0f)

        return suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                private var isResumed = false

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        // Prevent multiple resumptions
                        if (isResumed) return

                        val categories = mutableListOf<J_AppInstalleDonTelephone>()
                        val totalItems = snapshot.childrenCount.toInt()
                        var processedItems = 0

                        Log.d(TAG, "Loading data from database, total items: $totalItems")
                        modelDatas.clear()
                        progressFlow.value = 0f
                        progressRepo.value = 0f

                        for (dataSnapshot in snapshot.children) {
                            val category =
                                dataSnapshot.getValue(J_AppInstalleDonTelephone::class.java)
                            category?.let { cat ->
                                categories.add(cat)
                                modelDatas.add(cat)
                            }

                            processedItems++
                            progressFlow.value = processedItems.toFloat() / totalItems.toFloat()
                            progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                        }

                        // Sort categories by position
                        categories.sortBy { it.etatesMutable.indexDonsParentList }
                        modelDatas.sortBy { it.etatesMutable.indexDonsParentList }

                        progressFlow.value = 1.0f
                        progressRepo.value = 1.0f
                        Log.d(TAG, "Finished loading data, loaded ${categories.size} items")

                        // Ensure resumption happens only once
                        if (!isResumed) {
                            isResumed = true
                            continuation.resume(Pair(categories, progressFlow))
                            Log.d(TAG, "Resumed coroutine with loaded data")

                            // Remove the listener after successful data retrieval
                            J_AppInstalleDonTelephoneRepository.caReference.removeEventListener(this)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading data: ${e.message}", e)
                        if (!isResumed) {
                            isResumed = true
                            continuation.resumeWithException(e)
                            progressRepo.value = 0f
                            Log.e(TAG, "Resumed coroutine with exception")

                            // Remove the listener in case of error
                            J_AppInstalleDonTelephoneRepository.caReference.removeEventListener(this)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Database operation cancelled: ${error.message}")
                    if (!isResumed) {
                        isResumed = true
                        continuation.resumeWithException(Exception("Database error: ${error.message}"))
                        progressRepo.value = 0f
                        Log.e(TAG, "Resumed coroutine with database error")

                        // Remove the listener in case of cancellation
                        J_AppInstalleDonTelephoneRepository.caReference.removeEventListener(this)
                    }
                }
            }

            // Attach the listener
            J_AppInstalleDonTelephoneRepository.caReference.addValueEventListener(listener)
            Log.d(TAG, "Attached listener for data loading")

            // Ensure listener is removed if coroutine is cancelled
            continuation.invokeOnCancellation {
                J_AppInstalleDonTelephoneRepository.caReference.removeEventListener(listener)
                Log.d(TAG, "Coroutine cancelled, removed listener")
            }
        }
    }

    override suspend fun updateDatas(datas: SnapshotStateList<J_AppInstalleDonTelephone>) {
        Log.d(TAG, "updateDatas called with ${datas.size} items")
        // Update local modelDatas with the new data
        modelDatas.clear()
        modelDatas.addAll(datas)

        // Update Firebase with the new data
        datas.forEach { category ->
            J_AppInstalleDonTelephoneRepository.caReference.child(category.id.toString())
                .setValue(category)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully updated phone with id=${category.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to update phone with id=${category.id}: ${e.message}", e)
                }
        }
    }

    override fun updatePhones() {
        Log.d(TAG, "updatePhones called, updating ${modelDatas.size} phones")
        modelDatas.forEach { phone ->
            J_AppInstalleDonTelephoneRepository.caReference
                .child(phone.id.toString())
                .setValue(phone)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully updated phone with id=${phone.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to update phone with id=${phone.id}: ${e.message}", e)
                }
        }
    }
}
