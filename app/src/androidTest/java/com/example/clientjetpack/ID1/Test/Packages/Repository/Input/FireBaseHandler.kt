package com.example.clientjetpack.ID1.Test.Packages.Repository.Input

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.InputEtInfosSqlModels
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireBaseHandler() {
    suspend fun <T> loadDatasAsync(
        databaseRef: DatabaseReference,
        dataClass: Class<T>,
    ): List<T> {
        return suspendCancellableCoroutine { continuation ->
            val dataList = mutableListOf<T>()

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
                is InputEtInfosSqlModels.Tarification -> item.vidTimestamp.toString()
                is InputEtInfosSqlModels.ClientDataBase -> item.id.toString()
                is InputEtInfosSqlModels.ProduitInfos -> item.id.toString()
                is InputEtInfosSqlModels.TypeTarificationDataBase -> item.id.toString()
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
        val key = ref.push().key ?: return
        ref.child(key).setValue(item)
            .addOnSuccessListener {
                // Success handling if needed
            }
            .addOnFailureListener { e ->
                // Error handling
                e.printStackTrace()
            }
    }
}
