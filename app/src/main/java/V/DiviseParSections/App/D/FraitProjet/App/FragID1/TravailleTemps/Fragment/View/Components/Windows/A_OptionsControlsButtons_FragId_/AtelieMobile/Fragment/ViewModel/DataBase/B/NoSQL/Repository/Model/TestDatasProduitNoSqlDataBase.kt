package V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.B.NoSQL.Repository.Model

import V.DiviseParSections.App.D.FraitProjet.App.FragID1.TravailleTemps.Fragment.View.Components.Windows.A_OptionsControlsButtons_FragId_.AtelieMobile.Fragment.ViewModel.DataBase.A.SQL.Models.Function.createTimestamp

fun testDatasProduitNoSqlDataBase(): ProduitsNoSqlDataBase {   //<--
//TODO(1): ici c a marche 
    return ProduitsNoSqlDataBase(
        produits = listOf(
            ProduitsNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),
                infosId = 1,
                clientAchteurs = listOf(
                    ProduitsNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 1,
                        typeTarification = listOf(
                            ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = 1,
                                PrixsCurrency = listOf(
                                    ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
                                        vidTimestamp = createTimestamp(
                                            day = 1,
                                            hour = 12,
                                            minute = 30
                                        ),
                                        valeur = 20.99
                                    ),
                                    ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
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
                    ProduitsNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 2,
                        typeTarification = listOf(
                            ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = 2,
                                PrixsCurrency = listOf(
                                    ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
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
            ProduitsNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),
                infosId = 2,
                clientAchteurs = listOf(
                    ProduitsNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 1,
                        typeTarification = listOf(
                            ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = 1,
                                PrixsCurrency = listOf(
                                    ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
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
            ProduitsNoSqlDataBase.Produit(
                vidTimestamp = System.currentTimeMillis(),
                infosId = 3,
                clientAchteurs = listOf(
                    ProduitsNoSqlDataBase.Produit.ClientAchteur(
                        vidTimestamp = System.currentTimeMillis(),
                        infosId = 1,
                        typeTarification = listOf(
                            ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification(
                                vidTimestamp = System.currentTimeMillis(),
                                infosId = 3,
                                PrixsCurrency = listOf(
                                    ProduitsNoSqlDataBase.Produit.ClientAchteur.TypeTarification.Prix(
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
