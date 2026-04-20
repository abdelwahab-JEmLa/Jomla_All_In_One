package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.View.Buttons.View

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.Z.HistoriquesBons.List.List.Dialogs.add_Bon_Au_Stock
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

@Composable
fun But2_Add_Rapide(
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
            add_Bon_Au_Stock(
                repositorysMainGetter,
                repositorysMainSetter,
                relative_M8BonVent,
                context
            )
            Toast.makeText(
                context,
                "تم ++++ كمياتلون من المستودع",
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
            text = "But2_Add_Rapide",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
