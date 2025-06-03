package Z_CodePartageEntreApps.Model.Z.Archive


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

data class SuppliersTabelle(
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





data class EntreBonsGrosTabele(
    val vidBG: Long = 0,
    var idArticleInSectionsOfImageBG: String = "",
    var idArticleBG: Long = 0,
    var nomArticleBG: String = "",
    var ancienPrixBG: Double = 0.0,
    var ancienPrixOnUniterBG: Double = 0.0,
    var newPrixAchatBG: Double = 0.0,
    var quantityAcheteBG: Int = 0,
    var quantityUniterBG: Int = 0,
    var subTotaleBG: Double = 0.0,
    var grossisstBonN: Int = 0,
    var supplierIdBG: Long = 0,
    var supplierNameBG: String = "",
    var uniterCLePlusUtilise: Boolean = false,
    var erreurCommentaireBG: String = "",
    var passeToEndStateBG: Boolean = false,
    var dateCreationBG: String = "",
){
    // Secondary constructor for Firebase
    constructor() : this(0)
}
data class TabelleSupplierArticlesRecived(
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
