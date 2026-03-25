package Application4.App.Main.A.Navigation.Component.Main_DropDown.When_Its_FacadeElectroBoutique.Button.Views

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LabelOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Toggle_Active_section_ToggleButton_TagPreiorities__start_Collapsed_DropdownMenuItem(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns,
    onDismissDropdown: () -> Unit
) {
    val isActive = viewModelNewProtoPatterns.active_Datas.section_ToggleButton_TagPrioriter__start_Collapsed

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = if (isActive == true) Icons.Default.Label else Icons.Default.LabelOff,
                contentDescription = null,
                tint = if (isActive == true) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        text = {
            Text(
                text = if (isActive == true) "Tags priorités : actif" else "Tags priorités : inactif",
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive == true) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        onClick = {
            // null → true, true → null  (toggle)
            viewModelNewProtoPatterns.active_Datas.section_ToggleButton_TagPrioriter__start_Collapsed =
                if (isActive == true) null else true
            onDismissDropdown()
        }
    )
}
