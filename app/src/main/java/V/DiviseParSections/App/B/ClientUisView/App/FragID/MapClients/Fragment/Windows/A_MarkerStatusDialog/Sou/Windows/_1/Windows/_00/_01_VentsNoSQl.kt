package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Sou.Windows._1.Windows._00

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude

class _01_VentsNoSQl {
    var keyID by mutableStateOf("{dateDebutDeCettePeriode}->(tempDebutDeCettePeriode)")
    var dateDebutDeCettePeriode by mutableStateOf("yyyy_MM_dd")
    var tempDebutDeCettePeriode by mutableStateOf("HH:mm")

    @get:Exclude
    var vendeursActiveDonsCettePeriode: Map<String, VendeursActiveDonsCettePeriode> = mutableMapOf()


    @get:Exclude
    var a01PeriodesVent: List<_01_VentsNoSQl> = listOf()

    fun genereModelKeyID() {
        keyID = "${dateDebutDeCettePeriode}->($tempDebutDeCettePeriode)"
    }

    // Find periods that are in the same day
    fun getPeriodesFromSameDay(): List<_01_VentsNoSQl> {
        return a01PeriodesVent.filter {
            it.dateDebutDeCettePeriode == this.dateDebutDeCettePeriode && it.keyID != this.keyID
        }
    }

    // Find periods that have specific vendeur
    fun getPeriodesWithVendeur(vendeurNom: String): List<_01_VentsNoSQl> {
        return a01PeriodesVent.filter { periode ->
            periode.vendeursActiveDonsCettePeriode.values.any { vendeur ->
                vendeur.nom == vendeurNom
            }
        }
    }

    // Get total quantity of all products sold in this period
    fun getTotalQuantity(): Int {
        var total = 0
        vendeursActiveDonsCettePeriode.values.forEach { vendeur ->
            vendeur.produitsVenduParLui.values.forEach { produit ->
                total += produit.quantity
            }
        }
        return total
    }
}

@Entity
data class _01_PeriodesVentRoomSQl(
    @PrimaryKey
    var keyID: String = "0->(Periode nom)",
    var parentkeyID: String = "",  // Periods have no parent
    var startIndex: Int = 0,
    var nom: String = "",
    var quantity: Int = 0,
)

class VendeursActiveDonsCettePeriode {
    var keyID by mutableStateOf("{startIndex}->({nom})")
    var startIndex by mutableIntStateOf(0)
    var nom by mutableStateOf("")

    @get:Exclude
    var produitsVenduParLui: Map<String, ProduitsVenduParLui> = mutableMapOf()

    fun genereModelKeyID() {
        keyID = "${startIndex}->($nom)"
    }

    // Get total quantity sold by this vendeur
    fun getTotalQuantity(): Int {
        return produitsVenduParLui.values.sumOf { it.quantity }
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

class ProduitsVenduParLui {
    var keyID by mutableStateOf("{startIndex}->({nom})")
    var startIndex by mutableIntStateOf(0)
    var nom by mutableStateOf("")
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
