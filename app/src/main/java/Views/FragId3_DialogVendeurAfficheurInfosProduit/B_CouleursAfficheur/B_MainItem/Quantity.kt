package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem

import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model._1_1_CouleurAcheteOperation
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation_Repository
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
import org.koin.compose.koinInject

@Composable
fun QuantityButton(
    viewModelInitApp: ViewModelInitApp,
    quantity: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    currentSale: SoldArticlesTabelle?,
    currentClient: B_ClientsDataBase?,
    compose_1_1_CouleurAcheteOperationVid: Long,
) {
    val _1_1_CouleurAcheteOperation_Repository =
        koinInject<_1_1_CouleurAcheteOperation_Repository>()

    Button(
        onClick = {
            val data = _1_1_CouleurAcheteOperation_Repository.modelDatasSnapList.find {
                it.vid == compose_1_1_CouleurAcheteOperationVid
            }

            data?.apply {
                totaleQuantity=quantity
                etateActuellementEst= _1_1_CouleurAcheteOperation.EtateActuellementEst
                    .QUANTITY_CHOSI_MAIS_PAS_DE_CONFIRMATION_PRODUIT
            }?.let {
                _1_1_CouleurAcheteOperation_Repository.updateUnSeulData(
                    it
                )
            }

            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = quantity.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}








