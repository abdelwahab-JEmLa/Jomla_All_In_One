package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application2.App.Init.Initializer_Funcs_app2
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Clears M3 / M1 / M16 from Room then re-seeds using the **filtered** strategy
 * (Initializer_Funcs_app2): M3 first with ref-allowed keys → M1 filtered by M3
 * parents → M16 filtered by M1 category IDs.
 */
@Composable
fun InitFiltered_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit
) {
    val context = LocalContext.current

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FilterAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Text(
                text = "Init filtrée (réf M3)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        onClick = {
            onDismissDropdown()
            viewModelNewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
                val db = viewModelNewProtoPatterns.appDatabase

                // Reset progress bar before wiping tables
                viewModelNewProtoPatterns._uiStateNewProtoPatterns.value =
                    viewModelNewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                        initDatasProgressEtate = 0f
                    )

                db.dao_M03CouleurProduitInfos().deleteAll()
                db.dao_M1Produit().deleteAll()
                db.dao_16CategorieProduit().deleteAll()

                Initializer_Funcs_app2(
                    context = context,
                    on_Progress_Datas = { progress ->
                        viewModelNewProtoPatterns._uiStateNewProtoPatterns.value =
                            viewModelNewProtoPatterns._uiStateNewProtoPatterns.value.copy(
                                initDatasProgressEtate = progress
                            )
                    },
                    dao_M1Produit = db.dao_M1Produit(),
                    dao_16CategorieProduit = db.dao_16CategorieProduit(),
                    dao_M03CouleurProduitInfos = db.dao_M03CouleurProduitInfos(),
                ).initializeAllRepositories()
            }
        }
    )
}
