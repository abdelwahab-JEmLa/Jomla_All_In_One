package Views.FragId3_DialogVendeurAfficheurInfosProduit.B_CouleursAfficheur.B_MainItem

import Z_CodePartageEntreApps.Model.B_ClientsDataBase
import Z_CodePartageEntreApps.Model.Z.Archive.ColorsArticlesTabelle
import Z_CodePartageEntreApps.Model.Z.Archive.SoldArticlesTabelle
import Z_CodePartageEntreApps.Model._1_1_CouleurAcheteOperation
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Package_3._DisplayeProductInfosToSeller
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuantityButton(
    viewModelInitApp: ViewModelInitApp,
    composKeyID: String,
    quantity: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    currentSale: SoldArticlesTabelle?,
    currentClient: B_ClientsDataBase?,
    colorDetails: ColorsArticlesTabelle,
    composMainKeyModel: _1_1_CouleurAcheteOperation
) {
    val viewmodelfragmentApp2Id1 = viewModelInitApp.viewModelFragment_APP2_ID_1
    val uiStateviewModelFragment_APP2_ID_1 by viewmodelfragmentApp2Id1.uiStateFlow.collectAsState()

    Button(
        onClick = {
            viewmodelfragmentApp2Id1
                .updateUnSeulData_1_1_CouleurAcheteOperation_Repository(composMainKeyModel)
//
//            val colorAcMemeProduitQuiMemeBonIdCeBonAMemePeriedID = uiStateviewModelFragment_APP2_ID_1
//                ._1_1_CouleurAcheteOperationList
//                .find {
//                    it.couleurId == colorDetails.idColore &&
//                            it.parent_1_2_ProduitAcheteOperationID == currentSale?.idArticle
//
//                }
//
//            val couleurId = colorDetails.idColore
//
//            if (colorAcMemeProduitQuiMemeBonIdCeBonAMemePeriedID != null) {
//                // Update existing color entry
//                val updatedColor = colorAcMemeProduitQuiMemeBonIdCeBonAMemePeriedID.copy(
//                    totaleQuantity = quantity,
//                    etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.CHOISI_UNE_QUANTITY
//                )
//                viewmodelfragmentApp2Id1._1_1_CouleurAcheteOperation_Repository.updateUnSeulData(updatedColor)
//            } else {
//                // Add new color entry
//                val newColorAchete = _1_1_CouleurAcheteOperation(
//                    couleurId_ParentVID = couleurId,
//                    parent_1_2_ProduitAcheteOperationID = currentSale?.idArticle ?: 0,
//                    totaleQuantity = quantity,
//                    etateActuellementEst = _1_1_CouleurAcheteOperation.EtateActuellementEst.CHOISI_UNE_QUANTITY
//                )
//                viewmodelfragmentApp2Id1.upsert(newColorAchete)
//            }



            onClick()

            _DisplayeProductInfosToSeller(viewModelInitApp)
                .onClickComposeQuantityButton(
                quantity,
                currentSale,
                currentClient,
                colorDetails,
            )
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








