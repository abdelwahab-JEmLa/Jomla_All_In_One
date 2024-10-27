package a_RoomDB

import a_MainAppCompnents.ArticlesBasesStatsModelDao
import a_MainAppCompnents.CategoriesModelDao
import a_MainAppCompnents.ClientsModelDao
import a_MainAppCompnents.ColorsArticlesDao
import a_MainAppCompnents.SoldArticlesTabelleDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ArticlesBasesStatsTabelle::class,
        CategoriesTabelle::class,
        ColorsArticlesTabelle::class,
        SoldArticlesTabelle::class,
        ClientsModel::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class Objects : RoomDatabase() {
    abstract fun articlesBasesStatsModelDao(): ArticlesBasesStatsModelDao
    abstract fun categoriesModelDao(): CategoriesModelDao
    abstract fun colorsArticlesDao(): ColorsArticlesDao
    abstract fun soldArticlesTabelleDao(): SoldArticlesTabelleDao
    abstract fun clientsModelDao(): ClientsModelDao



    companion object {
        @Volatile
        private var instance: Objects? = null

        fun getInstance(context: Context): Objects {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    Objects::class.java,
                    "app_database"
                ).build().also { instance = it }
            }
        }
    }
}
