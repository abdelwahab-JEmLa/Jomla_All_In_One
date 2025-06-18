package V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository

import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.D_AchatOperation.Repository.E_AchatOperationComposeRepositoryProtoJuin17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_AppCompt.Repository.Z_AppComptComposeRepositoryProtoJuin17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A1.Proto.Juin17.Proto.Z_DatabaseInitializationManager
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A2_Passive.B_ClientsStateCompoRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A2_Passive.D_TransactionCommercialCompoRepository
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.A_ProduitDataBaseComposeRepositoryPJ17
import V.DiviseParSections.App.SectionID9.EditeBaseDonne.App.FragId1.Fragment.ViewModel.Repository.A_ProduitDataBase.Repository.ArticlesBasesStatsTable
import Z_CodePartageEntreApps.DataBase.Juin3.Proto.A_MasterRepositorysGrpProtoJuin3
import Z_CodePartageEntreApps.Repository._1_3_TransactionCommercial.C3_TransactionCommercial
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Stable
class A_CentralCompoRepositoryProtoJuin9(
    private val context: Context,
    val databaseInitializationManager: Z_DatabaseInitializationManager,
    val comptAppState: Z_ComptAppStateCompoRepositoryProtoAvanJuin17,
    val appComptComposeRepositoryProtoJuin17: Z_AppComptComposeRepositoryProtoJuin17,

    val a_ProduitDataBaseComposeRepositoryPJ17: A_ProduitDataBaseComposeRepositoryPJ17,

    val a_MasterRepositorysGrpProtoJuin3: A_MasterRepositorysGrpProtoJuin3,
    val b3CategoriesCompoRepository: C_CategoriesCompoRepository,
    val clientsState: B_ClientsStateCompoRepository,
    val transactionCommercialState: D_TransactionCommercialCompoRepository,

    val d_AchatOperationComposeRepositoryPJ17: E_AchatOperationComposeRepositoryProtoJuin17,
) {
    private val composScope = CoroutineScope(Dispatchers.IO)

    private val _loadingProgress = mutableFloatStateOf(0f)
    val loadingProgress: Float? by derivedStateOf { _loadingProgress.floatValue }

    private val categoriesList: List<CategoriesTabelle>
        get() = b3CategoriesCompoRepository.datasValue

    val categoryGroupedSortedProducts: List<ArticlesBasesStatsTable> by derivedStateOf {
        val categoryMap = categoriesList.associateBy { it.id }
        val catalogues = B4CatalogueCategoriesRepository().associateBy { it.id.toLong() }

        val (regularProducts, orphanProducts) = a_ProduitDataBaseComposeRepositoryPJ17.datasValue.partition { product ->
            val categoryId = product.idParentCategorie ?: 0L
            val category = categoryMap[categoryId]
            val catalogueId = category?.catalogueParentId ?: 4L

            category != null &&
                    catalogueId != 4L &&
                    !category.nom.equals("NONE", ignoreCase = true)
        }

        val sortedRegular = regularProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                val catalogueId = category?.catalogueParentId ?: 4L
                catalogues[catalogueId]?.position ?: Int.MAX_VALUE
            }.thenBy { product ->
                val categoryId = product.idParentCategorie ?: 0L
                categoryMap[categoryId]?.position ?: Int.MAX_VALUE
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        val sortedOrphan = orphanProducts.sortedWith(
            compareBy<ArticlesBasesStatsTable> { product ->
                val categoryId = product.idParentCategorie ?: 0L
                val category = categoryMap[categoryId]
                category?.nom?.takeIf { !it.equals("NONE", ignoreCase = true) }
                    ?: "ZZZZZ_NO_CATEGORY"
            }.thenBy { it.positionDonSonCesFrereCategorieProduits }
                .thenBy { it.nom.lowercase() }
        )

        sortedRegular + sortedOrphan
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
        clientsState.findClientById(
            comptAppState.idClientOuSonMarqueMapEstOuvert
        ).also { transaction ->
            Log.d(
                "ouvertTransactionCommercial", "comptAppState.idClientOuSonMarqueMapEstOuvert" +
                        " ${comptAppState.idClientOuSonMarqueMapEstOuvert}"
            )
        }
    }

    val ouvertTransactionCommercial: C3_TransactionCommercial? by derivedStateOf {
        clientOuSonMarqueMapEstOuvert?.let {
            transactionCommercialState.getClientLastTransactionParEtate(
                it.id,
                C3_TransactionCommercial.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            ).also { transaction ->
                Log.d("ouvertTransactionCommercial", "Transaction ID: ${transaction?.vid}")
            }
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
