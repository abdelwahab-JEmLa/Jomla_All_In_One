package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B1CouleurOuGoutProduitDataBaseRepository
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.BProduitInfosRepository
import V.DiviseParSections.App.Shared.Repository.CCategoriesCompoRepository
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FCouleurVentOperationInfos
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.FVentCouleurOperationRepository
import V.DiviseParSections.App.Shared.Repository.ID2HClientInfos.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2HClientInfos.Repository.HClientRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVentRepository
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository.KAchatCouleurOperationRepository
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.Shared.Repository.Z.Passive.Archive.MVentPeriodeRepository
import V.DiviseParSections.App._0.Navigation.Screen
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.WDatabaseInitializationManager
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

data class ParametresAppComptNonSaved(
    val gerantComptKeyByParent: String = "t1",
    val activePeriodKeyByParent: String = "Juin_30__8_00",

    val activePeriodKeyId: String = "-OU9Xi8t6tbGKf_IisuB",
    val parentDebugInfosID7VentPeriod: String = "Juin_30__8_00",

    val activeWindowsSearchProduit: Boolean = false,
    val startUpScree: Screen = Screen.TestProduitFastSearchDialog
)
data class IDsModels(
     val ID2HClientInfos :String= "ID2",
)
@Stable
class AGetter(
    private val context: Context,
    val databaseInitializationManager: WDatabaseInitializationManager,

    val bProduitInfosRepository: BProduitInfosRepository,
    val b1CouleurOuGoutProduitDataBaseRepository: B1CouleurOuGoutProduitDataBaseRepository,

    val a_GroupeValuesA_ProduitsToB_Categories: A_GroupeValuesA_ProduitsToB_Categories,
    val b3CategoriesCompoRepository: CCategoriesCompoRepository,

    val hClientRepository: HClientRepository,
    val gBonVentRepository: GBonVentRepository,

    val fVentCouleurOperationRepository: FVentCouleurOperationRepository,
    val kAchatRepository: KAchatCouleurOperationRepository,

    val mVentPeriodeRepository: MVentPeriodeRepository,

    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,

    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
) {
    val parametresAppComptNonSaved = ParametresAppComptNonSaved()

     val composScope = CoroutineScope(Dispatchers.IO)
    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    private fun createTempBonVent(
        clientId: Long,
        clientKey: String,
        etate: GBonVent.EtateActuellementEst,
        periodKey: String,
        comptKey: String
    ) = GBonVent(
        keyID = GBonVent.generePushKey(),
        parentPeriodeVentKeyID = periodKey,
        parentHClientKeyID = clientKey,
        parentHClientOldID = clientId,
        nomClientConcerned = hClientRepository.findHClientInfos(clientId)?.nom ?: "Unknown",
        parentZAppComptCreateurKeyID = comptKey,
        etateActuellementEst = etate,
        parentID2ClientKeyByParent = BSetterFacade.getListDesParentKeys("null")[HClientInfos.keyModel]
            ?: "",
        parentID7VentPeriodeKeyByParent = BSetterFacade.getListDesParentKeys("null")[Z_AppCompt.keyModelValID7VentParent]
            ?: "",
        parentID8C2TypeTransactionKeyByParent = BSetterFacade.getListDesParentKeys("null")[GBonVent.EtateActuellementEst.keyModel]
            ?: ""
    )

    fun getClientLastBonVentParEtate(
        clientId: Long,
        etateActuellementEst: GBonVent.EtateActuellementEst = GBonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    ): GBonVent? {
        return gBonVentRepository.datasValue.filter {
            it.parentHClientOldID == clientId && it.etateActuellementEst == etateActuellementEst
        }.maxByOrNull { it.creationTimestamps } // Use creation timestamp for better ordering
    }

    fun getClientLastTransaction(clientId: Long): GBonVent? {
        return gBonVentRepository.datasValue.filter {
            it.parentHClientOldID == clientId
        }.maxByOrNull { it.creationTimestamps } // Use creation timestamp for better ordering
    }

    fun getRelatedCouleur(
        produit: ArticlesBasesStatsTable, colorIndex: Int
    ) = b1CouleurOuGoutProduitDataBaseRepository.datasValue.find {
        it.parentBProduitOldID == produit.id && it.indexCouleurDansAncienProto == colorIndex
    }!!

    fun getVentForArticleAndColorInThisApp(
        article: ArticlesBasesStatsTable, colorIndex: Int
    ): FCouleurVentOperationInfos? {
        val relatedCouleur = relatedCouleurKeyParAncienMethod(article, colorIndex) ?: return null
        return getVent(relatedCouleur.key, article.id)
    }

    fun getVent(couleurKey: String, produitId: Long): FCouleurVentOperationInfos? {
        val ouvertData = zAppComptRepositoryComposable.currentAppCompt ?: return null

        val bonVentKey = ouvertData.onVentGBonVentKeyId
        val periodKey = ouvertData.onVentHVentPeriodKeyId
        val matchingOperation = fVentCouleurOperationRepository.datasValue.find { operation ->
            operation.parentCouleurInfosKeyID == couleurKey && operation.parentProduitInfosOldId == produitId && operation.parentGBonVentKeyId == bonVentKey && operation.parentHVentPeriodKeyId == periodKey
        }

        return matchingOperation
    }

    fun relatedCouleurKeyParAncienMethod(produit: ArticlesBasesStatsTable, colorIndex: Int) =
        b1CouleurOuGoutProduitDataBaseRepository.datasValue.find {
            it.parentBProduitOldID == produit.id && it.indexCouleurDansAncienProto == colorIndex
        }

    val filteredA_ProduitsParCatalogueBsonId by derivedStateOf {
        bProduitInfosRepository.datasValue.filteredParCatalogueBsonId()
    }

    fun List<ArticlesBasesStatsTable>.filteredParCatalogueBsonId(): List<ArticlesBasesStatsTable> {
        val catalogueFilterId =
            zAppComptRepositoryComposable.currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId

        val catalogues = B4CatalogueCategoriesRepository().associateBy { it.key }
        val targetCatalogue = catalogues[catalogueFilterId] ?: return this

        val categoriesInCatalogue =
            b3CategoriesCompoRepository.datasValue.filter { it.catalogueParentId == targetCatalogue.id }
                .map { it.id }

        return this.filter { product ->
            val categoryId = product.idParentCategorie
            categoryId != null && categoriesInCatalogue.contains(categoryId)
        }
    }

    val nombreClientsOuLeurDernierEtateCible: Int by derivedStateOf {
        hClientRepository.datasValue.count { client ->
            val lastTransaction = getClientLastTransaction(client.id)
            lastTransaction?.etateActuellementEst in listOf(
                GBonVent.EtateActuellementEst.Cible,
            )
        }
    }

    val clientOldIdOuSonMarqueMapPasFerme by derivedStateOf { zAppComptRepositoryComposable.currentAppCompt?.onVentFClientAncienId }

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

        val centralRef = Firebase.database.getReference(
            "00_DataPrototype-04-02" + "/_1_developingRef" + "/C_InfosSqlDataBases"
        )

        fun getPushFireBase(ref: DatabaseReference) = ref.push().key.toString()

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
            return compt.onVentHVentPeriodKeyId + "--${compt.onVentGBonVentKeyId}" + "--${bProduitDataBase.id}" + "--${bProduitDataBase.id}_${indexCouleur + 1}"
        }
    }
}
