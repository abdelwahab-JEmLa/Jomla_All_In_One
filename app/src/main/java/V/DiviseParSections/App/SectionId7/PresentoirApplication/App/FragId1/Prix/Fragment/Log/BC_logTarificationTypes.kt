package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.DataBase.Models.OutputNoSqlModel
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Passive.strDateEtTempFromVidTimestamp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.ViewModel.TarificationViewModel

fun logTarificationTypes(
    viewModel: TarificationViewModel,
    types: List<OutputNoSqlModel.Produit.Client.TypeTarification>,
    isLastProduit: Boolean,
    isLastClient: Boolean,
) {
    val currentClient = viewModel.outputNoSqlFlow.value.produits
        .flatMap { it.clients }
        .find { client -> client.typeTarification.any { types.contains(it) } }

    if (currentClient != null) {
        val clientInfo = viewModel.getSqlClient(currentClient.id)

        types.forEachIndexed { typeIndex, type ->
            val isLastType = typeIndex == types.size - 1
            val typePrefix = when {
                isLastProduit && isLastClient -> V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log.TreePrefix.Type4.get(isLastType)
                else -> V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Log.TreePrefix.Type5.get(isLastType)
            }

            val (typeDate, typeTime) = strDateEtTempFromVidTimestamp(type.vidTimestamp)
            val typeInfo = viewModel.getSqlTypeTarification(type.id)

            val isActive = clientInfo?.idActiveTypeTarificationDataBase == type.id
            val activeStatus = if (isActive) " [ACTIVE]" else ""

            val typeInfos = StringBuilder().apply {
                append(typePrefix)
                append(" Tarification Type : ")
                append(type.id)
                append("=(${typeInfo?.typeTarificationEnum ?: "Unknown"})")
                append(activeStatus)
                append(" , Date: ")
                append(typeDate)
                append(" Time: ")
                append(typeTime)
                append(" (${type.PrixsCurrency.size} currencies)")
            }.toString()

            println(typeInfos)

            logPrixCurrencies(type.PrixsCurrency, isLastProduit, isLastClient, isLastType)
        }
    }
}
