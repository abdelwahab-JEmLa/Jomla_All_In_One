package Z_MasterOfApps.Kotlin.Model

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.firebaseDatabase
import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.ref_HeadOfModels
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface A_ProduitModelRepository {
    var modelDatas: SnapshotStateList<A_ProduitModel>
    val progressRepo: MutableStateFlow<Float> //

    suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<A_ProduitModel>, Flow<Float>>
    fun updateModelDatas(datas: SnapshotStateList<A_ProduitModel>)

    companion object {
        val ancienFireBaseRef = firebaseDatabase.getReference("e_DBJetPackExport")
        val caReference = ref_HeadOfModels.child("produits")
    }
}

class A_ProduitModelRepositoryImpl : A_ProduitModelRepository {
    override var modelDatas: SnapshotStateList<A_ProduitModel> = mutableStateListOf()
    override val progressRepo: MutableStateFlow<Float> = MutableStateFlow(0f) // Initialize progressRepo

    private var listener: ValueEventListener? = null

    init {
        // Initialize the listener when the repository is created
        startDatabaseListener()
    }

    private fun startDatabaseListener() {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val datas = mutableListOf<A_ProduitModel>()
                    val totalItems = snapshot.childrenCount.toInt()
                    var processedItems = 0

                    modelDatas.clear()
                    progressRepo.value = 0f

                    for (dataSnapshot in snapshot.children) {
                        val data = dataSnapshot.getValue(A_ProduitModel::class.java)
                        data?.let { cat ->
                            datas.add(cat)
                            modelDatas.add(cat)
                        }

                        processedItems++
                        progressRepo.value = processedItems.toFloat() / totalItems.toFloat()
                    }

                    progressRepo.value = 1.0f
                } catch (e: Exception) {
                    // Handle the exception
                    Log.e("A_ProduitModelRepositoryImpl", "Error loading data: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("A_ProduitModelRepositoryImpl", "Database error: ${error.message}")
            }
        }

        // Attach the listener to the Firebase reference
        listener?.let {
            A_ProduitModelRepository.caReference.addValueEventListener(it)
        }
    }

    override suspend fun onDataBaseChangeListnerAndLoad(): Pair<List<A_ProduitModel>, Flow<Float>> {
        val progressFlow = MutableStateFlow(0f)

        val datasLisning = suspendCancellableCoroutine<List<A_ProduitModel>> { continuation ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val datas = mutableListOf<A_ProduitModel>()
                        val totalItems = snapshot.childrenCount.toInt()
                        var processedItems = 0

                        modelDatas.clear()
                        progressFlow.value = 0f

                        for (dataSnapshot in snapshot.children) {
                            val data = dataSnapshot.getValue(A_ProduitModel::class.java)
                            data?.let { cat ->
                                datas.add(cat)
                                modelDatas.add(cat)
                            }

                            processedItems++
                            progressFlow.value = processedItems.toFloat() / totalItems.toFloat()
                        }

                        progressFlow.value = 1.0f
                        continuation.resume(datas)
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(Exception("Database error: ${error.message}"))
                }
            }

            A_ProduitModelRepository.caReference.addValueEventListener(listener)
            continuation.invokeOnCancellation {
                A_ProduitModelRepository.caReference.removeEventListener(listener)
            }
        }

        return Pair(datasLisning, progressFlow)
    }

    override fun updateModelDatas(datas: SnapshotStateList<A_ProduitModel>) {
        modelDatas = datas

        datas.forEach {
            A_ProduitModelRepository.caReference.child(it.id.toString()).setValue(it)
        }
    }

    fun cleanup() {
        // Remove the listener when the repository is no longer needed
        listener?.let {
            A_ProduitModelRepository.caReference.removeEventListener(it)
        }
    }
}
