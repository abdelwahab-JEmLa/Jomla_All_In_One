package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.View
       /*
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.UiStateSec9Frag1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.MapClientsViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models.NoSqlSecteurDeClientsPolygonGeoLimite
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Repository.PolygonGeoLimiteDao
import android.util.Log

suspend fun getNoSqlDisplayer(
    polygonDao: PolygonGeoLimiteDao,
    uiState: UiStateSec9Frag1,
    viewModel: MapClientsViewModel,
): List<NoSqlSecteurDeClientsPolygonGeoLimite> {
    // Récupérer tous les secteurs
    val allSecteurs = uiState.e1SecteurDeClientsList
    Log.d("PolygonCreator", "Retrieved ${allSecteurs.size} sectors from database")

    // Récupérer tous les points de polygone
    val allPolygonPoints = polygonDao.getAll()
    Log.d("PolygonCreator", "Retrieved ${allPolygonPoints.size} polygon points from database")

    // Créer une liste de NoSqlSecteurDeClientsPolygonGeoLimite
    val result = mutableListOf<NoSqlSecteurDeClientsPolygonGeoLimite>()

    allSecteurs.forEach { secteur ->
        val secteurPoints = allPolygonPoints.filter {
            it.parentSecteurDeClientsId == secteur.vid
        }

        Log.d("PolygonCreator", "Sector ${secteur.nom} (ID: ${secteur.vid}) has ${secteurPoints.size} points")

        // Créer la clé pour ce secteur
        val secteurKey = "E1SecteurDeClients.${secteur.vid}->(${secteur.nom})"

        // Créer la liste de chaînes de caractères pour les points du polygone
        val pointKeys = secteurPoints.map { point ->
            "${point.vid}->(${point.parentE1SecteurDeClientsKey})"
        }

        // Ajouter le NoSqlSecteurDeClientsPolygonGeoLimite à la liste résultat
        result.add(
            NoSqlSecteurDeClientsPolygonGeoLimite(
                keyIDSecteurDeClients = secteurKey,
                listPolygonGeoLimite = pointKeys
            )
        )

        Log.d("PolygonCreator", "Added NoSqlSecteurDeClientsPolygonGeoLimite for sector: $secteurKey with ${pointKeys.size} points")
    }

    return result
}
                         */
