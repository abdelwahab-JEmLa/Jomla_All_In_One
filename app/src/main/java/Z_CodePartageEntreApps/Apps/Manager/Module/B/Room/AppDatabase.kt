package Z_CodePartageEntreApps.Apps.Manager.Module.B.Room

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_4_PeriodeVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.SQL.E1SecteurDeClientsDao
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.PolygonGeoLimite
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Repository.PolygonGeoLimiteDao
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.GTransactionVent
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.CategoriesTabelle
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.Z_AppCompt
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B1CouleurOuGoutProduitDataBase
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.B1CouleurOuGoutProduitDataBaseDao
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.FCouleurVentOperation
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.B_ClientInfosProtoJuin3
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.B_ClientInfosProtoJuin3.Repository.SQL.B_ClientInfosProtoJuin3Dao
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.A.Main.D_EtateMessageVocale
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions.H.Dao.D_EtateMessageVocaleDao
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.Z_App.Base.Extension.DataBase._1_5_VendeurDao
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.Z_App.Base._1_5_Vendeur
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.D_AchatOperationDao
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Z_AppComptDao
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.Extensions.H.Dao.ArticlesBasesStatsModelDao
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.C_CategorieProduitInfos.Repository.Extensions.H.Dao.CategoriesModelDao
import Z_CodePartageEntreApps.Model.A_Produit.A_Produit
import Z_CodePartageEntreApps.Model.A_Produit.Z.Repository.Extension.A_ProduitDao
import Z_CodePartageEntreApps.Model.A_ProduitInfos
import Z_CodePartageEntreApps.Model.I_CategorieProduits.I_CategorieProduits
import Z_CodePartageEntreApps.Model.I_CategorieProduits.Z.Repository.Extension.I_CategorieProduitsDao
import Z_CodePartageEntreApps.Model.Z.Archive.AppSettingsSaverModel
import Z_CodePartageEntreApps.Model.Z.Archive.BaseDonne
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.DevicesTypeManager
import Z_CodePartageEntreApps.Model.Z.Archive.DiviseurDeDisplayProductForEachClient
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Proto.Par.Type.Models.C_TypeTarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Repository.A_ProduitInfosDao
import Z_CodePartageEntreApps.Proto.Par.Type.Repository.C_TypeTarificationInfosDao
import Z_CodePartageEntreApps.Proto.Par.Type.Repository.D_TarificationInfosDao
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation.Z.Dao._1_1_CouleurAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation.Dao._1_2_ProduitAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.SQL._1_3_TransactionCommercialDao
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent._1_4_PeriodeVentDao
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase.Extension.DataBase._2_1_ProduitsDataBaseDao
import Z_CodePartageEntreApps.Repository._2_1_ProduitsDataBase._2_1_ProduitsDataBase
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
        //Sorted ID
        B1CouleurOuGoutProduitDataBase::class,

        ColorsArticlesTabelle::class,
        SoldArticlesTabelle::class,
        AppSettingsSaverModel::class,
        DevicesTypeManager::class,
        DiviseurDeDisplayProductForEachClient::class,
        BaseDonne::class,

        I_CategorieProduits::class,
        A_Produit::class,
        _1_1_CouleurAcheteOperation::class,
        _1_2_ProduitAcheteOperation::class,
        GTransactionVent::class,
        _1_4_PeriodeVent::class,
        _1_5_Vendeur::class,

        _2_1_ProduitsDataBase::class,
        _4_CouleurOperationCommand::class,

        E1SecteurDeClients::class,
        PolygonGeoLimite::class,

        D_EtateMessageVocale::class,

        A_ProduitInfos::class,
        C_TypeTarificationInfos::class,
        D_TarificationInfos::class   ,

        ArticlesBasesStatsTable::class,
        CategoriesTabelle::class,

        B_ClientInfosProtoJuin3::class,

        FCouleurVentOperation::class,
        Z_AppCompt::class,


    ],
    version = 3, // Increment version number since we're adding new entities
    exportSchema = false
)

@TypeConverters(DateConverter::class, ListLongConverter::class)
abstract class AppDatabase : RoomDatabase() {

    // All DAOs

    abstract fun colorsArticlesDao(): ColorsArticlesDao
    abstract fun soldArticlesModelDao(): SoldArticlesTabelleDao
    abstract fun appSettingsSaverModelDao(): AppSettingsSaverModelDao
    abstract fun devicesTypeManagerDao(): DevicesTypeManagerDao
    abstract fun diviseurDeDisplayProductForEachClientDao(): DiviseurDeDisplayProductForEachClientDao
    abstract fun articleDao(): ArticleDao

    abstract fun I_CategorieProduitsDao(): I_CategorieProduitsDao
    abstract fun a_ProduiteDao(): A_ProduitDao

    abstract fun _1_1_CouleurAcheteOperationDao(): _1_1_CouleurAcheteOperationDao
    abstract fun _1_2_ProduitAcheteOperationDao(): _1_2_ProduitAcheteOperationDao
    abstract fun _1_3_TransactionCommercialDao(): _1_3_TransactionCommercialDao
    abstract fun _1_4_PeriodeVentDao(): _1_4_PeriodeVentDao
    abstract fun _1_5_VendeurDao(): _1_5_VendeurDao

    abstract fun _2_1_ProduitsDataBaseDao(): _2_1_ProduitsDataBaseDao
    abstract fun _4_CouleurOperationCommandDao(): _4_CouleurOperationCommandDao

    abstract fun e1SecteurDeClientsDao(): E1SecteurDeClientsDao
    abstract fun polygonGeoLimiteDaoDao(): PolygonGeoLimiteDao


    abstract fun a_ProduitInfosDao(): A_ProduitInfosDao
    abstract fun c_TypeTarificationInfosDao(): C_TypeTarificationInfosDao
    abstract fun dTarificationInfosDao(): D_TarificationInfosDao

    //Proto j3
    abstract fun ArticlesBasesStatsModelDao(): ArticlesBasesStatsModelDao
    abstract fun categoriesModelDao(): CategoriesModelDao
    abstract fun D_EtateMessageVocaleDao(): D_EtateMessageVocaleDao

    abstract fun B_ClientInfosProtoJuin3Dao(): B_ClientInfosProtoJuin3Dao

    abstract fun D_AchatOperationDao(): D_AchatOperationDao
    abstract fun Z_AppComptDao(): Z_AppComptDao
    abstract fun B1CouleurOuGoutProduitDataBaseDao(): B1CouleurOuGoutProduitDataBaseDao

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
