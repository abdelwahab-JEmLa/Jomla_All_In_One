package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.ClickableFieldWithIcon
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.SOUAR
import Application5.App.Repository.M20ObsarvationEtudion
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun TakiyimEvaluationSection(
    etudiant: M19Etudiant,
    onShowTakiyimDialog: () -> Unit,
    aCentralFacade: ACentralFacade = koinInject()
) {
    // Get the most recent observation for this student
    val repo20 = aCentralFacade.repositorysMainGetter.repo20ObsarvationEtudion

    val latestObservation by remember(etudiant.keyID) {
        derivedStateOf {
            repo20.datasValue
                .filter { it.etudiant_keyID == etudiant.keyID }
                .maxByOrNull { it.creationTimestamps }
        }
    }

    // Get selected moulahadat from the latest observation
    val selectedMoulahadat = remember(latestObservation) {
        latestObservation?.getMoulahadatList() ?: emptyList()
    }

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

                // Show moulahadat if there's a recent observation
                if (selectedMoulahadat.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "ملاحظات للإصلاح:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    selectedMoulahadat.forEach { moulahada ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• $moulahada",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))
    }
}

fun processTakiyimEvaluation(
    etudiant: M19Etudiant,
    selectedTakiyim: M19Etudiant.Takiyim,
    selectedMoulahadat: List<String>,
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

    // Create the observation with selected moulahadat
    val observation = M20ObsarvationEtudion.get_default().copy(
        type = M20ObsarvationEtudion.Type.Tama_Hifdoha,
        etudiant_keyID = etudiant.keyID,
        min_soura = etudiant.dernier_Soura_Wassale_Laha,
        min_aya = minAya,
        ila_soura = etudiant.mokarrare_hifde,
        ila_aya = ilaAya,
        takyim = selectedTakiyim,
        moulahadat_takyim_li_islahiha = selectedMoulahadat.joinToString(","),
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
