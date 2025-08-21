package V.DiviseParSections.App.Shared.Repository.A.Base

import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.PrintReceiptHandler_Juil
import V.DiviseParSections.App.Shared.Modules.Ui.B.UI.DebugKey
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Set.Upload.FocusedValuesSetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Set.Upload.RepositorysMainSetter
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.Repo11AchatOperation.Repository.M11AchatOperation
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.M14VentPeriode
import Z_CodePartageEntreApps.Modules.B_RecordingHandler.IRecordingHandler
import Z_CodePartageEntreApps.Modules.C_PlayAndRecordeHandler.AudioRecorderAndPlayHandler
import Z_CodePartageEntreApps.Modules.FragmentNavigationHandler

class ACentralFacade(
    val repositorysMainGetter: RepositorysMainGetter,
    val repositorysMainSetter: RepositorysMainSetter,
    val focusedActiveValuesFacade: FocusedActiveValuesFacade,
    val modulesCentral: ModulesCentral
)

class FocusedActiveValuesFacade(val focusedValuesGetter: FocusedValuesGetter, val focusedValuesSetter: FocusedValuesSetter)

class ModulesCentral(
    val printReceiptHandler: PrintReceiptHandler_Juil,
    val recordingHandler: IRecordingHandler,
    val fragmentNavigationHandler: FragmentNavigationHandler,
    val audioRecorderAndPlayHandler: AudioRecorderAndPlayHandler,
    val debugKey: DebugKey
)

object functions_central{
    fun runtime_throw_Erreur_Pour_Regle_Le_Real_Bug(parent_Lenceur: String?=null): Nothing {
        throw RuntimeException(
            "RuntimeException $parent_Lenceur"
        )
    }
}
object filters_Central{
     fun filterAchatOperations(
        aCentralFacade: ACentralFacade,
    ): List<M11AchatOperation> {
        val activeCentralValues =
            aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.active_Central_Values
        val repo11AchatOperation = aCentralFacade.repositorysMainGetter.repo11AchatOperation
        val achatOperations = repo11AchatOperation.datasValue
        val repo10OperationVentCouleur = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur
        val repo8BonVent = aCentralFacade.repositorysMainGetter.repo8BonVent

        var filteredData = achatOperations

        activeCentralValues.active_M14VentPeriode_AuFilterAchats?.let { period ->
            filteredData = filteredData.filter {
                it.parent_M14VentPeriod_KeyID == period.keyID
            }
        }

        activeCentralValues.active_M15Grossist_AuFilterAchats?.let { grossist ->
            filteredData = filteredData.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
        }

        activeCentralValues.active_M2Client_AuFilterAchats?.let { client ->
            filteredData = filteredData.filter { achat ->
                val sales =
                    achat.get_list_v_Depuit_joinedStringKeys(repo10OperationVentCouleur.datasValue)
                sales.any { sale ->
                    if (sale.parentClientInfosKeyID.isNotBlank() && sale.parentClientInfosKeyID == client.keyID) {
                        return@any true
                    }
                    val bonVent = repo8BonVent.datasValue
                        .find { it.keyID == sale.parent_M8BonVent_KeyId }
                    bonVent?.parent_M2Client_KeyID == client.keyID
                }
            }
        }

        activeCentralValues.active_M1Produit_AuFilterAchats?.let { product ->
            filteredData = filteredData.filter { achat ->
                val sales =
                    achat.get_list_v_Depuit_joinedStringKeys(repo10OperationVentCouleur.datasValue)
                sales.any { sale ->
                    sale.parent_M1Produit_KeyId == product.keyID
                }
            }
        }

        return filteredData
    }
    data class ClientSalesSummary(
        val totalClients: Int,
        val totalQuantity: Int,
        val totalSalesValue: Double
    )
    fun calculateClientSalesSummary(
        aCentralFacade: ACentralFacade,
        activePeriod: M14VentPeriode?,
    ): ClientSalesSummary {
        val allClients = aCentralFacade.repositorysMainGetter.repo2Client.datasValue
        val allBonVents = aCentralFacade.repositorysMainGetter.repo8BonVent.datasValue
        val allVentOperations = aCentralFacade.repositorysMainGetter.repo10OperationVentCouleur.datasValue
        var allAchatOperations = aCentralFacade.repositorysMainGetter.repo11AchatOperation.datasValue
        val activeGrossist = aCentralFacade.focusedActiveValuesFacade.focusedValuesGetter.active_Central_Values.active_M15Grossist_AuFilterAchats

        // Filter achat operations by active period AND grossist if they are selected
        activePeriod?.let { period ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M14VentPeriod_KeyID == period.keyID
            }
        }

        activeGrossist?.let { grossist ->
            allAchatOperations = allAchatOperations.filter {
                it.parent_M15Grossist_KeyID == grossist.keyID
            }
        }

        val clientIdsWithPurchases = allAchatOperations.flatMap { achatOperation ->
            val relatedVentOperations = achatOperation.get_list_v_Depuit_joinedStringKeys(allVentOperations)
            relatedVentOperations.mapNotNull { ventOperation ->
                val bonVent = allBonVents.find { it.keyID == ventOperation.parent_M8BonVent_KeyId }
                bonVent?.parent_M2Client_KeyID
            }
        }.toSet()

        val clientsWithPurchases = allClients.filter { client ->
            client.keyID in clientIdsWithPurchases
        }

        // Calculate aggregate totals
        var totalSalesValue = 0.0
        var totalQuantity = 0
        var totalUniqueProducts = 0

        clientsWithPurchases.forEach { client ->
            val clientBonVents = allBonVents.filter { it.parent_M2Client_KeyID == client.keyID }
            val clientBonVentIds = clientBonVents.map { it.keyID }.toSet()
            val clientVentOperations = allVentOperations.filter {
                it.parent_M8BonVent_KeyId in clientBonVentIds
            }

            val relevantVentOperations = clientVentOperations.filter { ventOperation ->
                allAchatOperations.any { achatOperation ->
                    achatOperation.get_list_v_Depuit_joinedStringKeys(listOf(ventOperation)).isNotEmpty()
                }
            }

            totalQuantity += relevantVentOperations.sumOf { it.quantity }

            val clientSalesValue = relevantVentOperations.filter {
                it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
            }.sumOf {
                val parentM13TarificationPrix = aCentralFacade.repositorysMainGetter
                    .find_M13Tarification_By_KeyID(it.parentM13TarificationKeyID)?.prixCurrency ?: 0.0
                it.quantity * parentM13TarificationPrix
            }
            totalSalesValue += clientSalesValue

            val relatedAchatOperations = allAchatOperations.filter { achatOperation ->
                val relatedVentOps = achatOperation.get_list_v_Depuit_joinedStringKeys(clientVentOperations)
                relatedVentOps.isNotEmpty()
            }
            totalUniqueProducts += relatedAchatOperations.map { it.parent_M1Produit_KeyID }.toSet().size
        }

        return ClientSalesSummary(
            totalClients = clientsWithPurchases.size,
            totalQuantity = totalQuantity,
            totalSalesValue = totalSalesValue
        )
    }

}

