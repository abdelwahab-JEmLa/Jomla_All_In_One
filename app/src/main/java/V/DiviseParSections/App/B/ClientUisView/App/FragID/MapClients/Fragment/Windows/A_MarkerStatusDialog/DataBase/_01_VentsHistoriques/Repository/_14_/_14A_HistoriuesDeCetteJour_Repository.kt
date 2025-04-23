package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Repository._14_

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models._14A_HistoriuesDeCetteJour
import com.google.firebase.database.DataSnapshot

interface _14A_HistoriuesDeCetteJour_Repository {

    fun getHistoriquesByDate(dateStr: String): List<_14A_HistoriuesDeCetteJour>

    fun getHistoriquesByEtate(etatName: String): List<_14A_HistoriuesDeCetteJour>

    fun mapDatas(datas: List<_14A_HistoriuesDeCetteJour>): Map<String, Any>
    fun parseDataFromSnapshot(snapshot: DataSnapshot): _14A_HistoriuesDeCetteJour?
    fun deepCopy(source: _14A_HistoriuesDeCetteJour): _14A_HistoriuesDeCetteJour
    fun testData(): List<_14A_HistoriuesDeCetteJour>

    companion object {
        fun mapDatas(datas: List<_14A_HistoriuesDeCetteJour>): Map<String, Any> {
            return _14A_HistoriuesDeCetteJour_RepositoryImpl().mapDatas(datas)
        }
        fun testData(): List<_14A_HistoriuesDeCetteJour> {
            return _14A_HistoriuesDeCetteJour_RepositoryImpl().testData()
        }

        fun parseDataFromSnapshot(snapshot: DataSnapshot): _14A_HistoriuesDeCetteJour? {
            return _14A_HistoriuesDeCetteJour_RepositoryImpl().parseDataFromSnapshot(snapshot)
        }

        fun deepCopy(source: _14A_HistoriuesDeCetteJour): _14A_HistoriuesDeCetteJour {
            return _14A_HistoriuesDeCetteJour_RepositoryImpl().deepCopy(source)
        }
    }

}
