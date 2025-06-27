package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.WDatabaseInitializationManager
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.B4CatalogueCategoriesRepository
import Z_CodePartageEntreApps.Repository.Main.Passive.Repository.A2_Passive.CCategoriesCompoRepository
import Z_CodePartageEntreApps.Repository.Main.Proto.Z_ComptAppStateCompoRepositoryProtoAvanJuin17
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class ACentralCompoRepositoryProtoJuin9(
    private val context: Context,
    val databaseInitializationManager: WDatabaseInitializationManager,

    val bProduitDataBase_SubClassFunctionality: BProduitDataBaseComposeRepositoryPJ17,
    val b1CouleurOuGoutProduitDataBaseRepository: B1CouleurOuGoutProduitDataBaseRepository,

    val a_GroupeValuesA_ProduitsToB_Categories: A_GroupeValuesA_ProduitsToB_Categories,
    val b3CategoriesCompoRepository: CCategoriesCompoRepository,

    val bClientsStateCompoRepository: B_ClientsStateCompoRepository,
    val transactionCommercialState: GTransactionVentRepository,

    val fCouleurAchatOperationRepositoryComposable: FVentCouleurOperationRepository,

    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
    val comptAppState: Z_ComptAppStateCompoRepositoryProtoAvanJuin17,

    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    // Fixed getVent function in ACentralCompoRepositoryProtoJuin9.kt
    fun getRelatedCouleur(
        produit: ArticlesBasesStatsTable,
        colorIndex: Int
    ) =
        b1CouleurOuGoutProduitDataBaseRepository.datasValue
            .find {
                it.parentBProduitOldID == produit.id
                        && it.indexCouleurDansAncienProto == colorIndex
            }!!

    fun getVentForArticleAndColorInThisApp(article: ArticlesBasesStatsTable, colorIndex: Int): FCouleurVentOperation? {
        val relatedCouleur = relatedCouleurKeyParAncienMethod(article, colorIndex) ?: return null
        return getVent(relatedCouleur.key, article.id)
    }
    
    fun getVent(couleurKey: String, produitId: Long): FCouleurVentOperation? {
        val ouvertData = zAppComptRepositoryComposable.ouvertData ?: return null

        val bonVentKey = ouvertData.ouvertF2BonVentId
        val periodKey = ouvertData.ouvertHPeriodVentKeyId
             /*  val produitId = fCouleurAchatOperationRepositoryComposable.datasValue.find { vent ->
            vent.keyID == couleurKey
        }?.parentProduitAncienId ?: return "0" // Get Long value and handle null cas*/
        val matchingOperation =
            fCouleurAchatOperationRepositoryComposable.datasValue.find { operation ->
                operation.parentCouleurDataBaseKey == couleurKey &&
                        operation.parentProduitAncienId == produitId && // Use parentProduitAncienId instead of parentProduitId
                        operation.parentBonVentId == bonVentKey && // Fixed: use parentBonVentId instead of parentProduitId
                        operation.parentEPeriodVentId == periodKey // Fixed: use parentEPeriodVentId instead of parentProduitId
            }

        return matchingOperation
    }

    fun getKeyID(produitID: Long, index: Int): String {
        return createCouleurOnVentKey(
            compt = zAppComptRepositoryComposable.ouvertData!!,
            bProduitDataBase = bProduitDataBase_SubClassFunctionality
                .datasValue.find { it.id == produitID }!!,
            indexCouleur = index
        )
    }

    fun relatedCouleurKeyParAncienMethod(produit: ArticlesBasesStatsTable, colorIndex: Int) =
        b1CouleurOuGoutProduitDataBaseRepository.datasValue
            .find {
                it.parentBProduitOldID == produit.id
                        && it.indexCouleurDansAncienProto == colorIndex
            }



    val filteredA_ProduitsParCatalogueBsonId by derivedStateOf {
        bProduitDataBase_SubClassFunctionality.datasValue.filteredParCatalogueBsonId()
    }

    fun List<ArticlesBasesStatsTable>.filteredParCatalogueBsonId(): List<ArticlesBasesStatsTable> {
        val catalogueFilterId =
            zAppComptRepositoryComposable.ouvertData?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId

        val catalogues = B4CatalogueCategoriesRepository().associateBy { it.key }
        val targetCatalogue = catalogues[catalogueFilterId] ?: return this

        val categoriesInCatalogue = b3CategoriesCompoRepository.datasValue
            .filter { it.catalogueParentId == targetCatalogue.id }
            .map { it.id }

        return this.filter { product ->
            val categoryId = product.idParentCategorie
            categoryId != null && categoriesInCatalogue.contains(categoryId)
        }
    }

    val nombreClientsOuLeurDernierEtateCible: Int by derivedStateOf {
        bClientsStateCompoRepository.datasValue.count { client ->
            val lastTransaction = transactionCommercialState.getClientLastTransaction(client.id)
            lastTransaction?.etateActuellementEst in listOf(
                TransactionVent.EtateActuellementEst.Cible,
            )
        }
    }

    val clientOuSonMarqueMapEstOuvert by derivedStateOf {
        bClientsStateCompoRepository.findClientById(comptAppState.idClientOuSonMarqueMapEstOuvert)
    }

    val ouvertTransactionCommercial: TransactionVent? by derivedStateOf {
        clientOuSonMarqueMapEstOuvert?.let {
            transactionCommercialState.getClientLastTransactionParEtate(
                it.id, TransactionVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            )
        }
    }

    init {
        composScope.launch {
            try {
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
        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()

        // Version that returns Result for better error handling
        fun String?.withOutInvalidCharactersResult(): Result<String> {
            return try {
                val result = this.withOutInvalidCharacters()
                Result.success(result)
            } catch (e: IllegalArgumentException) {
                Result.failure(e)
            }
        }

        fun String?.withOutInvalidCharacters(): String {
            val cleanedNom = (this ?: "")
                .replace(Regex("[.#\$\\[\\]/®™©{}\"'`~!@%^&*()+=|\\\\:;<>?-]"), "")
                .replace(Regex("\\s+"), "_")
                .replace(Regex("_+"), "_")
                .trim('_')
                .take(40)


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
            return compt.ouvertHPeriodVentKeyId +
                    "--${compt.ouvertF2BonVentId}" +
                    "--${bProduitDataBase.id}" +
                    "--${bProduitDataBase.id}_${indexCouleur + 1}"
        }
    }
}
