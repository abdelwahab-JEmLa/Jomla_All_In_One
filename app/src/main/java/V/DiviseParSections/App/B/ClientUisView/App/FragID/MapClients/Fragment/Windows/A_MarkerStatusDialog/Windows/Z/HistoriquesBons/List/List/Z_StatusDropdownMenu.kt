package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
        // Helper function to create elevated card dropdown item
        @Composable
        fun StatusDropdownItem(
            status: M8BonVent.EtateActuellementEst,
            text: String
        ) {
            DropdownMenuItem(
                text = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = relative_M8BonVent.etateActuellementEst.color
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = text,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                },
                onClick = {
                    onStatusSelected(status)
                    onDismissRequest()
                }
            )
        }

        M8BonVent.EtateActuellementEst.Ordre_Gerant.let { etate ->
            StatusDropdownItem(
                status = etate,
                text = etate.nomArabe
            )
        }
        val etate = M8BonVent.EtateActuellementEst.Bloque_Probleme
        StatusDropdownItem(
            status = etate,
            text = etate.nomArabe
        )

        StatusDropdownItem(
            status = M8BonVent.EtateActuellementEst.AVEC_MARCHANDISE,
            text = "عندو سلعة"
        )

        StatusDropdownItem(
            status = M8BonVent.EtateActuellementEst.ACHETEUR_NON_DISPO,
            text = "الشاري غائب"
        )

        StatusDropdownItem(
            status = M8BonVent.EtateActuellementEst.FERME,
            text = "مغلق"
        )
    }
}
