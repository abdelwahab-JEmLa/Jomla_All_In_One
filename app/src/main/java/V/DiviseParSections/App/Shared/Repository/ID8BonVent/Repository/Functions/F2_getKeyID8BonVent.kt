package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.MID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.MID2ClientRepository.Repository.HClientRepository
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.GBonVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved

fun getKeyID8BonVent(
    clientOldID: Long? = null,
    etate: GBonVent.EtateActuellementEst? = null,
    parametresAppComptNonSaved: ParametresAppComptNonSaved,
    hClientRepository: HClientRepository,
): String {
    val activePeriodKeyByParent = parametresAppComptNonSaved.activePeriodKeyByParent
    val keyModelToOnVentHVentPeriodKeyByParent =
        Z_AppCompt.keyModelValID7VentParent + "-" + activePeriodKeyByParent

    val keyModelToClientKeyByParent =
        clientOldID?.let {
            "--" + HClientInfos.keyModel + "-" + hClientRepository.datasValue.find { it.id == clientOldID }?.getTempKeyByParent()
        }
    val keyModelToEtateKey =
        etate?.let { "--" + GBonVent.EtateActuellementEst.keyModel + "-" + it.name }
            ?: ""

    return ("$keyModelToOnVentHVentPeriodKeyByParent$keyModelToClientKeyByParent$keyModelToEtateKey")
        .withOutFireBaseInvalidCharacters()
}
