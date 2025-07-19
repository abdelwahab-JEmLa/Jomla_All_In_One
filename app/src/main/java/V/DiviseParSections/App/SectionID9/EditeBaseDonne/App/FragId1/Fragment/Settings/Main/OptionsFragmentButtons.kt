package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Settings.Main.Component.LabelEtShowButtonsButtons
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
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

data class Label_Datas(
    val showLabels: Boolean = true,
    val active_Str: String = "",
    val desactive_Str: String = "",
    val description_Functionement: String = "",
) { companion object { fun get_Default(): Label_Datas { return Label_Datas() } } }

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

    var mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed by remember { mutableStateOf(false) }

    var button9AlwaysVisible by remember { mutableStateOf(false) }
    var button8AlwaysVisible by remember { mutableStateOf(false) }

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

                            // Create add_New map for faster lookup
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
                        "Select add_New catalogue to move ${selectedCategories.size} selected categories:",
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
                                        viewModel.a_CentralDatasHandlerProtoJuin9.repoM16CategorieProduit
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
                if (mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed) {
                    Button_10(
                        label_Datas = Label_Datas.get_Default()
                            .copy(
                                showLabels, "click active button()", "click desacive button()"
                            )
                    ) {
                        mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed =
                            !mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed
                    }
                }

                if (button9AlwaysVisible && !showButtons) {
                    Button_9(
                        label_Datas = Label_Datas.get_Default()
                            .copy(showLabels, "toggle_selectedTypeChoisi()")
                    ) {
                        if(mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed){
                            button9AlwaysVisible = !button9AlwaysVisible
                        }
                    }
                }

                if (button8AlwaysVisible && !showButtons) {
                    ButtonId8(showLabels, viewModel) { button9AlwaysVisible = false }
                }

                if (!showButtons) {
                    ButtonId7(
                        viewModel = viewModel,
                        showLabels = showLabels,
                    )
                }

                if (!showButtons) {
                    ButtonId6(
                        showLabels = showLabels,
                    )
                }

                if (!showButtons) {
                    CameraFABProtoJuin3(
                        activeCatalogue = uiState.activeCatalogue,
                    )
                }

                if (!showButtons) {
                    But1(
                        showDialog = showDialog,
                        showLabels = showLabels,
                        onShowDialog = { showDialog = true },
                    )
                }

                if (!showButtons) {
                    But2(
                        showLabels = showLabels,
                        selectedCount = selectedProducts.size,
                        onBulkMove = onShowBulkMoveDialog,
                    )
                }

                if (!showButtons) {
                    ButtonId3(
                        viewModel = viewModel,
                        showLabels = showLabels,
                        selectedCount = selectedCategories.size,
                        onCatalogueMove = { showCatalogueDialog = true },
                    )
                }

                if (!showButtons) {
                    ButtonId4(
                        viewModel = viewModel,
                        showLabels = showLabels,
                    )
                }


                if (!showButtons) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showLabels) Text("AfficheElements")
                        FloatingActionButton(
                            onClick = {
                                viewModelScope.launch { onToggle() }
                            },
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
                }

                if (showButtons) {
                    if (!mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed) {
                        Button_10(
                            label_Datas = Label_Datas.get_Default()
                                .copy(showLabels, "mode_Click()")
                        ) {
                            mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed =
                                !mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed
                        }
                    }

                    // When in click mode, clicking a button makes it persistent
                    Button_9(
                        label_Datas = Label_Datas.get_Default()
                            .copy(showLabels, "toggle_selectedTypeChoisi()")
                    ) {
                        if (mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed) {
                            button9AlwaysVisible = true
                        }
                    }

                    ButtonId8(showLabels, viewModel)

                    ButtonId7(
                        viewModel = viewModel,
                        showLabels = showLabels,
                    )

                    CameraFABProtoJuin3(
                        activeCatalogue = uiState.activeCatalogue,
                    )

                    ButtonId6(
                        showLabels = showLabels,
                    )

                    But1(
                        showDialog = showDialog,
                        showLabels = showLabels,
                        onShowDialog = { showDialog = true },
                    )

                    But2(
                        showLabels = showLabels,
                        selectedCount = selectedProducts.size,
                        onBulkMove = onShowBulkMoveDialog,

                        )

                    ButtonId3(
                        viewModel = viewModel,
                        showLabels = showLabels,
                        selectedCount = selectedCategories.size,
                        onCatalogueMove = { showCatalogueDialog = true },
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (showLabels) Text("AfficheElements")
                        FloatingActionButton(
                            onClick = {
                                viewModelScope.launch { onToggle() }
                                if (mode_Click_Mete_Le_Clicked_Button_Persistent_AlwaysShowed) {
                                }
                            },
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
