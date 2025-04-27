package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.SecteurDeClientsPolygonGeoLimite
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Repository.PolygonGeoLimiteDao
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Repository.SecteurDeClientsDao
import android.util.Log

suspend fun getPolygenDeChaqueSecteur(
    secteurDao: SecteurDeClientsDao,
    polygonDao: PolygonGeoLimiteDao,
): List<SecteurDeClientsPolygonGeoLimite> {
    // Récupérer tous les secteurs
    val allSecteurs = secteurDao.getAll()
    Log.d("PolygonCreator", "Retrieved ${allSecteurs.size} sectors from database")

    // Récupérer tous les points de polygone
    val allPolygonPoints = polygonDao.getAll()
    Log.d("PolygonCreator", "Retrieved ${allPolygonPoints.size} polygon points from database")

    // Créer une liste de SecteurDeClientsPolygonGeoLimite
    val result = mutableListOf<SecteurDeClientsPolygonGeoLimite>()

    allSecteurs.forEach { secteur ->
        val secteurPoints = allPolygonPoints.filter {
            it.parentSecteurDeClientsId == secteur.vid
        }

        Log.d("PolygonCreator", "Sector ${secteur.nom} (ID: ${secteur.vid}) has ${secteurPoints.size} points")

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

        Log.d("PolygonCreator", "Added SecteurDeClientsPolygonGeoLimite for sector: $secteurKey with ${pointKeys.size} points")
    }

    return result
}
