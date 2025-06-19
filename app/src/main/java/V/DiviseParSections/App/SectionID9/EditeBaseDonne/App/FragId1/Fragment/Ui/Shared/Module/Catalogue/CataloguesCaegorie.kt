package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.Ui.Shared.Module.Catalogue

import androidx.compose.ui.graphics.Color
import org.mongodb.kbson.BsonObjectId

data class CataloguesCaegorie(
    var bsonObjectId: String = BsonObjectId().toHexString(),
    val id: Long = 0,
    val nom: String = "",
    val premierCategorieId: Long = 0,
    val position: Int = 0,
    val couleur: Color = Color(0xFF9C27B0) // Default purple color
)
