package V.DiviseParSections.App._0.Navigation.Main_DropDown.FabDropdownMenu_WhenIts_FragmentEducation.DropDownMenu.View.DropDownItems.View.But2

import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.M19Etudiant
import V.DiviseParSections.App.Shared.Repository.Repo19Etudion.Repository.Repo19Etudiant
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.BreakType
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.koin.compose.koinInject
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DropDownItem_Imprime_word_communication_ac_parent(
    nomFun: String = "بطاقة التواصل مع الولي (Word)",
    aCentralFacade: ACentralFacade = koinInject(),
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repositorysMainGetter.repo19Etudiant,
    context: Context = LocalContext.current
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun createAndOpenWordDocument() {
        isLoading = true
        scope.launch {
            try {
                val allEtudiants = repo19Etudiant.datasValue.sortedWith(
                    compareBy<M19Etudiant> { it.positon_don_classe }
                        .thenBy { it.creationTimestamps }
                )

                // Filter for the targeted student
                val targetedEtudiant = allEtudiants.find { it.prenom.contains("مسلم") }

                if (targetedEtudiant == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "لا يوجد طالب باسم 'مسلم'", Toast.LENGTH_LONG).show()
                    }
                    isLoading = false
                    return@launch
                }

                // Create list with only the targeted student
                val etudiants = listOf(targetedEtudiant)

                val wordFile = withContext(Dispatchers.IO) {
                    generateWordDocument(context, etudiants)
                }

                val saveResult = withContext(Dispatchers.IO) {
                    if (wordFile != null && wordFile.exists()) {
                        val fileName = "بطاقة_التواصل_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.docx"
                        WordSaverUtility.saveWord(
                            context = context,
                            sourceFile = wordFile,
                            fileName = fileName,
                            subFolder = "Tahfide_Quran"
                        )
                    } else {
                        Result.failure(Exception("فشل إنشاء ملف Word"))
                    }
                }

                withContext(Dispatchers.Main) {
                    saveResult.fold(
                        onSuccess = { savedPath ->
                            if (wordFile != null) {
                                openWordWithViewer(context, wordFile)
                            }
                            Toast.makeText(context, "✅ تم إنشاء وحفظ البطاقة: $savedPath", Toast.LENGTH_LONG).show()
                        },
                        onFailure = { error ->
                            Toast.makeText(context, "❌ خطأ في الحفظ: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            } catch (e: Exception) {
                Log.e("ParentCommWord", "❌ خطأ: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ خطأ: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                isLoading = false
            }
        }
    }
    Card(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoading) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        DropdownMenuItem(
            leadingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(4.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            text = {
                Text(
                    text = if (isLoading) "جاري الإنشاء..." else nomFun,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                if (!isLoading) {
                    createAndOpenWordDocument()
                }
            },
            enabled = !isLoading
        )
    }
}

private fun generateWordDocument(context: Context, etudiants: List<M19Etudiant>): File? {
    return try {
        val outputDir = context.cacheDir
        val wordFile = File(outputDir, "temp_parent_comm_${System.currentTimeMillis()}.docx")

        val document = XWPFDocument()

        // Set page to A5 landscape
        val section = document.document.body.addNewSectPr()
        val pageSize = section.addNewPgSz()
        pageSize.w = BigInteger.valueOf(11906) // A5 landscape width in twips
        pageSize.h = BigInteger.valueOf(8391)  // A5 landscape height in twips
        pageSize.orient = STPageOrientation.LANDSCAPE

        etudiants.forEachIndexed { index, etudiant ->
            if (index > 0) {
                // Add page break between students
                val breakPara = document.createParagraph()
                breakPara.createRun().addBreak(BreakType.PAGE)
            }

            createStudentCard(document, etudiant)
        }

        FileOutputStream(wordFile).use { out ->
            document.write(out)
        }
        document.close()

        Log.i("ParentCommWord", "✅ Word créé: ${wordFile.absolutePath}")
        wordFile
    } catch (e: Exception) {
        Log.e("ParentCommWord", "❌ Erreur lors de la création du Word", e)
        null
    }
}

private fun createStudentCard(document: XWPFDocument, etudiant: M19Etudiant) {
    // Create table with 4 rows and 3 columns
    val table = document.createTable(4, 3)
    table.width = 5000

    // Set RTL direction for the table
    val tblPr = table.ctTbl.tblPr ?: table.ctTbl.addNewTblPr()
    tblPr.addNewBidiVisual()

    // Row 0: Header (merged across all columns)
    val headerRow = table.getRow(0)
    mergeCellsHorizontally(table, 0, 0, 2)
    val headerCell = headerRow.getCell(0)
    setCellBackground(headerCell, "C8DCF0")
    setCellText(headerCell, "بطاقة معلومات حفظ القرآن الكريم - يرجى الاحتفاظ بها لمتابعة تقدم ابنكم",
        true, 14, ParagraphAlignment.CENTER)

    // Row 1: Name (merged across all columns)
    val nameRow = table.getRow(1)
    mergeCellsHorizontally(table, 1, 0, 2)
    val nameCell = nameRow.getCell(0)
    setCellText(nameCell, "الاسم لقب و السن: ${etudiant.nom} ${etudiant.prenom} - ${etudiant.age} سنة",
        true, 12, ParagraphAlignment.RIGHT)

    // Row 2: Main content (3 columns)
    val contentRow = table.getRow(2)

    // Right cell (col 2): الحفظ و المقرر
    val hifdCell = contentRow.getCell(2)
    val hifdText = buildString {
        append("الحفظ:\n")
        append("${etudiant.dernier_Soura_Wassale_Laha.arabicName}\n")
        append("إلى آية ${etudiant.dernier_Soura_sater}\n\n")
        append("المقرر:\n")
        if (etudiant.dernier_Soura_Wassale_Laha == etudiant.mokarrare_hifde) {
            append("${etudiant.mokarrare_hifde.arabicName}\n")
            append("آية ${etudiant.mokarrare_hifde_sater}")
        } else {
            append("${etudiant.mokarrare_hifde.arabicName}\n")
            append("محصى لإعادة ${etudiant.mokarrare_hifde_mahssou_li_3idat_souer} سور")
        }
    }
    setCellText(hifdCell, hifdText, false, 11, ParagraphAlignment.RIGHT)

    // Middle cell (col 1): التقييم
    val takiyimCell = contentRow.getCell(1)
    val takiyimText = buildString {
        append("التقييم:\n")
        append("${etudiant.dernier_takyim_dabte.arabicName}\n\n")
        append("التكرار: ${etudiant.tikrare}\n")
        append("تكرار عرض: ${etudiant.tikrare_3arde}\n\n")
        append("السلوك:\n")
        append("${etudiant.moulahada_3ala_soulouk.arabicName}")
    }
    setCellText(takiyimCell, takiyimText, false, 11, ParagraphAlignment.RIGHT)

    // Left cell (col 0): يرجى الاهتمام
    val notesCell = contentRow.getCell(0)
    val notesText = buildString {
        append("يرجى الاهتمام بـ:\n\n")
        if (etudiant.moulahada_makouba.isNotBlank()) {
            append(etudiant.moulahada_makouba)
        }
    }
    setCellText(notesCell, notesText, false, 11, ParagraphAlignment.RIGHT)

    // Row 3: Footer (merged in 2 parts)
    val footerRow = table.getRow(3)

    // Merge cells 1 and 2 for right side
    mergeCellsHorizontally(table, 3, 1, 2)

    val footerLeftCell = footerRow.getCell(0)
    val footerRightCell = footerRow.getCell(1)

    val dateText = SimpleDateFormat("dd/MM/yyyy", Locale("ar")).format(Date())
    val footerRightText = buildString {
        append("التاريخ: $dateText\n")
        append("الحاضر: ${if (etudiant.absent) "غائب ❌" else "حاضر ✅"}\n")
        append("هاتف الولي: ${etudiant.num_telephone_parent}")
    }
    setCellText(footerRightCell, footerRightText, false, 10, ParagraphAlignment.RIGHT)
    setCellText(footerLeftCell, "التوقيع:\n\n___________", false, 10, ParagraphAlignment.CENTER)

    // Set column widths
    setColumnWidth(table, 0, 2500) // Left column
    setColumnWidth(table, 1, 2500) // Middle column
    setColumnWidth(table, 2, 2000) // Right column
}

private fun mergeCellsHorizontally(table: XWPFTable, row: Int, fromCol: Int, toCol: Int) {
    val rowObj = table.getRow(row)
    for (colIndex in fromCol..toCol) {
        val cell = rowObj.getCell(colIndex)
        val tcPr = cell.ctTc.tcPr ?: cell.ctTc.addNewTcPr()

        if (colIndex == fromCol) {
            val gridSpan = tcPr.addNewGridSpan()
            gridSpan.`val` = BigInteger.valueOf((toCol - fromCol + 1).toLong())
        } else {
            val vMerge = tcPr.addNewVMerge()
            vMerge.`val` = STMerge.CONTINUE
        }
    }
}

private fun setCellBackground(cell: XWPFTableCell, color: String) {
    val tcPr = cell.ctTc.tcPr ?: cell.ctTc.addNewTcPr()
    val shd = tcPr.addNewShd()
    shd.fill = color.toByteArray()
}

private fun setCellText(
    cell: XWPFTableCell,
    text: String,
    bold: Boolean,
    fontSize: Int,
    alignment: ParagraphAlignment
) {
    cell.removeParagraph(0)
    val para = cell.addParagraph()
    para.alignment = alignment

    // Set RTL for Arabic using CTPPr
    val ppr = para.ctp.pPr ?: para.ctp.addNewPPr()
    ppr.addNewBidi()

    val run = para.createRun()
    run.setText(text)
    run.fontSize = fontSize
    run.isBold = bold
    run.fontFamily = "Arial"
}

private fun setColumnWidth(table: XWPFTable, col: Int, width: Int) {
    for (row in table.rows) {
        val cell = row.getCell(col)
        val tcPr = cell.ctTc.tcPr ?: cell.ctTc.addNewTcPr()
        val tcW = tcPr.addNewTcW()
        tcW.type = STTblWidth.DXA
        tcW.w = BigInteger.valueOf(width.toLong())
    }
}

private fun openWordWithViewer(context: Context, wordFile: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            wordFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "⚠️ لا يوجد تطبيق Word مثبت\nتم حفظ الملف في التنزيلات",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        Log.e("ParentCommWord", "❌ خطأ في فتح Word", e)
    }
}

object WordSaverUtility {
    fun saveWord(
        context: Context,
        sourceFile: File,
        fileName: String,
        subFolder: String
    ): Result<String> {
        return PdfSaverUtility.savePdf(context, sourceFile, fileName, subFolder)
    }
}
