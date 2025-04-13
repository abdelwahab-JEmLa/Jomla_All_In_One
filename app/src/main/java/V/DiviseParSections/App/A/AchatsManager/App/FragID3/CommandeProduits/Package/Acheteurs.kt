package V.DiviseParSections.App.A.AchatsManager.App.FragID3.CommandeProduits.Package

import Z_CodePartageEntreApps.Repository._0_0_HeadOfRepositorys._0_0_HeadOfRepositorys_Model
import Z_CodePartageEntreApps.Repository._1_1_CouleurAcheteOperation._1_1_CouleurAcheteOperation
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
fun Acheteurs(
    buyerIds: List<Long>,
    models: _0_0_HeadOfRepositorys_Model,
    colorsForProduct: List<_1_1_CouleurAcheteOperation>,
    Couleur: _1_1_CouleurAcheteOperation,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        buyerIds.forEach { buyerId ->
            // Get the client by ID and display name with a fallback
            val clientName =
                models._3_ClientsDataBase_Repository.modelDatasSnapList
                    .find { it.vid == buyerId }?.nom
                    ?: "Client #$buyerId"

            // Get the BonAchat VIDs for this client
            val clientBonAchatVids =
                models._1_3_BonAchat_Repository.modelDatasSnapList
                    .filter { it.clientAcheteurID == buyerId }
                    .map { it.vid }

            // Get all product VIDs associated with this client's BonAchat entries
            val clientProductVids =
                models._1_2_ProduitAcheteOperation_Repository.modelDatasSnapList
                    .filter { it.parent_1_3_BonAchat in clientBonAchatVids }
                    .map { it.vid }

            // Sum the quantities for this client and this color
            val clientColorQuantity = colorsForProduct
                .filter {
                    it.parentProduitAchateOperationVID in clientProductVids &&
                            it.couleurIndex_ParentVID == Couleur.couleurIndex_ParentVID
                }
                .sumOf { it.totaleQuantity }

            if (clientColorQuantity > 0) {
                Text(
                    text = clientName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Qté: $clientColorQuantity",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Add a small spacing between client entries
        Spacer(modifier = Modifier.height(4.dp))
    }
}
