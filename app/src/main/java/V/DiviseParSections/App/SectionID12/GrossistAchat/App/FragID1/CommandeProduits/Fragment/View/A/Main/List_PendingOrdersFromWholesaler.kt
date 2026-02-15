package V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.A.Main

import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.View_AchatCouleur
import V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.ViewModel.GrossistAchatSec12FragID1_ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.koinInject

@Composable
fun List_PendingOrdersFromWholesaler(
    modifier: Modifier = Modifier,
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    productsWithOrders: List<Triple<ArticlesBasesStatsTable, List<M3CouleurProduitInfos>, Int>>,
    aCentralFacade: ACentralFacade = viewModel.aCentralFacade,
    repositorysMainSetter: RepositorysMainSetter = koinInject()
) {
    var selectedColor by remember { mutableStateOf<M3CouleurProduitInfos?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Create mock M11AchatOperation entries for each product-color combination with pending orders
    val groupedAchatOperations by remember(productsWithOrders) {
        derivedStateOf {
            productsWithOrders.mapNotNull { (product, colors, _) ->
                // Filter colors with pending orders
                val colorsWithOrders = colors.filter { it.a_cammende_depuit_grossist > 0 }
                if (colorsWithOrders.isEmpty()) return@mapNotNull null

                // Create a mock M11AchatOperation for each color
                val achatOperations = colorsWithOrders.map { color ->
                    M11AchatOperation(
                        keyID = "pending_${product.keyID}_${color.keyID}",
                        parent_M3CouleurProduit_KeyID = color.keyID,
                        parent_M3CouleurProduit_DebugInfos = color.nomCouleurStrSiSonImageDispo,
                        sumAchatQantity = color.a_cammende_depuit_grossist,
                        prix_Achat_De_Cette_Grossist = 0.0,
                        parent_M15Grossist_KeyID = "",
                        parent_M15Grossist_DebugInfos = "",
                        parent_M14VentPeriod_KeyID = "",
                        parent_M14VentPeriod_DebugInfos = "",
                        dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                    )
                }

                product.keyID to achatOperations
            }.toMap().entries.toList()
        }
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Commandes en attente",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Commandes en attente du grossiste",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        items(groupedAchatOperations) { groupeAchatProduit ->
            // Create a custom View_AchatProduitOperation wrapper that handles clicks on individual colors
            View_AchatProduitOperation_WithColorClick(
                viewModel = viewModel,
                groupeAchatProduit = groupeAchatProduit,
                onColorClick = { clickedColor ->
                    selectedColor = clickedColor
                    showDialog = true
                }
            )
        }
    }

    // Dialog for editing pending order quantity
    if (showDialog && selectedColor != null) {
        Dialog_Edit_Pending_Order(
            color = selectedColor!!,
            onDismiss = {
                showDialog = false
                selectedColor = null
            },
            onSave = { newQuantity ->
                val updatedColor = selectedColor!!.copy(
                    a_cammende_depuit_grossist = newQuantity,
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
                repositorysMainSetter.addOrUpdateData_M3CouleurProduitInfos(updatedColor)
                showDialog = false
                selectedColor = null
            }
        )
    }
}

@Composable
private fun View_AchatProduitOperation_WithColorClick(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    groupeAchatProduit: Map.Entry<String, List<M11AchatOperation>>,
    onColorClick: (M3CouleurProduitInfos) -> Unit
) {
    val produit = viewModel.getter.repo1ProduitInfos.datasValue.find { it.keyID == groupeAchatProduit.key }

    if (produit != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            androidx.compose.foundation.layout.Column {
                // Use the existing Header component
                V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.W_AchatProduitOperation.View.Header(
                    produit,
                    viewModel = viewModel,
                    groupeAchatProduit = groupeAchatProduit,
                )

                // Custom List with click handling for each color
                List_AchatCouleurOperation_WithClick(
                    viewModel = viewModel,
                    listAchatCouleur = groupeAchatProduit.value,
                    onColorClick = onColorClick
                )
            }
        }
    }
}

@Composable
private fun List_AchatCouleurOperation_WithClick(
    viewModel: GrossistAchatSec12FragID1_ViewModel,
    listAchatCouleur: List<M11AchatOperation>,
    onColorClick: (M3CouleurProduitInfos) -> Unit
) {
    val aCentralFacade = viewModel.aCentralFacade

    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.lazy.LazyRow(
            state = androidx.compose.foundation.lazy.rememberLazyListState(),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listAchatCouleur) { achatCouleur ->
                androidx.compose.material3.VerticalDivider(
                    thickness = 9.dp,
                    color = androidx.compose.ui.graphics.Color.Red,
                    modifier = Modifier.width(9.dp)
                )

                // Wrap View_AchatCouleur with clickable Card
                Card(
                    modifier = Modifier.clickable {
                        // Find the M3CouleurProduitInfos from the achatCouleur
                        val colorKeyId = achatCouleur.parent_M3CouleurProduit_KeyID
                        val color = aCentralFacade.repositorysMainGetter.repo03CouleurProduitInfos.datasValue
                            .find { it.keyID == colorKeyId }
                        if (color != null) {
                            onColorClick(color)
                        }
                    }
                ) {
                    View_AchatCouleur(
                        relative_M11AchatOperation = achatCouleur,
                        viewModel = viewModel,
                    )
                }
            }
        }

        if (listAchatCouleur.size > 1) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                V.DiviseParSections.App.SectionID12.GrossistAchat.App.FragID1.CommandeProduits.Fragment.View.B.List.Z.List.W.Ui.AfficheIconVentMultiItems()
            }
        }
    }
}

@Composable
private fun Dialog_Edit_Pending_Order(
    color: M3CouleurProduitInfos,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var textValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Modifier quantité à commander",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Couleur: ${color.nomCouleurStrSiSonImageDispo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Ancienne quantité: ${color.a_cammende_depuit_grossist}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text(color.a_cammende_depuit_grossist.toString()) },
                    placeholder = { Text("Entrer un nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val newQuantity = textValue.toIntOrNull()
                            if (newQuantity != null && newQuantity >= 0) {
                                focusManager.clearFocus()
                                onSave(newQuantity)
                            }
                        }
                    ),
                    singleLine = true,
                    isError = textValue.toIntOrNull() == null && textValue.isNotEmpty()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onDismiss()
                        }
                    ) {
                        Text("Annuler")
                    }

                    FloatingActionButton(
                        onClick = {
                            val newQuantity = textValue.toIntOrNull()
                            if (newQuantity != null && newQuantity >= 0) {
                                focusManager.clearFocus()
                                onSave(newQuantity)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            "Enregistrer",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
