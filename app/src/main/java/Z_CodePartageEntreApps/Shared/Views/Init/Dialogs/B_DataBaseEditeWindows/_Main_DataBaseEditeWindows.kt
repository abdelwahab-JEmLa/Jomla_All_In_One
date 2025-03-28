package Z_CodePartageEntreApps.Shared.Views.Init.Dialogs.B_DataBaseEditeWindows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val dataBaseEditeWindowsModules = module {
    viewModel { ViewModelA_ProduitModelButtons(get()) }
}

// Load the module when the composable is first used
fun loadComposModule() {
    loadKoinModules(dataBaseEditeWindowsModules)
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun DataBaseEditeWindows(onDissmis: () -> Unit) {
    // Ensure Koin modules are loaded
    loadComposModule()

    KoinAndroidContext {
        Dialog(
            onDismissRequest = {
                onDissmis()
            }
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("DataBaseEditeWindows")
                    // Specific actions for the dialog
                    HorizontalDivider(thickness = 4.dp)

                    A_ProduitModelButtons()
                }
            }
        }
    }
}
