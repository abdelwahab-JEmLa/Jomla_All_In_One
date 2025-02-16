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

// Preview ViewModel implementation
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

data class SampleArticleData(
    val uiState: UiState,
    val client: B_ClientsDataBase?
)

// Updated sample data provider with null safety
class SampleArticleDataProvider : PreviewParameterProvider<SampleArticleData> {
    override val values = sequenceOf(createSampleData())

    // Provide a default empty value to prevent null pointer exceptions
    override val count: Int = 1

    private fun createSampleData(): SampleArticleData {
        return SampleArticleData(
            uiState = UiState(
                articlesBasesStatTables = listOf(
                    ArticlesBasesStatsTable(
                        idArticle = 4,
                        nomArticleFinale = "Praline®",
                        nomCategorie = "Chocolattes 5 Da",
                        nomArab = "برالين",
                        monPrixVent = 440.0,
                        monPrixAchat = 400.0,
                        monPrixVentUniter = 220.0,
                        monPrixAchatUniter = 3.67,
                        nmbrUnite = 120,
                        couleur1 = "🍫 chocolat 🍫",
                        couleur2 = "🍒 Ceris 🍒",
                        couleur3 = "🥛 lait 🥛",
                        diponibilityState = "",
                        itsNewArrivale = false
                    ),
                    ArticlesBasesStatsTable(
                        idArticle = 8,
                        nomArticleFinale = "Chiwawa 10 دج®",
                        nomCategorie = "زريعة",
                        nomArab = "شيواوا 10 دج",
                        monPrixVent = 705.0,
                        monPrixAchat = 680.0,
                        monPrixVentUniter = 352.5,
                        monPrixAchatUniter = 8.81,
                        nmbrUnite = 80,
                        couleur1 = "🟨 صفراء 🟨",
                        couleur2 = "🉐 بيضاء 🉐",
                        couleur3 = "[Barbrqu]©",
                        diponibilityState = "",
                        itsNewArrivale = false
                    )
                ),
                categories = listOf(
                    CategoriesTabelle().apply {
                        nomCategorieInCategoriesTabele = "Chocolattes 5 Da"
                        displayedHeader = true
                    },
                    CategoriesTabelle().apply {
                        nomCategorieInCategoriesTabele = "زريعة"
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
        viewModel = rememberPreviewViewModel(),
        reloadTrigger = 0,
        onClickToOpenWindos = { _, _ -> },
        currentClient = sampleData.client
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

// Preview ViewModel implementation
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

// Sample data provider using JSON structure
class SampleArticleDataProvider : PreviewParameterProvider<SampleArticleData> {
    override val values = sequenceOf(
        SampleArticleData(
            uiState = UiState(
                articlesBasesStatTables = listOf(
                    ArticlesBasesStatsTable(
                        idArticle = 4,
                        nomArticleFinale = "Praline®",
                        nomCategorie = "Chocolattes 5 Da",
                        nomArab = "برالين",
                        monPrixVent = 440.0,
                        monPrixAchat = 400.0,
                        monPrixVentUniter = 220.0,
                        monPrixAchatUniter = 3.67,
                        nmbrUnite = 120,
                        couleur1 = "🍫 chocolat 🍫",
                        couleur2 = "🍒 Ceris 🍒",
                        couleur3 = "🥛 lait 🥛",
                        diponibilityState = "",
                        itsNewArrivale = false
                    ),
                    ArticlesBasesStatsTable(
                        idArticle = 8,
                        nomArticleFinale = "Chiwawa 10 دج®",
                        nomCategorie = "زريعة",
                        nomArab = "شيواوا 10 دج",
                        monPrixVent = 705.0,
                        monPrixAchat = 680.0,
                        monPrixVentUniter = 352.5,
                        monPrixAchatUniter = 8.81,
                        nmbrUnite = 80,
                        couleur1 = "🟨 صفراء 🟨",
                        couleur2 = "🉐 بيضاء 🉐",
                        couleur3 = "[Barbrqu]©",
                        diponibilityState = "",
                        itsNewArrivale = false
                    ),
                    ArticlesBasesStatsTable(
                        idArticle = 10,
                        nomArticleFinale = "Kool Gateau®",
                        nomCategorie = "gateaux serir",
                        nomArab = "كول غاتو",
                        monPrixVent = 0.0,
                        monPrixAchat = 490.0,
                        monPrixVentUniter = 0.0,
                        monPrixAchatUniter = 0.0,
                        nmbrUnite = 24,
                        couleur1 = "🎁 standard 🎁",
                        couleur2 = "",
                        couleur3 = "",
                        diponibilityState = "",
                        itsNewArrivale = false
                    ),
                    ArticlesBasesStatsTable(
                        idArticle = 17,
                        nomArticleFinale = "Dragon®",
                        nomCategorie = "كوجاك 10 دج",
                        nomArab = "دراغون",
                        monPrixVent = 570.0,
                        monPrixAchat = 460.0,
                        monPrixVentUniter = 285.0,
                        monPrixAchatUniter = 6.71,
                        nmbrUnite = 85,
                        couleur1 = "🎨 Multi Couleur 🎨",
                        couleur2 = "",
                        couleur3 = "",
                        diponibilityState = "",
                        itsNewArrivale = false
                    )
                ),
                categories = listOf(
                    CategoriesTabelle().apply {
                        nomCategorieInCategoriesTabele = "Chocolattes 5 Da"
                        displayedHeader = true
                    },
                    CategoriesTabelle().apply {
                        nomCategorieInCategoriesTabele = "زريعة"
                        displayedHeader = true
                    },
                    CategoriesTabelle().apply {
                        nomCategorieInCategoriesTabele = "gateaux serir"
                        displayedHeader = true
                    },
                    CategoriesTabelle().apply {
                        nomCategorieInCategoriesTabele = "كوجاك 10 دج"
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
        viewModel = rememberPreviewViewModel(),
        reloadTrigger = 0,
        onClickToOpenWindos = { _, _ -> },
        currentClient = sampleData.client
    )
}
