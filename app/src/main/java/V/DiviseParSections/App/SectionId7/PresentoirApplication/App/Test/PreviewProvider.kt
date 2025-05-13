package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class PreviewProvider : PreviewParameterProvider<List<TypeTarification>> {
    override val values = sequenceOf(
        listOf(
            TypeTarification(
                id = 1L,
                timestamp = System.currentTimeMillis(),
                infos = TypeTarification.Infos(type = TypeTarification.TypeTarificationEnum.ParBenifice),
                parent = TypeTarification.Parent(
                    produit = Produit(
                        id = 1L,
                        timestamp = System.currentTimeMillis(),
                        infos = Produit.ProduitInfos(nom = "Produit 1"),
                        cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                        clients = listOf(
                            Produit.Client(
                                id = 1L,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.ClientInfos(nom = "Client 1"),
                                cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                                typesTarification = emptyList()
                            )
                        )
                    )
                ),
                PrixsCurrency = listOf(
                    TypeTarification.Prix(
                        id = 1L,
                        timestamp = System.currentTimeMillis() - 3600000,
                        valeur = 19.99
                    ),
                    TypeTarification.Prix(
                        id = 2L,
                        timestamp = System.currentTimeMillis(),
                        valeur = 24.99
                    )
                )
            ),
            TypeTarification(
                id = 3L,
                timestamp = System.currentTimeMillis() - 43200000,
                infos = TypeTarification.Infos(type = TypeTarification.TypeTarificationEnum.LeMaxPrixArrive),
                parent = TypeTarification.Parent(
                    produit = Produit(
                        id = 3L,
                        timestamp = System.currentTimeMillis() - 43200000,
                        infos = Produit.ProduitInfos(nom = "Produit 3"),
                        cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                        clients = listOf(
                            Produit.Client(
                                id = 3L,
                                timestamp = System.currentTimeMillis() - 43200000,
                                infos = Produit.Client.ClientInfos(nom = "Client 3"),
                                cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                                typesTarification = emptyList()
                            )
                        )
                    )
                ),
                PrixsCurrency = listOf(
                    TypeTarification.Prix(
                        id = 4L,
                        timestamp = System.currentTimeMillis() - 43200000,
                        valeur = 39.99
                    ),
                    TypeTarification.Prix(
                        id = 5L,
                        timestamp = System.currentTimeMillis() - 21600000,
                        valeur = 42.50
                    )
                )
            ),
            TypeTarification(
                id = 2L,
                timestamp = System.currentTimeMillis() - 86400000,
                infos = TypeTarification.Infos(type = TypeTarification.TypeTarificationEnum.Historique),
                parent = TypeTarification.Parent(
                    produit = Produit(
                        id = 2L,
                        timestamp = System.currentTimeMillis() - 86400000,
                        infos = Produit.ProduitInfos(nom = "Produit 2"),
                        cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                        clients = listOf(
                            Produit.Client(
                                id = 2L,
                                timestamp = System.currentTimeMillis() - 86400000,
                                infos = Produit.Client.ClientInfos(nom = "Client 2"),
                                cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                                typesTarification = emptyList()
                            )
                        )
                    )
                ),
                PrixsCurrency = listOf(
                    TypeTarification.Prix(
                        id = 3L,
                        timestamp = System.currentTimeMillis() - 172800000,
                        valeur = 15.50
                    )
                )
            )
        )
    )
}
