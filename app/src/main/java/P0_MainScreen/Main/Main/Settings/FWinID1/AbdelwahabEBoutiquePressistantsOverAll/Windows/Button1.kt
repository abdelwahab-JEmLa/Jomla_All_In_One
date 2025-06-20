package P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.Repository.Z_AppComptComposeRepositoryProtoJuin17
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Button1(
    appComptComposeRepositoryProtoJuin17: Z_AppComptComposeRepositoryProtoJuin17,
    showLabels: Boolean,
    onClickPourAfficheDialog: () -> Unit = {}
) {
    val catalogues = B4CatalogueCategoriesRepository()
    val catalogueId = appComptComposeRepositoryProtoJuin17.currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId
    val buttonAFficheAuCata = catalogues.find { it.bsonObjectId == catalogueId }

    // Get the catalogue name and color, with fallbacks
    val catalogueName = buttonAFficheAuCata?.nom ?: "Catalogues"
    val buttonBackgroundColor = buttonAFficheAuCata?.couleur ?: Color(0xFF9C27B0)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = {
                onClickPourAfficheDialog()
            },
            modifier = Modifier.size(40.dp),
            containerColor = buttonBackgroundColor,
        ) {
            val iconColor = Color.Black

            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = "Sélectionner Catalogue",
                tint = iconColor
            )
        }

        if (showLabels) {
            Text(
                text = catalogueName, // Now displays the actual catalogue name
                modifier = Modifier
                    .background(buttonBackgroundColor)
                    .padding(4.dp),
                color = Color.White
            )
        }
    }
}
