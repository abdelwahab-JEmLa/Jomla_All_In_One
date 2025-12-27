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
                    onClick = {
                        /*
                         * When clicking to evaluate (Takiyim):
                         *
                         * Example 1: Student memorized الناس (1) → الناس (2)
                         *   Range size = 2 - 1 = 1 (difference between ila and min)
                         *   If NOT لم يحفظ → New min = old ila + 1 = 3
                         *                    → New ila = new min + rangeSize = 3 + 1 = 4
                         *                    → New range: الناس (3) → الناس (4)
                         *
                         * Example 2: Student memorized الناس (3) → الناس (5)
                         *   Range size = 5 - 3 = 2
                         *   If NOT لم يحفظ → New min = 6, New ila = 6 + 2 = 8
                         *                    → New range: الناس (6) → الناس (8)
                         *
                         * Example 3: Student completed الناس (4) → الناس (نهاية السورة = 6)
                         *   If NOT لم يحفظ → old ila is nihaya, so jump to next soura
                         *                    → New range: الفلق (1) → الفلق (2)
                         */

                        onShowTakiyimDialog()

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
                            takyim = etudiant.dernier_takyim_dabte,
                            parent_ousstad_key = etudiant.parent_ousstad_key
                        )

                        aCentralFacade.repositorysMainSetter.upsert_M20ObsarvationEtudion(observation)

                        if (etudiant.dernier_takyim_dabte != M19Etudiant.Takiyim.Lam_Yahfed) {
                            val isCompletedSoura = etudiant.mokarrare_hifde.isNihaya(ilaAya)

                            val updatedEtudiant = if (isCompletedSoura) {
                                val nextSoura = getNextSoura(etudiant.mokarrare_hifde)

                                etudiant.copy(
                                    dernier_Soura_Wassale_Laha = nextSoura,
                                    dernier_Soura_sater = 1,
                                    mokarrare_hifde = nextSoura,
                                    mokarrare_hifde_sater = 2,
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
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )
                            }

                            aCentralFacade.repositorysMainSetter.upsert_M19Etudiant(updatedEtudiant)
                        }
                    },
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))
    }
}

private fun getNextSoura(currentSoura: SOUAR): SOUAR {
    val allSurahs = SOUAR.values()
    val currentIndex = allSurahs.indexOf(currentSoura)

    return if (currentIndex < allSurahs.size - 1) {
        allSurahs[currentIndex + 1]
    } else {
        currentSoura
    }
}
