package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views

import android.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import kotlin.math.cos
import kotlin.math.sin

// First fix: Add couleur property to SecteurDeClients class
@Entity
data class SecteurDeClients(
    @PrimaryKey(autoGenerate = true)
    val vid: Long,
    val nom: String = "Tamaris",
    val polygonEstFerme: Boolean = false,
    val couleur: String = "#ff0000ff",
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
    // Premier secteur - "Tamaris" with blue color
    val secteur1 = SecteurDeClients(
        vid = 0, // Auto-generated
        nom = "Tamaris",
        polygonEstFerme = true,
        couleur = "#ff0000ff" // Blue color as string
    )

    // Deuxième secteur - "Plage" with green color
    val secteur2 = SecteurDeClients(
        vid = 0, // Auto-generated
        nom = "Plage",
        polygonEstFerme = true,
        couleur = "#ff00ff00" // Green color as string
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

fun addSectorsToMap(
    mapView: MapView, secteurPolygonInfoList: List<SecteurDeClientsPolygonGeoLimite>,
    allPolygonPoints: List<PolygonGeoLimite>, allSecteurs: List<SecteurDeClients>,
) {
    // For each secteurPolygonInfo, create and add a polygon to the map
    secteurPolygonInfoList.forEach { secteurPolygonInfo ->
        // Extract secteur ID from key
        val secteurIdMatch = "SecteurDeClients\\.(\\d+)->.*"
            .toRegex().find(secteurPolygonInfo.keyIDSecteurDeClients)

        val secteurId = secteurIdMatch?.groupValues?.get(1)?.toLongOrNull() ?: return@forEach

        // Find the corresponding sector
        val secteur = allSecteurs.find { it.vid == secteurId } ?: return@forEach

        // Get polygon points for this sector
        val polygonPoints = allPolygonPoints.filter { it.parentSecteurDeClientsId == secteurId }

        // Create a new polygon for this sector
        val polygon = Polygon(mapView)

        // Convert PolygonGeoLimite points to GeoPoint
        val geoPoints = polygonPoints.map { point ->
            GeoPoint(
                point.aLatitude / 1E6, // Convert micro-degrees to degrees
                point.aLongitude / 1E6
            )
        }

        // Skip if polygon is empty
        if (geoPoints.isEmpty()) return@forEach

        // Close polygon if needed
        val pointsList = ArrayList<GeoPoint>(geoPoints)
        if (secteur.polygonEstFerme && pointsList.isNotEmpty() &&
            pointsList.first() != pointsList.last()
        ) {
            pointsList.add(pointsList.first())
        }

        // Configure polygon
        polygon.setPoints(pointsList)

        // CHANGE THIS LINE: Get sector color using the couleur property directly
        val sectorColor = try { secteur.couleur.toColorInt() } catch (e: Exception) {
            Color.BLUE // Default to blue if parsing fails
        }

        // Configure polygon appearance
        polygon.outlinePaint.color = sectorColor
        polygon.outlinePaint.strokeWidth = 5f

        // Set fill color with transparency
        val transparenceEnPourcent = 4

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

        // Refresh map
        mapView.invalidate()
    }
}
