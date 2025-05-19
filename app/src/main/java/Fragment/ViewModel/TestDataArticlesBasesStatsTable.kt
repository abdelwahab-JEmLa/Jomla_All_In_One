package Fragment.ViewModel

import Z_CodePartageEntreApps.Model.Z.Archive.ArticlesBasesStatsTable
import androidx.room.Entity

fun testDataArticlesBasesStatsTable(): ArticlesBasesStatsTable {
                                //<--
                                //TODO(1): cree test data dpuit json
                                //
                                @Entity
                                public final data class ArticlesBasesStatsTable(
                                    val idArticle: Int = 0,
                                    val nomArticleFinale: String = "",
                                    val classementCate: Double = 0.0,
                                    val nomArab: String = "",
                                    val autreNomDarticle: String? = null,
                                    val nmbrCat: Int = 0,
                                    val couleur1: String? = null,
                                    val idcolor1: Long = 0,
                                    val couleur2: String? = null,
                                    val idcolor2: Long = 0,
                                    val couleur3: String? = null,
                                    val idcolor3: Long = 0,
                                    val couleur4: String? = null,
                                    val idcolor4: Long = 0,
                                    val nomCategorie2: String? = null,
                                    val nmbrUnite: Int = 0,
                                    val nmbrCaron: Int = 0,
                                    val affichageUniteState: Boolean = false,
                                    val commmentSeVent: String? = null,
                                    val afficheBoitSiUniter: String? = null,
                                    val monPrixAchat: Double = 0.0,
                                    val clienPrixVentUnite: Double = 0.0,
                                    val minQuan: Int = 0,
                                    val monBenfice: Double = 0.0,
                                    val monPrixVent: Double = 0.0,
                                    val neaon2: String = "",
                                    val idCategorie: Double = 0.0,
                                    val catalogeParentID: Long = 0,
                                    val funChangeImagsDimention: Boolean = false,
                                    val nomCategorie: String = "",
                                    val neaon1: Double = 0.0,
                                    val lastUpdateState: String = "",
                                    val cartonState: String = "",
                                    val dateCreationCategorie: String = "",
                                    val prixDeVentTotaleChezClient: Double = 0.0,
                                    val benficeTotaleEntreMoiEtClien: Double = 0.0,
                                    val benificeTotaleEn2: Double = 0.0,
                                    val monPrixAchatUniter: Double = 0.0,
                                    val monPrixVentUniter: Double = 0.0,
                                    val benificeClient: Double = 0.0,
                                    val monBeneficeUniter: Double = 0.0,
                                    val diponibilityState: String = "",
                                    val cLeDataOuvertDuParentList: Boolean = false,
                                    val articleHaveUniteImages: Boolean = false,
                                    val itsNewArrivale: Boolean = false,
                                    val imageDimention: String = "",
                                    val idForSearchArticles: Long = 0
                                )

    Z_CodePartageEntreApps.Model.Z.Archive
    ArticlesBasesStatsTable.kt

}
