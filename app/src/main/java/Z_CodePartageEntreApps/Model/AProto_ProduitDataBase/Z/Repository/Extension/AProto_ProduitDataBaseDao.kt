package Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.Z.Repository.Extension

import Z_CodePartageEntreApps.Model.AProto_ProduitDataBase.AProto_ProduitDataBase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AProto_ProduitDataBaseDao{
    @Query("SELECT * FROM AProto_ProduitDataBase ")
    suspend fun getAll(): MutableList<AProto_ProduitDataBase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AProto_ProduitDataBase)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AProto_ProduitDataBase>)

    @Delete
    suspend fun delete(item: AProto_ProduitDataBase)

    @Query("DELETE FROM AProto_ProduitDataBase")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM AProto_ProduitDataBase")
    fun getCount(): Int

}
