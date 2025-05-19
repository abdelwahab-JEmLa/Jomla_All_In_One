package Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model

import Fragment.ViewModel.DataBase.A.SQL.Models.Function.createTimestamp

fun testDatasProduitNoSqlDataBase(): ProduitNoSqlDataBase {   //<--
//TODO(1): ici c a marche 
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
