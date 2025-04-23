package V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Models

import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository.Companion.getCurrentDateString
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.DataBase._01_VentsHistoriques.Repository._01_VentsHistoriquesDataBase_Repository.Companion.getCurrentTimeString
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class _14A_HistoriuesDeCetteJour : RealmObject {
    @PrimaryKey
    var bsonObjectId: ObjectId = BsonObjectId()

    var dateCreationStr: String = getCurrentDateString()
    var tempCreationStr: String = getCurrentTimeString()

    var etateName: String = Etate.NON_DEFINI.name

    var description: String = ""

    @Ignore
    var etate: Etate
        get() = try {
            Etate.valueOf(etateName)
        } catch (e: Exception) {
            Etate.NON_DEFINI
        }
        set(value) {
            etateName = value.name
        }

    enum class Etate(val color: Int, val nomArabe: String) {
        NON_DEFINI(android.R.color.holo_orange_light, "غير محدد"),
        AVEC_MARCHANDISE(android.R.color.holo_blue_light, "عندو سلعة"),
        A_EVITE(android.R.color.black, "يتجنب"),
        COMMANDE_LENCE(android.R.color.holo_green_light, "نشط / متصل"),
        ACHETEUR_NON_DISPO(android.R.color.darker_gray, "الشاري غائب"),
        FERME(android.R.color.darker_gray, "مغلق")
    }

    var fireBaseKeyID: String = "${etateName}->(${dateCreationStr}${tempCreationStr})"
}
