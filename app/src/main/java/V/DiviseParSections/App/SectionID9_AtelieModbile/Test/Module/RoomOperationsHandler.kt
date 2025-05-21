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
            data.forEachIndexed { index, tarif ->
                Log.d("RoomOperationsHandler", "[$index] ID: ${tarif.id}, nom: '${tarif.nom}', timestamps: ${tarif.timestamps}")
            }

            val resultMap = mutableMapOf<Long, D_TarificationInfos>()

            // Let's try a completely different approach - insert one by one with detailed logging
            data.forEachIndexed { index, tarif ->
                Log.d("RoomOperationsHandler", "=== Processing item $index ===")
                Log.d("RoomOperationsHandler", "Original tarif: id=${tarif.id}, nom='${tarif.nom}'")

                try {
                    // First, let's check current database state
                    val currentCount = database.dTarificationInfosDao().getAllTarificationsSync().size
                    Log.d("RoomOperationsHandler", "Current DB count before operation: $currentCount")

                    // For new items (id = 0), always insert fresh
                    val finalId = if (tarif.id == 0L) {
                        Log.d("RoomOperationsHandler", "Inserting new item...")
                        val newId = database.dTarificationInfosDao().insert(tarif)
                        Log.d("RoomOperationsHandler", "Insert returned ID: $newId")
                        newId
                    } else {
                        // For existing items, check if they exist
                        Log.d("RoomOperationsHandler", "Checking existing item with ID: ${tarif.id}")
                        val existing = database.dTarificationInfosDao().getTarificationById(tarif.id)
                        if (existing != null) {
                            Log.d("RoomOperationsHandler", "Item exists, updating...")
                            database.dTarificationInfosDao().update(tarif)
                            tarif.id
                        } else {
                            Log.d("RoomOperationsHandler", "Item doesn't exist, inserting...")
                            val newId = database.dTarificationInfosDao().insert(tarif)
                            Log.d("RoomOperationsHandler", "Insert returned ID: $newId")
                            newId
                        }
                    }

                    Log.d("RoomOperationsHandler", "Final ID determined: $finalId")

                    // Verify the operation worked
                    val newCount = database.dTarificationInfosDao().getAllTarificationsSync().size
                    Log.d("RoomOperationsHandler", "DB count after operation: $newCount")

                    if (finalId > 0L) {
                        val updatedTariff = tarif.copy(id = finalId)
                        resultMap[finalId] = updatedTariff
                        Log.d("RoomOperationsHandler", "✓ Successfully added to result map: ID=$finalId, nom='${updatedTariff.nom}'")

                        // Double-check by querying the inserted/updated item
                        val verifyItem = database.dTarificationInfosDao().getTarificationById(finalId)
                        if (verifyItem != null) {
                            Log.d("RoomOperationsHandler", "✓ Verified item exists in DB: ${verifyItem.nom}")
                        } else {
                            Log.w("RoomOperationsHandler", "⚠ Warning: Item not found in DB after operation!")
                        }
                    } else {
                        Log.w("RoomOperationsHandler", "✗ Invalid ID ($finalId) for tarif: ${tarif.nom}")
                    }

                } catch (itemException: Exception) {
                    Log.e("RoomOperationsHandler", "✗ Error processing tarif '${tarif.nom}': ${itemException.message}")
                    itemException.printStackTrace()
                }

                Log.d("RoomOperationsHandler", "=== End processing item $index ===")
            }

            // Final verification
            val allItemsInDb = database.dTarificationInfosDao().getAllTarificationsSync()
            Log.d("RoomOperationsHandler", "=== FINAL VERIFICATION ===")
            Log.d("RoomOperationsHandler", "Total items in DB: ${allItemsInDb.size}")
            Log.d("RoomOperationsHandler", "Result map size: ${resultMap.size}")
            Log.d("RoomOperationsHandler", "Result map keys: ${resultMap.keys}")

            allItemsInDb.forEach { item ->
                Log.d("RoomOperationsHandler", "DB Item: ID=${item.id}, nom='${item.nom}'")
            }

            Log.d("RoomOperationsHandler", "Final result map contains ${resultMap.size} entries with keys: ${resultMap.keys}")

            onAddSuccess(resultMap) //->
            //TODO(FIXME):Fix erreur  size = 0 pk 
            true
        } catch (e: Exception) {
            Log.e("RoomOperationsHandler", "Error in upsertAllAndReturnListIdToData: ${e.message}")
            e.printStackTrace()
            onAddSuccess(emptyMap()) // Make sure callback is called even on error
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

            DataBasesInfosSql(
                d_TarificationInfos = tarifications.toMutableList()
            )
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
            true // Assume empty if there's an error
        }
    }
}
