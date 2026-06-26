package A_Main.Shared.Views.Dialogs.B.Dialoge

import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.get_ListM21CataloguesCategorie
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.getPushFireBase
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

fun get_New_Datas(
    searchQuery: String,
    aCentralFacade: ACentralFacade,
    repositorysMainGetter: RepositorysMainGetter = aCentralFacade.repositorysMainGetter,
): Pair<M01Produit?, M3CouleurProduitInfos?> {
    val catalogues = get_ListM21CataloguesCategorie().sortedBy { it.position }
    val newOldId = repositorysMainGetter.repo1ProduitInfos.datasValue.maxOf { it.id } + 1
    val idParentCategorie = catalogues.find {
        it.keyID == "t1"
    }?.premierCategorieId

    val keyIDM3CouleurProduitInfos = getPushFireBase(M3CouleurProduitInfos.ref)
    val keyID = getPushFireBase(M01Produit.ref)

    val newProduit = idParentCategorie?.let {
        M01Produit.get_Default().copy(
            keyID = keyID,
            id = newOldId,
            creationTimestamp = System.currentTimeMillis(),
            nom = searchQuery,
            couleur1 = keyIDM3CouleurProduitInfos,
            idParentCategorie = it,
            disponibilityEtates = DisponibilityEtates.NON_DISPO
        )
    }

    val newCouleurP = newProduit?.let {
        M3CouleurProduitInfos.get_default().copy(
            keyID = keyIDM3CouleurProduitInfos,
            creationTimestamp = System.currentTimeMillis(),
            parentBProduitInfosKeyID = it.keyID,
            parentId1ProduitInfosDebugName = newProduit.nom,
            parentBProduitOldID = newProduit.id,
        )
    }

    return Pair(newProduit, newCouleurP)
}

@Composable
fun But_4_FloatingSearchFAB(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    currentMode: Filter_Affichage_Mode_Proto,
    modifier: Modifier = Modifier.Companion,
    aCentralFacade: ACentralFacade = koinInject(),
    onProductCreated: (M01Produit, M3CouleurProduitInfos) -> Unit,
) {
    var showField by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.Companion.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        FloatingActionButton(
            modifier = Modifier.Companion.size(40.dp),
            onClick = {
                if (showField) {
                    onSearchTextChange("")
                    keyboardController?.hide()
                    showField = false
                } else {
                    onSearchTextChange("")
                    showField = true
                    scope.launch {
                        delay(50)
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.tertiary,
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rechercher un produit",
                tint = Color.Companion.White
            )
        }
        if (showField) {

            OutlinedTextField(
                value = searchText,
                onValueChange = { newText ->
                    val capitalizedText = newText.split(" ").joinToString(" ") { word ->
                        if (word.isNotEmpty()) {
                            word.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase() else it.toString()
                            }
                        } else word
                    }
                    onSearchTextChange(capitalizedText)
                },
                modifier = Modifier.Companion
                    .width(200.dp)
                    .height(56.dp)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        "Rechercher...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    if (searchText.isNotEmpty()) {
                        val newDatas = get_New_Datas(
                            searchQuery = searchText,
                            aCentralFacade = aCentralFacade,
                        )
                        IconButton(
                            onClick = {
                                val newProduit = newDatas.first
                                val newCouleur = newDatas.second
                                if (newProduit != null && newCouleur != null) {
                                    onProductCreated(newProduit, newCouleur)
                                    val statusMessage =
                                        if (aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.active_Central_Values.active_EtateDispoNonDifinieAuAddNew) {
                                            "Produit créé (état non défini): ${newProduit.nom}"
                                        } else {
                                            "Produit WebP créé: ${newProduit.nom}"
                                        }
                                    Toast.makeText(context, statusMessage, Toast.LENGTH_SHORT).show()
                                    onSearchTextChange("")
                                }
                            },
                            modifier = Modifier.Companion
                                .size(24.dp)
                                .semantics(mergeDescendants = true) {
                                    set(
                                        value = newDatas,
                                        key = SemanticsPropertyKey("newDatas")
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Créer nouveau produit",
                                modifier = Modifier.Companion.size(18.dp)
                            )
                        }
                    }
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchTextChange("") },
                            modifier = Modifier.Companion.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Effacer",
                                modifier = Modifier.Companion.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Companion.Send),
                keyboardActions = KeyboardActions(
                    onSend = { onSearchTextChange("") }
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
            )
        }
    }
}
