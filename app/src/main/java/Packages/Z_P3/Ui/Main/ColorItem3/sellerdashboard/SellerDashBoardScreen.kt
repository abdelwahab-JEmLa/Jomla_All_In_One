package Packages.Z_P3.Ui.Main.ColorItem3.sellerdashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SellerDashBoardScreen(
    state: SellerDashBoardState,
    actions: SellerDashBoardActions
) {
    // TODO UI Rendering
}

@Composable
@Preview(name = "SellerDashBoard")
private fun SellerDashBoardScreenPreview() {
    SellerDashBoardScreen(
        state = SellerDashBoardState(),
        actions = SellerDashBoardActions()
    )
}

