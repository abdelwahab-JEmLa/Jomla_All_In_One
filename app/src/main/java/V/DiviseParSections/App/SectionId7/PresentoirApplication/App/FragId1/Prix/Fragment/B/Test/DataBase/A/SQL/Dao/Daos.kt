package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.D_TarificationInfos
import kotlinx.coroutines.flow.Flow
@Dao
interface A_ProduitInfosDao {
    @Query("SELECT * FROM A_ProduitInfos")
    fun getAllProduits(): Flow<List<A_ProduitInfos>>

    @Query("SELECT * FROM A_ProduitInfos")
    suspend fun getAllProduitsSync(): List<A_ProduitInfos>

    @Query("SELECT * FROM A_ProduitInfos WHERE id = :id")
    suspend fun getProduitById(id: Long): A_ProduitInfos?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(produit: A_ProduitInfos): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(produits: List<A_ProduitInfos>)

    @Update
    suspend fun update(produit: A_ProduitInfos)

    @Query("DELETE FROM A_ProduitInfos")
    suspend fun deleteAll()
}

@Dao
interface B_ClientInfosDao {
    @Query("SELECT * FROM B_ClientInfos")
    fun getAllClients(): Flow<List<B_ClientInfos>>

    @Query("SELECT * FROM B_ClientInfos")
    suspend fun getAllClientsSync(): List<B_ClientInfos>

    @Query("SELECT * FROM B_ClientInfos WHERE id = :id")
    suspend fun getClientById(id: Long): B_ClientInfos?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: B_ClientInfos): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clients: List<B_ClientInfos>)

    @Update
    suspend fun update(client: B_ClientInfos)

    @Query("DELETE FROM B_ClientInfos")
    suspend fun deleteAll()
}

@Dao
interface C_TypeTarificationInfosDao {
    @Query("SELECT * FROM C_TypeTarificationInfos")
    fun getAllTypeTarifications(): Flow<List<C_TypeTarificationInfos>>

    @Query("SELECT * FROM C_TypeTarificationInfos")
    suspend fun getAllTypeTarificationsSync(): List<C_TypeTarificationInfos>

    @Query("SELECT * FROM C_TypeTarificationInfos WHERE id = :id")
    suspend fun getTypeTarificationById(id: Long): C_TypeTarificationInfos?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(typeTarification: C_TypeTarificationInfos): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(typeTarifications: List<C_TypeTarificationInfos>)

    @Update
    suspend fun update(typeTarification: C_TypeTarificationInfos)

    @Query("DELETE FROM C_TypeTarificationInfos")
    suspend fun deleteAll()
}

@Dao
interface D_TarificationInfosDao {
    @Query("SELECT * FROM D_TarificationInfos")
    fun getAllTarifications(): Flow<List<D_TarificationInfos>>

    @Query("SELECT * FROM D_TarificationInfos")
    suspend fun getAllTarificationsSync(): List<D_TarificationInfos>

    @Query("SELECT * FROM D_TarificationInfos WHERE vidTimestamp = :id")
    suspend fun getTarificationById(id: Long): D_TarificationInfos?

    @Query("SELECT * FROM D_TarificationInfos WHERE idProduit = :produitId AND idClient = :clientId")
    suspend fun getTarificationsByProduitAndClient(produitId: Long, clientId: Long): List<D_TarificationInfos>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarification: D_TarificationInfos)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tarifications: List<D_TarificationInfos>)

    @Update
    suspend fun update(tarification: D_TarificationInfos)

    @Query("DELETE FROM D_TarificationInfos")
    suspend fun deleteAll()
}
