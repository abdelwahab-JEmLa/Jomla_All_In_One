package Z_CodePartageEntreApps.Proto.Par.Type.Repository

import Z_CodePartageEntreApps.Model.A_ProduitInfos
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface A_ProduitInfosDao {
    @Query("SELECT * FROM A_ProduitInfos")
    fun getAllProduits(): Flow<List<A_ProduitInfos>>

    @Query("SELECT * FROM A_ProduitInfos WHERE id = :id")
    suspend fun getProduitById(id: Long): A_ProduitInfos?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllReturnIDs(produits: List<A_ProduitInfos>): List<Long>

    @Query("DELETE FROM A_ProduitInfos")
    suspend fun deleteAll()

    // For single update - use IGNORE to let auto-increment work properly
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(produit: A_ProduitInfos): Long
    @Query("SELECT EXISTS(SELECT 1 FROM A_ProduitInfos WHERE id = :id)")
    suspend fun exists(id: Long): Boolean

    @Update
    suspend fun update(produitInfos: A_ProduitInfos)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(produitInfos: A_ProduitInfos): Long

    @Query("UPDATE A_ProduitInfos SET prixVent = :newPrice, needUpdate = 1 WHERE id = :id")
    suspend fun updatePrice(id: Long, newPrice: Double)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReturnID(produit: A_ProduitInfos): Long

    @Update
    suspend fun updateAll(produits: List<A_ProduitInfos>)

    @Query("SELECT COUNT(*) FROM A_ProduitInfos")
    suspend fun getCount(): Int

    @Query("SELECT * FROM A_ProduitInfos WHERE nom LIKE :searchQuery")
    suspend fun searchProduitsByNom(searchQuery: String): List<A_ProduitInfos>

    @Query("SELECT * FROM A_ProduitInfos WHERE needUpdate = 1")
    suspend fun getProduitsNeedingUpdate(): List<A_ProduitInfos>
}
