package Z_CodePartageEntreApps.Model

import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.firebaseDatabase
import Z_CodePartageEntreApps.Model.Z.Archive._ModelAppsFather.Companion.ref_HeadOfModels
import android.util.Log
import androidx.compose.runtime.getValue
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

class I_CategoriesProduits(
    var id: Long = 0,
    var infosDeBase: InfosDeBase = InfosDeBase(),
    var statuesMutable: StatuesMutable = StatuesMutable(),
) {
    @IgnoreExtraProperties
    class InfosDeBase(
        var nom: String = "Non Defini",
    )  {
        var groupeParentId by mutableLongStateOf(0L)
    }

    @IgnoreExtraProperties
    class StatuesMutable(
        var indexDonsParentList: Long = 0,

    ) {
        var afficheSonHeader by mutableStateOf(false)
    }
    companion object {

        fun syncData(
            data: I_CategoriesProduits? = null,
            dataSnapshot: DataSnapshot? = null
        ): Any {
            // Convert from Firebase to model
            if (dataSnapshot != null) {
                return dataSnapshot.getValue(I_CategoriesProduits::class.java) ?: I_CategoriesProduits()
            }

            // Convert from model to Firebase map
            val sourceData = data ?: I_CategoriesProduits()
            return mapOf(
                "keyID" to sourceData.id,
                "infosDeBase" to mapOf(
                    "nom" to sourceData.infosDeBase.nom,
                    "groupeParentId" to sourceData.infosDeBase.groupeParentId
                ),
                "statuesMutable" to mapOf(
                    "indexDonsParentList" to sourceData.statuesMutable.indexDonsParentList,
                    "afficheSonHeader" to sourceData.statuesMutable.afficheSonHeader
                )
            )
        }
    }

}

interface I_CategoriesRepository {
    var modelDatas: SnapshotStateList<I_CategoriesProduits>
    val progressRepo: MutableStateFlow<Float>  // Initialize progressRepo
        get() = MutableStateFlow(0f)

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<I_CategoriesProduits>, Flow<Float>>
    suspend fun getCategoriesById(id: String): I_CategoriesProduits?
    suspend fun updateDatas(datas: SnapshotStateList<I_CategoriesProduits>)
    fun updateUneSeulData(data: I_CategoriesProduits)


    companion object {
        val ancienBaseDonneRef = firebaseDatabase.getReference("H_CategorieTabele")
        val caReference = ref_HeadOfModels.child("I_CategoriesProduits")

        fun syncData(
            data: I_CategoriesProduits? = null,
            dataSnapshot: DataSnapshot? = null
        ): Any {
            // Convert from Firebase to model
            if (dataSnapshot != null) {
                return dataSnapshot.getValue(I_CategoriesProduits::class.java) ?: I_CategoriesProduits()
            }

            // Convert from model to Firebase map
            val sourceData = data ?: I_CategoriesProduits()
            return mapOf(
                "keyID" to sourceData.id,
                "infosDeBase" to mapOf(
                    "nom" to sourceData.infosDeBase.nom,
                    "groupeParentId" to sourceData.infosDeBase.groupeParentId
                ),
                "statuesMutable" to mapOf(
                    "indexDonsParentList" to sourceData.statuesMutable.indexDonsParentList,
                    "afficheSonHeader" to sourceData.statuesMutable.afficheSonHeader
                )
            )
        }
    }
}

class CategoriesRepositoryImpl : I_CategoriesRepository {
    override var modelDatas: SnapshotStateList<I_CategoriesProduits> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f) // Added progressRepo

    private var listener: ValueEventListener? = null

    init {
        // Initialize the listener when the repository is created
     //   startDatabaseListener()
        progressRepo.value = 1.0f

    }

    private fun startDatabaseListener() {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val totalItems = snapshot.childrenCount.toInt()
                    var processedItems = 0

                    modelDatas.clear()
                    progressRepo.value = 0f // Reset progress

                    for (dataSnapshot in snapshot.children) {
                        val category = dataSnapshot.getValue(I_CategoriesProduits::class.java)
                        category?.let { cat ->
                            modelDatas.add(cat)
                        }

                        processedItems++
                        progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                    }

                    // Sort categories by position (classmentDonsParentList)
                    modelDatas.sortBy { it.statuesMutable.indexDonsParentList }

                    progressRepo.value = 1.0f // Complete progress
                } catch (e: Exception) {
                    Log.e("CategoriesRepositoryImpl", "Error loading data: ${e.message}")
                    progressRepo.value = 0f // Reset progress on error
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CategoriesRepositoryImpl", "Database error: ${error.message}")
                progressRepo.value = 0f // Reset progress on cancellation
            }
        }

        // Attach the listener to the Firebase reference
        listener?.let {
            I_CategoriesRepository.caReference.addValueEventListener(it)
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<I_CategoriesProduits>, Flow<Float>> {
        val progressFlow = MutableStateFlow(0f)

        return suspendCancellableCoroutine { continuation ->
            val listener = object : ValueEventListener {
                private var isResumed = false

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        // Prevent multiple resumptions
                        if (isResumed) return

                        val categories = mutableListOf<I_CategoriesProduits>()
                        val totalItems = snapshot.childrenCount.toInt()
                        var processedItems = 0

                        modelDatas.clear()
                        progressFlow.value = 0f
                        progressRepo.value = 0f

                        for (dataSnapshot in snapshot.children) {
                            val category = dataSnapshot.getValue(I_CategoriesProduits::class.java)
                            category?.let { cat ->
                                categories.add(cat)
                                modelDatas.add(cat)
                            }

                            processedItems++
                            progressFlow.value = processedItems.toFloat() / totalItems.toFloat()
                            progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                        }

                        // Sort categories by position
                        categories.sortBy { it.statuesMutable.indexDonsParentList }
                        modelDatas.sortBy { it.statuesMutable.indexDonsParentList }

                        progressFlow.value = 1.0f
                        progressRepo.value = 1.0f

                        // Ensure resumption happens only once
                        if (!isResumed) {
                            isResumed = true
                            continuation.resume(Pair(categories, progressFlow))

                            // Remove the listener after successful data retrieval
                            I_CategoriesRepository.caReference.removeEventListener(this)
                        }
                    } catch (e: Exception) {
                        if (!isResumed) {
                            isResumed = true
                            continuation.resumeWithException(e)
                            progressRepo.value = 0f

                            // Remove the listener in case of error
                            I_CategoriesRepository.caReference.removeEventListener(this)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (!isResumed) {
                        isResumed = true
                        continuation.resumeWithException(Exception("Database error: ${error.message}"))
                        progressRepo.value = 0f

                        // Remove the listener in case of cancellation
                        I_CategoriesRepository.caReference.removeEventListener(this)
                    }
                }
            }

            // Attach the listener
            I_CategoriesRepository.caReference.addValueEventListener(listener)

            // Ensure listener is removed if coroutine is cancelled
            continuation.invokeOnCancellation {
                I_CategoriesRepository.caReference.removeEventListener(listener)
            }
        }
    }

    override suspend fun getCategoriesById(id: String): I_CategoriesProduits? {
        return modelDatas.find { it.id.toString() == id }
    }

    // Fix 2: A_ProduitModel.kt - Fixing the updateUneSeulData method
    override fun updateUneSeulData(data: I_CategoriesProduits) {
        // Find and upsertLenceCommandeRepoGroupedProtoAvantJuin3 the item in the modelDatas list if it exists
        val index = modelDatas.indexOfFirst { it.id == data.id }
        if (index >= 0) {
            modelDatas[index] = data
        }

        // Update the item in Firebase
        I_CategoriesRepository.caReference.child(data.id.toString()).setValue(data)
    }
    override suspend fun updateDatas(datas: SnapshotStateList<I_CategoriesProduits>) {
        // Update local modelDatas with the new data
        modelDatas.clear()
        modelDatas.addAll(datas)

        // Update Firebase with the new data
        datas.forEach { category ->
            I_CategoriesRepository.caReference.child(category.id.toString()).setValue(category)
        }
    }
}
