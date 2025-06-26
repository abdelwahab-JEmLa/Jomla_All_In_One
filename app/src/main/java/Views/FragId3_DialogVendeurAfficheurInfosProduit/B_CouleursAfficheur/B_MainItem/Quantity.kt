package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ArticlesBasesStatsTable
import Views.FragId3_DialogVendeurAfficheurInfosProduit.ViewModel.VendeurAfficheurInfosProduitViewModel
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
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
    val TAG = "QuantityButton"

    val vent = viewModel.aCentral.getter.getVentForArticleAndColorInThisApp(
        article,
        colorIndex
    )

    Log.d(TAG, "QuantityButton rendered - quantity: $quantity, isSelected: $isSelected")
    Log.d(TAG, "Article ID: ${article.id}, colorIndex: $colorIndex")
    Log.d(TAG, "compose_1_1_CouleurAcheteOperationVid: $compose_1_1_CouleurAcheteOperationVid")

    Button(
        onClick = {
            Log.d(TAG, "=== QuantityButton onClick START ===")
            Log.d(TAG, "Clicked quantity: $quantity")
            Log.d(TAG, "Article: ${article.id}, colorIndex: $colorIndex")

            val couleuracheteoperationRepository =
                viewModelInitApp._1_1_CouleurAcheteOperation_Repository

            val etateActuellementEst1 = if (quantity == 0)
                _1_1_CouleurAcheteOperation.EtateActuellementEst.SUPPRIME_AU_PREMIER_PICK
            else
                _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI

            Log.d(TAG, "Calculated etateActuellementEst1: $etateActuellementEst1")

            // Check if the operation exists
            val existingOperation = couleuracheteoperationRepository.modelDatasSnapList.find {
                it.vid == compose_1_1_CouleurAcheteOperationVid
            }

            Log.d(TAG, "Existing operation found: ${existingOperation != null}")
            if (existingOperation != null) {
                Log.d(TAG, "Existing operation before update:")
                Log.d(TAG, "  vid: ${existingOperation.vid}")
                Log.d(TAG, "  totaleQuantity: ${existingOperation.totaleQuantity}")
                Log.d(TAG, "  etateActuellementEst: ${existingOperation.etateActuellementEst}")
            }

            if (existingOperation != null) {
                // Update the existing operation
                Log.d(TAG, "Updating existing operation")
                existingOperation.apply {
                    Log.d(TAG, "Before update - totaleQuantity: $totaleQuantity")
                    totaleQuantity = quantity
                    etateActuellementEst = etateActuellementEst1
                    Log.d(TAG, "After update - totaleQuantity: $totaleQuantity")
                    Log.d(TAG, "After update - etateActuellementEst: $etateActuellementEst")
                }
                couleuracheteoperationRepository.updateUnSeulData(existingOperation)
                Log.d(TAG, "Called updateUnSeulData")
            } else if (compose_1_1_CouleurAcheteOperationVid > 0) {
                // Create new operation if we have a valid VID but no existing record
                Log.d(TAG, "Creating new operation")
                val newOperation = _1_1_CouleurAcheteOperation(
                    vid = compose_1_1_CouleurAcheteOperationVid,
                    totaleQuantity = quantity,
                    etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.QUANTITY_CHOISI
                )
                Log.d(TAG, "New operation created:")
                Log.d(TAG, "  vid: ${newOperation.vid}")
                Log.d(TAG, "  totaleQuantity: ${newOperation.totaleQuantity}")
                Log.d(TAG, "  etateActuellementEst: ${newOperation.etateActuellementEst}")
                couleuracheteoperationRepository.addData(newOperation)
                Log.d(TAG, "Called addData")
            } else {
                Log.w(TAG, "No valid VID provided: $compose_1_1_CouleurAcheteOperationVid")
            }

            Log.d(TAG, "Calling onClick callback")
            onClick()

            Log.d(TAG, "About to call acheterACaSetterCentral")
            Log.d(TAG, "vent object: $vent")
            Log.d(TAG, "quantity parameter: $quantity")

            viewModel.aCentral.setter.acheterACaSetterCentral(
                vent,
                produit = article,
                colorIndex = colorIndex,
                quantity = quantity
            )

            Log.d(TAG, "=== QuantityButton onClick END ===")
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
