package V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download

import V.DiviseParSections.App.Shared.Repository.A.Base.A.Bsetter.Helper.DebugsTests.getSemanticsTag
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.ParametresAppComptNonSaved
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.Repo10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID1C2CouleurProduitInfos.Repository.Repo3CouleurProduitInfos
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.Repo2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.Repo8BonVent
import V.DiviseParSections.App.Shared.Repository.ID9AppCompt.Repository.Repo9AppCompt
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.M13TarificationInfos.TypeChoisi
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
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
) {
    // Use collectAsState or observe the repository state directly
    val currentM9AppCompt by derivedStateOf {
        repo9AppCompt.datasValue.firstOrNull { it.keyID == ParametresAppComptNonSaved().currentAppComptKeyID }
    }

    val onVentM8BonVent by derivedStateOf {
        repo8BonVent.datasValue.find { it.keyID == currentM9AppCompt?.onVentM8BonVentKey }
    }
    val activeOnVentM2ClientInfos by derivedStateOf {
        repo2Client.datasValue.find {
            it.keyID == (onVentM8BonVent?.keyID ?: "")
        }
    }
    val defaultM8BonVent by derivedStateOf {
        M8BonVent(
            nomClientConcerned = "Default Data",
            parentKeyId9AppComptInfos = ParametresAppComptNonSaved().currentAppComptKeyID,
            parentDebugNameId9AppComptInfos = ParametresAppComptNonSaved().debugNameId9AppComptInfos,
            parentM7VentPeriodKeyId = ParametresAppComptNonSaved().keyIdId7VentPeriod,
            parentM7VentPeriodDebugInfos = ParametresAppComptNonSaved().debugNameId7VentPeriod,
        )
    }

    val onVentM2ClientInfos by derivedStateOf {
        val targetKey = onVentM8BonVent?.parentM2ClientInfosKey
        repo2Client.datasValue.find { it.keyID == targetKey }
    }

    val defaultM3CouleurProduitInfos by derivedStateOf {
        onVentM8BonVent?.let {
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

    val onVentM3CouleurProduitInfos by derivedStateOf {
        val targetKey = currentM9AppCompt?.onVentM3CouleurProduitInfosKeyID
        repo10OperationVentCouleur.datasValue.find { it.keyID == targetKey }
    }

    val onVent_ListM10VentCouleur_FiltrePar_OV_M8BonVent by derivedStateOf {
        repo10OperationVentCouleur.datasValue.filter {
            it.parentM8BonVentKeyId == (currentM9AppCompt?.onVentM8BonVentKey ?: "")
        }
    }

    val focused_M1ProduitInfos_Pour_PrixDifineur by derivedStateOf {
        repoM1ProduitInfos.datasValue.find { it.keyID == currentM9AppCompt?.focusedAuPrixDifineurM1ProduitInfosKeyId }
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
                                parentM2ClientInfosDebugName
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
