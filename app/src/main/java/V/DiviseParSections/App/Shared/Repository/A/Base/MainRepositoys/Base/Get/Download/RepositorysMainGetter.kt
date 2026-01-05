package V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.M3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo03CouleurProduitInfos.Repository.Repo03CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.Repo11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.Repo15Grossist.Repository.Repo15Grossist
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.CategoriesTabelle
import V.DiviseParSections.App.Shared.Repository.Repo16CategorieProduit.Repository.RepoM16CategorieProduit
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.M17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo17MessageVocale.Repository.Repo17MessageVocale
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.M18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo18ParametresAppComptNonSaved.Repository.Repo18CentralParametresOfAllApps
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.Repo20ObsarvationEtudion
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriodeRepository
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.Main.Main.WDatabaseInitializationManager
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class RepositorysMainGetter(
    private val context: Context,
    val databaseInitializationManager: WDatabaseInitializationManager,

    val repo1ProduitInfos: RepoM1Produit,
    val repoM1Produit: RepoM1Produit,
    val repo03CouleurProduitInfos: Repo03CouleurProduitInfos,

    val a_GroupeValuesA_ProduitsToB_Categories: A_GroupeValuesA_ProduitsToB_Categories,

    val repo2Client: Repo2Client,
    val repo8BonVent: Repo8BonVent,

    val repo10OperationVentCouleur: Repo10OperationVentCouleur,

    val repo11AchatOperation: Repo11AchatOperation,

    val mVentPeriodeRepository: MVentPeriodeRepository,

    val repo9AppCompt: Repo9AppCompt,
    val repo13TarificationInfos: Repo13TarificationInfos,
    val repo14VentPeriode: Repo14VentPeriode,
    val repo15Grossist: Repo15Grossist,
    val repoM16CategorieProduit: RepoM16CategorieProduit,
    val repo17MessageVocale: Repo17MessageVocale,
    val repo18CentralParametresOfAllApps: Repo18CentralParametresOfAllApps,
    val repo19Etudiant: Repo19Etudiant,
    val repo20ObsarvationEtudion: Repo20ObsarvationEtudion,

    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
) {
    val composScope = CoroutineScope(Dispatchers.IO)
    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    //--------------M1----------------------------------------------------------------------------------------------------------------------------------------------------------
    fun find_M1Produit_ByKeyID(keyId: String): ArticlesBasesStatsTable? =
        repo1ProduitInfos.datasValue.find { it.keyID == keyId }

    fun find_M1Produit_By_OldId(oldId: Long?): ArticlesBasesStatsTable? =
        repo1ProduitInfos.datasValue.find { it.id == oldId }


    //--------------M2Client----------------------------------------------------------------------------------------------------------------------------------------------------------
    fun find_M2Client(keyID: String): M2Client? =
        repo2Client.datasValue.find { it.keyID == keyID }

    fun find_M2Client_By_M10Vent(key: M10OperationVentCouleur): M2Client? =
        repo2Client.datasValue.find {
            it.keyID == find_M8BonVent(key.parent_M8BonVent_KeyId)
                ?.parent_M2Client_KeyID
        }

    fun get_Last_M8BonVent_Par_M2Client(m2Client: M2Client): M8BonVent? {
        return repo8BonVent.datasValue
            .filter {
                (it.parent_M2Client_KeyID == m2Client.keyID)
            }
            .maxByOrNull { it.creationTimestamps }
    }

    //--------------M3Couleur----------------------------------------------------------------------------------------------------------------------------------------------------------
    fun find_M3CouleurInfos_By_KeyID(keyId: String): M3CouleurProduitInfos? =
        repo03CouleurProduitInfos.datasValue.find { it.keyID == keyId }

    fun find_ListM3CouleurInfos_By_Parent_Produit_KeyID(parentBProduitInfosKeyID: String) =
        repo03CouleurProduitInfos.datasValue.filter { it.parentBProduitInfosKeyID == parentBProduitInfosKeyID }

    fun find_M3Couleur_By(
        m1Produit: ArticlesBasesStatsTable,
        indexCouleurDansAncienProto: Int
    ): M3CouleurProduitInfos? {
        return repo03CouleurProduitInfos.datasValue.find {
            it.parentBProduitInfosKeyID == m1Produit.keyID
                    && it.indexCouleurDansAncienProto == indexCouleurDansAncienProto
        }
    }

    //--------------M8BonVent----------------------------------------------------------------------------------------------------------------------------------------------------------
    fun find_M8BonVent(keyID: String): M8BonVent? =
        repo8BonVent.datasValue.find { it.keyID == keyID }

    //--------------M9AppCompt----------------------------------------------------------------------------------------------------------------------------------------------------------
    fun find_M9AppCompt_By_KeyID(keyID: String): Z_AppCompt? =
        repo9AppCompt.datasValue.find { it.keyID == keyID }

    //--------------M10----------------------------------------------------------------------------------------------------------------------------------------------------------
    fun find_M10OperationVentCouleur(keyID: String): M10OperationVentCouleur? =
        repo10OperationVentCouleur.datasValue.find { it.keyID == keyID }
     /*
    fun find_M10OperationVentCouleur_Du_Abdelwahab_echantillons_Produits(keyID: String,client_filtreur_key : String = "keyID	-Oh4W0-igT_bXGOo-LC_" ): List<M10OperationVentCouleur>? =
        repo10OperationVentCouleur.datasValue.find { it.parent_M8BonVent_KeyId
                                                    //<--
                                                    //TODO(1): du client keyid ==
            == keyID  &}     */



    //--------------M13----------------------------------------------------------------------------------------------------------------------------------------------------------
    fun find_M13Tarification_By_KeyID(keyID: String): M13TarificationInfos? =
        repo13TarificationInfos.datasValue.find { it.keyID == keyID }

    fun find_List_Tariffs_Du_Produit(
        parent_Produit_keyID: String,
        its_grossist_app: Boolean = false
    ): List<M13TarificationInfos> {
        return repo13TarificationInfos.datasValue
            .filter { tariff ->
                tariff.parent_M1Produit_KeyId == parent_Produit_keyID &&
                        if (its_grossist_app) {
                            tariff.typeChoisi.its_gro_app
                        } else {
                            !tariff.typeChoisi.its_gro_app
                        }
            }
            .groupBy { it.typeChoisi }
            .mapValues { (_, tariffsOfType) ->
                tariffsOfType.maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }
            }
            .values
            .filterNotNull()
    }

    // Helper function to find specific grossist tariff types
    fun find_Tariff_SuperGros_For_Produit(parent_Produit_keyID: String): M13TarificationInfos? {
        return repo13TarificationInfos.datasValue
            .filter {
                it.parent_M1Produit_KeyId == parent_Produit_keyID &&
                        it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
            }
            .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }
    }
    //--------------M16----------------------------------------------------------------------------------------------------------------------------------------------------------
    fun find_M16CategorieProduit_By_OldID(OldID: Long): CategoriesTabelle? =
        repoM16CategorieProduit.datasValue.find { it.id == OldID }
    fun find_M16CategorieProduit_By_KeyId(keyID: String): CategoriesTabelle? =
        repoM16CategorieProduit.datasValue.find { it.keyID == keyID }

    //--------------M17----------------------------------------------------------------------------------------------------------------------------------------------------------
    fun find_By_KeyID_M17MessageVocale(keyID: String): M17MessageVocale? =
        repo17MessageVocale.datasValue.value.find { it.keyID == keyID }


    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------


    fun getRelatedCouleur(
        produit: ArticlesBasesStatsTable, colorIndex: Int
    ) = repo03CouleurProduitInfos.datasValue.find {
        it.parentBProduitOldID == produit.id && it.indexCouleurDansAncienProto == colorIndex
    }!!

    fun getVentForArticleAndColorInThisApp(
        article: ArticlesBasesStatsTable, colorIndex: Int
    ): M10OperationVentCouleur? {
        val relatedCouleur = relatedCouleurKeyParAncienMethod(article, colorIndex) ?: return null
        return getVent(relatedCouleur.keyID, article.id)
    }

    fun getVent(couleurKey: String, produitId: Long): M10OperationVentCouleur? {
        val ouvertData = repo9AppCompt.currentAppCompt ?: return null

        val bonVentKey = ouvertData.onVentM8BonVentKey
        val periodKey = ouvertData.current_OnVent_M14VentPeriode_KeyID
        val matchingOperation = repo10OperationVentCouleur.datasValue.find { operation ->
            operation.parent_M3CouleurProduit_KeyID == couleurKey && operation.parentProduitInfosOldId == produitId && operation.parent_M8BonVent_KeyId == bonVentKey && operation.parent_M14VentPeriod_KeyId == periodKey
        }

        return matchingOperation
    }

    val filteredA_ProduitsParCatalogueBsonId by derivedStateOf {
        repo1ProduitInfos.datasValue.filteredParCatalogueBsonId()
    }

    fun relatedCouleurKeyParAncienMethod(produit: ArticlesBasesStatsTable, colorIndex: Int=0): M3CouleurProduitInfos? {
        return repo03CouleurProduitInfos.datasValue.find {
            it.parentBProduitInfosKeyID == produit.keyID &&
                    it.indexCouleurDansAncienProto == (colorIndex)
        }
    }

    fun List<ArticlesBasesStatsTable>.filteredParCatalogueBsonId(): List<ArticlesBasesStatsTable> {
        val catalogueFilterId =
            repo9AppCompt.currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId

        val catalogues = B4CatalogueCategoriesRepository().associateBy { it.keyID }
        val targetCatalogue = catalogues[catalogueFilterId] ?: return this

        val categoriesInCatalogue =
            repoM16CategorieProduit.datasValue.filter { it.catalogueParentId == targetCatalogue.id }
                .map { it.id }

        return this.filter { product ->
            val categoryId = product.idParentCategorie
            categoryId != null && categoriesInCatalogue.contains(categoryId)
        }
    }

    val nombreClientsOuLeurDernierEtateCible: Int by derivedStateOf {
        repo2Client.datasValue.count { client ->
            // Check if this client has any unconfirmed orders (confirmeCommande_TimeTamp == 0)
            repo8BonVent.datasValue.any { bonVent ->
                bonVent.etateActuellementEst == M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT &&
                        bonVent.parent_M2Client_KeyID == client.keyID &&
                        bonVent.confirmeCommande_TimeTamp == 0L
            }
        }
    }

// In RepositorysMainGetter.kt, replace the init block with this:

    init {
        composScope.launch {
            try {
                M18CentralParametresOfAllApps().au_Lence_DimininueDatasFB.ifTrue {
                    M11AchatOperation.remove_ref()
                }


                databaseInitializationManager.initializeAllRepositories(context)


            } catch (e: Exception) {
                databaseInitializationManager.updateMainInitDataBaseProgressEtate(1.0f)
            }
        }

        composScope.launch {
            a_MasterRepositorysGrpProtoJuin3.model.collect { masterModel ->
                masterModel?.let { model ->
                    _loadingProgress.floatValue = model.progress
                }
            }
        }
    }

    companion object {
        val centralRef = Firebase.database.getReference(
            "00_DataPrototype-04-02" + "/_1_developingRef" + "/C_InfosSqlDataBases"
        )

        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()

        inline fun Long?.ifNotNullOrZero(block: () -> Unit) {
            if (this != null && this != 0L) block()
        }

        inline fun String?.ifNotNullOrEmpty(block: () -> Unit) {
            if (!this.isNullOrEmpty()) block()
        }


        inline fun Boolean.ifTrue(block: () -> Unit) {
            if (this) block()
        }

        inline fun Boolean.ifFalse(block: () -> Unit) {
            if (!this) block()
        }

        fun String?.empty_If_Null(value: String = ""): String {
            return this ?: value
        }


        // Version that returns Result for better error handling
        fun String?.withOutFireBaseInvalidCharactersResult(): Result<String> {
            return try {
                val result = this.withOutFireBaseInvalidCharacters()
                Result.success(result)
            } catch (e: IllegalArgumentException) {
                Result.failure(e)
            }
        }

        fun String?.withOutFireBaseInvalidCharacters(): String {
            val cleanedNom =
                (this ?: "").replace(Regex("[.#\$\\[\\]/®™©{}\"'`~!@%^&*()+=|\\\\:;<>?]"), "")
                    .replace(Regex("\\s+"), "_").replace(Regex("_+"), "_").trim('_')


            return when {
                cleanedNom.isNotEmpty() -> cleanedNom
                else -> throw IllegalArgumentException("Invalid ID or name")
            }
        }

        fun createCouleurOnVentKey(
            compt: Z_AppCompt,
            bProduitDataBase: ArticlesBasesStatsTable,
            indexCouleur: Int,
        ): String {
            return compt.current_OnVent_M14VentPeriode_KeyID + "--${compt.onVentM8BonVentKey}" + "--${bProduitDataBase.id}" + "--${bProduitDataBase.id}_${indexCouleur + 1}"
        }


    }
}
