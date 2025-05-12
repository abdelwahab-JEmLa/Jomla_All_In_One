package com.example.clientjetpack.ID1.Test

import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.InputEtInfosSqlGroupeRepositorysImp
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.Test.initialClientsData
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.Test.initialProductsData
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.Test.initialTestData
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Repository.Input.Test.initialTypeTarificationData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * Cette classe initialise toutes les données requises pour les tests
 */
class TestDataInitializer {
    private val repos = InputEtInfosSqlGroupeRepositorysImp()
    private val fireBaseHandler = repos.fireBaseHandler
    
    /**
     * Initialise toutes les données nécessaires pour les tests
     */
    suspend fun initializeAllTestData() {
        // Initialiser les produits
        initializeProducts()
        
        // Initialiser les clients
        initializeClients()
        
        // Initialiser les types de tarification
        initializeTypeTarification()
        
        // Initialiser les tarifications
        initializeTarifications()
    }
    
    /**
     * Initialise les données de produits
     */
    private suspend fun initializeProducts() {
        val productsRef = getProductsReference()
        fireBaseHandler.clearDatabaseAsync(productsRef)
        
        initialProductsData.forEach { produit ->
            productsRef.child(produit.id.toString()).setValue(produit).await()
        }
    }
    
    /**
     * Initialise les données de clients
     */
    private suspend fun initializeClients() {
        val clientsRef = getClientsReference()
        fireBaseHandler.clearDatabaseAsync(clientsRef)
        
        initialClientsData.forEach { client ->
            clientsRef.child(client.id.toString()).setValue(client).await()
        }
    }
    
    /**
     * Initialise les données de types de tarification
     */
    private suspend fun initializeTypeTarification() {
        val typeTarifRef = getTypeTarificationReference()
        fireBaseHandler.clearDatabaseAsync(typeTarifRef)
        
        initialTypeTarificationData.forEach { typeTarif ->
            typeTarifRef.child(typeTarif.id.toString()).setValue(typeTarif).await()
        }
    }
    
    /**
     * Initialise les données de tarification
     */
    private suspend fun initializeTarifications() {
        val tarifRef = getTarificationReference()
        fireBaseHandler.clearDatabaseAsync(tarifRef)
        
        // Utilise la méthode du repository pour ajouter les tarifications
        fireBaseHandler.addAllToFireBaseAsync(initialTestData, tarifRef)
    }
    
    /**
     * Récupère la référence Firebase pour les produits
     */
    private fun getProductsReference(): DatabaseReference {
        return FirebaseDatabase.getInstance()
            .getReference("C_InputEtInfosSql/ProductInfos")
    }
    
    /**
     * Récupère la référence Firebase pour les clients
     */
    private fun getClientsReference(): DatabaseReference {
        return FirebaseDatabase.getInstance()
            .getReference("C_InputEtInfosSql/ClientDataBase")
    }
    
    /**
     * Récupère la référence Firebase pour les types de tarification
     */
    private fun getTypeTarificationReference(): DatabaseReference {
        return FirebaseDatabase.getInstance()
            .getReference("C_InputEtInfosSql/TypeTarificationDataBase")
    }
    
    /**
     * Récupère la référence Firebase pour les tarifications
     */
    private fun getTarificationReference(): DatabaseReference {
        return FirebaseDatabase.getInstance()
            .getReference("C_InputEtInfosSql/A_Tarification")
    }
}
