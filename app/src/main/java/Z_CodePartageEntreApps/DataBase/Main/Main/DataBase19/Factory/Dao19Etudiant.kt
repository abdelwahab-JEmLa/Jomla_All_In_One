package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase19.Factory

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao19Etudiant {
    @Query("DELETE FROM M19Etudiant WHERE keyID = :keyId")
    suspend fun deleteByKeyId(keyId: String)

    @Upsert
    suspend fun upsert(data: M19Etudiant)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(datas: List<M19Etudiant>)

    @Delete
    suspend fun delete(data: M19Etudiant)

    @Update
    suspend fun update(data: M19Etudiant)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: M19Etudiant): Long

    @Query("SELECT COUNT(*) FROM M19Etudiant")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM M19Etudiant")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M19Etudiant ")
    suspend fun getAll(): MutableList<M19Etudiant>

    @Query("SELECT * FROM M19Etudiant")
    fun getAllFlow(): Flow<List<M19Etudiant>>

    @Query("DELETE FROM M19Etudiant")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteData(data: M19Etudiant)

}
