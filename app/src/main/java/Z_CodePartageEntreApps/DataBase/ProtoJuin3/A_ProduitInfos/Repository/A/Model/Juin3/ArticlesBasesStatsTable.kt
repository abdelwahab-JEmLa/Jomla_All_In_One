package Z_CodePartageEntreApps.DataBase.ProtoJuin3.A_ProduitInfos.Repository.A.Model.Juin3

import A.AtelierMobile.Test.ID1.Test.Shared.DataBase.Fonctions.Main.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Entity
data class ArticlesBasesStatsTable(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    //S P Ids
    var idParentCategorie: Long? = null,

    // Section InfosDeBase
    var nom: String = "",

    //
    var nombreUniteInt: Int = 0,
    var nombreProduitDonSonCarton: Int = 0,

    // Section Etates Mutable
    var dernierFireBaseUpdateTimestamps: Long = 0,

    // Section InfosCoutes
    var prixVent: Double = 0.0,
    var prixAchat: Double = 0.0,
    var clientPrixVentUnite: Double = 0.0,


    //image
    var actualiseSonImage: Int = 0,
    var actualiseSonImageTest2: Int = 0,


    // Add availability states with proper initialization
    var disponibilityEtates: DisponibilityEtates = DisponibilityEtates.DISPO,

    // Section keyFireBase
    var keyFireBase: String = "",



    var nomArab: String = "",
    var autreNomDarticle: String? = null,
    var couleur1: String? = null,
    var idcolor1: Long = 0,
    var couleur2: String? = null,
    var idcolor2: Long = 0,
    var couleur3: String? = null,
    var idcolor3: Long = 0,
    var couleur4: String? = null,
    var idcolor4: Long = 0,
    var nomCategorie2: String? = null,
    var affichageUniteState: Boolean = false,
    var commmentSeVent: String? = null,
    var afficheBoitSiUniter: String? = null,
    var minQuan: Int = 0,
    var monBenfice: Double = 0.0,
    var neaon2: String = "",
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

    var articleHaveUniteImages: Boolean = false,
    var itsNewArrivale: Boolean = false,
    var imageDimention: String = "",
    var idForSearchArticles: Long = 0,

    )
{

    fun withProperKeyFireBaseAndTimeTamp(): ArticlesBasesStatsTable {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    fun toggleDisponibilityEtates(): ArticlesBasesStatsTable {
        val newState = disponibilityEtates.toggleEntreEtates()
        return this.copy(
            disponibilityEtates = newState,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    fun getNomFilesDesCouleursImagesDispoDonSockage(): List<String> {
        return emptyList()
    }


    companion object {
        val ref =
            Firebase.database.getReference("00_DataPrototype-04-02" +
                    "/_1_developingRef" +
                    "/C_InfosSqlDataBases" +
                    "/A_ProduitInfos")

        fun securedRemoveFireBaseDB() {
            ref.removeValue()
        }

        fun removeRef(
            preparedData: ArticlesBasesStatsTable
        ) {
            ref.child(preparedData.keyFireBase).removeValue()
        }
    }
}
