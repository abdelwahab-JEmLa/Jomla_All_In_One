package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Mode_EditePrixs.AffichageDuMode_EditePrix
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.ActionButtons
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.HeaderSection
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.Prix.Components.Prix_Detailer_Section
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.B_MainItem.Views.QuickInfoSection
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.PRODUCTS_LIST.ViewModel.Sec9FragId1ViewId2ViewModel
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Ui.StringEditor
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.PrixAjustableButtons.Fragment.ItsLancedDepuit
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.ActiveCentralValues.ModeEditesProduit
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ProductItem(
    aCentralFacade: ACentralFacade= koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    modifier: Modifier = Modifier,
    mainComposRepository: RepoM1Produit,
    produit: ArticlesBasesStatsTable,
    uiState: Sec9FragId1ViewId2ViewModel.UiState,
    shouldHideQuickInfoCards: Boolean,
    onNextField: (() -> Unit)? = null,
    focusRequester: FocusRequester? = null,
    viewModel: Sec9FragId1ViewId2ViewModel
) {
    val paddingDefaulte = 3.dp

    val modifierWithDefinedPadding = modifier.padding(4.dp)
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showNameEditor by remember { mutableStateOf(false) }

    fun updateProduct(updatedProduct: ArticlesBasesStatsTable) {
        mainComposRepository.upsert(updatedProduct)
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Supprimer le produit",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Êtes-vous sûr de vouloir supprimer \"${produit.nom}\" ?\n\nCette action est irréversible."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mainComposRepository.deleteData(produit)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Supprimer",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Annuler")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        )
    }

    // Name editor dialog
    if (showNameEditor) {
        AlertDialog(
            onDismissRequest = { showNameEditor = false },
            title = { Text("Modifier le nom du produit") },
            text = {
                StringEditor(
                    currentValue = produit.nom,
                    label = "Etate du produit",
                    onValueUpdate = { newName ->
                        updateProduct(produit.copy(nom = newName))
                        showNameEditor = false
                    },
                    onCancel = { showNameEditor = false }
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    val itsLancedDepuit =
        if (focusedValuesGetter.active_Central_Values.active_ModeEditesProduit == ModeEditesProduit.PrixHanled)
            ItsLancedDepuit.EditeBaseDonne(relative_Produit = produit)
        else
            ItsLancedDepuit.Autres

    Card(
        modifier = modifierWithDefinedPadding
            .fillMaxWidth()
            .let { if (focusRequester != null) it.focusRequester(focusRequester) else it },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        // FIXED: Added the missing onShowNameEditorChange parameter
        when (itsLancedDepuit) {
            is ItsLancedDepuit.EditeBaseDonne -> {
                AffichageDuMode_EditePrix(
                    relative_produit = produit,
                    updateProduct = ::updateProduct,
                    onShowDeleteDialogChange = { showDeleteDialog = it },
                    onShowNameEditorChange = { showNameEditor = it }, // FIXED: Added missing parameter
                    paddingDefaulte = paddingDefaulte
                )
            }

            else -> {
                Column(
                    modifier = modifierWithDefinedPadding.fillMaxWidth()
                ) {
                    HeaderSection(
                        produit = produit,
                        onShowNameEditorChange = { showNameEditor = it },
                        onShowDeleteDialogChange = { showDeleteDialog = it },
                        updateProduct = ::updateProduct,
                        paddingDefaulte = paddingDefaulte
                    )

                    if (!shouldHideQuickInfoCards) {
                        ActionButtons(
                            uiState = uiState,
                            viewModel = viewModel,
                            modifier = modifierWithDefinedPadding,
                            produit = produit,
                            updateProduct = ::updateProduct
                        )
                    }

                    QuickInfoSection(
                        shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                        modifier = modifier,
                        produit = produit,
                        updateProduct = ::updateProduct,
                    )

                    Prix_Detailer_Section(
                        modifier = modifierWithDefinedPadding,
                        viewModel = viewModel,
                        relative_M1Produit = produit,
                        shouldHideQuickInfoCards = shouldHideQuickInfoCards,
                        showDetailsExpanded = uiState.showDetailsExpandedPourTout,
                        onNextField = onNextField,
                        updateProduct = ::updateProduct
                    )
                }
            }
        }

    }
}
