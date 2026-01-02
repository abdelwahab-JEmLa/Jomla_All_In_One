package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.View.C.Main.Ui.Components

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import com.example.clientjetpack.ViewModel.HeadViewModel
import java.io.File

fun checkImageExists(
    viewModel: HeadViewModel,
    article: ArticlesBasesStatsTable,
    colorIndex: Int,
    reloadTrigger: Int,
    repo03CouleurProduitInfos: Repo03CouleurProduitInfos
): Boolean {
    // Try to get the actual filename from M3CouleurProduitInfos first
    val baseFileName = if (colorIndex == -1) {
        "${article.id}_Unite"
    } else run {
        // Get color info to find actual filename
        val colorInfo = repo03CouleurProduitInfos.datasValue.find {
            it.parentBProduitOldID == article.id &&
                    it.indexCouleurDansAncienProto == colorIndex
        }

        colorInfo?.nomImageFichieSansEtansion ?: "${article.id}_${colorIndex}"
    }

    val baseImagePath = File(viewModel.viewModelImagesPath, baseFileName).absolutePath

    val exists = listOf("webp", "jpg").any { extension ->
        val file = File("$baseImagePath.$extension")
        file.exists() && file.canRead()
    }

    // Log for product 4308
    if (article.id == 4308L) {
        android.util.Log.d("CheckImageExists_4308",
            "Article: ${article.id}, colorIndex: $colorIndex, " +
                    "baseFileName: $baseFileName, exists: $exists")
    }

    return exists
}
