package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel

import Z_CodePartageEntreApps.Model.B_ClientDataBase.B_ClientDataBase

/**
 * Gère le filtrage des clients sur la carte
 */
class FilterClientMarkesManager {
    // Type de filtre simple pour les clients
    enum class FilterType {
        ALL,                // Tous les clients
        CIBLE,              // Clients ciblés
        TEMPORARY,          // Clients temporaires
        ACTIVE              // Clients actifs
    }

    // Filtre actuel
    private var currentFilter = FilterType.ALL

    // Définir un nouveau filtre
    fun setFilter(filter: FilterType) {
        currentFilter = filter
    }

    // Convertir depuis VisibleClientsNow
    fun setFilterFromVisibleMode(mode: ViewModel_MapClients_App2FragID1.VisibleClientsNow) {
        currentFilter = when(mode) {
            ViewModel_MapClients_App2FragID1.VisibleClientsNow.AFFICHE_CIBLE_POUR_VENDEUR -> FilterType.CIBLE
            else -> FilterType.ALL
        }
    }

    // Appliquer le filtre à une liste de clients
    fun filterClients(clients: List<B_ClientDataBase>): List<B_ClientDataBase> {
        return when(currentFilter) {
            FilterType.ALL -> clients

            FilterType.CIBLE -> clients.filter {
                it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.Cible
            }

            FilterType.TEMPORARY -> clients.filter {
                it.cUnClientTemporaire
            }

            FilterType.ACTIVE -> clients.filter {
                it.actuelleEtat == B_ClientDataBase.DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT
            }
        }
    }

    // Filtrer par jour de la semaine
    fun filterByDays(clients: List<B_ClientDataBase>, days: List<String>): List<B_ClientDataBase> {
        if (days.isEmpty()) return clients

        // Simple exemple: utiliser l'ID du client modulo 7 pour associer à un jour
        val dayNames = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")

        return clients.filter { client ->
            val dayIndex = (client.id % 7).toInt()
            days.contains(dayNames[dayIndex])
        }
    }

    // Exemple d'utilisation
    fun getFilterExample(): String {
        return """
            // Exemple 1: Filtrer par type de magasin
            filterManager.setFilter(FilterType.ATAY_CLIENTS)
            val filteredClients = filterManager.filterClients(allClients)
            
            // Exemple 2: Filtrer par jour de la semaine
            val clientsFilteredByDay = filterManager.filterByDays(
                allClients, 
                listOf("الإثنين", "الثلاثاء")  // Lundi et Mardi
            )
            
            // Exemple 3: Utiliser avec VisibleClientsNow
            val visibleMode = ViewModel_MapClients_App2FragID1.VisibleClientsNow.showAtayClients
            filterManager.setFilterFromVisibleMode(visibleMode)
            val filteredByMode = filterManager.filterClients(allClients)
        """
    }
}
