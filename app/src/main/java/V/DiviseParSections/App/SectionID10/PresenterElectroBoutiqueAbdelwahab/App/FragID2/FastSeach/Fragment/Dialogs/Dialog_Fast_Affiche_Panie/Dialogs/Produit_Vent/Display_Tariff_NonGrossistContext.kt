package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Produit_Vent

import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf.PdfFormatterUtils
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.Double_OutlinedText_Avec_Click_Button_Modulable_Proto0
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.annotation.SuppressLint
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
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
fun Display_Tariff_NonGrossistContext(
    relative_List_M10OperationVentCouleur: List<M10OperationVentCouleur>,
    relative_produit: M01Produit,
    allNonTrouve: Boolean,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter
) {
    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade

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

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Quantity badge ───────────────────────────────────────────────────
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
        val parentM13TarificationKeyID =
            relative_List_M10OperationVentCouleur.first().parentM13TarificationKeyID
        val relative_Tariff = datasValue.find { it.keyID == parentM13TarificationKeyID }

        // ── Un seul tariff par type (le plus récent) ────────────────────────
        val allTariffsForProduit = datasValue
            .filter { tariff ->
                tariff.parent_M1Produit_KeyId == relative_produit.keyID &&
                        !tariff.typeChoisi.its_gro_app &&
                        !tariff.typeChoisi.ignore_affiche
            }
            .groupBy { it.typeChoisi }
            .mapValues { (_, list) -> list.maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase } }
            .values
            .filterNotNull()

        // Ajoute un tariff éditable si aucun Prix_Progressive_Editable / Edited_Pour_Client n'existe
        val tariffsWithEditable = if (allTariffsForProduit.none {
                it.typeChoisi == TypeChoisi.Prix_Progressive_Editable ||
                        it.typeChoisi == TypeChoisi.Edited_Pour_Client
            }) {
            allTariffsForProduit + M13TarificationInfos(
                prixCurrency = 0.0,
                parent_M1Produit_KeyId = relative_produit.keyID,
                typeChoisi = TypeChoisi.Prix_Progressive_Editable,
                creationTimestamps = System.currentTimeMillis()
            )
        } else allTariffsForProduit

        val sortedTariffs = tariffsWithEditable.sortedBy { tariff ->
            when (tariff.typeChoisi) {
                TypeChoisi.Prix_Detaille -> 0
                TypeChoisi.Edited_Pour_Client,
                TypeChoisi.Prix_Progressive_Editable -> 1
                TypeChoisi.Prix_SupperGro_Et_PresentationService -> 2
                else -> tariff.typeChoisi.profitabilityScore
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            sortedTariffs.forEach { tariff ->
                val isSelected = tariff.keyID == relative_Tariff?.keyID
                val isInvalid = tariff.prixCurrency == 0.0
                val effectiveAllNonTrouve = allNonTrouve || isInvalid

                val tariffType = tariff.typeChoisi
                val nom = tariffType.nomArabe.take(2)
                val tariffIcon = tariffType.iconVector ?: Icons.Default.History
                // Toujours texte blanc sur fond coloré (fix Prix_Detaille text invisible)
                val textColor = Color.White
                val containerColor = if (effectiveAllNonTrouve) blinkColor else tariffType.couleur

                val isEditable = tariffType == TypeChoisi.Prix_Progressive_Editable ||
                        tariffType == TypeChoisi.Edited_Pour_Client

                val selectedBorderModifier = if (isSelected)
                    Modifier.border(width = 2.dp, color = Color.Red, shape = RoundedCornerShape(20.dp))
                else Modifier

                if (isEditable) {
                    // ── Tariff éditable : champ inline pour saisir le prix ──
                    Card(
                        modifier = selectedBorderModifier.clickable { /* sélection via onPriceUpdated */ },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Double_OutlinedText_Avec_Click_Button_Modulable_Proto0(
                                value = tariff.prixCurrency,
                                onValueChanged = { newPrice ->
                                    if (newPrice <= 0) return@Double_OutlinedText_Avec_Click_Button_Modulable_Proto0
                                    val updatedTariff = tariff.copy(
                                        prixCurrency = newPrice,
                                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                    )
                                    aCentralFacade.repositorysMainSetter.update_M13TarificationInfos(updatedTariff)
                                    // Appliquer immédiatement sur les opérations
                                    val updatedOps = relative_List_M10OperationVentCouleur.map { op ->
                                        op.copy(
                                            parentM13TarificationKeyID = updatedTariff.keyID,
                                            prix_de_Vent_entre_directement_NewProto = newPrice,
                                            typeTarificationEnumT2 = tariffType,
                                            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                        )
                                    }
                                    updatedOps.forEach { op ->
                                        aCentralFacade.repositorysMainSetter.update_M10OperationVentCouleur(op)
                                    }
                                    focusedVarsHandlerFacade.focusedValuesSetter
                                        .setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(relative_produit)
                                },
                                compact_taille = true,
                                containerColor = containerColor,
                                textColor = textColor,
                            )
                        }
                    }
                } else {
                    // ── Tariff normal : Card affichage seul ─────────────────
                    Card(
                        modifier = selectedBorderModifier.clickable(enabled = !effectiveAllNonTrouve) {
                            // 1. Lier le tariff à toutes les opérations du produit
                            aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                                m13TarificationInfos_Pour_Produit = tariff,
                                m10OperationVentCouleurs = relative_List_M10OperationVentCouleur,
                                aCentralFacade = aCentralFacade
                            )
                            // 2. Mettre à jour le snapshot prix sur chaque opération
                            val updatedOps = relative_List_M10OperationVentCouleur.map { op ->
                                op.copy(
                                    parentM13TarificationKeyID = tariff.keyID,
                                    prix_de_Vent_entre_directement_NewProto = tariff.prixCurrency,
                                    typeTarificationEnumT2 = tariff.typeChoisi,
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )
                            }
                            updatedOps.forEach { op ->
                                aCentralFacade.repositorysMainSetter.update_M10OperationVentCouleur(op)
                            }
                            focusedVarsHandlerFacade.focusedValuesSetter
                                .setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(relative_produit)
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "$nom - ${tariff.prixCurrency}",
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
        }
    }
}
