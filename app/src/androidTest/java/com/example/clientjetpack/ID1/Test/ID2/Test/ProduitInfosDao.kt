package com.example.clientjetpack.ID1.Test.ID2.Test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProduitInfosDao {
    @Query("SELECT * FROM produits")
    fun getAllProduits(): Flow<List<A_ProduitInfos>>
    
    @Query("SELECT * FROM produits")
    suspend fun getAllProduitsSync(): List<A_ProduitInfos>
    
    @Query("SELECT * FROM produits WHERE id = :id")
    suspend fun getProduitById(id: Long): A_ProduitInfos?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(produit: A_ProduitInfos): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(produits: List<A_ProduitInfos>)
    
    @Update
    suspend fun update(produit: A_ProduitInfos)
    
    @Query("DELETE FROM produits")
    suspend fun deleteAll()
}

@Dao
interface ClientInfosDao {
    @Query("SELECT * FROM clients")
    fun getAllClients(): Flow<List<B_ClientInfos>>
    
    @Query("SELECT * FROM clients")
    suspend fun getAllClientsSync(): List<B_ClientInfos>
    
    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: Long): B_ClientInfos?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: B_ClientInfos): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clients: List<B_ClientInfos>)
    
    @Update
    suspend fun update(client: B_ClientInfos)
    
    @Query("DELETE FROM clients")
    suspend fun deleteAll()
}

@Dao
interface TypeTarificationInfosDao {
    @Query("SELECT * FROM type_tarifications")
    fun getAllTypeTarifications(): Flow<List<C_TypeTarificationInfos>>
    
    @Query("SELECT * FROM type_tarifications")
    suspend fun getAllTypeTarificationsSync(): List<C_TypeTarificationInfos>
    
    @Query("SELECT * FROM type_tarifications WHERE id = :id")
    suspend fun getTypeTarificationById(id: Long): C_TypeTarificationInfos?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(typeTarification: C_TypeTarificationInfos): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(typeTarifications: List<C_TypeTarificationInfos>)
    
    @Update
    suspend fun update(typeTarification: C_TypeTarificationInfos)
    
    @Query("DELETE FROM type_tarifications")
    suspend fun deleteAll()
}

@Dao
interface TarificationInfosDao {
    @Query("SELECT * FROM tarifications")
    fun getAllTarifications(): Flow<List<D_TarificationInfos>>
    
    @Query("SELECT * FROM tarifications")
    suspend fun getAllTarificationsSync(): List<D_TarificationInfos>
    
    @Query("SELECT * FROM tarifications WHERE vidTimestamp = :id")
    suspend fun getTarificationById(id: Long): D_TarificationInfos?
    
    @Query("SELECT * FROM tarifications WHERE idProduit = :produitId AND idClient = :clientId")
    suspend fun getTarificationsByProduitAndClient(produitId: Long, clientId: Long): List<D_TarificationInfos>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarification: D_TarificationInfos)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tarifications: List<D_TarificationInfos>)
    
    @Update
    suspend fun update(tarification: D_TarificationInfos)
    
    @Query("DELETE FROM tarifications")
    suspend fun deleteAll()
}
