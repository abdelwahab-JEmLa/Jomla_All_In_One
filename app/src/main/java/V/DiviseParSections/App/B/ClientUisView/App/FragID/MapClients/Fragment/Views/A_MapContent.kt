package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.handleActiveTransaction
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Functions.handleFilterMarkersClick
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.Init.getMapUpdateTriggers
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.B_MarkersHandler.updateMapMarkers
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.cleanupMapResources
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.configureOSMDroid
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions.initializeMapPosition
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.MarkerEditModeOverlay
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Ui.handleMarkerPositionUpdate
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.MarkerStatusDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Options.A_GlobalOptionsControlsFloatingActionButtons_FragId1
import Z_CodePartageEntreApps.Windows.B.Windows.Options.A_OptionsControlsButtons_Main
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.Color as ComposeColor


@Entity
data class SecteurDeClients(
    @PrimaryKey(autoGenerate = true)
    val vid: Long,
    val nom: String = "Tamaris",
    val polygonEstFerme: Boolean = false,
)

@Entity
data class PolygonGeoLimite(
    @PrimaryKey(autoGenerate = true)
    val vid: Long,

    val parentSecteurDeClientsId: Long,
    val parentSecteurDeClientsKey: String =
        "SecteurDeClients.vid(SecteurDeClients.nom)",
    val aLatitude: Int,
    val aLongitude: Int,

    )

suspend fun insert2SecteurEtPolygon(
    secteurDao: SecteurDeClientsDao,
    polygonDao: PolygonGeoLimiteDao,
) {
    // Premier secteur - "Tamaris"
    val secteur1 = SecteurDeClients(
        vid = 0, // Auto-generated
        nom = "Tamaris",
        polygonEstFerme = true
    )

    // Deuxième secteur - "Plage"
    val secteur2 = SecteurDeClients(
        vid = 0, // Auto-generated
        nom = "Plage",
        polygonEstFerme = true
    )

    // Insérer les deux secteurs et obtenir leurs IDs
    val secteur1Id = secteurDao.insertAvecRetureNewVid(secteur1)
    val secteur2Id = secteurDao.insertAvecRetureNewVid(secteur2)

    // Point de départ pour le premier secteur
    val startPoint = GeoPoint(36.73928, 3.17188)

    // Points pour le premier hexagone (secteur1)
    val polygonPoints1 = ArrayList<PolygonGeoLimite>()
    val radius = 0.003 // Rayon pour le premier hexagone

    // Créer 6 points pour former un hexagone pour le secteur 1
    for (i in 0 until 6) {
        val angle = Math.toRadians(60.0 * i)
        val lat = startPoint.latitude + radius * sin(angle)
        val lon = startPoint.longitude + radius * cos(angle)

        polygonPoints1.add(
            PolygonGeoLimite(
                vid = 0, // Auto-generated
                parentSecteurDeClientsId = secteur1Id,
                parentSecteurDeClientsKey = "SecteurDeClients.$secteur1Id(${secteur1.nom})",
                aLatitude = (lat * 1E6).toInt(), // Stocker comme entiers (coordonnées en micro-degrés)
                aLongitude = (lon * 1E6).toInt()
            )
        )
    }

    // Point de départ pour le deuxième secteur (1km distant)
    val startPoint2 = GeoPoint(36.74828, 3.18188) // Ajout de ~1km en latitude et longitude

    // Points pour le deuxième hexagone (secteur2)
    val polygonPoints2 = ArrayList<PolygonGeoLimite>()

    // Créer 6 points pour former un hexagone pour le secteur 2
    for (i in 0 until 6) {
        val angle = Math.toRadians(60.0 * i)
        val lat = startPoint2.latitude + radius * sin(angle)
        val lon = startPoint2.longitude + radius * cos(angle)

        polygonPoints2.add(
            PolygonGeoLimite(
                vid = 0, // Auto-generated
                parentSecteurDeClientsId = secteur2Id,
                parentSecteurDeClientsKey = "SecteurDeClients.$secteur2Id(${secteur2.nom})",
                aLatitude = (lat * 1E6).toInt(),
                aLongitude = (lon * 1E6).toInt()
            )
        )
    }

    // Insérer tous les points des deux polygones
    polygonDao.insertAll(polygonPoints1)
    polygonDao.insertAll(polygonPoints2)
}

data class SecteurDeClientsPolygonGeoLimite(
    val keyIDSecteurDeClients: String = "vid->(nom)",
    val listPolygonGeoLimite: List<String> = listOf(),
)

suspend fun getPolygenDeChaqueSecteur(
    secteurDao: SecteurDeClientsDao,
    polygonDao: PolygonGeoLimiteDao,
): List<SecteurDeClientsPolygonGeoLimite> {
    // Récupérer tous les secteurs
    val allSecteurs = secteurDao.getAll()

    // Récupérer tous les points de polygone
    val allPolygonPoints = polygonDao.getAll()

    // Créer une liste de SecteurDeClientsPolygonGeoLimite
    val result = mutableListOf<SecteurDeClientsPolygonGeoLimite>()

    allSecteurs.forEach { secteur ->
        val secteurPoints = allPolygonPoints.filter {
            it.parentSecteurDeClientsId == secteur.vid
        }

        // Créer la clé pour ce secteur
        val secteurKey = "SecteurDeClients.${secteur.vid}->(${secteur.nom})"

        // Créer la liste de chaînes de caractères pour les points du polygone
        val pointKeys = secteurPoints.map { point ->
            "${point.vid}->(${point.parentSecteurDeClientsKey})"
        }

        // Ajouter le SecteurDeClientsPolygonGeoLimite à la liste résultat
        result.add(
            SecteurDeClientsPolygonGeoLimite(
                keyIDSecteurDeClients = secteurKey,
                listPolygonGeoLimite = pointKeys
            )
        )
    }

    return result
}

suspend fun addSectorsToMap(
    mapView: MapView, secteurPolygonInfoList: List<SecteurDeClientsPolygonGeoLimite>,
    allPolygonPoints: List<PolygonGeoLimite>, allSecteurs: List<SecteurDeClients>,
) {
    // Pour chaque secteurPolygonInfo, créer et ajouter un polygone à la carte
    secteurPolygonInfoList.forEach { secteurPolygonInfo ->
        // Extraire l'ID du secteur à partir de la clé
        val secteurKeyRegex = "SecteurDeClients\\.(\\d+)->.*".toRegex()
        val secteurIdMatch = secteurKeyRegex.find(secteurPolygonInfo.keyIDSecteurDeClients)
        val secteurId = secteurIdMatch?.groupValues?.get(1)?.toLongOrNull() ?: return@forEach

        // Trouver le secteur correspondant
        val secteur = allSecteurs.find { it.vid == secteurId } ?: return@forEach

        // Récupérer les points du polygone pour ce secteur
        val polygonPoints = allPolygonPoints.filter { it.parentSecteurDeClientsId == secteurId }

        // Créer un nouveau polygone pour ce secteur
        val polygon = Polygon(mapView)

        // Convertir les points PolygonGeoLimite en GeoPoint
        val geoPoints = polygonPoints.map { point ->
            GeoPoint(
                point.aLatitude / 1E6, // Conversion des micro-degrés en degrés
                point.aLongitude / 1E6
            )
        }

        // Si le polygone est vide, ignorer
        if (geoPoints.isEmpty()) return@forEach

        // Fermer le polygone si nécessaire
        val pointsList = ArrayList<GeoPoint>(geoPoints)
        if (secteur.polygonEstFerme && pointsList.isNotEmpty() &&
            pointsList.first() != pointsList.last()
        ) {
            pointsList.add(pointsList.first())
        }

        // Configurer le polygone
        polygon.setPoints(pointsList)

        // Configurer l'apparence du polygone
        polygon.outlinePaint.color = 0xFF0000FF.toInt() // Bleu
        polygon.outlinePaint.strokeWidth = 5f

        // Définir la couleur de remplissage avec transparence de 10%
        // 10% d'opacité = 1A en valeur hexadécimale
        polygon.fillPaint.color = 0x1A0000FF.toInt() // Bleu avec 10% d'opacité

        // S'assurer que le polygone est rendu en dessous des autres overlays
        mapView.overlays.add(0, polygon) // Index 0 pour l'ajouter au début de la liste

        // Rafraîchir la carte
        mapView.invalidate()
    }
}

@Composable
fun MapContent(
    viewModel: ViewModel_MapClients_App2FragID1,
    viewModelInitApp: ViewModelInitApp,
    clientEnCourDeVent: Long,
    onUpdateLongAppSetting: () -> Unit,
    onClear: () -> Unit,
    mapReloadTrigger: Int = 0,
) {
    val context = LocalContext.current
    val currentZoom by remember { mutableDoubleStateOf(18.2) }
    val mapView = remember { MapView(context) }
    var selectedMarker by remember { mutableStateOf<Marker?>(null) }
    var showMarkerDialog by remember { mutableStateOf(false) }
    val showMarkerDetails by remember { mutableStateOf(true) }
    var currentFilterMode by remember {
        mutableStateOf<ViewModel_MapClients_App2FragID1.VisibleClientsNow>(
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.CIBLE_ET_CELUIT_ON_A_PASSE_A_EUX
        )
    }

    var editingMarkerId by remember { mutableLongStateOf(0L) }
    var showEditMarkerMode by remember { mutableStateOf(false) }
    val activeTransactionId =
        viewModel.repo_0_0_HeadSQLRepositorys.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState().value
    val clientDataBaseSnapList = viewModel.bProto_ClientsDataBase

    // Handle active transaction by showing the relevant marker
    LaunchedEffect(viewModel.repo_0_0_HeadSQLRepositorys.repositorys_Model.activeVId_1_3_TransactionCommercial.collectAsState().value) {
        handleActiveTransaction(activeTransactionId, viewModel, mapView) { marker ->
            selectedMarker = marker
            showMarkerDialog = true
        }
    }

    // Ajouter un effet pour dessiner les secteurs une fois que la carte est prête
    LaunchedEffect(mapView) {
        // Attendre que la carte soit initialisée
        withContext(Dispatchers.Main) {
            // Récupérer les secteurs et leurs polygones
            val secteurDao = viewModel.appDatabase.secteurDeClientsDao()
            val polygonDao = viewModel.appDatabase.polygonGeoLimiteDaoDao()

            // Vérifier s'il y a des secteurs existants
            if (secteurDao.getCount() == 0) {
                // Si aucun secteur n'existe, en créer deux avec leurs polygones
                insert2SecteurEtPolygon(secteurDao, polygonDao)
            }

            // Récupérer tous les secteurs et points de polygone
            val allSecteurs = secteurDao.getAll()
            val allPolygonPoints = polygonDao.getAll()

            // Récupérer les informations structurées sur les secteurs et leurs polygones
            val secteurPolygonInfoList = getPolygenDeChaqueSecteur(secteurDao, polygonDao)

            // Ajouter les secteurs à la carte
            addSectorsToMap(mapView, secteurPolygonInfoList, allPolygonPoints, allSecteurs)
        }
    }

    // Initialize map with current location or default position
    LaunchedEffect(Unit) {
        initializeMapPosition(context, mapView, currentZoom)
    }

    // Configure OSMDroid and handle cleanup
    DisposableEffect(context, mapReloadTrigger) {
        configureOSMDroid(context, mapView)

        onDispose {
            cleanupMapResources(mapView, viewModel)
        }
    }

    // Main effect for updating markers on the map when data changes
    val updateTriggers = getMapUpdateTriggers(
        viewModel,
        clientDataBaseSnapList.size,
        clientEnCourDeVent,
        currentFilterMode,
        mapReloadTrigger
    )

    LaunchedEffect(updateTriggers) {
        updateMapMarkers(
            mapView,
            viewModel,
            clientDataBaseSnapList,
            clientEnCourDeVent,
            currentFilterMode,
            showMarkerDetails
        ) { marker ->
            selectedMarker = marker
            showMarkerDialog = true
        }
    }

    // Main UI layout with map and controls
    Box(modifier = Modifier.fillMaxSize()) {
        // Map view
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )

        // Center marker indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(ComposeColor.Red, CircleShape)   // Fixed: Using Compose Color
                .align(Alignment.Center)
        )

        // Main controls
        A_OptionsControlsButtons_Main()

        // Floating action buttons for map controls
        if (viewModelInitApp._paramatersAppsViewModelModel.fabsVisibility) {
            A_GlobalOptionsControlsFloatingActionButtons_FragId1(
                viewModel = viewModel,
                mapView = mapView,
                viewModelInitApp = viewModelInitApp,
                onClear = onClear,
                currentFilterMode = currentFilterMode,
                onFilterMarkers = {
                    handleFilterMarkersClick(mapView, currentFilterMode) { newMode ->
                        currentFilterMode = newMode
                    }
                },
                onPickFilter = {
                    currentFilterMode = it
                }
            )
        }

        // Marker edit mode overlay
        if (showEditMarkerMode) {
            MarkerEditModeOverlay(
                onCancel = {
                    showEditMarkerMode = false
                    editingMarkerId = 0L
                },
                onConfirm = {
                    handleMarkerPositionUpdate(
                        clientDataBaseSnapList,
                        editingMarkerId,
                        mapView,
                        viewModel
                    )
                    showEditMarkerMode = false
                    editingMarkerId = 0L
                }
            )
        }

        // Marker status dialog
        if (showMarkerDialog && selectedMarker != null) {
            MarkerStatusDialog(
                viewModel = viewModel,
                viewModelInitApp = viewModelInitApp,
                selectedMarker = selectedMarker,
                onDismiss = { showMarkerDialog = false },
                onUpdateLongAppSetting = onUpdateLongAppSetting,
                onClickToEditeMarquerPosition = { clientId ->
                    showMarkerDialog = false
                    editingMarkerId = clientId
                    showEditMarkerMode = true
                },
                onRemoveMark = { marker ->
                    marker?.let {
                        mapView.overlays.remove(it)
                        mapView.invalidate()
                    }
                }
            )
        }
    }
}
