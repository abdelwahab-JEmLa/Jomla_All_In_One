package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.E.Repository

import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import Z_CodePartageEntreApps.Model.A0_DataBasesGroup
import Z_CodePartageEntreApps.Model.A_ProduitInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Data.testD_TarificationInfosT2
import Z_CodePartageEntreApps.Proto.Par.Type.Models.D_TarificationInfos
import Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase.F0_FireBaseOperationsHandler
import Z_CodePartageEntreApps.Proto.Par.Type.Modules.FireBase.deleteRef
import Z_CodePartageEntreApps.Proto.Par.Type.Modules.SQL.G_RoomOperationsHandler
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys.GroupeRepositorysProtoAvJuin3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class E_GroupedDataBasesRepository(
    val database: AppDatabase,
    val repo_0_0_HeadSQLRepositorys: GroupeRepositorysProtoAvJuin3,
    private val fireBase: F0_FireBaseOperationsHandler,
    private val room: G_RoomOperationsHandler
) {
    val repositorysModel = repo_0_0_HeadSQLRepositorys.repositorys_Model

    private val idComptActivePourCeTelep = repositorysModel.activeIdDe_1_5_Vendeur

    private val repository15Vendeur = repositorysModel.repository_1_5_Vendeur

     private val findComptActive = repository15Vendeur
        .modelDatasSnapList.find { it.vid == idComptActivePourCeTelep }

    private val comptActuelle_migreSonDataBaseAuStart = findComptActive?.migreSonDataBaseAuStart ?: false

    private val repoCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val _modelListFlow = MutableStateFlow<List<A0_DataBasesGroup>>(emptyList())
    private var modelList: List<A0_DataBasesGroup>
        get() = _modelListFlow.value
        set(value) {
            _modelListFlow.value = value
        }

    val modelListFlow: StateFlow<List<A0_DataBasesGroup>> = _modelListFlow.asStateFlow()
    val mainProgressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    private fun getDataType(data: Any): KClass<*> {
        return when (data::class) {
            D_TarificationInfos::class -> {
                D_TarificationInfos::class
            }

            A_ProduitInfos::class -> {
                A_ProduitInfos::class
            }

            else -> {
                throw IllegalArgumentException("Unsupported data type: ${data::class.simpleName}")
            }
        }
    }

    fun update(
        data: Any,
        onSuccess: (Long, Any) -> Unit = { _, _ -> },
        onError: (Exception) -> Unit = {}
    ) {
        repoCoroutineScope.launch {
            try {
                val dataType = getDataType(data)
                val (insertedId, updatedData) = room.update(data, dataType)

                // Insert into Firebase using the new updateInFB function
                when (updatedData) {
                    is D_TarificationInfos -> {
                        fireBase.updateInFB(updatedData)
                    }

                    is A_ProduitInfos -> {
                        fireBase.updateInFB(updatedData)
                    }
                }

                onSuccess(insertedId, updatedData)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private fun upsertAllRoomEtFireBase(
        dataList: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit = {}
    ) {
        repoCoroutineScope.launch(Dispatchers.IO) {
            try {
                updateProgress(0.1f)

                room.insertAllAndReturnListIdToData(dataList) { mapData ->
                    repoCoroutineScope.launch {
                        updateProgress(0.5f)

                        if (mapData.isNotEmpty()) {
                            val dataListForFirebase = mapData.values.toList()
                            fireBase.setListDataInlineFun<D_TarificationInfos>(dataListForFirebase)

                            repoCoroutineScope.launch {
                                updateProgress(1f)
                                onAddSuccess(mapData)
                            }
                        } else {
                            updateProgress(1f)
                            onAddSuccess(emptyMap())
                        }
                    }
                }
            } catch (e: Exception) {
                updateProgress(0f)
                onAddSuccess(emptyMap())
            }
        }
    }

    private fun collectRoom() {
        repoCoroutineScope.launch {
            try {
                val produitsFlow = database.a_ProduitInfosDao().getAllProduits()
                val tarificationsFlow = database.dTarificationInfosDao().getAllTarifications()

                combine(
                    produitsFlow,
                    tarificationsFlow
                ) { produits: List<A_ProduitInfos>, tarifications: List<D_TarificationInfos> ->
                    listOf(
                        A0_DataBasesGroup(
                            a_ProduitInfos = produits.toMutableList(),
                            d_TarificationInfos = tarifications.toMutableList()
                        )
                    )
                }.collect { combinedData ->
                    modelList = combinedData
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun initializeProduitInfos() {
        updateProgress(0.9f)
        collectRoom()
        updateProgress(1f)
    }

    private fun updateProgress(progress: Float) {
        val clampedProgress = progress.coerceIn(0f, 1f)
        mainProgressRepo.value = clampedProgress
    }

    private fun initializeTarificationInfos() {
        updateProgress(0f)

        fireBase.getDataFromFirebase { tariffDataList, produitInfoList ->
            repoCoroutineScope.launch {
                try {
                    if (tariffDataList.isEmpty()) {
                        val testData = testD_TarificationInfosT2()
                        upsertAllRoomEtFireBase(testData)
                    } else {
                        room.checkDataBaseIsEmpty { roomHandler ->
                            repoCoroutineScope.launch {
                                roomHandler.insertAllAndReturnListIdToData(tariffDataList) {
                                    updateProgress(0.5f)
                                }
                            }
                        }
                    }
                    val hasFirebaseProducts = produitInfoList.isNotEmpty()

                    migreOldDatas(comptActuelle_migreSonDataBaseAuStart)

                    val isRoomEmpty = !room.inlineCheckDataBaseIsNotEmpty<A_ProduitInfos>()

                    if (isRoomEmpty && hasFirebaseProducts) {
                        room.insertAllAndReturnListIdToDataInline<A_ProduitInfos>(produitInfoList)
                        updateProgress(0.8f)
                    } else {
                        updateProgress(0.8f)
                    }

                    initializeDatabase<A_ProduitInfos>()

                } catch (e: Exception) {
                    updateProgress(0f)
                }
            }
        }

        collectRoom()
    }

    private inline fun <reified DataBase : Any> initializeDatabase() {
        repoCoroutineScope.launch(Dispatchers.IO) {
            try {
                when (DataBase::class) {
                    D_TarificationInfos::class -> {
                        initializeTarificationInfos()
                    }

                    A_ProduitInfos::class -> {
                        initializeProduitInfos()
                    }

                    else -> {
                        updateProgress(1f)
                    }
                }
            } catch (e: Exception) {
                updateProgress(0f)
            }
        }
    }

    init {
        initializeDatabase<D_TarificationInfos>()
    }

    private suspend fun migreOldDatas(comptActuelle_migreSonDataBaseAuStart: Boolean) {
        if (comptActuelle_migreSonDataBaseAuStart) {
            try {
                fireBase.deleteRef<A_ProduitInfos>()
                val (originalCount, resultMap) = fireBase.getAncienDB_changeKeysFireBase()

                val newDataList = resultMap.values.toList()

                fireBase.setListDataInlineFun<A_ProduitInfos>(newDataList)

                if (findComptActive != null) {
                    repository15Vendeur.addDataAndReturneItVID(
                        findComptActive.copy(migreSonDataBaseAuStart = false)
                    )
                }

            } catch (migrationError: Exception) {
                migrationError.printStackTrace()
            }
        }
    }

}
