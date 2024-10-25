package a_RoomDB

import a_MainAppCompnents.CategoriesDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ArticlesBasesStats::class,
        Categories::class,
        ArticlesSelled::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class Objects : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao

    companion object {
        @Volatile
        private var INSTANCE: Objects? = null

        fun getInstance(context: Context): Objects {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Objects::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
