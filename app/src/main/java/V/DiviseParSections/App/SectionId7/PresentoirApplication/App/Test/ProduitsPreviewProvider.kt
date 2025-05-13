package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ProduitsPreviewProvider : PreviewParameterProvider<List<Produit>> {
    override val values = sequenceOf(
        listOf(
            Produit(
                id = 1,
                timestamp = System.currentTimeMillis(),
                infos = Produit.ProduitInfos(nom = "Produit A"),
                clients = listOf(
                    Produit.Client(
                        id = 101,
                        timestamp = System.currentTimeMillis() - 86400000, // Yesterday
                        infos = Produit.Client.ClientInfos(nom = "Client Alpha"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1001,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.TypeTarification.Infos(nom = "Standard"),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10001,
                                        timestamp = System.currentTimeMillis(),
                                        valeur = 29.99
                                    ),
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10002,
                                        timestamp = System.currentTimeMillis() - 604800000, // Week ago
                                        valeur = 24.99
                                    )
                                )
                            ),
                            Produit.Client.TypeTarification(
                                id = 1002,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.TypeTarification.Infos(nom = "Premium"),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10003,
                                        timestamp = System.currentTimeMillis(),
                                        valeur = 49.99
                                    )
                                )
                            )
                        )
                    ),
                    Produit.Client(
                        id = 102,
                        timestamp = System.currentTimeMillis(),
                        infos = Produit.Client.ClientInfos(nom = "Client Beta"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1003,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.TypeTarification.Infos(nom = "Basic"),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10004,
                                        timestamp = System.currentTimeMillis(),
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
                timestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                infos = Produit.ProduitInfos(nom = "Produit B"),
                clients = listOf(
                    Produit.Client(
                        id = 103,
                        timestamp = System.currentTimeMillis(),
                        infos = Produit.Client.ClientInfos(nom = "Client Gamma"),
                        typesTarification = listOf(
                            Produit.Client.TypeTarification(
                                id = 1004,
                                timestamp = System.currentTimeMillis(),
                                infos = Produit.Client.TypeTarification.Infos(nom = "Enterprise"),
                                PrixsCurrency = listOf(
                                    Produit.Client.TypeTarification.Prix(
                                        id = 10005,
                                        timestamp = System.currentTimeMillis(),
                                        valeur = 99.99
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
