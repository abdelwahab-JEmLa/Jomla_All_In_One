package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.ID1.B.Models

import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class A_ProduitInfos(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    // Section InfosDeBase
    var nom: String = "",

    // Section Etates Mutable
    val timestamps: Long = System.currentTimeMillis(),
    val needUpdate: Boolean = true ,

    // Section sonCategory
    // Section InfosCoutes
    var prixVent: Double = 0.0,

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
    var nmbrUnite: Int = 0,
    var nmbrCaron: Int = 0,
    var affichageUniteState: Boolean = false,
    var commmentSeVent: String? = null,
    var afficheBoitSiUniter: String? = null,
    var monPrixAchat: Double = 0.0,
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
    fun withProperKeyFireBase(): A_ProduitInfos {
        val safeKey = keyFireBase.ifEmpty {
            getKeyFireBase(id, nom)
        }
        return this.copy(
            keyFireBase = safeKey,
            needUpdate = true
        )
    }

}

fun getActiveMigration(): Boolean {
    return true
}

fun parseDepuitOldAuNew(ancien: ArticlesBasesStatsTable) =
    A_ProduitInfos(
        id = ancien.idArticle.toLong(),
        nom = ancien.nomArticleFinale,
        keyFireBase = "",
        timestamps = System.currentTimeMillis(),
        needUpdate = true,
        prixVent = ancien.monPrixVent,


        classementCate = ancien.classementCate,
        nomArab = ancien.nomArab,
        autreNomDarticle = ancien.autreNomDarticle,
        nmbrCat = ancien.nmbrCat,
        couleur1 = ancien.couleur1,
        idcolor1 = ancien.idcolor1,
        couleur2 = ancien.couleur2,
        idcolor2 = ancien.idcolor2,
        couleur3 = ancien.couleur3,
        idcolor3 = ancien.idcolor3,
        couleur4 = ancien.couleur4,
        idcolor4 = ancien.idcolor4,
        nomCategorie2 = ancien.nomCategorie2,
        nmbrUnite = ancien.nmbrUnite,
        nmbrCaron = ancien.nmbrCaron,
        affichageUniteState = ancien.affichageUniteState,
        commmentSeVent = ancien.commmentSeVent,
        afficheBoitSiUniter = ancien.afficheBoitSiUniter,
        monPrixAchat = ancien.monPrixAchat,
        clienPrixVentUnite = ancien.clienPrixVentUnite,
        minQuan = ancien.minQuan,
        monBenfice = ancien.monBenfice,
        neaon2 = ancien.neaon2,
        idCategorie = ancien.idCategorie,
        catalogeParentID = ancien.catalogeParentID,
        funChangeImagsDimention = ancien.funChangeImagsDimention,
        nomCategorie = ancien.nomCategorie,
        neaon1 = ancien.neaon1,
        lastUpdateState = ancien.lastUpdateState,
        cartonState = ancien.cartonState,
        dateCreationCategorie = ancien.dateCreationCategorie,
        prixDeVentTotaleChezClient = ancien.prixDeVentTotaleChezClient,
        benficeTotaleEntreMoiEtClien = ancien.benficeTotaleEntreMoiEtClien,
        benificeTotaleEn2 = ancien.benificeTotaleEn2,
        monPrixAchatUniter = ancien.monPrixAchatUniter,
        monPrixVentUniter = ancien.monPrixVentUniter,
        benificeClient = ancien.benificeClient,
        monBeneficeUniter = ancien.monBeneficeUniter,
        diponibilityState = ancien.diponibilityState,
        cLeDataOuvertDuParentList = ancien.cLeDataOuvertDuParentList,
        articleHaveUniteImages = ancien.articleHaveUniteImages,
        itsNewArrivale = ancien.itsNewArrivale,
        imageDimention = ancien.imageDimention,
        idForSearchArticles = ancien.idForSearchArticles,

        )

