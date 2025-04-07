package Z_CodePartageEntreApps.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_5_Vendeur(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section InfosDeBase
    var deviceModelNom: String = "",
    var nom: String = "Manager Vendor",

    // Section StatuesMutable

)
