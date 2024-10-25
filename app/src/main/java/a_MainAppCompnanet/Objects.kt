package a_RoomDB

import a_MainAppCompnents.ArticlesBasesStatsModelDao
import a_MainAppCompnents.CategoriesModelDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ArticlesBasesStatsModel::class,
        CategoriesModel::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class Objects : RoomDatabase() {
    abstract fun articlesBasesStatsModelDao(): ArticlesBasesStatsModelDao
    abstract fun categoriesModelDao(): CategoriesModelDao

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
