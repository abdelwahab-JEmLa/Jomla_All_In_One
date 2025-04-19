package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01

override fun upsertUnSeulDataEtReturnVID(data: _01_PeriodesVentNoSQl, onSuccess: (Long) -> Unit): Unit {
        try {
            // Create a copy of the data to work with
            val dataToUpsert = data.copy()

            repositoryScope.launch(Dispatchers.IO) {
                try {
                    // Check if the data already exists (if it has a valid vid)
                    if (dataToUpsert.vid > 0) {
                        // Update existing data
                        appDatabase._01_PeriodesVentRoomSQlModelDao().insert(dataToUpsert)

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
                        _01_PeriodesVent_Repository.sonDataBaseRef.child(dataToUpsert.vid.toString())
                            .setValue(dataToUpsert).await()

                        // Call the success callback with the existing vid
                        onSuccess(dataToUpsert.vid)
                    } else {
                        // If no valid vid, insert as new (same as addDataAndReturneItVID)
                        val newVid = appDatabase._01_PeriodesVentRoomSQlModelDao().insertAvecRetureNewVid(dataToUpsert)

                        // Update the object with the new vid
                        dataToUpsert.vid = newVid

                        withContext(Dispatchers.Main) {
                            modelDatasSnapList.add(dataToUpsert)
                        }

                        // Update Firebase with the new vid
                        _01_PeriodesVent_Repository.sonDataBaseRef.child(newVid.toString())
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
        dataList: List<_01_PeriodesVentNoSQl>,
        onAddSuccess: (List<Long>) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    private suspend fun initialize_01_PeriodesVentRepository() {
        try {
            loadDepuitRoom()
            checkDataConsistency()


            if (TAG.isNotEmpty()) {
                log()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing repository: ${e.message}")
        }
    }
