package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.Z.AcheteursDeCetteProduit.List

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.A.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository.KAchatCouleurOperation
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
    achatCouleur: KAchatCouleurOperation
) {
    val listGBonVentKeyID = achatCouleur.listFCouleurVentOperation.map { it.parentGBonVentKeyId }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        listGBonVentKeyID.forEach { gBonVentKeyID ->
            val gBonVent =
                viewModel.getter.gBonVentRepository.datasValue.find { it.keyID == gBonVentKeyID }
            val lClient =
                viewModel.getter.hClientRepository.datasValue.find { it.id == gBonVent?.parentHClientOldID }

            if (lClient != null) {
                Text(
                    text = "${lClient.nom} ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            val achatQuantityDeCetteBonVent = viewModel.getter.fVentCouleurOperationRepository
                .datasFilteredParCurrentHVentPeriod
                .find { it.parentGBonVentKeyId == gBonVentKeyID }?.quantityAchete

            Text(
                text = "Qté: $achatQuantityDeCetteBonVent",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
