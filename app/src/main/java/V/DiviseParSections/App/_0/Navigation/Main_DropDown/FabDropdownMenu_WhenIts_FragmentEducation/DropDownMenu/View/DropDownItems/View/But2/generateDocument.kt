package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2

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
 * Dépendance à ajouter dans build.gradle:
 * implementation 'com.aminography:primecalendar:1.7.0'
 */

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
        val parentPhone: String,
        val absenceCount: Int
    )

    companion object {
        fun fromEtudiant(etudiant: M19Etudiant): ParentCommunicationCardData {
            val dateText = SimpleDateFormat("dd/MM/yyyy", Locale("ar")).format(Date())

            // Determine mokarrar details based on whether it's the same soura or different
            val mokarrarText = if (etudiant.dernier_Soura_Wassale_Laha == etudiant.mokarrare_hifde) {
                // Same soura: show range within the soura
                """${etudiant.mokarrare_hifde.arabicName}
من الآية ${etudiant.dernier_Soura_sater} إلى ${etudiant.mokarrare_hifde_sater}"""
            } else {
                // Different souras: show range from current soura to mokarar soura
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
                    parentPhone = etudiant.num_telephone_parent,
                    absenceCount = etudiant.nmbr_absence_sans_justification
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

        // Get day name in Arabic
        val dayNames = arrayOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت")
        val dayName = dayNames[hijriCalendar.dayOfWeek - 1]

        // Get month name in Arabic
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
        // Fallback
        val gregorianYear = SimpleDateFormat("yyyy", Locale.FRENCH).format(Date()).toInt()
        val hijriYear = gregorianYear - 579
        val dayNum = SimpleDateFormat("dd", Locale.FRENCH).format(Date())
        "التاريخ الهجري: $dayNum $hijriYear هـ"
    }
}

/**
 * Generate PDF document from structured card data with proper RTL support
 */
fun generatePdfDocument(context: Context, cardsData: List<ParentCommunicationCardData>): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_parent_comm_${System.currentTimeMillis()}.pdf")

        // A5 Portrait dimensions in points
        val pageWidth = 420
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        cardsData.forEachIndexed { index, cardData ->
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Margins
            val marginLeft = 30f
            val marginRight = 30f
            val marginTop = 40f
            val contentWidth = (pageWidth - marginLeft - marginRight).toInt()

            var yPosition = marginTop

            // TextPaint for RTL rendering
            val paintArabic = TextPaint().apply {
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintArabicBold = TextPaint().apply {
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintArabicMediumBold = TextPaint().apply {
                textSize = 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintHeaderLarge = TextPaint().apply {
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintSmall = TextPaint().apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintVerySmall = TextPaint().apply {
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            // Paint for borders
            val paintBorder = Paint().apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

            // Header text starts directly
            drawRTLText(canvas, "هذه البطاقة هي أداة تواصل",
                marginLeft, yPosition, contentWidth, paintHeaderLarge, Layout.Alignment.ALIGN_CENTER)
            yPosition += 25f

            drawRTLText(canvas, "لمتابعة سير حفظ ابنكم ليليسكم الله حلة الكرامة بما أقرأتماه و صبرتما",
                marginLeft, yPosition, contentWidth, paintSmall, Layout.Alignment.ALIGN_CENTER)
            yPosition += 25f

            // Poetry verses
            val poetryText = """وحلتان من الفردوس قد كسيت ... لوالديه لها الأكوان لم تقم
قالا: بماذا كسيناها؟ فقيل: بما ... أقرأتما ابنكما فاشكر لذي النعم"""

            drawRTLText(canvas, poetryText,
                marginLeft, yPosition, contentWidth, paintVerySmall, Layout.Alignment.ALIGN_CENTER)
            yPosition += 30f

            // Student name and age header (with background)
            val headerHeight = 40f
            val paintHeader = Paint().apply {
                color = "#E8F4FF".toColorInt()
                style = Paint.Style.FILL
            }
            canvas.drawRect(marginLeft, yPosition, pageWidth - marginRight,
                yPosition + headerHeight, paintHeader)
            canvas.drawRect(marginLeft, yPosition, pageWidth - marginRight,
                yPosition + headerHeight, paintBorder)

            drawRTLText(canvas, "${cardData.studentInfo.fullName} - ${cardData.studentInfo.age} سنة",
                marginLeft, yPosition + 10f, contentWidth, paintArabicBold)
            yPosition += headerHeight

            // ========== TABLEAU 1: الحفظ القديم | المقرر لتحضيره ==========
            val cellHeight = 120f
            val cellWidth = contentWidth / 2f

            // Draw borders for tableau 1
            canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + cellHeight, paintBorder)
            canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + cellHeight, paintBorder)

            // RIGHT cell: الحفظ القديم
            drawRTLText(canvas, "الحفظ القديم",
                marginLeft + cellWidth + 5f, yPosition + 10f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
                Layout.Alignment.ALIGN_NORMAL)

            val hifdText = """${cardData.hifdProgress.currentSoura}
قبل الآية ${cardData.hifdProgress.currentAya}

التقييم: ${cardData.evaluation.dabteLevel}"""

            drawRTLText(canvas, hifdText,
                marginLeft + cellWidth + 5f, yPosition + 35f, (cellWidth - 10f).toInt(), paintArabic,
                Layout.Alignment.ALIGN_NORMAL)

            // LEFT cell: المقرر لتحضيره
            drawRTLText(canvas, "المقرر لتحضيره",
                marginLeft + 5f, yPosition + 10f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
                Layout.Alignment.ALIGN_NORMAL)

            drawRTLText(canvas, cardData.hifdProgress.mokarrarDetails,
                marginLeft + 5f, yPosition + 35f, (cellWidth - 10f).toInt(), paintArabic,
                Layout.Alignment.ALIGN_NORMAL)

            yPosition += cellHeight + 15f

            // Table for parent observations
            val parentObsHeight = 60f
            canvas.drawRect(marginLeft, yPosition, pageWidth - marginRight, yPosition + parentObsHeight, paintBorder)

            drawRTLText(canvas, "هنا يمكنكم إعلامنا بملاحظاتكم",
                marginLeft + 5f, yPosition + 20f, contentWidth - 10, paintArabic,
                Layout.Alignment.ALIGN_CENTER)

            yPosition += parentObsHeight + 15f

            // Table for absence notification and notes
            val infoTableHeight = 80f

            // Draw borders
            canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + infoTableHeight, paintBorder)
            canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + infoTableHeight, paintBorder)

            // RIGHT cell: Absence notification with count
            val absenceText = if (cardData.footer.attendanceStatus.contains("غائب")) {
                "نعلمكم بغياب ابنكم لـ ${cardData.footer.absenceCount} حصة"
            } else {
                if (cardData.footer.absenceCount > 0) {
                    "مجموع الغيابات: ${cardData.footer.absenceCount} حصة"
                } else {
                    "الحضور منتظم"
                }
            }

            drawRTLText(canvas, absenceText,
                marginLeft + cellWidth + 5f, yPosition + 25f, (cellWidth - 10f).toInt(), paintArabic,
                Layout.Alignment.ALIGN_NORMAL)

            // LEFT cell: Notes or information
            drawRTLText(canvas, "ملاحظة أو معلومة و شكرا",
                marginLeft + 5f, yPosition + 25f, (cellWidth - 10f).toInt(), paintArabic,
                Layout.Alignment.ALIGN_NORMAL)

            yPosition += infoTableHeight + 15f

            // Bottom section with fixed position
            val bottomMargin = 30f
            val row2Height = 100f
            yPosition = pageHeight - bottomMargin - row2Height

            canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + row2Height, paintBorder)
            canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + row2Height, paintBorder)

            // RIGHT cell: يرجى الاطلاع
            val notesText = if (cardData.notes.specialAttention.isNotBlank()) {
                """يرجى الاطلاع على المقرر
و محاولة التعاون على تحقيقه
بالهدايا و التنبيه له

${cardData.notes.specialAttention}"""
            } else {
                """يرجى الاطلاع على المقرر
و محاولة التعاون على تحقيقه
بالهدايا و التنبيه له"""
            }

            drawRTLText(canvas, notesText,
                marginLeft + cellWidth + 5f, yPosition + 10f, (cellWidth - 10f).toInt(), paintSmall,
                Layout.Alignment.ALIGN_NORMAL)

            // Get Hijri date
            val hijriDate = getHijriDate()

            // Get Gregorian date
            val gregorianDay = SimpleDateFormat("dd", Locale.FRENCH).format(Date())
            val gregorianMonth = SimpleDateFormat("MMMM", Locale("ar")).format(Date())
            val gregorianYear = SimpleDateFormat("yyyy", Locale.FRENCH).format(Date())

            val todayDate = "$hijriDate\nموافق ل $gregorianDay $gregorianMonth $gregorianYear م"

            drawRTLText(canvas, todayDate,
                marginLeft + 5f, yPosition + 10f, (cellWidth - 10f).toInt(), paintSmall,
                Layout.Alignment.ALIGN_NORMAL)

            drawRTLText(canvas, "التوقيع:",
                marginLeft + 5f, yPosition + 60f, (cellWidth - 10f).toInt(), paintArabic,
                Layout.Alignment.ALIGN_NORMAL)

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
