package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.Buttons.View

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Modules.Wi.Module.Wifi_Messages_Types_NewProto
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun Button_StockOptions_SubtractFromDepot(
    viewModelNewProtoPatterns_passed: A_ViewModel_NewProtoPatterns?=null,
    onDismiss: () -> Unit,
    repositorysMainGetter: RepositorysMainGetter,
    repositorysMainSetter: RepositorysMainSetter,
    relative_M8BonVent: M8BonVent,
    context: Context,
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onDismiss()
            var updatedCount = 0
            // Accumulate (keyID → newCount) pairs so we can batch-send them over WiFi.
            val depotUpdates = mutableListOf<Pair<String, Int>>()

            viewModelNewProtoPatterns_passed?.active_Datas?.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
                ?.forEach { vent ->
                    repositorysMainGetter.repo03CouleurProduitInfos.datasValue
                        .find { it.keyID == vent.parent_M3CouleurProduit_KeyID }
                        ?.let { couleur ->
                            val newCount = couleur.count_Don_Depot - vent.quantity
                            val n_data = couleur.copy(count_Don_Depot = newCount)
                            repositorysMainSetter.addOrUpdateData_M3CouleurProduitInfos(
                                n_data
                            )
                            viewModelNewProtoPatterns_passed.update_m3couleur(n_data)

                            depotUpdates.add(couleur.keyID to newCount)
                            updatedCount++
                        }
                }

            if (depotUpdates.isNotEmpty()) {
                val jsonArray = JSONArray().apply {
                    depotUpdates.forEach { (keyID, count) ->
                        put(JSONObject().apply {
                            put("keyID", keyID)
                            put("count_Don_Depot", count)
                        })
                    }
                }
                val jsonPayload = JSONObject()
                    .put("list_m3_a_Update_Leur_Count_Depot", jsonArray)
                    .toString()

                viewModelNewProtoPatterns_passed?.sendOrderToClientDisplayerT(
                    order = Wifi_Messages_Types_NewProto.Update_Depot_Count_Par_Chain_Key_to_NewCount,
                    data = jsonPayload,
                )
            }

            Toast.makeText(
                context,
                "تم خصم كميات $updatedCount لون من المستودع",
                Toast.LENGTH_SHORT
            ).show()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Icon(
            imageVector = Icons.Default.RemoveShoppingCart,
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "خصم كميات الطلبية من المستودع",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
