package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2.generatePdfDocument

import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.SOUAR
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing the organized card data structure
 */
data class ParentCommunicationCardData_2(
    val studentInfo: StudentInfo,
    val hifdProgress: HifdProgress,
    val istedrakProgress: IstedrakProgress?,
    val evaluation: Evaluation,
    val questionOuiNon: String,
    val notes: Notes,
    val footer: FooterInfo
) {
    data class StudentInfo(
        val fullName: String,
        val keyID: String,
        val age: Int
    )

    data class HifdProgress(
        val currentSoura: String,
        val currentAya: Int,
        val mokarrarSoura: String,
        val mokarrarDetails: String
    )

    data class IstedrakProgress(
        val soura: String,
        val mokarrare: String,
        val takyim: String
    )

    data class Evaluation(
        val dabteLevel: String,
        val tikrare: Int,
        val tikrare3arde: Int,
        val behaviorNote: String
    )

    data class Notes(
        val specialAttention: String
    )

    data class FooterInfo(
        val date: String,
        val attendanceStatus: String,
        val parentPhone: String,
        val absenceCount: Int,
        val shouldPrintJustification: Boolean
    )

    // Ensure question is never empty
    fun getQuestionText(): String {
        return questionOuiNon.ifBlank {
            "هل يعاني ابنكم من مرض معين جزاكم الله خيرا؟"
        }
    }

    companion object {
        /**
         * ✅ FIXED: Helper function to format aya display - handles aya=0 case
         * Returns "نهاية" for aya=0, otherwise returns the aya number
         */
        private fun formatAyaForDisplay(aya: Int): String {
            return if (aya == 0) "نهاية" else aya.toString()
        }

        fun fromEtudiant(etudiant: M19Etudiant): ParentCommunicationCardData_2 {
            val dateText = SimpleDateFormat("dd/MM/yyyy", Locale("ar")).format(Date())

            // ✅ FIXED: Determine mokarrar details with proper aya=0 handling
            val mokarrarText = if (etudiant.dernier_Soura_Wassale_Laha == etudiant.mokarrare_hifde) {
                // Same soura case
                val fromAyaDisplay = formatAyaForDisplay(etudiant.dernier_Soura_sater)
                val toAyaDisplay = formatAyaForDisplay(etudiant.mokarrare_hifde_sater)

                """${etudiant.mokarrare_hifde.arabicName}
من الآية $fromAyaDisplay إلى $toAyaDisplay"""
            } else {
                // Different souras case
                val fromAyaDisplay = formatAyaForDisplay(etudiant.dernier_Soura_sater)
                val toAyaDisplay = formatAyaForDisplay(etudiant.mokarrare_hifde_sater)

                """${etudiant.mokarrare_hifde.arabicName}
من الآية $fromAyaDisplay إلى
${etudiant.dernier_Soura_Wassale_Laha.arabicName} الآية $toAyaDisplay"""
            }

            // Check if istedrak data exists and is different from default
            val hasIstedrak = etudiant.istedrak_kadim_Akher_Soura_Wassale_Laha != SOUAR.El_Nasse ||
                    etudiant.istedrak_kadim_Moukarare != SOUAR.El_Nasse

            val istedrakData = if (hasIstedrak) {
                IstedrakProgress(
                    soura = etudiant.istedrak_kadim_Akher_Soura_Wassale_Laha.arabicName,
                    mokarrare = etudiant.istedrak_kadim_Moukarare.arabicName,
                    takyim = etudiant.istedrak_kadim_Takyim_hali.arabicName
                )
            } else null

            return ParentCommunicationCardData_2(
                studentInfo = StudentInfo(
                    fullName = "${etudiant.nom} ${etudiant.prenom}",
                    age = etudiant.age,
                    keyID = etudiant.keyID
                ),
                hifdProgress = HifdProgress(
                    currentSoura = etudiant.dernier_Soura_Wassale_Laha.arabicName,
                    currentAya = etudiant.dernier_Soura_sater,
                    mokarrarSoura = etudiant.mokarrare_hifde.arabicName,
                    mokarrarDetails = mokarrarText
                ),
                istedrakProgress = istedrakData,
                evaluation = Evaluation(
                    dabteLevel = etudiant.dernier_takyim_dabte.arabicName,
                    tikrare = etudiant.tikrare,
                    tikrare3arde = etudiant.tikrare_3arde,
                    behaviorNote = etudiant.moulahada_3ala_soulouk.arabicName
                ),
                questionOuiNon = etudiant.question_par_non.ifBlank {
                    "هل يعاني ابنكم من مرض معين جزاكم الله خيرا؟"
                },
                notes = Notes(
                    specialAttention = etudiant.moulahada_makouba.takeIf { it.isNotBlank() } ?: ""
                ),
                footer = FooterInfo(
                    date = dateText,
                    attendanceStatus = if (etudiant.absent) "غائب ❌" else "حاضر ✅",
                    parentPhone = etudiant.num_telephone_parent,
                    absenceCount = 0,
                    shouldPrintJustification = etudiant.imprime_justification
                )
            )
        }
    }
}
