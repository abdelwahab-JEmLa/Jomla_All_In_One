package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.ViewModel.ViewModel_MapClients_App2FragID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._012_ComptsVendeurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._013_Acheteurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._01_VentsHistoriquesDataBase
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import android.util.Log
import io.realm.kotlin.ext.realmListOf
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun _01_Upsert(
    viewModel: ViewModel_MapClients_App2FragID1,
    ceComptVendeurInsertBonsAchatAuPeriodID: Long?,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    clientId: Long,
) {
    /**
     *  repo_01_VentsHistoriquesDataBase
     */
    val TAG = "MarkerStatusDialog"

    // 1. Get repository and data list
    Log.d(TAG, "Upsert: Starting upsert operation with periodID: $ceComptVendeurInsertBonsAchatAuPeriodID, clientId: $clientId")
    val repo_01_VentsHistoriquesDataBase = viewModel.repo_01_VentsHistoriquesDataBase
    Log.d(TAG, "Upsert: Repository retrieved")

    val _01_VentsHistoriquesDataBaseList = repo_01_VentsHistoriquesDataBase.modelDatasSnapList
    Log.d(TAG, "Upsert: Data list retrieved with ${_01_VentsHistoriquesDataBaseList.size} periods")

    // 2. Find or create period
    var period = _01_VentsHistoriquesDataBaseList.find {
        it.idPeriodDonAncienDataBase == ceComptVendeurInsertBonsAchatAuPeriodID
    }

    if (period == null) {
        // Create new period and add it to the repository
        Log.d(TAG, "Upsert: Period with ID $ceComptVendeurInsertBonsAchatAuPeriodID not found, creating new period")
        period = _01_VentsHistoriquesDataBase().apply {
            idPeriodDonAncienDataBase = ceComptVendeurInsertBonsAchatAuPeriodID ?: 0L
            // Set date and time values
            dateDebutDeCettePeriode = getCurrentDataString()
            tempDebutDeCettePeriode = getCurrentTimeString()
            tempCreationString = "$dateDebutDeCettePeriode-<$tempDebutDeCettePeriode"
            // Explicitly update keyID after setting vid
            keyID = "${ceComptVendeurInsertBonsAchatAuPeriodID}-($tempCreationString)"
            // Initialize empty vendor list
            child_012_Compts_Vendeurs = realmListOf()
        }
        _01_VentsHistoriquesDataBaseList.add(period)
        Log.d(TAG, "Upsert: New period created with vid: ${period.vid} and added to list")
    } else {
        Log.d(TAG, "Upsert: Found existing period with vid: ${period.vid}, keyID: ${period.keyID}")
    }

    // 3. Find or create vendeur in period
    var vendeur = period.child_012_Compts_Vendeurs.find {
        it.idCompt == repositorysModel.activeIdDe_1_5_Vendeur
    }

    if (vendeur == null) {
        // Create new vendeur and add it to the period
        Log.d(TAG, "Upsert: Vendeur with idCompt ${repositorysModel.activeIdDe_1_5_Vendeur} not found, creating new vendeur")
        vendeur = _012_ComptsVendeurs().apply {
            // Set all required fields
            vid = repositorysModel.activeIdDe_1_5_Vendeur
            idCompt = repositorysModel.activeIdDe_1_5_Vendeur
            startDesignation = "_012_ComptsVendeurs $idCompt"
            keyID = "${vid}=${startDesignation.replace(" ", "_")}"
            child_013_Acheteurs = realmListOf()
        }
        period.child_012_Compts_Vendeurs.add(vendeur)
        Log.d(TAG, "Upsert: New vendeur created with vid: ${vendeur.vid} and added to period")
    } else {
        Log.d(TAG, "Upsert: Found existing vendeur with vid: ${vendeur.vid}, idCompt: ${vendeur.idCompt}")
    }

    // 4. Find or create acheteur in vendeur
    var acheteur = vendeur.child_013_Acheteurs.find {
        it.idClient == clientId
    }

    if (acheteur == null) {
        // Create new acheteur and add it to the vendeur
        Log.d(TAG, "Upsert: Acheteur with clientId $clientId not found, creating new acheteur")
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"))
        val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        val creationTimestamp = "${currentDate}(${currentTime})"

        acheteur = _013_Acheteurs().apply {
            // Set all required fields
            vid = System.currentTimeMillis()
            idClient = clientId
            startDesignation = "_013_Acheteurs for client $idClient"
            tempCreationString = creationTimestamp
            keyID = "${vid}->${startDesignation.replace(" ", "_")}"
            child_14Produits = realmListOf()
        }

        Log.d(
            TAG,
            "Upsert: Adding new acheteur with id: ${acheteur.vid} for client: $clientId to vendeur: ${vendeur.vid}"
        )
        vendeur.child_013_Acheteurs.add(acheteur)
        Log.d(
            TAG,
            "Upsert: After adding: vendeur has ${vendeur.child_013_Acheteurs.size} acheteurs"
        )
    } else {
        Log.d(TAG, "Upsert: Found existing acheteur with vid: ${acheteur.vid}, idClient: ${acheteur.idClient}")
    }

    // 5. Perform upsert operation to save changes
    Log.d(TAG, "Upsert: Saving changes to database for period with vid: ${period.vid}")
    repo_01_VentsHistoriquesDataBase.upsert_01_PeriodesVentEtReturnItVid(period)
    Log.d(TAG, "Upsert: Operation completed successfully")
}

// Helper functions for date and time
private fun getCurrentDataString(): String = LocalDate.now().format(
    DateTimeFormatter.ofPattern("yyyy_MM_dd")
)

private fun getCurrentTimeString(): String = LocalTime.now().format(
    DateTimeFormatter.ofPattern("HH:mm")
)
