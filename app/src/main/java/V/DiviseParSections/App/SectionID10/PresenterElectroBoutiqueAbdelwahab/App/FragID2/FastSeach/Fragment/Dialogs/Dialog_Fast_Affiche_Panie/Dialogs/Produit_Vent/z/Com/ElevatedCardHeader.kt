package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent.z.Com

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup.ProductHeader_SemiModularized.NonTrouve_Handler
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ElevatedCardHeader(
    produit: ArticlesBasesStatsTable,
    hasNonTrouve: Boolean,
    allNonTrouve: Boolean,
    ventList: List<M10OperationVentCouleur>,
    aCentralFacade: ACentralFacade,
    focusedValuesSetter: FocusedValuesSetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter,
    modifier: Modifier = Modifier,
    upsert_M10OperationVentCouleur: (Boolean) -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasNonTrouve) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                reverseLayout = true,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    ToggleButton_PremierCheckDonne(
                        ventList = ventList,
                        onToggle = { newState ->
                            upsert_M10OperationVentCouleur(newState)
                        },
                        modifier = Modifier
                    )
                }
                item {
                    InfoButton(
                        produit = produit,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                item {
                    ToggleButton_MoveToStorePosition(
                        produit = produit,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                item {
                    NonTrouve_Handler(
                        aCentralFacade = aCentralFacade,
                        allNonTrouve = allNonTrouve,
                        hasNonTrouve = hasNonTrouve,
                        relative_List_M10OperationVentCouleur = ventList
                    )
                }
            }
        }
    }
}
