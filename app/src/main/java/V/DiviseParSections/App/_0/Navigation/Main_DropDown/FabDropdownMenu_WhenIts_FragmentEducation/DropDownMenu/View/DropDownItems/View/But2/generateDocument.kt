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

            val mokarrarDetails = if (etudiant.dernier_Soura_Wassale_Laha == etudiant.mokarrare_hifde) {
                "آية ${etudiant.mokarrare_hifde_sater}"
            } else {
                "محصى لإعادة ${etudiant.mokarrare_hifde_mahssou_li_3idat_souer} سور"
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
                    mokarrarDetails = mokarrarDetails
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
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintArabicBold = TextPaint().apply {
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                color = android.graphics.Color.BLACK
            }

            val paintSmall = TextPaint().apply {
                textSize = 9f
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

            // Header messages
            drawRTLText(canvas, "هذه البطاقة هي أداة اتصال",
                marginLeft, yPosition, contentWidth, paintSmall)
            yPosition += 20f

            drawRTLText(canvas, "لمتابعة سير حفظ ابنكم بغية تلبسو حلة الكرامة بما اقرئتماه و صبرتما",
                marginLeft, yPosition, contentWidth, paintSmall)
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

            // Main content table (2 columns)
            val cellHeight = 120f
            val cellWidth = contentWidth / 2f

            // Row 1: الحفظ القديم | لتحضيره المقرر
            val row1Y = yPosition

            // Draw borders for row 1
            canvas.drawRect(marginLeft, row1Y, marginLeft + cellWidth, row1Y + cellHeight, paintBorder)
            canvas.drawRect(marginLeft + cellWidth, row1Y, pageWidth - marginRight, row1Y + cellHeight, paintBorder)

            // Right cell: الحفظ القديم
            val hifdText = """الحفظ القديم

${cardData.hifdProgress.currentSoura}
من الآية ${cardData.hifdProgress.currentAya}
من سورة إلى 10 من 15"""

            drawRTLText(canvas, hifdText,
                marginLeft + cellWidth, row1Y + 10f, cellWidth.toInt(), paintArabic)

            // Left cell: لتحضيره المقرر
            val mokarrarText = """لتحضيره المقرر

${cardData.hifdProgress.mokarrarSoura}
${cardData.hifdProgress.mokarrarDetails}"""

            drawRTLText(canvas, mokarrarText,
                marginLeft, row1Y + 10f, cellWidth.toInt(), paintArabic)

            yPosition += cellHeight

            // Row 2: جيد تقييم تقريبي | empty
            val row2Y = yPosition
            val row2Height = 60f

            canvas.drawRect(marginLeft, row2Y, marginLeft + cellWidth, row2Y + row2Height, paintBorder)
            canvas.drawRect(marginLeft + cellWidth, row2Y, pageWidth - marginRight, row2Y + row2Height, paintBorder)

            // Right cell: Evaluation
            val evalText = """${cardData.evaluation.dabteLevel}
تقييم تقريبي"""

            drawRTLText(canvas, evalText,
                marginLeft + cellWidth, row2Y + 15f, cellWidth.toInt(), paintArabic)

            yPosition += row2Height

            // Row 3: التوقيع والتاريخ | يرجى الاطلاع على المقرر
            val row3Y = yPosition
            val row3Height = 100f

            canvas.drawRect(marginLeft, row3Y, marginLeft + cellWidth, row3Y + row3Height, paintBorder)
            canvas.drawRect(marginLeft + cellWidth, row3Y, pageWidth - marginRight, row3Y + row3Height, paintBorder)

            // Right cell: Signature and Date (Latin numerals)
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val todayDate = dateFormatter.format(Date())

            val signatureText = """التوقيع:


التاريخ: $todayDate"""

            drawRTLText(canvas, signatureText,
                marginLeft + cellWidth, row3Y + 15f, cellWidth.toInt(), paintArabic)

            // Left cell: يرجى الاطلاع على المقرر
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
                marginLeft, row3Y + 10f, cellWidth.toInt(), paintSmall)

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
