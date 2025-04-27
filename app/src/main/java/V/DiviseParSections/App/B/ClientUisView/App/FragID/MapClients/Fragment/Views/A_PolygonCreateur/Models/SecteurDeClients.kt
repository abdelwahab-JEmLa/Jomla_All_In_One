package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SecteurDeClients(
    @PrimaryKey(autoGenerate = true)
    val vid: Long,
    val nom: String = "Tamaris",
    val ouvert: Boolean = false,
    val polygonEstFerme: Boolean = false,
    val couleur: String = "#ff0000ff",
)

