package Z_CodePartageEntreApps.Model.I_CategorieProduits.Z.Repository.Extension

import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface I_CategorieProduitsDao{
    @Query("SELECT * FROM I_CategorieProduits ")
    suspend fun getAll(): MutableList<I_CategorieProduits>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: I_CategorieProduits)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<I_CategorieProduits>)

    @Delete
    suspend fun delete(item: I_CategorieProduits)

    @Query("DELETE FROM I_CategorieProduits")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM I_CategorieProduits")
    fun getCount(): Int

}
