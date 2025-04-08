package Z_CodePartageEntreApps.Repository._1_5_Vendeur

import android.os.Build
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class _1_5_Vendeur(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section InfosDeBase
    var deviceModelNom: String = Build.MODEL,
    var nom: String = "Manager Vendor",

    // Section StatuesMutable

) {
    companion object {
        // Method moved outside the class since it needs repository access
        fun checkADD(modelDatasSnapList: SnapshotStateList<_1_5_Vendeur>): Pair<_1_5_Vendeur, Long> {
            val existingVendor = modelDatasSnapList.find { it.deviceModelNom == Build.MODEL }

            return if (existingVendor != null) {
                Pair(existingVendor, existingVendor.vid)
            } else {
                val newVid = modelDatasSnapList.maxOfOrNull { it.vid }?.plus(1) ?: 1
                val newVendor = _1_5_Vendeur(
                    vid = newVid,
                )
                Pair(newVendor, newVid)
            }
        }
    }
}
