package a_MainAppCompnents

import a_RoomDB.ArticlesBasesStatsModel
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
    @Query("SELECT * FROM ArticlesBasesStatsModel ORDER BY idCategorie")
    suspend fun getAll(): MutableList<ArticlesBasesStatsModel>

    @Transaction
    suspend fun transaction(block: suspend ArticlesBasesStatsModelDao.() -> Unit) {
        block()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: ArticlesBasesStatsModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articlesBasesStatsModel: List<ArticlesBasesStatsModel>)

    @Query("DELETE FROM ArticlesBasesStatsModel")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(articlesBasesStatsModel: List<ArticlesBasesStatsModel>)
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
