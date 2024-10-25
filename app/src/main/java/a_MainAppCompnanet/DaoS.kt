package a_MainAppCompnents

import a_RoomDB.Categories
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CategoriesDao {
    @Transaction
    suspend fun transaction(block: suspend CategoriesDao.() -> Unit) {
        block()
    }

    @Query("SELECT * FROM Categories ORDER BY id")
    suspend fun getAllCategoriesList(): MutableList<Categories>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Categories)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Categories>)

    @Query("DELETE FROM Categories")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<Categories>)
}

