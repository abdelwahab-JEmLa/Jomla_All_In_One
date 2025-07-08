package Z_CodePartageEntreApps.DataBase.Main.Main.DB13TarificationInfos.Factory.Proto.SQL

import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.A_ProduitInfos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

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
                M13TarificationInfos::class -> {
                    emptyList()
                   // database.Dao13TarificationInfos().insertAllReturnIDs(data as A_MainListView<M13TarificationInfos>)
                }
                else -> throw IllegalArgumentException("Unsupported data type: ${DataBase::class.simpleName}")
            }

            onProgressUpdate(0.7f)

            val resultMap = mutableMapOf<Long, DataBase>()
            ids.forEachIndexed { index, generatedId ->
                if (index < data.size) {
                    val itemWithGeneratedId = when (val originalItem = data[index]) {
                        is A_ProduitInfos -> originalItem.copy(id = generatedId) as DataBase
                        is M13TarificationInfos -> originalItem.copy(id = generatedId) as DataBase
                        else -> throw IllegalArgumentException("Unsupported item type")
                    }

                    val itemWithDefaults = when (itemWithGeneratedId) {
                        is A_ProduitInfos -> itemWithGeneratedId.withProperKeyFireBase() as DataBase
                        is M13TarificationInfos -> itemWithGeneratedId.withProperDefaults() as DataBase
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
                M13TarificationInfos::class -> {
                    database.Dao13TarificationInfos().getCount() > 0
                }
                else -> false
            }
        }

    suspend fun checkDataBaseIsEmpty(
        onCheckIsTrue: (G_RoomOperationsHandler) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.2f)

            val count = database.Dao13TarificationInfos().getCount()

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
        data: List<M13TarificationInfos>,
        onAddSuccess: (Map<Long, M13TarificationInfos>) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.1f)

            if (data.isEmpty()) {
                onProgressUpdate(1f)
                onAddSuccess(emptyMap())
                return@withContext true
            }

            onProgressUpdate(0.3f)

            val ids = emptyList<Long>()

            onProgressUpdate(0.7f)

            val resultMap = mutableMapOf<Long, M13TarificationInfos>()
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

    // FIXED: Changed from insert to proper update_showDetailsExpanded logic
    suspend fun update(data: Any, dataType: KClass<*>): Pair<Long, Any> = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.2f)

            val (updatedId, updatedData) = when (dataType) {
                A_ProduitInfos::class -> {
                    val produitData = data as A_ProduitInfos
                    val dataWithDefaults = produitData.withProperKeyFireBase()

                    // FIXED: Check if item exists first, then update_showDetailsExpanded or insert accordingly
                    val existingId = dataWithDefaults.id
                    val finalId = if (existingId > 0 && database.a_ProduitInfosDao().exists(existingId)) {
                        // Update existing record
                        database.a_ProduitInfosDao().update(dataWithDefaults)
                        existingId
                    } else {
                        // Insert new record
                        database.a_ProduitInfosDao().insertReturnID(dataWithDefaults)
                    }

                    val finalData = dataWithDefaults.copy(id = finalId)
                    Pair(finalId, finalData)
                }
                M13TarificationInfos::class -> {
                    val tarificationData = data as M13TarificationInfos
                    val dataWithDefaults = tarificationData.withProperDefaults()

                    // FIXED: Check if item exists first, then update_showDetailsExpanded or insert accordingly
                    val existingId = dataWithDefaults.id
                    val finalId = if (existingId > 0 && database.Dao13TarificationInfos().exists(existingId)) {
                        // Update existing record
                        database.Dao13TarificationInfos().update(dataWithDefaults)
                        existingId
                    } else {
                        // Insert new record
                        database.Dao13TarificationInfos().insert(dataWithDefaults)
                    }

                    val finalData = dataWithDefaults.copy(id = finalId)
                    Pair(finalId, finalData)
                }
                else -> throw IllegalArgumentException("Unsupported data type: ${dataType.simpleName}")
            }

            onProgressUpdate(1f)
            Pair(updatedId, updatedData)

        } catch (e: Exception) {
            onProgressUpdate(0f)
            throw e
        }
    }

    // FIXED: Added proper upsert functionality
    suspend fun upsert(data: Any, dataType: KClass<*>): Pair<Long, Any> = withContext(Dispatchers.IO) {
        try {
            onProgressUpdate(0.2f)

            val (upsertedId, upsertedData) = when (dataType) {
                A_ProduitInfos::class -> {
                    val produitData = data as A_ProduitInfos
                    val dataWithDefaults = produitData.withProperKeyFireBase()

                    // Use upsert (insert or replace)
                    val id = database.a_ProduitInfosDao().upsert(dataWithDefaults)
                    val finalData = dataWithDefaults.copy(id = id)
                    Pair(id, finalData)
                }
                M13TarificationInfos::class -> {
                    val tarificationData = data as M13TarificationInfos
                    val dataWithDefaults = tarificationData.withProperDefaults()

                    // Use upsert (insert or replace)
                    val id = database.Dao13TarificationInfos().upsert(dataWithDefaults)
                    val finalData = dataWithDefaults.copy(id = id)
                    Pair(id, finalData)
                }
                else -> throw IllegalArgumentException("Unsupported data type: ${dataType.simpleName}")
            }

            onProgressUpdate(1f)
            Pair(upsertedId, upsertedData)

        } catch (e: Exception) {
            onProgressUpdate(0f)
            throw e
        }
    }
}
