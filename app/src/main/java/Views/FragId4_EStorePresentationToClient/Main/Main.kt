package Views.FragId4_EStorePresentationToClient.Main

import Views.FragId4_EStorePresentationToClient.Ui.A_CouleurSectionAfficheur_F7
import Views.FragId4_EStorePresentationToClient.Ui.ProductNameSection7
import Z_MasterOfApps.Kotlin.ViewModel.ViewModelInitApp
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ColorsArticlesTabelle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import com.example.clientjetpack.Models.ProductDisplayController

@Composable
fun ProductDisplayDialog(
    displayController: ProductDisplayController,
    articleStatsDataBase: ArticlesBasesStatsTable,
    colorsArticlesList: List<ColorsArticlesTabelle>,
    reloadTrigger: Int,
    modifier: Modifier = Modifier, viewModelInitApp: ViewModelInitApp
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            securePolicy = SecureFlagPolicy.Inherit,
        )
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 2.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(4.dp)
                ) {
                    // Product information
                    ProductNameSection7(articleStatsDataBase)

                    // Color selection header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 3.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Text(
                            text = "اختر اللون",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    // Color selection cards with proper scrolling
                    A_CouleurSectionAfficheur_F7(
                        displayController = displayController,
                        articlesBasesStatsTable = articleStatsDataBase,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        relodeTigger = reloadTrigger,
                        colorsArticlesList = colorsArticlesList, viewModelInitApp = viewModelInitApp
                    )
                }
            }
        }
    }
}

@Composable
fun FragmentDisplayeInfoProductToClient7(
    displayController: ProductDisplayController,
    articleStatsDataBase: ArticlesBasesStatsTable,
    colorsArticlesList: List<ColorsArticlesTabelle>,
    reloadTrigger: Int,
    modifier: Modifier = Modifier, viewModelInitApp: ViewModelInitApp
) {
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showDialog = true
    }

    if (showDialog) {
        ProductDisplayDialog(
            displayController = displayController,
            articleStatsDataBase = articleStatsDataBase,
            colorsArticlesList = colorsArticlesList,
            reloadTrigger = reloadTrigger,
            modifier = modifier, viewModelInitApp = viewModelInitApp
        )
    }
}
