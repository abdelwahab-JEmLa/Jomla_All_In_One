package EntreApps.Shared.Models

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Fonctions.Main.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot

@Entity
data class M01Produit(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var keyID: String = RepositorysMainGetter.Companion.getPushFireBase(ref),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),


    var bsonObjectId: String = RepositorysMainGetter.Companion.getPushFireBase(ref),
    var dernierFireBaseUpdateTimestamps: Long = 0,

    var count_Don_Depot: Int = 0,

    // Garde les propriétés originales pour la compatibilité
    val processPositioningInFactory: ProcessPositioningInFactoryID1 = ProcessPositioningInFactoryID1.CreeAuGeneralHandler,

    //S P Ids
    var idParentCategorie: Long = 0,
    var positionDonSonCesFrereCategorieProduits: Int = 0,


    // Section InfosDeBase
    var nom: String = "",
    var nomMutable: String = "",


    // Garde la propriété originale pour la compatibilité
    val etateActuelleOnFusionAvecBaseDonne: EtateActuelleOnFusionAvecBaseDonne = EtateActuelleOnFusionAvecBaseDonne.CategorieOriginaleDefinie,

    var nombreUniteInt: Int = 1,
    //-----------------Cartons-------------------------------------------------------------------------------------------------------------------------
    var nombreProduitDonSonCarton: Int = 1,
    val its_Carton: Boolean = false,
    var cartonState: String = "",

//-----------------Section.Etates.Mutable-------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------------------------------
    val heldPrioriteDemandAuGrossist: Boolean = false,
    //---------------------Positions---------------------------------------------------------------------------------------------------------------------
    var position_store_3jamale: Int = 0,
    var dernier_timeTamps_position_store_3jamale: Long = 0,

//------------------------------------------------------------------------------------------------------------------------------------------
    // Section InfosCoutes
    var prixDefiniParGerant: Double = 0.0,
    var prixVent: Double = 0.0,
    var cachePrixVent: Boolean = false,
    var pourcentage_Prix_Progressive: Int = 60,


    var prixAchat: Double = 0.0,
    var prixAchatDernierTimeTempUpdate: Long = 0L,
    var clientPrixVentUnite: Double = 0.0,

    var afficheUniteAuPrint: Boolean = false,


    //image
    var actualiseSonImage: Int = 0,
    var actualiseSonImageTest2: Int = 0,

    //Ui States Personele Paramater
    var afficheCesDetailPourComptBsonId: String = "",

    // Garde la propriété originale pour la compatibilité
    var disponibilityEtates: DisponibilityEtates = DisponibilityEtates.NON_DISPO,

    var disponibilityEtates_Pour_presentaion_par_Camion: DisponibilityEtates = DisponibilityEtates.NON_DISPO,

    // Section keyFireBase
    var keyFireBase: String = "",

    var nomArab: String = "",
    var autreNomDarticle: String? = null,

    var couleur1: String? = "couleur1",
    var couleur2: String? = null,
    var couleur3: String? = null,
    var couleur4: String? = null,
    var couleur5: String? = null,
    var couleur6: String? = null,
    var couleur7: String? = null,
    var couleur8: String? = null,
    var couleur9: String? = null,

    var idcolor2: Long = 0,
    var idcolor7: Long = 0,
    var idcolor8: Long = 0,
    var idcolor4: Long = 0,
    var idcolor6: Long = 0,
    var idcolor5: Long = 0,
    var idcolor3: Long = 0,
    var idcolor1: Long = 1,
    var idcolor9: Long = 0,

    var nomCategorie2: String? = null,
    var affichageUniteState: Boolean = false,
    var commmentSeVent: String? = null,
    var afficheBoitSiUniter: String? = null,
    var minQuan: Int = 0,
    var monBenfice: Double = 0.0,
    var neaon2: String = "",
    var funChangeImagsDimention: Boolean = false,
    var nomCategorie: String = "",
    var neaon1: Double = 0.0,
    var lastUpdateState: String = "",
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
    ///-------------------------------------------------------------------------------
    var setIN_Vent_Its_Quantity_Represent: M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent =
        M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit,
    var quantite_Boit_Par_Carton: Int = 1,

    ) {

    fun getDebugInfos(): String {
        return nom + "[" + keyID.takeLast(4).uppercase() + "]"
    }

    fun toFirebaseMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "keyID" to keyID,
            "creationTimestamp" to creationTimestamp,
            "bsonObjectId" to bsonObjectId,
            "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
            "dernierFireBaseUpdateTimestamps" to dernierFireBaseUpdateTimestamps,
            "count_Don_Depot" to count_Don_Depot,

            // Process positioning
            "processPositioningInFactory" to processPositioningInFactory.name,

            // Category and position
            "idParentCategorie" to idParentCategorie,
            "positionDonSonCesFrereCategorieProduits" to positionDonSonCesFrereCategorieProduits,

            // Names
            "nom" to nom,
            "nomMutable" to nomMutable,
            "nomArab" to nomArab,
            "autreNomDarticle" to autreNomDarticle,

            // Fusion state
            "etateActuelleOnFusionAvecBaseDonne" to etateActuelleOnFusionAvecBaseDonne.name,

            // Units and cartons
            "nombreUniteInt" to nombreUniteInt,
            "nombreProduitDonSonCarton" to nombreProduitDonSonCarton,
            "its_Carton" to its_Carton,
            "cartonState" to cartonState,
            "quantite_Boit_Par_Carton" to quantite_Boit_Par_Carton,

            // Priority and positions
            "heldPrioriteDemandAuGrossist" to heldPrioriteDemandAuGrossist,
            "position_store_3jamale" to position_store_3jamale,
            "dernier_timeTamps_position_store_3jamale" to dernier_timeTamps_position_store_3jamale,

            // Pricing
            "prixDefiniParGerant" to prixDefiniParGerant,
            "prixVent" to prixVent,
            "cachePrixVent" to cachePrixVent,
            "pourcentage_Prix_Progressive" to pourcentage_Prix_Progressive,
            "prixAchat" to prixAchat,
            "prixAchatDernierTimeTempUpdate" to prixAchatDernierTimeTempUpdate,
            "clientPrixVentUnite" to clientPrixVentUnite,
            "afficheUniteAuPrint" to afficheUniteAuPrint,

            // Images
            "actualiseSonImage" to actualiseSonImage,
            "actualiseSonImageTest2" to actualiseSonImageTest2,
            "articleHaveUniteImages" to articleHaveUniteImages,
            "funChangeImagsDimention" to funChangeImagsDimention,
            "imageDimention" to imageDimention,

            // UI states
            "afficheCesDetailPourComptBsonId" to afficheCesDetailPourComptBsonId,

            "disponibilityEtates" to disponibilityEtates.name,
            "disponibilityEtates_Pour_presentaion_par_Camion" to disponibilityEtates_Pour_presentaion_par_Camion.name,

            // Firebase key
            "keyFireBase" to keyFireBase,

            // Colors - all 9 colors
            "couleur1" to couleur1,
            "idcolor1" to idcolor1,
            "couleur2" to couleur2,
            "idcolor2" to idcolor2,
            "couleur3" to couleur3,
            "idcolor3" to idcolor3,
            "couleur4" to couleur4,
            "idcolor4" to idcolor4,
            "couleur5" to couleur5,
            "idcolor5" to idcolor5,
            "couleur6" to couleur6,
            "idcolor6" to idcolor6,
            "couleur7" to couleur7,
            "idcolor7" to idcolor7,
            "couleur8" to couleur8,
            "idcolor8" to idcolor8,
            "couleur9" to couleur9,
            "idcolor9" to idcolor9,

            // Additional fields
            "nomCategorie2" to nomCategorie2,
            "affichageUniteState" to affichageUniteState,
            "commmentSeVent" to commmentSeVent,
            "afficheBoitSiUniter" to afficheBoitSiUniter,
            "minQuan" to minQuan,
            "monBenfice" to monBenfice,
            "neaon2" to neaon2,
            "catalogeParentID" to idParentCategorie,
            "nomCategorie" to nomCategorie,
            "neaon1" to neaon1,
            "lastUpdateState" to lastUpdateState,
            "dateCreationCategorie" to dateCreationCategorie,

            // Financial calculations
            "prixDeVentTotaleChezClient" to prixDeVentTotaleChezClient,
            "benficeTotaleEntreMoiEtClien" to benficeTotaleEntreMoiEtClien,
            "benificeTotaleEn2" to benificeTotaleEn2,
            "monPrixAchatUniter" to monPrixAchatUniter,
            "monPrixVentUniter" to monPrixVentUniter,

            // Flags
            "itsNewArrivale" to itsNewArrivale,
            "idForSearchArticles" to idForSearchArticles,

            // Quantity representation
            "setIN_Vent_Its_Quantity_Represent" to setIN_Vent_Its_Quantity_Represent.name
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

    fun withProperKeyFireBaseAndTimeTamp(): M01Produit {
        val safeKey = keyFireBase.ifEmpty { getKeyFireBase(id, nom) }
        return this.copy(
            keyFireBase = safeKey,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    fun toggleDisponibilityEtates(): M01Produit {
        val newState = disponibilityEtates.toggleEntreEtates()
        return this.copy(
            disponibilityEtates = newState,
            dernierFireBaseUpdateTimestamps = System.currentTimeMillis()
        )
    }

    companion object {
        fun get_Default(): M01Produit {
            return  M01Produit()
        }
        val KeyTagModel = "IdKeyModel1"

        fun safe_Remove_DataBase_Ref(): Unit {
            ref.removeValue()
        }

        val ref = Firebase.database.getReference(
            "00_DataPrototype-04-02" +
                    "/_1_developingRef" +
                    "/C_InfosSqlDataBases" +
                    "/A_ProduitInfos"
        )

        val refFirestore: CollectionReference = RepositorysMainGetter.firestoreCentralRefData
            .document("Model01Produit")
            .collection("Datas")

        fun removeRef(preparedData: M01Produit) {
            ref.child(preparedData.keyFireBase).removeValue()
        }

        fun compareEntre(
            ancien: M01Produit,
            newData: M01Produit
        ): Boolean {
            return ancien.id == newData.id
        }
    }
}

/**
 * Extension Firestore → ArticlesBasesStatsTable
 * Utilisable partout : snapshot.documents.mapNotNull { it.toArticle() }
 * Gère tous les types (enums, Long/Int, nullable strings) sans boilerplate répété.
 */
fun DocumentSnapshot.toArticle(): M01Produit? {
    val d = data ?: return null

    fun str(k: String, def: String = "") = (d[k] as? String) ?: def
    fun bool(k: String, def: Boolean = false) = (d[k] as? Boolean) ?: def
    fun long(k: String, def: Long = 0L) = (d[k] as? Long) ?: (d[k] as? Number)?.toLong() ?: def
    fun int(k: String, def: Int = 0) = (d[k] as? Long)?.toInt() ?: (d[k] as? Number)?.toInt() ?: def
    fun dbl(k: String, def: Double = 0.0) = (d[k] as? Double) ?: (d[k] as? Number)?.toDouble() ?: def
    fun <T : Enum<T>> enum(k: String, values: Array<T>, def: T): T =
        values.firstOrNull { it.name == d[k] as? String } ?: def

    return M01Produit(
        id = long("id"),
        keyID = str("keyID"),
        creationTimestamp = long("creationTimestamp"),
        dernierTimeTampsSynchronisationAvecFireBase = long("dernierTimeTampsSynchronisationAvecFireBase"),
        bsonObjectId = str("bsonObjectId"),
        dernierFireBaseUpdateTimestamps = long("dernierFireBaseUpdateTimestamps"),
        count_Don_Depot = int("count_Don_Depot"),
        processPositioningInFactory = enum("processPositioningInFactory",
            M01Produit.ProcessPositioningInFactoryID1.values(),
            M01Produit.ProcessPositioningInFactoryID1.CreeAuGeneralHandler),
        idParentCategorie = long("idParentCategorie"),
        positionDonSonCesFrereCategorieProduits = int("positionDonSonCesFrereCategorieProduits"),
        nom = str("nom"),
        nomMutable = str("nomMutable"),
        etateActuelleOnFusionAvecBaseDonne = enum("etateActuelleOnFusionAvecBaseDonne",
            M01Produit.EtateActuelleOnFusionAvecBaseDonne.values(),
            M01Produit.EtateActuelleOnFusionAvecBaseDonne.CategorieOriginaleDefinie),
        nombreUniteInt = int("nombreUniteInt", 1),
        nombreProduitDonSonCarton = int("nombreProduitDonSonCarton", 1),
        its_Carton = bool("its_Carton"),
        cartonState = str("cartonState"),
        heldPrioriteDemandAuGrossist = bool("heldPrioriteDemandAuGrossist"),
        position_store_3jamale = int("position_store_3jamale"),
        dernier_timeTamps_position_store_3jamale = long("dernier_timeTamps_position_store_3jamale"),
        prixDefiniParGerant = dbl("prixDefiniParGerant"),
        prixVent = dbl("prixVent"),
        cachePrixVent = bool("cachePrixVent"),
        pourcentage_Prix_Progressive = int("pourcentage_Prix_Progressive", 60),
        prixAchat = dbl("prixAchat"),
        prixAchatDernierTimeTempUpdate = long("prixAchatDernierTimeTempUpdate"),
        clientPrixVentUnite = dbl("clientPrixVentUnite"),
        afficheUniteAuPrint = bool("afficheUniteAuPrint"),
        actualiseSonImage = int("actualiseSonImage"),
        actualiseSonImageTest2 = int("actualiseSonImageTest2"),
        afficheCesDetailPourComptBsonId = str("afficheCesDetailPourComptBsonId"),
        disponibilityEtates = enum("disponibilityEtates",
            DisponibilityEtates.values(), DisponibilityEtates.NON_DISPO),
        disponibilityEtates_Pour_presentaion_par_Camion = enum("disponibilityEtates_Pour_presentaion_par_Camion",
            DisponibilityEtates.values(), DisponibilityEtates.NON_DISPO),
        keyFireBase = str("keyFireBase"),
        nomArab = str("nomArab"),
        autreNomDarticle = d["autreNomDarticle"] as? String,
        couleur1 = d["couleur1"] as? String ?: "couleur1",
        couleur2 = d["couleur2"] as? String,
        couleur3 = d["couleur3"] as? String,
        couleur4 = d["couleur4"] as? String,
        couleur5 = d["couleur5"] as? String,
        couleur6 = d["couleur6"] as? String,
        couleur7 = d["couleur7"] as? String,
        couleur8 = d["couleur8"] as? String,
        couleur9 = d["couleur9"] as? String,
        idcolor1 = long("idcolor1", 1),
        idcolor2 = long("idcolor2"),
        idcolor3 = long("idcolor3"),
        idcolor4 = long("idcolor4"),
        idcolor5 = long("idcolor5"),
        idcolor6 = long("idcolor6"),
        idcolor7 = long("idcolor7"),
        idcolor8 = long("idcolor8"),
        idcolor9 = long("idcolor9"),
        nomCategorie2 = d["nomCategorie2"] as? String,
        affichageUniteState = bool("affichageUniteState"),
        commmentSeVent = d["commmentSeVent"] as? String,
        afficheBoitSiUniter = d["afficheBoitSiUniter"] as? String,
        minQuan = int("minQuan"),
        monBenfice = dbl("monBenfice"),
        neaon2 = str("neaon2"),
        funChangeImagsDimention = bool("funChangeImagsDimention"),
        nomCategorie = str("nomCategorie"),
        neaon1 = dbl("neaon1"),
        lastUpdateState = str("lastUpdateState"),
        dateCreationCategorie = str("dateCreationCategorie"),
        prixDeVentTotaleChezClient = dbl("prixDeVentTotaleChezClient"),
        benficeTotaleEntreMoiEtClien = dbl("benficeTotaleEntreMoiEtClien"),
        benificeTotaleEn2 = dbl("benificeTotaleEn2"),
        monPrixAchatUniter = dbl("monPrixAchatUniter"),
        monPrixVentUniter = dbl("monPrixVentUniter"),
        articleHaveUniteImages = bool("articleHaveUniteImages"),
        itsNewArrivale = bool("itsNewArrivale"),
        imageDimention = str("imageDimention"),
        idForSearchArticles = long("idForSearchArticles"),
        quantite_Boit_Par_Carton = int("quantite_Boit_Par_Carton", 1),
        setIN_Vent_Its_Quantity_Represent = enum("setIN_Vent_Its_Quantity_Represent",
            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.values(),
            M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit),
    )
}
