package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._01

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

class _01_PeriodesVentNoSQl {
    private var keyID by mutableStateOf("{dateDebutDeCettePeriode}->(tempDebutDeCettePeriode)")
    private var dateDebutDeCettePeriode by mutableStateOf("yyyy_MM_dd")
    private var tempDebutDeCettePeriode by mutableStateOf("HH:mm")

    @get:Exclude
    var vendeursActiveDonsCettePeriode: Map<String, VendeursActiveDonsCettePeriode> = mutableMapOf()

    private fun genereModelKeyID() {
        keyID = "${dateDebutDeCettePeriode}->($tempDebutDeCettePeriode)"
    }
}

@Entity
data class _01_PeriodesVentRoomSQl(
    @PrimaryKey
    var keyID: String = "0->(Vendeur nom)",
    var parentkeyID: String = "2025_01_01->(00:00)",
    var startIndex: Int = 0,
    var nom: String = "",
    var quantity: Int = 0,
)

// Class representing a vendor active during a sales period
class VendeursActiveDonsCettePeriode {
    private var keyID by mutableStateOf("{startIndex}->({nom})")
    private var startIndex by mutableIntStateOf(0)
    private var nom by mutableStateOf("")

    @get:Exclude
    var produitsVenduParLui: Map<String, ProduitsVenduParLui> = mutableMapOf()

    fun genereModelKeyID() {
        keyID = "${startIndex}->($nom)"
    }
}
@Entity
data class _02_VendeursActiveDonsCettePeriodeRoomSQlModel(
    @PrimaryKey
    var keyID: String = "0->(Vendeur nom)",
    var parentkeyID: String = "2025_01_01->(00:00)",
    var startIndex: Int = 0,
    var nom: String = "",
    var quantity: Int = 0,
)

// Class representing a vendor active during a sales period
class ProduitsVenduParLui {
    private var keyID by mutableStateOf("{startIndex}->({nom})")
    private var startIndex by mutableIntStateOf(0)
    private var nom by mutableStateOf("")
    var quantity by mutableStateOf(0)

    fun genereModelKeyID() {
        keyID = "${startIndex}->($nom)"
    }

}

@Entity
data class _03_ProduitsVenduParLuiRoomSQlModel(
    @PrimaryKey
    var keyID: String = "0->(Produit 0)",
    var parentkeyID: String = "0->(Vendeur 0)",
    var id: Int = 0,
    var nom: String = "",
    var quantity: Int = 0,
)
