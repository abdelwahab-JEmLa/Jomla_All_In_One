package Z_MasterOfApps.Z.Android.Base.App.Main.C_EcranDeDepart.Startup.B.Dialogs

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather.Companion.updateProduit
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import Z_MasterOfApps.Z.Android.Base.App.Packages._2LocationGpsClients.NH_1.id1_ClientsLocationGps.B.Dialogs.ControlButton

@Composable
fun B_2_ClearAchatsEtCommendsEtSauvgardHistoriques(
    viewModelInitApp: ViewModelInitApp,
    showLabels: Boolean
) {
    var clearDataClickCount by remember { mutableIntStateOf(0) }

    ControlButton(
        onClick = {
            if (clearDataClickCount == 0) {
                clearDataClickCount++
            } else {
                viewModelInitApp._modelAppsFather.produitsMainDataBase.forEach { produit ->
                    // Safely add current data to history
                    produit.bonCommendDeCetteCota?.let { currentBonCommend ->
                        produit.historiqueBonsCommend.add(currentBonCommend)
                    }
                    if (produit.bonsVentDeCetteCotaList.isNotEmpty()) {
                        produit.historiqueBonsVents.addAll(produit.bonsVentDeCetteCotaList)
                    }

                    // Clear current data
                    produit.bonCommendDeCetteCota = null
                    produit.bonsVentDeCetteCota.clear()

                    // Update the product in the database
                    try {
                        updateProduit(produit, viewModelInitApp)
                    } catch (e: Exception) {
                        Log.e("ClearHistory", "Failed to update product ${produit.id}", e)
                    }
                }
                clearDataClickCount = 0
            }
        },
        icon =if (clearDataClickCount == 0)  Icons.Default.Delete else  Icons.Default.Done,
        contentDescription = "Clear history",
        showLabels = showLabels,
        labelText = if (clearDataClickCount == 0) "Clear History" else "Click again to confirm",
        containerColor = if (clearDataClickCount == 0) Color(0xFF4CAF50) else Color(0xFFF44336)
    )
}
