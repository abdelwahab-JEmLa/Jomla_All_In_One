package Z_CodePartageEntreApps.Apps.Manager.Module.B.Room


import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.SQL.E1SecteurDeClientsDao
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.PolygonGeoLimite
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Repository.PolygonGeoLimiteDao
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Repository.EtateMessageVocaleDao
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Repository.MessageVocaleDao
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.EtateMessageVocale
import V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.ViewModel.MessageVocale
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.Z.Daos.A_ProduitInfosDao
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository.Z.Daos.D_TarificationInfosDao
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.C3_BonAchat.C3_BonAchate
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository._1_2_ProduitAcheteOperation
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.B_ClientInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.B_ClientInfosDao
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.Models.C_TypeTarificationInfosDao
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
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation.Z.Dao._1_1_CouleurAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation.Dao._1_2_ProduitAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL._1_3_TransactionCommercialDao
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentDao
import Z_CodePartageEntreApps.Repository._1_5_Vendeur.Extension.DataBase._1_5_VendeurDao
import Z_CodePartageEntreApps.Repository._1_5_Vendeur._1_5_Vendeur
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase.Extension.DataBase._2_1_ProduitsDataBaseDao
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase.Extension.DataBase._3_ClientsDataBaseDao
import Z_CodePartageEntreApps.Repository._3_ClientsDataBase._3_ClientsDataBase
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand.Extension.DataBase._4_CouleurOperationCommandDao
import Z_CodePartageEntreApps.Repository._4_2_._4_CouleurOperationCommand._4_CouleurOperationCommand
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
        C3_BonAchate::class,
        _1_4_PeriodeVent::class,
        _1_5_Vendeur::class,

        _2_1_ProduitsDataBase::class,
        _3_ClientsDataBase::class,
        _4_CouleurOperationCommand::class,

        E1SecteurDeClients::class,
        PolygonGeoLimite::class,

        MessageVocale::class,
        EtateMessageVocale::class,

        A_ProduitInfos::class,
        B_ClientInfos::class,
        C_TypeTarificationInfos::class,
        D_TarificationInfos::class
    ],
    version = 3, // Increment version number since we're adding new entities
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
    abstract fun _1_3_TransactionCommercialDao(): _1_3_TransactionCommercialDao
    abstract fun _1_4_PeriodeVentDao(): _1_4_PeriodeVentDao
    abstract fun _1_5_VendeurDao(): _1_5_VendeurDao

    abstract fun _2_1_ProduitsDataBaseDao(): _2_1_ProduitsDataBaseDao
    abstract fun _3_ClientsDataBaseDao(): _3_ClientsDataBaseDao
    abstract fun _4_CouleurOperationCommandDao(): _4_CouleurOperationCommandDao

    abstract fun e1SecteurDeClientsDao(): E1SecteurDeClientsDao
    abstract fun polygonGeoLimiteDaoDao(): PolygonGeoLimiteDao

    abstract fun messageVocaleDao(): MessageVocaleDao
    abstract fun etateMessageVocaleDao(): EtateMessageVocaleDao

    abstract fun a_ProduitInfosDao(): A_ProduitInfosDao
    abstract fun b_ClientInfosDao(): B_ClientInfosDao
    abstract fun c_TypeTarificationInfosDao(): C_TypeTarificationInfosDao
    abstract fun dTarificationInfosDao(): D_TarificationInfosDao


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
