package Z_CodePartageEntreApps.Repository._4_CouleurOperationCommand.Extension.DataBase

import Z_CodePartageEntreApps.Repository._4_CouleurOperationCommand._4_CouleurOperationCommand
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface _4_CouleurOperationCommandDao {
    @Query("DELETE FROM sqlite_sequence WHERE name = '_4_CouleurOperationCommand'")
    suspend fun restartSequence()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAndReturnVids(items: List<_4_CouleurOperationCommand>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvecRetureNewVid(item: _4_CouleurOperationCommand): Long

    @Query("SELECT * FROM _4_CouleurOperationCommand")
    suspend fun getAll(): MutableList<_4_CouleurOperationCommand>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: _4_CouleurOperationCommand)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<_4_CouleurOperationCommand>)

    @Delete
    suspend fun delete(item: _4_CouleurOperationCommand)

    @Query("DELETE FROM _4_CouleurOperationCommand")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM _4_CouleurOperationCommand")
    fun getCount(): Int
}
