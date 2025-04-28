package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01
      /*
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_VentsNoSQl
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

override fun upsertUnSeulDataEtReturnVID(data: _01_VentsNoSQl, onSuccess: (Long) -> Unit): Unit {
        try {
            // Create a copy of the data to work with
            val dataToUpsert = data.copy()

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    // Check if the data already exists (if it has a valid vid)
                    if (dataToUpsert.vid > 0) {
                        // Update existing data
                        appDatabase._01_PeriodesVentRoomSQlModelDao().upsertEtReturnSonNewVid(dataToUpsert)

                        // Update in snapshot list
                        withContext(Dispatchers.Main) {
                            val index = modelDatasSnapList.indexOfFirst { it.vid == dataToUpsert.vid }
                            if (index >= 0) {
                                modelDatasSnapList[index] = dataToUpsert
                            } else {
                                modelDatasSnapList.add(dataToUpsert)
                            }
                        }

                        // Update in Firebase
                        _01_VentsHistoriquesDataBase_Repository.sonDataBaseRef.child(dataToUpsert.vid.toString())
                            .setValue(dataToUpsert).await()

                        // Call the success callback with the existing vid
                        onSuccess(dataToUpsert.vid)
                    } else {
                        // If no valid vid, upsertEtReturnSonNewVid as new (same as addDataAndReturneItVID)
                        val newVid = appDatabase._01_PeriodesVentRoomSQlModelDao().insertAvecReturnNewVid(dataToUpsert)

                        // Update the object with the new vid
                        dataToUpsert.vid = newVid

                        withContext(Dispatchers.Main) {
                            modelDatasSnapList.add(dataToUpsert)
                        }

                        // Update Firebase with the new vid
                        _01_VentsHistoriquesDataBase_Repository.sonDataBaseRef.child(newVid.toString())
                            .setValue(dataToUpsert).await()

                        // Call the success callback with the new vid
                        onSuccess(newVid)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error upserting data: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in upsertUnSeulDataEtReturnVID: ${e.message}")
        }
    }

    override fun upsertMultiDatasEtReturnVID(
        dataList: List<_01_VentsNoSQl>,
        onAddSuccess: (List<Long>) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

private suspend fun checkDataConsistency() {
    try {
        val roomCount = withContext(Dispatchers.IO) {
            try {
                appDatabase._01_PeriodesVentRoomSQlModelDao().getCount()
            } catch (e: Exception) {
                0
            }
        }

        val firebaseSnapshot = try {
            withContext(Dispatchers.IO) {
                val task = _01_VentsHistoriquesDataBase_Repository.sonDataBaseRef.get()
                Tasks.await(task)
            }
        } catch (e: Exception) {
            null
        }

        val firebaseCount = firebaseSnapshot?.childrenCount?.toInt() ?: 0

        if (roomCount != firebaseCount || roomCount == 0) {
            if (firebaseCount > 0) {
                importDeFireBaseAuRoom(repositoryScope)
            }
        }

        withContext(Dispatchers.Main) {
            FireBaseOnDataChangeListner()
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            FireBaseOnDataChangeListner()
        }
    }
}


private suspend fun initialize_01_PeriodesVentRepository() {
    try {
        loadDepuitRoom()
        checkDataConsistency()

    } catch (e: Exception) {
    }
}
private suspend fun loadDepuitRoom() {
    try {
        progressRepo.value = 0.2f
        withContext(Dispatchers.IO) {
            val dataList = try {
                appDatabase._01_PeriodesVentRoomSQlModelDao().getAll()
            } catch (e: Exception) {
                emptyList()
            }

            withContext(Dispatchers.Main) {
                modelDatasSnapList.clear()
                if (dataList.isNotEmpty()) {
                    modelDatasSnapList.addAll(dataList)
                }
                initialDataLoaded = true
                progressRepo.value = 1.0f
            }
        }
    } catch (e: Exception) {
        progressRepo.value = 0f
    }
}



private fun FireBaseOnDataChangeListner() {
    synchronized(flowListenerLock) {
        removeFlowDataListener()

        if (!isFlowListenerActive.get()) {
            flowValueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val updatedList = mutableListOf<_01_VentsNoSQl>()
                        for (dataSnapshot in snapshot.children) {
                            val data = dataSnapshot.getValue(_01_VentsNoSQl::class.java)
                            data?.let {
                                updatedList.add(it)
                            }
                        }

                        repositoryScope.launch(Dispatchers.Main) {
                            if (updatedList.isNotEmpty()) {
                                modelDatasSnapList.clear()
                                modelDatasSnapList.addAll(updatedList)
                            }
                        }

                        repositoryScope.launch(Dispatchers.IO) {
                            try {
                                appDatabase._01_PeriodesVentRoomSQlModelDao().deleteAll()
                                appDatabase._01_PeriodesVentRoomSQlModelDao()
                                    .insertAll(updatedList)
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                )
                            }
                        }
                    } catch (e: Exception) {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }

            _01_VentsHistoriquesDataBase_Repository.sonDataBaseRef.addValueEventListener(
                flowValueEventListener!!
            )
            isFlowListenerActive.set(true)
        }
    }
}

private fun removeFlowDataListener() {
    synchronized(flowListenerLock) {
        if (isFlowListenerActive.get() && flowValueEventListener != null) {
            try {
                _01_VentsHistoriquesDataBase_Repository.sonDataBaseRef.removeEventListener(
                    flowValueEventListener!!
                )
            } catch (e: Exception) {
            } finally {
                flowValueEventListener = null
                isFlowListenerActive.set(false)
            }
        }
    }
}

private fun importDeFireBaseAuRoom(viewModelScope: CoroutineScope) {
    try {
        progressRepo.value = 0f
        viewModelScope.launch(Dispatchers.Main) {
            modelDatasSnapList.clear()
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val task = _01_VentsHistoriquesDataBase_Repository.sonDataBaseRef.get()
                val snapshot = Tasks.await(task)

                try {
                    appDatabase._01_PeriodesVentRoomSQlModelDao().deleteAll()
                } catch (e: Exception) {
                }

                val dataList = mutableListOf<_01_VentsNoSQl>()

                for (dataSnapshot in snapshot.children) {
                    try {
                        val data = dataSnapshot.getValue(_01_VentsNoSQl::class.java)
                        data?.let {
                            dataList.add(it)
                        }
                    } catch (e: Exception) {
                    }
                }

                if (dataList.isNotEmpty()) {
                    try {
                        appDatabase._01_PeriodesVentRoomSQlModelDao().insertAll(dataList)

                        withContext(Dispatchers.Main) {
                            modelDatasSnapList.addAll(dataList)
                        }
                    } catch (e: Exception) {
                    }
                }

                initialDataLoaded = true
                progressRepo.value = 1.0f
            } catch (e: Exception) {
                progressRepo.value = 0f
            }
        }
    } catch (e: Exception) {
        progressRepo.value = 0f
    }
}

private fun removeDataChangeListener() {
    synchronized(listenerLock) {
        if (isListenerActive.get() && valueEventListener != null) {
            try {
                _01_VentsHistoriquesDataBase_Repository.sonDataBaseRef.removeEventListener(
                    valueEventListener!!
                )
            } catch (e: Exception) {
            } finally {
                valueEventListener = null
                isListenerActive.set(false)
            }
        }
    }
}


fun cleanup() {
    repositoryScope.launch {
        removeDataChangeListener()
        removeFlowDataListener()
    }
}

fun onDestroy() {
    cleanup()
}
              */
