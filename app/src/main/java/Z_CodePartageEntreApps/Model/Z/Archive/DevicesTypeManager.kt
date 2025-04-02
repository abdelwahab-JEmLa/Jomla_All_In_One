package Z_CodePartageEntreApps.Model.Z.Archive

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DevicesTypeManager(
    @PrimaryKey var id: Long = 0,
    var name: String = "",
    var isHost: Boolean = false,
    )
