package Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.Extension

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface A_ProduitDao{
    @Query("SELECT * FROM A_Produit ")
    suspend fun getAll(): MutableList<A_Produit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: A_Produit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<A_Produit>)

    @Delete
    suspend fun delete(item: A_Produit)

    @Query("DELETE FROM A_Produit")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM A_Produit")
    fun getCount(): Int



}
