package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.PolygonGeoLimite
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.SecteurDeClients
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.SecteurDeClientsPolygonGeoLimite
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.example.clientjetpack.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline

fun addToMapOsmdroid(
    mapView: MapView, secteurPolygonInfoList: List<SecteurDeClientsPolygonGeoLimite>,
    allPolygonPoints: List<PolygonGeoLimite>, allSecteurs: List<SecteurDeClients>,
) {     //<--
//TODO(1): pk la couleur et line de polygon ne s affiche pas
    Log.d("PolygonCreator", "Starting to add sectors to map")
    Log.d("PolygonCreator", "Number of sectors: ${allSecteurs.size}")
    Log.d("PolygonCreator", "Number of polygon points: ${allPolygonPoints.size}")
    Log.d("PolygonCreator", "Number of secteurPolygonInfo items: ${secteurPolygonInfoList.size}")

    // For each secteurPolygonInfo, create and add a polygon to the map
    secteurPolygonInfoList.forEach { secteurPolygonInfo ->
        Log.d("PolygonCreator", "Processing secteur info: ${secteurPolygonInfo.keyIDSecteurDeClients}")

        val secteurId = "SecteurDeClients\\.(\\d+)->.*"
            .toRegex().find(secteurPolygonInfo.keyIDSecteurDeClients)
            ?.groupValues?.get(1)?.toLongOrNull()

        Log.d("PolygonCreator", "Extracted secteurId: $secteurId")

        // Find the corresponding sector
        val secteur = allSecteurs.find { it.vid == secteurId }
        if (secteur == null) {
            Log.e("PolygonCreator", "Could not find sector with ID: $secteurId")
            return@forEach
        }

        Log.d("PolygonCreator", "Found sector: ${secteur.nom}, open: ${secteur.ouvert}, closed: ${secteur.polygonEstFerme}")

        // Get polygon points for this sector
        val polygonPoints = allPolygonPoints.filter { it.parentSecteurDeClientsId == secteurId }
        Log.d("PolygonCreator", "Number of points for this sector: ${polygonPoints.size}")

        // Skip if polygon is empty
        if (polygonPoints.isEmpty()) {
            Log.w("PolygonCreator", "No points found for sector ${secteur.nom}")
            return@forEach
        }

        // Convert PolygonGeoLimite points to GeoPoint
        val geoPoints = polygonPoints.map { point ->
            GeoPoint(
                point.aLatitude / 1E6, // Convert micro-degrees to degrees
                point.aLongitude / 1E6
            )
        }

        Log.d("PolygonCreator", "Converted ${geoPoints.size} points to GeoPoints")
        geoPoints.forEachIndexed { index, point ->
            Log.d("PolygonCreator", "Point $index: lat=${point.latitude}, lon=${point.longitude}")
        }

        // Get sector color using the couleur property directly
        val sectorColor = try {
            secteur.couleur.toColorInt()
            Log.d("PolygonCreator", "Successfully parsed color: ${secteur.couleur}")
        } catch (e: Exception) {
            Log.e("PolygonCreator", "Failed to parse color: ${secteur.couleur}, using default blue", e)
            Color.BLUE // Default to blue if parsing fails
        }

        if (secteur.ouvert) {
            Log.d("PolygonCreator", "Processing open sector: ${secteur.nom}")

            // FIX: Instead of drawing individual polylines, create a single polyline for all points
            // This ensures all line segments are part of the same visual object and appear connected
            if (geoPoints.size > 1) {
                val polyline = Polyline(mapView)
                polyline.setPoints(geoPoints)
                polyline.outlinePaint.color = sectorColor
                polyline.outlinePaint.strokeWidth = 10f  // Increased thickness for better visibility

                // Add polyline to map overlays
                // Make sure to add it before markers so it appears beneath them
                mapView.overlays.add(polyline)
                Log.d("PolygonCreator", "Added single polyline connecting all ${geoPoints.size} points")
            }

            // Add markers at each point
            geoPoints.forEachIndexed { index, point ->
                val marker = Marker(mapView)
                marker.position = point
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.icon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_location_dot)
                marker.setInfoWindow(null) // No info window

                mapView.overlays.add(marker)
                Log.d("PolygonCreator", "Added marker at point $index")
            }
        } else if (secteur.polygonEstFerme) {
            Log.d("PolygonCreator", "Processing closed sector: ${secteur.nom}")
            // Create a new polygon for closed sectors
            val polygon = Polygon(mapView)

            // Create a copy of the points
            val pointsList = ArrayList<GeoPoint>(geoPoints)

            // Close polygon by adding the first point at the end if needed
            if (pointsList.isNotEmpty() && pointsList.first() != pointsList.last()) {
                pointsList.add(pointsList.first())
                Log.d("PolygonCreator", "Added first point at the end to close the polygon")
            }

            // Configure polygon
            polygon.setPoints(pointsList)
            polygon.outlinePaint.color = sectorColor
            polygon.outlinePaint.strokeWidth = 5f

            // Set fill color with transparency
            val transparenceEnPourcent = 30

            // Calculate alpha value based on transparency percentage (1% = ~2.5/255 alpha)
            val alphaValue = (255 * transparenceEnPourcent / 100)

            // Create a color with the calculated alpha value (transparency)
            val fillColor = Color.argb(
                alphaValue,
                Color.red(sectorColor),
                Color.green(sectorColor),
                Color.blue(sectorColor)
            )

            polygon.fillPaint.color = fillColor

            // Make sure polygon is rendered below other overlays
            mapView.overlays.add(0, polygon) // Index 0 to add at start of list
            Log.d("PolygonCreator", "Added closed polygon with ${pointsList.size} points")
        } else {
            Log.w("PolygonCreator", "Sector ${secteur.nom} is neither open nor closed")
        }
    }

    // Refresh map
    Log.d("PolygonCreator", "Invalidating map to refresh display")
    mapView.invalidate()
}
