package Z_CodePartageEntreApps.Apps.Manager.Module.B.Room

import Z_CodePartageEntreApps.Model.Z.Archive.AppSettingsSaverModel
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.DevicesTypeManager
import Z_CodePartageEntreApps.Model.Z.Archive.DiviseurDeDisplayProductForEachClient
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface DiviseurDeDisplayProductForEachClientDao {
    @Query("SELECT * FROM DiviseurDeDisplayProductForEachClient ")
    suspend fun getAll(): MutableList<DiviseurDeDisplayProductForEachClient>

    @Transaction
    suspend fun transaction(block: suspend DiviseurDeDisplayProductForEachClientDao.() -> Unit) {
        block()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: DiviseurDeDisplayProductForEachClient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(item: List<DiviseurDeDisplayProductForEachClient>)

    @Query("DELETE FROM DiviseurDeDisplayProductForEachClient")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(item: List<DiviseurDeDisplayProductForEachClient>)
}

@Dao
interface ColorsArticlesDao {
    @Query("SELECT * FROM ColorsArticlesTabelle ORDER BY classementColore")
    suspend fun getAllOrdred(): MutableList<ColorsArticlesTabelle>

    @Transaction
    suspend fun transaction(block: suspend ColorsArticlesDao.() -> Unit) {
        block()
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: ColorsArticlesTabelle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(colorsArticleTabelles: List<ColorsArticlesTabelle>)

    @Query("DELETE FROM ColorsArticlesTabelle")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(colorsArticleTabelles: List<ColorsArticlesTabelle>)
}

@Dao
interface SoldArticlesTabelleDao{
    @Query("SELECT * FROM SoldArticlesTabelle ORDER BY vid")
    suspend fun getAll(): MutableList<SoldArticlesTabelle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(soldArticlesTabelle: SoldArticlesTabelle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(soldArticles: List<SoldArticlesTabelle>)

    @Delete
    suspend fun delete(item: SoldArticlesTabelle)

    @Query("DELETE FROM SoldArticlesTabelle")
    suspend fun deleteAll()

    @Update
    suspend fun updateAll(soldArticles: List<SoldArticlesTabelle>)

    @Transaction
    suspend fun transaction(block: suspend SoldArticlesTabelleDao.() -> Unit) {
        block()
    }
}

@Dao
interface AppSettingsSaverModelDao{
    @Query("SELECT * FROM AppSettingsSaverModel ORDER BY id")
    suspend fun getAll(): MutableList<AppSettingsSaverModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AppSettingsSaverModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AppSettingsSaverModel>)
    @Query("DELETE FROM AppSettingsSaverModel")
    suspend fun deleteAll()

}
@Dao
interface DevicesTypeManagerDao{
    @Query("SELECT * FROM DevicesTypeManager ORDER BY id")
    suspend fun getAll(): MutableList<DevicesTypeManager>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DevicesTypeManager)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<DevicesTypeManager>)
    @Query("SELECT MAX(id) FROM DevicesTypeManager")
    suspend fun getMaxId(): Long?

    @Query("DELETE FROM DevicesTypeManager")
    suspend fun deleteAll()

}

