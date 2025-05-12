package com.example.clientjetpack.ID1.Test

import com.example.clientjetpack.ID1.Test.Z.Fragment.A.ViewModel.TarificationViewModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.DataBase.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Z.Fragment.Log.logProduits
import com.example.clientjetpack.ID1.Test.Z.Fragment.Passive.strDateEtTempFromVidTimestamp

fun SepareReferentialDataBasesNoVM(
    produitsList: MutableList<OutputNoSqlModel.Produit>,
    name: String,
    viewModel: TarificationViewModel,
)  {
    try {
        val currentStrTime =
            strDateEtTempFromVidTimestamp(
                System.currentTimeMillis()
            )
        println(
            "\n=================$name ===========================================" +
                    "\n================================================================" +
                    "\n======== C Le Test Log Output Print Du Temp=${currentStrTime.first} " +
                    "\n================================================================" +
                    " du   ========"
        )

        println("\n-- Hierarchical Structure --")

        // Create an OutputNoSqlModel from the produitsList
        val outputModel = OutputNoSqlModel(produits = produitsList)

        // Now pass the properly constructed model to logProduits
        logProduits(
            outputModel,
            viewModel
        )

        println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

    } catch (e: Exception) {
        println("Erreur dans SepareReferentialDataBases: ${e.message}")
        throw e
    }
}
