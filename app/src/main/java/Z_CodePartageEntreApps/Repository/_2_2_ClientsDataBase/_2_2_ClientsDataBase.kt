package Z_CodePartageEntreApps.Repository._2_2_ClientsDataBase

import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _2_2_ClientsDataBase(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section InfosDeBase
    var deviceModelNom: String = Build.MODEL,
    var nom: String = "Manager Vendor",

    // Section StatuesMutable

)
