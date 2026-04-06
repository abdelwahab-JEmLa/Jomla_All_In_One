package EntreApps.Shared.Modules.Base

import EntreApps.Shared.Models.AppType
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M14VentPeriode
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import EntreApps.Shared.Modules.Base.SQL.Dao_M03CouleurProduitInfos
import EntreApps.Shared.Modules.Base.SQL.Dao_M16CategorieProduit
import EntreApps.Shared.Modules.Base.SQL.Dao_M1Produit
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients.SQL.E1SecteurDeClientsDao
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.PolygonGeoLimite
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Repository.PolygonGeoLimiteDao
import V.DiviseParSections.App.Shared.Repository.C_TypeTarificationInfos
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M2Client
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.M15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriode
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppSettingsSaverModelDao
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.ArticleDao
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.ColorsArticlesDao
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.DevicesTypeManagerDao
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.DiviseurDeDisplayProductForEachClientDao
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.SoldArticlesTabelleDao
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.D_EtateMessageVocale.Repository.Extensions.H.Dao.M17MessageVocaleDao
import Z_CodePartageEntreApps.DataBase.M18CentralParametresOfAllAppsDao
import Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.Dao13TarificationInfos
import Z_CodePartageEntreApps.DataBase.Main.Main.D_AchatOperationDataBaseProtoJuin17.Base.C.SQL.Dao_M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase02.Factory.Dao_M2Client
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase11.Factory.Dao11AchatOperation
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase14VentPeriode.Factory.Dao14VentPeriode
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.Dao15Grossist
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase19.Factory.Dao19Etudiant
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase20.Factory.Dao20ObsarvationEtudion
import Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.SQL.Dao_M8BonVent
import Z_CodePartageEntreApps.DataBase.Main.Main.Z.Base.SQL.Dao_M9AppCompt
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
import Z_CodePartageEntreApps.Proto.Par.Type.Repository.A_ProduitInfosDao
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation.Z.Dao._1_1_CouleurAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation.Dao._1_2_ProduitAcheteOperationDao
import Z_CodePartageEntreApps.Repository._1_2_ProduitAcheteOperation._1_2_ProduitAcheteOperation
import Z_CodePartageEntreApps.Repository._1_4_PeriodeVent.MVentPeriodeDao
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
        M3CouleurProduitInfos::class,

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
        MVentPeriode::class,

        _2_1_ProduitsDataBase::class,
        _4_CouleurOperationCommand::class,

        E1SecteurDeClients::class,
        PolygonGeoLimite::class,

        A_ProduitInfos::class,
        C_TypeTarificationInfos::class,
        M13TarificationInfos::class,

        M2Client::class,

        M10OperationVentCouleur::class,
        M09AppCompt::class,
        M8BonVent::class,
        M14VentPeriode::class,
        M15Grossist::class,
        M11AchatOperation::class,

        M17MessageVocale::class,
        M00CentralParametresOfAllApps::class,
        M19Etudiant::class,
        M20ObsarvationEtudion::class,

        M01Produit::class,
        M16CategorieProduit::class,
    ],
    version = 4, // Bumped from 3 → 4 to register the new AppTypeConverter
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListLongConverter::class, AppTypeConverter::class)
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
    abstract fun _2_1_ProduitsDataBaseDao(): _2_1_ProduitsDataBaseDao
    abstract fun _4_CouleurOperationCommandDao(): _4_CouleurOperationCommandDao

    abstract fun e1SecteurDeClientsDao(): E1SecteurDeClientsDao
    abstract fun polygonGeoLimiteDaoDao(): PolygonGeoLimiteDao

    abstract fun a_ProduitInfosDao(): A_ProduitInfosDao

    // Proto j3
    abstract fun MVentPeriodeDao(): MVentPeriodeDao
    abstract fun Dao15Grossist(): Dao15Grossist
    abstract fun Dao11AchatOperation(): Dao11AchatOperation
    abstract fun M17MessageVocaleDao(): M17MessageVocaleDao
    abstract fun M18CentralParametresOfAllAppsDao(): M18CentralParametresOfAllAppsDao

    abstract fun Dao19Etudiant(): Dao19Etudiant
    abstract fun Dao20ObsarvationEtudion(): Dao20ObsarvationEtudion

    abstract fun dao_M1Produit(): Dao_M1Produit
    abstract fun dao_16CategorieProduit(): Dao_M16CategorieProduit
    abstract fun dao_M03CouleurProduitInfos(): Dao_M03CouleurProduitInfos
    abstract fun dao_M9AppCompt(): Dao_M9AppCompt
    abstract fun dao_M8BonVent(): Dao_M8BonVent
    abstract fun dao_M13TarificationInfos(): Dao13TarificationInfos
    abstract fun dao_M14VentPeriode(): Dao14VentPeriode
    abstract fun dao_M10OperationVentCouleur(): Dao_M10OperationVentCouleur
    abstract fun dao_M2Client(): Dao_M2Client

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
                    .fallbackToDestructiveMigration() // Safe for dev — replace with Migration() in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Type Converters
// -------------------------------------------------------------------------------------------------

/**
 * Converts [AppType] enum to/from String for Room storage.
 * Uses name-based lookup so any future enum values are handled automatically
 * without requiring a database version bump.
 */
class AppTypeConverter {
    @TypeConverter
    fun fromAppType(value: AppType?): String? = value?.name

    @TypeConverter
    fun toAppType(value: String?): AppType? =
        value?.let { name -> AppType.entries.firstOrNull { it.name == name } }
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
