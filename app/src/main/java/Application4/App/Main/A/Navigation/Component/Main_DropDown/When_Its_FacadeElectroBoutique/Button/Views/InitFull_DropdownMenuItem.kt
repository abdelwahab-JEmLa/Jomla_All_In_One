package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.A.Start.Init.Initializer_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
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
 * Clears Room then re-seeds using the **full** strategy (Initializer_App4):
 *  all repos fetched from Firebase/Realtime-DB without reference filtering.
 */
@Composable
fun InitFull_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit
) {
    val context = LocalContext.current

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Text(
                text = "Init complète (tout Firebase)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        onClick = {
            onDismissDropdown()
            viewModelNewProtoPatterns.viewModelScope.launch(Dispatchers.IO) {
                val db = viewModelNewProtoPatterns.appDatabase
                // Delete all relevant tables
                db.dao_M03CouleurProduitInfos().deleteAll()
                db.dao_M1Produit().deleteAll()
                db.dao_16CategorieProduit().deleteAll()
                db.dao_M13TarificationInfos().deleteAll()
                db.dao_M14VentPeriode().deleteAll()
                db.dao_M8BonVent().deleteAll()
                db.dao_M10OperationVentCouleur().deleteAll()
                db.dao_M9AppCompt().deleteAll()

                // Re-seed using full strategy identical to Initializer_App4
                Initializer_App4.initializeAllRepositories(
                    context = context,
                    appDatabase = db,
                    on_Progress_Datas = { /* progress ignored for manual re-init */ },
                    dao_M1Produit = db.dao_M1Produit(),
                    dao_16CategorieProduit = db.dao_16CategorieProduit(),
                    dao_M03CouleurProduitInfos = db.dao_M03CouleurProduitInfos(),
                    dao_M13TarificationInfos = db.dao_M13TarificationInfos(),
                    dao_M14VentPeriode = db.dao_M14VentPeriode(),
                    dao_M8BonVent = db.dao_M8BonVent(),
                    dao_M10OperationVentCouleur = db.dao_M10OperationVentCouleur(),
                    dao_M9AppCompt = db.dao_M9AppCompt(),
                    callerScope = viewModelNewProtoPatterns.viewModelScope,
                )
            }
        }
    )
}
