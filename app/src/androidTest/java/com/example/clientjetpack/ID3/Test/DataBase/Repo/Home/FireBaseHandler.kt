package com.example.clientjetpack.ID3.Test.DataBase.Repo.Home

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.A_ProduitInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.B_ClientInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.C_TypeTarificationInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.D_TarificationInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.DataBasesInfosSql
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FireBaseHandler {
    private val ref: DatabaseReference =
        _0_0_HeadOfRepositorys_Model.getHeadSqlDataBaseRef()
            .child("C_InfosSqlDataBases")

    fun addToFirebaseAsync(dataBasesInfosSql: DataBasesInfosSql) {
        val firebaseData = mapToFirebaseFormat(dataBasesInfosSql)
        ref.setValue(firebaseData)
    }

    suspend fun getDataFromFirebase(): DataBasesInfosSql? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val infosSqlDataBases = mapFromFirebaseSnapshot(snapshot)
                        continuation.resume(infosSqlDataBases)
                    } else {
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(Exception("Firebase data retrieval cancelled: ${error.message}"))
                }
            })

            continuation.invokeOnCancellation {
                // Clean up if needed
            }
        }
    }

    suspend fun clearAndAddTestData(testData: DataBasesInfosSql) = withContext(Dispatchers.IO) {
        ref.removeValue().await()

        val firebaseData = mapToFirebaseFormat(testData)
        ref.setValue(firebaseData).await()
    }

    private fun mapToFirebaseFormat(dataBasesInfosSql: DataBasesInfosSql): Map<String, Any> {
        val data = mutableMapOf<String, Any>()

        val productsMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.a_ProduitInfos.forEach { produit ->
            productsMap["prod_${produit.id}"] = mapOf(
                "id" to produit.id,
                "nom" to produit.nom,
                "needUpdate" to produit.needUpdate
            )
        }
        data["produits"] = productsMap

        val clientsMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.b_ClientInfos.forEach { client ->
            clientsMap["client_${client.id}"] = mapOf(
                "id" to client.id,
                "nom" to client.nom,
                "idActiveTypeTarificationDataBase" to client.idActiveTypeTarificationDataBase,
                "needUpdate" to client.needUpdate
            )
        }
        data["clients"] = clientsMap

        val typeTarifMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.c_TypeTarificationInfos.forEach { typeTarif ->
            typeTarifMap["type_${typeTarif.id}"] = mapOf(
                "id" to typeTarif.id,
                "typeTarificationEnum" to typeTarif.typeTarificationEnum.name,
                "needUpdate" to typeTarif.needUpdate
            )
        }
        data["typeTarifications"] = typeTarifMap

        // Map tarifications
        val tarifsMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.d_TarificationInfos.forEach { tarif ->
            tarifsMap["tarif_${tarif.vidTimestamp}"] = mapOf(
                "vidTimestamp" to tarif.vidTimestamp,
                "idProduit" to tarif.idProduit,
                "idClient" to tarif.idClient,
                "idTypeTarification" to tarif.idTypeTarification,
                "prixCurrency" to tarif.prixCurrency,
                "needUpdate" to tarif.needUpdate
            )
        }
        data["tarifications"] = tarifsMap

        return data
    }

    private fun mapFromFirebaseSnapshot(snapshot: DataSnapshot): DataBasesInfosSql {
        val produits = mutableListOf<A_ProduitInfos>()
        val clients = mutableListOf<B_ClientInfos>()
        val typeTarifications = mutableListOf<C_TypeTarificationInfos>()
        val tarifications = mutableListOf<D_TarificationInfos>()

        // Parse products
        snapshot.child("produits").children.forEach { produitSnapshot ->
            val id = produitSnapshot.child("id").getValue(Long::class.java) ?: 0
            val nom = produitSnapshot.child("nom").getValue(String::class.java) ?: ""
            val needUpdate = produitSnapshot.child("needUpdate").getValue(Boolean::class.java) ?: false

            produits.add(A_ProduitInfos(id, nom, needUpdate))
        }

        // Parse clients
        snapshot.child("clients").children.forEach { clientSnapshot ->
            val id = clientSnapshot.child("id").getValue(Long::class.java) ?: 0
            val nom = clientSnapshot.child("nom").getValue(String::class.java) ?: "Non Difinie"
            val idActiveTypeTarificationDataBase = clientSnapshot.child("idActiveTypeTarificationDataBase").getValue(Long::class.java) ?: 0
            val needUpdate = clientSnapshot.child("needUpdate").getValue(Boolean::class.java) ?: false

            clients.add(B_ClientInfos(id, nom, idActiveTypeTarificationDataBase, needUpdate))
        }

        // Parse type tarifications
        snapshot.child("typeTarifications").children.forEach { typeSnapshot ->
            val id = typeSnapshot.child("id").getValue(Long::class.java) ?: 0
            val typeEnumStr = typeSnapshot.child("typeTarificationEnum").getValue(String::class.java) ?: "ParBenifice"
            val typeEnum = try {
                com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.TypeTarificationEnum.valueOf(typeEnumStr)
            } catch (e: Exception) {
                com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.TypeTarificationEnum.ParBenifice
            }
            val needUpdate = typeSnapshot.child("needUpdate").getValue(Boolean::class.java) ?: false

            typeTarifications.add(C_TypeTarificationInfos(id, typeEnum, needUpdate))
        }

        // Parse tarifications
        snapshot.child("tarifications").children.forEach { tarifSnapshot ->
            val vidTimestamp = tarifSnapshot.child("vidTimestamp").getValue(Long::class.java) ?: 0L
            val idProduit = tarifSnapshot.child("idProduit").getValue(Long::class.java) ?: 0L
            val idClient = tarifSnapshot.child("idClient").getValue(Long::class.java) ?: 0L
            val idTypeTarification = tarifSnapshot.child("idTypeTarification").getValue(Long::class.java) ?: 0L
            val prixCurrency = tarifSnapshot.child("prixCurrency").getValue(Double::class.java) ?: 0.0
            val needUpdate = tarifSnapshot.child("needUpdate").getValue(Boolean::class.java) ?: false

            tarifications.add(D_TarificationInfos(vidTimestamp, idProduit, idClient, idTypeTarification, prixCurrency, needUpdate))
        }

        return DataBasesInfosSql(produits, clients, typeTarifications, tarifications)
    }

    // Extension function to convert Firebase Task to suspend function
    private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        continuation.invokeOnCancellation {
            // Cancel the task if possible
            if (isComplete.not()) {
                cancel()
            }
        }
    }
}
