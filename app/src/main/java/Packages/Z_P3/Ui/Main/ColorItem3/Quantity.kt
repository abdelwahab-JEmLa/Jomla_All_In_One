package Packages.Z_P3.Ui.Main.ColorItem3

import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Package_3._DisplayeProductInfosToSeller
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ClientsModel
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.ColorsArticlesTabelle
import Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models.SoldArticlesTabelle
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuantityButton(
    quantity: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    currentSale: SoldArticlesTabelle?,
    viewModelInitApp: ViewModelInitApp,
    currentClient: ClientsModel?,
    colorDetails: ColorsArticlesTabelle
) {
    Button(
        onClick = {
            onClick()
            _DisplayeProductInfosToSeller(viewModelInitApp)
                .onClickComposeQuantityButton(
                quantity,
                currentSale,
                currentClient,
                colorDetails,
            )

        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = quantity.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}








