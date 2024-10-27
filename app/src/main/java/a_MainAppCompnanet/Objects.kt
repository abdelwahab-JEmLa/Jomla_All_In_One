package a_RoomDB

import a_MainAppCompnents.ArticlesBasesStatsModelDao
import a_MainAppCompnents.CategoriesModelDao
import a_MainAppCompnents.ColorsArticlesDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ArticlesBasesStats::class,
        CategoriesModel::class,
        ColorsArticles::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class Objects : RoomDatabase() {
    abstract fun articlesBasesStatsModelDao(): ArticlesBasesStatsModelDao
    abstract fun categoriesModelDao(): CategoriesModelDao
    abstract fun colorsArticlesDao(): ColorsArticlesDao


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
