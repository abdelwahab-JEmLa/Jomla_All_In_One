package Z_CodePartageEntreApps.Model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.database.IgnoreExtraProperties

class P_BonsCommandGrossist(var vid: Long = 0) {
    var infosDeBase by mutableStateOf(InfosDeBase())
    @IgnoreExtraProperties
    class InfosDeBase {
        var GrossistChoisiID by mutableLongStateOf(0L)
        var dateInString by mutableStateOf("2025_01_01")
    }

    var etatesMutable by mutableStateOf(EtatesMutable())
    @IgnoreExtraProperties
    class EtatesMutable

    var produitCommendeIDs by mutableStateOf<List<Long>>(emptyList())
}
