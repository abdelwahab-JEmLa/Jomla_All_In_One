package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFormatterUtils
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun Display_Tariff(
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
    relative_produit: M01Produit,
    allNonTrouve: Boolean,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade
    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

    val infiniteTransition = rememberInfiniteTransition(label = "blinkAnimation")
    val blinkColor by infiniteTransition.animateColor(
        initialValue = Color.Gray,
        targetValue = Color.Red,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkColor"
    )

    val totalQuantity by derivedStateOf {
        focusedValuesGetter
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { it.parent_M1Produit_KeyId == relative_produit.keyID }
            .sumOf { it.quantity }
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
                val cartonSize = relative_produit.quantite_Boit_Par_Carton ?: 1
                val formattedQuantity = PdfFormatterUtils(repositorysMainGetter).formatQuantity(
                    qty = totalQuantity,
                    cartonSize = cartonSize,
                    produit = relative_produit
                )
                Text(
                    text = formattedQuantity,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (allNonTrouve) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        val datasValue = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue

        val find_Tariff_Prix_Detaille = datasValue
            .filter { it.typeChoisi == TypeChoisi.Prix_Detaille && it.parent_M1Produit_KeyId == relative_produit.keyID }
            .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }

        val parentM13TarificationKeyID = relative_List_M10OperationVentCouleur.first().parentM13TarificationKeyID
        val relative_Tariff = datasValue.find { it.keyID == parentM13TarificationKeyID }

        val displayTariff: M13TarificationInfos
        val effectiveAllNonTrouve: Boolean

        if (currentApp_ItsWorkChezGrossisst) {
            val ventTariffIfGrossist = if (
                relative_Tariff?.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros ||
                relative_Tariff?.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_Gro ||
                relative_Tariff?.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_Achat
            ) relative_Tariff else null

            val grossistTariff = ventTariffIfGrossist ?: datasValue
                .filter { tariff ->
                    (tariff.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros ||
                            tariff.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_Gro ||
                            tariff.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_Achat) &&
                            tariff.parent_M1Produit_KeyId == relative_produit.keyID
                }
                .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }

            if (grossistTariff != null) {
                displayTariff = grossistTariff
                effectiveAllNonTrouve = allNonTrouve || (grossistTariff.prixCurrency == 0.0)
            } else {
                // FIX : fallback sur le tariff lié à l'opération, peu importe son type
                val fallback = relative_Tariff?.takeIf { it.prixCurrency != 0.0 }
                if (fallback != null) {
                    displayTariff = fallback
                    effectiveAllNonTrouve = allNonTrouve
                } else {
                    displayTariff = M13TarificationInfos(
                        typeChoisi = TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                        prixCurrency = relative_produit.prixAchat,
                        parent_M1Produit_KeyId = relative_produit.keyID,
                        parent_M1Produit_DebugInfos = relative_produit.nom
                    )
                    effectiveAllNonTrouve = true
                }
            }
        } else {
            if (relative_Tariff != null) {
                displayTariff = relative_Tariff
                effectiveAllNonTrouve = allNonTrouve || (relative_Tariff.prixCurrency == 0.0)
            } else {
                displayTariff = find_Tariff_Prix_Detaille ?: M13TarificationInfos(
                    typeChoisi = TypeChoisi.Prix_Detaille,
                    prixCurrency = relative_produit.prixAchat,
                    parent_M1Produit_KeyId = relative_produit.keyID,
                    parent_M1Produit_DebugInfos = relative_produit.nom
                )
                effectiveAllNonTrouve = allNonTrouve || (find_Tariff_Prix_Detaille == null) || (displayTariff.prixCurrency == 0.0)
            }
        }

        // Click is intentionally disabled when effectiveAllNonTrouve=true:
        // no valid tariff exists to save, so there is nothing meaningful to commit.
        Card(
            modifier = Modifier.clickable(enabled = !effectiveAllNonTrouve) {
                val get = focusedVarsHandlerFacade.focusedValuesGetter
                aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                    m13TarificationInfos_Pour_Produit = get.focused_M13TarificationInfos_Pour_Produit,
                    m10OperationVentCouleurs = get.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                    aCentralFacade = aCentralFacade
                )
                focusedVarsHandlerFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                    relative_produit
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (effectiveAllNonTrouve) blinkColor
                else displayTariff.typeChoisi.couleur
            )
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tariffType = displayTariff.typeChoisi
                    val nom = tariffType.nomArabe.take(2)
                    val tariffIcon = tariffType.iconVector ?: Icons.Default.History
                    val textColor = if (effectiveAllNonTrouve) Color.White else tariffType.couleur_Text

                    Text(
                        text = "$nom - ${displayTariff.prixCurrency}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Icon(
                        imageVector = tariffIcon,
                        contentDescription = tariffType.nomArabe,
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
