package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL._1_3_TransactionCommercial
import androidx.room.Entity

fun testData_1_3_TransactionCommercial(): _1_3_TransactionCommercial {
    //<--
    //TODO(1): cree test data dpuit json //<--
    //TODO(1):
    @Entity
    public final data class _1_3_TransactionCommercial(
        val vid: Long = 0L,
        val parentVID_1_4_PeriodeVent: Long = 0L,
        val clientAcheteurID: Long = 0L,
        val nomClientConcerned: String = "Non Defini",
        val timestamps: Long = DatesHandler().getCurrentTimestamps(),
        val heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
        val heurFinInString: String = "Non Defini",
        val cLeDataOuvertDuParentList: Boolean? = null,
        val cActive: Boolean = false,
        val cJustPourVoirPanie: Boolean = false,
        val ouvert: Boolean = false,
        val vocaleKeyID: String = "",
        val sonVocaleEstEcoute: Boolean = false,
        val sonEcoutementEstFaitAutimestamps: Long = 0,
        val etateActuellementEst: _1_3_TransactionCommercial.EtateActuellementEst = EtateActuellementEst.NON_DEFINI
    )

    V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.FilterManager.Options.SQL
    _1_3_TransactionCommercial.kt

}
