package Packages.Z_P3.Ui.Main.ColorItem3.sellerdashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember


@Composable
fun SellerDashBoardRoute(
    coordinator: SellerDashBoardCoordinator = rememberSellerDashBoardCoordinator()
) {
    // State observing and declarations
    val uiState by coordinator.screenStateFlow.collectAsState(SellerDashBoardState())

    // UI Actions
    val actions = rememberSellerDashBoardActions(coordinator)

    // UI Rendering
    SellerDashBoardScreen(uiState, actions)
}


@Composable
fun rememberSellerDashBoardActions(coordinator: SellerDashBoardCoordinator): SellerDashBoardActions {
    return remember(coordinator) {
        SellerDashBoardActions(

        )
    }
}
