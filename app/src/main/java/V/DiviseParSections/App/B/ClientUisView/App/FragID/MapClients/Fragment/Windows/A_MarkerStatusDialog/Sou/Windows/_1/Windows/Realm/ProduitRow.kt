package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows.Realm.Repository.Produit
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// In ProduitRow.kt

@Composable
fun ProduitRow(
    produit: Produit,
    monitoredProductKey: String = "",
    monitoredQuantity: Int = 0
) {
    // For monitored product, use the observed quantity
    val quantity = if (produit.keyID == monitoredProductKey) {
        monitoredQuantity
    } else {
        produit.quantity
    }

    // Create a local state with the appropriate quantity
    val displayQuantity = remember(quantity) { mutableStateOf(quantity) }

    // Log for debugging
    if (produit.keyID == monitoredProductKey) {
        Log.d("ProduitRow", "Displaying monitored product with quantity: $quantity")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = produit.nom,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = displayQuantity.value.toString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
