package Z_CodePartageEntreApps.Model.Q_CouleurEtGoutDataBase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Q_CouleurEtGoutDataBase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Section InfosDeBase
    var nom: String = "Non Defini",
    var imogi: String = "🎨",

    // Section Etates Mutable
    var sonImageNeExistPas: Boolean = false,
    var position_Du_Couleur_Au_Produit: Long = 0,
)
