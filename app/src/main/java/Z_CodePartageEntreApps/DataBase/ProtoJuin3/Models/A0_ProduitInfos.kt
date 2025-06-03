package Z_CodePartageEntreApps.DataBase.ProtoJuin3.Models

import A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Entity
data class A_ProduitInfosProtoJuin3(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    // Section InfosDeBase
    var nom: String = "",

    // Section Etates Mutable
    var dernierFireBaseUpdateTimestamps: Long = 0,

    // Section sonCategory
    // Section InfosCoutes
    var prixVent: Double = 0.0,
    //edited
    var prixAchat: Double = 0.0,
    var nombreUniteInt: Int = 0,
    var clientPrixVentUnite: Double = 0.0,
    //ajoute
    var actualiseSonImage: Int = 0,
    var actualiseSonImageTest2: Int = 0,
    var idParentCategorie: Long? = null,

    // Add availability states with proper initialization
    var disponibilityEtates: DisponibilityEtates = DisponibilityEtates.DISPO,

    // Section keyFireBase
    var keyFireBase: String = "",

    var nomCategorie2: String? = null,

    var classementCate: Double = 0.0,
    var nomArab: String = "",
    var autreNomDarticle: String? = null,
    var nmbrCat: Int = 0,
    var couleur1: String? = null,
    var idcolor1: Long = 0,
    var couleur2: String? = null,
    var idcolor2: Long = 0,
    var couleur3: String? = null,
    var idcolor3: Long = 0,
    var couleur4: String? = null,
    var idcolor4: Long = 0,
    var nmbrCaron: Int = 0,
    var affichageUniteState: Boolean = false,
    var commmentSeVent: String? = null,
    var afficheBoitSiUniter: String? = null,
    var minQuan: Int = 0,
    var monBenfice: Double = 0.0,
    var neaon2: String = "",
    var catalogeParentID: Long = 0,
    var funChangeImagsDimention: Boolean = false, //imgStatIsSmall
    var neaon1: Double = 0.0,
    var lastUpdateState: String = "",
    var cartonState: String = "",
    var dateCreationCategorie: String = "",
    var prixDeVentTotaleChezClient: Double = 0.0,
    var benficeTotaleEntreMoiEtClien: Double = 0.0,
    var benificeTotaleEn2: Double = 0.0,
    var monPrixAchatUniter: Double = 0.0,
    var monPrixVentUniter: Double = 0.0,
    var benificeClient: Double = 0.0,
    var monBeneficeUniter: Double = 0.0,

    //Stats
    var diponibilityState: String = "",  //StatsInIt: "Non Dispo"

    var cLeDataOuvertDuParentList: Boolean = false,

    var articleHaveUniteImages: Boolean = false,
    var itsNewArrivale: Boolean = false,
    var imageDimention: String = "",
    var idForSearchArticles: Long = 0,
) {
    fun withProperKeyFireBaseAndTimeTamp(): A_ProduitInfosProtoJuin3 {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    fun toggleDisponibilityEtates(): A_ProduitInfosProtoJuin3 {
        val newState = disponibilityEtates.toggleEntreEtates()
        return this.copy(
            disponibilityEtates = newState,
            diponibilityState = newState.nomArabe,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    companion object {
        val caRef =
            Firebase.database.getReference("00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases/A_ProduitInfos")

        fun securedRemoveFireBaseDB() {
            caRef.removeValue()
        }
    }
}

enum class DisponibilityEtates(val nomArabe: String = "") {
    DISPO("متوفر"),
    NON_DISPO("غير متوفر"),
    PETITE_PROBABILITY("احتمال كبير");

    fun toggleEntreEtates(): DisponibilityEtates = when (this) {
        DISPO -> NON_DISPO
        NON_DISPO -> PETITE_PROBABILITY
        PETITE_PROBABILITY -> DISPO
    }
}
