package Application4.App.Fragment.ID1.Fragment

import A_Main.Shared.Views.Dialogs.B.Dialoge.PressistatntMainActivityButtons_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.Ui.PubAbdelwahabElectroGroStore
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.FabDropdownMenu_WhenIts_FacadeBoutiqueElectro_App4
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import Application4.App.Modules.Wi.Module.Wifi_Messages_Types_NewProto
import EntreApps.Shared.Compose_Injectable_Sepecialise.Kotlin.ID1.EditeBaseDonne.Package.M16Categorie.Dialog.CategorySelectionDialog
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Modules.Base.AppDatabase
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.a.ID1_Fe.Feature.Options.a.Main.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.clientjetpack.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun A_Compact_Presentoire_App_Produits_App4(
    modifier: Modifier = Modifier,
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
    appDatabase: AppDatabase,
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit,
) {
    val context = LocalContext.current
    val focusedValuesGetter: FocusedValuesGetter = koinInject()
    val active_Central_Values = focusedValuesGetter.active_Central_Values
    val activeAfficheButtons =
        active_Central_Values.affiche_buttons_lien_unite_couleur_au_couleut_parent
    val repo13TarificationInfos =
        koinInject<V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos>()

    val viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns =
        viewModel(
            factory = viewModelFactory {
                initializer {
                    A_ViewModel_NewProtoPatterns(
                        wifiTransferDatas_ControllerApp = wifiTransferDatas_ControllerApp,
                        context = context,
                        appDatabase = appDatabase,
                        fragmentNavigationHandler = fragmentNavigationHandler,
                        repo13TarificationInfos = repo13TarificationInfos,
                    )
                }
            }
        )


    LaunchedEffect(Unit) {
        viewModelNewProtoPatterns.retryLoadingData()
    }

    val showFabDropdown_Compact_Presentoire_App_Produits_FragID4 by fragmentNavigationHandler.showFabDropdown_Compact_Presentoire.collectAsState()
    var affiche_pub_abdelwahab_electro_gro_store by remember { mutableStateOf(false) }

    val active_Datas = viewModelNewProtoPatterns.active_Datas

    val uiState by viewModelNewProtoPatterns.uiState.collectAsState()
    val isInitDone = uiState.initDatasProgressEtate >= 1f

    val allCategories: List<M16CategorieProduit>? by remember {
        derivedStateOf {
            active_Datas.list_M16CategorieProduit
                ?.takeIf { it.isNotEmpty() }
        }
    }

    val allProducts: List<M01Produit>? by remember {
        derivedStateOf { active_Datas.list_M1Produit }
    }

    var selectedProductForCategoryChange by remember { mutableStateOf<M01Produit?>(null) }
    var justMovedProductKeyID by remember { mutableStateOf<String?>(null) }
    var hasRetriedLoading by remember { mutableStateOf(false) }

    // Retry loading data if initialization is done but no items are displayed
    LaunchedEffect(isInitDone, allProducts) {
        if (isInitDone && !hasRetriedLoading && allProducts.isNullOrEmpty()) {
            delay(6000)
            if (allProducts.isNullOrEmpty()) {
                hasRetriedLoading = true
                viewModelNewProtoPatterns.retryLoadingData()
            }
        }
    }

    LaunchedEffect(justMovedProductKeyID) {
        justMovedProductKeyID?.let {
            delay(1500)
            justMovedProductKeyID = null
        }
    }


    var couleursAuDepot_by_dao by remember { mutableStateOf<List<M3CouleurProduitInfos>?>(null) }
    var currentBonVent_m9 by remember { mutableStateOf<M8BonVent?>(null) }
    var vents_de_count by remember { mutableStateOf<List<M10OperationVentCouleur>?>(null) }

    var tariffs by remember { mutableStateOf<List<M13TarificationInfos>?>(null) }

    LaunchedEffect(Unit) {           //<--
        val (m9, bonVent) = withContext(Dispatchers.IO) {
            val m9Result = viewModelNewProtoPatterns.appDatabase
                .dao_M9AppCompt()
                .getAll()
                .find { it.keyID == M00CentralParametresOfAllApps.get_Default().au_Lence_Set_Compt_Ac_KeyId }

            val bonVentResult = viewModelNewProtoPatterns.appDatabase.dao_M8BonVent().getAll()
                .find { it.keyID == m9Result?.onVentM8BonVentKey }

            m9Result to bonVentResult
        }

        currentBonVent_m9 = bonVent
        val all = withContext(Dispatchers.IO) {
            appDatabase.dao_M03CouleurProduitInfos().getAll()
        }
        val tariffs_dao = withContext(Dispatchers.IO) {
            appDatabase.dao_M13TarificationInfos().getAll()
        }
        tariffs = tariffs_dao
        val depotList = all.filter { it.count_Don_Depot > 0 }
        couleursAuDepot_by_dao = depotList

        if (currentBonVent_m9 != null) {


            val currentTariffs = tariffs.orEmpty()
            val newOperations = depotList.map { couleur ->
                // "super gros" (its_gro_app = true), pas un prix d'achat. Utiliser
                // Tariff_Achat_Depuit_Grossisst (its_gro_app = false), le vrai type
                // "prix d'achat" pour l'app non-grossiste — sinon achatTariff ne
                // matchait quasiment jamais et retombait silencieusement à 0.0.
                val achatTariff = currentTariffs
                    .sortedBy { it.creationTimestamps }
                    .lastOrNull {
                        it.parent_M1Produit_KeyId == couleur.parentBProduitInfosKeyID &&
                                it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_Achat_Depuit_Grossisst
                    }
                val newTariff = M13TarificationInfos.get_default().copy(
                    typeChoisi = M13TarificationInfos.TypeChoisi.Prix_Progressive_Editable,
                    prixCurrency = achatTariff?.prixCurrency ?: 0.0,
                    parent_M1Produit_KeyId = couleur.parentBProduitInfosKeyID,
                    parent_M1Produit_DebugInfos = "par.produit ${couleur.parentId1ProduitInfosDebugName}",
                )
                M10OperationVentCouleur.get_Default().copy(
                    creationTimestamps = System.currentTimeMillis(),
                    quantity = couleur.count_Don_Depot,
                    prix_de_Vent_entre_directement_NewProto = newTariff.prixCurrency,
                    parentM13TarificationKeyID = "Prix_Progressive_Editable Non Saved",
                    parentM13TarificationDebugInfos = newTariff.getDebugInfos(),
                    parent_M1Produit_KeyId = couleur.parentBProduitInfosKeyID,
                    parent_M1Produit_DebugInfos = "par.produit ${couleur.parentId1ProduitInfosDebugName}",
                    parent_M3CouleurProduit_KeyID = couleur.keyID,
                    parent_M3CouleurProduit_DebugInfos = couleur.get_DebugsInfos(),
                    parent_M8BonVent_KeyId = currentBonVent_m9!!.keyID,
                    parent_M8BonVent_DebugInfos = currentBonVent_m9!!.get_DebugInfos(),
                    parent_M2Client_KeyID = currentBonVent_m9!!.parent_M2Client_KeyID,
                    typeTarificationEnumT2 = newTariff.typeChoisi,
                )
            }

            vents_de_count = newOperations
        }
    }



    if (!isInitDone) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { uiState.initDatasProgressEtate },
                modifier = Modifier.size(48.dp),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Box(
            modifier = Modifier.semantics(mergeDescendants = true) {
                set(value = currentBonVent_m9, key = SemanticsPropertyKey("currentBonVent_m9"))
                set(value = tariffs, key = SemanticsPropertyKey("tariffs"))
                set(value = tariffs?.lastOrNull() {
                    it.parent_M1Produit_KeyId == "-Od3KgneO-CS5cATvGxI"
                            && it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros &&
                            it.prixCurrency != 0.0
                }, key = SemanticsPropertyKey("tariffs.find "))

                set(
                    value = couleursAuDepot_by_dao,
                    key = SemanticsPropertyKey("couleursAuDepot_by_dao")
                )
                set(value = vents_de_count, key = SemanticsPropertyKey("vents_de_count"))
            }
        ) {
            if (affiche_pub_abdelwahab_electro_gro_store) {
                val allImageIds = listOf(
                    R.drawable.imgs__1_,
                    R.drawable.imgs__2_,
                    R.drawable.imgs__3_,
                    R.drawable.imgs__4_,
                    R.drawable.imgs__5_,
                )
                val landscapeImages = remember(allImageIds) {
                    allImageIds.filter { resId ->
                        val opts = android.graphics.BitmapFactory.Options().apply {
                            inJustDecodeBounds =
                                true  // lecture seule des dimensions, sans décode complet
                        }
                        BitmapFactory.decodeResource(context.resources, resId, opts)
                        opts.outWidth > opts.outHeight   // true = paysage
                    }
                }
                PubAbdelwahabElectroGroStore(
                    affiche = true,
                    images = landscapeImages.ifEmpty { allImageIds },  // fallback si tout est portrait
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Main_LazyColumnList_App4(
                    active_Central_Values = active_Central_Values,
                    modifier = modifier,
                    uiState_NewProtoPatterns_viewModel = Pair(uiState, viewModelNewProtoPatterns),
                    onProductCategoryClick = { product ->
                        selectedProductForCategoryChange = product
                    },
                    justMovedProductKeyID = justMovedProductKeyID,
                    on_update_M13TarificationInfos_par_ecriture = on_update_M13TarificationInfos_par_ecriture,
                    ventCouleurs = active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state,
                    affiche_buttons_lien_unite_couleur_au_couleut_parent = activeAfficheButtons
                )
            }

            PressistatntMainActivityButtons_App4(viewModelNewProtoPatterns)
            val haptic = LocalHapticFeedback.current

            FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button(
                onClick_Lence_Ventes_Depot = {
                    // Relatif au bouton "Vents Dépôt (Super Gros)" de
                    // bouton-ci ne doit QUE créer les ventes de dépôt à partir de
                    // vents_de_count — celui-ci est déjà construit plus haut avec le
                    // vrai prix d'achat (achatTariff / Tariff_Achat_Depuit_Grossisst),
                    // pas le prix de vente, donc pas d'action supplémentaire ici.
                    viewModelNewProtoPatterns.addNew_listM10OperationVentCouleur(vents_de_count)
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onClick_Lence_Ventes_Delicates = {
                    couleursAuDepot_by_dao
                        ?.filter { it.its_delicate_a_regle_apres }?.let {
                            currentBonVent_m9?.let { currentBonVent ->
                                viewModelNewProtoPatterns.lanceVentesPourCouleursDelicates(
                                    it,
                                    currentBonVent
                                )
                            }
                        }
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onClick_Activer_Delicates_Pour_Ventes_Actives = {
                    viewModelNewProtoPatterns.setDelicatePourCouleursDesVentesActives(true)
                },
                onClick_Desactiver_Delicates_Pour_Ventes_Actives = {
                    viewModelNewProtoPatterns.setDelicatePourTout()
                },

                appDatabase = viewModelNewProtoPatterns.appDatabase,
                onClick_Affiche_Pub = {
                    val bool = !affiche_pub_abdelwahab_electro_gro_store
                    affiche_pub_abdelwahab_electro_gro_store =
                        bool
                    wifiTransferDatas_ControllerApp.sendOrderToClientDisplayerT(
                        Wifi_Messages_Types_NewProto.Update_affiche_pub_abdelwahab_electro_gro_store,
                        bool.toString()
                    )
                },
                affiche_buttons_lien_unite_couleur_au_couleut_parent = activeAfficheButtons,
                on_pour_update_affiche_buttons_lien_unite_couleur_au_couleut_parent = { newVal ->
                    focusedValuesGetter.update_activeCentralValues(
                        active_Central_Values.copy(
                            affiche_buttons_lien_unite_couleur_au_couleut_parent = newVal
                        )
                    )
                },
                on_pour_update_compact_buttons = { newVal ->
                    focusedValuesGetter.update_activeCentralValues(
                        active_Central_Values.copy(
                            compact_button_au_edite_base_donne_options = newVal
                        )
                    )
                },
                compact_buttons = active_Central_Values.compact_button_au_edite_base_donne_options,
            )
        }
    }

    selectedProductForCategoryChange?.let { product ->
        CategorySelectionDialog(
            product = product,
            allCategories = allCategories,
            allProducts = allProducts,
            isFastMoveMode = true,
            onCategorySelected = { newCategoryId ->
                val updatedProduct = newCategoryId?.let {
                    product.copy(
                        idParentCategorie = it,
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                } ?: product
                viewModelNewProtoPatterns.update_m1Produit(updatedProduct)
                justMovedProductKeyID = product.keyID
                selectedProductForCategoryChange = null
            },
            onDismiss = {
                selectedProductForCategoryChange = null
            },
            onCreateNewCategory = { categoryName ->
                val newId = System.currentTimeMillis()
                val data = M16CategorieProduit(
                    id = newId,
                    nom = categoryName,
                    position = 0,
                    catalogueParentId = 4
                )
                viewModelNewProtoPatterns.insert_M16CategorieProduit(data)
                val updatedProduct = product.copy(
                    idParentCategorie = newId,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                viewModelNewProtoPatterns.update_m1Produit(updatedProduct)
                justMovedProductKeyID = product.keyID
                selectedProductForCategoryChange = null
            },
            onUpdateCategoryName = { categoryId, newName ->
                allCategories?.find { it.id == categoryId }?.let { category ->
                    viewModelNewProtoPatterns.update_m16CategorieProduit(category.copy(nom = newName))
                }
            }
        )
    }
    if (showFabDropdown_Compact_Presentoire_App_Produits_FragID4) {
        FabDropdownMenu_WhenIts_FacadeBoutiqueElectro_App4(
            viewModelNewProtoPatterns = viewModelNewProtoPatterns,
            affiche_ProduitDataBaseEdites_ComposableViews = activeAfficheButtons,
            on_pour_update_affiche_ProduitDataBaseEdites_ComposableViews = { newVal ->
                focusedValuesGetter.update_activeCentralValues(
                    active_Central_Values.copy(
                        affiche_buttons_lien_unite_couleur_au_couleut_parent = newVal
                    )
                )
            },
            onDismissDropdown = {
                fragmentNavigationHandler.setShowFabDropdown_Compact_Presentoire(false)
            },
        )
    }
}
