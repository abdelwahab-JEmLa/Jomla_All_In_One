package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.View.Z.View.Z.List.UI.ViewVentCouleur_T1.View

import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

// Extension function defined at top level
fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        if (word.isNotEmpty()) {
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
        } else {
            word
        }
    }
}

@Composable
fun ColorNameDropdownTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Nom couleur",
    focusRequester: FocusRequester = remember { FocusRequester() },
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    repo03CouleurProduitInfos: Repo03CouleurProduitInfos,
    textAlign: TextAlign = TextAlign.Start
) {
    var showDropdown by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Get distinct color names from repository, excluding empty names
    val availableColors by remember(repo03CouleurProduitInfos.datasValue) {
        derivedStateOf {
            repo03CouleurProduitInfos.datasValue
                .mapNotNull { it.nomCouleurStrSiSonImageDispo.takeIf { name -> name.isNotBlank() } }
                .distinct()
                .map { it.capitalizeWords() }
                .sorted()
        }
    }

    // Filter colors based on current input
    val filteredColors by remember(value, availableColors) {
        derivedStateOf {
            if (value.isBlank()) {
                availableColors.take(10) // Show top 10 when no input
            } else {
                availableColors.filter {
                    it.contains(value, ignoreCase = true)
                }.take(8) // Show max 8 filtered results
            }
        }
    }

    // Handle value changes with auto-capitalization
    fun handleValueChange(newText: String) {
        val capitalizedText = newText.capitalizeWords()
        onValueChange(capitalizedText)
        showDropdown = isFocused && filteredColors.isNotEmpty()
    }

    // Handle color selection from dropdown
    fun selectColor(colorName: String) {
        onValueChange(colorName)
        showDropdown = false
        keyboardController?.hide()
    }

    Box(modifier = modifier) {
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = ::handleValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    textAlign = textAlign,
                    fontWeight = if (textAlign == TextAlign.Center) FontWeight.Medium else FontWeight.Normal
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        if (focusState.isFocused) {
                            showDropdown = filteredColors.isNotEmpty()
                        } else {
                            showDropdown = false
                        }
                    },
                singleLine = true,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                trailingIcon = if (isFocused && filteredColors.isNotEmpty()) {
                    {
                        IconButton(
                            onClick = { showDropdown = !showDropdown }
                        ) {
                            Icon(
                                imageVector = if (showDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = if (showDropdown) "Hide suggestions" else "Show suggestions",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else null
            )

            // Dropdown menu
            if (showDropdown && filteredColors.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .zIndex(10f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredColors) { colorName ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectColor(colorName) },
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = colorName,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
