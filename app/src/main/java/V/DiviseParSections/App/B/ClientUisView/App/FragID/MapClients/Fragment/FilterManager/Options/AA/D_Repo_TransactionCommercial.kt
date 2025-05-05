package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.AA

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.Type
import Z_CodePartageEntreApps.Modules.DatesHandler
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class D_Repo_TransactionCommercial(
    @PrimaryKey(autoGenerate = true)
    var vid: Long = 0L,

    // Section Related Parents Foreign Key IDs
    var parentVID_1_4_PeriodeVent: Long = 0L,
    var clientAcheteurID: Long = 0L,
    var nomClientConcerned: String = "Non Defini",

    // Section InfosDeBase
    var timestamps: Long = DatesHandler().getCurrentTimestamps(),
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),

    var heurFinInString: String = "Non Defini",

    // Section StatuesMutable
    var cActiveDataDeParentList: Boolean = false,

    var cJustPourVoirPanie: Boolean = false,
    var ouvert: Boolean = false,

    var vocaleKeyID: String = "",
    var sonVocaleEstEcoute: Boolean = false,
    var sonEcoutementEstFaitAutimestamps: Long = 0,

    var etateActuellementEst: Type =
        Type.NON_DEFINI,
)
