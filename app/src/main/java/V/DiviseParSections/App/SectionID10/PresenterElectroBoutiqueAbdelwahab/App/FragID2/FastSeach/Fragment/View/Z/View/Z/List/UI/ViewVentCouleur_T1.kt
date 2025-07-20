package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.Dialog_Choisire_Quantity_Modularized
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@SuppressLint("UnrememberedMutableState")
@Composable
fun ViewVentCouleur_T1(
    modifier: Modifier = Modifier,
    viewModel: ViewModelsProduit_T1,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    m3Couleur: M3CouleurProduitInfos,
    produit: ArticlesBasesStatsTable,
    size: Dp = 200.dp
) {
    val relative_M10OperationVentCouleur by remember {
        derivedStateOf {
            focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                .find { it.parent_M3CouleurProduit_KeyID == m3Couleur.keyID }
        }
    }

    val setter = viewModel.setterFocusedVarsHandlerFacade

    val uiState by viewModel.uiState.collectAsState()
    val getterFocusedVarsHandlerFacade = viewModel.getterFocusedVarsHandlerFacade
    val parentM1ProduitDebugInfos = produit.getDebugInfos() ?: "null"

    val haptic = LocalHapticFeedback.current

    fun handelUiAction(haptic: HapticFeedback) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val imageFile by derivedStateOf {
        viewModel.getImageFile(
            m3Couleur.nomImageFichieSansEtansion, m3Couleur.extensionDisponible
        )
    }

    val defaultM10Vent = produit.let {
        getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()?.copy(
            //---------------------------------Parent M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
            parent_M1Produit_KeyId = produit.keyID,
            parent_M1Produit_DebugInfos = parentM1ProduitDebugInfos,
            //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
            parent_M3CouleurProduit_KeyID = m3Couleur.keyID,
            parent_M3CouleurProduit_DebugInfos = parentM1ProduitDebugInfos + m3Couleur.indexCouleurDansAncienProto,
            setIN_Vent_Its_Quantity_Represent = produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = produit.quantite_Boit_Par_Carton,
            quantity = if (produit.setIN_Vent_Its_Quantity_Represent ==
                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
            )
                1 * produit.quantite_Boit_Par_Carton
            else 1
        )
    }

    val ventUIState = remember(relative_M10OperationVentCouleur, uiState) {
        derivedStateOf {
            viewModel.calculateUIState(
                produit, relative_M10OperationVentCouleur, uiState
            )
        }
    }.value

    val shouldShowDialog by remember(relative_M10OperationVentCouleur, m3Couleur.keyID) {
        derivedStateOf {
            val onVentM3 = viewModel.getterFocusedVarsHandlerFacade.onVentM10VentOperation

            onVentM3?.parent_M3CouleurProduit_KeyID == m3Couleur.keyID
        }
    }
    val datasValue = viewModel.aCentralFacade.repoMainGetter.repo13TarificationInfos.datasValue
    val findTariff =
        M13TarificationInfos.findTariff(datasValue, produit, TypeChoisi.DefiniParGerant)
    val default_Tariff =
        M13TarificationInfos.get_default_P0(produit, start_Prix_Depuit_Ancient = produit.prixAchat)

    val finale_Tariff = findTariff ?: default_Tariff.first

    Column(
        modifier = modifier
            .getSemanticsTag(
                nomVal = "defaultM3CouleurProduitInfos", data = defaultM10Vent
            )
            .fillMaxWidth()
            .alpha(ventUIState.itemAlpha)
            .graphicsLayer(alpha = if (relative_M10OperationVentCouleur?.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve) 0.5f else 1.0f)
    ) {
        // Image/Color display card
        Card(
            modifier = Modifier
                .getSemanticsTag(
                    relative_M10OperationVentCouleur,
                    "relative_M10OperationVentCouleur"
                )
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                fun lenceVent() {
                    relative_M10OperationVentCouleur?.let { findVent ->
                        viewModel.aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                            finale_Tariff,
                            buildList { add(findVent) }
                        )
                        setter.active_M3Couleur_pour_ouvrire_son_Dialog_choixQuantity(findVent)
                    } ?: run {
                        defaultM10Vent?.let { defaultVent ->
                            setter.ajoute_New_M10OperationVentCouleur(defaultVent)
                            viewModel.aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                                finale_Tariff,
                                buildList { add(defaultVent) }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    when (m3Couleur.aAffiche) {
                        M3CouleurProduitInfos.Type.Image -> {
                            ImageDisplayerGlide_Sec2FragID2_SearchProduit(
                                modifier = Modifier.size(size),
                                imageFile = imageFile,
                                colorName = m3Couleur.nomCouleurStrSiSonImageDispo,
                                contentScale = ContentScale.Crop,
                                imageSize = DpSize(size, size),
                                colorFilter = ventUIState.colorMatrix?.let {
                                    ColorFilter.colorMatrix(
                                        it
                                    )
                                },
                                onClickToOpenWindow = {
                                    lenceVent()
                                    handelUiAction(haptic)
                                },
                            )
                        }

                        M3CouleurProduitInfos.Type.Nom -> {
                            ColorNameDisplayer_Sec2FragID2(
                                modifier = Modifier.size(size),
                                colorName = m3Couleur.nomCouleurStrSiSonImageDispo,
                                onClickToOpenWindow = {
                                    lenceVent()
                                    handelUiAction(haptic)
                                })
                        }
                    }

                    if (ventUIState.isRemoved) {
                        Surface(
                            modifier = Modifier.align(Alignment.Center),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "REMOVED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (ventUIState.quantity > 0 && !ventUIState.isRemoved) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(
                                        text = relative_M10OperationVentCouleur?.quantity
                                            .toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }, modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Box(modifier = Modifier.size(16.dp))
                        }
                    }

                    val handled_M10OperationVent_Pour_Link by remember {
                        derivedStateOf {
                            focusedValuesGetter.active_Central_Values.handled_M10OperationVent_Pour_Link
                        }
                    }
                    val its_Pour_Link by remember {
                        derivedStateOf {
                            handled_M10OperationVent_Pour_Link?.keyID == relative_M10OperationVentCouleur?.keyID
                        }
                    }

                    focusedValuesGetter.active_Central_Values
                        .affiche_Panier_au_Search_Dialog.ifTrue {

                            Column(
                                modifier = Modifier
                                    .getSemanticsTag(
                                        relative_M10OperationVentCouleur,
                                        "relative_M10OperationVentCouleur"
                                    )
                                    .getSemanticsTag(
                                        handled_M10OperationVent_Pour_Link,
                                        "handled_M10OperationVent_Pour_Link"
                                    )
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .zIndex(1f),
                            ) {
                                SmallFloatingActionButton(
                                    onClick = {
                                        val new_Data =
                                            focusedValuesGetter.active_Central_Values.copy(
                                                handled_M10OperationVent_Pour_Link = relative_M10OperationVentCouleur
                                            )
                                        focusedValuesGetter.update_activeCentralValues(
                                            new_Data
                                        )

                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    },
                                    containerColor = if (its_Pour_Link) {
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                                    } else {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                                    },
                                    contentColor = if (its_Pour_Link) {
                                        MaterialTheme.colorScheme.onSecondary
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (its_Pour_Link) Icons.Default.Close else Icons.Default.BackHand,
                                        contentDescription = if (its_Pour_Link) "Unlink from cart" else "Link to cart",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                    relative_M10OperationVentCouleur?.let {
                        val text = it.linked_To_M10OperationVent_DebugInfos
                        if (text.isNotEmpty()) {
                            Text(
                                text = "si $text est non dispo",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (shouldShowDialog) {
        Dialog_Choisire_Quantity_Modularized(
            old_quantity = relative_M10OperationVentCouleur!!.get_Quantity_Apre_Passe_Au_SetIN_Vent_Its_Quantity_Represent(),
            label = m3Couleur.nomCouleurStrSiSonImageDispo,
        ) { new_Qyt ->

            relative_M10OperationVentCouleur?.let { existingVent ->
                val updatedVent = new_Qyt?.let {
                    existingVent.copy(
                        quantity = it,
                    )
                }

                if (updatedVent != null) {
                    viewModel.aCentralFacade.repoMainGetter.repo10OperationVentCouleur.addOrUpdateData(
                        updatedVent
                    )
                }
            }

            viewModel.setterFocusedVarsHandlerFacade.fermeDialogChoisireQuantityDeVentCouleur(
                relative_M10OperationVentCouleur!!.parent_M1Produit_KeyId
            )
        }
    }
}

