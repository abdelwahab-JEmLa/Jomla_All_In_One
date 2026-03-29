package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Z.Archive.UiState_NewProtoPatterns
import Application4.App.Fragment.View.A_Item_Produit_App4
import Application4.App.Fragment.Z.Components.Modules.HandlePresenterClientScroll
import Application4.App.Fragment.Z.Components.Modules.HandlePresenterScrollBroadcast
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M01Produit
import EntreApps.Shared.Models.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Etager_LazyColumn(
    modifier: Modifier = Modifier,
    on_pour_send_data: (String, String) -> Unit,
    onProductCategoryClick: (M01Produit) -> Unit,
    justMovedProductKeyID: String?,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>
) {        //<--
//TODO(1): pk mem si  Firestore network enabled
//14:14:18.134 ProfileInstaller  Installing profile for com.example.clientjetpack
//14:14:18.958 SeedInit          getReturne_M1_3_16: isOnline=true
//14:14:18.961                   seedColors: fetching ref keys…
//14:14:19.610                   seedColors: allowedKeys.size=11  keys=[-OWDMGWJE1IlVe7YkCgU, -OWDMGXQojQ1mNCpEcsm, -OWDMI15250V4yS6VuGN, -OWDMI1I8lW5kiKr0gB-, -OWDMI1PhERZd41GeVK5, -OWDMI2WGsY9y99veXeC, -OWDMJwZusjMEGv-M_GQ, -OWDMKvyl6_NwOtMXBNF, -OatXXVji-6A_OPcfUQa, -OatXcvef9wYkYbA5dC3, -Od4O2OxzMd_1Dxy3Luf]
//14:14:19.628                   seedColors: seededFilterKeys.size=11
//14:14:21.727 GoogleApiManager  Failed to get service from broker. 
//java.lang.SecurityException: Unknown calling package name 'com.google.android.gms'.
//	at android.os.Parcel.createExceptionOrNull(Parcel.java:2376)
//	at android.os.Parcel.createException(Parcel.java:2360)
//	at android.os.Parcel.readException(Parcel.java:2343)
//	at android.os.Parcel.readException(Parcel.java:2285)
//	at bdli.a(:com.google.android.gms@260834022@26.08.34 (150400-876566425):36)
//	at bdjj.z(:com.google.android.gms@260834022@26.08.34 (150400-876566425):150)
//	at bcpp.run(:com.google.android.gms@260834022@26.08.34 (150400-876566425):42)
//	at android.os.Handler.handleCallback(Handler.java:938)
//	at android.os.Handler.dispatchMessage(Handler.java:99)
//	at cqiw.me(:com.google.android.gms@260834022@26.08.34 (150400-876566425):1)
//	at cqiw.dispatchMessage(:com.google.android.gms@260834022@26.08.34 (150400-876566425):5)
//	at android.os.Looper.loop(Looper.java:236)
//	at android.os.HandlerThread.run(HandlerThread.java:67)
//14:14:21.729                   Not showing notification since connectionResult is not user-facing: ConnectionResult{statusCode=DEVELOPER_ERROR, resolution=null, message=null, clientMethodKey=null}
//14:14:22.978 SeedInit          seedColors: raw Firebase colors count=1
//14:14:22.978                   seedColors: seededColors after filter=0 ⚠️ ALL FILTERED OUT — keyID mismatch? sample keyIDs=[-Ootin901pNKivDWlQIw]
//14:14:22.982                   seedProducts: seededColors.size=0
//14:14:22.982                   seedProducts: ⚠️ seededColors is empty — products will be empty too. Check seedColors logs above.
//14:14:22.984                   seedCategories: seededProducts.size=0
//14:14:22.984                   seedCategories: distinct category ids=0  ids=[]
//14:14:24.235                   seedCategories: raw Firebase categories count=218
//14:14:24.237                   seedCategories: seededCategories after filter=0 
//rien ne s affiche ici 
    val gridState = rememberLazyStaggeredGridState()
    val viewModel = uiState_NewProtoPatterns_viewModel.second
    val activeDatas = viewModel.active_Datas
    val wifiState by viewModel.wifiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val isHostPhone = wifiState.isHostPhone
    val isConnected = wifiState.isConnected
    val currentScrollPosition = wifiState.mainGridScrollPosition
    val tag = if (isHostPhone) "📱 ServerScreen_FragID4" else "📱 ClientScreen_FragID4"
    val isScrollEnabled = isHostPhone || !isConnected

    val expanded_M3CouleurProduitInfos = wifiState.expanded_M3CouleurProduitInfos

    // Grouper M3 → M1, trier par classement_By_FilterKeys_M3.
    // Aucune dépendance sur M16 ou M21 — la source unique est list_M03CouleurProduitInfos + list_M1Produit.
    val productWithColorsList by remember {
        derivedStateOf {
            val allColours = activeDatas.list_M03CouleurProduitInfos ?: emptyList()
            val allProducts = activeDatas.list_M1Produit ?: emptyList()
            val activeFilter = activeDatas.affiche_produits_Ou_On_TagPrioriter

            // Index M1 par keyID pour lookup O(1)
            val productByKey = allProducts.associateBy { it.keyID }

            // Grouper les couleurs par produit parent
            allColours
                .groupBy { it.parentBProduitInfosKeyID }
                .mapNotNull { (produitKeyID, colours) ->
                    val product = productByKey[produitKeyID] ?: return@mapNotNull null
                    if (!product.matchesPrioriteFilter(activeFilter)) return@mapNotNull null
                    product to colours
                }
                // Trier par classement_By_FilterKeys_M3 ascending
                .sortedBy { (product, _) -> product.classement_By_FilterKeys_M3 }
        }
    }

    // Scroll vers le produit qui porte la couleur expansée
    LaunchedEffect(expanded_M3CouleurProduitInfos) {
        expanded_M3CouleurProduitInfos ?: return@LaunchedEffect
        if (!isHostPhone) return@LaunchedEffect
        val targetKeyID = expanded_M3CouleurProduitInfos.parentBProduitInfosKeyID
        if (targetKeyID.isBlank()) return@LaunchedEffect

        val foundIndex =
            productWithColorsList.indexOfFirst { (product, _) -> product.keyID == targetKeyID }
        if (foundIndex >= 0) {
            delay(300)
            coroutineScope.launch { gridState.animateScrollToItem(foundIndex) }
        }
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
        gridState = gridState,
        tag = tag
    )

    // Opérations de vente — lues une fois, utilisées pour les semantics
    var lenceVentOperations by remember { mutableStateOf<List<M10OperationVentCouleur>>(emptyList()) }
    var all by remember { mutableStateOf<List<M10OperationVentCouleur>>(emptyList()) }
    var activeBonVentKey by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val targetComptKeyId =
                M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId
            activeBonVentKey = viewModel.appDatabase.dao_M9AppCompt().getAll()
                .find { it.keyID == targetComptKeyId }?.onVentM8BonVentKey ?: ""
            val allOps = viewModel.appDatabase.dao_M10OperationVentCouleur().getAll()
            lenceVentOperations = allOps.filter { it.parent_M8BonVent_KeyId == activeBonVentKey }
            all = allOps
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(value = lenceVentOperations, key = SemanticsPropertyKey("lenceVentOperations"))
            }
            .semantics(mergeDescendants = true) {
                set(value = activeBonVentKey, key = SemanticsPropertyKey("activeBonVentKey"))
            }
            .semantics(mergeDescendants = true) {
                set(value = all, key = SemanticsPropertyKey("all"))
            }
            .fillMaxWidth()
            .background(Color(0xFFFFF0F5)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
        userScrollEnabled = isScrollEnabled
    ) {
        productWithColorsList.forEach { (product, colors) ->
            val isExpanded = wifiState.expanded_M1Produit?.keyID == product.keyID
            val justMoved = product.keyID == justMovedProductKeyID

            item(
                key = "product_${product.keyID}",
                span = if (isExpanded) StaggeredGridItemSpan.FullLine
                else StaggeredGridItemSpan.SingleLane
            ) {
                LazyStigerList_Produits_FragID4(
                    uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
                    product = product,
                    colors = colors,
                    on_pour_send_data = on_pour_send_data,
                    onCategoryClick = { onProductCategoryClick(product) },
                    justMoved = justMoved
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
    on_pour_send_data: (String, String) -> Unit,
    onCategoryClick: (() -> Unit)? = null,
    justMoved: Boolean = false,
    uiState_NewProtoPatterns_viewModel: Pair<UiState_NewProtoPatterns, A_ViewModel_NewProtoPatterns>
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
            uiState_NewProtoPatterns_viewModel = uiState_NewProtoPatterns_viewModel,
            relative_M1produit = product,
            relative_ListM3Couleurs_override = colors,
            on_pour_send_data = on_pour_send_data,
            onCategoryClick = onCategoryClick,
            modifier = modifier
        )
    }
}
