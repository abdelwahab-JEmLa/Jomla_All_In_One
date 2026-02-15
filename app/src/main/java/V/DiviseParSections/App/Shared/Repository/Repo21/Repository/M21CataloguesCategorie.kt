package V.DiviseParSections.App.Shared.Repository.Repo21.Repository

import androidx.compose.ui.graphics.Color
import org.mongodb.kbson.BsonObjectId

data class M21CataloguesCategorie(
    var keyID: String = BsonObjectId().toHexString(),
    val id: Long = 0,
    val nom: String = "",
    val premierCategorieId: Long = 0,
    val position: Int = 0,
    val couleur: Color = Color(0xFF9C27B0)
)
