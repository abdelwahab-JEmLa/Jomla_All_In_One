package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.A2_Passive.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.ViewModel.Repository.A2_Passive.CategoriesTabelle
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.Component.LabelEtShowButtonsButtons
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.AvJuin3.Proto.E_JetPackAncienProduitDabase
import Z_CodePartageEntreApps.Modules.CameraHandler.CameraFABProtoJuin3
import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class AfficheElements { APP_BAR, DOCUMENTATION_TEXT }

@Composable
fun OptionsFragmentButtons(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    viewModelScope: CoroutineScope,
    onToggleMasque: (Set<AfficheElements>) -> Unit = {},
    selectedProducts: Set<ArticlesBasesStatsTable> = emptySet(),
    onShowBulkMoveDialog: () -> Unit = {},
    selectedCategories: Set<Long> = emptySet(),
    onCategoriesUpdated: (List<CategoriesTabelle>) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var showButtons by remember { mutableStateOf(false) }
    var showLabels by remember { mutableStateOf(true) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf((screenWidth.value - 180f)) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value) }
    var maskedElements by remember { mutableStateOf(setOf<AfficheElements>()) }
    var showDialog by remember { mutableStateOf(false) }
    var showCatalogueDialog by remember { mutableStateOf(false) }

    val catalogues = remember { B4CatalogueCategoriesRepository() }

    val onToggle = {
        maskedElements = if (maskedElements.contains(AfficheElements.APP_BAR)) {
            maskedElements - AfficheElements.APP_BAR
        } else {
            maskedElements + AfficheElements.APP_BAR
        }
        onToggleMasque(maskedElements)
    }

    // Update categories dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Update Datas? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModelScope.launch {
                        E_JetPackAncienProduitDabase.getFirebaseData { ancDatas ->
                            val newPrdList =
                                viewModel.uiState.value.a_ProduitInfosList.toMutableList()

                            // Create a map for faster lookup
                            val ancDataMap = ancDatas.associateBy { it.idArticle.toLong() }

                            newPrdList.forEach { currentProduct ->
                                val ancData = ancDataMap[currentProduct.id]

                                if (ancData != null) {
                                    // Update product details
                                    currentProduct.prixAchat = ancData.monPrixAchat
                                    currentProduct.nomArab = ancData.nomArab
                                    currentProduct.autreNomDarticle = ancData.autreNomDarticle

                                    // Update quantity information
                                    currentProduct.nombreUniteInt =
                                        if (ancData.nmbrUnite == 0) 1 else ancData.nmbrUnite
                                    currentProduct.nombreProduitDonSonCarton =
                                        if (ancData.nmbrCaron == 0) 1 else ancData.nmbrCaron

                                    // Update display and state information
                                    currentProduct.affichageUniteState = ancData.affichageUniteState
                                    currentProduct.commmentSeVent = ancData.commmentSeVent
                                    currentProduct.afficheBoitSiUniter = ancData.afficheBoitSiUniter
                                    currentProduct.cartonState = ancData.cartonState
                                }
                            }

                            // Apply the updates to the view model
                            viewModel.addOrUpdateProduits(newPrdList)

                            // Optional: Log the synchronization results
                            val updatedCount = newPrdList.count { product ->
                                ancDataMap.containsKey(product.id)
                            }
                            val totalCount = newPrdList.size
                            val notFoundCount = totalCount - updatedCount
                            Log.d(
                                "DataSync",
                                "Synchronization completed: $updatedCount updated, $notFoundCount not found in legacy data"
                            )
                        }
                        showDialog = false
                    }
                }) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }

    // Catalogue selection dialog
    if (showCatalogueDialog) {
        AlertDialog(
            onDismissRequest = { showCatalogueDialog = false },
            title = {
                Text(
                    "Move Categories to Catalogue",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "Select a catalogue to move ${selectedCategories.size} selected categories:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn {
                        items(catalogues) { catalogue ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                onClick = {
                                    viewModel.moveCategoriesAuCatalogue(catalogue.id)

                                    onCategoriesUpdated(
                                        viewModel.a_CentralDatasHandlerProtoJuin9.b3CategoriesCompoRepository
                                            .datasValue
                                    )

                                    showCatalogueDialog = false

                                    Log.d(
                                        "CatalogueMove",
                                        "Moved ${selectedCategories.size} categories to catalogue: ${catalogue.nom}"
                                    )
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = catalogue.nom,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                        }

                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCatalogueDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (showButtons) {

                    ButtonId8(showLabels,viewModel)

                    ButtonId7(
                        viewModel = viewModel,
                        showLabels = showLabels,
                    )
                    CameraFABProtoJuin3(activeCatalogue = uiState.activeCatalogue)

                    ButtonId6(
                        showLabels = showLabels,
                    )

                    But1(showDialog = showDialog, showLabels = showLabels) { showDialog = true }


                    But2(
                        showLabels = showLabels,
                        selectedCount = selectedProducts.size,
                        onBulkMove = onShowBulkMoveDialog
                    )

                    ButtonId3(
                        viewModel = viewModel,
                        showLabels = showLabels,
                        selectedCount = selectedCategories.size,
                        onCatalogueMove = { showCatalogueDialog = true }
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showLabels) Text("AfficheElements")
                        FloatingActionButton(
                            onClick = { viewModelScope.launch { onToggle() } },
                            modifier = Modifier.size(40.dp),
                            containerColor = Color.Red
                        ) {
                            Icon(
                                if (maskedElements.contains(AfficheElements.APP_BAR)) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                "Toggle Mask Elements",
                                tint = Color.Black
                            )
                        }
                    }
                    ButtonId3(
                        viewModel = viewModel,
                        showLabels = showLabels,
                        selectedCount = selectedCategories.size,
                        onCatalogueMove = { }
                    )
                    ButtonId4(
                        viewModel = viewModel,
                        showLabels = showLabels,
                    )
                    ButtonId5(
                        viewModel = viewModel,
                        showLabels = showLabels,
                    )
                }

                LabelEtShowButtonsButtons(
                    showLabels = showLabels,
                    showButtons = showButtons,
                    onShowLabelsToggle = { showLabels = !showLabels },
                    onShowButtonsToggle = { showButtons = !showButtons },
                    modifier = Modifier
                )
            }
        }
    }
}
