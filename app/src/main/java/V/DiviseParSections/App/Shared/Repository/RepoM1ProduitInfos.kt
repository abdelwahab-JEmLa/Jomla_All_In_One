package V.DiviseParSections.App.Shared.Repository

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.MainRepositorysGetterFacade
import Z_CodePartageEntreApps.DataBase.Main.Main.A.Base.A_ProduitDataBaseProtoJuin17
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Fonctions.Main.getKeyFireBase
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class RepoM1ProduitInfos(
    val ancienRepo: A_ProduitDataBaseProtoJuin17,
) {
    val dao = ancienRepo.dao
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _datas = mutableStateOf<List<ArticlesBasesStatsTable>>(emptyList())
    val datasValue by derivedStateOf { _datas.value }


    init {
        composScope.launch {
            dao.getAllFlow().collect { _datas.value = it }
        }
    }

    fun upsert(data: ArticlesBasesStatsTable) {
        val existingIndex = datasValue.indexOfFirst { ancien ->
            ArticlesBasesStatsTable.compareEntre(ancien = ancien, newData = data)
        }
        _datas.value = if (existingIndex >= 0) {
            datasValue.toMutableList().apply {
                this[existingIndex] = this[existingIndex].copy(
                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                )
            }
        } else {
            datasValue + data
        }

        addOrUpdatedAncienRepo(existingIndex, data)
    }

    fun deleteData(data: ArticlesBasesStatsTable) {
        _datas.value = datasValue.filter { existing ->
            !ArticlesBasesStatsTable.compareEntre(ancien = existing, newData = data)
        }
        deleteDataAncienRepo(data)
    }

    private fun addOrUpdatedAncienRepo(
        existingIndex: Int,
        data: ArticlesBasesStatsTable
    ) {
        composScope.launch {
            ancienRepo.addOrUpdatedAncienRepo(existingIndex, data)
        }
    }

    private fun deleteDataAncienRepo(
        data: ArticlesBasesStatsTable
    ) {
        composScope.launch {
            ancienRepo.deleteDataAncienRepo(data)
        }
    }

    companion object {
        fun ArticlesBasesStatsTable?.logDebugIt(nomVale: String = "") {
            Log.d(
                "ArticlesBasesStatsTable",
                infos(nomVale)
            )
        }

        private fun ArticlesBasesStatsTable?.infos(
            nomVale: String
        ) = nomVale + if (this != null) {
            keyID
            "\n id = $id "
            "\n keyID = $keyID "
        } else {
            "data is null"
        }
    }
}

@Entity
data class ArticlesBasesStatsTable(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var keyID: String = MainRepositorysGetterFacade.getPushFireBase(ref),

    var bsonObjectId: String = MainRepositorysGetterFacade.getPushFireBase(ref),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var dernierFireBaseUpdateTimestamps: Long = 0,

    // Garde les propriétés originales pour la compatibilité
    val processPositioningInFactory: ProcessPositioningInFactoryID1 = ProcessPositioningInFactoryID1.CreeAuGeneralHandler,

    //S P Ids
    var idParentCategorie: Long? = null,
    var positionDonSonCesFrereCategorieProduits: Int = 0,

    // Section InfosDeBase
    var nom: String = "",
    var nomMutable: String = "",

    // Garde la propriété originale pour la compatibilité
    val etateActuelleOnFusionAvecBaseDonne: EtateActuelleOnFusionAvecBaseDonne = EtateActuelleOnFusionAvecBaseDonne.CategorieOriginaleDefinie,

    var nombreUniteInt: Int = 1,
    var nombreProduitDonSonCarton: Int = 1,

    // Section Etates Mutable
    val heldPrioriteDemandAuGrossist: Boolean = false,

    // Section InfosCoutes
    var prixVent: Double = 0.0,
    var cachePrixVent: Boolean = false,

    var prixAchat: Double = 0.0,
    var prixAchatDernierTimeTempUpdate: Long = 0L,
    var clientPrixVentUnite: Double = 0.0,

    //image
    var actualiseSonImage: Int = 0,
    var actualiseSonImageTest2: Int = 0,

    //Ui States Personele Paramater
    var afficheCesDetailPourComptBsonId: String = "",

    // Garde la propriété originale pour la compatibilité
    var disponibilityEtates: DisponibilityEtates = DisponibilityEtates.DISPO,

    // Section keyFireBase
    var keyFireBase: String = "",

    var nomArab: String = "",
    var autreNomDarticle: String? = null,
    var couleur1: String? = "couleur1",
    var idcolor1: Long = 1,
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
    var funChangeImagsDimention: Boolean = false,
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
) {
    fun getDebugInfos(): String {
        return nom + "[" + keyID.takeLast(4).uppercase() + "]"
    }

    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "keyID" to keyID,
            "bsonObjectId" to bsonObjectId,
            "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
            "dernierFireBaseUpdateTimestamps" to dernierFireBaseUpdateTimestamps,
            "processPositioningInFactory" to processPositioningInFactory.name,
            "idParentCategorie" to idParentCategorie,
            "positionDonSonCesFrereCategorieProduits" to positionDonSonCesFrereCategorieProduits,
            "nom" to nom,
            "nomMutable" to nomMutable,
            "etateActuelleOnFusionAvecBaseDonne" to etateActuelleOnFusionAvecBaseDonne.name,
            "nombreUniteInt" to nombreUniteInt,
            "nombreProduitDonSonCarton" to nombreProduitDonSonCarton,
            "heldPrioriteDemandAuGrossist" to heldPrioriteDemandAuGrossist,
            "prixVent" to prixVent,
            "cachePrixVent" to cachePrixVent,
            "prixAchat" to prixAchat,
            "prixAchatDernierTimeTempUpdate" to prixAchatDernierTimeTempUpdate,
            "clientPrixVentUnite" to clientPrixVentUnite,
            "actualiseSonImage" to actualiseSonImage,
            "actualiseSonImageTest2" to actualiseSonImageTest2,
            "afficheCesDetailPourComptBsonId" to afficheCesDetailPourComptBsonId,
            "disponibilityEtates" to disponibilityEtates.name,
            "keyFireBase" to keyFireBase,
            "nomArab" to nomArab,
            "autreNomDarticle" to autreNomDarticle,
            "couleur1" to couleur1,
            "idcolor1" to idcolor1,
            "couleur2" to couleur2,
            "idcolor2" to idcolor2,
            "couleur3" to couleur3,
            "idcolor3" to idcolor3,
            "couleur4" to couleur4,
            "idcolor4" to idcolor4,
            "nomCategorie2" to nomCategorie2,
            "affichageUniteState" to affichageUniteState,
            "commmentSeVent" to commmentSeVent,
            "afficheBoitSiUniter" to afficheBoitSiUniter,
            "minQuan" to minQuan,
            "monBenfice" to monBenfice,
            "neaon2" to neaon2,
            "catalogeParentID" to catalogeParentID,
            "funChangeImagsDimention" to funChangeImagsDimention,
            "nomCategorie" to nomCategorie,
            "neaon1" to neaon1,
            "lastUpdateState" to lastUpdateState,
            "cartonState" to cartonState,
            "dateCreationCategorie" to dateCreationCategorie,
            "prixDeVentTotaleChezClient" to prixDeVentTotaleChezClient,
            "benficeTotaleEntreMoiEtClien" to benficeTotaleEntreMoiEtClien,
            "benificeTotaleEn2" to benificeTotaleEn2,
            "monPrixAchatUniter" to monPrixAchatUniter,
            "monPrixVentUniter" to monPrixVentUniter,
            "articleHaveUniteImages" to articleHaveUniteImages,
            "itsNewArrivale" to itsNewArrivale,
            "imageDimention" to imageDimention,
            "idForSearchArticles" to idForSearchArticles
        )
    }

    enum class ProcessPositioningInFactoryID1 {
        CreeDepuitRechercheRapid,
        CreeAuGeneralHandler
    }

    enum class EtateActuelleOnFusionAvecBaseDonne {
        CaprtureSonImage,
        PrixAchatPriseDepuitGrossist,
        PrixDeVentDefinie,
        CategorieOriginaleDefinie,
        PositionAvecCesFrereDefinie,
    }

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

    companion object {
        val KeyTagModel = "IdKeyModel1"

        fun safeRemoveRef(): Unit {
            ref.removeValue()
        }

        val ref = Firebase.database.getReference(
            "00_DataPrototype-04-02" +
                    "/_1_developingRef" +
                    "/C_InfosSqlDataBases" +
                    "/A_ProduitInfos"
        )

        fun removeRef(preparedData: ArticlesBasesStatsTable) {
            ref.child(preparedData.keyFireBase).removeValue()
        }

        fun compareEntre(
            ancien: ArticlesBasesStatsTable,
            newData: ArticlesBasesStatsTable
        ): Boolean {
            return ancien.id == newData.id
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

    companion object {
        fun fromString(value: String): DisponibilityEtates {
            return when (value) {
                "DISPO" -> DISPO
                "NON_DISPO" -> NON_DISPO
                "PETITE_PROBABILITY" -> PETITE_PROBABILITY
                else -> DISPO // default value
            }
        }
    }
}
