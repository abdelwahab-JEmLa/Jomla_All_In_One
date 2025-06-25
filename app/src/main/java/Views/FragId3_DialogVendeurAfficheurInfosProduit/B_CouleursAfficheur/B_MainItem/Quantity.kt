package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem

import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.ViewModel.Repository.ArticlesBasesStatsTable
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuantityButton(
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
    viewModel: VendeurAfficheurInfosProduitViewModel,
    viewModelInitApp: ViewModelInitApp,
    quantity: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    compose_1_1_CouleurAcheteOperationVid: Long,
) {
    Button(
        onClick = {
            val couleuracheteoperationRepository =
                viewModelInitApp._1_1_CouleurAcheteOperation_Repository

            val etateActuellementEst1 = if (quantity == 0)
                _1_1_CouleurAcheteOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK
            else
                _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI

            // Check if the operation exists
            val existingOperation = couleuracheteoperationRepository.modelDatasSnapList.find {
                it.vid == compose_1_1_CouleurAcheteOperationVid
            }

            if (existingOperation != null) {
                // Update the existing operation
                existingOperation.apply {
                    totaleQuantity = quantity
                    etateActuellementEst = etateActuellementEst1
                }
                couleuracheteoperationRepository.updateUnSeulData(existingOperation)
            } else if (compose_1_1_CouleurAcheteOperationVid > 0) {
                // Create add new operation if we have add valid VID but no existing record
                val newOperation = _1_1_CouleurAcheteOperation(
                    vid = compose_1_1_CouleurAcheteOperationVid,
                    totaleQuantity = quantity,
                    etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                )
                couleuracheteoperationRepository.addData(newOperation)
            }

            onClick()

            viewModel.acheter(
                produit =article,
                colorIndex = colorIndex,
                quantity = quantity
            )
        },
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = quantity.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
