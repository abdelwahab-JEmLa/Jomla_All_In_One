package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabButton_When_Its_FastVent.DropDownMenu.View.DropDownItems.View.B6.View

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class CompanyHeader(val displayName: String) {
    JOMLA("Jomla.com"),
    BELFORT("Belfort Gros Confisserie")
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DropDownItem_ThermiquePrint(
    nomFun: String = "Imprimer Bluetooth",
    onDismissDropdown: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    context: Context = LocalContext.current
) {
    var selectedHeader by remember {
        mutableStateOf(
            if (focusedValuesGetter.currentApp_ItsWorkChezGrossisst) CompanyHeader.BELFORT else
                CompanyHeader.JOMLA
        )
    }
    val scope = rememberCoroutineScope()
    val printHandler = aCentralFacade.modulesCentral.printReceiptHandler

    val activeVents = focusedValuesGetter
        .onVent_ListM10VentCouleur_FiltrePar_onVent_M8BonVent
        .filter { vent ->
            vent.etateDelivery != M10OperationVentCouleur.EtateDelivery.NonTrouve &&
                    vent.quantity > 0
        }

    // State for showing the warning dialog
    var showWarningDialog by remember { mutableStateOf(false) }
    var productsWithoutPrice by remember { mutableStateOf<List<String>>(emptyList()) }
    var printWithoutProductsState by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = nomFun,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    FloatingActionButton(
                        onClick = {
                            selectedHeader = if (selectedHeader == CompanyHeader.JOMLA) {
                                CompanyHeader.BELFORT
                            } else {
                                CompanyHeader.JOMLA
                            }
                        },
                        modifier = Modifier.size(32.dp),
                        containerColor = when (selectedHeader) {
                            CompanyHeader.JOMLA -> Color(0xFF4CAF50)
                            CompanyHeader.BELFORT -> Color(0xFFFF9800)
                        },
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = "Changer l'en-tête: ${selectedHeader.displayName}",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    FloatingActionButton(
                        onClick = {
                            printWithoutProductsState = true
                            if (activeVents.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Aucun article à imprimer",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@FloatingActionButton
                            }

                            val tarificationRepo = aCentralFacade.repositorysMainGetter.repo13TarificationInfos
                            val produitRepo = aCentralFacade.repositorysMainGetter.repo1ProduitInfos

                            val invalidPriceProducts = mutableListOf<String>()

                            activeVents.forEach { vent ->
                                val tariff = tarificationRepo.datasValue.find {
                                    it.keyID == vent.parentM13TarificationKeyID
                                }
                                val product = produitRepo.datasValue.find {
                                    it.keyID == vent.parent_M1Produit_KeyId
                                }

                                // Check if price is 0.0 or null
                                if (tariff?.prixCurrency == null || tariff.prixCurrency == 0.0) {
                                    invalidPriceProducts.add(product?.nom ?: "Produit inconnu")
                                }
                            }

                            // Show warning dialog if products without valid prices exist
                            if (invalidPriceProducts.isNotEmpty()) {
                                productsWithoutPrice = invalidPriceProducts
                                showWarningDialog = true
                                return@FloatingActionButton
                            }

                            // Proceed with printing if all prices are valid
                            proceedWithPrinting(
                                scope = scope,
                                aCentralFacade = aCentralFacade,
                                focusedValuesGetter = focusedValuesGetter,
                                context = context,
                                activeVents = activeVents,
                                printHandler = printHandler,
                                selectedHeader = selectedHeader,
                                onDismissDropdown = onDismissDropdown,
                                printWithoutProducts = true
                            )
                        },
                        modifier = Modifier.size(32.dp),
                        containerColor = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Imprimer sans produits",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }
                }
            },
            onClick = {
                printWithoutProductsState = false
                if (activeVents.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Aucun article à imprimer",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@DropdownMenuItem
                }

                val tarificationRepo = aCentralFacade.repositorysMainGetter.repo13TarificationInfos
                val produitRepo = aCentralFacade.repositorysMainGetter.repo1ProduitInfos

                val invalidPriceProducts = mutableListOf<String>()

                activeVents.forEach { vent ->
                    val tariff = tarificationRepo.datasValue.find {
                        it.keyID == vent.parentM13TarificationKeyID
                    }
                    val product = produitRepo.datasValue.find {
                        it.keyID == vent.parent_M1Produit_KeyId
                    }

                    // Check if price is 0.0 or null
                    if (tariff?.prixCurrency == null || tariff.prixCurrency == 0.0) {
                        invalidPriceProducts.add(product?.nom ?: "Produit inconnu")
                    }
                }

                // Show warning dialog if products without valid prices exist
                if (invalidPriceProducts.isNotEmpty()) {
                    productsWithoutPrice = invalidPriceProducts
                    showWarningDialog = true
                    return@DropdownMenuItem
                }

                // Proceed with printing if all prices are valid
                proceedWithPrinting(
                    scope = scope,
                    aCentralFacade = aCentralFacade,
                    focusedValuesGetter = focusedValuesGetter,
                    context = context,
                    activeVents = activeVents,
                    printHandler = printHandler,
                    selectedHeader = selectedHeader,
                    onDismissDropdown = onDismissDropdown,
                    printWithoutProducts = false
                )
            }
        )
    }

    // Full-screen Warning Dialog with PressistatntMainActivityButtons overlay
    if (showWarningDialog) {
        Dialog(
            onDismissRequest = { /* Don't allow dismiss without validation */ },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 2.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Main content with scrolling
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Warning Icon
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Title
                        Text(
                            text = "⚠️ تحذير: منتجات بدون أسعار",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Main warning message
                        Text(
                            text = "خطر خسارة المال!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Products count
                        Text(
                            text = "${productsWithoutPrice.size} منتج بدون سعر محدد:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Products list with buttons
                        productsWithoutPrice.forEach { productName ->
                            Card(
                                modifier = Modifier
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "• $productName",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Button to show tariff dialog
                                    TextButton(
                                        onClick = {
                                            val produitRepo =
                                                aCentralFacade.repositorysMainGetter.repo1ProduitInfos
                                            val product =
                                                produitRepo.datasValue.find { it.nom == productName }

                                            product?.let { prod ->
                                                val get =
                                                    aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter

                                                aCentralFacade.repositorysMainSetter.saveTariff_Et_RelateIt_Au_Vents_Correspond(
                                                    m13TarificationInfos_Pour_Produit = get.focused_M13TarificationInfos_Pour_Produit,
                                                    m10OperationVentCouleurs = get.focused_ListM10OpeVentCouleur_Par_PD_M1Produit,
                                                    aCentralFacade = aCentralFacade
                                                )

                                                aCentralFacade.focusedActiveValuesFacade.focusedValuesSetter.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
                                                    prod
                                                )
                                            }

                                        }
                                    ) {
                                        Text(
                                            text = "تعريف السعر",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bottom message
                        Text(
                            text = "يرجى تحديد الأسعار قبل الطباعة لتجنب الخسائر المالية",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Action buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Verify and Close button
                            TextButton(
                                onClick = {
                                    val tarificationRepo =
                                        aCentralFacade.repositorysMainGetter.repo13TarificationInfos
                                    val produitRepo =
                                        aCentralFacade.repositorysMainGetter.repo1ProduitInfos

                                    val stillInvalidProducts = mutableListOf<String>()

                                    activeVents.forEach { vent ->
                                        val tariff = tarificationRepo.datasValue.find {
                                            it.keyID == vent.parentM13TarificationKeyID
                                        }
                                        val product = produitRepo.datasValue.find {
                                            it.keyID == vent.parent_M1Produit_KeyId
                                        }

                                        if (tariff?.prixCurrency == null || tariff.prixCurrency == 0.0) {
                                            stillInvalidProducts.add(
                                                product?.nom ?: "Produit inconnu"
                                            )
                                        }
                                    }

                                    if (stillInvalidProducts.isEmpty()) {
                                        showWarningDialog = false
                                        Toast.makeText(
                                            context,
                                            "جميع الأسعار محددة الآن ✓",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        productsWithoutPrice = stillInvalidProducts
                                        Toast.makeText(
                                            context,
                                            "لا يزال ${stillInvalidProducts.size} منتج بدون سعر",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "تحقق وأغلق",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            // Print anyway button
                            TextButton(
                                onClick = {
                                    showWarningDialog = false
                                    proceedWithPrinting(
                                        scope = scope,
                                        aCentralFacade = aCentralFacade,
                                        focusedValuesGetter = focusedValuesGetter,
                                        context = context,
                                        activeVents = activeVents,
                                        printHandler = printHandler,
                                        selectedHeader = selectedHeader,
                                        onDismissDropdown = onDismissDropdown,
                                        printWithoutProducts = printWithoutProductsState
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "اطبع على أي حال",
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    PressistatntMainActivityButtons_Sec8FWinID1()

                    // Close button at bottom right
                    FloatingActionButton(
                        onClick = {
                            showWarningDialog = false
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .zIndex(100f),
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق"
                        )
                    }
                }
            }
        }
    }
}
// In the proceedWithPrinting function, replace the update_M8BonVent call:

private fun proceedWithPrinting(
    scope: CoroutineScope,
    aCentralFacade: ACentralFacade,
    focusedValuesGetter: FocusedValuesGetter,
    context: Context,
    activeVents: List<M10OperationVentCouleur>,
    printHandler: PrintReceiptHandler_Juil,
    selectedHeader: CompanyHeader,
    onDismissDropdown: () -> Unit,
    repositorysMainSetter: RepositorysMainSetter=aCentralFacade.repositorysMainSetter,
    printWithoutProducts: Boolean = false
) {
    scope.launch {
        try {
            // Calculate total value of vents
            val totalValue = activeVents.sumOf { vent ->
                val tariff = aCentralFacade.repositorysMainGetter
                    .find_M13Tarification_By_KeyID(vent.parentM13TarificationKeyID)

                val prixCurrency = tariff?.prixCurrency ?: 0.0
                vent.quantity * prixCurrency
            }

            // ✅ Fixed: Update M8BonVent with boolean flag instead of integer counter
            aCentralFacade.repositorysMainSetter.update_M8BonVent(
                focusedValuesGetter.activeOnVent_M8BonVent?.copy(
                    affiche_le_verssement_au_prochen_print = false,
                    a_etai_imprime_au_moi_ne_foit = true,  // ✅ Set to true when printed
                    sum_De_Totale_Vents = totalValue
                )
            )

            delay(300)

            printHandler.printBluetoothOnly(
                context = context,
                repo13TarificationInfos = aCentralFacade.repositorysMainGetter.repo13TarificationInfos,
                repoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
                repo3CouleurProduitInfos = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos,
                client = focusedValuesGetter.activeOnVentM2ClientInfos,
                scope = scope,
                relative_ListM10OperationVentCouleur = activeVents,
                bonVent = focusedValuesGetter.activeOnVent_M8BonVent,
                companyHeader = selectedHeader.displayName,
                printWithoutProducts = printWithoutProducts
            )

        //   repositorysMainSetter.refresh_Datas_M8BonVent()

            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "Impression Bluetooth lancée",
                    Toast.LENGTH_SHORT
                ).show()
            }

            onDismissDropdown()
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    context,
                    "Erreur Bluetooth: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            e.printStackTrace()
        }
    }
}
