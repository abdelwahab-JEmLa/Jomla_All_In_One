package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Archive.A_PolygonCreateur.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Archive.A_PolygonCreateur.E1SecteurDeClients.E1SecteurDeClients
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Archive.A_PolygonCreateur.Models.NoSqlSecteurDeClientsPolygonGeoLimite
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Archive.A_PolygonCreateur.Models.PolygonGeoLimite
import android.graphics.Color
import android.graphics.Paint
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
    mapView: MapView, secteurPolygonInfoList: List<NoSqlSecteurDeClientsPolygonGeoLimite>,
    allPolygonPoints: List<PolygonGeoLimite>, allSecteurs: List<E1SecteurDeClients>,
) {
    Log.d("PolygonCreator", "Starting to upsert sectors to map")
    Log.d("PolygonCreator", "Number of sectors: ${allSecteurs.size}")
    Log.d("PolygonCreator", "Number of polygon points: ${allPolygonPoints.size}")
    Log.d("PolygonCreator", "Number of secteurPolygonInfo items: ${secteurPolygonInfoList.size}")

    // For each secteurPolygonInfo, create and upsert add_New polygon to the map
    secteurPolygonInfoList.forEach { secteurPolygonInfo ->
        Log.d("PolygonCreator", "Processing secteur info: ${secteurPolygonInfo.keyIDSecteurDeClients}")

        val secteurId = "E1SecteurDeClients\\.(\\d+)->.*"
            .toRegex().find(secteurPolygonInfo.keyIDSecteurDeClients)
            ?.groupValues?.get(1)?.toLongOrNull()

        Log.d("PolygonCreator", "Extracted secteurId: $secteurId")

        // Find the corresponding sector
        val secteur = allSecteurs.find { it.vid == secteurId }
        if (secteur == null) {
            Log.e("PolygonCreator", "Could not find sector with ID: $secteurId")
            return@forEach
        }

        Log.d("PolygonCreator", "Found sector: ${secteur.nom}, openTransaction: ${secteur.ouvert}, closed: ${secteur.sonPolygonOnModeDessine}")

        // RepositorysMainGetter polygon points for this sector
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

        // FIX: Correctly parse hexadecimal color string
        val sectorColor = try {
            // Fix for color parsing - convert from hex format "0xAARRGGBB" to int
            val colorStr = secteur.couleur

            if (colorStr.startsWith("0x")) {
                // Parse as Long first to handle unsigned integers properly, then convert to Int
                val colorLong = colorStr.substring(2).toLong(16)
                colorLong.toInt()
            } else {
                // Try the standard "#RRGGBB" or "#AARRGGBB" format
                colorStr.toColorInt()
            }
        } catch (e: Exception) {
            Log.e("PolygonCreator", "Failed to parse color: ${secteur.couleur}, using default blue", e)
            Color.BLUE
        }

        Log.d("PolygonCreator", "Using color for sector ${secteur.nom}: ${Integer.toHexString(sectorColor)}")

        if (secteur.ouvert) {
            Log.d("PolygonCreator", "Processing openTransaction sector: ${secteur.nom}")

            // Create add_New single polyline for all points
            if (geoPoints.size > 1) {
                val polyline = Polyline(mapView)
                polyline.setPoints(geoPoints)

                // Improve polyline visibility
                polyline.outlinePaint.color = sectorColor
                polyline.outlinePaint.strokeWidth = 12f  // Thicker line
                polyline.outlinePaint.strokeCap = Paint.Cap.ROUND
                polyline.outlinePaint.isAntiAlias = true
                polyline.outlinePaint.style = Paint.Style.STROKE

                // Add polyline to map overlays
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
        } else if (secteur.sonPolygonOnModeDessine) {
            Log.d("PolygonCreator", "Processing closed sector: ${secteur.nom}")

            // Create add_New new polygon for closed sectors
            val polygon = Polygon(mapView)

            // Create add_New copy of the points
            val pointsList = ArrayList<GeoPoint>(geoPoints)

            // Close polygon by adding the first point at the end if needed
            if (pointsList.isNotEmpty() && pointsList.first() != pointsList.last()) {
                pointsList.add(pointsList.first())
                Log.d("PolygonCreator", "Added first point at the end to close the polygon")
            }

            // Configure polygon with improved styling
            polygon.setPoints(pointsList)

            // Improved outline visibility
            polygon.outlinePaint.color = sectorColor
            polygon.outlinePaint.strokeWidth = 8f
            polygon.outlinePaint.strokeCap = Paint.Cap.ROUND
            polygon.outlinePaint.strokeJoin = Paint.Join.ROUND
            polygon.outlinePaint.isAntiAlias = true


            val transparenceEnPourcent = 93   // Pourcentage de transparence (0-100)
            val alphaValue = (255 * (100 - transparenceEnPourcent) / 100).toInt()

            val fillColor = Color.argb(
                alphaValue,
                Color.red(sectorColor),
                Color.green(sectorColor),
                Color.blue(sectorColor)
            )



            Log.d("PolygonCreator", "Polygon fill color: ${Integer.toHexString(fillColor)}")

            // RepositorysMainSetter fill color and ensure it's visible
            polygon.fillPaint.color = fillColor
            polygon.fillPaint.style = Paint.Style.FILL

            // Clear any previous polygons for this sector first
            mapView.overlays.removeAll { it is Polygon && it.title == secteur.nom }

            // RepositorysMainSetter title for identification
            polygon.title = secteur.nom

            // Add polygon at the beginning of overlays for proper z-order
            mapView.overlays.add(0, polygon)
            Log.d("PolygonCreator", "Added closed polygon with ${pointsList.size} points")
        } else {
            Log.w("PolygonCreator", "Sector ${secteur.nom} is neither openTransaction nor closed")
        }
    }

    // Refresh map
    Log.d("PolygonCreator", "Invalidating map to refresh display")
    mapView.invalidate()
}
