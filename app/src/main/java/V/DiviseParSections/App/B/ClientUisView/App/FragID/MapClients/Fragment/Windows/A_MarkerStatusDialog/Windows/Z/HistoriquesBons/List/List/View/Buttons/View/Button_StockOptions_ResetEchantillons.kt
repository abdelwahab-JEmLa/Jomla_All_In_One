package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.Buttons.View

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.ViewModel.E0AfficheHistoriqueTransactionsViewModel
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Button_StockOptions_ResetEchantillons(
    repositorysMainGetter: RepositorysMainGetter,
    viewModel: E0AfficheHistoriqueTransactionsViewModel,
    context: Context,
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState.isResettingEchantillons

    Column(modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                val allColorsReset = repositorysMainGetter.repo03CouleurProduitInfos.datasValue
                    .map { it.copy(its_in_echantiallants = false) }

                // Log: debug why its_in_echantiallants may stay true in Firebase
                Log.d("ResetEchantillons", "Sending ${allColorsReset.size} items to Firebase")
                allColorsReset.forEach { color ->
                    Log.d(
                        "ResetEchantillons",
                        "keyID=${color.keyID} | its_in_echantiallants=${color.its_in_echantiallants} | name=${color.nomCouleurStrSiSonImageDispo}"
                    )
                }

                viewModel.fireBase_batch_set_list_M3CouleurProduitInfos(
                    datas = allColorsReset,
                    onSuccess = {
                        // Toast fires only after Firebase confirms the write
                        Toast.makeText(
                            context,
                            "تمت إعادة تعيين ${allColorsReset.size} لون — تم مسح العينات ✓",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "مسح كل العينات النشطة",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
