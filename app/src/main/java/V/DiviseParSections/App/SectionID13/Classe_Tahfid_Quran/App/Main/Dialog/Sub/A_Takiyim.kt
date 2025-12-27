package V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub

import V.DiviseParSections.App.SectionID13.Classe_Tahfid_Quran.App.Main.Dialog.Sub.Utils.ClickableFieldWithIcon
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
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
                        onShowTakiyimDialog()

                        // Apply the same logic for both min_aya and ila_aya
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
                            min_aya = minAya,  // Fixed: now uses the same logic as ilaAya
                            ila_soura = etudiant.mokarrare_hifde,
                            ila_aya = ilaAya,
                            takyim = etudiant.dernier_takyim_dabte,
                            parent_ousstad_key = etudiant.parent_ousstad_key
                        )

                        aCentralFacade.repositorysMainSetter.upsert_M20ObsarvationEtudion(observation)
                    },
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))
    }
}
