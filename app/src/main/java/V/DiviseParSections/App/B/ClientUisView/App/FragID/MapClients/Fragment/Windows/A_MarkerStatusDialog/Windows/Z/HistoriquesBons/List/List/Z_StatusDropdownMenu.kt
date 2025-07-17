package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun StatusDropdownMenu(
    relative_M8BonVent: M8BonVent,
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onStatusSelected: (M8BonVent.EtateActuellementEst) -> Unit
) {

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text("اقترح ان يتجنب لمدة اسبوعين") },
            onClick = {
                onStatusSelected(M8BonVent.EtateActuellementEst.A_EVITE)
                onDismissRequest()
            }
        )

        DropdownMenuItem(
            text = { Text("عندو سلعة") },
            onClick = {
                onStatusSelected(M8BonVent.EtateActuellementEst.AVEC_MARCHANDISE)
                onDismissRequest()
            }
        )

        DropdownMenuItem(
            text = { Text("للنظر") },
            onClick = {
                onStatusSelected(M8BonVent.EtateActuellementEst.PourVoirPanie)
                onDismissRequest()
            }
        )

        DropdownMenuItem(
            text = { Text("تم أيصال منتجاته") },
            onClick = {
                onStatusSelected(M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI)
                onDismissRequest()
            }
        )

        DropdownMenuItem(
            text = { Text("الشاري غائب") },
            onClick = {
                onStatusSelected(M8BonVent.EtateActuellementEst.ACHETEUR_NON_DISPO)
                onDismissRequest()
            }
        )

        DropdownMenuItem(
            text = { Text("مغلق") },
            onClick = {
                onStatusSelected(M8BonVent.EtateActuellementEst.FERME)
                onDismissRequest()
            }
        )
    }
}
