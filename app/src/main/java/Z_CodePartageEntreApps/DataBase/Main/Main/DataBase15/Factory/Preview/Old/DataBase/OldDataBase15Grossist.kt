package Z_CodePartageEntreApps.DataBase.Main.Main.DataBase15.Factory.Preview.Old.DataBase

import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.centralRef

data class OldDataBase15Grossist(
    val bonDuSupplierSu: String = "",
    val classmentSupplier: Int = 0,
    val couleurSu: String = "",
    val currentCreditBalance: Int = 0,
    val idSupplierSu: Int = 0,
    val ignoreItProdects: Boolean = false,
    val longTermCredit: Boolean = false,
    val nameInFrenche: String = "",
    val nomSupplierSu: String = "",
    val nomVocaleArabeDuSupplier: String = "",
    val supplierNameInFrenche: String = ""
) {
    companion object {
        val ref = centralRef.child("/AncienDataBase/M15Grossist/1_1")
    }
}
