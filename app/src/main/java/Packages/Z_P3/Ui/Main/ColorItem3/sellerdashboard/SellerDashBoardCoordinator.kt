package Packages.Z_P3.Ui.Main.ColorItem3.sellerdashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.androidx.compose.koinViewModel

/**
 * Screen's coordinator which is responsible for handling actions from the UI layer
 * and one-shot actions based on the new UI state
 */
class SellerDashBoardCoordinator(
    val viewModel: SellerDashBoardViewModel
) {
    val screenStateFlow = viewModel.stateFlow


}

@Composable
fun rememberSellerDashBoardCoordinator(
    viewModel: SellerDashBoardViewModel = koinViewModel()
): SellerDashBoardCoordinator {
    return remember(viewModel) {
        SellerDashBoardCoordinator(
            viewModel = viewModel
        )
    }
}
