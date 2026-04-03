package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS

import Application4.App.Fragment.View.Components.A_Header.View.EditableInfoCard
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Item_Carton_FragID3(
    quantite_Boit_Par_Carton: Int,
    isExpanded: Boolean,
    currentApp_Est_Admin: Boolean,
    onUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val labelTextSize = if (isExpanded) 10.sp else 7.sp
    val valueTextSize = if (isExpanded) 12.sp else 9.sp
    val iconSize     = if (isExpanded) 14.dp else 10.dp
    val itemPadding  = if (isExpanded) 4.dp  else 2.dp

    val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Inventory2,
            contentDescription = "Carton",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(iconSize)
        )
    }

    if (currentApp_Est_Admin) {
        EditableInfoCard(
            icon          = icon,
            value         = "$quantite_Boit_Par_Carton",
            label         = "C",
            labelTextSize = labelTextSize,
            valueTextSize = valueTextSize,
            itemPadding   = itemPadding,
            startCount    = quantite_Boit_Par_Carton,
            isExpanded    = isExpanded,
            onUpdate      = onUpdate,
            modifier      = modifier
        )
    } else {
        InfoCard(
            icon          = icon,
            value         = "$quantite_Boit_Par_Carton",
            label         = "C",
            labelTextSize = labelTextSize,
            valueTextSize = valueTextSize,
            itemPadding   = itemPadding,
            modifier      = modifier
        )
    }
}
