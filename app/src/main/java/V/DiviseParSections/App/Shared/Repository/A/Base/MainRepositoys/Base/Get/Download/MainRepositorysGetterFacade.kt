package V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download

import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.GetFocusedVars
import V.DiviseParSections.App.Shared.Repository.ArticlesBasesStatsTable
import V.DiviseParSections.App.Shared.Repository.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.Shared.Repository.CCategoriesCompoRepository
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.IDKeyModel11.Repository.KAchatCouleurOperationRepository
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1ProduitInfos
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class ParametresAppComptNonSaved(
    val itsDevMode: Boolean = true,
    val currentAppComptKeyID: String = "-OTmoNn0cljrRuhVR2s4",

    val debugNameId9AppComptInfos: String = "",

    val keyIdId7VentPeriod: String = "-OU9Xi8t6tbGKf_IisuB",
    val debugNameId7VentPeriod: String = "Juin_30__8_00",

    val activeWindowsSearchProduit: Boolean = false,
    val devStartUpScree: Screen = Screen.FacadePresentoireProduits,
    var enablePerformAutoClickImageDisplayer: Boolean= false,
)

@Stable
class MainRepositorysGetterFacade(
    private val context: Context,
    val databaseInitializationManager: WDatabaseInitializationManager,

    val repoM1ProduitInfos: RepoM1ProduitInfos,
    val repo3CouleurProduitInfos: Repo3CouleurProduitInfos,

    val a_GroupeValuesA_ProduitsToB_Categories: A_GroupeValuesA_ProduitsToB_Categories,
    val b3CategoriesCompoRepository: CCategoriesCompoRepository,

    val repo2Client: Repo2Client,
    val repo8BonVent: Repo8BonVent,

    val repo10OperationVentCouleur: Repo10OperationVentCouleur,

    val kAchatRepository: KAchatCouleurOperationRepository,

    val mVentPeriodeRepository: MVentPeriodeRepository,

    val repo9AppCompt: Repo9AppCompt,
    val repo13TarificationInfos: Repo13TarificationInfos,

    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    getterFocusedVars: GetFocusedVars,

    ) {
    val parametresAppComptNonSaved = ParametresAppComptNonSaved()
    val composScope = CoroutineScope(Dispatchers.IO)
    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    val travailleChezGrossisst3Ali = getterFocusedVars.currentM9AppCompt?.travailleChezGrossisst3Ali ?: false


    fun getClientLastBonVentParEtate(
        clientId: Long,
        etateActuellementEst: M8BonVent.EtateActuellementEst = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    ): M8BonVent? {
        return repo8BonVent.datasValue.filter {
            it.parentHClientOldID == clientId && it.etateActuellementEst == etateActuellementEst
        }.maxByOrNull { it.creationTimestamps } // Use creation timestamp for better ordering
    }

    fun getClientLastTransaction(clientId: Long): M8BonVent? {
        return repo8BonVent.datasValue.filter {
            it.parentHClientOldID == clientId
        }.maxByOrNull { it.creationTimestamps } // Use creation timestamp for better ordering
    }

    fun getRelatedCouleur(
        produit: ArticlesBasesStatsTable, colorIndex: Int
    ) = repo3CouleurProduitInfos.datasValue.find {
        it.parentBProduitOldID == produit.id && it.indexCouleurDansAncienProto == colorIndex
    }!!

    fun getVentForArticleAndColorInThisApp(
        article: ArticlesBasesStatsTable, colorIndex: Int
    ): M10OperationVentCouleur? {
        val relatedCouleur = relatedCouleurKeyParAncienMethod(article, colorIndex) ?: return null
        return getVent(relatedCouleur.key, article.id)
    }

    fun getVent(couleurKey: String, produitId: Long): M10OperationVentCouleur? {
        val ouvertData = repo9AppCompt.currentAppCompt ?: return null

        val bonVentKey = ouvertData.onVentM8BonVentKey
        val periodKey = ouvertData.onVentHVentPeriodKeyId
        val matchingOperation = repo10OperationVentCouleur.datasValue.find { operation ->
            operation.parentM3CouleurProduitInfosKeyID == couleurKey && operation.parentProduitInfosOldId == produitId && operation.parentM8BonVentKeyId == bonVentKey && operation.parentHVentPeriodKeyId == periodKey
        }

        return matchingOperation
    }

    fun relatedCouleurKeyParAncienMethod(produit: ArticlesBasesStatsTable, colorIndex: Int) =
        repo3CouleurProduitInfos.datasValue.find {
            it.parentBProduitOldID == produit.id && it.indexCouleurDansAncienProto == colorIndex
        }

    val filteredA_ProduitsParCatalogueBsonId by derivedStateOf {
        repoM1ProduitInfos.datasValue.filteredParCatalogueBsonId()
    }

    fun List<ArticlesBasesStatsTable>.filteredParCatalogueBsonId(): List<ArticlesBasesStatsTable> {
        val catalogueFilterId =
            repo9AppCompt.currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId

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
        repo2Client.datasValue.count { client ->
            val lastTransaction = getClientLastTransaction(client.id)
            lastTransaction?.etateActuellementEst in listOf(
                M8BonVent.EtateActuellementEst.Cible,
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
        // Fixed: This should be a function that returns a Modifier
        fun modifierAcDebugSemantics(hClientRepository: Repo2Client? = null): Modifier {
            return Modifier.semantics(mergeDescendants = true) {
                set(SemanticsPropertyKey("DebugID1=HClientInfos"), HClientInfos())
            }
        }

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
            return compt.onVentHVentPeriodKeyId + "--${compt.onVentM8BonVentKey}" + "--${bProduitDataBase.id}" + "--${bProduitDataBase.id}_${indexCouleur + 1}"
        }
    }
}
