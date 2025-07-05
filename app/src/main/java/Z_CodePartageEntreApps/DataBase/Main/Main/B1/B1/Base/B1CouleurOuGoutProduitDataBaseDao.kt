package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base

import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface B1CouleurOuGoutProduitDataBaseDao {
    @Query("SELECT * FROM M3CouleurProduitInfos")
    fun getAllFlow(): Flow<List<M3CouleurProduitInfos>>

    @Update
    suspend fun update(data: M3CouleurProduitInfos)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: M3CouleurProduitInfos)

    @Delete
    suspend fun delete(article: M3CouleurProduitInfos)

    @Query("SELECT * FROM M3CouleurProduitInfos ")
    suspend fun getAll(): MutableList<M3CouleurProduitInfos>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articlesBasesStatTabelles: List<M3CouleurProduitInfos>)

    @Query("DELETE FROM M3CouleurProduitInfos")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM M3CouleurProduitInfos")
    suspend fun getCount(): Int

    @Query("SELECT CASE WHEN COUNT(*) = 0 THEN 1 ELSE 0 END FROM M3CouleurProduitInfos")
    suspend fun isTableEmpty(): Boolean
}
