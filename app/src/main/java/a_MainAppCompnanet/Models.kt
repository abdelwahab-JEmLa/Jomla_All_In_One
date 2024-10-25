package a_RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ArticlesBasesStats(
    @PrimaryKey var idArticle: Int = 0,
    var nomArticleFinale: String = "",
    var classementCate: Double = 0.0,
    var nomArab: String = "",
    var autreNomDarticle: String? = null,
    var nmbrCat: Int = 0,
    var couleur1: String? = null,
    var couleur2: String? = null,
    var couleur3: String? = null,
    var couleur4: String? = null,
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
    var monPrixVent: Double = 0.0,
    var diponibilityState: String = "",
    var neaon2: String = "",
    var idCategorie: Double = 0.0,
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
    var monBeneficeUniter: Double = 0.0
) {
    // No-argument constructor for Firebase
    constructor() : this(0)
}
data class ColorsArticles(
    val idColore: Long = 0,
    val nameColore: String = "",
    val iconColore: String = "",
    var classementColore: Int = 0
)
data class PlacesOfArticelsInCamionette(
    val idPlace: Long = 0,
    val namePlace: String = "",
    var classement: Int = 0
)

data class ArticlesRecived(
    val aa_vid: Long = 0,
    var a_c_idarticle_c: Long = 0,
    val a_d_nomarticlefinale_c: String = "",
    var idSupplierTSA: Int = 0,
    var nomSupplierTSA: String? = null,
    var idInStoreOfSupp: Long = 0,
    var nmbrCat: Int = 0,
    val trouve_c: Boolean = false,
    val a_u_prix_1_q1_c: Double = 0.0,
    var a_q_prixachat_c: Double = 0.0,
    val a_l_nmbunite_c: Int = 0,
    val a_r_prixdevent_c: Double = 0.0,
    val nomclient: String = "",
    val datedachate: String = "",
    val a_d_nomarticlefinale_c_1: String = "",
    val quantityachete_c_1: Int = 0,
    val a_d_nomarticlefinale_c_2: String = "",
    val quantityachete_c_2: Int = 0,
    val a_d_nomarticlefinale_c_3: String = "",
    val quantityachete_c_3: Int = 0,
    val a_d_nomarticlefinale_c_4: String = "",
    val quantityachete_c_4: Int = 0,
    val totalquantity: Int = 0,
    val etatdecommendcolum: Int = 0,
    var itsInFindedAskSupplierSA: Boolean = false,
    var disponibylityStatInSupplierStore: String = "",
) {
    constructor() : this(0L)
}

data class Suppliers(
    var idSupplierSu: Long = 0,
    var nomSupplierSu: String = "",
    var nomVocaleArabeDuSupplier: String = "",
    var nameInFrenche: String = "",
    var bonDuSupplierSu: String = "",
    val couleurSu: String = "#FFFFFF", // Default color
    var currentCreditBalance: Double = 0.0, // New field for current credit balance
    var longTermCredit : Boolean = false,
    var ignoreItProdects: Boolean = false,
    var classmentSupplier: Double = 0.0,
) {
    constructor() : this(0)
}




data class PlacesOfArticelsInEacheSupplierSrore(
    val idCombinedIdArticleIdSupplier: String = "",
    val idPlace: Long= 0,
    val idArticle: Long = 0,
    val idSupplierSu: Long= 0,
)
data class MapArticleInSupplierStore(
    val idPlace: Long = 0,
    val namePlace: String = "",
    val idSupplierOfStore: Long = 0,
    val inRightOfPlace: Boolean = false,
    val itClassement: Int = 0,
)

@Entity
data class Categories(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    var position: Int = 0
) {
    constructor() : this(0, "", 0)
}
@Entity
data class ArticlesSelled(
    @PrimaryKey(autoGenerate = true) val vid: Long = 0,
    val idArticle: Long = 0,
    val nomArticleFinale: String = "",
    val prixAchat: Double = 0.0,
    val nmbrunitBC: Double = 0.0,
    val clientPrixVentUnite: Double = 0.0,
    val nomClient: String = "",
    val dateDachate: String = "",
    val nomCouleur1: String = "",
    val quantityAcheteCouleur1: Int = 0,
    val nomCouleur2: String = "",
    val quantityAcheteCouleur2: Int = 0,
    val nomCouleur3: String = "",
    val quantityAcheteCouleur3: Int = 0,
    val nomCouleur4: String = "",
    val quantityAcheteCouleur4: Int = 0,
    val totalQuantity: Int = 0,
    val nonTrouveState: Boolean = false,
    val verifieState: Boolean = false,
    var changeCaronState: String = "",
    var monPrixAchatUniterBC: Double =  0.0,
    var benificeDivise: Double =  0.0,

    //Stats
    var typeEmballage: String = "",
    var idArticlePlaceInCamionette: Long = 0,

    var choisirePrixDepuitFireStoreOuBaseBM: String = "",
    val warningRecentlyChanged: Boolean = false,

    //FireBase PrixEditeur
    val monPrixVentBM: Double = 0.0,
    var monPrixVentUniterBM: Double =  0.0,

    var monBenificeBM: Double =  0.0,
    var monBenificeUniterBM: Double =  0.0,
    var totalProfitBM: Double =  0.0,


    var clientBenificeBM: Double =  0.0,

    //FireStore
    var monPrixVentFireStoreBM: Double =  0.0,
    var monPrixVentUniterFireStoreBM: Double =  0.0,

    var monBenificeFireStoreBM: Double =  0.0,
    var monBenificeUniterFireStoreBM: Double =  0.0,
    var totalProfitFireStoreBM: Double =  0.0,

    var clientBenificeFireStoreBM: Double =  0.0,

    ) {
    // Constructeur sans argument nécessaire pour Firebase
    constructor() : this(0)

}