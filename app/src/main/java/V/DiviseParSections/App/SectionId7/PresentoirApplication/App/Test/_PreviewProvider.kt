package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class _PreviewProvider : PreviewParameterProvider<List<Produit>> {
    override val values = sequenceOf(
        listOf(
            Produit(
                id = 1,
                timestamp = createTimestamp(10, 14, 30, 2025, 5),
                infos = Produit.ProduitInfos(nom = "Produit A"),
                clients = listOf(
                    Produit.Client(
                        id = 1,
                        timestamp = createTimestamp(9, 14, 30, 2025, 5), // Yesterday
                        infos = Produit.Client.ClientInfos(nom = "Client 1"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1,
                                timestamp = createTimestamp(10, 14, 30, 2025, 5),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.ParBenifice),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 1,
                                        timestamp = createTimestamp(10, 14, 30, 2025, 5),
                                        valeur = 29.99
                                    ),
                                    Produit.Client.TypeTarification.Prix(
                                        id = 2,
                                        timestamp = createTimestamp(3, 14, 30, 2025, 5), // Week ago
                                        valeur = 24.99
                                    )
                                )
                            ),
                            Produit.Client.TypeTarification(
                                id = 2,
                                timestamp = createTimestamp(10, 14, 30, 2025, 5),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.Historique),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 1,
                                        timestamp = createTimestamp(10, 14, 30, 2025, 5),
                                        valeur = 49.99
                                    )
                                )
                            )
                        )
                    ),
                    Produit.Client(
                        id = 2,
                        timestamp = createTimestamp(10, 14, 30, 2025, 5),
                        infos = Produit.Client.ClientInfos(nom = "Client Beta"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1,
                                timestamp = createTimestamp(10, 14, 30, 2025, 5),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.LeMaxPrixArrive),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 1,
                                        timestamp = createTimestamp(10, 14, 30, 2025, 5),
                                        valeur = 19.99
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            Produit(
                id = 2,
                timestamp = createTimestamp(7, 14, 30, 2025, 5), // 3 days ago
                cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                infos = Produit.ProduitInfos(nom = "Produit B"),
                clients = listOf(
                    Produit.Client(
                        id = 1,
                        timestamp = createTimestamp(10, 14, 30, 2025, 5),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                        infos = Produit.Client.ClientInfos(nom = "Client Gamma"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1,
                                timestamp = createTimestamp(10, 14, 30, 2025, 5),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.ParBenifice),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 1,
                                        timestamp = createTimestamp(10, 14, 30, 2025, 5),
                                        valeur = 99.99
                                    )
                                )
                            ),
                            Produit.Client.TypeTarification(
                                id = 2,
                                timestamp = createTimestamp(10, 14, 30, 2025, 5),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.Historique),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 1,
                                        timestamp = createTimestamp(10, 1, 0, 2025, 5),
                                        valeur = 200.99
                                    ) ,
                                    Produit.Client.TypeTarification.Prix(
                                        id = 2,
                                        timestamp = createTimestamp(10, 2, 0, 2025, 5),
                                        valeur = 300.99
                                    ) ,
                                )
                            ),
                        )
                    )
                )
            ),
            Produit(
                id = 3,
                timestamp = createTimestamp(10, 9, 15, 2025, 5),
                infos = Produit.ProduitInfos(nom = "Produit C"),
                clients = listOf(
                    Produit.Client(
                        id = 1,
                        timestamp = createTimestamp(10, 10, 0, 2025, 5),
                        infos = Produit.Client.ClientInfos(nom = "Client Delta"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1,
                                timestamp = createTimestamp(10, 10, 30, 2025, 5),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.ParBenifice),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10006,
                                        timestamp = createTimestamp(10, 11, 0, 2025, 5),
                                        valeur = 79.99
                                    )
                                )
                            )
                        )
                    ),
                    Produit.Client(
                        id = 2,
                        timestamp = createTimestamp(10, 12, 0, 2025, 5),
                        infos = Produit.Client.ClientInfos(nom = "Client Epsilon"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1,
                                timestamp = createTimestamp(10, 12, 30, 2025, 5),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.Historique),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 1,
                                        timestamp = createTimestamp(10, 13, 0, 2025, 5),
                                        valeur = 59.99
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
