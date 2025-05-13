package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import java.util.Calendar

class PreviewProvider : PreviewParameterProvider<List<TypeTarification>> {
    override val values = sequenceOf(
        listOf(
            TypeTarification(
                id = 1L,
                timestamp = createTimestamp(day = 13, month = 5, hour = 10, minute = 30),
                infos = TypeTarification.Infos(type = TypeTarification.TypeTarificationEnum.ParBenifice),
                cesStatuesMutable = TypeTarification.CesStatuesMutable(cActiveDonsSonListParent = true),
                parent = TypeTarification.Parent(
                    produit = Produit(
                        id = 1L,
                        timestamp = createTimestamp(day = 13, month = 5, hour = 10, minute = 30),
                        infos = Produit.ProduitInfos(nom = "Produit 1"),
                        cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                        clients = listOf(
                            Produit.Client(
                                id = 1L,
                                timestamp = createTimestamp(day = 13, month = 5, hour = 10, minute = 30),
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
                        timestamp = createTimestamp(day = 13, month = 5, hour = 9, minute = 30),
                        valeur = 19.99
                    ),
                    TypeTarification.Prix(
                        id = 2L,
                        timestamp = createTimestamp(day = 13, month = 5, hour = 10, minute = 30),
                        valeur = 24.99
                    )
                )
            ),
            TypeTarification(
                id = 3L,
                timestamp = createTimestamp(day = 12, month = 5, hour = 22, minute = 30),
                infos = TypeTarification.Infos(type = TypeTarification.TypeTarificationEnum.LeMaxPrixArrive),
                cesStatuesMutable = TypeTarification.CesStatuesMutable(cActiveDonsSonListParent = true),
                parent = TypeTarification.Parent(
                    produit = Produit(
                        id = 3L,
                        timestamp = createTimestamp(day = 12, month = 5, hour = 22, minute = 30),
                        infos = Produit.ProduitInfos(nom = "Produit 3"),
                        cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                        clients = listOf(
                            Produit.Client(
                                id = 3L,
                                timestamp = createTimestamp(day = 12, month = 5, hour = 22, minute = 30),
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
                        timestamp = createTimestamp(day = 12, month = 5, hour = 22, minute = 30),
                        valeur = 39.99
                    ),
                    TypeTarification.Prix(
                        id = 5L,
                        timestamp = createTimestamp(day = 13, month = 5, hour = 4, minute = 30),
                        valeur = 42.50
                    )
                )
            ),
            TypeTarification(
                id = 2L,
                timestamp = createTimestamp(day = 12, month = 5, hour = 10, minute = 30),
                infos = TypeTarification.Infos(type = TypeTarification.TypeTarificationEnum.Historique),
                cesStatuesMutable = TypeTarification.CesStatuesMutable(cActiveDonsSonListParent = true),
                parent = TypeTarification.Parent(
                    produit = Produit(
                        id = 2L,
                        timestamp = createTimestamp(day = 12, month = 5, hour = 10, minute = 30),
                        infos = Produit.ProduitInfos(nom = "Produit 2"),
                        cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                        clients = listOf(
                            Produit.Client(
                                id = 2L,
                                timestamp = createTimestamp(day = 12, month = 5, hour = 10, minute = 30),
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
                        timestamp = createTimestamp(day = 11, month = 5, hour = 10, minute = 30),
                        valeur = 15.50
                    )
                )
            )
        )
    )
}

fun createTimestamp(year: Int = 2025, month: Int = 5, day: Int, hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day, hour, minute, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}
