package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.PriceEditor
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.UnitEditor
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
 fun PriceAndUnitSection(
    produit: ArticlesBasesStatsTable,
    updateProduct: (ArticlesBasesStatsTable) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (produit.nombreUniteInt > 0 && produit.clientPrixVentUnite>0) {
                PriceEditor(
                    currentPrice = produit.clientPrixVentUnite*produit.nombreUniteInt,
                    label = "تخرج",
                    onPriceUpdate = { newClientPrixUnite -> },
                    textColor = Color.Gray,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))
            }

            if (produit.nombreUniteInt > 0) {
                PriceEditor(
                    currentPrice = produit.clientPrixVentUnite,
                    label = "clientPrixVentUnite",
                    onPriceUpdate = { newClientPrixUnite ->
                        updateProduct(produit.copy(clientPrixVentUnite = newClientPrixUnite))
                    },
                    textColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))
            }

            // Unit Editor
            UnitEditor(
                currentUnits = produit.nombreUniteInt,
                label = "Unités",
                onUnitsUpdate = { newUnits ->
                    updateProduct(produit.copy(nombreUniteInt = newUnits))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
