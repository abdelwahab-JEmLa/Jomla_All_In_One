package V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.W.Modules.PrintReceiptHandler.Module.Pdf

import V.DiviseParSections.App.Shared.Repository.ID10VentCouleurOperation.Repository.M10OperationVentCouleur
import V.DiviseParSections.App.Shared.Repository.ID2ClientRepository.Repository.M2Client
import V.DiviseParSections.App.Shared.Repository.ID8BonVent.Repository.M8BonVent
import V.DiviseParSections.App.Shared.Repository.Repo13TarificationInfos.Repository.Repo13TarificationInfos
import V.DiviseParSections.App.Shared.Repository.RepoM1Produit
import java.io.File

enum class PdfType {
    RECEIPT_ONLY,           // Regular receipt with products only
    RECEIPT_WITH_CREDIT,    // Receipt with products + credit section
    CREDIT_ONLY            // Credit payment receipt only
}

/**
 * Data class for credit receipt information
 */
data class CreditReceiptData(
    val client: M2Client?,
    val totalAmount: Double,
    val currentPayment: Double,
    val previousPayments: List<Double> = emptyList(),
    val transactionId: String,
    val showPaymentHistory: Boolean = false,
    val oldBalance: Double = 0.0,
    val currentBill: Double = 0.0
)

/**
 * Parameters for PDF generation
 */
data class PdfGenerationParams(
    val type: PdfType,
    val client: M2Client? = null,
    val operations: List<M10OperationVentCouleur> = emptyList(),
    val tarificationRepo: Repo13TarificationInfos? = null,
    val produitRepo: RepoM1Produit? = null,
    val bonVent: M8BonVent? = null,
    val versement: Double = 0.0,
    val transactionId: String = "",
    val its_GrossistApp: Boolean = true,
    val creditData: CreditReceiptData? = null,
    val relative_bonVent: M8BonVent?
)

/**
 * Result of PDF generation
 */
data class PdfResult(
    val file: File,
    val firebaseUrl: String? = null,
    val success: Boolean = true,
    val message: String = ""
)
