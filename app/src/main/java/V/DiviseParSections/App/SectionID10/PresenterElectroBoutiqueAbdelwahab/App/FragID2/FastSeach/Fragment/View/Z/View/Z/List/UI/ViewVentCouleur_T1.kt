package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.ColorNameDisplayer_Sec2FragID2
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.Dialog_Choisire_Quantity_Modularized
import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import Z_CodePartageEntreApps.DataBase.Main.Main.B1.B1.Base.Preview.View.A.List.ColorNameDisplayer
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
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
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import org.koin.compose.koinInject
import java.io.File

@SuppressLint("UnrememberedMutableState")
@Composable
fun ViewVentCouleur_T1(
    modifier: Modifier = Modifier,
    viewModel: ViewModelsProduit_T1,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
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
    val findTariff = M13TarificationInfos.findTariff(datasValue, produit, TypeChoisi.DefiniParGerant)
    val default_Tariff = M13TarificationInfos.get_default_P0(produit,start_Prix_Depuit_Ancient = produit.prixAchat)

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

                    val isLinked by remember(focusedValuesGetter.active_Central_Values.afficheur_Panier_Pour_Link_M10OperationVentCouleur, relative_M10OperationVentCouleur) {
                        derivedStateOf {
                            focusedValuesGetter.active_Central_Values.afficheur_Panier_Pour_Link_M10OperationVentCouleur == relative_M10OperationVentCouleur
                        }
                    }

                    SmallFloatingActionButton(
                        onClick = {
                            val currentLinkedVent = focusedValuesGetter.active_Central_Values.afficheur_Panier_Pour_Link_M10OperationVentCouleur

                            // Toggle: if currently linked to this vent, unlink it; otherwise, link to this vent
                            val newLinkedVent = if (currentLinkedVent == relative_M10OperationVentCouleur) {
                                null // Unlink
                            } else {
                                relative_M10OperationVentCouleur // Link to this vent
                            }

                            focusedValuesGetter.update_activeCentralValues(
                                focusedValuesGetter.active_Central_Values.copy(
                                    afficheur_Panier_Pour_Link_M10OperationVentCouleur = newLinkedVent
                                )
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .zIndex(1f),
                        containerColor = if (isLinked) {
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        },
                        contentColor = if (isLinked) {
                            MaterialTheme.colorScheme.onSecondary
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    ) {
                        Icon(
                            imageVector = if (isLinked) Icons.Default.LinkOff else Icons.Default.Link,
                            contentDescription = if (isLinked) "Unlink from cart" else "Link to cart",
                            modifier = Modifier.size(16.dp)
                        )
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

@SuppressLint("CheckResult")
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageDisplayerGlide_Sec2FragID2_SearchProduit(
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    imageFile: File? = null,
    colorName: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    imageSize: DpSize,
    colorFilter: ColorFilter? = null,
    onClickToOpenWindow: () -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(true) }
    val blurRadius by animateFloatAsState(
        targetValue = if (isLoading) 25f else 0f,
        animationSpec = tween(700),
        label = "blur"
    )

    val imageExists = imageFile?.exists() == true


    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .size(imageSize.width, imageSize.height)
        ) {
            if (imageExists && imageFile != null) {
                GlideImage(
                    model = imageFile,
                    contentDescription = "Color image for $colorName",
                    contentScale = contentScale,
                    colorFilter = colorFilter, // Apply the colorFilter here
                    modifier = Modifier
                        .clickable {
                            onClickToOpenWindow()
                        }
                        .fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .graphicsLayer {
                            if (blurRadius > 0f) {
                                renderEffect =
                                    BlurEffect(blurRadius, blurRadius, TileMode.Decal)
                            }
                        }
                ) { request ->
                    request.apply {
                        thumbnail(0.1f)
                        transition(DrawableTransitionOptions.withCrossFade())
                        diskCacheStrategy(DiskCacheStrategy.ALL)
                        priority(Priority.HIGH)
                        signature(ObjectKey(imageFile.absolutePath))
                        listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>,
                                isFirstResource: Boolean
                            ) = false

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: Target<Drawable>?,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                if (isFirstResource) isLoading = false
                                return false
                            }
                        })
                    }
                }
            } else {
                ColorNameDisplayer(
                    modifier = Modifier.fillMaxSize(),
                    colorName = colorName,
                    onClickToOpenWindow = onClickToOpenWindow
                )
            }


        }
    }
}
