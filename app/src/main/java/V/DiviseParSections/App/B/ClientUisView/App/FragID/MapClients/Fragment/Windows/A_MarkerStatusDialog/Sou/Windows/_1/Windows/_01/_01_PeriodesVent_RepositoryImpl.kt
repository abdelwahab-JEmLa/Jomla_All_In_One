package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01
  /*
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00.ProduitsVenduParLui
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00.VendeursActiveDonsCettePeriode
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_PeriodesVentRoomSQl
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00._01_VentsNoSQl
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

class _01_VentsHistoriquesDataBase_RepositoryImpl(
    val appDatabase: AppDatabase,
) : _01_VentsHistoriquesDataBase_Repository {
    private val TAG = "_01_VentsNoSQl"

    override var modelDatasSnapList: SnapshotStateList<_01_VentsNoSQl> =
        mutableStateListOf()

    private val _progressRepo = MutableStateFlow(0f)
    override val progressRepo: StateFlow<Float> = _progressRepo.asStateFlow()


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

            val count = appDatabase
                ._02_VendeursActiveDonsCettePeriode_RoomSQlModelDao()
                .getCount()

            if (count == 0) {
                insertTestData()
            }

            collecteConvertSQlToNoSqlDataBase()
        }
    }

    override suspend fun refreshData() {
        collecteConvertSQlToNoSqlDataBase()
    }

    override suspend fun addPeriode(periode: _01_PeriodesVentRoomSQl) {
        appDatabase._01_PeriodesVentRoomSQlModelDao().insert(periode)
        refreshData()
    }

    private fun insertTestData() {
        repositoryScope.launch {
            try {
                // Create test data for periods
                val currentDate = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault()).format(Date())
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                val periode1 = _01_PeriodesVentRoomSQl(
                    keyID = "2023_04_17->(14:30)",
                    parentkeyID = "",
                    startIndex = 1,
                    nom = "Période Test 1",
                    quantity = 10
                )

                val periode2 = _01_PeriodesVentRoomSQl(
                    keyID = "$currentDate->($currentTime)",
                    parentkeyID = "",
                    startIndex = 2,
                    nom = "Période Test 2",
                    quantity = 5
                )

                // Add another period on the same day as periode1 for testing related periods
                val periode3 = _01_PeriodesVentRoomSQl(
                    keyID = "2023_04_17->(18:00)",
                    parentkeyID = "",
                    startIndex = 3,
                    nom = "Période Test 3",
                    quantity = 7
                )

                // Insert periods
                appDatabase._01_PeriodesVentRoomSQlModelDao().insertAll(listOf(periode1, periode2, periode3))

                // Create test data for vendeurs
                val vendeur1 = _02_VendeursActiveDonsCettePeriodeRoomSQlModel(
                    keyID = "1->(_012_ComptsVendeurs Test 1)",
                    parentkeyID = "2023_04_17->(14:30)",
                    startIndex = 1,
                    nom = "_012_ComptsVendeurs Test 1",
                    quantity = 8
                )

                val vendeur2 = _02_VendeursActiveDonsCettePeriodeRoomSQlModel(
                    keyID = "2->(_012_ComptsVendeurs Test 2)",
                    parentkeyID = "2023_04_17->(14:30)",
                    startIndex = 2,
                    nom = "_012_ComptsVendeurs Test 2",
                    quantity = 2
                )

                val vendeur3 = _02_VendeursActiveDonsCettePeriodeRoomSQlModel(
                    keyID = "1->(_012_ComptsVendeurs Test 3)",
                    parentkeyID = "$currentDate->($currentTime)",
                    startIndex = 1,
                    nom = "_012_ComptsVendeurs Test 3",
                    quantity = 5
                )

                // Add the same vendeur in another period to test related periods
                val vendeur4 = _02_VendeursActiveDonsCettePeriodeRoomSQlModel(
                    keyID = "1->(_012_ComptsVendeurs Test 1)",
                    parentkeyID = "2023_04_17->(18:00)",
                    startIndex = 1,
                    nom = "_012_ComptsVendeurs Test 1",
                    quantity = 7
                )

                // Create test data for produits
                val produit1 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "1->(_014_Produits Test 1)",
                    parentkeyID = "1->(_012_ComptsVendeurs Test 1)",
                    id = 1,
                    nom = "_014_Produits Test 1",
                    quantity = 5
                )

                val produit2 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "2->(_014_Produits Test 2)",
                    parentkeyID = "1->(_012_ComptsVendeurs Test 1)",
                    id = 2,
                    nom = "_014_Produits Test 2",
                    quantity = 3
                )

                val produit3 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "1->(_014_Produits Test 3)",
                    parentkeyID = "2->(_012_ComptsVendeurs Test 2)",
                    id = 1,
                    nom = "_014_Produits Test 3",
                    quantity = 2
                )

                val produit4 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "1->(_014_Produits Test 4)",
                    parentkeyID = "1->(_012_ComptsVendeurs Test 3)",
                    id = 1,
                    nom = "_014_Produits Test 4",
                    quantity = 5
                )

                // Add products for vendeur4
                val produit5 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "1->(_014_Produits Test 1)",
                    parentkeyID = "1->(_012_ComptsVendeurs Test 1)",
                    id = 1,
                    nom = "_014_Produits Test 1",
                    quantity = 4
                )

                val produit6 = _03_ProduitsVenduParLuiRoomSQlModel(
                    keyID = "3->(_014_Produits Test 5)",
                    parentkeyID = "1->(_012_ComptsVendeurs Test 1)",
                    id = 3,
                    nom = "_014_Produits Test 5",
                    quantity = 3
                )

                // Insert test data
                appDatabase._02_VendeursActiveDonsCettePeriode_RoomSQlModelDao().insertAll(
                    listOf(vendeur1, vendeur2, vendeur3, vendeur4)
                )
                appDatabase._03_ProduitsVenduParLui_RoomSQlModelDao().insertAll(
                    listOf(produit1, produit2, produit3, produit4, produit5, produit6)
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
                val periodesRoomList = _periodesVentRoomSQl  // Renamed this variable
                val vendeursList = _vendeursActiveDonsCettePeriodeRoomSQlModel
                val produitsList = _produitsVenduParLuiRoomSQlModel

                // Create a periode map to group vendeurs by periode
                val periodeMap = mutableMapOf<String, _01_VentsNoSQl>()

                // First process periods
                periodesRoomList.forEach { periodeModel ->
                    periodeMap[periodeModel.keyID] = _01_VentsNoSQl().apply {
                        this.keyID = periodeModel.keyID
                        this.dateDebutDeCettePeriode = periodeModel.keyID.split("->(")[0]
                        this.tempDebutDeCettePeriode = periodeModel.keyID.split("->(")[1].removeSuffix(")")
                        this.vendeursActiveDonsCettePeriode = mutableMapOf()
                    }
                }

                _progressRepo.value = 0.3f

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

                _progressRepo.value = 0.8f

                // Convert the map to a list for the modelDatasSnapList
                val periodesNoSqlList = periodeMap.values.toList()  // Used a different name here

                // Now set the a01PeriodesVent for each periode to reference all periods
                // This allows each period to access other periods for related data
                periodeMap.values.forEach { periode ->
                    periode.a01PeriodesVent = periodesNoSqlList
                }

                _progressRepo.value = 0.9f

                // Update the model data list
                modelDatasSnapList.clear()
                modelDatasSnapList.addAll(periodesNoSqlList)

                _progressRepo.value = 1.0f

            } catch (e: Exception) {
                // Handle errors without logging
                _progressRepo.value = 0f
            }
        }
    }}
                                */
