package A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Module

import A_Main.Shared.Views.Dialogs.B.Dialoge.ButtonID7.Action.Datas
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.content.Context
import kotlinx.coroutines.CoroutineScope

/**
 * FIXED: Added shouldOpenFile parameter to control when PDFs are opened
 */
class A_PrintReceiptHandler_ProMai(
    private val b_Generateur_ProMai: B_Generateur_ProMai,
    datas: Datas,
    ) {
    private val CPdfPrintHandler = C_PdfPrintHandler(b_Generateur_ProMai)

    /**
     * Generate PDF only - Returns Result for proper error handling
     * FIXED: Now checks demande_Versemet_si_Type_est_regle
     * FIXED: Added shouldOpenFile parameter to control when PDF is opened
     *
     * @param shouldOpenFile If false, PDF will be generated but not opened automatically.
     *                       This is useful when the file will be copied to a different location
     *                       before opening (e.g., in background PDF creation).
     *                       Default is true for backward compatibility.
     */
    suspend fun printPdfOnly(
        context: Context,
        repoM1Produit: List<M01Produit>?,
        repo3CouleurProduitInfos: List<M13TarificationInfos>,
        client: M2Client?,
        scope: CoroutineScope? = null,
        relative_ListM10OperationVentCouleur: List<M10OperationVentCouleur>,
        repo13TarificationInfos: List<M13TarificationInfos>,
        relative_bonVent: M8BonVent? = null,
        showCreditSection: Boolean = true,
        versement: Double = 0.0,
        shouldOpenFile: Boolean = true
    ): Result<String> {
        return try {
            // FIXED: The CPdfPrintHandler will now automatically check demande_Versemet_si_Type_est_regle
            // No need to override showCreditSection here since the handler checks the bonVent property
            // FIXED: Pass shouldOpenFile to control when PDF is opened
            CPdfPrintHandler.generateAndOpenPdf(
                context,
                client,
                relative_ListM10OperationVentCouleur,
                repo13TarificationInfos,
                repoM1Produit,
                relative_bonVent,
                showCreditSection,
                versement,
                shouldOpenFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
        }
    }
