package V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.B.NoSQL.Repository

import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Z.Archive.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model.ProduitsNoSqlDataBase
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.C_TypeTarificationInfos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import V.DiviseParSections.App.SectionID9_AtelieModbile.Test.Repository.Models.B_ClientInfos as SqlClientInfos

suspend fun ConvertiseurNoSqlToSqlRepositorys.convertSqlToNoSql(
    onSuccess: () -> Unit = {}
): ProduitsNoSqlDataBase {
    return withContext(Dispatchers.IO) {
        try {
            val sqlDataList = sqlRepository.modelListFlow.first()

            if (sqlDataList.isEmpty()) {
                return@withContext ProduitsNoSqlDataBase(emptyList())
            }

            val sqlData = sqlDataList.first()

            val produitsList = sqlData.a_ProduitInfos.map { produit ->
                val produitTarifications = sqlData.d_TarificationInfos.filter { it.idProduit == produit.id }

                if (produitTarifications.isEmpty()) {
                    ProduitsNoSqlDataBase.Produit(
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

                            val tariffList = tarificationsForType.map { tarif ->
                                ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Tariff(
                                    vidTimestamp = tarif.vidTimestamp,
                                    valeur = tarif.prixCurrency
                                )
                            }

                            ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = typeInfo.id,
                                tariffsList = tariffList
                            )
                        }

                        ProduitsNoSqlDataBase.Produit.ClientAchteur(
                            vidTimestamp = System.currentTimeMillis(),
                            infosId = clientInfo.id,
                            typeTarification = typeTarifications
                        )
                    }

                    ProduitsNoSqlDataBase.Produit(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = produit.id,
                        clientAchteurs = clientAcheteurs
                    )
                }
            }

            val result = ProduitsNoSqlDataBase(produitsList)

            onSuccess()
            result
        } catch (e: Exception) {
            ProduitsNoSqlDataBase(emptyList())
        }
    }
}
