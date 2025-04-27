package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Views.A_PolygonCreateur.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PolygonGeoLimite(
    @PrimaryKey(autoGenerate = true)
    val vid: Long=0,

    val parentSecteurDeClientsId: Long=0,
    val parentSecteurDeClientsKey: String =
        "SecteurDeClients.vid(SecteurDeClients.nom)",
    val aLatitude: Int,
    val aLongitude: Int,

    )
