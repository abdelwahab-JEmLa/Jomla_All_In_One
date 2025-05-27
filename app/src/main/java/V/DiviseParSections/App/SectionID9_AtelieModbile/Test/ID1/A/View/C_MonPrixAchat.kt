package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A.View

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.A_ProduitInfosTest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
 fun MonPrixAchat(
    produit: A_ProduitInfosTest,
    onPrixUpdate: (Double) -> Unit = {}
) {
    var isEditing by remember { mutableStateOf(false) }
    var tempPrixText by remember { mutableStateOf(produit.monPrixAchat.toString()) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Update tempPrixText when produit changes
    LaunchedEffect(produit.monPrixAchat) {
        if (!isEditing) {
            tempPrixText = produit.monPrixAchat.toString()
        }
    }

    // Focus and show keyboard when editing starts
    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    fun savePrix() {
        val newPrix = tempPrixText.toDoubleOrNull() ?: produit.monPrixAchat
        onPrixUpdate(newPrix)
        isEditing = false
        keyboardController?.hide()
    }

    if (produit.monPrixAchat > 0 || isEditing) {
        if (isEditing) {
            OutlinedTextField(
                value = tempPrixText,
                onValueChange = { tempPrixText = it },
                label = { Text("Prix d'achat") },
                suffix = { Text("DA") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { savePrix() }
                ),
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester)
            )
        } else {
            Column {
                Text(
                    text = "Achat: ${produit.monPrixAchat} DA",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable {
                        isEditing = true
                        tempPrixText = produit.monPrixAchat.toString()
                    }
                )
                val benefice = produit.prixVent - produit.monPrixAchat
                Text(
                    text = "Bénéfice: $benefice DA",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (benefice > 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
