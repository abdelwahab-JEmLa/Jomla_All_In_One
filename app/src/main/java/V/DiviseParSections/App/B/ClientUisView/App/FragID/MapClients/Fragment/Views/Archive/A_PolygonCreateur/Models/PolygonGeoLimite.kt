package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.Archive.A_PolygonCreateur.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PolygonGeoLimite(
    @PrimaryKey(autoGenerate = true)
    val vid: Long=0,

    //Forging Keys
    val parentSecteurDeClientsId: Long=0,
    val parentE1SecteurDeClientsKey: String =
        "E1SecteurDeClients.vid(E1SecteurDeClients.nom)",

    //Infos De Base
    val aLatitude: Int,
    val aLongitude: Int,

    //Etates Mutable

    )
