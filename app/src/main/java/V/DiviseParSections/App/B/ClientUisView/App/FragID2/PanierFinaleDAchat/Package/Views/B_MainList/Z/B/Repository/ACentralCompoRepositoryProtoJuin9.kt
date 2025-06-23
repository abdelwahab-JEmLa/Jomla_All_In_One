package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A1.Proto.Juin17.Proto.WDatabaseInitializationManager
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A1.Proto.Juin17.Proto.Z.Repository.Juin9.Proto.Z_ComptAppStateCompoRepositoryProtoAvanJuin17
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.A_GroupeValuesA_ProduitsToB_Categories
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.B4CatalogueCategoriesRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.B_ClientsStateCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.C3_TransactionCommercial
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.CCategoriesCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.DTransactionCommercialCompoRepository
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.A2_Passive.ZAppCompt_RepositoryComposable
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Package.Views.B_MainList.Z.B.Repository.DSubClassFunctionality_CouleurAchatOperation.trouve_nomImageFichieOuApellationDuCouleurPar
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class ACentralCompoRepositoryProtoJuin9(
    private val context: Context,
    val databaseInitializationManager: WDatabaseInitializationManager,

    val bProduitDataBase_SubClassFunctionality: BProduitDataBaseComposeRepositoryPJ17,

    val a_GroupeValuesA_ProduitsToB_Categories: A_GroupeValuesA_ProduitsToB_Categories,
    val b3CategoriesCompoRepository: CCategoriesCompoRepository,

    val clientsState: B_ClientsStateCompoRepository,
    val transactionCommercialState: DTransactionCommercialCompoRepository,

    val dCouleurAchatOperationRepositoryComposable: DCouleurAchatOperationRepositoryComposable,
    val dCouleurAchatOperationSubClassFunctionality: DSubClassFunctionality_CouleurAchatOperation,

    val zAppComptRepositoryComposable: ZAppCompt_RepositoryComposable,
    val comptAppState: Z_ComptAppStateCompoRepositoryProtoAvanJuin17,

    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)
    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    val ouvertData_dCouleurAchatOperation_SubClassFunctionality = dCouleurAchatOperationRepositoryComposable.ouvertData
    val ouvertData_bProduitDataBase_SubClassFunctionality = bProduitDataBase_SubClassFunctionality.ouvertData

    fun ouvreAddDataDepuitIndexCouleur(
        article: ArticlesBasesStatsTable,
        index: Int
    ): Unit {
        val data = dCouleurAchatOperationSubClassFunctionality.getDataDepuitIndex(
            transactionCommercialState.ouvertData,
            article,
            nomImageFichieOuApellationDuCouleur= trouve_nomImageFichieOuApellationDuCouleurPar(
                index,
                article
            )
        )

        dCouleurAchatOperationRepositoryComposable.addOrUpdateData(data)

        dCouleurAchatOperationSubClassFunctionality.confirmeOldOuvertData(
            ouvertData_dCouleurAchatOperation_SubClassFunctionality
        )?.let {
            dCouleurAchatOperationRepositoryComposable.addOrUpdateData(
                it
            )
        }

        zAppComptRepositoryComposable
            .subClassFunctionality
            .ouvrireCouleurAchatOperationPourCeCompt(
                data.bsonObjectId,
                "${ouvertData_bProduitDataBase_SubClassFunctionality?.nom}_${data.nomImageFichieOuApellationDuCouleur}"
            )
    }


    val filteredA_ProduitsParCatalogueBsonId by derivedStateOf {
        bProduitDataBase_SubClassFunctionality.datasValue.filteredParCatalogueBsonId()
    }

    fun List<ArticlesBasesStatsTable>.filteredParCatalogueBsonId(): List<ArticlesBasesStatsTable> {
        val catalogueFilterId =
            zAppComptRepositoryComposable.currentAppCompt?.presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId

        val catalogues = B4CatalogueCategoriesRepository().associateBy { it.bsonObjectId }
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
}
