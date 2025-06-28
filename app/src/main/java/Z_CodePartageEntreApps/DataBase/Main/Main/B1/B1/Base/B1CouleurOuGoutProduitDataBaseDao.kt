package Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.GrossistAchat.Fragment.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface B1CouleurOuGoutProduitDataBaseDao {
    @Query("SELECT * FROM B1CouleurOuGoutProduitDataBase")
    fun getAllFlow(): Flow<List<B1CouleurOuGoutProduitDataBase>>

    @Update
    suspend fun update(data: B1CouleurOuGoutProduitDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: B1CouleurOuGoutProduitDataBase)

    @Delete
    suspend fun delete(article: B1CouleurOuGoutProduitDataBase)

    @Query("SELECT * FROM B1CouleurOuGoutProduitDataBase ")
    suspend fun getAll(): MutableList<B1CouleurOuGoutProduitDataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articlesBasesStatTabelles: List<B1CouleurOuGoutProduitDataBase>)

    @Query("DELETE FROM B1CouleurOuGoutProduitDataBase")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM B1CouleurOuGoutProduitDataBase")
    suspend fun getCount(): Int

    @Query("SELECT CASE WHEN COUNT(*) = 0 THEN 1 ELSE 0 END FROM B1CouleurOuGoutProduitDataBase")
    suspend fun isTableEmpty(): Boolean
}
