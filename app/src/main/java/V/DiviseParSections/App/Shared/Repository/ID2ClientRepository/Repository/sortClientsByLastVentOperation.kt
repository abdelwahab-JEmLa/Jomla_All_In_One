package V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository

import EntreApps.Shared.Models.M2Client
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter

/**
 * Trie une liste de clients par leur dernière opération de vente (M10OperationVentCouleur)
 * Les clients avec les opérations les plus récentes apparaissent en premier
 */
fun sortClientsByLastVentOperation(
    clients: List<M2Client>,
    repositorysMainGetter: RepositorysMainGetter
): List<M2Client> {
    return clients.sortedByDescending { client ->
        getLastVentOperationTimestamp(client, repositorysMainGetter)
    }
}

/**
 * Récupère le timestamp de la dernière opération de vente pour un client donné
 * @return Le timestamp de la dernière synchronisation ou 0L si aucune opération n'existe
 */
private fun getLastVentOperationTimestamp(
    client: M2Client,
    repositorysMainGetter: RepositorysMainGetter
): Long {
    // Get the last M8BonVent for this client
    val lastBonVent = repositorysMainGetter.get_Last_M8BonVent_Par_M2Client(client)
        ?: return 0L

    // Find all M10OperationVentCouleur for this BonVent
    val ventOperations = repositorysMainGetter.repo10OperationVentCouleur.datasValue
        .filter { it.parent_M8BonVent_KeyId == lastBonVent.keyID }

    // Get the most recent dernierTimeTampsSynchronisationAvecFireBase
    return ventOperations.maxOfOrNull { it.dernierTimeTampsSynchronisationAvecFireBase } ?: 0L
}
