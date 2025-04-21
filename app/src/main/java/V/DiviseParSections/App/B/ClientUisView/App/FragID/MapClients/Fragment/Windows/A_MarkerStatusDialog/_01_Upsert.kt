package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog


import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._013_Acheteurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import io.realm.kotlin.ext.realmListOf

fun _01_Upsert(
    viewModel: ViewModel_MapClients_App2FragID1,
    ceComptVendeurInsertBonsAchatAuPeriodID: Long?,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    clientId: Long,
) {
    // Get repository for periods
    val repo_01_VentsHistoriquesDataBase = viewModel.repo_01_VentsHistoriquesDataBase
    val _01_VentsHistoriquesDataBaseList = repo_01_VentsHistoriquesDataBase.modelDatasSnapList

    // Find or create period
    var period = _01_VentsHistoriquesDataBaseList.find {
        it.idPeriodDonAncienDataBase == ceComptVendeurInsertBonsAchatAuPeriodID
    }

    if (period == null) {
        // Create new period and add it to the repository
        period = _01_VentsHistoriquesDataBase().apply {
            idPeriodDonAncienDataBase = ceComptVendeurInsertBonsAchatAuPeriodID ?: 0L

            val currentDateTime = _01_VentsHistoriquesDataBase.getCurrentDataTimeString()

            tempCreationStr = currentDateTime
            // Update keyID with proper format
            fireBaseKeyID = "${idPeriodDonAncienDataBase}-($tempCreationStr)"
            // Initialize empty vendor list
            child_012_Compts_Vendeurs = realmListOf()
        }
        _01_VentsHistoriquesDataBaseList.add(period)
    }

    // Find or create vendeur in period
    var vendeur = period.child_012_Compts_Vendeurs.find {
        it.idCompt == repositorysModel.activeIdDe_1_5_Vendeur
    }

    if (vendeur == null) {
        // Create new vendeur and add it to the period
        vendeur = _012_ComptsVendeurs().apply {
            idCompt = repositorysModel.activeIdDe_1_5_Vendeur
            startDesignation = "_012_ComptsVendeurs $idCompt"
            fireBaseKeyID = "${idCompt}=${startDesignation.replace(" ", "_")}"
            child_013_Acheteurs = realmListOf()
        }
        period.child_012_Compts_Vendeurs.add(vendeur)
    }

    // Find or create acheteur in vendeur
    var acheteur = vendeur.child_013_Acheteurs.find {
        it.idClient == clientId
    }

    if (acheteur == null) {
        // Create new acheteur and add it to the vendeur
        acheteur = _013_Acheteurs().apply {
            idClient = clientId
            startDesignation = "_013_Acheteurs for client $idClient"
            tempDateCreationStr = _01_VentsHistoriquesDataBase.getCurrentDataTimeString()
            fireBaseKeyID = "${System.currentTimeMillis()}->${startDesignation.replace(" ", "_")}"
            child_14Produits = realmListOf()
        }
        vendeur.child_013_Acheteurs.add(acheteur)
    }

    // Save changes to database
    repo_01_VentsHistoriquesDataBase.upsert_01_PeriodesVentEtReturnItVid(period)
}
