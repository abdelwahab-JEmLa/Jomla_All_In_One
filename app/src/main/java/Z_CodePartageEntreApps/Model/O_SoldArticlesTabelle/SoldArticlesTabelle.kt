package Z_CodePartageEntreApps.Model.O_SoldArticlesTabelle

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SoldArticlesTabelle(
    @PrimaryKey(autoGenerate = true) val vid: Long = 0,
    val idArticle: Long = 0,
    val nameArticle: String = "",
    val clientSoldToItId: Long = 0,
    val nomClient: String = "",
    val date: String = "2025-01-01",
    val dateInString: String = "2025-01-01",
    val color1IdPicked: Long = 0,
    val color1SoldQuantity: Int = 0,
    val color2IdPicked: Long = 0,
    val color2SoldQuantity: Int = 0,
    val color3IdPicked: Long = 0,
    val color3SoldQuantity: Int = 0,
    val color4IdPicked: Long = 0,
    val color4SoldQuantity: Int = 0,
    val confimed: Boolean = false,
) {
    val colorsAcheterIdsToQuantity: Map<Long, Int>
        get() = mapOf(
            color1IdPicked to color1SoldQuantity,
            color2IdPicked to color2SoldQuantity,
            color3IdPicked to color3SoldQuantity,
            color4IdPicked to color4SoldQuantity
        ).filter { it.key != 0L && it.value > 0 }

    constructor() : this(0)
}
