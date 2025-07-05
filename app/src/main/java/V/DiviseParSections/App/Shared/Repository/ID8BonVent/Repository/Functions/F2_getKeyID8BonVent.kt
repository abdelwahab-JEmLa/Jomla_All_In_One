package V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Functions

import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.A.Base.AGetter.Companion.withOutFireBaseInvalidCharacters
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ParametresAppComptNonSaved

fun getKeyID8BonVent(
    clientOldID: Long? = null,
    etate: M8BonVent.EtateActuellementEst? = null,
    parametresAppComptNonSaved: ParametresAppComptNonSaved,
    hClientRepository: Repo2Client,
): String {
    val activePeriodKeyByParent = parametresAppComptNonSaved.keyIdId7VentPeriod
    val keyModelToOnVentHVentPeriodKeyByParent =
        Z_AppCompt.keyModelValID7VentParent + "-" + activePeriodKeyByParent

    val keyModelToClientKeyByParent =
        clientOldID?.let {
            "--" + HClientInfos.keyModel + "-" + hClientRepository.datasValue.find { it.id == clientOldID }?.getTempKeyByParent()
        }
    val keyModelToEtateKey =
        etate?.let { "--" + M8BonVent.EtateActuellementEst.keyModel + "-" + it.name }
            ?: ""

    return ("$keyModelToOnVentHVentPeriodKeyByParent$keyModelToClientKeyByParent$keyModelToEtateKey")
        .withOutFireBaseInvalidCharacters()
}
