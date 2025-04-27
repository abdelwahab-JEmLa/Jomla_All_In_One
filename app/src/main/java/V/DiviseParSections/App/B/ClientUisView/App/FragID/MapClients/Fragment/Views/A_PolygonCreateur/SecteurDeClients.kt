package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.osmdroid.util.GeoPoint
import kotlin.math.cos
import kotlin.math.sin

@Entity
data class SecteurDeClients(
    @PrimaryKey(autoGenerate = true)
    val vid: Long,
    val nom: String = "Tamaris",
    val ouvert: Boolean = false,
    val polygonEstFerme: Boolean = false,
    val couleur: String = "#ff0000ff",
)
@Entity
data class PolygonGeoLimite(
    @PrimaryKey(autoGenerate = true)
    val vid: Long=0,

    val parentSecteurDeClientsId: Long=0,
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

