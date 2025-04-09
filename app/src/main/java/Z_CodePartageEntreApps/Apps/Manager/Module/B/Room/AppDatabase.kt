package Z_CodePartageEntreApps.Apps.Manager.Module.B.Room

import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.Extension.A_ProduitDao
import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase
import Z_CodePartageEntreApps.Model.B_ClientDataBase.Repository.Extension.B_ClientDataBaseDao
import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Model.I_CategorieProduits.Z.Repository.Extension.I_CategorieProduitsDao
import Z_CodePartageEntreApps.Model.Z.Archive.AppSettingsSaverModel
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Model.Z.Archive.BaseDonne
import Z_CodePartageEntreApps.Model.Z.Archive.CategoriesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.DevicesTypeManager
import Z_CodePartageEntreApps.Model.Z.Archive.DiviseurDeDisplayProductForEachClient
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Model._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Model._1_3_BonAchat
import Z_CodePartageEntreApps.Model._1_4_PeriodeVent
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation.Z.Dao._1_1_CouleurAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation.Dao._1_2_ProduitAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_3_BonAchat._1_3_BonAchatDao
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentDao
import Z_CodePartageEntreApps.Repository._1_5_Vendeur.Extension.DataBase._1_5_VendeurDao
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase.Extension.DataBase._2_2_ClientsDataBaseDao
import Z_CodePartageEntreApps.Model._2_2_ClientsDataBase
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
        BaseDonne::class,
         B_ClientDataBase::class,

        I_CategorieProduits::class,
        A_Produit::class,
        _1_1_CouleurAcheteOperation::class,
        _1_2_ProduitAcheteOperation::class,
        _1_3_BonAchat::class,
        _1_4_PeriodeVent::class,
        _1_5_Vendeur::class,
        _2_2_ClientsDataBase::class,
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
    abstract fun articleDao(): ArticleDao


    abstract fun b_ClientDataBaseDao(): B_ClientDataBaseDao
    abstract fun I_CategorieProduitsDao(): I_CategorieProduitsDao
    abstract fun a_ProduiteDao(): A_ProduitDao

    abstract fun _1_1_CouleurAcheteOperationDao(): _1_1_CouleurAcheteOperationDao
    abstract fun _1_2_ProduitAcheteOperationDao(): _1_2_ProduitAcheteOperationDao
    abstract fun _1_3_BonAchatDao(): _1_3_BonAchatDao
    abstract fun _1_4_PeriodeVentDao(): _1_4_PeriodeVentDao
    abstract fun _1_5_VendeurDao(): _1_5_VendeurDao

    abstract fun _2_2_ClientsDataBaseDao(): _2_2_ClientsDataBaseDao

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

