package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Main.B.Models.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class A_ProduitInfosTest(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    // Section InfosDeBase
    var nom: String = "",

    // Section Etates Mutable
    val timestamps: Long = System.currentTimeMillis(),
    val needUpdate: Boolean = true,

    // Section sonCategory
    // Section InfosCoutes
    var prixVent: Double = 0.0,
    var prixAchat: Double = 0.0,    //edited
    var nombreUniteInt: Int = 0,           //edited

    // Section keyFireBase
    val keyFireBase: String = "",


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
    var nomCategorie2: String? = null,
    var nmbrCaron: Int = 0,
    var affichageUniteState: Boolean = false,
    var commmentSeVent: String? = null,
    var afficheBoitSiUniter: String? = null,
    var clienPrixVentUnite: Double = 0.0,
    var minQuan: Int = 0,
    var monBenfice: Double = 0.0,
    var neaon2: String = "",
    var idCategorie: Double = 0.0,
    var catalogeParentID: Long = 0,
    var funChangeImagsDimention: Boolean = false, //imgStatIsSmall
    var nomCategorie: String = "",
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
    fun withProperKeyFireBase(): A_ProduitInfosTest {
        val safeKey = keyFireBase.ifEmpty {
            getKeyFireBase(id, nom)
        }
        return this.copy(
            keyFireBase = safeKey,
            needUpdate = true
        )
    }

}
