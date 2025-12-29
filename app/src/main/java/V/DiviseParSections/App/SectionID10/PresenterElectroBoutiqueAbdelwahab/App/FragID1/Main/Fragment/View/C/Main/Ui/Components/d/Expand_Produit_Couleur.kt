package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components.d

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun Expand_Produit_Couleur(
    relative_M3CouleurProduitInfos: M3CouleurProduitInfos, // Fixed: Added missing comma
    focusedValuesGetter: FocusedValuesGetter = koinInject()
) {

    val active_Central_Values = focusedValuesGetter.active_Central_Values

    fun updateExpandedCouleur() {
        focusedValuesGetter.update_activeCentralValues(
            active_Central_Values.copy(
                expanded_M3CouleurProduitInfos = relative_M3CouleurProduitInfos
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Default.Expand,
            contentDescription = "Expand color details",
            tint = Color.White,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Red.copy(alpha = 0.6f))
                .clickable {
                    updateExpandedCouleur()
                }
                .padding(4.dp)
        )
    }
}
