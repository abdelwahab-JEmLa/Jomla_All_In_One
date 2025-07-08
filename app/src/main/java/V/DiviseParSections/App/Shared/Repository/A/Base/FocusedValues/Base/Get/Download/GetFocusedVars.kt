package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.HClientInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Z_AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.Repo14VentPeriode.Repository.Repo14VentPeriode
import V.DiviseParSections.App.Shared.Repository.RepoM1ProduitInfos
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Stable
class GetFocusedVars(
    repo2Client: Repo2Client,
    repoM1ProduitInfos: RepoM1ProduitInfos,
    repo3CouleurProduitInfos: Repo3CouleurProduitInfos,
    repo8BonVent: Repo8BonVent,
    private val repo9AppCompt: Repo9AppCompt,
    private val repo10OperationVentCouleur: Repo10OperationVentCouleur,
    private val repo13TarificationInfos: Repo13TarificationInfos,
    private val repo14VentPeriode: Repo14VentPeriode,
) {
    // Use collectAsState or observe the repository state directly
    val currentM9AppCompt by derivedStateOf {
        repo9AppCompt.datasValue.firstOrNull { it.keyID == ParametresAppComptNonSaved().currentActiveFocucedM9AppComptKeyID }
    }

    val currentActiveFocuced_M14VentPeriode by derivedStateOf {
        repo14VentPeriode.datasValue.find { it.keyID == currentM9AppCompt?.current_OnVent_M14VentPeriode_KeyID }
    }

    val onVentM8BonVent by derivedStateOf {
        repo8BonVent.datasValue.find { it.keyID == currentM9AppCompt?.onVentM8BonVentKey }
    }

    // FIXED: Use the correct property to find the client
    val activeOnVentM2ClientInfos by derivedStateOf {
        repo2Client.datasValue.find {
            it.keyID == (onVentM8BonVent?.parentM2ClientInfosKey ?: "")
        }
    }

    fun getDefaultM8BonVent(): M8BonVent {
        return M8BonVent(
            parentKeyId9AppComptInfos = ParametresAppComptNonSaved().currentActiveFocucedM9AppComptKeyID,
            parentDebugNameId9AppComptInfos = ParametresAppComptNonSaved().currentActiveFocucedM9AppComptDebugInfos,
            parentM7VentPeriodKeyId =  (currentActiveFocuced_M14VentPeriode?.keyID?: "null"),
            parentM7VentPeriodDebugInfos = (currentActiveFocuced_M14VentPeriode?.get_DebugInfos()?: "null"),
        )
    }

    val onVentM2ClientInfos by derivedStateOf {
        val targetKey = onVentM8BonVent?.parentM2ClientInfosKey
        repo2Client.datasValue.find { it.keyID == targetKey }
    }

    fun getDefaultM10VentOperation(): M10OperationVentCouleur? {
        return onVentM8BonVent?.let {
            with(it) {
                M10OperationVentCouleur(
                    //---------------------------------Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
                    parentHVentPeriodKeyId = parentM7VentPeriodKeyId,
                    parentEVentPeriodDebugName = parentM7VentPeriodDebugInfos,
                    //---------------------------------Parent M8BonVent----------------------------------------------------------------------------------------------------------------------------------
                    parentM8BonVentKeyId = keyID,
                    parentM8BonVentDebugInfos = debugInfos,
                )
            }
        }

    }

    val onVentM10VentOperation by derivedStateOf {
        repo10OperationVentCouleur.datasValue.find { it.keyID == currentM9AppCompt?.onVentM3CouleurProduitInfosKeyID }
    }

    val onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent by derivedStateOf {
        repo10OperationVentCouleur.datasValue.filter {
            it.parentM8BonVentKeyId == (currentM9AppCompt?.onVentM8BonVentKey ?: "")
        }
    }

    val focused_M1ProduitInfos_Pour_PrixDifineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find { it.keyID == currentM9AppCompt?.activeFocuce_TariffPrixDifineur_M1ProduitKeyID }
    }

    val focused_ListM10OpeVentCouleur_Par_PD_M1Produit by derivedStateOf {
        onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent.filter {
            it.parentM1ProduitInfosKeyId == (focused_M1ProduitInfos_Pour_PrixDifineur?.keyID ?: "")
        }
    }

    val active_M1ProduitInfos_In_CurCompt_DialogQantity_Defineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find {
            it.keyID == (currentM9AppCompt?.dialogChoisireQuantityM1ProduitInfosKeyID ?: "")
        }
    }

    val focused_M13TarificationInfos_Pour_Produit by derivedStateOf {
        repo13TarificationInfos.datasValue.lastOrNull { tariff ->
            tariff.typeChoisi == TypeChoisi.DefiniParGerant2 &&
                    tariff.parentM1ProduitInfosKeyId == (focused_M1ProduitInfos_Pour_PrixDifineur?.keyID
                ?: "")
        }
    }

    val activeDialogSearchM1Produit by derivedStateOf {
        currentM9AppCompt?.activeDialogSearchM1Produit ?: false
    }

    fun get_By_Client_Edited_M8BonVent_Et_M9CurrComptFacade(
        m2Client: HClientInfos,
        newEtate: M8BonVent.EtateActuellementEst = M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
    ) = get_By_Client_Edited_M8BonVent_Et_M9CurrCompt(
        m2Client, getDefaultM8BonVent(), onVentM8BonVent, currentM9AppCompt, newEtate
    )

    companion object {
        @SuppressLint("ModifierFactoryUnreferencedReceiver")
        fun Modifier.getSemanticsTagFocucedVars(getter: GetFocusedVars): Modifier {
            val map = buildMap {
                with(getter) {
                    put(
                        "onVentM2ClientInfos",
                        onVentM2ClientInfos?.let {
                            with(it) {
                                nom
                            }
                        } ?: "null"
                    )
                    put(
                        "onVentM8BonVent",
                        onVentM8BonVent?.let {
                            with(it) {
                                "$parentM2ClientInfosDebugName/$etateActuellementEst"
                            }
                        } ?: "null"
                    )
                    put(
                        "focused_M1ProduitInfos_Pour_PrixDifineur", getter
                            .focused_M1ProduitInfos_Pour_PrixDifineur?.let {
                                it.nom + it.keyID
                            } ?: "null")

                    put(
                        "onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent",
                        getter.onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent.map {
                            "${it.parentM1ProduitDebugInfos} / ${it.parentM1ProduitInfosKeyId}"
                        }
                    )
                    put(
                        "focused_ListM10OpeVentCouleur_Par_PD_M1Produit",
                        getter.focused_ListM10OpeVentCouleur_Par_PD_M1Produit.map { it.getDebugInfos() }
                    )
                }
            }

            return map.entries.foldIndexed(this) { index, modifier, (key, value) ->
                modifier.getSemanticsTag(value, key, index + 6)
            }
        }
    }
}

fun get_By_Client_Edited_M8BonVent_Et_M9CurrCompt(
    m2Client: HClientInfos,
    defaultM8BonVent: M8BonVent,
    onVentM8BonVent: M8BonVent?,
    currentM9AppCompt: Z_AppCompt?,
    newEtate: M8BonVent.EtateActuellementEst,
): Pair<M8BonVent, Z_AppCompt?> {
    val onVentM8BonVentWithDefault = onVentM8BonVent ?: defaultM8BonVent

    val editedM8BonVent = onVentM8BonVentWithDefault.copy(
        debugInfos = m2Client.nom,
        parentM2ClientInfosKey = m2Client.keyID,
        parentM2ClientInfosDebugName = m2Client.nom,
        etateActuellementEst = newEtate
    )

    val editedM9CurrCompt = currentM9AppCompt?.copy(
        onVentM8BonVentKey = editedM8BonVent.keyID,
        onVentM8BonVentDebugInfos = editedM8BonVent.debugInfos
    )
    return Pair(editedM8BonVent, editedM9CurrCompt)
}
