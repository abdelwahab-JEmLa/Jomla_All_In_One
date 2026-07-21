package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Functions

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LATITUDE
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.DEFAULT_LONGITUDE
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.Utils.getCurrentLocation
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

val CARTO_DB_VOYAGER = XYTileSource(
    "CartoDB_Voyager",
    0, 20, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://c.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://d.basemaps.cartocdn.com/rastertiles/voyager/"
    ),
    "© OpenStreetMap contributors, © CARTO"
)

val ESRI_SATELLITE = XYTileSource(
    "ESRI_WorldImagery",
    0, 19, 256, ".jpg",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/"),
    "Tiles © Esri"
)

val CARTO_DB_DARK = XYTileSource(
    "CartoDB_DarkMatter",
    0, 20, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/rastertiles/dark_all/",
        "https://b.basemaps.cartocdn.com/rastertiles/dark_all/",
        "https://c.basemaps.cartocdn.com/rastertiles/dark_all/",
        "https://d.basemaps.cartocdn.com/rastertiles/dark_all/"
    ),
    "© OpenStreetMap contributors, © CARTO"
)

val OPENTOPOMAP = XYTileSource(
    "OpenTopoMap",
    0, 17, 256, ".png",
    arrayOf(
        "https://a.tile.opentopomap.org/",
        "https://b.tile.opentopomap.org/",
        "https://c.tile.opentopomap.org/"
    ),
    "Map data: © OpenStreetMap contributors, SRTM | Map style: © OpenTopoMap"
)

val WIKIMEDIA_MAPS = XYTileSource(
    "WikimediaMaps",
    1, 19, 256, ".png",
    arrayOf("https://maps.wikimedia.org/osm-intl/"),
    "Wikimedia maps | Map data © OpenStreetMap contributors"
)

val USGS_IMAGERY_TOPO = XYTileSource(
    "USGS_ImageryTopo",
    0, 16, 256, "",
    arrayOf("https://basemap.nationalmap.gov/arcgis/rest/services/USGSImageryTopo/MapServer/tile/"),
    "USGS The National Map"
)

val CARTO_DB_POSITRON = XYTileSource(
    "CartoDB_Positron",
    0, 20, 256, ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/rastertiles/light_all/",
        "https://b.basemaps.cartocdn.com/rastertiles/light_all/",
        "https://c.basemaps.cartocdn.com/rastertiles/light_all/",
        "https://d.basemaps.cartocdn.com/rastertiles/light_all/"
    ),
    "© OpenStreetMap contributors, © CARTO"
)

val ESRI_STREET = XYTileSource(
    "ESRI_WorldStreetMap",
    0, 19, 256, ".jpg",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/"),
    "Tiles © Esri"
)

val ESRI_TOPO = XYTileSource(
    "ESRI_WorldTopoMap",
    0, 19, 256, ".jpg",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/"),
    "Tiles © Esri"
)

val OSM_HOT = XYTileSource(
    "OSM_HOT",
    0, 19, 256, ".png",
    arrayOf(
        "https://a.tile.openstreetmap.fr/hot/",
        "https://b.tile.openstreetmap.fr/hot/",
        "https://c.tile.openstreetmap.fr/hot/"
    ),
    "© OpenStreetMap contributors, Tiles style by Humanitarian OpenStreetMap Team"
)

enum class MapSource { VOYAGER, SATELLITE, OSM, DARK, TOPO, WIKIMEDIA, USGS, POSITRON, ESRI_STREET, ESRI_TOPO, OSM_HOT }

fun changeMapSource(mapView: MapView, source: MapSource) {
    val tileSource = when (source) {
        MapSource.TOPO -> OPENTOPOMAP
        MapSource.VOYAGER -> CARTO_DB_VOYAGER
        MapSource.SATELLITE -> ESRI_SATELLITE
        MapSource.OSM -> TileSourceFactory.MAPNIK
        MapSource.DARK -> CARTO_DB_DARK
        MapSource.WIKIMEDIA -> WIKIMEDIA_MAPS
        MapSource.USGS -> USGS_IMAGERY_TOPO
        MapSource.POSITRON -> CARTO_DB_POSITRON
        MapSource.ESRI_STREET -> ESRI_STREET
        MapSource.ESRI_TOPO -> ESRI_TOPO
        MapSource.OSM_HOT -> OSM_HOT
    }
    mapView.setTileSource(tileSource)
    mapView.invalidate()
}

fun cleanupMapResources(mapView: MapView, viewModel: MapClientsViewModel) {
    mapView.onDetach()
    mapView.overlays.clear()
    viewModel.cancelActiveOperations()
}
suspend fun initializeMapPosition(
    context: Context,
    mapView: MapView,
    currentZoom: Double,
    shouldCenterOnLocation: Boolean = true
) {
    val location = getCurrentLocation(context)
    val geoPoint = if (location != null) {
        GeoPoint(location.latitude, location.longitude)
    } else {
        GeoPoint(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }
    mapView.apply {
        setMultiTouchControls(true)
        setTileSource(OPENTOPOMAP)
        controller.setZoom(currentZoom)
        if (shouldCenterOnLocation) {
            withContext(Dispatchers.Main) {
                controller.animateTo(geoPoint)
            }
        }
    }
}

