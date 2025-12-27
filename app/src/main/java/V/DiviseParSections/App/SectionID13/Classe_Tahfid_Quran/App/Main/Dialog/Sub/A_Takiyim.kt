package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.ClickableFieldWithIcon
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SOUAR
import V.DiviseParSections.App.Shared.Repository.Repo20OrderEducative.Repository.M20ObsarvationEtudion
import android.util.Log
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

private const val TAG = "TAKIYIM_DEBUG"

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

                        // 🔍 LOG: État AVANT mise à jour
                        Log.d(TAG, "==================== TAKIYIM DEBUG ====================")
                        Log.d(TAG, "📋 État actuel de l'étudiant:")
                        Log.d(TAG, "   - Nom: ${etudiant.nom} ${etudiant.prenom}")
                        Log.d(TAG, "   - Dernier Soura: ${etudiant.dernier_Soura_Wassale_Laha.arabicName}")
                        Log.d(TAG, "   - Dernier Sater: ${etudiant.dernier_Soura_sater}")
                        Log.d(TAG, "   - Mokarrare Soura: ${etudiant.mokarrare_hifde.arabicName}")
                        Log.d(TAG, "   - Mokarrare Sater: ${etudiant.mokarrare_hifde_sater}")
                        Log.d(TAG, "   - Takyim: ${etudiant.dernier_takyim_dabte.arabicName}")
                        Log.d(TAG, "")
                        Log.d(TAG, "📊 Valeurs calculées:")
                        Log.d(TAG, "   - minAya = $minAya")
                        Log.d(TAG, "   - ilaAya = $ilaAya")
                        Log.d(TAG, "")

                        if (etudiant.dernier_takyim_dabte != M19Etudiant.Takiyim.Lam_Yahfed) {
                            val isCompletedSoura = etudiant.mokarrare_hifde.isNihaya(ilaAya)
                            Log.d(TAG, "✅ L'étudiant a réussi (pas 'لم يحفظ')")
                            Log.d(TAG, "   - isCompletedSoura = $isCompletedSoura")
                            Log.d(TAG, "")

                            val updatedEtudiant = if (isCompletedSoura) {
                                val nextSoura = getNextSoura(etudiant.mokarrare_hifde)

                                Log.d(TAG, "🎯 Cas 1: Sourate terminée - Passage à la suivante")
                                Log.d(TAG, "   - Next Soura: ${nextSoura.arabicName}")
                                Log.d(TAG, "   - Nouveau range: ${nextSoura.arabicName} (1) → ${nextSoura.arabicName} (2)")
                                Log.d(TAG, "")

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

                                Log.d(TAG, "🎯 Cas 2: Progression dans la même sourate")
                                Log.d(TAG, "   - rangeSize = $ilaAya - $minAya = $rangeSize")
                                Log.d(TAG, "   - newMin = $ilaAya + 1 = $newMin")
                                Log.d(TAG, "   - newIla = $newMin + $rangeSize = $newIla")
                                Log.d(TAG, "   - Nouveau range: ${etudiant.mokarrare_hifde.arabicName} ($newMin) → ${etudiant.mokarrare_hifde.arabicName} ($newIla)")
                                Log.d(TAG, "")

                                etudiant.copy(
                                    dernier_Soura_Wassale_Laha = etudiant.mokarrare_hifde,
                                    dernier_Soura_sater = newMin,
                                    mokarrare_hifde = etudiant.mokarrare_hifde,
                                    mokarrare_hifde_sater = newIla,
                                    dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
                                )
                            }

                            Log.d(TAG, "📤 État APRÈS mise à jour:")
                            Log.d(TAG, "   - Dernier: ${updatedEtudiant.dernier_Soura_Wassale_Laha.arabicName} (${updatedEtudiant.dernier_Soura_sater})")
                            Log.d(TAG, "   - Mokarrare: ${updatedEtudiant.mokarrare_hifde.arabicName} (${updatedEtudiant.mokarrare_hifde_sater})")
                            Log.d(TAG, "======================================================")
                            Log.d(TAG, "")

                            aCentralFacade.repositorysMainSetter.upsert_M19Etudiant(updatedEtudiant)
                        } else {
                            Log.d(TAG, "❌ L'étudiant n'a pas réussi (لم يحفظ) - Pas de mise à jour")
                            Log.d(TAG, "======================================================")
                            Log.d(TAG, "")
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
