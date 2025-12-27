package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.ClickableFieldWithIcon
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SOUAR
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun TakiyimEvaluationSection(
    etudiant: M19Etudiant,
    onShowTakiyimDialog: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject()
) {
    Column {
        Text(
            text = "⭐ تقييم الاجتهاد",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                ClickableFieldWithIcon(
                    label = "تقييم الاجتهاد:",
                    value = etudiant.dernier_takyim_dabte.arabicName,
                    onClick = onShowTakiyimDialog,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))
    }
}

fun processTakiyimEvaluation(
    etudiant: M19Etudiant,
    selectedTakiyim: M19Etudiant.Takiyim,
    aCentralFacade: ACentralFacade
): M19Etudiant {
    val minAya = if (etudiant.dernier_Soura_sater == 0) {
        etudiant.dernier_Soura_Wassale_Laha.rakme_akher_aya
    } else {
        etudiant.dernier_Soura_sater
    }

    val ilaAya = if (etudiant.mokarrare_hifde_sater == 0) {
        etudiant.mokarrare_hifde.rakme_akher_aya
    } else {
        etudiant.mokarrare_hifde_sater
    }

    val observation = M20ObsarvationEtudion.get_default().copy(
        type = M20ObsarvationEtudion.Type.Tama_Hifdoha,
        etudiant_keyID = etudiant.keyID,
        min_soura = etudiant.dernier_Soura_Wassale_Laha,
        min_aya = minAya,
        ila_soura = etudiant.mokarrare_hifde,
        ila_aya = ilaAya,
        takyim = selectedTakiyim,
        parent_ousstad_key = etudiant.parent_ousstad_key
    )

    aCentralFacade.repositorysMainSetter.upsert_M20ObsarvationEtudion(observation)

    return if (selectedTakiyim != M19Etudiant.Takiyim.Lam_Yahfed) {
        val isCompletedSoura = etudiant.mokarrare_hifde.isNihaya(ilaAya)

        if (isCompletedSoura) {
            val nextSoura = getNextSoura(etudiant.mokarrare_hifde)
            etudiant.copy(
                dernier_Soura_Wassale_Laha = nextSoura,
                dernier_Soura_sater = 1,
                mokarrare_hifde = nextSoura,
                mokarrare_hifde_sater = 2,
                dernier_takyim_dabte = selectedTakiyim,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        } else {
            val rangeSize = ilaAya - minAya
            val newMin = ilaAya + 1
            val newIla = newMin + rangeSize

            etudiant.copy(
                dernier_Soura_Wassale_Laha = etudiant.mokarrare_hifde,
                dernier_Soura_sater = newMin,
                mokarrare_hifde = etudiant.mokarrare_hifde,
                mokarrare_hifde_sater = newIla,
                dernier_takyim_dabte = selectedTakiyim,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
        }
    } else {
        etudiant.copy(
            dernier_takyim_dabte = selectedTakiyim,
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }
}

private fun getNextSoura(currentSoura: SOUAR): SOUAR {
    val allSurahs = SOUAR.values()
    val currentIndex = allSurahs.indexOf(currentSoura)
    return if (currentIndex < allSurahs.size - 1) allSurahs[currentIndex + 1] else currentSoura
}
