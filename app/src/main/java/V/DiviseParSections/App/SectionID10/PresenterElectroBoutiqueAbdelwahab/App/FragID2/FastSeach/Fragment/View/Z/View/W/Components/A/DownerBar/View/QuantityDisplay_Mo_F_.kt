package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.A.DownerBar.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Genere_Tariffs_currentApp_ItsWorkChezGrossisst
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun QuantityDisplay_Mo_F_(
    produit: ArticlesBasesStatsTable,
    allNonTrouve: Boolean,
    aCentralFacade: ACentralFacade,
    onShowColorsClick: (() -> Unit)? = null  // NEW: Callback to show colors
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter
    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

    val getterFocusedVarsHandlerFacade =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedValuesSetter =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter

    val totalQuantity by derivedStateOf {
        getterFocusedVarsHandlerFacade
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { ventOperation ->
                ventOperation.parent_M1Produit_KeyId == produit.keyID
            }.sumOf { it.quantity }
    }

    // Get the SuperGros tariff for the current product
    val superGrosTariff = if (currentApp_ItsWorkChezGrossisst) {
        Genere_Tariffs_currentApp_ItsWorkChezGrossisst()
            .find_existing_Tariff_Grossist_SuperGros(aCentralFacade, produit)
    } else null

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
                .clickable(enabled = !allNonTrouve) {
                    val get = focusedVarsHandlerFacade.focusedValuesGetter

                    aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                        m13TarificationInfos_Pour_Produit = get.focused_M13TarificationInfos_Pour_Produit,
                        m10OperationVentCouleurs = get.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                        aCentralFacade = aCentralFacade
                    )

                    focusedValuesSetter.active_M1Produit_Pour_Choisire_TotalQuantity(
                        produit
                    )

                    // NEW: Trigger callback to show colors
                    onShowColorsClick?.invoke()
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

        val findTariff = datasValue.find { tariff ->
            tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                    tariff.parent_M1Produit_KeyId == produit.keyID
        }

        val default_Tariff = M13TarificationInfos.get_default_P0(produit, start_Prix_Depuit_Ancient = produit.prixAchat)
        val finale_Tariff = findTariff ?: default_Tariff.first

        // Check if manager allows price display - if true, use white background and black text
        val shouldUseManagerColors = finale_Tariff.laisse_Au_Gerant

        Card(
            modifier = Modifier
                .getSemanticsTag(nomVal = "repo13TarificationInfos", data = datasValue)
                .getSemanticsTag_By_datas_A_Affiche_Au_Nom(2, "finale_Tariff", finale_Tariff)
                .getSemanticsTag_By_datas_A_Affiche_Au_Nom(3, "findTariff", findTariff)
                .clickable(enabled = !allNonTrouve) {

                    repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                        m13TarificationInfos_Pour_Produit = finale_Tariff,
                        m10OperationVentCouleurs = focusedValuesGetter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                        aCentralFacade = aCentralFacade
                    )

                    focusedVarsHandlerFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                        produit
                    )

                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    allNonTrouve -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    shouldUseManagerColors -> Color.White
                    else -> MaterialTheme.colorScheme.error
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val (depuit_Qui, tariffIcon) = if (findTariff != null) {
                    "Définie Par Ali" to Icons.Default.TrendingUp
                } else {
                    "Depuis Mon Old BaseDonnée" to Icons.Default.History
                }

                val displayText = if (currentApp_ItsWorkChezGrossisst) {
                    superGrosTariff?.let { tariff ->
                        "${tariff.prixCurrency} DA"
                    } ?: "غير متوفر"
                } else {
                    "اضغط لاظهار السعر"
                }

                Text(
                    text = displayText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        shouldUseManagerColors -> Color.Black
                        else -> MaterialTheme.colorScheme.onSecondary
                    }
                )

                Icon(
                    imageVector = tariffIcon,
                    contentDescription = if (findTariff != null) "Defined by Ali" else "From old database",
                    tint = when {
                        allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        shouldUseManagerColors -> Color.Black
                        else -> MaterialTheme.colorScheme.onSecondary
                    },
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
