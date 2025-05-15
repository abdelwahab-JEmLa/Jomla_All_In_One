package com.example.clientjetpack.ID3.Test.DataBase.Repo.Home

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.A_ProduitInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.B_ClientInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.C_TypeTarificationInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.D_TarificationInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.DataBasesInfosSql
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.TypeTarificationEnum
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
import kotlin.reflect.full.memberProperties

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

    private fun mapToFirebaseFormat(dataBasesInfosSql: DataBasesInfosSql): Map<String, Any> {
        val data = mutableMapOf<String, Any>()

        // Map products 
        val productsMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.a_ProduitInfos.forEach { produit ->
            // Use reflection to get all properties dynamically
            val produitMap = produit::class.memberProperties.associate {
                it.name to it.getter.call(produit)
            }
            productsMap["prod_${produit.id}"] = produitMap
        }
        data["produits"] = productsMap

        // Map clients
        val clientsMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.b_ClientInfos.forEach { client ->
            val clientMap = client::class.memberProperties.associate {
                it.name to it.getter.call(client)
            }
            clientsMap["client_${client.id}"] = clientMap
        }
        data["clients"] = clientsMap

        // Map type tarifications
        val typeTarifMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.c_TypeTarificationInfos.forEach { typeTarif ->
            val typeMap = mutableMapOf<String, Any>()
            typeTarif::class.memberProperties.forEach { prop ->
                val value = prop.getter.call(typeTarif)
                // Handle enum specially
                if (value is TypeTarificationEnum) {
                    typeMap[prop.name] = value.name
                } else {
                    typeMap[prop.name] = value as Any
                }
            }
            typeTarifMap["type_${typeTarif.id}"] = typeMap
        }
        data["typeTarifications"] = typeTarifMap

        // Map tarifications
        val tarifsMap = mutableMapOf<String, Any>()
        dataBasesInfosSql.d_TarificationInfos.forEach { tarif ->
            val tarifMap = tarif::class.memberProperties.associate {
                it.name to it.getter.call(tarif)
            }
            tarifsMap["tarif_${tarif.vidTimestamp}"] = tarifMap
        }
        data["tarifications"] = tarifsMap

        return data
    }

    private fun mapFromFirebaseSnapshot(snapshot: DataSnapshot): DataBasesInfosSql {
        // Create empty lists for each data type
        val products = mutableListOf<A_ProduitInfos>()
        val clients = mutableListOf<B_ClientInfos>()
        val typeTarifications = mutableListOf<C_TypeTarificationInfos>()
        val tarifications = mutableListOf<D_TarificationInfos>()

        // Extract products
        val productsSnapshot = snapshot.child("produits")
        if (productsSnapshot.exists()) {
            for (productSnap in productsSnapshot.children) {
                try {
                    // Use reflection to create a new product instance with all fields
                    val id = productSnap.child("id").getValue(Long::class.java) ?: 0L
                    val product = A_ProduitInfos::class.java.getDeclaredConstructor().newInstance()

                    // Set each field dynamically
                    for (field in A_ProduitInfos::class.java.declaredFields) {
                        field.isAccessible = true
                        val childSnapshot = productSnap.child(field.name)
                        if (childSnapshot.exists()) {
                            val value = when (field.type) {
                                Long::class.java -> childSnapshot.getValue(Long::class.java)
                                String::class.java -> childSnapshot.getValue(String::class.java)
                                Boolean::class.java -> childSnapshot.getValue(Boolean::class.java)
                                else -> null
                            }
                            if (value != null) {
                                field.set(product, value)
                            }
                        }
                    }

                    products.add(product)
                } catch (e: Exception) {
                    // Handle parsing errors safely
                    println("Error parsing product data: ${e.message}")
                }
            }
        }

        // Extract clients
        val clientsSnapshot = snapshot.child("clients")
        if (clientsSnapshot.exists()) {
            for (clientSnap in clientsSnapshot.children) {
                try {
                    // Use reflection to create a new client instance with all fields
                    val client = B_ClientInfos::class.java.getDeclaredConstructor().newInstance()

                    // Set each field dynamically
                    for (field in B_ClientInfos::class.java.declaredFields) {
                        field.isAccessible = true
                        val childSnapshot = clientSnap.child(field.name)
                        if (childSnapshot.exists()) {
                            val value = when (field.type) {
                                Long::class.java -> childSnapshot.getValue(Long::class.java)
                                String::class.java -> childSnapshot.getValue(String::class.java)
                                Boolean::class.java -> childSnapshot.getValue(Boolean::class.java)
                                else -> null
                            }
                            if (value != null) {
                                field.set(client, value)
                            }
                        }
                    }

                    clients.add(client)
                } catch (e: Exception) {
                    println("Error parsing client data: ${e.message}")
                }
            }
        }

        // Extract type tarifications
        val typeTarifsSnapshot = snapshot.child("typeTarifications")
        if (typeTarifsSnapshot.exists()) {
            for (typeSnap in typeTarifsSnapshot.children) {
                try {
                    // Use reflection to create a new type tarification instance with all fields
                    val typeTarif = C_TypeTarificationInfos::class.java.getDeclaredConstructor().newInstance()

                    // Set each field dynamically
                    for (field in C_TypeTarificationInfos::class.java.declaredFields) {
                        field.isAccessible = true
                        val childSnapshot = typeSnap.child(field.name)
                        if (childSnapshot.exists()) {
                            val value = when (field.type) {
                                Long::class.java -> childSnapshot.getValue(Long::class.java)
                                String::class.java -> childSnapshot.getValue(String::class.java)
                                Boolean::class.java -> childSnapshot.getValue(Boolean::class.java)
                                TypeTarificationEnum::class.java -> {
                                    val typeTarifString = childSnapshot.getValue(String::class.java) ?: "ParBenifice"
                                    try {
                                        TypeTarificationEnum.valueOf(typeTarifString)
                                    } catch (e: Exception) {
                                        TypeTarificationEnum.ParBenifice
                                    }
                                }
                                else -> null
                            }
                            if (value != null) {
                                field.set(typeTarif, value)
                            }
                        }
                    }

                    typeTarifications.add(typeTarif)
                } catch (e: Exception) {
                    println("Error parsing type tarification data: ${e.message}")
                }
            }
        }

        // Extract tarifications
        val tarifsSnapshot = snapshot.child("tarifications")
        if (tarifsSnapshot.exists()) {
            for (tarifSnap in tarifsSnapshot.children) {
                try {
                    // Use reflection to create a new tarification instance with all fields
                    val tarif = D_TarificationInfos::class.java.getDeclaredConstructor().newInstance()

                    // Set each field dynamically
                    for (field in D_TarificationInfos::class.java.declaredFields) {
                        field.isAccessible = true
                        val childSnapshot = tarifSnap.child(field.name)
                        if (childSnapshot.exists()) {
                            val value = when (field.type) {
                                Long::class.java -> childSnapshot.getValue(Long::class.java)
                                Double::class.java -> childSnapshot.getValue(Double::class.java)
                                Boolean::class.java -> childSnapshot.getValue(Boolean::class.java)
                                else -> null
                            }
                            if (value != null) {
                                field.set(tarif, value)
                            }
                        }
                    }

                    tarifications.add(tarif)
                } catch (e: Exception) {
                    println("Error parsing tarification data: ${e.message}")
                }
            }
        }

        // Return the complete data structure
        return DataBasesInfosSql(
            a_ProduitInfos = products,
            b_ClientInfos = clients,
            c_TypeTarificationInfos = typeTarifications,
            d_TarificationInfos = tarifications
        )
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
