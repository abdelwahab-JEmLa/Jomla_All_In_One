package Views.P1.Ui.ArticlesGrid

import Z_MasterOfApps.Kotlin.Model.B_ClientsDataBase
import Z_MasterOfApps.Z.Android.Base.App.App3_Client_JetPack.Models.ArticlesBasesStatsTable
import Z_MasterOfApps.Z_AppsFather.Kotlin._1.Model.App.CategoriesTabelle
import android.content.Context
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.room.Room
import com.example.clientjetpack.Models.ProductDisplayController
import com.example.clientjetpack.Models.UiState
import com.example.clientjetpack.Modules.AppDatabase
import com.example.clientjetpack.ViewModel.HeadViewModel

// Mock database for previews
class PreviewDatabase {
    companion object {
        fun createTestDatabase(context: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
            ).allowMainThreadQueries().build()
        }
    }
}

// Preview ViewModel implementation with mock dependencies
class PreviewHeadViewModel(
    context: Context,
    database: AppDatabase
) : HeadViewModel(
    context = context,
    database = database
)

@Composable
fun rememberPreviewViewModel(): HeadViewModel {
    val context = LocalContext.current
    return remember {
        PreviewHeadViewModel(
            context = context,
            database = PreviewDatabase.createTestDatabase(context)
        )
    }
}

@Preview(
    name = "Article Grid - Empty",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun ArticleGridPreviewEmpty() {
    val emptyUiState = UiState(
        articlesBasesStatTables = emptyList(),
        categories = emptyList(),
        productDisplayController = ProductDisplayController(),
        isLoading = false
    )

    ArticleGridWithScrollbar(
        uiState = emptyUiState,
        gridColumns = 2,
        filterText = "",
        showFilter = false,
        gridState = rememberLazyStaggeredGridState(),
        viewModel = rememberPreviewViewModel(),
        reloadTrigger = 0,
        onClickToOpenWindos = { _, _ -> },
        currentClient = null
    )
}

@Preview(
    name = "Article Grid - Loading",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun ArticleGridPreviewLoading() {
    val loadingUiState = UiState(
        articlesBasesStatTables = emptyList(),
        categories = emptyList(),
        productDisplayController = ProductDisplayController(),
        isLoading = true,
        loadingProgress = 0.5f
    )

    ArticleGridWithScrollbar(
        uiState = loadingUiState,
        gridColumns = 2,
        filterText = "",
        showFilter = false,
        gridState = rememberLazyStaggeredGridState(),
        viewModel = rememberPreviewViewModel(),
        reloadTrigger = 0,
        onClickToOpenWindos = { _, _ -> },
        currentClient = null
    )
}




@Preview(
    name = "Article Grid - With Data",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun ArticleGridPreviewWithData(
    @PreviewParameter(SampleArticleDataProvider::class) sampleData: SampleArticleData
) {
    ArticleGridWithScrollbar(
        uiState = sampleData.uiState,
        gridColumns = 2,
        filterText = "",
        showFilter = false,
        gridState = rememberLazyStaggeredGridState(),
        viewModel = rememberPreviewViewModel(),  // Fixed: Using rememberPreviewViewModel
        reloadTrigger = 0,
        onClickToOpenWindos = { _, _ -> },
        currentClient = sampleData.client
    )
}

// Sample data provider for previews
class SampleArticleDataProvider : PreviewParameterProvider<SampleArticleData> {    //-->
//TODO(1): change  depuit le json
    override val values = sequenceOf(
        SampleArticleData(
            uiState = UiState(
                articlesBasesStatTables = listOf(
                    ArticlesBasesStatsTable(
                        idArticle = 1,
                        nomArticleFinale = "Sample Article 1",
                        nomCategorie = "Category 1",
                        monPrixVent = 99.99,
                        monPrixAchat = 50.0
                    ),
                    ArticlesBasesStatsTable(
                        idArticle = 2,
                        nomArticleFinale = "Sample Article 2",
                        nomCategorie = "Category 1",
                        monPrixVent = 149.99,
                        monPrixAchat = 75.0
                    )
                ),
                categories = listOf(
                    CategoriesTabelle().apply {
                        nomCategorieInCategoriesTabele = "Category 1"
                        displayedHeader = true
                    }
                ),
                productDisplayController = ProductDisplayController(),
                isLoading = false
            ),
            client = B_ClientsDataBase(
                id = 1,
                nom = "Sample Client"
            )
        )
    )
}

data class SampleArticleData(
    val uiState: UiState,
    val client: B_ClientsDataBase
)


