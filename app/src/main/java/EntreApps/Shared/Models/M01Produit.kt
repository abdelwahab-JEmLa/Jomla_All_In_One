package EntreApps.Shared.Models

import Application4.App.Fragment.ID1.Fragment.ViewModel.Prioriter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.DisponibilityEtates
import Z_CodePartageEntreApps.DataBase.ProtoJuin3.Fonctions.Main.getKeyFireBase
import androidx.room.Entity
import androidx.room.PrimaryKey

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


    //S P Ids
    var idParentCategorie: Long = 0,
    var positionDonSonCesFrereCategorieProduits: Int = 0,

    var classement_By_FilterKeys_M3: Int = 0,


    // Section InfosDeBase
    var nom: String = "",
    var nomMutable: String = "",

    //-----------------Filter States-------------------------------------------------------------------------------------------------------------------------
    val processPositioningInFactory: ProcessPositioningInFactoryID1 = ProcessPositioningInFactoryID1.CreeAuGeneralHandler,
    val etateActuelleOnFusionAvecBaseDonne: EtateActuelleOnFusionAvecBaseDonne = EtateActuelleOnFusionAvecBaseDonne.CategorieOriginaleDefinie,
    // default tag: Dernier_VentAchat_Est_Trop_Luin
    var tag_prioriter_str: String = Prioriter.Dernier_VentAchat_Est_Trop_Luin.name +",",
    var its_in_echantiallants : Boolean = false,

    //----------------------------------------------------------------------------------------------------------------------------------------

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
    var prioriter: Prioriter? = null,

    ) {

    fun getDebugInfos(): String {
        return nom + "[" + keyID.takeLast(4).uppercase() + "]"
    }

    /**
     * Parses [tag_prioriter_str] (comma-separated Prioriter names) into a Set<Prioriter>.
     * Unknown / blank tokens are silently ignored.
     */
    fun produit_set_Tag_Priorite(): Set<Prioriter> {
        if (tag_prioriter_str.isBlank()) return emptySet()
        return tag_prioriter_str
            .split(",")
            .mapNotNull { token -> Prioriter.entries.firstOrNull { it.name == token.trim() } }
            .toSet()
    }

    fun setReturn_Produit_Ac_tag_prioriter_str(
        produit_set_Tag_Priorite: Set<Prioriter>,
        produit: M01Produit
    ): M01Produit {
        val serialized = produit_set_Tag_Priorite.joinToString(",") { it.name }
        return produit.copy(
            tag_prioriter_str = serialized,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    fun priorite_GetSet(prioriter: Prioriter): Prioriter {
        this.prioriter = if (this.prioriter == prioriter) null else prioriter
        return prioriter
    }


    fun matchesPrioriteFilter(filter: Set<Prioriter>?): Boolean {
        // null filter = show everything
        if (filter == null) return true

        // Parse tags from the stored string field (reuses the existing helper)
        val tags = produit_set_Tag_Priorite()

        // Produit has no priority tags → always show it (untagged products are never filtered out)
        if (tags.isEmpty()) return true

        // Produit has tags → show only if at least one tag is in the active filter
        return tags.any { it in filter }
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
            "classement_By_FilterKeys_M3" to classement_By_FilterKeys_M3,

            // Names
            "nom" to nom,
            "nomMutable" to nomMutable,
            "nomArab" to nomArab,
            "autreNomDarticle" to autreNomDarticle,

            // Fusion state
            "etateActuelleOnFusionAvecBaseDonne" to etateActuelleOnFusionAvecBaseDonne.name,
            "tag_prioriter_str" to tag_prioriter_str,
            "its_in_echantiallants" to its_in_echantiallants,

            // Units / cartons
            "nombreUniteInt" to nombreUniteInt,
            "nombreProduitDonSonCarton" to nombreProduitDonSonCarton,
            "its_Carton" to its_Carton,
            "cartonState" to cartonState,

            // Priority / position
            "heldPrioriteDemandAuGrossist" to heldPrioriteDemandAuGrossist,
            "position_store_3jamale" to position_store_3jamale,
            "dernier_timeTamps_position_store_3jamale" to dernier_timeTamps_position_store_3jamale,

            // Prices
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
            "afficheCesDetailPourComptBsonId" to afficheCesDetailPourComptBsonId,

            // Disponibility
            "disponibilityEtates" to disponibilityEtates.name,
            "disponibilityEtates_Pour_presentaion_par_Camion" to disponibilityEtates_Pour_presentaion_par_Camion.name,

            "keyFireBase" to keyFireBase,
            "couleur1" to couleur1,
            "couleur2" to couleur2,
            "couleur3" to couleur3,
            "couleur4" to couleur4,
            "couleur5" to couleur5,
            "couleur6" to couleur6,
            "couleur7" to couleur7,
            "couleur8" to couleur8,
            "couleur9" to couleur9,
            "idcolor1" to idcolor1,
            "idcolor2" to idcolor2,
            "idcolor3" to idcolor3,
            "idcolor4" to idcolor4,
            "idcolor5" to idcolor5,
            "idcolor6" to idcolor6,
            "idcolor7" to idcolor7,
            "idcolor8" to idcolor8,
            "idcolor9" to idcolor9,
            "nomCategorie2" to nomCategorie2,
            "affichageUniteState" to affichageUniteState,
            "commmentSeVent" to commmentSeVent,
            "afficheBoitSiUniter" to afficheBoitSiUniter,
            "minQuan" to minQuan,
            "monBenfice" to monBenfice,
            "neaon2" to neaon2,
            "funChangeImagsDimention" to funChangeImagsDimention,
            "nomCategorie" to nomCategorie,
            "neaon1" to neaon1,
            "lastUpdateState" to lastUpdateState,
            "dateCreationCategorie" to dateCreationCategorie,
            "prixDeVentTotaleChezClient" to prixDeVentTotaleChezClient,
            "benficeTotaleEntreMoiEtClien" to benficeTotaleEntreMoiEtClien,
            "benificeTotaleEn2" to benificeTotaleEn2,
            "monPrixAchatUniter" to monPrixAchatUniter,
            "monPrixVentUniter" to monPrixVentUniter,
            "articleHaveUniteImages" to articleHaveUniteImages,
            "itsNewArrivale" to itsNewArrivale,
            "imageDimention" to imageDimention,
            "idForSearchArticles" to idForSearchArticles,
            "prioriter" to prioriter?.name,
            "quantite_Boit_Par_Carton" to quantite_Boit_Par_Carton,

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

        fun safe_Remove_DataBase_Ref(): Unit {
            ref.removeValue()
        }

        val ref = M00CentralParametresOfAllApps.centralRef
            .child("A_ProduitInfos")

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
