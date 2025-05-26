package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class G_RoomOperationsHandler(
    val database: AppDatabase,
    val onProgressUpdate: (Float) -> Unit = { }
) {
    suspend inline fun <reified DataBase : Any> insertAllAndReturnListIdToDataInline(
        data: List<DataBase>,
    ): Map<Long, DataBase> = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            if (data.isEmpty()) {
                onProgressUpdate(1f)
                return@withContext emptyMap()
            }

            onProgressUpdate(0.3f)

            val ids = when (DataBase::class) {
                A_ProduitInfos::class -> {
                    @Suppress("UNCHECKED_CAST")
                    database.a_ProduitInfosDao().insertAllReturnIDs(data as List<A_ProduitInfos>)
                }
                D_TarificationInfos::class -> {
                    @Suppress("UNCHECKED_CAST")
                    database.dTarificationInfosDao().insertAllReturnIDs(data as List<D_TarificationInfos>)
                }
                else -> throw IllegalArgumentException("Unsupported data type: ${DataBase::class.simpleName}")
            }

            onProgressUpdate(0.7f)

            val resultMap = mutableMapOf<Long, DataBase>()
            ids.forEachIndexed { index, generatedId ->
                if (index < data.size) {
                    val itemWithGeneratedId = when (val originalItem = data[index]) {
                        is A_ProduitInfos -> originalItem.copy(idArticle = generatedId) as DataBase
                        is D_TarificationInfos -> originalItem.copy(id = generatedId) as DataBase
                        else -> throw IllegalArgumentException("Unsupported item type")
                    }

                    val itemWithDefaults = when (itemWithGeneratedId) {
                        is A_ProduitInfos -> itemWithGeneratedId.withProperKeyFireBase() as DataBase  // NEW
                        is D_TarificationInfos -> itemWithGeneratedId.withProperDefaults() as DataBase  // NEW
                        else -> itemWithGeneratedId
                    }

                    resultMap[generatedId] = itemWithDefaults
                }
            }

            onProgressUpdate(1f)
            resultMap

        } catch (e: Exception) {
            onProgressUpdate(0f)
            emptyMap()
        }
    }

    suspend inline fun <reified DataBase : Any> inlineCheckDataBaseIsNotEmpty(): Boolean =
        withContext(Dispatchers.IO) {
            when (DataBase::class) {
                A_ProduitInfos::class -> {
                    database.a_ProduitInfosDao().getCount() > 0
                }
                D_TarificationInfos::class -> {
                    database.dTarificationInfosDao().getCount() > 0
                }
                else -> false
            }
        }

    suspend fun checkDataBaseIsEmpty(
        onCheckIsTrue: (G_RoomOperationsHandler) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.2f)

            val count = database.dTarificationInfosDao().getCount()

            onProgressUpdate(0.8f)

            if (count == 0) {
                onProgressUpdate(1f)
                onCheckIsTrue(this@G_RoomOperationsHandler)
            } else {
                onProgressUpdate(1f)
            }
        } catch (e: Exception) {
            onProgressUpdate(0f)
        }
    }

    suspend fun insertAllAndReturnListIdToData(
        data: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            if (data.isEmpty()) {
                onProgressUpdate(1f)
                onAddSuccess(emptyMap())
                return@withContext true
            }

            onProgressUpdate(0.3f)

            val ids = database.dTarificationInfosDao()
                .insertAllReturnIDs(data)

            onProgressUpdate(0.7f)

            val resultMap = mutableMapOf<Long, D_TarificationInfos>()
            ids.forEachIndexed { index, generatedId ->
                if (index < data.size) {
                    val originalItem = data[index]
                    val itemWithGeneratedId = originalItem.copy(id = generatedId)
                    val itemWithDefaults = itemWithGeneratedId.withProperDefaults()
                    resultMap[generatedId] = itemWithDefaults
                }
            }

            onProgressUpdate(1f)
            onAddSuccess(resultMap)
            true
        } catch (e: Exception) {
            onProgressUpdate(0f)
            onAddSuccess(emptyMap())
            false
        }
    }

    suspend fun getAncienProditDB() : List<ArticlesBasesStatsTable> = withContext(Dispatchers.IO) {
        return@withContext database.articlesBasesStatsModelDao().getAll()
    }
}
