package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase20.Factory

import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao20OrderEducative {
    @Query("DELETE FROM M20ObsarvationEtudion WHERE keyID = :keyId")
    suspend fun deleteByKeyId(keyId: String)

    @Upsert
    suspend fun upsert(data: M20ObsarvationEtudion)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(datas: List<M20ObsarvationEtudion>)

    @Delete
    suspend fun delete(data: M20ObsarvationEtudion)

    @Update
    suspend fun update(data: M20ObsarvationEtudion)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(data: M20ObsarvationEtudion): Long

    @Query("SELECT COUNT(*) FROM M20ObsarvationEtudion")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM M20ObsarvationEtudion")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M20ObsarvationEtudion ")
    suspend fun getAll(): MutableList<M20ObsarvationEtudion>

    @Query("SELECT * FROM M20ObsarvationEtudion")
    fun getAllFlow(): Flow<List<M20ObsarvationEtudion>>

    @Query("DELETE FROM M20ObsarvationEtudion")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteData(data: M20ObsarvationEtudion)

}
