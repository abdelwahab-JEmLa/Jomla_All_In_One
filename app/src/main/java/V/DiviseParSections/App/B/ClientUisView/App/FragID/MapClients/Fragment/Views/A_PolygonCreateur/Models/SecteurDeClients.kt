package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SecteurDeClients(
    @PrimaryKey(autoGenerate = true)
    val vid: Long,

    //Infos De Base
    val nom: String = "Tamaris",
    val couleur: String = "0xff0000ff",

    //Etates Mutable
    val ouvert: Boolean = false,
    val ciblePourCettePeriodDeVent: Boolean = false,
    val polygonEstFerme: Boolean = false,
)

