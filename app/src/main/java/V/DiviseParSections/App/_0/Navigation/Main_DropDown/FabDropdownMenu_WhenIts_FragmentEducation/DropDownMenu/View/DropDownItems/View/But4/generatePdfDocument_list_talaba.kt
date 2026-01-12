package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But4

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import android.content.Context
import android.graphics.Canvas
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

        // Filter out excluded students (exclue_de_l_affiche_au_classe)
        val filteredData = cardsData.filterIndexed { index, _ ->
            val etudiant = etudiants.getOrNull(index)
            etudiant?.exclue_de_l_affiche_au_classe != true
        }

        val filteredEtudiants = etudiants.filter { !it.exclue_de_l_affiche_au_classe }

        // Calculate students per page (approximately 8-10 students per page)
        val studentsPerPage = 10
        val totalPages = (filteredData.size + studentsPerPage - 1) / studentsPerPage

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
            drawRTLText(canvas, "تقرير السداسي من بداية الدراسة الى شهر ديسمبر",
                marginLeft, yPosition, contentWidth.toInt(), paintHeader, Layout.Alignment.ALIGN_CENTER)
            yPosition += 25f

            // Date
            val hijriDate = getHijriDate()
            val gregorianDate = SimpleDateFormat("dd/MM/yyyy", Locale("ar")).format(Date())
            drawRTLText(canvas, "$hijriDate - $gregorianDate",
                marginLeft, yPosition, contentWidth.toInt(), paintSmall, Layout.Alignment.ALIGN_CENTER)
            yPosition += 25f

            // Table column widths (removed المقرر للتحضير)
            val colWidths = floatArrayOf(
                40f,   // # الرقم
                120f,  // الاسم الكامل
                35f,   // السن
                90f,   // رقم هاتف الولي
                50f,   // الغيابات
                100f,  // آخر سورة وصل إليها
                100f,  // تقدم في الحفظ
                100f,  // التفاعل و المتابعة مع الولي
                contentWidth - (40f + 120f + 35f + 90f + 50f + 100f + 100f + 100f) // ملاحظات
            )

            val rowHeight = 45f
            val headerHeight = 35f

            // Draw table header (removed المقرر للتحضير)
            var xPosition = marginLeft
            canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth, yPosition + headerHeight, paintHeaderBg)

            val headers = arrayOf(
                "#",
                "الاسم الكامل",
                "السن",
                "رقم هاتف الولي",
                "الغيابات",
                "آخر ما وصل إليه",
                "تقدم في الحفظ",
                "التفاعل و المتابعة مع الولي",
                "ملاحظات"
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
            val endIndex = minOf(startIndex + studentsPerPage, filteredData.size)

            for (i in startIndex until endIndex) {
                val student = filteredData[i]
                val etudiant = filteredEtudiants.getOrNull(i)
                val absenceCount =  0

                // Alternate row background
                if ((i - startIndex) % 2 == 1) {
                    canvas.drawRect(marginLeft, yPosition, marginLeft + contentWidth, yPosition + rowHeight, paintAlternateBg)
                }

                xPosition = marginLeft

                // Display "غير محدد" if phone number is empty or blank
                val phoneNumber = if (student.footer.parentPhone.isBlank()) {
                    "غير محدد"
                } else {
                    student.footer.parentPhone
                }

                // Display "غير محدد" for absences if count is 0
                val absenceDisplay = if (absenceCount == 0) {
                    "غير محدد"
                } else {
                    "$absenceCount"
                }

                val cellData = arrayOf(
                    "${i + 1}",
                    student.studentInfo.fullName,
                    "${student.studentInfo.age}",
                    phoneNumber,
                    absenceDisplay,
                    "${student.hifdProgress.currentSoura}\nآية ${student.hifdProgress.currentAya}",
                    "غير محدد",  // تقدم في الحفظ
                    "غير محدد",  // التفاعل و المتابعة مع الولي
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
    canvas: Canvas,
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
