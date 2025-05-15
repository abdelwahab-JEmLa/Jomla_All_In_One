package com.example.clientjetpack.ID3.Test.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Dao.A_ProduitInfosDao
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Dao.B_ClientInfosDao
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Dao.C_TypeTarificationInfosDao
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Dao.D_TarificationInfosDao
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.A_ProduitInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.B_ClientInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.C_TypeTarificationInfos
import com.example.clientjetpack.ID3.Test.DataBase.Repo.Models.D_TarificationInfos
@Database(
    entities = [
        A_ProduitInfos::class,
        B_ClientInfos::class,
        C_TypeTarificationInfos::class,
        D_TarificationInfos::class
    ],
    version = 3,
    exportSchema = false
)
abstract class TestAppDatabase : RoomDatabase() {
    abstract fun a_ProduitInfosDao(): A_ProduitInfosDao
    abstract fun b_ClientInfosDao(): B_ClientInfosDao
    abstract fun c_TypeTarificationInfosDao(): C_TypeTarificationInfosDao
    abstract fun dTarificationInfosDao(): D_TarificationInfosDao

    companion object {
        @Volatile
        private var INSTANCE: TestAppDatabase? = null

        fun getTestDatabase(context: Context): TestAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TestAppDatabase::class.java,  // Fixed: Use TestAppDatabase instead of AppDatabase
                    "test_database"               // Changed: Use a different name for test database
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
