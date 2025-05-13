package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

data class Produit(
    val id: Long,
    val timestamp: Long,
    val infos: ProduitInfos,
    val cesStatuesMutable: CesStatuesMutable,
    val clients: List<Client>,
) {
    data class ProduitInfos(
        val nom: String = ""
    )
    data class CesStatuesMutable(
        val cActiveDonsSonListParent: Boolean =false,
    )

    data class Client(
        val id: Long,
        val timestamp: Long,
        val infos: ClientInfos,
        val cesStatuesMutable: CesStatuesMutable,
        val typesTarification: List<TypeTarification>,
    ) {
        data class ClientInfos(
            val nom: String = ""
        )
        data class CesStatuesMutable(
            val cActiveDonsSonListParent: Boolean =false,
        )

        data class TypeTarification(
            val id: Long,
            val timestamp: Long,
            val infos: Infos,
            val PrixsCurrency: List<Prix>,
        ) {
            data class Infos(
                var type : TypeTarificationEnum = TypeTarificationEnum.NonDefini,
            )
            enum class TypeTarificationEnum {
                NonDefini,
                ParBenifice,
                Historique,
                LeMaxPrixArrive
            }

            data class Prix(
                val id: Long,
                val timestamp: Long,
                val valeur: Double,
            )
        }
    }
}
