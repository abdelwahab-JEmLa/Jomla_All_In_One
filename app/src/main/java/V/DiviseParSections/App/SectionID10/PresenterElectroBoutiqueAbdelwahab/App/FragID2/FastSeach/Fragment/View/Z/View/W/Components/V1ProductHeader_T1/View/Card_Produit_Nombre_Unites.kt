package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.V1ProductHeader_T1.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifFalse
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Card_Produit_Nombre_Unites(
    allNonTrouve: Boolean,
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    onClick_PourOuvrireDialog: () -> Unit
) {
    // Track toggle state
    var toggleState by remember { mutableStateOf(produit.afficheUniteAuPrint) }

    fun clickHandel() {
        // Toggle the state
        toggleState = !toggleState

        // Update the product with the new state
        viewModel.aCentralFacade.repositorysMainGetter.repo1ProduitInfos.update(
            produit.copy(
                afficheUniteAuPrint = toggleState
            )
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allNonTrouve) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.padding(start = petitePaddine)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(petitePaddine),
            modifier = Modifier.padding(petitePaddine)
        ) {
            // Original quantity display button
            IconButton(
                onClick = {
                    onClick_PourOuvrireDialog()
                },
                modifier = Modifier
                    .width(50.dp)
                    .height(36.dp)
            ) {
                Row {
                    Text(
                        text = "Nbr.U ",
                        fontSize = 8.sp,
                        color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.6f
                        )
                        else MaterialTheme.colorScheme.tertiary,
                    )
                    Text(
                        text = "${produit.nombreUniteInt}",
                        fontSize = 15.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            ActiveCentralValues.get_Default().affiche_Produit_OnGrid.ifFalse {
            // Toggle button for afficheUniteAuPrint
            IconButton(
                onClick = { clickHandel() },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (toggleState) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = if (toggleState) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = if (toggleState) "Print units enabled" else "Print units disabled",
                    tint = if (toggleState) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }     }
        }
    }
}
