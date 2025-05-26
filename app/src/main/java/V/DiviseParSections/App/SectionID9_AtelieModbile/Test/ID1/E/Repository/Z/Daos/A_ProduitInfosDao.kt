package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.Z.Daos

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
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

    @Update
    suspend fun update(produit: A_ProduitInfos)

    @Query("DELETE FROM A_ProduitInfos")
    suspend fun deleteAll()

    // For single insert - use IGNORE to let auto-increment work properly
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(produit: A_ProduitInfos): Long

    @Update
    suspend fun updateAll(produits: List<A_ProduitInfos>)

    @Query("SELECT COUNT(*) FROM A_ProduitInfos")
    suspend fun getCount(): Int

    @Query("SELECT * FROM A_ProduitInfos WHERE nomArticleFinale LIKE :searchQuery")
    suspend fun searchProduitsByNom(searchQuery: String): List<A_ProduitInfos>

    @Query("SELECT * FROM A_ProduitInfos WHERE needUpdate = 1")
    suspend fun getProduitsNeedingUpdate(): List<A_ProduitInfos>
}
