package a_RoomDB

import a_MainAppCompnents.AppSettingsSaverModelDao
import a_MainAppCompnents.ArticlesBasesStatsModelDao
import a_MainAppCompnents.CategoriesModelDao
import a_MainAppCompnents.ClientsModelDao
import a_MainAppCompnents.ColorsArticlesDao
import a_MainAppCompnents.SoldArticlesTabelleDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Database(
    entities = [
        ArticlesBasesStatsTabelle::class,
        CategoriesTabelle::class,
        ColorsArticlesTabelle::class,
        SoldArticlesTabelle::class,
        ClientsModel::class,
        AppSettingsSaverModel::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    // All DAOs
    abstract fun articlesBasesStatsModelDao(): ArticlesBasesStatsModelDao
    abstract fun categoriesModelDao(): CategoriesModelDao
    abstract fun colorsArticlesDao(): ColorsArticlesDao
    abstract fun soldArticlesTabelleDao(): SoldArticlesTabelleDao
    abstract fun clientsModelDao(): ClientsModelDao
    abstract fun appSettingsSaverModelDao(): AppSettingsSaverModelDao

    // DatabaseModule.kt
    object DatabaseModule {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// First, let's properly set up the DateConverter
class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}
