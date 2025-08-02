package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.B_ProductGroup

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun QuantityDisplay_Mo_F_Panie(
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
    relative_produit: ArticlesBasesStatsTable,
    allNonTrouve: Boolean,
    aCentralFacade: ACentralFacade
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter

    val getterFocusedVarsHandlerFacade =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedValuesSetter =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter

    val totalQuantity by derivedStateOf {
        getterFocusedVarsHandlerFacade
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { ventOperation ->
                ventOperation.parent_M1Produit_KeyId == relative_produit.keyID
            }   .sumOf { it.quantity }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (allNonTrouve) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .clickable(enabled = false) {
                    val get = focusedVarsHandlerFacade.focusedValuesGetter

                    aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                        m13TarificationInfos_Pour_Produit = get.focused_M13TarificationInfos_Pour_Produit,
                        m10OperationVentCouleurs = get.focused_ListM10OpeVentCouleur_Par_PD_M1Produit
                    )

                    focusedValuesSetter.active_M1Produit_Pour_Choisire_TotalQuantity(
                        relative_produit
                    )

                }
                .getSemanticsTag(
                    nomVal = "dialogChoisireQuantityM1ProduitInfosDebugName",
                    data = focusedValuesGetter.currentActive_M9AppCompt?.dialogChoisireQuantityM1ProduitInfosDebugName
                )
                .getSemanticsTag_By_datas_A_Affiche_Au_Nom(
                    1,
                    "dialogChoisireQuantityM1ProduitInfosKeyID",
                    focusedValuesGetter.currentActive_M9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID
                )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Total quantity",
                    tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = totalQuantity.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        val datasValue = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue

        val findTariff = datasValue
            .filter { tariff ->
                tariff.typeChoisi == TypeChoisi.DefiniParGerant &&
                        tariff.parent_M1Produit_KeyId == relative_produit.keyID
            }
            .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }


        val default_Tariff = M13TarificationInfos.get_default_P0(relative_produit, start_Prix_Depuit_Ancient = relative_produit.prixAchat)

        val finale_Tariff_Prix = findTariff?.prixCurrency.takeIf { it!! >0.0 }
            ?: relative_List_M10OperationVentCouleur.first().provisoireMonPrix.takeIf { it>0.0 }
            ?: default_Tariff.first.prixCurrency

        Card(
            modifier = Modifier
                .semantics(mergeDescendants = true) {
                    val datasValuefilter= datasValue.filter { it.parent_M1Produit_KeyId == relative_produit.keyID }
                    set(value =datasValue, key = SemanticsPropertyKey("datasValue")) //<--
                    //TODO(1): pk ca ne s affiche pas le tarrife avec prix 10.0
                    Log.d("datasValuefilter", datasValuefilter.toString())
                }
                .semantics(mergeDescendants = true) {
                    set(value = findTariff, key = SemanticsPropertyKey("findTariff"))
                }
                .semantics(mergeDescendants = true) {
                    set(
                        value = relative_List_M10OperationVentCouleur,
                        key = SemanticsPropertyKey("relative_List_M10OperationVentCouleur")
                    )
                }
                .clickable(enabled = !allNonTrouve) {
                    /* repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                        m13TarificationInfos_Pour_Produit = finale_Tariff,
                        m10OperationVentCouleurs = focusedValuesGetter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit
                    )       */
                    focusedVarsHandlerFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                        relative_produit
                    )

                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (allNonTrouve) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.secondary
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val (depuit_Qui, tariffIcon) =
                    /*if (findTariff != null) {
                    "Définie Par Ali" to Icons.Default.TrendingUp
                } else {    */
                    "Depuis Mon Old BaseDonnée" to Icons.Default.History

                Text(
                    text = "$depuit_Qui - ${finale_Tariff_Prix}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSecondary
                )

                Icon(
                    imageVector = tariffIcon,
                    contentDescription = if (findTariff != null) "Defined by Ali" else "From old database",
                    tint = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
