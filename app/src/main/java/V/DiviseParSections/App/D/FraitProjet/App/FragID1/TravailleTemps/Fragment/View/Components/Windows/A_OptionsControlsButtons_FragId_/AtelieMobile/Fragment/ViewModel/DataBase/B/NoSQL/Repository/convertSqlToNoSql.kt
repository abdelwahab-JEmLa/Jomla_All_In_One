package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.C_TypeTarificationInfos
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitNoSqlDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.B_ClientInfos as SqlClientInfos

suspend fun ConvertiseurNoSqlToSqlRepositorys.convertSqlToNoSql(
    onSuccess: () -> Unit = {}
): ProduitNoSqlDataBase {
    return withContext(Dispatchers.IO) {
        try {
            val sqlDataList = sqlRepository.modelListFlow.first()

            if (sqlDataList.isEmpty()) {
                return@withContext ProduitNoSqlDataBase(emptyList())
            }

            val sqlData = sqlDataList.first()

            val produitsList = sqlData.a_ProduitInfos.map { produit ->
                val produitTarifications = sqlData.d_TarificationInfos.filter { it.idProduit == produit.id }

                if (produitTarifications.isEmpty()) {
                    ProduitNoSqlDataBase.Produit(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = produit.id,
                        clientAchteurs = emptyList()
                    )
                } else {
                    val clientGroups = produitTarifications.groupBy { it.idClient }

                    val clientAcheteurs = clientGroups.map { (clientId, clientTarifications) ->
                        val clientInfo = sqlData.b_ClientInfosList.find { it.id == clientId }
                            ?: SqlClientInfos(
                                id = clientId,
                                nom = "Client $clientId"
                            )

                        val typeGroups = clientTarifications.groupBy { it.idTypeTarification }

                        val typeTarifications = typeGroups.map { (typeId, tarificationsForType) ->
                            val typeInfo = sqlData.c_TypeTarificationInfos.find { it.id == typeId }
                                ?: C_TypeTarificationInfos(id = typeId)

                            val prixList = tarificationsForType.map { tarif ->
                                ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                    vidTimestamp = tarif.vidTimestamp,
                                    valeur = tarif.prixCurrency
                                )
                            }

                            ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = typeInfo.id,
                                PrixsCurrency = prixList
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
            }

            val result = ProduitNoSqlDataBase(produitsList)

            onSuccess()
            result
        } catch (e: Exception) {
            ProduitNoSqlDataBase(emptyList())
        }
    }
}
