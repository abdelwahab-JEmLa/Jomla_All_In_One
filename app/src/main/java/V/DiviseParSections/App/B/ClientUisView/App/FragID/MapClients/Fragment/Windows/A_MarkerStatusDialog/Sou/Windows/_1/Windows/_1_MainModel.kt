package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Entity
import com.google.firebase.firestore.Exclude

class PeriodesVent {
    private var keyID by mutableStateOf("{dateDebutDeCettePeriode}->(tempDebutDeCettePeriode)")
    private var dateDebutDeCettePeriode by mutableStateOf("yyyy_MM_dd")
    private var tempDebutDeCettePeriode by mutableStateOf("HH:mm")

    @get:Exclude
    var vendeursActiveDonsCettePeriode: Map<String, VendeursActiveDonsCettePeriode>    //->
    //TODO(FIXME):Fix erreur Property must be initialized or be abstract

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
    var produitsVenduParLui: Map<String, ProduitsVenduParLui>    //->
    //TODO(FIXME):Fix erreur Property must be initialized or be 

    fun genereModelKeyID() {
        keyID = "${startIndex}->($nom)"
    }

    @Entity
    data class RoomSQlModel(
        var keyID: String,
        var parentkeyID: String ,
        var startIndex: Int,
        var nom: String,
        var quantity: Int,
    )  {
        fun testData(): this {
              //<--
              //TODO(1): cree un test data
        }


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

    @Entity
    data class RoomSQlModel(
        var keyID: String,
        var parentkeyID: String ,
        var startIndex: Int,
        var nom: String,
        var quantity: Int,
        )
    { fun testData(): this {
        //<--
        //TODO(1): cree un test data
    }}
}
