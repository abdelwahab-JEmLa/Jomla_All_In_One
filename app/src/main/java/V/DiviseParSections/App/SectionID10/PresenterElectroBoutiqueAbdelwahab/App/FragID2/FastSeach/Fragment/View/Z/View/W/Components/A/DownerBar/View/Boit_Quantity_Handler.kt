package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.A.DownerBar.View

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ZZ.MainList.Genere_Tariffs_currentApp_ItsWorkChezGrossisst
import V.DiviseParSections.App.Shared.Modules.Ui.FastEdite_OutlinedTextField.View.V.Proto.OutlinedText_Avec_Init_Click_Button_Modulable
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag_By_datas_A_Affiche_Au_Nom
import EntreApps.Shared.Models.Home.ActiveCentralValues
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import EntreApps.Shared.Models.M01Produit
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import EntreApps.Shared.Models.M13TarificationInfos
import EntreApps.Shared.Models.M13TarificationInfos.TypeChoisi
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
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun Boit_Quantity_Handler(
    produit: M01Produit,
    allNonTrouve: Boolean,
    aCentralFacade: ACentralFacade,
    onRequestSearchFocus: () -> Unit = {},
    focusedValuesGetter: FocusedValuesGetter= koinInject(),
    mainGetter: RepositorysMainGetter=koinInject()
) {
    val its_fournisseur = focusedValuesGetter.activeOnVentM2ClientInfos?.its_Fournisseur
    val achat_tariff = mainGetter.find_List_Tariffs_Du_Produit(produit.keyID, true)

    // TODO(1) fixed: find the Achat tariff for this product (grossist only)
    val achat_Tariff_ItsWorkInGrossist: M13TarificationInfos? = achat_tariff.find {
        it.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_Achat
    }

    // TODO(1) fixed: if its_fournisseur, display Tariff_ItsWorkInGrossist_Achat
    //   The resolved tariff to show in the price card for fournisseur clients:
    //     - use the real Achat tariff when available
    //     - fall back to a default 0.0 placeholder so the UI is never blank
    val affiche_Achat_Tariff: M13TarificationInfos? = if (its_fournisseur == true) {
        achat_Tariff_ItsWorkInGrossist ?: M13TarificationInfos(
            typeChoisi = TypeChoisi.Tariff_ItsWorkInGrossist_Achat,
            prixCurrency = 0.0,
            parent_M1Produit_KeyId = produit.keyID,
            parent_M1Produit_DebugInfos = produit.nom
        )
    } else null

    // TODO(2) fixed: LaunchedEffect – when its_fournisseur and no Achat tariff exists yet,
    //   create one with prixCurrency = 0.0, persist it, then update every related vent
    //   so they all point to the new tariff.
    LaunchedEffect(produit.keyID, its_fournisseur) {
        if (its_fournisseur == true && achat_Tariff_ItsWorkInGrossist == null) {
            val newAchatTariff = M13TarificationInfos(
                typeChoisi = TypeChoisi.Tariff_ItsWorkInGrossist_Achat,
                prixCurrency = 0.0,
                parent_M1Produit_KeyId = produit.keyID,
                parent_M1Produit_DebugInfos = produit.nom
            )
            // Persist the new tariff
            mainGetter.repo13TarificationInfos.add(newAchatTariff)

            // Update every vent for this product so it references the new Achat tariff
            val relatedVents = mainGetter.repo10OperationVentCouleur.datasValue.filter {
                it.parent_M1Produit_KeyId == produit.keyID
            }
            relatedVents.forEach { vent ->
                mainGetter.repo10OperationVentCouleur.addOrUpdateData(
                    vent.copy(
                        parentM13TarificationKeyID = newAchatTariff.keyID,
                        parentM13TarificationDebugInfos = newAchatTariff.getDebugInfos(),
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    val focusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedVarsHandlerFacade = aCentralFacade.focusedActiveValuesFacade
    val repositorysMainSetter = aCentralFacade.repositorysMainSetter
    val repositorysMainGetter = aCentralFacade.repositorysMainGetter
    val currentApp_ItsWorkChezGrossisst = focusedValuesGetter.currentApp_ItsWorkChezGrossisst

    val getterFocusedVarsHandlerFacade =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

    val totalQuantity by derivedStateOf {
        getterFocusedVarsHandlerFacade
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { ventOperation ->
                ventOperation.parent_M1Produit_KeyId == produit.keyID
            }.sumOf { it.quantity }
    }

    val superGrosTariff = if (currentApp_ItsWorkChezGrossisst) {
        Genere_Tariffs_currentApp_ItsWorkChezGrossisst()
            .find_existing_Tariff_Grossist_SuperGros(aCentralFacade, produit)
    } else null

    val repo3CouleurProduitInfos = repositorysMainGetter.repo03CouleurProduitInfos
    val repo10OperationVentCouleur = repositorysMainGetter.repo10OperationVentCouleur
    val repo13TarificationInfos = repositorysMainGetter.repo13TarificationInfos

    val affiche_Produit_OnGrid = ActiveCentralValues.get_Default().affiche_Produit_OnGrid
    val datasValue = aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue

    val findTariff = datasValue.find { tariff ->
        tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                tariff.parent_M1Produit_KeyId == produit.keyID
    }

    val default_Tariff = M13TarificationInfos.get_default_P0(
        produit,
        start_Prix_Depuit_Ancient = produit.prixAchat
    )
    val finale_Tariff = findTariff ?: default_Tariff.first
    val shouldUseManagerColors = finale_Tariff.laisse_Au_Gerant

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = if (affiche_Produit_OnGrid) Alignment.CenterHorizontally else Alignment.Start
    ) {
        // Row 1: Quantity using the new reusable component
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedText_Avec_Init_Click_Button_Modulable(
                start_count = totalQuantity,
                icon = Icons.Default.ShoppingCart,
                allNonTrouve = allNonTrouve,
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
            ) { newQuantity ->
                when {
                    newQuantity == 1 && totalQuantity == 0 -> {
                        // First click: Create new vent with quantity 1
                        val productColors = repo3CouleurProduitInfos.datasValue.filter {
                            it.parentBProduitInfosKeyID == produit.keyID
                        }

                        if (productColors.isNotEmpty()) {
                            val defaultVent =
                                getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()

                            if (defaultVent != null) {
                                val firstColor = productColors.first()

                                val existingTariff = if (currentApp_ItsWorkChezGrossisst) {
                                    repo13TarificationInfos.datasValue.find { tariff ->
                                        tariff.parent_M1Produit_KeyId == produit.keyID &&
                                                tariff.typeChoisi == TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
                                    }
                                } else {
                                    repo13TarificationInfos.datasValue.find { tariff ->
                                        tariff.parent_M1Produit_KeyId == produit.keyID &&
                                                tariff.typeChoisi == TypeChoisi.Prix_Detaille
                                    }
                                }

                                val tariffToUse = if (existingTariff != null) {
                                    existingTariff
                                } else {
                                    val newTariff = if (currentApp_ItsWorkChezGrossisst) {
                                        val activeClient =
                                            focusedValuesGetter.activeOnVentM2ClientInfos

                                        val startingPrice =
                                            if (activeClient?.its_Fournisseur == true) {
                                                produit.prixAchat
                                            } else {
                                                0.0
                                            }

                                        M13TarificationInfos(
                                            typeChoisi = TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros,
                                            prixCurrency = startingPrice,
                                            parent_M1Produit_KeyId = produit.keyID,
                                            parent_M1Produit_DebugInfos = produit.nom
                                        )
                                    } else {
                                        M13TarificationInfos.get_default_P0(
                                            produit,
                                            start_Prix_Depuit_Ancient = produit.prixAchat
                                        ).first
                                    }

                                    repo13TarificationInfos.add(newTariff)
                                    newTariff
                                }

                                val newVent = defaultVent.copy(
                                    keyID = getPushFireBase(M10OperationVentCouleur.ref),
                                    parent_M1Produit_KeyId = produit.keyID,
                                    parent_M1Produit_DebugInfos = produit.nom,
                                    parent_M3CouleurProduit_KeyID = firstColor.keyID,
                                    parent_M3CouleurProduit_DebugInfos = "${produit.nom}_${firstColor.indexCouleurDansAncienProto}",
                                    parentM13TarificationKeyID = tariffToUse.keyID,
                                    parentM13TarificationDebugInfos = tariffToUse.getDebugInfos(),
                                    quantity = 1,
                                    etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme,
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )

                                repo10OperationVentCouleur.addOrUpdateData(newVent)

                                if (existingTariff == null) {
                                    repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                                        m13TarificationInfos_Pour_Produit = tariffToUse,
                                        m10OperationVentCouleurs = listOf(newVent),
                                        aCentralFacade = aCentralFacade
                                    )
                                }
                            }
                        }
                    }

                    newQuantity > 0 -> {
                        // Update existing quantity
                        val existingVent = focusedValuesGetter
                            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                            .find { it.parent_M1Produit_KeyId == produit.keyID }

                        if (existingVent != null) {
                            val updatedVent = existingVent.copy(
                                quantity = newQuantity,
                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                            )
                            repo10OperationVentCouleur.addOrUpdateData(updatedVent)
                        }

                        onRequestSearchFocus()
                    }

                    newQuantity == 0 -> {
                        // Delete vent when quantity is 0
                        val existingVent = focusedValuesGetter
                            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                            .find { it.parent_M1Produit_KeyId == produit.keyID }

                        if (existingVent != null) {
                            repo10OperationVentCouleur.delete(existingVent)
                        }

                        onRequestSearchFocus()
                    }
                }
            }
        }

        // Row 2: Price + Carton choice (unchanged)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    modifier = Modifier.padding(
                        horizontal = if (affiche_Produit_OnGrid) 8.dp else 12.dp,
                        vertical = if (affiche_Produit_OnGrid) 4.dp else 6.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val displayText = if (currentApp_ItsWorkChezGrossisst) {
                        when {
                            // Fournisseur: show Achat tariff (or "0.0 DA" placeholder)
                            its_fournisseur == true -> {
                                val price = affiche_Achat_Tariff?.prixCurrency ?: 0.0
                                "${price} DA"
                            }
                            // Regular grossist client: show SuperGros tariff as before
                            else -> superGrosTariff?.let { tariff ->
                                "${tariff.prixCurrency} DA"
                            } ?: "غير متوفر"
                        }
                    } else {
                        if (affiche_Produit_OnGrid) {
                            "${finale_Tariff.prixCurrency} DA"
                        } else {
                            "اضغط لاظهار السعر"
                        }
                    }

                    Text(
                        text = displayText,
                        style = if (affiche_Produit_OnGrid) MaterialTheme.typography.labelSmall
                        else MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            allNonTrouve -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            shouldUseManagerColors -> Color.Black
                            else -> MaterialTheme.colorScheme.onSecondary
                        }
                    )

                    if (!affiche_Produit_OnGrid) {
                        val tariffIcon = if (findTariff != null) Icons.Default.TrendingUp else Icons.Default.History
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
    }
}
