package V.DiviseParSections.App.SectionID6.Messager.App.FragID1.Messager.Fragment.Views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun PreviewMessageurMainScreenP() {
    // Fixed: Now it's a Box that displays the dialog at start
    Box(modifier = Modifier.fillMaxSize()) {
        A_MessageurMainScreen(
            onDismiss = {
                // Handle dialog dismissal in preview
                // In actual implementation, this would handle navigation or state changes
            }
        )
    }
}
