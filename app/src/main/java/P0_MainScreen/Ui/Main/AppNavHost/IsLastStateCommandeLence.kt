package P0_MainScreen.Ui.Main.AppNavHost

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._013_Acheteurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._14A_HistoriuesDeCetteJour
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog._01_Upsert_013_Acheteurs
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

private const val TAG_ORDER_STATE = "OrderStateTracker"

// Extension function to check if an acheteur's last state is COMMANDE_LENCE
 fun _013_Acheteurs.isLastStateCommandeLence(): Boolean {
    // Add logging to track function execution
    Log.d(TAG_ORDER_STATE, "Checking last state for client ID: $idClient, name: $nomClient")

    if (child_14A_HistoriquesDeCetteJour.isEmpty()) {
        Log.d(TAG_ORDER_STATE, "No historical entries found for client $idClient")
        return false
    }

    // Log the number of historical entries
    Log.d(
        TAG_ORDER_STATE,
        "Found ${child_14A_HistoriquesDeCetteJour.size} historical entries for client $idClient"
    )

    // Sort by date and time to get the most recent entry
    val sortedHistoriques = child_14A_HistoriquesDeCetteJour.sortedWith(
        compareByDescending<_14A_HistoriuesDeCetteJour> { it.dateCreationStr }
            .thenByDescending { it.tempCreationStr }
    )

    // Get the most recent state and log it
    val lastHistorique = sortedHistoriques.firstOrNull()
    val lastState = lastHistorique?.etate

    Log.d(
        TAG_ORDER_STATE, "Last state for client $idClient is: ${lastState?.name ?: "NULL"}, " +
                "date: ${lastHistorique?.dateCreationStr ?: "N/A"}, " +
                "time: ${lastHistorique?.tempCreationStr ?: "N/A"}"
    )

    // Check if the most recent state is COMMANDE_LENCE
    return lastState == _14A_HistoriuesDeCetteJour.Etate.COMMANDE_LENCE
}

// Update acheteur state to ACHAT_TERMINE
 fun updateAcheteurToAchatTermine(
    viewModelInitApp: ViewModelInitApp,
    clientId: Long,
    repositorysModel: _0_0_HeadOfRepositorys_Model
) {
    val ceComptVendeurInsertBonsAchatAuPeriodID =
        repositorysModel.repository_1_5_Vendeur.modelDatasSnapList
            .find { it.vid == repositorysModel.activeIdDe_1_5_Vendeur }
            ?.ceComptVendeurInsertBonsAchatAuPeriodID

    _01_Upsert_013_Acheteurs(
        ceComptVendeurInsertBonsAchatAuPeriodID = ceComptVendeurInsertBonsAchatAuPeriodID,
        repositorysModel = repositorysModel,
        clientId = clientId,
        historiqueState = _14A_HistoriuesDeCetteJour.Etate.ACHAT_TERMINE,
        nom = viewModelInitApp.clientDataBaseSnapList.find { it.id == clientId }?.nom ?: "",
        repo_01_VentsHistoriquesDataBase = viewModelInitApp.repo_01_VentsHistoriquesDataBase_Repository
    )
}

// Order completion dialog component
@Composable
 fun OrderCompletionDialog(
    clientId: Long,
    clientName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete Order") },
        text = {
            Text("Mark the order for client $clientName (ID: $clientId) as completed?")
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Complete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Class to manage order state checks and prevent redundant checks
class OrderStateManager(
     val repo_01_VentsHistoriquesDataBase_Repository: _01_VentsHistoriquesDataBase_Repository, // Update with actual type
     val repositorysModel: _0_0_HeadOfRepositorys_Model,
     val currentClientId: () -> Long,
     val onShowDialog: () -> Unit
) {
     var checkedOrderStatus = false

    fun hasCheckedOrderStatus(): Boolean = checkedOrderStatus

    fun resetOrderStatusCheck() {
        checkedOrderStatus = false
    }

    fun checkOrderStatus() {
        val clientId = currentClientId()
        if (clientId <= 0) {
            Log.d(TAG_ORDER_STATE, "No current client selected (ID is 0)")
            checkedOrderStatus = true
            return
        }

        Log.d(TAG_ORDER_STATE, "Checking for active order for client ID: $clientId")

        val period = repo_01_VentsHistoriquesDataBase_Repository
            .modelDatasSnapList
            .firstOrNull()

        if (period == null) {
            Log.d(TAG_ORDER_STATE, "No period found in database")
            checkedOrderStatus = true
            return
        }

        Log.d(TAG_ORDER_STATE, "Found period with ID: ${period.idPeriodDonAncienDataBase}")

        val vendeur = period.child_012_Compts_Vendeurs?.firstOrNull {
            it.idCompt == repositorysModel.activeIdDe_1_5_Vendeur
        }

        if (vendeur == null) {
            Log.d(
                TAG_ORDER_STATE,
                "No vendeur found with ID: ${repositorysModel.activeIdDe_1_5_Vendeur}"
            )
            checkedOrderStatus = true
            return
        }

        Log.d(TAG_ORDER_STATE, "Found vendeur with ID: ${vendeur.idCompt}")

        val acheteur = vendeur.child_013_Acheteurs?.firstOrNull {
            it.idClient == clientId
        }

        if (acheteur == null) {
            Log.d(TAG_ORDER_STATE, "No acheteur found with ID: $clientId")
            checkedOrderStatus = true
            return
        }

        Log.d(
            TAG_ORDER_STATE,
            "Checking last state for acheteur: ${acheteur.nomClient} (ID: ${acheteur.idClient})"
        )

        if (acheteur.isLastStateCommandeLence()) {
            Log.d(
                TAG_ORDER_STATE,
                "Acheteur ${acheteur.nomClient} has COMMANDE_LENCE state, showing dialog"
            )
            onShowDialog()
        } else {
            Log.d(
                TAG_ORDER_STATE,
                "Acheteur ${acheteur.nomClient} does not have COMMANDE_LENCE state"
            )
        }

        // Mark as checked so we don't recheck
        checkedOrderStatus = true
    }
}
