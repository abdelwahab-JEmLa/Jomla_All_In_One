package V.DiviseParSections.App.Shared.A.MemoireVive.Debug.ID1.Test

import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainFastSearchProduitPourVent(
    viewModel: ViewModelMainFastSearchProduitPourVent = koinViewModel(),
    modifier: Modifier = Modifier,
    tag: String = ""
) {
    val uiState by viewModel.uiState.collectAsState()
    val products = viewModel.getter.bProduitInfosRepository.datasValue
    val categories = viewModel.getter.b3CategoriesCompoRepository.datasValue

    // Keep it as a single val that's a Pair
    val semanticsInfo = Pair(
        SemanticsPropertyKey<String>("MainFastSearchProduitPourVent"),
        "--${Z_AppCompt.keyModel}-${ParametresAppComptNonSaved().gerantComptKeyByParent}--${Z_AppCompt.keyModelValID7VentParent}-${ParametresAppComptNonSaved().activePeriodKeyByParent}"
    )

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                set(semanticsInfo.first, semanticsInfo.second)
            }
    ) {
        Column(Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recherche Rapide Produits",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                FloatingActionButton(
                    onClick = viewModel::onAddNewProduct,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, "Ajouter produit")
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = { Text("Rechercher un produit...") },
                leadingIcon = { Icon(Icons.Default.Search, "Rechercher") },
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // FIX: Corrected MainFilterT1 call
            MainFilterT1(
                viewModel,
                products, categories, uiState.searchText, Modifier.fillMaxSize(),semanticsInfo
            )
        }
    }
}
