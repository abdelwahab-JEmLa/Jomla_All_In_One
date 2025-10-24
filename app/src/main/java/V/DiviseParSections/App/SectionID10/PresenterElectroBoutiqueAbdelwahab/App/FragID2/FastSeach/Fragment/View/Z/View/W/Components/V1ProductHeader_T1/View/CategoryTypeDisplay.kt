package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.W.Components.V1ProductHeader_T1.View

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.RepoM16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun CategoryTypeDisplay(
    produit: ArticlesBasesStatsTable,
    category: CategoriesTabelle?,
    modifier: Modifier = Modifier,
    aCentralFacade: ACentralFacade = koinInject(),
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
    repo1ProduitInfos: RepoM1Produit = aCentralFacade.repositorysMainGetter.repo1ProduitInfos,
    repoM16CategorieProduit: RepoM16CategorieProduit = aCentralFacade.repositorysMainGetter.repoM16CategorieProduit,
    repositorysMainSetter: RepositorysMainSetter
) {
    // State management
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember(produit.nom_type_categorie) {
        mutableStateOf(
            produit.nom_type_categorie.ifEmpty { category?.nom ?: "" }
        )
    }
    val focusRequester = remember { FocusRequester() }
    var expanded by remember { mutableStateOf(false) }

    // Get distinct names from categories and products
    val dropdownNoms = remember(
        repoM16CategorieProduit.tigerDataRecompose,
        repo1ProduitInfos.datasValue
    ) {
        val categoryNames = repoM16CategorieProduit.datasValue
            .map { it.nom }
            .filter { it.isNotBlank() }
            .distinct()

        val productCategoryNames = repo1ProduitInfos.datasValue
            .map { it.nom_type_categorie }
            .filter { it.isNotBlank() }
            .distinct()

        (categoryNames + productCategoryNames)
            .distinct()
            .sorted()
    }

    // Filter dropdown items based on text input
    val filteredDropdownNoms = remember(textValue, dropdownNoms) {
        if (textValue.isEmpty()) {
            dropdownNoms
        } else {
            dropdownNoms.filter {
                it.contains(textValue, ignoreCase = true)
            }
        }
    }

    // Get initial text for editing
    val initialEditText = remember(produit.nom_type_categorie, category?.nom) {
        val currentText = produit.nom_type_categorie.ifEmpty { category?.nom ?: "" }
        if (currentText.startsWith("Confiserie_New_Arrivage")) {
            ""
        } else {
            currentText
        }
    }

    // Check if nom_type_categorie matches category name
    val isMatching = produit.nom_type_categorie.isNotEmpty() &&
            produit.nom_type_categorie == category?.nom

    // Save function
    fun saveText() {
        if (textValue != produit.nom_type_categorie) {
            repositorysMainSetter.upsert_M1Produit(
                produit.copy(
                    nom_type_categorie = textValue,
                    dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                )
            )
        }
        isEditing = false
        expanded = false
    }

    // Cancel function
    fun cancelEditing() {
        textValue = initialEditText
        isEditing = false
        expanded = false
    }

    if (isEditing) {
        Box(modifier = modifier) {
            OutlinedTextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    expanded = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isMatching) FontWeight.Bold else FontWeight.Normal
                ),
                placeholder = {
                    Text(
                        category?.nom ?: "Type",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (isMatching) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = if (isMatching) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = if (isMatching) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (isMatching) MaterialTheme.colorScheme.error.copy(
                        alpha = 0.7f
                    ) else MaterialTheme.colorScheme.outline,
                    cursorColor = if (isMatching) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    focusedContainerColor = if (isMatching) MaterialTheme.colorScheme.errorContainer.copy(
                        alpha = 0.3f
                    ) else Color.Transparent,
                    unfocusedContainerColor = if (isMatching) MaterialTheme.colorScheme.errorContainer.copy(
                        alpha = 0.2f
                    ) else Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        saveText()
                    }
                ),
                trailingIcon = {
                    if (textValue.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textValue = ""
                                expanded = false
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Effacer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            )

            // Dropdown menu with filtered items - shows only after 3 characters
            DropdownMenu(
                expanded = expanded && filteredDropdownNoms.isNotEmpty() && textValue.length >= 3,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 200.dp)
            ) {
                filteredDropdownNoms.take(10).forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (item == category?.nom) FontWeight.Bold else FontWeight.Normal,
                                color = if (item == category?.nom) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        },
                        onClick = {
                            textValue = item
                            expanded = false
                            // Auto-save when selecting from dropdown
                            repositorysMainSetter.upsert_M1Produit(
                                produit.copy(
                                    nom_type_categorie = item,
                                    dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                                )
                            )
                            isEditing = false
                        },
                        trailingIcon = {
                            if (item == category?.nom) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Catégorie actuelle",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    )
                }

                // Show message if no results
                if (filteredDropdownNoms.isEmpty() && textValue.isNotEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Aucun résultat trouvé",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        onClick = { expanded = false }
                    )
                }
            }
        }

        // Request focus when entering edit mode
        LaunchedEffect(isEditing) {
            if (isEditing) {
                focusRequester.requestFocus()
                expanded = true
            }
        }
    } else {
        // Display mode - clickable to edit
        Card(
            modifier = modifier.clickable {
                textValue = initialEditText
                isEditing = true
            },
            colors = CardDefaults.cardColors(
                containerColor = if (isMatching) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = textValue.ifEmpty { category?.nom ?: "Type" },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isMatching) FontWeight.Bold else FontWeight.Medium,
                    color = if (isMatching) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Quick action buttons
                if (textValue != category?.nom && category?.nom != null) {
                    IconButton(
                        onClick = {
                            textValue = category.nom
                            repositorysMainSetter.upsert_M1Produit(
                                produit.copy(
                                    nom_type_categorie = category.nom,
                                    dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
                                )
                            )
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Réinitialiser",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
