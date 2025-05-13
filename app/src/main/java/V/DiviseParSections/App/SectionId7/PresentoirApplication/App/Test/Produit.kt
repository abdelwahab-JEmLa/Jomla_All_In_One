package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.Test

data class Produit(
    val id: Long,
    val timestamp: Long,
    val infos: ProduitInfos,
    val clients: List<Client>,
) {
    data class ProduitInfos(
        val nom: String = ""
    )

    data class Client(
        val id: Long,
        val timestamp: Long,
        val infos: ClientInfos,
        val typesTarification: List<TypeTarification>,
    ) {
        data class ClientInfos(
            val nom: String = ""
        )

        data class TypeTarification(
            val id: Long,
            val timestamp: Long,
            val infos: Infos,
            val PrixsCurrency: List<Prix>,
        ) {
            data class Infos(
                val nom: String = "",
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
