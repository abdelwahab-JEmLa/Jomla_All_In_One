package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.Buttons.View

import EntreApps.Shared.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Button_StockOptions_AddColorsToEchantillons(
    onDismiss: () -> Unit,
    repositorysMainGetter: RepositorysMainGetter,
    relative_M8BonVent: M8BonVent,
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    context: Context,
) {
    fun get_relative_operations_du_bon() =
        repositorysMainGetter.repo10OperationVentCouleur.datasValue.filter {
            it.parent_M8BonVent_KeyId == relative_M8BonVent.keyID
        }

    fun get_updated_Colors(): List<M3CouleurProduitInfos> =
        get_relative_operations_du_bon().mapNotNull { operation ->
            repositorysMainGetter
                .find_M3CouleurInfos_By_KeyID(operation.parent_M3CouleurProduit_KeyID)
                ?.copy(its_in_echantiallants = true)
        }

    val updatedColors = get_updated_Colors()

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                set(
                    value = updatedColors
                        .filter { it.its_in_echantiallants == true }
                        .map { it.parentId1ProduitInfosDebugName },
                    key = SemanticsPropertyKey("")
                )
                set(
                    value = get_relative_operations_du_bon()
                        .map {
                            it.keyID.takeLast(3) to
                                    (it.parent_M3CouleurProduit_KeyID.takeLast(3) to it.parent_M3CouleurProduit_DebugInfos)
                        },
                    key = SemanticsPropertyKey("getSet_parent_M3CouleurProduit_DebugInfos()")
                )
            },
        onClick = {
            onDismiss()
            viewModel.fireBase_batch_set_list_M3CouleurProduitInfos(updatedColors)
            Toast.makeText(
                context,
                "تمت إضافة ${updatedColors.size} لون إلى العينات النشطة",
                Toast.LENGTH_SHORT
            ).show()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "إضافة ألوان الطلبية إلى العينات النشطة",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
