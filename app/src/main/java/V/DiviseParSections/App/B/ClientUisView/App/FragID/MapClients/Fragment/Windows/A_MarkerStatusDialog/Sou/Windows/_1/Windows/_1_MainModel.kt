package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

class PeriodesVent {
    private var keyID by mutableStateOf("{dateDebutDeCettePeriode}->(tempDebutDeCettePeriode)")
    private var dateDebutDeCettePeriode by mutableStateOf("yyyy_MM_dd")
    private var tempDebutDeCettePeriode by mutableStateOf("HH:mm")

    @get:Exclude
    var vendeursActiveDonsCettePeriode: Map<String, VendeursActiveDonsCettePeriode> = mutableMapOf()

    private fun genereModelKeyID() {
        keyID = "${dateDebutDeCettePeriode}->($tempDebutDeCettePeriode)"
    }
}

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
data class VendeursActiveDonsCettePeriodeRoomSQlModel(
    @PrimaryKey
    var keyID: String = "",
    var parentkeyID: String = "",
    var startIndex: Int = 0,
    var nom: String = "",
    var quantity: Int = 0,
) {
    fun testData(): VendeursActiveDonsCettePeriodeRoomSQlModel {
        return VendeursActiveDonsCettePeriodeRoomSQlModel(
            keyID = "1->(Vendeur Test)",
            parentkeyID = "2023_04_17->(14:30)",
            startIndex = 1,
            nom = "Vendeur Test",
            quantity = 10
        )
    }
}
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
data class ProduitsVenduParLuiRoomSQlModel(
    @PrimaryKey
    var keyID: String = "",
    var parentkeyID: String = "",
    var startIndex: Int = 0,
    var nom: String = "",
    var quantity: Int = 0,
) {
    fun testData(): ProduitsVenduParLuiRoomSQlModel {
        return ProduitsVenduParLuiRoomSQlModel(
            keyID = "1->(Produit Test)",
            parentkeyID = "1->(Vendeur Test)",
            startIndex = 1,
            nom = "Produit Test",
            quantity = 5
        )
    }
}
