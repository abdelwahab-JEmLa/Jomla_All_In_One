package Application4.App.Fragment.View.Components.A_Header.View

import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS.FastInit_Outlined_Int_Edite_Modulable_Proto4
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
 fun EditableInfoCard(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    labelTextSize: TextUnit,
    valueTextSize: TextUnit,
    itemPadding: Dp,
    startCount: Int,
    isExpanded: Boolean,
    onUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier.Companion
) {
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing) {
        FastInit_Outlined_Int_Edite_Modulable_Proto4(
            start_count = startCount,
            standard_count = 1,
            force_edit_mode_on_start = true,
            isAvailable = true,
            compact_taille = !isExpanded,
            is_admin = true,
            modifier = modifier,
            on_Data_Update = { newValue ->
                onUpdate(newValue)
                isEditing = false
            }
        )
    } else {
        Card(
            modifier = modifier.clickable { isEditing = true },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.Companion.padding(
                    horizontal = itemPadding + 2.dp,
                    vertical = itemPadding
                ),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                icon()
                Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
                    Text(
                        text = label,
                        fontSize = labelTextSize,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Companion.Medium,
                        lineHeight = labelTextSize
                    )
                    Text(
                        text = value,
                        fontSize = valueTextSize,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Companion.Bold,
                        lineHeight = valueTextSize
                    )
                }
            }
        }
    }
}
