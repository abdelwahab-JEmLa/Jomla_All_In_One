package Z_CodePartageEntreApps.Repository._1_5_Vendeur.Extension.DataBase

import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _1_5_VendeurDao {
    @Query("SELECT * FROM _1_5_Vendeur")
    suspend fun getAll(): MutableList<_1_5_Vendeur>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _1_5_Vendeur)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_1_5_Vendeur>)

    @Delete
    suspend fun delete(item: _1_5_Vendeur)

    @Query("DELETE FROM _1_5_Vendeur")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _1_5_Vendeur")
    fun getCount(): Int
}
