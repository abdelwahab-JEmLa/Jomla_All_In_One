package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00.ProduitsVenduParLui
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00.VendeursActiveDonsCettePeriode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_PeriodesVentNoSQl
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_PeriodesVentRoomSQl
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._02_VendeursActiveDonsCettePeriodeRoomSQlModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._03_ProduitsVenduParLuiRoomSQlModel
import Z_CodePartageEntreApps.Apps.Manager.Module.B.Room.AppDatabase
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class _01_PeriodesVent_RepositoryImpl(
    val appDatabase: AppDatabase,
) : _01_PeriodesVent_Repository {
    private val TAG = "_01_PeriodesVentNoSQl"

    override var modelDatasSnapList: SnapshotStateList<_01_PeriodesVentNoSQl> =
        mutableStateListOf()

    private val _progressRepo = MutableStateFlow(0f)
    override val progressRepo: StateFlow<Float> = _progressRepo.asStateFlow()

    // Use proper SnapshotStateList for each model
    private var _periodesVentRoomSQl: SnapshotStateList<_01_PeriodesVentRoomSQl> =
        mutableStateListOf()

    private var _vendeursActiveDonsCettePeriodeRoomSQlModel:
            SnapshotStateList<_02_VendeursActiveDonsCettePeriodeRoomSQlModel> =
        mutableStateListOf()

    private var _produitsVenduParLuiRoomSQlModel:
            SnapshotStateList<_03_ProduitsVenduParLuiRoomSQlModel> =
        mutableStateListOf()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        repositoryScope.launch {
            // This is fine now because getCount() is suspend
            val count = appDatabase._02_VendeursActiveDonsCettePeriode_RoomSQlModelDao().getCount()
            if (count == 0) {
                insertTestData()
            }
            loadDataFromDatabase()
            collecteConvertSQlToNoSqlDataBase()
        }
    }

    private suspend fun loadDataFromDatabase() {
        // Load periods from database
        val periodes = appDatabase._01_PeriodesVentRoomSQlModelDao().getAll()
        _periodesVentRoomSQl.clear()
        _periodesVentRoomSQl.addAll(periodes)

        // Load vendeurs from database
        val vendeurs = appDatabase._02_VendeursActiveDonsCettePeriode_RoomSQlModelDao().getAll()
        _vendeursActiveDonsCettePeriodeRoomSQlModel.clear()
        _vendeursActiveDonsCettePeriodeRoomSQlModel.addAll(vendeurs)

        // Load produits from database
        val produits = appDatabase._03_ProduitsVenduParLui_RoomSQlModelDao().getAll()
        _produitsVenduParLuiRoomSQlModel.clear()
        _produitsVenduParLuiRoomSQlModel.addAll(produits)
    }

    override suspend fun refreshData() {
        loadDataFromDatabase()
        collecteConvertSQlToNoSqlDataBase()
    }

    override suspend fun addPeriode(periode: _01_PeriodesVentRoomSQl) {
        appDatabase._01_PeriodesVentRoomSQlModelDao().insert(periode)
        refreshData()
    }

    override suspend fun getAllPeriodes(): List<_01_PeriodesVentRoomSQl> {
        return appDatabase._01_PeriodesVentRoomSQlModelDao().getAll()
    }

    private fun insertTestData() {
        repositoryScope.launch {
            try {
                // Create test data for periods
                val currentDate = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(Date())
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                val periode1 = _01_PeriodesVentRoomSQl(
                    keyID = "2023_04_17->(14:30)",
                    parentkeyID = "", // No parent for periods
                    startIndex = 1,
                    nom = "Période Test 1",
                    quantity = 10
                )

                val periode2 = _01_PeriodesVentRoomSQl(
                    keyID = "$currentDate->($currentTime)",
                    parentkeyID = "", // No parent for periods
                    startIndex = 2,
                    nom = "Période Test 2",
                    quantity = 5
                )

                // Insert periods
                appDatabase._01_PeriodesVentRoomSQlModelDao().insertAll(listOf(periode1, periode2))

                // Create test data for vendeurs
                val vendeur1 = _02_VendeursActiveDonsCettePeriodeRoomSQlModel(
                    keyID = "1->(Vendeur Test 1)",
                    parentkeyID = "2023_04_17->(14:30)",
                    startIndex = 1,
                    nom = "Vendeur Test 1",
                    quantity = 8
                )

                val vendeur2 = _02_VendeursActiveDonsCettePeriodeRoomSQlModel(
                    keyID = "2->(Vendeur Test 2)",
                    parentkeyID = "2023_04_17->(14:30)",
                    startIndex = 2,
                    nom = "Vendeur Test 2",
                    quantity = 2
                )

                val vendeur3 = _02_VendeursActiveDonsCettePeriodeRoomSQlModel(
                    keyID = "1->(Vendeur Test 3)",
                    parentkeyID = "$currentDate->($currentTime)",
                    startIndex = 1,
                    nom = "Vendeur Test 3",
                    quantity = 5
                )

                // Create test data for produits
                val produit1 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "1->(Produit Test 1)",
                    parentkeyID = "1->(Vendeur Test 1)",
                    id = 1,
                    nom = "Produit Test 1",
                    quantity = 5
                )

                val produit2 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "2->(Produit Test 2)",
                    parentkeyID = "1->(Vendeur Test 1)",
                    id = 2,
                    nom = "Produit Test 2",
                    quantity = 3
                )

                val produit3 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "1->(Produit Test 3)",
                    parentkeyID = "2->(Vendeur Test 2)",
                    id = 1,
                    nom = "Produit Test 3",
                    quantity = 2
                )

                val produit4 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "1->(Produit Test 4)",
                    parentkeyID = "1->(Vendeur Test 3)",
                    id = 1,
                    nom = "Produit Test 4",
                    quantity = 5
                )

                // Insert test data
                appDatabase._02_VendeursActiveDonsCettePeriode_RoomSQlModelDao().insertAll(
                    listOf(vendeur1, vendeur2, vendeur3)
                )
                appDatabase._03_ProduitsVenduParLui_RoomSQlModelDao().insertAll(
                    listOf(produit1, produit2, produit3, produit4)
                )
            } catch (e: Exception) {
                // Error handling without logging
            }
        }
    }

    private fun collecteConvertSQlToNoSqlDataBase() {
        repositoryScope.launch {
            try {
                // Convert the lists to flows
                _progressRepo.value = 0.1f

                // Process data directly without using combine since we already have the lists
                val periodesList = _periodesVentRoomSQl
                val vendeursList = _vendeursActiveDonsCettePeriodeRoomSQlModel
                val produitsList = _produitsVenduParLuiRoomSQlModel

                // Create a periode map to group vendeurs by periode
                val periodeMap = mutableMapOf<String, _01_PeriodesVentNoSQl>()

                // First process periods
                periodesList.forEach { periodeModel ->
                    periodeMap[periodeModel.keyID] = _01_PeriodesVentNoSQl().apply {
                        this.keyID = periodeModel.keyID
                        this.dateDebutDeCettePeriode = periodeModel.keyID.split("->(")[0]
                        this.tempDebutDeCettePeriode = periodeModel.keyID.split("->(")[1].removeSuffix(")")
                        this.vendeursActiveDonsCettePeriode = mutableMapOf()
                    }
                }

                _progressRepo.value = 0.3f

                // Then process vendeurs
                vendeursList.forEach { vendeurModel ->
                    val periodeId = vendeurModel.parentkeyID
                    if (periodeMap.containsKey(periodeId)) {
                        // Create vendeur object
                        val vendeur = VendeursActiveDonsCettePeriode().apply {
                            this.keyID = vendeurModel.keyID
                            this.startIndex = vendeurModel.startIndex
                            this.nom = vendeurModel.nom
                            this.produitsVenduParLui = mutableMapOf()
                        }

                        (periodeMap[periodeId]!!.vendeursActiveDonsCettePeriode as
                                MutableMap<String, VendeursActiveDonsCettePeriode>)[vendeurModel.keyID] = vendeur
                    }
                }

                _progressRepo.value = 0.6f

                // Finally process produits
                produitsList.forEach { produitModel ->
                    val vendeurId = produitModel.parentkeyID

                    // Find the vendeur in all periods
                    periodeMap.values.forEach { periode ->
                        periode.vendeursActiveDonsCettePeriode.forEach { (vendeurKey, vendeur) ->
                            if (vendeurKey == vendeurId) {
                                // Create produit object
                                val produit = ProduitsVenduParLui().apply {
                                    this.keyID = produitModel.keyID
                                    this.startIndex = produitModel.id
                                    this.nom = produitModel.nom
                                    this.quantity = produitModel.quantity
                                }

                                // Add produit to vendeur
                                (vendeur.produitsVenduParLui as MutableMap<String, ProduitsVenduParLui>)[produitModel.keyID] = produit
                            }
                        }
                    }
                }

                _progressRepo.value = 0.9f

                // Update the model data list
                modelDatasSnapList.clear()
                modelDatasSnapList.addAll(periodeMap.values)

                _progressRepo.value = 1.0f

            } catch (e: Exception) {
                // Handle errors without logging
                _progressRepo.value = 0f
            }
        }
    }
}
