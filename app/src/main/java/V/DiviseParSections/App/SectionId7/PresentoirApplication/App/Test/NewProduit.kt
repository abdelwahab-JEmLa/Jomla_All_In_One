package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

import java.util.Calendar

fun newData(data: List<TypeTarification>): TypeTarification {
    val newId = (data.maxOfOrNull { it.id } ?: 0) + 1
    val newData = TypeTarification(
        id = newId,
        timestamp = createTimestamp(day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            minute = Calendar.getInstance().get(Calendar.MINUTE)),
        infos = TypeTarification.Infos(type = TypeTarification.TypeTarificationEnum.NonDefini),
        cesStatuesMutable = TypeTarification.CesStatuesMutable(cActiveDonsSonListParent = true),
        parent = TypeTarification.Parent(
            produit = Produit(
                id = newId,
                timestamp = createTimestamp(day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    minute = Calendar.getInstance().get(Calendar.MINUTE)),
                infos = Produit.ProduitInfos(nom = "Nouveau Produit $newId"),
                cesStatuesMutable = Produit.CesStatuesMutable(cActiveDonsSonListParent = true),
                clients = listOf(
                    Produit.Client(
                        id = newId,
                        timestamp = createTimestamp(day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                            minute = Calendar.getInstance().get(Calendar.MINUTE)),
                        infos = Produit.Client.ClientInfos(nom = "Nouveau Client $newId"),
                        cesStatuesMutable = Produit.Client.CesStatuesMutable(cActiveDonsSonListParent = true),
                        typesTarification = emptyList()
                    )
                )
            )
        ),
        PrixsCurrency = listOf(
            TypeTarification.Prix(
                id = 1L,
                timestamp = createTimestamp(day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    minute = Calendar.getInstance().get(Calendar.MINUTE)),
                valeur = 0.0
            )
        )
    )
    return newData
}
