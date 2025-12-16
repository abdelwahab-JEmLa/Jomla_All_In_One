package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But4

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.util.Log
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withTranslation
import com.aminography.primecalendar.hijri.HijriCalendar
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing the organized card data structure
 */
data class ParentCommunicationCardData(
    val studentInfo: StudentInfo,
    val hifdProgress: HifdProgress,
    val evaluation: Evaluation,
    val notes: Notes,
    val footer: FooterInfo
) {
    data class StudentInfo(
        val fullName: String,
        val age: Int
    )

    data class HifdProgress(
        val currentSoura: String,
        val currentAya: Int,
        val mokarrarSoura: String,
        val mokarrarDetails: String
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
        val parentPhone: String
    )

    companion object {
        fun fromEtudiant(etudiant: M19Etudiant): ParentCommunicationCardData {
            val dateText = SimpleDateFormat("dd/MM/yyyy", Locale("ar")).format(Date())

            val mokarrarText = if (etudiant.dernier_Soura_Wassale_Laha == etudiant.mokarrare_hifde) {
                """${etudiant.mokarrare_hifde.arabicName}
من الآية ${etudiant.dernier_Soura_sater} إلى ${etudiant.mokarrare_hifde_sater}"""
            } else {
                """${etudiant.mokarrare_hifde.arabicName}
من الآية ${etudiant.dernier_Soura_sater} إلى
${etudiant.dernier_Soura_Wassale_Laha.arabicName} الآية ${etudiant.mokarrare_hifde_sater}"""
            }

            return ParentCommunicationCardData(
                studentInfo = StudentInfo(
                    fullName = "${etudiant.nom} ${etudiant.prenom}",
                    age = etudiant.age
                ),
                hifdProgress = HifdProgress(
                    currentSoura = etudiant.dernier_Soura_Wassale_Laha.arabicName,
                    currentAya = etudiant.dernier_Soura_sater,
                    mokarrarSoura = etudiant.mokarrare_hifde.arabicName,
                    mokarrarDetails = mokarrarText
                ),
                evaluation = Evaluation(
                    dabteLevel = etudiant.dernier_takyim_dabte.arabicName,
                    tikrare = etudiant.tikrare,
                    tikrare3arde = etudiant.tikrare_3arde,
                    behaviorNote = etudiant.moulahada_3ala_soulouk.arabicName
                ),
                notes = Notes(
                    specialAttention = etudiant.moulahada_makouba.takeIf { it.isNotBlank() } ?: ""
                ),
                footer = FooterInfo(
                    date = dateText,
                    attendanceStatus = if (etudiant.absent) "غائب ❌" else "حاضر ✅",
                    parentPhone = etudiant.num_telephone_parent
                )
            )
        }
    }
}

/**
 * Helper function to draw RTL text correctly using StaticLayout
 */
fun drawRTLText(
    canvas: android.graphics.Canvas,
    text: String,
    x: Float,
    y: Float,
    width: Int,
    paint: TextPaint,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER
) {
    canvas.withTranslation(x, y) {
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(alignment)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setIncludePad(false)
            .build()

        layout.draw(this)
    }
}

/**
 * Get formatted Hijri date using PrimeCalendar library
 */
fun getHijriDate(): String {
    return try {
        val hijriCalendar = HijriCalendar()

        val dayNames = arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
        val dayName = dayNames[hijriCalendar.dayOfWeek - 1]

        val monthNames = arrayOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
            "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )
        val monthName = monthNames[hijriCalendar.month]

        val day = hijriCalendar.dayOfMonth
        val year = hijriCalendar.year

        "$dayName $day $monthName $year هـ"
    } catch (e: Exception) {
        Log.e("HijriDate", "Error formatting Hijri date", e)
        val gregorianYear = SimpleDateFormat("yyyy", Locale.FRENCH).format(Date()).toInt()
        val hijriYear = gregorianYear - 579
        val dayNum = SimpleDateFormat("dd", Locale.FRENCH).format(Date())
        "التاريخ الهجري: $dayNum $hijriYear هـ"
    }
}


fun generatePdfDocument_list_talaba(
    context: Context,
    cardsData: List<ParentCommunicationCardData>,
    etudiants: List<M19Etudiant> = emptyList()
): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_parent_comm_${System.currentTimeMillis()}.pdf")

        // A4 Landscape dimensions for better table layout
        val pageWidth = 842
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        // Calculate students per page (approximately 8-10 students per page)
        val studentsPerPage = 10
        val totalPages = (cardsData.size + studentsPerPage - 1) / studentsPerPage

        for (pageIndex in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val marginLeft = 20f
            val marginRight = 20f
            val marginTop = 30f
            val marginBottom = 30f
            val contentWidth = (pageWidth - marginLeft - marginRight)

            var yPosition = marginTop

            // Paint configurations
            val paintHeader = TextPaint().apply {
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintSmall = TextPaint().apply {
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintTableHeader = TextPaint().apply {
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.WHITE
            }

            val paintTableCell = TextPaint().apply {
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintBorder = Paint().apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }

            val paintHeaderBg = Paint().apply {
                color = "#2196F3".toColorInt()
                style = Paint.Style.FILL
            }

            val paintAlternateBg = Paint().apply {
                color = "#F5F5F5".toColorInt()
                style = Paint.Style.FILL
            }

            // Title
            drawRTLText(canvas, "قائمة الطلاب - تقرير الحفظ والتقدم",
                marginLeft, yPosition, contentWidth.toInt(), paintHeader, Layout.Alignment.ALIGN_CENTER)
            yPosition += 25f

            // Date
            val hijriDate = getHijriDate()
            val gregorianDate = SimpleDateFormat("dd/MM/yyyy", Locale("ar")).format(Date())
            drawRTLText(canvas, "$hijriDate - $gregorianDate",
                marginLeft, yPosition, contentWidth.toInt(), paintSmall, Layout.Alignment.ALIGN_CENTER)
            yPosition += 25f

            // Table column widths
            val colWidths = floatArrayOf(
                40f,   // # الرقم
                100f,  // الاسم الكامل
                35f,   // السن
                80f,   // رقم الهاتف
                40f,   // الغيابات
                90f,   // آخر سورة وصل إليها
                90f,   // المقرر للتحضير
                80f,   // التقييم
                contentWidth - (40f + 100f + 35f + 80f + 40f + 90f + 90f + 80f) // ملاحظات
            )

            val rowHeight = 45f
            val headerHeight = 35f

            // Draw table header
            var xPosition = marginLeft
            canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth, yPosition + headerHeight, paintHeaderBg)

            val headers = arrayOf(
                "#", "الاسم الكامل", "السن", "رقم الهاتف",
                "الغيابات", "آخر ما وصل إليه", "المقرر للتحضير", "التقييم", "ملاحظات"
            )

            for (i in headers.indices) {
                canvas.drawRect(xPosition, yPosition, xPosition + colWidths[i], yPosition + headerHeight, paintBorder)
                drawRTLText(canvas, headers[i],
                    xPosition + 5f, yPosition + 8f, (colWidths[i] - 10f).toInt(), paintTableHeader,
                    Layout.Alignment.ALIGN_CENTER)
                xPosition += colWidths[i]
            }

            yPosition += headerHeight

            // Draw student rows
            val startIndex = pageIndex * studentsPerPage
            val endIndex = minOf(startIndex + studentsPerPage, cardsData.size)

            for (i in startIndex until endIndex) {
                val student = cardsData[i]
                val etudiant = etudiants.getOrNull(i)
                val absenceCount = etudiant?.nmbr_absence_sans_justification ?: 0

                // Alternate row background
                if ((i - startIndex) % 2 == 1) {
                    canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth, yPosition + rowHeight, paintAlternateBg)
                }

                xPosition = marginLeft

                // Fix: Display "غير محدد" if phone number is empty or blank
                val phoneNumber = if (student.footer.parentPhone.isBlank()) {
                    "غير محدد"
                } else {
                    student.footer.parentPhone
                }

                val cellData = arrayOf(
                    "${i + 1}",
                    student.studentInfo.fullName,
                    "${student.studentInfo.age}",
                    phoneNumber,  // Fixed: Now shows "غير محدد" if empty
                    "$absenceCount",
                    "${student.hifdProgress.currentSoura}\nآية ${student.hifdProgress.currentAya}",
                    "${student.hifdProgress.mokarrarSoura}\nآية ${student.hifdProgress.mokarrarDetails.split("\n").lastOrNull()?.filter { it.isDigit() } ?: ""}",
                    student.evaluation.dabteLevel,
                    student.notes.specialAttention.take(30) + if (student.notes.specialAttention.length > 30) "..." else ""
                )

                for (j in cellData.indices) {
                    canvas.drawRect(xPosition, yPosition, xPosition + colWidths[j], yPosition + rowHeight, paintBorder)
                    drawRTLText(canvas, cellData[j],
                        xPosition + 3f, yPosition + 5f, (colWidths[j] - 6f).toInt(), paintTableCell,
                        Layout.Alignment.ALIGN_NORMAL)
                    xPosition += colWidths[j]
                }

                yPosition += rowHeight
            }

            // Footer
            yPosition = pageHeight - marginBottom - 20f
            drawRTLText(canvas, "صفحة ${pageIndex + 1} من $totalPages",
                marginLeft, yPosition, contentWidth.toInt(), paintSmall, Layout.Alignment.ALIGN_CENTER)

            pdfDocument.finishPage(page)
        }

        // Write to file
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        Log.i("ParentCommPdf", "✅ PDF créé: ${pdfFile.absolutePath}")
        pdfFile
    } catch (e: Exception) {
        Log.e("ParentCommPdf", "❌ Erreur lors de la création du PDF", e)
        null
    }
}
