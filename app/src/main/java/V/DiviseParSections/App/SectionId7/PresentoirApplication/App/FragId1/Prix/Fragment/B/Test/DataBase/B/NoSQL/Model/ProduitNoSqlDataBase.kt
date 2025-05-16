package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.B.NoSQL.Model

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.A.SQL.Models.createTimestamp

data class ProduitNoSqlDataBase(
    val produits: List<Produit>,
) {
    data class Produit(
        val vidTimestamp: Long,
        val infosId: Long,
        val clientAchteurs: List<ClientAchteur>,
    ) {
        data class ClientAchteur(
            val vidTimestamp: Long,
            val infosId: Long,
            val typeTarification: List<TypeTarification>,
        ) {
            data class TypeTarification(
                val vidTimestamp: Long,
                val infosId: Long,
                val PrixsCurrency: List<Prix>,
            ) {
                data class Prix(
                    val vidTimestamp: Long,
                    val valeur: Double,
                )
            }
        }
    }
}

fun testDatasProduitNoSqlDataBase(): ProduitNoSqlDataBase {
    return ProduitNoSqlDataBase(
        produits = listOf(
            ProduitNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),
                infosId = 1,
                clientAchteurs = listOf(
                    ProduitNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 1,
                        typeTarification = listOf(
                            ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = 1,
                                PrixsCurrency = listOf(
                                    ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                        vidTimestamp = createTimestamp(
                                            day = 1,
                                            hour = 12,
                                            minute = 30
                                        ),
                                        valeur = 20.99
                                    ),
                                    ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                        vidTimestamp = createTimestamp(
                                            day = 5,
                                            hour = 13,
                                            minute = 30
                                        ),
                                        valeur = 25.50
                                    )
                                )
                            )
                        )
                    ),
                    ProduitNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 2,
                        typeTarification = listOf(
                            ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = 2,
                                PrixsCurrency = listOf(
                                    ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                        vidTimestamp = createTimestamp(
                                            day = 5,
                                            hour = 14,
                                            minute = 30
                                        ),
                                        valeur = 9.75
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            ProduitNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),
                infosId = 2,
                clientAchteurs = listOf(
                    ProduitNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 1,
                        typeTarification = listOf(
                            ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = 1,
                                PrixsCurrency = listOf(
                                    ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                        vidTimestamp = createTimestamp(
                                            day = 6,
                                            hour = 3,
                                            minute = 30
                                        ),
                                        valeur = 15.25
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            ProduitNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),
                infosId = 3,
                clientAchteurs = listOf(
                    ProduitNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 1,
                        typeTarification = listOf(
                            ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = 3,
                                PrixsCurrency = listOf(
                                    ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                        vidTimestamp = createTimestamp(
                                            day = 6,
                                            hour = 4,
                                            minute = 30
                                        ),
                                        valeur = 14.80
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}
