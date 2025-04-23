package P0_MainScreen.Ui.Main.AppNavHost

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._013_Acheteurs
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._14A_HistoriuesDeCetteJour
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog._01_Upsert_013_Acheteurs
import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

fun _013_Acheteurs.isLastStateCommandeLence(): Boolean {
    if (child_14A_HistoriquesDeCetteJour.isEmpty()) {
        return false
    }

    val sortedHistoriques = child_14A_HistoriquesDeCetteJour.sortedWith(
        compareByDescending<_14A_HistoriuesDeCetteJour> { it.dateCreationStr }
            .thenByDescending { it.tempCreationStr }
    )

    val lastHistorique = sortedHistoriques.firstOrNull()
    val lastState = lastHistorique?.etate

    return lastState == _14A_HistoriuesDeCetteJour.Etate.COMMANDE_LENCE
}

fun updateAcheteurToAchatTermine(
    viewModelInitApp: ViewModelInitApp,
    clientId: Long,
    repositorysModel: _0_0_HeadOfRepositorys_Model,
    newState: _14A_HistoriuesDeCetteJour.Etate = _14A_HistoriuesDeCetteJour.Etate.ACHAT_TERMINE,
) {
    val ceComptVendeurInsertBonsAchatAuPeriodID =
        repositorysModel.repository_1_5_Vendeur.modelDatasSnapList
            .find { it.vid == repositorysModel.activeIdDe_1_5_Vendeur }
            ?.ceComptVendeurInsertBonsAchatAuPeriodID

    _01_Upsert_013_Acheteurs(
        ceComptVendeurInsertBonsAchatAuPeriodID = ceComptVendeurInsertBonsAchatAuPeriodID,
        repositorysModel = repositorysModel,
        clientId = clientId,
        historiqueState = newState,
        nom = viewModelInitApp.clientDataBaseSnapList.find { it.id == clientId }?.nom ?: "",
        repo_01_VentsHistoriquesDataBase = viewModelInitApp.repo_01_VentsHistoriquesDataBase_Repository
    )
}

@Composable
fun OrderCompletionDialog(
    clientId: Long,
    clientName: String,
    onDismiss: () -> Unit,
    onStateChange: (state: _14A_HistoriuesDeCetteJour.Etate) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تحديث حالة الطلب") }, // "Update Order Status" in Arabic
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("حدد حالة العميل $clientName (ID: $clientId)") // Select status for client

                // Add buttons for all possible states
                _14A_HistoriuesDeCetteJour.Etate.entries.forEach { state ->
                    if (state != _14A_HistoriuesDeCetteJour.Etate.COMMANDE_LENCE) {
                        Button(
                            onClick = {
                                onStateChange(state)
                                onDismiss() // Auto-dismiss after selecting state
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(
                                    ContextCompat.getColor(
                                        LocalContext.current,
                                        state.color
                                    )
                                )
                            )
                        ) {
                            Text(
                                text = state.nomArabe,
                                color = if (state == _14A_HistoriuesDeCetteJour.Etate.A_EVITE) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء") // "Cancel" in Arabic
            }
        }
    )
}

// Class to manage order state checks and prevent redundant checks
class OrderStateManager(
    val repo_01_VentsHistoriquesDataBase_Repository: _01_VentsHistoriquesDataBase_Repository,
    val repositorysModel: _0_0_HeadOfRepositorys_Model,
    val currentClientId: () -> Long,
    val onShowDialog: () -> Unit,
    val onReloadMap: () -> Unit = {}, // Add this parameter
) {
    var checkedOrderStatus = false

    fun hasCheckedOrderStatus(): Boolean = checkedOrderStatus

    fun resetOrderStatusCheck() {
        checkedOrderStatus = false
    }

    fun checkOrderStatus() {
        val clientId = currentClientId()
        if (clientId <= 0) {
            checkedOrderStatus = true
            return
        }

        val period = repo_01_VentsHistoriquesDataBase_Repository
            .modelDatasSnapList
            .firstOrNull()

        if (period == null) {
            checkedOrderStatus = true
            return
        }

        val vendeur = period.child_012_Compts_Vendeurs?.firstOrNull {
            it.idCompt == repositorysModel.activeIdDe_1_5_Vendeur
        }

        if (vendeur == null) {
            checkedOrderStatus = true
            return
        }

        val acheteur = vendeur.child_013_Acheteurs?.firstOrNull {
            it.idClient == clientId
        }

        if (acheteur == null) {
            checkedOrderStatus = true
            return
        }

        if (acheteur.isLastStateCommandeLence()) {
            onShowDialog()
        }

        // Mark as checked so we don't recheck
        checkedOrderStatus = true
    }
}
