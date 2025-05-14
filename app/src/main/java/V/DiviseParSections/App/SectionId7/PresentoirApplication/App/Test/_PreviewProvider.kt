package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class _PreviewProvider : PreviewParameterProvider<List<Produit>> {
    override val values = sequenceOf(
        createSampleData()  // Call a method to create sample data
    )

    // No need to override count, the default implementation will work based on the sequence

    private fun createSampleData(): List<Produit> {
        return listOf(
            Produit(
                id = 1,
                timestamp = createTimestamp(10, 14, 30, 2025, 5),
                infos = Produit.ProduitInfos(nom = "Produit A"),
                cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                clients = listOf(
                    Produit.Client(
                        id = 1,
                        timestamp = createTimestamp(9, 14, 30, 2025, 5), // Yesterday
                        infos = Produit.Client.ClientInfos(nom = "ClientAchteur 1"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = false),
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
                        infos = Produit.Client.ClientInfos(nom = "ClientAchteur Beta"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
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
                infos = Produit.ProduitInfos(nom = "Produit B"),
                cesStatuesMutable = Produit.CesStatuesMutable(),  // Added this
                clients = listOf(
                    Produit.Client(
                        id = 1,
                        timestamp = createTimestamp(10, 14, 30, 2025, 5),
                        infos = Produit.Client.ClientInfos(nom = "ClientAchteur Gamma"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(),  // Added this
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
                                    ),
                                    Produit.Client.TypeTarification.Prix(
                                        id = 2,
                                        timestamp = createTimestamp(10, 2, 0, 2025, 5),
                                        valeur = 300.99
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            Produit(
                id = 3,
                timestamp = createTimestamp(10, 9, 15, 2025, 5),
                infos = Produit.ProduitInfos(nom = "Produit C"),
                cesStatuesMutable = Produit.CesStatuesMutable(),  // Added this
                clients = listOf(
                    Produit.Client(
                        id = 1,
                        timestamp = createTimestamp(10, 10, 0, 2025, 5),
                        infos = Produit.Client.ClientInfos(nom = "ClientAchteur Delta"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(),  // Added this
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
                        infos = Produit.Client.ClientInfos(nom = "ClientAchteur Epsilon"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(),  // Added this
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
    }
}
