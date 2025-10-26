package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFormatterUtils
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import android.annotation.SuppressLint
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun Display_Tariff(
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
    relative_produit: ArticlesBasesStatsTable,
    allNonTrouve: Boolean,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade
    val getterFocusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedValuesSetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter
    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst
    val coroutineScope = rememberCoroutineScope()

    val totalQuantity by derivedStateOf {
        getterFocusedVarsHandlerFacade
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { ventOperation ->
                ventOperation.parent_M1Produit_KeyId == relative_produit.keyID
            }.sumOf { it.quantity }
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
            .filter { tariff ->
                tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                        tariff.parent_M1Produit_KeyId == relative_produit.keyID
            }
            .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }

        // Search tariff by parentM13TarificationKeyID (same logic as PDF print)
        val parentM13TarificationKeyID = relative_List_M10OperationVentCouleur.first().parentM13TarificationKeyID
        val relative_Tariff = datasValue.find { it.keyID == parentM13TarificationKeyID }

        val isNonTrouveForGrossist = if (currentApp_ItsWorkChezGrossisst) {
            // In grossist mode, tariff must exist AND be SuperGros type
            relative_Tariff == null ||
                    relative_Tariff.typeChoisi != TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
        } else {
            false
        }

        // Update allNonTrouve to include the grossist validation
        val effectiveAllNonTrouve = allNonTrouve || isNonTrouveForGrossist

        // Handle tariff display with fallback logic
        val displayTariff = if (relative_Tariff != null && !isNonTrouveForGrossist) {
            // Valid tariff exists and passes grossist validation
            relative_Tariff
        } else {
            // Try to find existing SuperGros tariff for this product
            val superGrosTariff = datasValue
                .filter { tariff ->
                    tariff.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                            tariff.parent_M1Produit_KeyId == relative_produit.keyID
                }
                .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }

            // If SuperGros tariff exists, use it; otherwise create a fallback
            superGrosTariff ?: M13TarificationInfos(
                typeChoisi = TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                prixCurrency = relative_produit.prixAchat,
                parent_M1Produit_KeyId = relative_produit.keyID,
                parent_M1Produit_DebugInfos = relative_produit.nom
            )
        }

        Card(
            modifier = Modifier
                .semantics(mergeDescendants = true) {
                    val datasValuefilter = datasValue.filter { it.parent_M1Produit_KeyId == relative_produit.keyID }
                    set(value = datasValue, key = SemanticsPropertyKey("datasValue"))
                }
                .semantics(mergeDescendants = true) {
                    set(value = find_Tariff_Prix_Detaille, key = SemanticsPropertyKey("findTariff"))
                }
                .semantics(mergeDescendants = true) {
                    set(value = relative_List_M10OperationVentCouleur, key = SemanticsPropertyKey("relative_List_M10OperationVentCouleur"))
                }
                .clickable(enabled = !allNonTrouve) {
                    val get = focusedVarsHandlerFacade.focusedValuesGetter

                    aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                        m13TarificationInfos_Pour_Produit = get.focused_M13TarificationInfos_Pour_Produit,
                        m10OperationVentCouleurs = get.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                        aCentralFacade = aCentralFacade
                    )

                    focusedVarsHandlerFacade.focusedValuesSetter.clear_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID()

                    // Launch coroutine to add delay before setting new focused tariff
                    coroutineScope.launch {
                        delay(100) // 100ms delay, adjust as needed
                        focusedVarsHandlerFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                            relative_produit
                        )
                    }
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (effectiveAllNonTrouve) MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                else displayTariff.typeChoisi.couleur
            )
        ) {
            Column(
                modifier = Modifier.semantics(mergeDescendants = true) {
                    set(value = displayTariff, key = SemanticsPropertyKey("relative_Tariff"))
                }
            ) {
                Row(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(value = displayTariff, key = SemanticsPropertyKey("relative_Tariff"))
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tariffType = displayTariff.typeChoisi
                    val nom = tariffType.nomArabe.take(2)
                    val tariffIcon = tariffType.iconVector ?: Icons.Default.History
                    val textColor = if (effectiveAllNonTrouve) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        tariffType.couleur_Text
                    }

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
