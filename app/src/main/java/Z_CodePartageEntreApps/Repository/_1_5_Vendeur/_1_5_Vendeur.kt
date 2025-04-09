package Z_CodePartageEntreApps.Repository._1_5_Vendeur

import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class _1_5_Vendeur(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section InfosDeBase
    var deviceModelNom: String = Build.MODEL,
    var deviceModelId: String = getAppSpecificDeviceId(),
    var nom: String = "Manager Vendor",

    // Section StatuesMutable

) {
    companion object {
        private var cachedDeviceId: String? = null

        /**
         * Generates an app-specific device identifier that doesn't use hardware IDs
         */
        fun getAppSpecificDeviceId(): String {
            if (cachedDeviceId == null) {
                cachedDeviceId = UUID.randomUUID().toString()
            }
            return cachedDeviceId!!
        }
    }
}
