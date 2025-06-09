package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.E1SecteurDeClients

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class E1SecteurDeClients(
    @PrimaryKey(autoGenerate = true)
    val vid: Long,

    //Infos De Base
    val nom: String = "Tamaris",
    val couleur: String = "0xff0000ff",

    //Etates Mutable
    val ouvert: Boolean = false,
    val ciblePourCettePeriodDeVent: Boolean = false,
    val sonPolygonOnModeDessine: Boolean = false,
)

/* // Effect to update sectors on map - now responds to viewModel.mapReloadTigger changes
LaunchedEffect(mapView, sectorMapReloadTrigger) {
// Execute all database operations on Dispatchers.IO
withContext(Dispatchers.IO) {
// Get sector and polygon DAOs
val polygonDao = viewModel.appDatabase.polygonGeoLimiteDaoDao()

// Check if there are existing sectors
if (uiState.e1SecteurDeClientsList.isEmpty()) {
  // If no sectors exist, create two with their polygons
  insert2SecteurEtPolygon(
      viewModel,
      polygonDao = polygonDao
  )
}

// Get all sectors and polygon points
val allSecteurs = uiState.e1SecteurDeClientsList
val allPolygonPoints = polygonDao.getAll()

// Get structured information about sectors and their polygons

val secteurPolygonInfoList = getNoSqlDisplayer(
  uiState = uiState,
  viewModel = viewModel,
  polygonDao = polygonDao
)

// Back to main thread to update UI
withContext(Dispatchers.Main) {
  // Clear existing sector polygons first to prevent duplicates
  val sectorsToRemove = mapView.overlays.take(allSecteurs.size)
  mapView.overlays.removeAll(sectorsToRemove)

  // Add sectors to map
  addToMapOsmdroid(mapView, secteurPolygonInfoList, allPolygonPoints, allSecteurs)
}
}
}          */
