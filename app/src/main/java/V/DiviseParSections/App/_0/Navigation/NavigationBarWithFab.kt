package V.DiviseParSections.App._0.Navigation

import EntreApps.Shared.Models.Components.AppType
import EntreApps.Shared.Models.M18CentralParametresOfAllApps
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App._0.Navigation.Buttons_Gps.PdfSaverUtility
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.FabButton_When_ItsEditeBaseDonne
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.FabDropdownMenu_BaseDonneEdite
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.Floating_Separated_FragMap_Button_1_SelectCategorieEtAddNewProduit
import V.DiviseParSections.App._0.Navigation.Main_DropDown.BaseDonneEdite.Floating_Separated_FragMap_Button_5
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.DropDownMenu.View.FabDropdownMenu_WhenItsAchatsFragment
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.FloatingItems.Views.FabButton_When_Its_Achats
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_Achats.FloatingItems.Views.FragAchats_FloatingOutlinedSearcher_4
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.FabDropdownMenu_WhenIts_FragFastVent
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.FloatingItems.Views.CheckList_ChoisiseurActiveFilter
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.FabButton_When_Its_EducationFragment
import V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.FabDropdownMenu_WhenIts_FragmentEducation
import V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.FabButton_When_Its_FacadeElectroBoutique
import V.DiviseParSections.App._0.Navigation.Main_DropDown.When_Its_FacadeElectroBoutique.FabDropdownMenu_WhenIts_FacadeBoutiqueElectro
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ContentAlpha
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File

private const val TAG = "NavigationBarWithFab"

@Composable
fun NavigationBarWithFab(
    viewModelInitApp: ViewModelInitApp,
    aCentralFacade: ACentralFacade = viewModelInitApp.aCentralFacade,
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repo8BonVent: Repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent,
    fragmentNavigationHandler: FragmentNavigationHandler = aCentralFacade.modulesCentral.fragmentNavigationHandler,
    items: List<Screen>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    isFabVisible: Boolean,
    onToggleFabVisibility: () -> Unit,
    onCatalogSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    onClickImageToShowControles: () -> Unit,
    showWarningState: Boolean = true
) {
    var showCatalogDialog by remember { mutableStateOf(false) }
    var showDialogTests by remember { mutableStateOf(false) }
    val _activeFragment by fragmentNavigationHandler.currentFragment.collectAsState()
    val activeFragment = if (M18CentralParametresOfAllApps.get_Default().its_AppType == AppType.GrossistRealSeller) {
        Screen.FragmentProduitFastSearchDialog
    } else {
        _activeFragment
    }
    var showFabDropdown by remember { mutableStateOf(false) }
    var showFabDropdownBaseDonne by remember { mutableStateOf(false) }
    var showFabDropdownAchats by remember { mutableStateOf(false) }
    var showFabDropdownFastVent by remember { mutableStateOf(false) }
    var showFabDropdownEducation by remember { mutableStateOf(false) }
    var showFabDropdown_MainPresenterFragment by remember { mutableStateOf(false) }

    val currentValues = focusedValuesGetter.active_Central_Values

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            // Calculate middle index
            val middleIndex = items.size / 2

            items.forEachIndexed { index, screen ->
                if (index == middleIndex) {
                    // Add empty space for FAB
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Box(modifier = Modifier.size(48.dp)) },
                        enabled = false
                    )
                }
                NavigationBarItem(
                    icon = {
                        if (screen.customIconRes != null) {
                            Image(
                                painter = painterResource(id = screen.customIconRes),
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp),
                                colorFilter = if (currentRoute == screen.route) {
                                    ColorFilter.tint(screen.color)
                                } else {
                                    ColorFilter.tint(LocalContentColor.current.copy(alpha = ContentAlpha.medium))
                                }
                            )
                        } else {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (currentRoute == screen.route) screen.color
                                else LocalContentColor.current.copy(alpha = ContentAlpha.medium)
                            )
                        }
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        try {
                            if (screen.route == Screen.DialogTests.route) {
                                showDialogTests = true
                            } else {
                                onNavigate(screen.route)
                            }
                        } catch (e: IllegalStateException) {
                            // Navigation graph not ready yet, ignore
                        }
                    }
                )
            }
        }

        val its_Targeted_Frag = activeFragment == Screen.A_Clients_LocationGps
        val its_EditDatabaseWithCreateNewArticles = activeFragment == Screen.EditDatabaseWithCreateNewArticles
        val its_Achats_Produits_Chez_Grossists = activeFragment == Screen.Achats_Produits_Chez_Grossists
        val its_FragmentProduitFastSearchDialog = activeFragment == Screen.FragmentProduitFastSearchDialog
        val its_EducationFragment = activeFragment == Screen.EducationFragment
        val its_Compact_Presentoire_App_Produits_FragID4 = activeFragment == Screen.Compact_Presentoire_App_Produits_FragID5

        when {
            its_EditDatabaseWithCreateNewArticles -> {
                FabButton_When_ItsEditeBaseDonne(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = {
                        showFabDropdownBaseDonne = true
                    }
                )
            }
            its_Achats_Produits_Chez_Grossists -> {
                FabButton_When_Its_Achats(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = {
                        showFabDropdownAchats = true
                    }
                )
            }
            its_Compact_Presentoire_App_Produits_FragID4 -> {
                FabButton_When_Its_FacadeElectroBoutique(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = {
                        showFabDropdown_MainPresenterFragment = true
                    }
                )
            }

            its_FragmentProduitFastSearchDialog -> {
                FabButton(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = { showFabDropdownFastVent = true }
                )
            }

            its_EducationFragment -> {
                FabButton_When_Its_EducationFragment(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = {
                        showFabDropdownEducation = true
                    }
                )
            }

            else -> {
                FabButton(
                    showWarningState = showWarningState,
                    isFabVisible = isFabVisible,
                    its_Targeted_Frag = its_Targeted_Frag,
                    onToggleFabVisibility = onToggleFabVisibility,
                    onShowDropdown = { showFabDropdown = true }
                )
            }
        }

        if (showCatalogDialog) {
            CatalogSelectionDialog(
                onDismiss = { showCatalogDialog = false },
                onCatalogSelected = { categoryId ->
                    onCatalogSelected(categoryId)
                    showCatalogDialog = false
                    onNavigate(Screen.Fragment_Compact_Presentoir_Echantilliants.route)
                },
                viewModelInitApp = viewModelInitApp
            )
        }

        if (showDialogTests) {
            TestScreens(
                onDismiss = { showDialogTests = false },
                fragmentNavigationHandler = fragmentNavigationHandler
            )
        }

        if (showFabDropdown && !its_EditDatabaseWithCreateNewArticles && !its_Achats_Produits_Chez_Grossists && !its_FragmentProduitFastSearchDialog && !its_EducationFragment) {
            FabDropdownMenu(
                showFabDropdown = showFabDropdown,
                onDismissDropdown = { showFabDropdown = false },
                repo8BonVent = repo8BonVent
            )
        }

        if (showFabDropdownBaseDonne && its_EditDatabaseWithCreateNewArticles) {
            FabDropdownMenu_BaseDonneEdite(
                onDismissDropdown = {
                    showFabDropdownBaseDonne = false
                }
            )
        }

        if (showFabDropdownAchats && its_Achats_Produits_Chez_Grossists) {
            FabDropdownMenu_WhenItsAchatsFragment(
                onDismissDropdown = {
                    showFabDropdownAchats = false
                }
            )
        }

        if (showFabDropdownEducation && its_EducationFragment) {
            FabDropdownMenu_WhenIts_FragmentEducation(
                onDismissDropdown = { showFabDropdownEducation = false }
            )
        }

        if (showFabDropdown_MainPresenterFragment && its_Compact_Presentoire_App_Produits_FragID4) {
            FabDropdownMenu_WhenIts_FacadeBoutiqueElectro(
                onDismissDropdown = { showFabDropdown_MainPresenterFragment = false } ,
                onClickImageToShowControles= onClickImageToShowControles
            )
        }

        val printHandler = aCentralFacade.modulesCentral.printReceiptHandler

        val activeVents = focusedValuesGetter
            .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
            .filter { vent ->
                vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                        vent.quantity > 0
            }

        val initiateBackgroundPdfCreation: () -> Unit = {
            Log.d(TAG, "═══════════════════════════════════════════════════════")
            Log.d(TAG, "🚀 initiateBackgroundPdfCreation: Starting")
            Log.d(TAG, "═══════════════════════════════════════════════════════")

            // Dismiss immediately
            showFabDropdownFastVent = false
            Log.d(TAG, "✅ Dropdown dismissed")

            val activeClient = focusedValuesGetter.activeOnVentM2ClientInfos
            val activeBonVent = focusedValuesGetter.activeOnVent_M8BonVent

            if (activeClient == null) {
                Log.e(TAG, "❌ No active client found")
                Toast.makeText(context, "Aucun client actif trouvé", Toast.LENGTH_SHORT).show()
            } else if (activeBonVent == null) {
                Log.e(TAG, "❌ No active bon de vente found")
                Toast.makeText(context, "Aucun bon de vente actif", Toast.LENGTH_SHORT).show()
            } else if (activeVents.isEmpty()) {
                Log.e(TAG, "❌ No active vents to process")
                Toast.makeText(context, "Aucun article à traiter", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "✅ Validation passed:")
                Log.i(TAG, "   Client: ${activeClient.nom}")
                Log.i(TAG, "   BonVent: ${activeBonVent.keyID}")
                Log.i(TAG, "   Items: ${activeVents.size}")

                // Launch background task
                GlobalScope.launch(Dispatchers.IO) {
                    Log.d(TAG, "🔄 Background task started on IO dispatcher")

                    try {
                        // Add bon to image list
                        val currentValues = focusedValuesGetter.active_Central_Values
                        val currentBonsWithImages = currentValues.bons_a_imprime_avec_image_produit.toMutableList()

                        if (!currentBonsWithImages.any { it.keyID == activeBonVent.keyID }) {
                            currentBonsWithImages.add(activeBonVent)
                            focusedValuesGetter.update_activeCentralValues(
                                currentValues.copy(bons_a_imprime_avec_image_produit = currentBonsWithImages)
                            )
                            Log.d(TAG, "📸 Added bon to image print list")
                        }

                        delay(300)

                        Log.d(TAG, "───────────────────────────────────────────────────────")
                        Log.d(TAG, "📄 Starting PDF generation (30s timeout)...")
                        Log.d(TAG, "───────────────────────────────────────────────────────")

                        // Generate PDF with timeout
                        val pdfFilePath = withTimeout(30000L) {
                            val handler = printHandler as? PrintReceiptHandler_Juil
                            if (handler == null) {
                                Log.e(TAG, "❌ PrintHandler is NULL!")
                                null
                            } else {
                                Log.d(TAG, "▶️ Calling printPdfOnly with shouldOpenFile=false...")
                                val result = handler.printPdfOnly(
                                    context = context,
                                    repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                                    repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                                    repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                                    scope = null,
                                    relative_ListM10OperationVentCouleur = activeVents,
                                    relative_bonVent = activeBonVent,
                                    client = activeClient,
                                    showCreditSection = false,
                                    versement = 0.0,
                                    shouldOpenFile = false
                                )

                                Log.d(TAG, "◀️ printPdfOnly completed: ${if (result.isSuccess) "✅ SUCCESS" else "❌ FAILED"}")
                                result.getOrNull()?.substringAfter("PDF saved: ")?.substringBefore("\n")
                            }
                        }

                        Log.d(TAG, "───────────────────────────────────────────────────────")
                        Log.d(TAG, "📦 PDF generation completed - Path: $pdfFilePath")
                        Log.d(TAG, "───────────────────────────────────────────────────────")

                        if (pdfFilePath != null) {
                            val tempPdfFile = File(pdfFilePath)

                            if (tempPdfFile.exists()) {
                                Log.i(TAG, "✅ Temp PDF exists - Size: ${tempPdfFile.length()} bytes")

                                val timestamp = java.time.LocalDateTime.now()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("MM_dd_HH:mm"))
                                val clientNamePart = activeClient.nom
                                    .replace(Regex("[^A-Za-z0-9_\\-]"), "_")
                                    .take(20)
                                val bonNumPart = activeBonVent.keyID.takeLast(6)
                                val cleanFileName = "${clientNamePart}_${bonNumPart}_${timestamp}.pdf"

                                Log.d(TAG, "───────────────────────────────────────────────────────")
                                Log.d(TAG, "💾 Saving via MediaStore...")
                                Log.d(TAG, "   Destination: Downloads/BonsWhatsApp/${PdfSaverUtility.getCurrentDateFolder()}/")
                                Log.d(TAG, "   File name: $cleanFileName")
                                Log.d(TAG, "───────────────────────────────────────────────────────")

                                // Save using PdfSaverUtility
                                val saveResult = PdfSaverUtility.savePdf(
                                    context = context,
                                    sourceFile = tempPdfFile,
                                    fileName = cleanFileName,
                                    subFolder = "BonsWhatsApp"
                                )

                                saveResult.onSuccess { savedPath ->
                                    Log.i(TAG, "═══════════════════════════════════════════════════════")
                                    Log.i(TAG, "✅✅✅ PDF SAVED SUCCESSFULLY! ✅✅✅")
                                    Log.i(TAG, "   Location: $savedPath")
                                    Log.i(TAG, "   Accessible via: Gestionnaire de fichiers → Téléchargements → BonsWhatsApp")
                                    Log.i(TAG, "═══════════════════════════════════════════════════════")

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "✅ PDF terminé!\n$cleanFileName\nTéléchargements/BonsWhatsApp",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }.onFailure { error ->
                                    Log.e(TAG, "❌ Save failed: ${error.message}", error)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "❌ Erreur: ${error.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                // Delete temp file
                                Log.d(TAG, "🗑️ Deleting temp file...")
                                if (tempPdfFile.delete()) {
                                    Log.d(TAG, "✅ Temp file deleted")
                                } else {
                                    Log.w(TAG, "⚠️ Failed to delete temp file")
                                }

                            } else {
                                Log.e(TAG, "❌ Temp PDF not found at: $pdfFilePath")
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "❌ Erreur: Fichier introuvable", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Log.e(TAG, "❌ PDF generation returned null")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "❌ Génération échouée", Toast.LENGTH_LONG).show()
                            }
                        }

                    } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                        Log.e(TAG, "⏱️ TIMEOUT after 30s", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "❌ Timeout (>30s)", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "💥 Exception", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "❌ Erreur: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                        e.printStackTrace()
                    } finally {
                        Log.d(TAG, "🧹 Cleanup...")
                        delay(500)

                        // Clean up bons list
                        val finalValues = focusedValuesGetter.active_Central_Values
                        activeBonVent.let { bon ->
                            val cleanedBons = finalValues.bons_a_imprime_avec_image_produit.filter { it.keyID != bon.keyID }
                            focusedValuesGetter.update_activeCentralValues(
                                finalValues.copy(bons_a_imprime_avec_image_produit = cleanedBons)
                            )
                            Log.d(TAG, "✅ Removed bon from image list")
                        }

                        Log.d(TAG, "✅ Cleanup completed")
                        Log.d(TAG, "═══════════════════════════════════════════════════════")
                    }
                }

                Log.d(TAG, "🚀 Background task launched")
            }
        }
        if (showFabDropdownFastVent && its_FragmentProduitFastSearchDialog) {
            FabDropdownMenu_WhenIts_FragFastVent(
                onDismissDropdown = { showFabDropdownFastVent = false },
                onClick_to_initiateBackgroundPdfCreation = initiateBackgroundPdfCreation ,
                onClickImageToShowControles= onClickImageToShowControles
            )
        }

        if (focusedValuesGetter.active_Central_Values.affiche_Floating_Button_SelecteCategorieEtAddNewProduit) {
            Floating_Separated_FragMap_Button_1_SelectCategorieEtAddNewProduit(
                aCentralFacade = aCentralFacade,
                repositorysMainGetter = aCentralFacade.repositorysMainGetter,
                focusedValuesGetter = focusedValuesGetter
            )
        }


        if (focusedValuesGetter.active_Central_Values.affiche_Floating_Button_FABsModeEditesProduit) {
            Floating_Separated_FragMap_Button_5(
                aCentralFacade = aCentralFacade,
                repositorysMainGetter = aCentralFacade.repositorysMainGetter,
                focusedValuesGetter = focusedValuesGetter
            )
        }

        if (focusedValuesGetter.active_Central_Values.afficheFloatingOutlinedSearcher_of_Achat) {
            FragAchats_FloatingOutlinedSearcher_4(
                aCentralFacade = aCentralFacade,
                focusedValuesGetter = focusedValuesGetter
            )
        }
        if (focusedValuesGetter.active_Central_Values.affiche_CheckList_ChoisiseurActiveFilter) {
            CheckList_ChoisiseurActiveFilter(
                aCentralFacade = aCentralFacade,
                focusedValuesGetter = focusedValuesGetter
            )
        }
    }
}

data class Item_States(
    val function_noms_separatedStrings: String = ",",
    val avec_Premier_Click_Jane: Boolean = true,
    val time_pressing_millis: Int = 1000,
    val icon_imageVector: ImageVector = Icons.Default.Close,
) {
    companion object {
        fun get_Arab_Nom(function_noms_separatedStrings: String): String {
            return extract_Noms(function_noms_separatedStrings).getOrNull(1) ?: ""
        }

        fun get_English_Nom(function_noms_separatedStrings: String): String {
            return extract_Noms(function_noms_separatedStrings).getOrNull(0) ?: ""
        }

        fun extract_Noms(function_noms_separatedStrings: String): List<String> {
            return function_noms_separatedStrings.split(",").map { it.trim() }
        }

        fun get_Default(): Item_States {
            return Item_States()
        }
    }
}
