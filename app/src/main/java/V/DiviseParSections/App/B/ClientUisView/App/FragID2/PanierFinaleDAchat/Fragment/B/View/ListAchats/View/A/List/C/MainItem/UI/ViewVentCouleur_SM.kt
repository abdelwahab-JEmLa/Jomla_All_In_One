package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.ListAchats.View.A.List.C.MainItem.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ImageDisplayerGlide_Sec2FragID2_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.Dialog_Choisire_Quantity_Modularized
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedActiveValuesFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import java.io.File

@SuppressLint("UnrememberedMutableState")
@Composable
fun ViewVentCouleur_Module(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedVarsHandlerFacade: FocusedActiveValuesFacade = aCentralFacade.focusedActiveValuesFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    relative_M1Produit: ArticlesBasesStatsTable,
    relative_M3CouleurProduit: M3CouleurProduitInfos,
    size: Dp = 200.dp
) {
    val relative_M10OperationVentCouleur by remember {
        derivedStateOf {
            focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent.find {
                it.parent_M3CouleurProduit_KeyID == relative_M3CouleurProduit.keyID
            }
        }
    }

    val getterFocusedVarsHandlerFacade =
        aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter
    val focusedValuesSetter = focusedVarsHandlerFacade.focusedValuesSetter

    val parentM1ProduitDebugInfos = relative_M1Produit.getDebugInfos() ?: "null"

    val haptic = LocalHapticFeedback.current

    fun handelUiAction(haptic: HapticFeedback) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val imageFile by derivedStateOf {
        getImageFile(
            relative_M3CouleurProduit.nomImageFichieSansEtansion,
            relative_M3CouleurProduit.extensionDisponible
        )
    }


    val defaultM10Vent = relative_M1Produit.let {
        getterFocusedVarsHandlerFacade.getDefaultM10VentOperation()?.copy(
            //---------------------------------Parent M1ProduitInfos----------------------------------------------------------------------------------------------------------------------------------
            parent_M1Produit_KeyId = relative_M1Produit.keyID,
            parent_M1Produit_DebugInfos = parentM1ProduitDebugInfos,
            //---------------------------------Parent M3CouleurProduitInfos----------------------------------------------------------------------------------------------------------------------------------
            parent_M3CouleurProduit_KeyID = relative_M3CouleurProduit.keyID,
            parent_M3CouleurProduit_DebugInfos = parentM1ProduitDebugInfos + relative_M3CouleurProduit.indexCouleurDansAncienProto,
            setIN_Vent_Its_Quantity_Represent = relative_M1Produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1Produit.quantite_Boit_Par_Carton,
            quantity = if (relative_M1Produit.setIN_Vent_Its_Quantity_Represent ==
                M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
            )
                1 * relative_M1Produit.quantite_Boit_Par_Carton
            else 1
        )
    }

    val ventUIState = remember(relative_M10OperationVentCouleur) {
        derivedStateOf {
            calculateUIState(
                relative_M1Produit, relative_M10OperationVentCouleur,
            )
        }
    }.value

    val shouldShowDialog by remember(
        relative_M10OperationVentCouleur,
        relative_M3CouleurProduit.keyID
    ) {
        derivedStateOf {
            val onVentM3 = getterFocusedVarsHandlerFacade.onVentM10VentOperation

            onVentM3?.parent_M3CouleurProduit_KeyID == relative_M3CouleurProduit.keyID
        }
    }
    val datasValue =
        aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue

    val findTariff = datasValue.lastOrNull { tariff ->
        tariff.parent_M1Produit_KeyId == relative_M1Produit.keyID
                && tariff.parent_M2Client_KeyId == focusedValuesGetter.activeOnVentM2ClientInfos?.keyID
    }

    val default_Tariff =
        M13TarificationInfos.get_default_P0(
            relative_M1Produit,
            start_Prix_Depuit_Ancient = relative_M1Produit.prixAchat
        )

    val finale_Tariff = findTariff ?: default_Tariff.first

    // Main container with proper layout structure
    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(
                    value = relative_M10OperationVentCouleur,
                    key = SemanticsPropertyKey("relative_M10OperationVentCouleur")
                )
            }
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
                    nomVal = "defaultM3CouleurProduitInfos", data = defaultM10Vent
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
                        aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                            finale_Tariff,
                            buildList { add(findVent) },
                            aCentralFacade
                        )
                        focusedValuesSetter.active_M3Couleur_pour_ouvrire_son_Dialog_choixQuantity(
                            findVent
                        )
                    } ?: run {
                        defaultM10Vent?.let { defaultVent ->
                            focusedValuesSetter.ajoute_New_M10OperationVentCouleur(defaultVent)
                            aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                                finale_Tariff,
                                buildList { add(defaultVent) },
                                aCentralFacade
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    when (relative_M3CouleurProduit.aAffiche) {
                        M3CouleurProduitInfos.Type.Image -> {
                            ImageDisplayerGlide_Sec2FragID2_Panie(
                                modifier = Modifier.size(size),
                                relative_M10OperationVentCouleur = relative_M10OperationVentCouleur,
                                relative_M3CouleurProduit = relative_M3CouleurProduit,
                                imageFile = imageFile,
                                colorName = relative_M3CouleurProduit.nomCouleurStrSiSonImageDispo,
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
                                colorName = relative_M3CouleurProduit.nomCouleurStrSiSonImageDispo,
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
                                    val text = relative_M10OperationVentCouleur?.quantity
                                        .toString()
                                    val takeLast = if (true) "" else
                                        relative_M10OperationVentCouleur?.parent_M14VentPeriod_KeyId?.takeLast(
                                            3
                                        )

                                    Text(
                                        text = "$text $takeLast",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }, modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Box(modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    if (shouldShowDialog) {
        Dialog_Choisire_Quantity_Modularized(
            old_quantity = relative_M10OperationVentCouleur!!.get_Quantity_Apre_Passe_Au_SetIN_Vent_Its_Quantity_Represent(),
            setIN_Vent_Its_Quantity_Represent = relative_M1Produit.setIN_Vent_Its_Quantity_Represent,
            quantite_Boit_Par_Carton = relative_M1Produit.quantite_Boit_Par_Carton,
            label = relative_M3CouleurProduit.nomCouleurStrSiSonImageDispo,
        ) { new_Qyt ->

            relative_M10OperationVentCouleur?.let { existingVent ->
                val updatedVent = new_Qyt?.let {
                    existingVent.copy(
                        quantity = it,
                    )
                }

                if (updatedVent != null) {
                    aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.addOrUpdateData(
                        updatedVent
                    )
                }
            }

            focusedValuesSetter.fermeDialogChoisireQuantityDeVentCouleur(
                relative_M10OperationVentCouleur!!.parent_M1Produit_KeyId
            )
        }
    }
}

data class ViewVentUIState(
    val ventKey: String = "",
    val quantity: Int = 0,
    val isRemoved: Boolean = false,
    val itemAlpha: Float = 1.0f,
    val colorMatrix: ColorMatrix? = null
)

fun calculateUIState(
    produit: ArticlesBasesStatsTable,
    existingVent: M10OperationVentCouleur?,
): ViewVentUIState {
    val ventKey = existingVent?.keyID ?: ""
    val isRemoved =
        existingVent?.etateActuellementEst == M10OperationVentCouleur.EtateActuellementEst.SUPP_AU_PANIER_FINALE

    val existingVentQ = existingVent?.quantity ?: 0
    val quantity = if (produit.setIN_Vent_Its_Quantity_Represent ==
        M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Carton
    ) {
        existingVentQ * produit.quantite_Boit_Par_Carton
    } else {
        existingVentQ
    }

    return ViewVentUIState(
        ventKey = ventKey,
        quantity = quantity,
        isRemoved = isRemoved,
        itemAlpha = if (isRemoved) 0.4f else 1.0f,
        colorMatrix = if (isRemoved) ColorMatrix().apply { setToSaturation(0f) } else null
    )
}

fun getImageFile(nomImageFichieSansEtansion: String, extensionDisponible: String): File? =
    if (nomImageFichieSansEtansion != "Non Dispo")
        File(
            "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne",
            "$nomImageFichieSansEtansion.$extensionDisponible"
        )
    else null
