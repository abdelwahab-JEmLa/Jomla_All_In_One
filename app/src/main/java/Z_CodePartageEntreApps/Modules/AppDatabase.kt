package Z_CodePartageEntreApps.Modules

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.Extension.A_ProduitDao
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.Extension.B_ClientDataBaseDao
import Z_CodePartageEntreApps.Model.I_CategorieProduits.A.Repository.Extension.I_CategorieProduitsDao
import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Model.Z.Archive.AppSettingsSaverModel
import Z_CodePartageEntreApps.Model.Z.Archive.CategoriesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.DevicesTypeManager
import Z_CodePartageEntreApps.Model.Z.Archive.DiviseurDeDisplayProductForEachClient
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.AppSettingsSaverModelDao
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.ArticlesBasesStatsModelDao
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.CategoriesModelDao
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.ColorsArticlesDao
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.DevicesTypeManagerDao
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.DiviseurDeDisplayProductForEachClientDao
import Z_MasterOfApps.Z.Android.A_MainActivityApp.Start.Modules.SoldArticlesTabelleDao
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.SoldArticlesTabelle
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

@Database(
    entities = [
        ArticlesBasesStatsTable::class,
        CategoriesTabelle::class,
        ColorsArticlesTabelle::class,
        SoldArticlesTabelle::class,
        AppSettingsSaverModel::class,
        DevicesTypeManager::class,
        DiviseurDeDisplayProductForEachClient::class,

        B_ClientDataBase::class,
        I_CategorieProduits::class,
        A_Produit::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListLongConverter::class)
abstract class AppDatabase : RoomDatabase() {
    // All DAOs
    abstract fun articlesBasesStatsModelDao(): ArticlesBasesStatsModelDao
    abstract fun categoriesModelDao(): CategoriesModelDao
    abstract fun colorsArticlesDao(): ColorsArticlesDao
    abstract fun soldArticlesModelDao(): SoldArticlesTabelleDao
    abstract fun appSettingsSaverModelDao(): AppSettingsSaverModelDao
    abstract fun devicesTypeManagerDao(): DevicesTypeManagerDao
    abstract fun diviseurDeDisplayProductForEachClientDao(): DiviseurDeDisplayProductForEachClientDao

    abstract fun b_ClientDataBaseDao(): B_ClientDataBaseDao
    abstract fun i_CategorieProduitsDao(): I_CategorieProduitsDao
    abstract fun a_ProduiteDao(): A_ProduitDao

    object DatabaseModule {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class ListLongConverter {
    @TypeConverter
    fun fromListLong(value: List<Long>): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toListLong(value: String): List<Long> {
        val gson = Gson()
        val listType = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(value, listType)
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
