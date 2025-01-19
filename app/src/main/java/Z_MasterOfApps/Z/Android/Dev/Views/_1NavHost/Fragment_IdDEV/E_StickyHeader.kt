package Z_MasterOfApps.Z.Android.Dev.Views._1NavHost.Fragment_IdDEV

import Z_MasterOfApps.Kotlin.Model._ModelAppsFather
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GrossistHeader_F5(
    grossist: _ModelAppsFather.ProduitModel.GrossistBonCommandes.GrossistInformations?,
    selectedProductsCount: Int,
    onMoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2C2C2C))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = grossist?.nom ?: "",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        IconButton(onClick = onMoveClick) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.DragIndicator,
                    contentDescription = "Move products",
                    tint = Color.White
                )
                if (selectedProductsCount > 0) {
                    Text(
                        "$selectedProductsCount",
                        color = Color.White
                    )
                }
            }
        }
    }
}
