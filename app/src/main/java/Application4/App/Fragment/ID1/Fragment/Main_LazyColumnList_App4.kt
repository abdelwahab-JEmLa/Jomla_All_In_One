package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.View.A_Item_Produit_App4
import Application4.App.Modules.Wi.Module.HandlePresenterClientScroll
import Application4.App.Modules.Wi.Module.HandlePresenterScrollBroadcast
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.get_ListM21CataloguesCategorie
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Main_LazyColumnList_App4(
    modifier: Modifier = Modifier,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit,
    mode: Filter_Affichage_Mode_Proto,       //<--
    //TODO(1): pk mem si mode	Panie 
    ventCouleurs: List<M10OperationVentCouleur>,
    relative_m3_Couleurs: List<M3CouleurProduitInfos>?,
) {
    val gridState = rememberLazyStaggeredGridState()
    val viewModel = uiState_NewProtoPatterns_viewModel.second
    val activeDatas = viewModel.active_Datas
    val wifiState by viewModel.wifiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val set_couleursKey_echantilliants_achat by remember {
        derivedStateOf {
            activeDatas.list_M10OperationVentCouleur
                ?.sortedByDescending { it.creationTimestamps }
                ?.map { it.parent_M3CouleurProduit_KeyID }
                ?: emptyList()
        }
    }

    val isHostPhone = wifiState.isHostPhone
    val isConnected = wifiState.isConnected
    val currentScrollPosition = wifiState.mainGridScrollPosition
    val isScrollEnabled = isHostPhone || !isConnected

    val finale_filtred_list by remember {
        derivedStateOf {
            // Read ALL inputs directly from activeDatas so that derivedStateOf tracks
            // them as Compose state. Parameters like `mode`, `ventCouleurs`, and
            // `relative_m3_Couleurs` are plain captured values — they don't trigger
            // recomputation when they change between recompositions.
            val currentMode   = activeDatas.filterAffichageMode_Proto
            val currentColors = activeDatas.list_M03CouleurProduitInfos
            val currentVents  = activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
            activeDatas.filter_relode_tiger  // touch to track manual reload trigger
            ProductListFilterLogic.compute(
                rawColors = currentColors,
                productMap = activeDatas.list_M1Produit?.associateBy { it.keyID } ?: emptyMap(),
                query = activeDatas.filter_echatilaten.trim().lowercase(),
                mode = currentMode,
                ventCouleurs = currentVents,
                categories = activeDatas.list_M16CategorieProduit ?: emptyList(),
                catalogues = get_ListM21CataloguesCategorie(),
                echantillantsPurchaseOrder = set_couleursKey_echantilliants_achat,
                classement = activeDatas.parentProduit_Classement,
                sort_Order = currentMode.mais_sort_order,
                periode = activeDatas.active_PeriodVent
            )
        }
    }

    val gridColumns by remember {
        derivedStateOf {
            // Same fix: read from activeDatas, not from the captured `mode` parameter.
            when (activeDatas.filterAffichageMode_Proto) {
                Filter_Affichage_Mode_Proto.Echants_Seulement -> 4
                else -> 2
            }
        }
    }

    LaunchedEffect(finale_filtred_list) {
        activeDatas.parentProduit_Classement = finale_filtred_list
            .mapIndexed { index, (product, _) -> product.keyID to index }
            .toMap()
    }

    val expanded_M1Produit = wifiState.expanded_M1Produit

    LaunchedEffect(expanded_M1Produit) {
        expanded_M1Produit ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect
        val targetKeyID = expanded_M1Produit.keyID
        if (targetKeyID.isBlank()) return@LaunchedEffect
        val foundIndex =
            finale_filtred_list.indexOfFirst { (product, _) -> product.keyID == targetKeyID }
        if (foundIndex < 0) return@LaunchedEffect
        coroutineScope.launch { gridState.scrollToItem(foundIndex) }
        delay(300)
        coroutineScope.launch { gridState.animateScrollToItem(foundIndex, scrollOffset = 0) }
    }

    HandlePresenterScrollBroadcast(
        isHostPhone = isHostPhone,
        isConnected = isConnected,
        gridState = gridState,
        viewModel = viewModel
    )
    HandlePresenterClientScroll(
        isHostPhone = isHostPhone,
        scrollPosition = currentScrollPosition,
        gridState = gridState
    )

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(gridColumns),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = ventCouleurs.firstOrNull()?.toString() ?: "[]", key = SemanticsPropertyKey("ventCouleurs.first().toString()"))      //<--
                //TODO(2.C Relative Au Todo(1):
                //... et ventCouleurs_semantic_debug	M10OperationVentCouleur(keyID=-Osm-VRnahLiWy6q2KHk, creationTimestamps=1778955453109, dernierTimeTampsSynchronisationAvecFireBase=1778955453109, its_created_in_working_for_wholesaler=true, commetaire=, prix_de_Vent_entre_directement_NewProto=530.0, its_Linked_To_Autre_Vent_Si_NonDispo=false, linked_To_M10OperationVent_KeyID=, linked_To_M10OperationVent_DebugInfos=, siNonDispoParentM10Vent_it_parent_M3CouleurInfos_KeyId=, siNonDispoParentM10Vent_it_parent_M1Produit_Nom=, parent_M9AppCompt_KeyID=null, parent_M9AppCompt_DebugInfos=null, parent_M14VentPeriod_KeyId=null, parent_M14VentPeriod_DebugInfos=null, parentEPeriodVentStartDate=0, parent_M8BonVent_KeyId=-OslwWCTmrnXSGSAwoPG, parent_M8BonVent_DebugInfos=Bon[p.cli->(M2=test[ROP])) [woPG]), parent_M1Produit_KeyId=-OohnpOhLiEcL6ezu3E0, parent_M1Produit_DebugInfos=par.produit Flach Racha, parent_M1Produit_Nom=, parentProduitInfosOldId=0, parent_M3CouleurProduit_KeyID=-OohnpOhLiEcL6ezu3E-, parent_M3CouleurProduit_DebugInfos=03Coul[{U3E-}
                // To [{ACHA}
                //]], parentM13TarificationKeyID=-Osb_07F7nHJSqa0s2qJ, parentM13TarificationDebugInfos=Flach Racha[U3E0] Edited_Pour_Client, etateActuellementEst=CreeSlote, provisoireMonPrix=0.0, etateDelivery=Trouve, lence_pour_check=false, premier_Check_Donne=false, last_update_premier_Check_Donne_TimeTamps=0, non_places_au_depot=false, pas_Dispo_Pour_Aujourduit=false, typeTarificationEnumT2=Edited_Pour_Client, parentClientInfosKeyID=, parentClientName=, type=CommandeDeLui, achatParentBsonIDOld=, quantite_Boit_Par_Carton=1, quantity=1, setIN_Vent_Its_Quantity_Represent=quantity_Par_Boit, affiche_Unite_Au_Printing=true, parent_M2Client_KeyID=-OpS1KwWBzDVzNDuvROP)


                set(value = finale_filtred_list, key = SemanticsPropertyKey("finale_filtred_list"))       //<--
                //TODO(2.C Relative Au Todo(1):
                //...  ici size == 0 log.d au filter normalemnt car ilya une vent et mode panie ca affiche les couleur avec vent
            }
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled
    ) {
        finale_filtred_list.forEach { (product, colors) ->
            val isExpanded = wifiState.expanded_M1Produit?.keyID == product.keyID
            item(
                key = "product_${product.keyID}",
                span = if (isExpanded) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane
            ) {
                LazyStigerList_Produits_FragID4(
                    product = product,
                    colors = colors,
                    onCategoryClick = { onProductCategoryClick(product) },
                    justMoved = product.keyID == justMovedProductKeyID,
                    uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                    on_update_M13TarificationInfos_par_ecriture = on_update_M13TarificationInfos_par_ecriture,
                )
            }
        }
    }
}

@Composable
fun LazyStigerList_Produits_FragID4(
    modifier: Modifier = Modifier,
    product: M01Produit,
    colors: List<M3CouleurProduitInfos>,
    onCategoryClick: (() -> Unit)? = null,
    justMoved: Boolean = false,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (justMoved) Color(0xFF4CAF50).copy(alpha = 0.3f) else Color.Transparent,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "backgroundColorAnimation"
    )
    val scale by animateFloatAsState(
        targetValue = if (justMoved) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleAnimation"
    )
    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(backgroundColor, RoundedCornerShape(12.dp))
    ) {
        A_Item_Produit_App4(
            relative_M1produit = product,
            modifier = modifier,
            onCategoryClick = onCategoryClick,
            uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
            relative_ListM3Couleurs_override = colors,
            on_update_M13TarificationInfos_par_ecriture = on_update_M13TarificationInfos_par_ecriture,
        )
    }
}
