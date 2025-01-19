package Views.P1.Ui.Objects

import Z_MasterOfApps.Z.Android.Actions._2.Client_JetPack.Models.ArticlesBasesStatsTable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.ViewModel.HeadViewModel
import com.example.clientjetpack.ViewModel.WifiUpdateClientDisplayerStats
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
    onClickDonne: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
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
                .focusRequester(focusRequester),

            trailingIcon = {
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

                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Search")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onClickDonne()
                }
            )
        )
    }

    LaunchedEffect(showFilter) {
        if (showFilter) {
            focusRequester.requestFocus()
            onFilterTextChange("")
            keyboardController?.show()
        }
    }
}
