package com.example.clientjetpack.ID3.Test.DataBase.SQL.Home

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.A_ProduitInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.B_ClientInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.C_TypeTarificationInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.D_TarificationInfos
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.DataBasesInfosSql
import com.example.clientjetpack.ID3.Test.DataBase.SQL.Models.TypeTarificationEnum
import com.google.android.gms.tasks.Task
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

    fun addToFirebaseAsync(
        dataBasesInfosSql: DataBasesInfosSql,
        onSuccess: () -> Unit={}
    ) {
        val firebaseData = mapToFirebaseFormat(dataBasesInfosSql)

        // Using batch updates instead of single setValue operation
        val updates = mutableMapOf<String, Any>()

        // Add each collection as a separate batch update
        if (firebaseData.containsKey("produits")) {
            updates["produits"] = firebaseData["produits"] as Any
        }

        if (firebaseData.containsKey("clients")) {
            updates["clients"] = firebaseData["clients"] as Any
        }

        if (firebaseData.containsKey("typeTarifications")) {
            updates["typeTarifications"] = firebaseData["typeTarifications"] as Any
        }

        if (firebaseData.containsKey("tarifications")) {
            updates["tarifications"] = firebaseData["tarifications"] as Any
        }

        // Execute batch update and invoke onSuccess when complete
        ref.updateChildren(updates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Log error but still call onSuccess to ensure the flow continues
                println("Firebase update failed: ${exception.message}")
                onSuccess()
            }
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
        val products = mutableListOf<A_ProduitInfos>()
        val clients = mutableListOf<B_ClientInfos>()
        val typeTarifications = mutableListOf<C_TypeTarificationInfos>()
        val tarifications = mutableListOf<D_TarificationInfos>()

        val productsSnapshot = snapshot.child("produits")
        if (productsSnapshot.exists()) {
            for (productSnap in productsSnapshot.children) {
                try {
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
                    println("Error parsing product data: ${e.message}")
                }
            }
        }

        val clientsSnapshot = snapshot.child("clients")
        if (clientsSnapshot.exists()) {
            for (clientSnap in clientsSnapshot.children) {
                try {
                    val client = B_ClientInfos::class.java.getDeclaredConstructor().newInstance()

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

        val typeTarifsSnapshot = snapshot.child("typeTarifications")
        if (typeTarifsSnapshot.exists()) {
            for (typeSnap in typeTarifsSnapshot.children) {
                try {
                    val typeTarif = C_TypeTarificationInfos::class.java.getDeclaredConstructor().newInstance()

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

        val tarifsSnapshot = snapshot.child("tarifications")
        if (tarifsSnapshot.exists()) {
            for (tarifSnap in tarifsSnapshot.children) {
                try {
                    val tarif = D_TarificationInfos::class.java.getDeclaredConstructor().newInstance()

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

        return DataBasesInfosSql(
            a_ProduitInfos = products,
            b_ClientInfos = clients,
            c_TypeTarificationInfos = typeTarifications,
            d_TarificationInfos = tarifications
        )
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resume(result)
        }
        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
        continuation.invokeOnCancellation {
            if (isComplete.not()) {
                cancel()
            }
        }
    }
}
