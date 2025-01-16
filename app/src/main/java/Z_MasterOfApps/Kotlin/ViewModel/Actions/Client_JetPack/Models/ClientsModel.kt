package Z_MasterOfApps.Kotlin.ViewModel.Actions.Client_JetPack.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ClientsModel(
    @PrimaryKey(autoGenerate = true) val vidSu: Long = 0,
    var idClientsSu: Long = 0,
    var nomClientsSu: String = "",
    var bonDuClientsSu: String = "",
    val couleurSu: String = "#FFFFFF",
    var currentCreditBalance: Double = 0.0,
    val numberTelephoney: String = "",

    ) {
    constructor() : this(0)
}
