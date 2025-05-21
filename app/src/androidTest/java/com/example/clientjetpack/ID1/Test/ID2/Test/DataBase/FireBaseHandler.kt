package com.example.clientjetpack.ID1.Test.ID2.Test.DataBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.A_ProduitInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.B_ClientInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.C_TypeTarificationInfos
import com.example.clientjetpack.ID1.Test.ID2.Test.DataBase.Repo.Models.D_TarificationInfos
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireBaseHandler() {
    private val startFireBaseReference: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InfosSqlDataBases")

    private val produitRef = startFireBaseReference.child("A_ProduitInfos")
    private val clientRef = startFireBaseReference.child("B_ClientInfos")
    private val typeTarificationRef = startFireBaseReference.child("C_TypeTarificationInfos")
    private val tarificationRef = startFireBaseReference.child("D1_Tariff")

    // Getter methods for references to be used in repository implementations
    fun getProduitRef(): DatabaseReference = produitRef
    fun getClientRef(): DatabaseReference = clientRef
    fun getTypeTarificationRef(): DatabaseReference = typeTarificationRef
    fun getTarificationRef(): DatabaseReference = tarificationRef

    // New method to get a reference by type
    fun getRefByType(type: Class<*>): DatabaseReference {
        return when (type) {
            A_ProduitInfos::class.java -> produitRef
            B_ClientInfos::class.java -> clientRef
            C_TypeTarificationInfos::class.java -> typeTarificationRef
            D_TarificationInfos::class.java -> tarificationRef
            else -> throw IllegalArgumentException("Unknown type: ${type.simpleName}")
        }
    }

    suspend fun <T> loadDatasAsync(
        databaseRef: DatabaseReference,
        dataClass: Class<T>,
    ): List<T> {
        // Get reference from within this class using the type parameter
        val ref = when {
            databaseRef != produitRef && databaseRef != clientRef &&
                    databaseRef != typeTarificationRef && databaseRef != tarificationRef -> {
                getRefByType(dataClass)
            }
            else -> databaseRef
        }

        return suspendCancellableCoroutine { continuation ->
            val dataList = mutableListOf<T>()

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.getValue(dataClass)?.let {
                            dataList.add(it)
                        }
                    }

                    continuation.resume(dataList)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    suspend fun <T> addAllToFireBaseAsync(
        modelList: List<T>,
        databaseRef: DatabaseReference,
    ) {
        if (modelList.isEmpty()) return

        modelList.map { item ->
            val key = when (item) {
                is D_TarificationInfos -> item.vidTimestamp.toString()
                is A_ProduitInfos -> item.id.toString()
                is B_ClientInfos -> item.id.toString()
                is C_TypeTarificationInfos -> item.id.toString()
                else -> databaseRef.push().key
            } ?: databaseRef.push().key

            suspendCancellableCoroutine { continuation ->
                databaseRef.child(key!!).setValue(item).addOnSuccessListener {
                    continuation.resume(Unit)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            }
        }
    }

    suspend fun clearDatabaseAsync(databaseRef: DatabaseReference) {
        return suspendCancellableCoroutine { continuation ->
            databaseRef.removeValue().addOnSuccessListener {
                continuation.resume(Unit)
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }

    fun addToFirebaseAsync(item: Any, ref: DatabaseReference) {
        val key = when(item) {
            is D_TarificationInfos -> item.vidTimestamp.toString()
            is A_ProduitInfos -> item.id.toString()
            is B_ClientInfos -> item.id.toString()
            is C_TypeTarificationInfos -> item.id.toString()
            else -> ref.push().key
        } ?: return

        ref.child(key).setValue(item)
            .addOnSuccessListener {
                // Success handling if needed
            }
            .addOnFailureListener { e ->
                // Error handling
                e.printStackTrace()
            }
    }

    // Overloaded version that determines reference from item type
    fun addToFirebaseAsync(item: Any) {
        val ref = when (item) {
            is A_ProduitInfos -> produitRef
            is B_ClientInfos -> clientRef
            is C_TypeTarificationInfos -> typeTarificationRef
            is D_TarificationInfos -> tarificationRef
            else -> throw IllegalArgumentException("Unknown type: ${item.javaClass.simpleName}")
        }

        addToFirebaseAsync(item, ref)
    }
}
