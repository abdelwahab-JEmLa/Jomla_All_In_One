package Views.P1.Ui.Objects

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.Modules.WifiUpdateClientDisplayerStats
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.Repositorys.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchFilterPB(
    showFilter: Boolean,
    filterText: String,
    onFilterTextChange: (String) -> Unit,
    onAddNotInBaseArticle: (ArticlesBasesStatsTable, Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HeadViewModel,
    uiState: UiState,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = showFilter,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        OutlinedTextField(
            value = filterText,
            onValueChange = onFilterTextChange,
            label = { Text("Filter Articles") },
            modifier = modifier
                .fillMaxWidth()
                .padding(3.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    // Handle focus changes - when focus is lost, this will be called
                    if (!focusState.isFocused) {
                        keyboardController?.hide()
                    }
                }
                // Add clickable behavior to show keyboard when field is clicked
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.any { it.pressed }) {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }
                        }
                    }
                },
            trailingIcon = {
                if (filterText.isNotEmpty()) {
                    // Row to contain both clear and upsert buttons
                    Row {
                        // Clear button
                        IconButton(
                            onClick = {
                                onFilterTextChange("")
                                focusRequester.requestFocus() // Keep focus on the field after clearing
                            }
                        ) {
                            Icon(
                                Icons.Default.Clear, // You'll need to import this icon
                                contentDescription = "Clear Text"
                            )
                        }

                        // Existing Add button
                        IconButton(
                            onClick = {
                                scope.launch {
                                    viewModel.addNewEmptyArticle(filterText)?.let { newArticle ->
                                        onAddNotInBaseArticle(newArticle, 0)
                                    }
                                }
                                viewModel.sendOrderToClientDisplayer(
                                    WifiUpdateClientDisplayerStats.SearchWindowsDisplaye.prefix,
                                    filterText
                                )
                                // Clear focus and hide keyboard after action is performed
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Article")
                        }
                    }
                } else {
                    // Only show Add button when there's no text
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.addNewEmptyArticle(filterText)?.let { newArticle ->
                                    onAddNotInBaseArticle(newArticle, 0)
                                }
                            }
                            viewModel.sendOrderToClientDisplayer(
                                WifiUpdateClientDisplayerStats.SearchWindowsDisplaye.prefix,
                                filterText
                            )
                            // Clear focus and hide keyboard after action is performed
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Article")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus() // Clear focus when done is pressed
                }
            )
        )
    }

    LaunchedEffect(showFilter) {
        if (showFilter) {
            focusRequester.requestFocus()
            onFilterTextChange("")
            keyboardController?.show()
        } else {
            // Ensure keyboard is hidden when filter is not shown
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }
}
