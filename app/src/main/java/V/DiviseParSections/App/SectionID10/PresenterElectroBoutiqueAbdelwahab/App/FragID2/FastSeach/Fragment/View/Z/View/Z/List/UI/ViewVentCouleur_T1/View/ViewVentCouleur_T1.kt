package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.A.ViewModel.ViewModelsProduit_T1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View.Z.Components.ColorImageDisplayer
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View.Z.Components.View_LikedTo_FragSearcher
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.Z.ModernQuantityDialog_T1.Ui.A.Screen.Dialog_Choisire_Quantity_Modularized
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import Z_CodePartageEntreApps.Modules.CameraHandler.CameraFABProtoJuin3
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

@SuppressLint("UnrememberedMutableState")
@Composable
fun ViewVentCouleur_T1(
    modifier: Modifier = Modifier,
    relative_M3CouleurInfos: M3CouleurProduitInfos,
    produit: ArticlesBasesStatsTable,
    viewModel: ViewModelsProduit_T1,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repo03CouleurProduitInfos: Repo03CouleurProduitInfos = viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    size: Dp = 200.dp
) {
    // State for color name editing
    var isEditingColorName by remember { mutableStateOf(false) }
    var editingColorName by remember { mutableStateOf("") }
    val colorNameFocusRequester = remember { FocusRequester() }

    val relative_M10OperationVentCouleur by remember {
        derivedStateOf {
            focusedValuesGetter.onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
                .find { it.parent_M3CouleurProduit_KeyID == relative_M3CouleurInfos.keyID }
        }
    }

    val setter = viewModel.setterFocusedVarsHandlerFacade
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    fun handelUiAction(haptic: HapticFeedback) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val imageFile by derivedStateOf {
        viewModel.getImageFile(
            relative_M3CouleurInfos.nomImageFichieSansEtansion,
            relative_M3CouleurInfos.extensionDisponible
        )
    }

    val isImageAvailable by remember(imageFile) {
        derivedStateOf {
            imageFile?.exists() == true && relative_M3CouleurInfos.nomImageFichieSansEtansion != "Non Dispo"
        }
    }

    fun handleCameraCapture() {
        val updatedCouleur = relative_M3CouleurInfos.copy(
            aAffiche = M3CouleurProduitInfos.Type.Image,
            nomImageFichieSansEtansion = "${produit.id}_${relative_M3CouleurInfos.indexCouleurDansAncienProto}",
            extensionDisponible = "webp"
        )

        viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(updatedCouleur)
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Function to handle color name editing
    fun handleStartEditingColorName() {
        editingColorName = relative_M3CouleurInfos.nomCouleurStrSiSonImageDispo
        isEditingColorName = true
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun handleSaveColorName() {
        if (editingColorName.isNotBlank()) {
            val updatedCouleur = relative_M3CouleurInfos.copy(
                nomCouleurStrSiSonImageDispo = editingColorName.trim(),
                aAffiche = if (relative_M3CouleurInfos.aAffiche == M3CouleurProduitInfos.Type.Image)
                    M3CouleurProduitInfos.Type.Image
                else
                    M3CouleurProduitInfos.Type.Nom
            )

            viewModel.aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.addOrUpdateData(updatedCouleur)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        isEditingColorName = false
    }

    val defaultM10Vent = produit.let {
        M10OperationVentCouleur.get_default_By_BonVentEtCouleur(
            focusedValuesGetter.activeOnVent_M8BonVent,
            relative_M3CouleurInfos
        ).copy(
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

    val shouldShowDialog by remember(
        relative_M10OperationVentCouleur,
        relative_M3CouleurInfos.keyID
    ) {
        derivedStateOf {
            val onVentM3 = viewModel.getterFocusedVarsHandlerFacade.onVentM10VentOperation
            onVentM3?.parent_M3CouleurProduit_KeyID == relative_M3CouleurInfos.keyID
        }
    }

    val datasValue = viewModel.aCentralFacade.repositorysMainGetter.repo13TarificationInfos.datasValue
    val findTariff = datasValue.find { tariff ->
        tariff.typeChoisi == TypeChoisi.Prix_Detaille &&
                tariff.parent_M1Produit_KeyId == produit.keyID
    }

    val default_Tariff = M13TarificationInfos.get_default_P0(produit, start_Prix_Depuit_Ancient = produit.prixAchat)
    val finale_Tariff = findTariff ?: default_Tariff.first

    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(ventUIState.itemAlpha)
            .graphicsLayer(alpha = if (relative_M10OperationVentCouleur?.etateDelivery == M10OperationVentCouleur.EtateDelivery.NonTrouve) 0.5f else 1.0f)
    ) {

        if (isEditingColorName) {
            ColorNameDropdownTextField(
                value = editingColorName,
                onValueChange = { editingColorName = it },
                placeholder = "Nom définiteur",
                focusRequester = colorNameFocusRequester,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { handleSaveColorName() }
                ),
                repo03CouleurProduitInfos = repo03CouleurProduitInfos,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .zIndex(10f)
            )

            // Keep the existing LaunchedEffect blocks
            LaunchedEffect(isEditingColorName) {
                if (isEditingColorName) {
                    colorNameFocusRequester.requestFocus()
                }
            }

            LaunchedEffect(isEditingColorName) {
                if (isEditingColorName) {
                    delay(15000)
                    if (editingColorName.isBlank() || editingColorName == relative_M3CouleurInfos.nomCouleurStrSiSonImageDispo) {
                        isEditingColorName = false
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = if (!isImageAvailable && relative_M3CouleurInfos.aAffiche == M3CouleurProduitInfos.Type.Image) {
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                CardDefaults.cardColors()
            },
            border = if (!isImageAvailable && relative_M3CouleurInfos.aAffiche == M3CouleurProduitInfos.Type.Image) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            } else null
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
                    ColorImageDisplayer(
                        colorInfo = relative_M3CouleurInfos,
                        imageFile = imageFile,
                        isImageAvailable = isImageAvailable,
                        size = size,
                        colorMatrix = ventUIState.colorMatrix,
                        onClickToOpenWindow = {
                            lenceVent()
                            handelUiAction(haptic)
                        }
                    )

                    // Camera button - always visible
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = 4.dp, y = 4.dp)
                            .zIndex(2f)
                            .clickable {
                                handleCameraCapture()
                            }
                    ) {
                        CameraFABProtoJuin3(
                            size = 24.dp,
                            aCentralFacade = aCentralFacade
                        )
                    }

                    // Edit color name button - only visible when image file doesn't exist
                    if (!isImageAvailable) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = 32.dp, y = 4.dp)
                                .zIndex(2f)
                                .clickable {
                                    handleStartEditingColorName()
                                }
                        ) {
                            SmallFloatingActionButton(
                                onClick = {
                                    handleStartEditingColorName()
                                },
                                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f),
                                contentColor = MaterialTheme.colorScheme.onTertiary,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit color name",
                                    modifier = Modifier.size(12.dp)
                                )
                            }
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
                                        text = relative_M10OperationVentCouleur?.quantity.toString(),
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

                    focusedValuesGetter.active_Central_Values.affiche_Panier_au_Search_Dialog.ifTrue {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .zIndex(1f),
                        ) {
                            SmallFloatingActionButton(
                                modifier = Modifier.getSemanticsTag(
                                    relative_M10OperationVentCouleur,
                                    "relative_M10OperationVentCouleur"
                                ),
                                onClick = {
                                    val new_Data = focusedValuesGetter.active_Central_Values.copy(
                                        handled_M10OperationVent_Pour_Link = relative_M10OperationVentCouleur
                                    )
                                    focusedValuesGetter.update_activeCentralValues(new_Data)
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

                    View_LikedTo_FragSearcher(
                        relative_M10OperationVentCouleur = relative_M10OperationVentCouleur,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    if (shouldShowDialog) {
        Dialog_Choisire_Quantity_Modularized(
            old_quantity = relative_M10OperationVentCouleur!!.get_Quantity_Apre_Passe_Au_SetIN_Vent_Its_Quantity_Represent(),
            label = relative_M3CouleurInfos.nomCouleurStrSiSonImageDispo,
        ) { new_Qyt ->
            relative_M10OperationVentCouleur?.let { existingVent ->
                val updatedVent = new_Qyt?.let {
                    existingVent.copy(quantity = it)
                }

                if (updatedVent != null) {
                    viewModel.aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.addOrUpdateData(updatedVent)
                }
            }

            viewModel.setterFocusedVarsHandlerFacade.fermeDialogChoisireQuantityDeVentCouleur(
                relative_M10OperationVentCouleur!!.parent_M1Produit_KeyId
            )
        }
    }
}
