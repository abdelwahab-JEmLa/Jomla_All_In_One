package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Item_PrixUnitaireClient_FragID3(
    clientPrixVentUnite: Double,
    isExpanded: Boolean,
    onUpdate: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val labelTextSize = if (isExpanded) 10.sp else 7.sp
    val valueTextSize = if (isExpanded) 12.sp else 9.sp
    val iconSize      = if (isExpanded) 14.dp else 10.dp
    val itemPadding   = if (isExpanded) 4.dp  else 2.dp

    var isEditing by remember { mutableStateOf(false) }
    // reset si la valeur externe change (mise à jour depuis ailleurs)
    var textValue by remember(clientPrixVentUnite) {
        mutableStateOf("%.2f".format(clientPrixVentUnite))
    }

    fun confirm() {
        val parsed = textValue.replace(",", ".").toDoubleOrNull()
        if (parsed != null) onUpdate(parsed)
        isEditing = false
    }

    if (isEditing) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = itemPadding + 2.dp, vertical = itemPadding),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(iconSize)
                )
                BasicTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { confirm() }),
                    textStyle = TextStyle(
                        fontSize = valueTextSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.width(56.dp)
                )
                IconButton(
                    onClick = { confirm() },
                    modifier = Modifier.size(iconSize + 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirmer",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    } else {
        ClickableInfoCard(
            icon = {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Prix client unité",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(iconSize)
                )
            },
            value         = "%.2f".format(clientPrixVentUnite),
            label         = "P.U.Cli",
            labelTextSize = labelTextSize,
            valueTextSize = valueTextSize,
            itemPadding   = itemPadding,
            onClick       = { isEditing = true },
            modifier      = modifier
        )
    }
}
