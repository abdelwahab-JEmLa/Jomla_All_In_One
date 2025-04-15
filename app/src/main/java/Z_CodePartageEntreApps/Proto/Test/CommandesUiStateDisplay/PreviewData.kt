package Z_CodePartageEntreApps.Proto.Test.CommandesUiStateDisplay

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun PreviewData() {
    MaterialTheme {
        CommandesUiStateDisplay(viewModel = CommandesViewModel())
    }
}
