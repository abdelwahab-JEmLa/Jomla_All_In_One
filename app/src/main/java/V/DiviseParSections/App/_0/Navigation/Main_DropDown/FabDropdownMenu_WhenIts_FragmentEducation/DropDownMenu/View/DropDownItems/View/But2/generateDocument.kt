package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2

import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.SOUAR
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
 * Clean PDF Generator for Parent Communication Cards
 * OPTIMIZED: Reduced text sizes and spacing for better layout
 */

data class ParentCommunicationCardData(
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

            val hasIstedrak = etudiant.istedrak_kadim_Akher_Soura_Wassale_Laha != SOUAR.El_Nasse ||
                    etudiant.istedrak_kadim_Moukarare != SOUAR.El_Nasse

            val istedrakData = if (hasIstedrak) {
                IstedrakProgress(
                    soura = etudiant.istedrak_kadim_Akher_Soura_Wassale_Laha.arabicName,
                    mokarrare = etudiant.istedrak_kadim_Moukarare.arabicName,
                    takyim = etudiant.istedrak_kadim_Takyim_hali.arabicName
                )
            } else null

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
                istedrakProgress = istedrakData,
                evaluation = Evaluation(
                    dabteLevel = etudiant.dernier_takyim_dabte.arabicName,
                    tikrare = etudiant.tikrare,
                    tikrare3arde = etudiant.tikrare_3arde,
                    behaviorNote = etudiant.moulahada_3ala_soulouk.arabicName
                ),
                questionOuiNon = etudiant.question_par_non,
                notes = Notes(
                    specialAttention = etudiant.moulahada_makouba.takeIf { it.isNotBlank() } ?: ""
                ),
                footer = FooterInfo(
                    date = dateText,
                    attendanceStatus = if (etudiant.absent) "غائب ❌" else "حاضر ✅",
                    parentPhone = etudiant.num_telephone_parent,
                    absenceCount = etudiant.nmbr_absence_sans_justification,
                    shouldPrintJustification = etudiant.imprime_justification
                )
            )
        }
    }
}

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

fun drawWritableSpace(
    canvas: android.graphics.Canvas,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    lines: Int = 3
) {
    val paintLine = Paint().apply {
        color = android.graphics.Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 0.5f
    }

    val lineSpacing = height / (lines + 1)
    for (i in 1..lines) {
        val lineY = y + (lineSpacing * i)
        canvas.drawLine(x, lineY, x + width, lineY, paintLine)
    }
}

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

/**
 * OPTIMIZED PDF Generator - Reduced text sizes and spacing
 */
fun generatePdfDocument(context: Context, cardsData: List<ParentCommunicationCardData>): File? {
    return try {
        val outputDir = context.cacheDir
        val pdfFile = File(outputDir, "temp_parent_comm_${System.currentTimeMillis()}.pdf")

        val pageWidth = 420
        val pageHeight = 595

        val pdfDocument = PdfDocument()

        cardsData.forEachIndexed { index, cardData ->
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val marginLeft = 30f
            val marginRight = 30f
            val marginTop = 35f
            val contentWidth = (pageWidth - marginLeft - marginRight).toInt()

            var yPosition = marginTop

            // REDUCED TextPaint sizes
            val paintArabic = TextPaint().apply {
                textSize = 13f  // Reduced from 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintArabicBold = TextPaint().apply {
                textSize = 17f  // Reduced from 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintArabicMediumBold = TextPaint().apply {
                textSize = 15f  // Reduced from 18f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintHeaderLarge = TextPaint().apply {
                textSize = 14f  // Reduced from 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintSmall = TextPaint().apply {
                textSize = 11f  // Reduced from 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintVerySmall = TextPaint().apply {
                textSize = 9f  // Reduced from 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintBorder = Paint().apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

            // Header - REDUCED spacing
            drawRTLText(canvas, "هذه البطاقة هي أداة تواصل",
                marginLeft, yPosition, contentWidth, paintHeaderLarge, Layout.Alignment.ALIGN_CENTER)
            yPosition += 18f  // Reduced from 25f

            drawRTLText(canvas, "لمتابعة سير حفظ ابنكم ليليسكم الله حلة الكرامة بما أقرأتماه و صبرتما",
                marginLeft, yPosition, contentWidth, paintSmall, Layout.Alignment.ALIGN_CENTER)
            yPosition += 18f  // Reduced from 25f

            // Poetry - REDUCED spacing
            val poetryText = """وحلتان من الفردوس قد كسيت ... لوالديه لها الأكوان لم تقم
قالا: بماذا كسيناها؟ فقيل: بما ... أقرأتما ابنكما فاشكر لذي النعم"""

            drawRTLText(canvas, poetryText,
                marginLeft, yPosition, contentWidth, paintVerySmall, Layout.Alignment.ALIGN_CENTER)
            yPosition += 22f  // Reduced from 30f

            // Student header - REDUCED height
            val headerHeight = 32f  // Reduced from 40f
            val paintHeader = Paint().apply {
                color = "#E8F4FF".toColorInt()
                style = Paint.Style.FILL
            }
            canvas.drawRect(marginLeft, yPosition, pageWidth - marginRight,
                yPosition + headerHeight, paintHeader)
            canvas.drawRect(marginLeft, yPosition, pageWidth - marginRight,
                yPosition + headerHeight, paintBorder)

            drawRTLText(canvas, "${cardData.studentInfo.fullName} - ${cardData.studentInfo.age} سنة",
                marginLeft, yPosition + 7f, contentWidth, paintArabicBold)
            yPosition += headerHeight

            // TABLEAU 1 - REDUCED height
            val cellHeight = 95f  // Reduced from 120f
            val cellWidth = contentWidth / 2f

            canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + cellHeight, paintBorder)
            canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + cellHeight, paintBorder)

            // RIGHT cell
            drawRTLText(canvas, "الحفظ القديم",
                marginLeft + cellWidth + 5f, yPosition + 7f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
                Layout.Alignment.ALIGN_NORMAL)

            val hifdText = """${cardData.hifdProgress.currentSoura}
قبل الآية ${cardData.hifdProgress.currentAya}

التقييم: ${cardData.evaluation.dabteLevel}"""

            drawRTLText(canvas, hifdText,
                marginLeft + cellWidth + 5f, yPosition + 28f, (cellWidth - 10f).toInt(), paintArabic,
                Layout.Alignment.ALIGN_NORMAL)

            // LEFT cell
            drawRTLText(canvas, "المقرر لتحضيره",
                marginLeft + 5f, yPosition + 7f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
                Layout.Alignment.ALIGN_NORMAL)

            drawRTLText(canvas, cardData.hifdProgress.mokarrarDetails,
                marginLeft + 5f, yPosition + 28f, (cellWidth - 10f).toInt(), paintArabic,
                Layout.Alignment.ALIGN_NORMAL)

            yPosition += cellHeight + 10f  // Reduced spacing

            // TABLEAU ISTEDRAK
            if (cardData.istedrakProgress != null) {
                val istedrakHeight = 80f  // Reduced from 100f

                canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + istedrakHeight, paintBorder)
                canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + istedrakHeight, paintBorder)

                drawRTLText(canvas, "برناج المراجعة -ما وصل اليه",
                    marginLeft + cellWidth + 5f, yPosition + 7f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
                    Layout.Alignment.ALIGN_NORMAL)

                val istedrakHifdText = """${cardData.istedrakProgress.soura}

التقييم: ${cardData.istedrakProgress.takyim}"""

                drawRTLText(canvas, istedrakHifdText,
                    marginLeft + cellWidth + 5f, yPosition + 28f, (cellWidth - 10f).toInt(), paintArabic,
                    Layout.Alignment.ALIGN_NORMAL)

                drawRTLText(canvas, "برناج المراجعة - المقرر",
                    marginLeft + 5f, yPosition + 7f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
                    Layout.Alignment.ALIGN_NORMAL)

                drawRTLText(canvas, cardData.istedrakProgress.mokarrare,
                    marginLeft + 5f, yPosition + 32f, (cellWidth - 10f).toInt(), paintArabic,
                    Layout.Alignment.ALIGN_NORMAL)

                yPosition += istedrakHeight + 10f
            }

            // TABLEAU QUESTION
            if (cardData.questionOuiNon.isNotBlank()) {
                val questionHeight = 65f  // Reduced from 80f

                canvas.drawRect(marginLeft, yPosition, pageWidth - marginRight, yPosition + questionHeight, paintBorder)

                drawRTLText(canvas, "سؤال:",
                    marginLeft + 5f, yPosition + 7f, contentWidth - 10, paintArabicMediumBold,
                    Layout.Alignment.ALIGN_NORMAL)

                drawRTLText(canvas, cardData.questionOuiNon,
                    marginLeft + 5f, yPosition + 25f, contentWidth - 10, paintArabic,
                    Layout.Alignment.ALIGN_NORMAL)

                val checkboxY = yPosition + questionHeight - 20f
                drawRTLText(canvas, "☐ نعم          ☐ لا",
                    marginLeft + 5f, checkboxY, contentWidth - 10, paintArabic,
                    Layout.Alignment.ALIGN_NORMAL)

                yPosition += questionHeight + 10f
            }

            // TABLEAU JUSTIFICATION
            if (cardData.footer.shouldPrintJustification && cardData.footer.absenceCount > 0) {
                val justificationHeight = 85f  // Reduced from 100f

                canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + justificationHeight, paintBorder)
                canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + justificationHeight, paintBorder)

                val absenceText = """نعلمكم بغياب ابنكم
لـ ${cardData.footer.absenceCount} حصة

يرجى توضيح السبب"""

                drawRTLText(canvas, absenceText,
                    marginLeft + cellWidth + 5f, yPosition + 8f, (cellWidth - 10f).toInt(), paintArabic,
                    Layout.Alignment.ALIGN_NORMAL)

                drawRTLText(canvas, "المبرر:",
                    marginLeft + 5f, yPosition + 5f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
                    Layout.Alignment.ALIGN_NORMAL)

                drawWritableSpace(
                    canvas,
                    marginLeft + 5f,
                    yPosition + 22f,
                    cellWidth - 10f,
                    justificationHeight - 25f,
                    lines = 3
                )

                yPosition += justificationHeight + 10f
            } else if (cardData.footer.absenceCount > 0) {
                val infoTableHeight = 65f  // Reduced from 80f

                canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + infoTableHeight, paintBorder)
                canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + infoTableHeight, paintBorder)

                val absenceText = if (cardData.footer.attendanceStatus.contains("غائب")) {
                    "نعلمكم بغياب ابنكم لـ ${cardData.footer.absenceCount} حصة"
                } else {
                    "مجموع الغيابات: ${cardData.footer.absenceCount} حصة"
                }

                drawRTLText(canvas, absenceText,
                    marginLeft + cellWidth + 5f, yPosition + 20f, (cellWidth - 10f).toInt(), paintArabic,
                    Layout.Alignment.ALIGN_NORMAL)

                drawRTLText(canvas, "ملاحظة:",
                    marginLeft + 5f, yPosition + 5f, (cellWidth - 10f).toInt(), paintArabicMediumBold,
                    Layout.Alignment.ALIGN_NORMAL)

                drawWritableSpace(
                    canvas,
                    marginLeft + 5f,
                    yPosition + 22f,
                    cellWidth - 10f,
                    infoTableHeight - 25f,
                    lines = 2
                )

                yPosition += infoTableHeight + 10f
            }

            // BOTTOM SECTION - GREATLY REDUCED height
            val bottomMargin = 25f
            val row2Height = 75f  // Reduced from 100f
            yPosition = pageHeight - bottomMargin - row2Height

            canvas.drawRect(marginLeft, yPosition, marginLeft + cellWidth, yPosition + row2Height, paintBorder)
            canvas.drawRect(marginLeft + cellWidth, yPosition, pageWidth - marginRight, yPosition + row2Height, paintBorder)

            // RIGHT cell: Instructions
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
                marginLeft + cellWidth + 5f, yPosition + 7f, (cellWidth - 10f).toInt(), paintSmall,
                Layout.Alignment.ALIGN_NORMAL)

            // LEFT cell: Date and signature - REDUCED text size
            val hijriDate = getHijriDate()
            val gregorianDay = SimpleDateFormat("dd", Locale.FRENCH).format(Date())
            val gregorianMonth = SimpleDateFormat("MMMM", Locale("ar")).format(Date())
            val gregorianYear = SimpleDateFormat("yyyy", Locale.FRENCH).format(Date())
            val todayDate = "$hijriDate\nموافق ل $gregorianDay $gregorianMonth $gregorianYear م"

            drawRTLText(canvas, todayDate,
                marginLeft + 5f, yPosition + 7f, (cellWidth - 10f).toInt(), paintVerySmall,  // Using very small font
                Layout.Alignment.ALIGN_NORMAL)

            drawRTLText(canvas, "التوقيع:",
                marginLeft + 5f, yPosition + 50f, (cellWidth - 10f).toInt(), paintSmall,  // Reduced from paintArabic
                Layout.Alignment.ALIGN_NORMAL)

            pdfDocument.finishPage(page)
        }

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

fun generateSingleStudentPdf(context: Context, etudiant: M19Etudiant): File? {
    val cardData = ParentCommunicationCardData.fromEtudiant(etudiant)
    return generatePdfDocument(context, listOf(cardData))
}

fun generateMultipleStudentsPdf(context: Context, etudiants: List<M19Etudiant>): File? {
    val cardsData = etudiants.map { ParentCommunicationCardData.fromEtudiant(it) }
    return generatePdfDocument(context, cardsData)
}
