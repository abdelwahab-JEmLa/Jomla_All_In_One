package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.ProduitNoSqlDataBase
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.A_ProduitInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.B_ClientInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.C_TypeTarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql.D_TarificationInfos
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.ViewModel._A.Models.Sql._InfosSqlDataBases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * DataSynchronizer est responsable de la synchronisation entre les données SQL et NoSQL
 * Cette classe convertit les données entre les deux formats et maintient leur cohérence
 */
class DataSynchronizer(
    private val sqlRepository: InfosSqlDataBasesRepository
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    
    // State flow pour les données NoSQL
    private val _noSqlDataFlow = MutableStateFlow(ProduitNoSqlDataBase(emptyList()))
    val noSqlDataFlow: StateFlow<ProduitNoSqlDataBase> = _noSqlDataFlow.asStateFlow()
    
    init {
        // Écouter les changements dans les données SQL et mettre à jour les données NoSQL
        coroutineScope.launch {
            combine(
                sqlRepository.produitRepository.modelListFlow,
                sqlRepository.clientRepository.modelListFlow,
                sqlRepository.typeTarificationRepository.modelListFlow,
                sqlRepository.tarificationRepository.modelListFlow
            ) { produits, clients, typeTarifications, tarifications ->
                // Convertir les données SQL en format NoSQL
                convertSqlToNoSql(produits, clients, typeTarifications, tarifications)
            }.collect { noSqlData ->
                _noSqlDataFlow.value = noSqlData
            }
        }
    }
    
    /**
     * Convertit les données SQL en format NoSQL
     */
    private fun convertSqlToNoSql(
        produits: List<A_ProduitInfos>,
        clients: List<B_ClientInfos>,
        typeTarifications: List<C_TypeTarificationInfos>,
        tarifications: List<D_TarificationInfos>
    ): ProduitNoSqlDataBase {
        val produitsList = produits.map { produit ->
            // Trouver toutes les tarifications pour ce produit
            val produitTarifications = tarifications.filter { it.idProduit == produit.id }
            
            // Regrouper par client
            val clientMap = produitTarifications.groupBy { it.idClient }
            
            val clientAcheteurs = clientMap.map { (clientId, clientTarifications) ->
                // Trouver les infos du client
                val clientInfo = clients.find { it.id == clientId } ?: B_ClientInfos(clientId)
                
                // Regrouper par type de tarification
                val typeTarificationMap = clientTarifications.groupBy { it.idTypeTarification }
                
                val typeTarifications = typeTarificationMap.map { (typeId, typeTarifications) ->
                    // Trouver les infos du type de tarification
                    val typeInfo = typeTarifications.find { it.vidTimestamp == typeId } ?: C_TypeTarificationInfos(typeId)
                    
                    // Créer la liste des prix
                    val prix = typeTarifications.map { tarif ->
                        ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                            vidTimestamp = tarif.vidTimestamp,
                            valeur = tarif.prixCurrency
                        )
                    }
                    
                    ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = typeInfo.id,  //->
                        //TODO(FIXME):Fix erreur Unresolved reference: id
                        PrixsCurrency = prix
                    )
                }
                
                ProduitNoSqlDataBase.Produit.ClientAchteur(
                    vidTimestamp = System.currentTimeMillis(),
                    infosId = clientInfo.id,
                    typeTarification = typeTarifications
                )
            }
            
            ProduitNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),
                infosId = produit.id,
                clientAchteurs = clientAcheteurs
            )
        }
        
        return ProduitNoSqlDataBase(produitsList)
    }
    
}
