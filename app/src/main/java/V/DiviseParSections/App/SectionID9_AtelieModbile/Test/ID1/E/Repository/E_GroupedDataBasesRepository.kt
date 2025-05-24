package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.E.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A0_DataBasesGroup
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.A_ProduitInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models.D_TarificationInfos
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.Test.testD_TarificationInfosT2
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class E_GroupedDataBasesRepository(
    val database: AppDatabase,
    private val fireBase: F0_FireBaseOperationsHandler,
    private val room: G_RoomOperationsHandler
) {

    private val repoCoroutineScope = CoroutineScope(Dispatchers.IO)
    private val _modelListFlow = MutableStateFlow<List<A0_DataBasesGroup>>(emptyList())
    private var modelList: List<A0_DataBasesGroup>
        get() = _modelListFlow.value
        set(value) {
            _modelListFlow.value = value
        }

    val modelListFlow: StateFlow<List<A0_DataBasesGroup>> = _modelListFlow.asStateFlow()
    val mainProgressRepo: MutableStateFlow<Float> = MutableStateFlow(0f)

    init {
        initializeDatabase<D_TarificationInfos>()
        initializeDatabase<A_ProduitInfos>()
    }

    private inline fun <reified DataBase : Any> initializeDatabase() {
        repoCoroutineScope.launch(Dispatchers.IO) {
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
        }
    }

    private fun initializeTarificationInfos() {
        updateProgress(0f)

        fireBase.getDataFromFirebase { tariffDataList, produitInfoList ->
            repoCoroutineScope.launch {
                if (tariffDataList.isEmpty()) {
                    upsertAllRoomEtFireBase(testD_TarificationInfosT2())
                } else {
                    room.checkDataBaseIsEmpty { roomHandler ->
                        repoCoroutineScope.launch {
                            roomHandler.insertAllAndReturnListIdToData(tariffDataList) {
                                updateProgress(1f)
                            }
                        }
                    }
                }
                val lence = true
                if (lence) {
                    fireBase.changeKeysFireBase()
                }

                if (room.inlineCheckDataBaseIsNotEmpty<A_ProduitInfos>()) {
                    val insertResult =
                        room.insertAllAndReturnListIdToDataInline<A_ProduitInfos>(produitInfoList)
                    updateProgress(1f)
                    println("Total items inserted: ${insertResult.size}")
                }
            }
        }

        collectRoom()
    }

    private fun initializeProduitInfos() {
        updateProgress(0f)
        collectRoom()
        updateProgress(1f)
    }

    private fun createDefaultProducts(): List<A_ProduitInfos> {
        return listOf(
            A_ProduitInfos.create(nom = "Produit par défaut 1"),
            A_ProduitInfos.create(nom = "Produit par défaut 2"),
            A_ProduitInfos.create(nom = "Produit par défaut 3")
        )
    }

    private fun updateProgress(progress: Float) {
        mainProgressRepo.value = progress.coerceIn(0f, 1f)
    }

    private fun upsertAllRoomEtFireBase(
        dataList: List<D_TarificationInfos>,
        onAddSuccess: (Map<Long, D_TarificationInfos>) -> Unit = {}
    ) {
        repoCoroutineScope.launch(Dispatchers.IO) {
            updateProgress(0.5f)

            room.insertAllAndReturnListIdToData(dataList) { mapData ->
                repoCoroutineScope.launch {
                    updateProgress(0.7f)

                    fireBase.upsertAllAndReturnListIdToData(mapData) { firebaseMap ->
                        repoCoroutineScope.launch {
                            updateProgress(1f)
                            onAddSuccess(mapData)
                        }
                    }
                }
            }
        }
    }

    private fun collectRoom() {
        repoCoroutineScope.launch {
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
        }
    }
}
