package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.REORDER_GRID

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.A.ViewModel.EditeBaseDonneMainScreenIdS9ViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.Repo01Produit.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.M16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.RepoM16CategorieProduit
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
internal fun MainItem(
    viewModel: EditeBaseDonneMainScreenIdS9ViewModel,
    aCentralFacade: ACentralFacade = koinInject(),
    focusedValuesGetter: FocusedValuesGetter = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter,
    repositorysMainSetter: RepositorysMainSetter = aCentralFacade.repositorysMainSetter,
    repoM16CategorieProduit: RepoM16CategorieProduit = aCentralFacade.repositorysMainGetter.repoM16CategorieProduit,
    relative_category: M16CategorieProduit,
    productsByCategory: Map<Long, List<ArticlesBasesStatsTable>>,
) {
    val list_repoM16CategorieProduit = repoM16CategorieProduit.datasValue
    val uiState by viewModel.uiState.collectAsState()
    val activeCentralValues = focusedValuesGetter.active_Central_Values

    val categoryProducts = productsByCategory[relative_category.id] ?: emptyList()

    var deleteCountdown by remember { mutableStateOf(0) }
    var isInDeleteMode by remember { mutableStateOf(false) }

    LaunchedEffect(deleteCountdown) {
        if (deleteCountdown > 0) {
            delay(1000)
            deleteCountdown -= 1
        } else if (isInDeleteMode) {
            isInDeleteMode = false
        }
    }

    val selectionePourDeplacement_Categorie = uiState.selectionePourDeplacement_Categorie
    val selected_Cate_Key = selectionePourDeplacement_Categorie?.keyID ?: ""
    val isSelected =
        selected_Cate_Key == relative_category.keyID

    val hasSelections = selectionePourDeplacement_Categorie != null
    val affiche_DeleteButtons = activeCentralValues.affiche_DeleteButtons

    val old_Index = list_repoM16CategorieProduit.indexOf(selectionePourDeplacement_Categorie)
    val targeted_Index = list_repoM16CategorieProduit.indexOf(relative_category)
    val newAdd_Index = maxOf(0, targeted_Index - 1)

    Box {
        Card(
            modifier = Modifier
                .semantics(mergeDescendants = true) {
                    set(
                        value = selectionePourDeplacement_Categorie,
                        key = SemanticsPropertyKey("selectionePourDeplacement_Categorie")
                    )
                }
                .semantics(mergeDescendants = true) {
                    set(
                        value = relative_category,
                        key = SemanticsPropertyKey("relative_category")
                    )
                }
                .getSemanticsTag(relative_category, "")
                .height(150.dp)
                .fillMaxWidth()
                .clickable {
                    viewModel.updateCate_cSelectionePourDeplace(relative_category)
                },
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row with delete button (if needed) and reorder button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Delete button at top start
                    if (affiche_DeleteButtons) {
                        IconButton(
                            modifier = Modifier.size(25.dp),
                            onClick = {
                                if (!isInDeleteMode) {
                                    // First click: start countdown
                                    isInDeleteMode = true
                                    deleteCountdown = 3
                                } else {
                                    // Second click: delete immediately
                                    repoM16CategorieProduit.delete(relative_category)
                                    isInDeleteMode = false
                                    deleteCountdown = 0
                                }
                            }
                        ) {
                            if (isInDeleteMode && deleteCountdown > 0) {
                                // Show countdown number
                                Text(
                                    text = deleteCountdown.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    Icons.Default.Delete,
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = if (isInDeleteMode) "Confirm delete" else "Delete category",
                                    tint = if (isInDeleteMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.size(25.dp))
                    }

                    // Reorder button at top end
                    if (hasSelections) {
                        IconButton(
                            modifier = Modifier.size(25.dp),
                            onClick = {
                                val positionDouble_SC =
                                    relative_category.positionDouble

                                if (selectionePourDeplacement_Categorie != null) {
                                    repositorysMainSetter.upsert_M16CategorieProduit(
                                        selectionePourDeplacement_Categorie.copy(
                                            positionDouble = positionDouble_SC
                                        )
                                    )
                                }

                                viewModel.updateCate_cSelectionePourDeplace(null)
                            }
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowUp,
                                modifier = Modifier
                                    .semantics(mergeDescendants = true) {
                                        set(
                                            value = selectionePourDeplacement_Categorie,
                                            key = SemanticsPropertyKey("selectionePourDeplacement_Categorie")
                                        )
                                    }
                                    .semantics(mergeDescendants = true) {
                                        set(
                                            value = relative_category,
                                            key = SemanticsPropertyKey("relative_category")
                                        )
                                    }
                                    .size(25.dp),
                                contentDescription = "Reorder category",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(25.dp))
                    }
                }

                RowProduitImages(categoryProducts.take(3))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    val positionDouble = relative_category.positionDouble
                    Text(
                        modifier = Modifier
                            .semantics(mergeDescendants = true) {
                                set(SemanticsPropertyKey(""), relative_category.position)
                            }
                            .fillMaxWidth(),
                        text = "${relative_category.nom}-",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )

                    if (isSelected) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(6.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✓",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
