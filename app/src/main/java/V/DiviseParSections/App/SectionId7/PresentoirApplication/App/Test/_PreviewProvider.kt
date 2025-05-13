package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class _PreviewProvider : PreviewParameterProvider<List<Produit>> {
    override val values = sequenceOf(
        listOf(
            Produit(
                id = 1,
                timestamp = createTimestamp(2025, 5, 10, 14, 30),
                infos = Produit.ProduitInfos(nom = "Produit A"),
                cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                clients = listOf(
                    Produit.Client(
                        id = 101,
                        timestamp = createTimestamp(2025, 5, 9, 14, 30), // Yesterday
                        infos = Produit.Client.ClientInfos(nom = "Client Alpha"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1001,
                                timestamp = createTimestamp(2025, 5, 10, 14, 30),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.ParBenifice),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10001,
                                        timestamp = createTimestamp(2025, 5, 10, 14, 30),
                                        valeur = 29.99
                                    ),
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10002,
                                        timestamp = createTimestamp(2025, 5, 3, 14, 30), // Week ago
                                        valeur = 24.99
                                    )
                                )
                            ),
                            Produit.Client.TypeTarification(
                                id = 1002,
                                timestamp = createTimestamp(2025, 5, 10, 14, 30),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.Historique),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10003,
                                        timestamp = createTimestamp(2025, 5, 10, 14, 30),
                                        valeur = 49.99
                                    )
                                )
                            )
                        )
                    ),
                    Produit.Client(
                        id = 102,
                        timestamp = createTimestamp(2025, 5, 10, 14, 30),
                        infos = Produit.Client.ClientInfos(nom = "Client Beta"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = false),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1003,
                                timestamp = createTimestamp(2025, 5, 10, 14, 30),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.LeMaxPrixArrive),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10004,
                                        timestamp = createTimestamp(2025, 5, 10, 14, 30),
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
                timestamp = createTimestamp(2025, 5, 7, 14, 30), // 3 days ago
                infos = Produit.ProduitInfos(nom = "Produit B"),
                cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = false),
                clients = listOf(
                    Produit.Client(
                        id = 103,
                        timestamp = createTimestamp(2025, 5, 10, 14, 30),
                        infos = Produit.Client.ClientInfos(nom = "Client Gamma"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1004,
                                timestamp = createTimestamp(2025, 5, 10, 14, 30),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.ParBenifice),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10005,
                                        timestamp = createTimestamp(2025, 5, 10, 14, 30),
                                        valeur = 99.99
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            // Added another product as per TODO(1): cree d autre
            Produit(
                id = 3,
                timestamp = createTimestamp(2025, 5, 10, 9, 15),
                infos = Produit.ProduitInfos(nom = "Produit C"),
                cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                clients = listOf(
                    Produit.Client(
                        id = 104,
                        timestamp = createTimestamp(2025, 5, 10, 10, 0),
                        infos = Produit.Client.ClientInfos(nom = "Client Delta"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1005,
                                timestamp = createTimestamp(2025, 5, 10, 10, 30),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.ParBenifice),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10006,
                                        timestamp = createTimestamp(2025, 5, 10, 11, 0),
                                        valeur = 79.99
                                    )
                                )
                            )
                        )
                    ),
                    Produit.Client(
                        id = 105,
                        timestamp = createTimestamp(2025, 5, 10, 12, 0),
                        infos = Produit.Client.ClientInfos(nom = "Client Epsilon"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1006,
                                timestamp = createTimestamp(2025, 5, 10, 12, 30),
                                infos = Produit.Client.TypeTarification.Infos(type = Produit.Client.TypeTarification.TypeTarificationEnum.Historique),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10007,
                                        timestamp = createTimestamp(2025, 5, 10, 13, 0),
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
