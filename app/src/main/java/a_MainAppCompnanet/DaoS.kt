package a_MainAppCompnents

import a_RoomDB.ArticlesBasesStats
import a_RoomDB.CategoriesModel
import a_RoomDB.ColorsArticles
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface CategoriesModelDao {
    @Query("SELECT * FROM CategoriesModel ORDER BY idClassementCategorieInCategoriesTabele")
    suspend fun getAll(): MutableList<CategoriesModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoriesModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoriesModel>)

    @Query("DELETE FROM CategoriesModel")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<CategoriesModel>)

    @Transaction
    suspend fun transaction(block: suspend CategoriesModelDao.() -> Unit) {
        block()
    }
}

@Dao
interface ArticlesBasesStatsModelDao {
    @Query("SELECT * FROM ArticlesBasesStats ORDER BY idCategorie")
    suspend fun getAll(): MutableList<ArticlesBasesStats>

    @Transaction
    suspend fun transaction(block: suspend ArticlesBasesStatsModelDao.() -> Unit) {
        block()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: ArticlesBasesStats)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articlesBasesStats: List<ArticlesBasesStats>)

    @Query("DELETE FROM ArticlesBasesStats")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(articlesBasesStats: List<ArticlesBasesStats>)
}


@Dao
interface ColorsArticlesDao {
    @Query("SELECT * FROM ColorsArticles ORDER BY classementColore")
    suspend fun getAllOrdred(): MutableList<ColorsArticles>

    @Transaction
    suspend fun transaction(block: suspend ColorsArticlesDao.() -> Unit) {
        block()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: ColorsArticles)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(colorsArticles: List<ColorsArticles>)

    @Query("DELETE FROM ColorsArticles")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(colorsArticles: List<ColorsArticles>)
}
