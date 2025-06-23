package V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID1.Main.Fragment.A.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ACentralCompoRepositoryProtoJuin9
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.D_AchatOperation
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mongodb.kbson.BsonObjectId
import java.io.File

class Sec10Frag1ViewModel(
    val aCentralDatasHandlerProtoJuin9: ACentralCompoRepositoryProtoJuin9,
) : ViewModel() {
    val subClassFunctionality =aCentralDatasHandlerProtoJuin9.zAppComptRepositoryComposable

    fun addNewDAchatCouleurOperationEtOuvreLe(
        article: ArticlesBasesStatsTable,
        indexCouleur: Int,
    ) {
        val data = D_AchatOperation(
            parentProduitBsonObjectId = article.bsonObjectId,
            nomImageFichieOuApellationDuCouleur = trouve_nomImageFichieOuApellationDuCouleurPar(
                article, indexCouleur
            ),
            parentBonVentObjectId = aCentralDatasHandlerProtoJuin9.ouvertTransactionCommercial!!.bsonObjectId
        )
        aCentralDatasHandlerProtoJuin9.dCouleurAchatOperationRepositoryComposable.addOrUpdateData(
            data
        )
        subClassFunctionality.ouvrireCouleurAchatOperationPourCeCompt(
            data.bsonObjectId,
            "${article.nom}_${data.nomImageFichieOuApellationDuCouleur}"
        )
    }

    data class UiState(
        val catalogueFilterId: BsonObjectId? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun trouve_nomImageFichieOuApellationDuCouleurPar(
        article: ArticlesBasesStatsTable,
        indexCouleur: Int
    ): String {
        // Get the color name based on the index
        val couleurName = when (indexCouleur) {
            0 -> article.couleur1
            1 -> article.couleur2
            2 -> article.couleur3
            3 -> article.couleur4
            else -> null
        }

        // Return empty string if color is null or blank
        if (couleurName.isNullOrBlank()) {
            return ""
        }

        // Base path for images (same as in CreateCouleurInfosFromProduct.kt)
        val basePath = "/storage/emulated/0/Abdelwahab_jeMla.com/IMGs/BaseDonne"

        // Create the image file name pattern: {articleId}_{imageIndex}
        val imageIndex = indexCouleur + 1
        val baseFileName = "${article.id}_$imageIndex"

        // Check for image file existence with different extensions
        val supportedExtensions = listOf("jpg", "webp", "jpeg", "png")
        val imageFile = supportedExtensions
            .map { extension -> File("$basePath/$baseFileName.$extension") }
            .firstOrNull { file ->
                file.exists() && file.canRead() && file.length() > 0
            }

        // Return image file name (without extension) if image exists, otherwise return color name
        return if (imageFile != null && imageFile.name != "NonTrouve.webp") {
            baseFileName // Return the base file name without extension
        } else {
            couleurName // Return the color name if no image is available
        }
    }

}
