package a_MainAppCompnents

import a_RoomDB.ArticlesBasesStatsTabelle
import a_RoomDB.CategoriesTabelle
import a_RoomDB.ColorsArticlesTabelle
import a_RoomDB.SoldArticlesTabelle
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
@Dao
interface ArticlesBasesStatsModelDao {
    @Query("SELECT * FROM ArticlesBasesStatsTabelle ORDER BY idCategorie")
    suspend fun getAll(): MutableList<ArticlesBasesStatsTabelle>

    @Transaction
    suspend fun transaction(block: suspend ArticlesBasesStatsModelDao.() -> Unit) {
        block()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: ArticlesBasesStatsTabelle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articlesBasesStatTabelles: List<ArticlesBasesStatsTabelle>)

    @Query("DELETE FROM ArticlesBasesStatsTabelle")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(articlesBasesStatTabelles: List<ArticlesBasesStatsTabelle>)
}
@Dao
interface ColorsArticlesDao {
    @Query("SELECT * FROM ColorsArticlesTabelle ORDER BY classementColore")
    suspend fun getAllOrdred(): MutableList<ColorsArticlesTabelle>

    @Transaction
    suspend fun transaction(block: suspend ColorsArticlesDao.() -> Unit) {
        block()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: ColorsArticlesTabelle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(colorsArticleTabelles: List<ColorsArticlesTabelle>)

    @Query("DELETE FROM ColorsArticlesTabelle")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(colorsArticleTabelles: List<ColorsArticlesTabelle>)
}
@Dao
interface CategoriesModelDao {
    @Query("SELECT * FROM CategoriesTabelle ORDER BY idClassementCategorieInCategoriesTabele")
    suspend fun getAll(): MutableList<CategoriesTabelle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoriesTabelle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoriesTabelle>)

    @Query("DELETE FROM CategoriesTabelle")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(categories: List<CategoriesTabelle>)

    @Transaction
    suspend fun transaction(block: suspend CategoriesModelDao.() -> Unit) {
        block()
    }
}

@Dao
interface SoldArticlesTabelleDao{
    @Query("SELECT * FROM SoldArticlesTabelle ORDER BY vid")
    suspend fun getAll(): MutableList<SoldArticlesTabelle>

    @Query("SELECT MAX(vid) FROM SoldArticlesTabelle")
    suspend fun getMaxId(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(soldArticlesTabelle: SoldArticlesTabelle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(soldArticles: List<SoldArticlesTabelle>)

    @Query("DELETE FROM SoldArticlesTabelle")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(soldArticles: List<SoldArticlesTabelle>)

    @Transaction
    suspend fun transaction(block: suspend SoldArticlesTabelleDao.() -> Unit) {
        block()
    }
}



