package Views.P1.Ui.ArticlesGrid

import Views.P1.Ui.ArticlesGrid.A.List.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import com.example.clientjetpack.ViewModel.HeadViewModel
import java.io.File

// Utility functions
fun checkImageExists(
    viewModel: HeadViewModel,
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
    reloadTrigger: Int
): Boolean {
    val baseImagePath = File(
        viewModel.viewModelImagesPath,
        "${article.id}_${if (colorIndex == -1) "Unite" else (colorIndex + 1)}"
    ).absolutePath

    return listOf("jpg", "webp").any { extension ->
        val file = File("$baseImagePath.$extension")
        file.exists() && file.canRead()
    }
}
