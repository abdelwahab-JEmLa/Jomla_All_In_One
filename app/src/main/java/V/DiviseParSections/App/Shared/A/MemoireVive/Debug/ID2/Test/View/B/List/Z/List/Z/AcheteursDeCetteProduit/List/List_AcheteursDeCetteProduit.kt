package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List

import V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID2.Test.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository.M11AchatOperation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun List_AcheteursDeCetteProduit(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    achatCouleur: M11AchatOperation
) {
    val listFCouleurVentOperation = achatCouleur.listFCouleurVentOperation
    val listGBonVentKeyID = listFCouleurVentOperation.map { it.parentM8BonVentKeyId }
    Column(
        modifier = Modifier
            .getSemanticsTag(nomVal = "listFCouleurVentOperation", data = listFCouleurVentOperation)
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        listGBonVentKeyID.forEach { gBonVentKeyID ->
            val gBonVent =
                viewModel.getter.repo8BonVent.datasValue.find { it.keyID == gBonVentKeyID }
            val lClient =
                viewModel.getter.repo2Client.datasValue.find { it.keyID == gBonVent?.parent_M2Client_KeyID }

            if (lClient != null) {
                Text(
                    text = "${lClient.nom} ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            val achatQuantityDeCetteBonVent = viewModel.aCentralFacade.focusedActiveValuesFacade
                .focusedValuesGetter
                .filtered_ListM10Vent_BY_Curr_M14VentPeriod
                .find { it.parentM8BonVentKeyId == gBonVentKeyID }?.quantity

            Text(
                text = "Qté: $achatQuantityDeCetteBonVent",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
