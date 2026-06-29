package Application4.App.Fragment.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main.toastLogIfErr
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import Application4.App.Fragment.View.Components.A_Header.View.A_Compact_Header_App4
import Application4.App.Fragment.View.Components.Big_Principale_FragID3
import Application4.App.Fragment.View.Components.SubColorCard_WithButton
import EntreApps.Shared.Models.Home.find_ListM3CouleurInfos_By_Parent_Produit_KeyID
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.A.View.Expanded_Multi_Couleurs.View.Functions.findMatchingColorIndex
import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import android.net.Uri
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.io.FileOutputStream
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun A_Item_Produit_App4(
    relative_M1produit: M01Produit,
    modifier: Modifier = Modifier,
    onCategoryClick: (() -> Unit)? = null,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    relative_ListM3Couleurs_override: List<M3CouleurProduitInfos>? = null,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit,
    affiche_buttons_lien_unite_couleur_au_couleut_parent: Boolean = false,
) {
    val (uiState, viewModel) = uiState_NewProtoPatterns_viewModel
    val wifiState by viewModel.wifiState.collectAsState()
    val centralValues = viewModel.active_Datas
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val allColorsForProduit = relative_ListM3Couleurs_override
        ?: remember(viewModel.active_Datas.list_M03CouleurProduitInfos) {
            find_ListM3CouleurInfos_By_Parent_Produit_KeyID(
                viewModel.active_Datas.list_M03CouleurProduitInfos ?: emptyList(),
                relative_M1produit.keyID
            )
        }

    val isEchatillantsMode = centralValues.filterAffichageMode_Proto == Filter_Affichage_Mode_Proto.Echants_Seulement

    val relative_ListM3Couleurs = remember(allColorsForProduit, isEchatillantsMode) {
        if (isEchatillantsMode) allColorsForProduit.filter { it.its_in_echantiallants }
        else allColorsForProduit
    }

    val expanded_M1Produit = wifiState.expanded_M1Produit
    val expanded_M3CouleurProduitInfos = wifiState.expanded_M3CouleurProduitInfos

    val isThisProductExpanded = remember(expanded_M1Produit) {
        expanded_M1Produit?.keyID == relative_M1produit.keyID
    }
    val shouldShowButtons = true

    val initialColorIndex = remember(expanded_M3CouleurProduitInfos, relative_ListM3Couleurs) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            if (expandedColor.parentBProduitOldID == relative_M1produit.id) {
                val matchingIndex = findMatchingColorIndex(
                    expandedColor = expandedColor,
                    availableColors = relative_ListM3Couleurs
                )
                if (matchingIndex != -1) matchingIndex else 0
            } else 0
        } ?: 0
    }

    var big_presenter_couleur_produit by remember(initialColorIndex) {
        mutableStateOf(initialColorIndex)
    }
    val m3 = relative_ListM3Couleurs.getOrNull(big_presenter_couleur_produit)
    var mode_selection_parent_couleur by remember { mutableStateOf<M3CouleurProduitInfos?>(null) }
    LaunchedEffect(expanded_M3CouleurProduitInfos, relative_ListM3Couleurs) {
        expanded_M3CouleurProduitInfos?.let { expandedColor ->
            if (expandedColor.parentBProduitOldID == relative_M1produit.id) {
                val matchingIndex = findMatchingColorIndex(
                    expandedColor = expandedColor,
                    availableColors = relative_ListM3Couleurs
                )
                if (matchingIndex != -1 && matchingIndex != big_presenter_couleur_produit) {
                    big_presenter_couleur_produit = matchingIndex
                }
            }
        }
    }

    val datasValue_distinct_type =
        uiState.list_M13TarificationInfos
            .filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
            .groupBy { it.typeChoisi }
            .mapValues { (_, tariffs) -> tariffs.maxByOrNull { it.creationTimestamps } }
            .values
            .filterNotNull()

    val activeM9compt = centralValues.active_M9Compt

    val tariff_ItsWorkInGrossist_SuperGros by remember {
        derivedStateOf {
            datasValue_distinct_type.find {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                        it.prixCurrency != 0.0
            }
        }
    }

    val supperGro = datasValue_distinct_type.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService &&
                it.prixCurrency != 0.0
    }

    val detaille = datasValue_distinct_type.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Detaille &&
                it.prixCurrency != 0.0
    }

    val editedPourClient = datasValue_distinct_type.find {
        it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                it.prixCurrency != 0.0
    }

    val new_Prix_Progressive_Editable = remember(relative_M1produit.keyID) {
        M13TarificationInfos.get_default().copy(
            typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
            parent_M1Produit_KeyId = relative_M1produit.keyID,
        )
    }

    val tariff_algorithme_De_Start = editedPourClient ?: supperGro ?: detaille

    var selectedTariffKeyID by remember(tariff_algorithme_De_Start?.keyID) {
        mutableStateOf(tariff_algorithme_De_Start?.keyID ?: new_Prix_Progressive_Editable.keyID)
    }

    val selectedTariff by remember(selectedTariffKeyID, datasValue_distinct_type) {
        derivedStateOf {
            datasValue_distinct_type.find { it.keyID == selectedTariffKeyID }
                ?: new_Prix_Progressive_Editable
        }
    }

    if (relative_ListM3Couleurs.isEmpty()) return

    val safeIndex = big_presenter_couleur_produit.coerceIn(0, relative_ListM3Couleurs.lastIndex)
    if (safeIndex != big_presenter_couleur_produit) big_presenter_couleur_produit = safeIndex
    val selectedCouleur = relative_ListM3Couleurs[safeIndex]

    var isUserManuallySelectedTariff by remember(relative_M1produit.keyID, selectedCouleur.keyID) {
        mutableStateOf(false)
    }

    val activeM10ForSelectedCouleur by remember(
        selectedCouleur.keyID,
        viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
    ) {
        derivedStateOf {
            viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                ?.find { it.parent_M3CouleurProduit_KeyID == selectedCouleur.keyID }
        }
    }

    LaunchedEffect(activeM10ForSelectedCouleur) {
        if (isUserManuallySelectedTariff) return@LaunchedEffect
        val opTariffKeyID = activeM10ForSelectedCouleur?.parentM13TarificationKeyID
            ?: return@LaunchedEffect
        if (datasValue_distinct_type.any { it.keyID == opTariffKeyID }) {
            selectedTariffKeyID = opTariffKeyID
        }
    }

    val hasPremierCheckDonne by remember(
        viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state,
        relative_M1produit.keyID
    ) {
        derivedStateOf {
            viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                .any {
                    it.parent_M1Produit_KeyId == relative_M1produit.keyID &&
                            it.premier_Check_Donne
                }
        }
    }

    val itemBackgroundColor by animateColorAsState(
        targetValue = if (hasPremierCheckDonne) Color(0xFFFFFF00).copy(alpha = 0.35f)
        else Color.Transparent,
        animationSpec = tween(durationMillis = 400),
        label = "itemPremierCheckBackground"
    )

    val cardPadding = if (isThisProductExpanded) 8.dp else 4.dp

    val isAdmin = centralValues.currentApp_Est_Admin
            && viewModel.active_Datas.active_M9Compt?.affiche_ProduitDataBaseEdites_ComposableViews == true
    val categoryClickForHeader: (() -> Unit)? = if (isAdmin) onCategoryClick else null

    Column(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = relative_M1produit, key = SemanticsPropertyKey("m1"))
            }
            .fillMaxWidth()
            .background(itemBackgroundColor, RoundedCornerShape(8.dp))
            .padding(cardPadding)
    ) {
        A_Compact_Header_App4(
            prix_achat = tariff_ItsWorkInGrossist_SuperGros?.prixCurrency,
            relative_M1produit = relative_M1produit,
            isExpanded = isThisProductExpanded,
            onUpdateTariff = {
                activeM9compt?.let { appCompt ->
                    viewModel.setActiveFocuceTariffPrixDifineur(relative_M1produit, appCompt)
                }
            },
            onUpdateProduit = { viewModel.update_m1Produit(it) },
            affiche_ProduitDataBaseEdites_ComposableViews = centralValues.currentApp_Est_Admin
                    && viewModel.active_Datas.active_M9Compt?.affiche_ProduitDataBaseEdites_ComposableViews == true,
            affiche_buttons_lien_unite_couleur_au_couleut_parent = affiche_buttons_lien_unite_couleur_au_couleut_parent,
            onDelete = { viewModel.delete_m1Produit(it) },
            modifier = modifier,
            onCategoryClick = categoryClickForHeader,
            section_ToggleButton_TagPreiorities__start_Collapsed = viewModel.active_Datas.section_ToggleButton_TagPrioriter__start_Collapsed == true,
            onSetPremierCheckDonneForAllVents = {
                val currentList = viewModel.active_Datas
                    .listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                val now = System.currentTimeMillis()
                val shouldActivate = !hasPremierCheckDonne
                val updated = currentList.map { op ->
                    if (op.parent_M1Produit_KeyId == relative_M1produit.keyID)
                        op.copy(
                            premier_Check_Donne = shouldActivate,
                            dernierTimeTampsSynchronisationAvecFireBase = now
                        )
                    else op
                }
                viewModel.update_listM10OperationVentCouleur(updated)
            },
            onAddNewColor = {
                scope.launch {
                    try {
                        val colorIndex = run {
                            val existingColors = viewModel.active_Datas.list_M03CouleurProduitInfos ?: emptyList()
                            val prodColors = existingColors.filter { it.parentBProduitOldID == relative_M1produit.id }
                            var nextIdx = 1
                            for (i in 1..9) {
                                if (prodColors.none { it.indexCouleurDansAncienProto == i }) {
                                    nextIdx = i
                                    break
                                }
                            }
                            if (nextIdx == 1 && prodColors.isNotEmpty()) prodColors.size + 1 else nextIdx
                        }

                        val newCouleur = M3CouleurProduitInfos.get_default().copy(
                            aAffiche = M3CouleurProduitInfos.Type.Nom,
                            nomCouleurStrSiSonImageDispo = "Couleur $colorIndex",
                            nomImageFichieSansEtansion = "Non Dispo",
                            indexCouleurDansAncienProto = colorIndex,
                            parentBProduitOldID = relative_M1produit.id,
                            parentBProduitInfosKeyID = relative_M1produit.keyID,
                            parentId1ProduitInfosDebugName = relative_M1produit.nom,
                            processPositioningInFactory = M3CouleurProduitInfos.ProcessPositioningInFactory.CreeDepuitRechercheRapid
                        )

                        android.util.Log.d("M03Couleur", "Click Add Color: prodId=${relative_M1produit.id}, colorIndex=$colorIndex, newKeyID=${newCouleur.keyID}")
                        viewModel.update_m3couleur(newCouleur)

                        val updatedProduit = when (colorIndex) {
                            1 -> relative_M1produit.copy(couleur1 = newCouleur.keyID)
                            2 -> relative_M1produit.copy(couleur2 = newCouleur.keyID)
                            3 -> relative_M1produit.copy(couleur3 = newCouleur.keyID)
                            4 -> relative_M1produit.copy(couleur4 = newCouleur.keyID)
                            5 -> relative_M1produit.copy(couleur5 = newCouleur.keyID)
                            6 -> relative_M1produit.copy(couleur6 = newCouleur.keyID)
                            7 -> relative_M1produit.copy(couleur7 = newCouleur.keyID)
                            8 -> relative_M1produit.copy(couleur8 = newCouleur.keyID)
                            9 -> relative_M1produit.copy(couleur9 = newCouleur.keyID)
                            else -> relative_M1produit
                        }.copy(
                            actualiseSonImage = relative_M1produit.actualiseSonImage + 1,
                            actualiseSonImageTest2 = relative_M1produit.actualiseSonImageTest2 + 1,
                            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                        )

                        viewModel.update_m1Produit(updatedProduit)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Nouvelle couleur ajoutée !",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        toastLogIfErr("Erreur lors de l'ajout de la couleur: ${e.message}", "A_Item_Produit_App4", context, isError = true)
                    }
                }
            }
        )

        Big_Principale_FragID3(
            on_update_M13TarificationInfos_par_ecriture = on_update_M13TarificationInfos_par_ecriture,
            uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
            relative_M1produit = relative_M1produit,
            selectedCouleur = selectedCouleur,
            selectedTariff = selectedTariff,
            onTariffSelected = { newTariff ->
                isUserManuallySelectedTariff = true

                val parentM13TarificationKeyID =
                    if (newTariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client) "Prix_Progressive_Editable Non Saved"
                    else newTariff.keyID

                selectedTariffKeyID = newTariff.keyID

                if (newTariff.typeChoisi == M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable) {
                    val createdTariff = viewModel.maybeCreateEditedPourClientTariff(
                        produit = relative_M1produit,
                        synthetic = newTariff,
                        datasValue_distinct_type = datasValue_distinct_type.toList(),
                    )
                    if (createdTariff != null) {
                        selectedTariffKeyID = createdTariff.keyID
                    }
                }

                val currentList = viewModel.active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                val affected = currentList?.filter { it.parent_M1Produit_KeyId == relative_M1produit.keyID }
                if (!affected.isNullOrEmpty() && newTariff.prixCurrency > 0.0) {
                    val updatedList = currentList.map { op ->
                        if (op.parent_M1Produit_KeyId == relative_M1produit.keyID)
                            op.copy(
                                parentM13TarificationKeyID = parentM13TarificationKeyID,
                                parentM13TarificationDebugInfos = newTariff.getDebugInfos(),
                                typeTarificationEnumT2 = newTariff.typeChoisi,
                                prix_de_Vent_entre_directement_NewProto = newTariff.prixCurrency,
                                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                            )
                        else op
                    }
                    viewModel.update_listM10OperationVentCouleur(updatedList)
                }
            },
            tariffsList = datasValue_distinct_type,
            isThisProductExpanded = isThisProductExpanded,
            shouldShowButtons = shouldShowButtons,
            affiche_buttons_lien_unite_couleur_au_couleut_parent = affiche_buttons_lien_unite_couleur_au_couleut_parent,
            mode_selection_parent_couleur = mode_selection_parent_couleur,
            on_pour_update_mode_selection_parent_couleur = { clickedColor ->
                if (mode_selection_parent_couleur?.keyID == clickedColor?.keyID) {
                    mode_selection_parent_couleur = null
                } else {
                    mode_selection_parent_couleur = clickedColor
                }
            },
        )

        if (relative_ListM3Couleurs.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))

            if (isThisProductExpanded) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    maxItemsInEachRow = 4
                ) {
                    relative_ListM3Couleurs.forEachIndexed { index, couleur ->
                        if (index != big_presenter_couleur_produit) {
                            SubColorCard_WithButton(
                                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                                couleur = couleur,
                                relative_M1produit = relative_M1produit,
                                selectedTariff = selectedTariff,
                                isExpanded = true,
                                modifier = Modifier.weight(1f, fill = false),
                                shouldShowButtons = shouldShowButtons,
                                affiche_buttons_lien_unite_couleur_au_couleut_parent = affiche_buttons_lien_unite_couleur_au_couleut_parent,
                                mode_selection_parent_couleur = mode_selection_parent_couleur,
                                on_pour_update_mode_selection_parent_couleur = { clickedColor ->
                                    if (mode_selection_parent_couleur?.keyID == clickedColor?.keyID) {
                                        mode_selection_parent_couleur = null
                                    } else {
                                        mode_selection_parent_couleur = clickedColor
                                    }
                                },
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    relative_ListM3Couleurs.forEachIndexed { index, couleur ->
                        if (index != big_presenter_couleur_produit) {
                            SubColorCard_WithButton(
                                uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                                couleur = couleur,
                                relative_M1produit = relative_M1produit,
                                selectedTariff = selectedTariff,
                                shouldShowButtons = shouldShowButtons,
                                isExpanded = false,
                                modifier = Modifier.fillMaxWidth(),
                                affiche_buttons_lien_unite_couleur_au_couleut_parent = affiche_buttons_lien_unite_couleur_au_couleut_parent,
                                mode_selection_parent_couleur = mode_selection_parent_couleur,
                                on_pour_update_mode_selection_parent_couleur = { clickedColor ->
                                    if (mode_selection_parent_couleur?.keyID == clickedColor?.keyID) {
                                        mode_selection_parent_couleur = null
                                    } else {
                                        mode_selection_parent_couleur = clickedColor
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
