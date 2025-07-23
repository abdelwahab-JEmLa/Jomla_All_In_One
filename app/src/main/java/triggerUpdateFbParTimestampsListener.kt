/*package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase8.Factory.Init
    fun triggerUpdateFbParTimestampsListener() {
        if (isListenerRegistered) return
        isListenerRegistered = true

        Log.d(repoTAG, "Starting Firebase listener registration")

        repoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(repoTAG, "Firebase data changed - processing ${snapshot.childrenCount} items")

                factoryScope.launch {
                    try {
                        val localData = dao.getAll()
                        val localDataMap = localData.associateBy { it.keyID }
                        val firebaseKeyIds = mutableSetOf<String>()

                        var updateCount = 0
                        var addCount = 0

                        Log.d(repoTAG, "Local entities count: ${localData.size}")

                        // Process Firebase data
                        for (child in snapshot.children) {
                            try {
                                child.getValue(M8BonVent::class.java)?.let { fbEntity ->
                                    val entityWithKey = fbEntity.copy(keyID = child.key ?: "")
                                    firebaseKeyIds.add(entityWithKey.keyID)

                                    val localEntity = localDataMap[entityWithKey.keyID]

                                    when {
                                        // New entity from Firebase
                                        localEntity == null -> {
                                            dao.upsert(entityWithKey)
                                            addCount++
                                            Log.d(repoTAG, "Added new entity: ${entityWithKey.keyID} ${getEtate(entityWithKey)} " +
                                                    "with timestamp: ${entityWithKey.dernierTimeTampsSynchronisationAvecFireBase}")
                                        }
                                        // Update existing entity if Firebase timestamp is newer
                                        entityWithKey.dernierTimeTampsSynchronisationAvecFireBase > localEntity.dernierTimeTampsSynchronisationAvecFireBase -> {
                                            // Always update when Firebase is newer - this ensures data consistency
                                            dao.upsert(entityWithKey)
                                            updateCount++
                                            Log.d(repoTAG, "Updated entity: ${entityWithKey.keyID}${getEtate(entityWithKey)} (FB: ${entityWithKey.dernierTimeTampsSynchronisationAvecFireBase}, Local: ${localEntity.dernierTimeTampsSynchronisationAvecFireBase})")
                                        }
                                        // Local is newer or equal - potentially push to Firebase if local is newer
                                        localEntity.dernierTimeTampsSynchronisationAvecFireBase > entityWithKey.dernierTimeTampsSynchronisationAvecFireBase -> {
                                            Log.d(repoTAG, "Local entity is newer, syncing to Firebase: ${localEntity.keyID}${getEtate(entityWithKey)} (Local: ${localEntity.dernierTimeTampsSynchronisationAvecFireBase}, FB: ${entityWithKey.dernierTimeTampsSynchronisationAvecFireBase})")
                                            try {
                                                batchFireBaseUpdateGBonVent(listOf(localEntity))
                                            } catch (e: Exception) {
                                                Log.e(repoTAG, "Error syncing local entity to Firebase: ${e.message}", e)
                                            }
                                        }
                                        // Timestamps are equal - no action needed
                                        else -> {
                                            Log.d(repoTAG, "Timestamps are equal for entity: ${entityWithKey.keyID}${getEtate(entityWithKey)} (${entityWithKey.dernierTimeTampsSynchronisationAvecFireBase})")
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(repoTAG, "Error processing child ${child.key}: ${e.message}", e)
                            }
                        }

                        // Delete entities that exist locally but not in Firebase
                        val itemsToDelete = localDataMap.keys - firebaseKeyIds
                        var deleteCount = 0

                        for (keyToDelete in itemsToDelete) {
                            try {
                                dao.deleteByKeyId(keyToDelete)
                                deleteCount++
                                Log.d(repoTAG, "Deleted entity: $keyToDelete")
                            } catch (e: Exception) {
                                Log.e(repoTAG, "Error deleting entity $keyToDelete: ${e.message}", e)
                            }
                        }

                        // Log synchronization summary
                        Log.i(repoTAG, "Sync complete - Added: $addCount, Updated: $updateCount, Deleted: $deleteCount")

                    } catch (e: Exception) {
                        Log.e(repoTAG, "Error in Firebase listener: ${e.message}", e)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                isListenerRegistered = false
                Log.e(repoTAG, "Firebase listener cancelled: ${error.message}")
            }
        })
    }                      */
