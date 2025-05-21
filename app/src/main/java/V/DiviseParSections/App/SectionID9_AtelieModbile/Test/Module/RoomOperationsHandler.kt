package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Module

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.DataBasesInfosSql
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomOperationsHandler(private val database: AppDatabase) {
    suspend fun upsertAllAndReturnListIdToData(
        data: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d("RoomOperationsHandler", "=== STARTING UPSERT OPERATION ===")
            Log.d("RoomOperationsHandler", "Incoming ${data.size} tarifications")

            val resultMap = mutableMapOf<Long, D_TarificationInfos>()

            data.forEachIndexed { index, tarif ->
                Log.d("RoomOperationsHandler", "=== Processing item $index ===")
                Log.d("RoomOperationsHandler", "Original tarif: id=${tarif.id}, nom='${tarif.nom}'")

                try {
                    val finalId = when {
                        tarif.id == 0L -> {
                            // New item - let Room auto-generate ID
                            Log.d("RoomOperationsHandler", "Inserting new item with auto-generated ID...")
                            val newId = database.dTarificationInfosDao().insert(tarif)
                            Log.d("RoomOperationsHandler", "Auto-generated ID: $newId")
                            newId
                        }
                        else -> {
                            // Check if item exists
                            val existsCount = database.dTarificationInfosDao().existsById(tarif.id)
                            if (existsCount > 0) {
                                Log.d("RoomOperationsHandler", "Updating existing item with ID: ${tarif.id}")
                                database.dTarificationInfosDao().update(tarif)
                                tarif.id
                            } else {
                                Log.d("RoomOperationsHandler", "Force inserting item with specific ID: ${tarif.id}")
                                database.dTarificationInfosDao().forceInsert(tarif)
                                tarif.id
                            }
                        }
                    }

                    if (finalId > 0L) {
                        val updatedTariff = tarif.copy(id = finalId)
                        resultMap[finalId] = updatedTariff
                        Log.d("RoomOperationsHandler", "✓ Added to result map: ID=$finalId, nom='${updatedTariff.nom}'")
                    } else {
                        Log.w("RoomOperationsHandler", "✗ Failed to get valid ID for tarif: ${tarif.nom}")
                    }

                } catch (itemException: Exception) {
                    Log.e("RoomOperationsHandler", "✗ Error processing tarif '${tarif.nom}': ${itemException.message}")
                    itemException.printStackTrace()
                }
            }

            // Final verification
            val allItemsInDb = database.dTarificationInfosDao().getAllTarificationsSync()
            Log.d("RoomOperationsHandler", "=== FINAL VERIFICATION ===")
            Log.d("RoomOperationsHandler", "Total items in DB: ${allItemsInDb.size}")
            Log.d("RoomOperationsHandler", "Result map size: ${resultMap.size}")
            Log.d("RoomOperationsHandler", "Result map keys: ${resultMap.keys.toList()}")

            onAddSuccess(resultMap)
            true
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in upsertAllAndReturnListIdToData: ${e.message}")
            e.printStackTrace()
            onAddSuccess(emptyMap())
            false
        }
    }

    suspend fun insertAll(data: DataBasesInfosSql): Boolean = withContext(Dispatchers.IO) {
        try {
            database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
            true
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in insertAll: ${e.message}")
            false
        }
    }

    suspend fun deleteAll(): Boolean = withContext(Dispatchers.IO) {
        try {
            database.dTarificationInfosDao().deleteAll()
            true
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in deleteAll: ${e.message}")
            false
        }
    }

    suspend fun updateData(data: DataBasesInfosSql): Boolean = withContext(Dispatchers.IO) {
        try {
            if (data.d_TarificationInfos.isNotEmpty()) {
                database.dTarificationInfosDao().deleteAll()
                database.dTarificationInfosDao().insertAll(data.d_TarificationInfos)
            }
            true
        } catch (e: Exception) {

            Log.e("RoomOperationsHandler", "Error in updateData: ${e.message}")
            false
        }
    }

    suspend fun getAllData(): DataBasesInfosSql = withContext(Dispatchers.IO) {
        try {
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()
            DataBasesInfosSql(d_TarificationInfos = tarifications.toMutableList())
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in getAllData: ${e.message}")
            DataBasesInfosSql()
        }
    }

    suspend fun isDatabaseEmpty(): Boolean = withContext(Dispatchers.IO) {
        try {
            val tarifications = database.dTarificationInfosDao().getAllTarificationsSync()
            tarifications.isEmpty()
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in isDatabaseEmpty: ${e.message}")
            true
        }
    }
}
