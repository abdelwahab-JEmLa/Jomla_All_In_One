package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test.DataBase.B.FireBase.Model

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
    // This should match the structure expected after conversion from testDatas()
    return ProduitNoSqlDataBase(
        produits = listOf(
            // Produit 1: "Produit Optila"
            ProduitNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),  // Exact timestamp doesn't matter in test
                infosId = 1,
                clientAchteurs = listOf(
                    // Client 1: "ClientAchteur Abderrahman"
                    ProduitNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 1,
                        typeTarification = listOf(
                            // Type Tarification 1: "ParBenifice"
                            ProduitNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = 1,
                                PrixsCurrency = listOf(
                                    // Two prices for this combination
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
                    // Client 2: "ClientAchteur Beta"
                    ProduitNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 2,
                        typeTarification = listOf(
                            // Type Tarification 2: "Historique"
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
            // Produit 2: "Produit Hnina"
            ProduitNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),
                infosId = 2,
                clientAchteurs = listOf(
                    // Client 1: "ClientAchteur Abderrahman"
                    ProduitNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 1,
                        typeTarification = listOf(
                            // Type Tarification 1: "ParBenifice"
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
            // Produit 3: "Produit kemya"
            ProduitNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),
                infosId = 3,
                clientAchteurs = listOf(
                    // Client 1: "ClientAchteur Abderrahman"
                    ProduitNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 1,
                        typeTarification = listOf(
                            // Type Tarification 3: "LeMaxPrixArrive"
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
