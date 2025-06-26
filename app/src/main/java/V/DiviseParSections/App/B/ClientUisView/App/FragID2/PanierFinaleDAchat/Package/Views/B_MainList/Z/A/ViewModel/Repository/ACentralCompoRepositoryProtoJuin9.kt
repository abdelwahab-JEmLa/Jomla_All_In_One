package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.B_ClientsStateCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.C3_TransactionCommercial
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.CCategoriesCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.ETransactionCommercialCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.FAchatOperationCouleurRepositoryComposable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.FCouleurVentOperation
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.A.ViewModel.Repository.A2_Passive.Z_AppCompt
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.DataBase.WDatabaseInitializationManager
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

    val clientsState: B_ClientsStateCompoRepository,
    val transactionCommercialState: ETransactionCommercialCompoRepository,

    val fCouleurAchatOperationRepositoryComposable: FAchatOperationCouleurRepositoryComposable,

    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
    val comptAppState: Z_ComptAppStateCompoRepositoryProtoAvanJuin17,

    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    fun getKeyID(produitID: Long, index: Int): String {
        return createCouleurOnVentKey(
            compt = zAppComptRepositoryComposable.ouvertData!!,
            bProduitDataBase = bProduitDataBase_SubClassFunctionality
                .datasValue.find { it.id == produitID }!!,
            indexCouleur = index
        )
    }

    fun relatedCouleurKey(produit: ArticlesBasesStatsTable, colorIndex: Int) =
        b1CouleurOuGoutProduitDataBaseRepository.datasValue
            .find {
                it.parentBProduitOldID == produit.id
                        && it.indexCouleurDansAncienProto == colorIndex
            }!!


    fun getRelatedFAchatCouleurOperation(produitID: Long, index: Int): FCouleurVentOperation? {
        val fAchatCouleurOperation = fCouleurAchatOperationRepositoryComposable
            .datasValue.find { it.keyID == getKeyID(produitID, index) }

        return fAchatCouleurOperation
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
        clientsState.datasValue.count { client ->
            val lastTransaction = transactionCommercialState.getClientLastTransaction(client.id)
            lastTransaction?.etateActuellementEst in listOf(
                C3_TransactionCommercial.EtateActuellementEst.Cible,
            )
        }
    }

    val clientOuSonMarqueMapEstOuvert by derivedStateOf {
        clientsState.findClientById(comptAppState.idClientOuSonMarqueMapEstOuvert)
    }

    val ouvertTransactionCommercial: C3_TransactionCommercial? by derivedStateOf {
        clientOuSonMarqueMapEstOuvert?.let {
            transactionCommercialState.getClientLastTransactionParEtate(
                it.id, C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
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
            return compt.ouvertF1PeriodVentId +
                    "--${compt.ouvertF2BonVentId}" +
                    "--${bProduitDataBase.id}" +
                    "--${bProduitDataBase.id}_${indexCouleur + 1}"
        }
    }
}
