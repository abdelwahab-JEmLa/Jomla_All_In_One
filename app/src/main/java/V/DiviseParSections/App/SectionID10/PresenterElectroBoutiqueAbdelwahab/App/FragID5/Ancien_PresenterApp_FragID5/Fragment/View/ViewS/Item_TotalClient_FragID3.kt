package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Item_TotalClient_FragID3(
    clientPrixVentUnite: Double,
    nombreUniteInt: Int,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    val labelTextSize = if (isExpanded) 10.sp else 7.sp
    val valueTextSize = if (isExpanded) 12.sp else 9.sp
    val iconSize     = if (isExpanded) 14.dp else 10.dp
    val itemPadding  = if (isExpanded) 4.dp  else 2.dp

    val totalClient = clientPrixVentUnite * nombreUniteInt

    InfoCard(
        icon = {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = "Total client",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(iconSize)
            )
        },
        value        = "%.2f".format(totalClient),
        label        = "Tot.Cli",
        labelTextSize = labelTextSize,
        valueTextSize = valueTextSize,
        itemPadding  = itemPadding,
        modifier     = modifier
    )
}
