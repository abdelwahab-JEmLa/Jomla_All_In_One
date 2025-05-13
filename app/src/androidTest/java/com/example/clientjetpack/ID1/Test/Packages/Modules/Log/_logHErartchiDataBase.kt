package com.example.clientjetpack.ID1.Test.Packages.Modules.Log

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment._A.Preview.Preview.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test.Packages.Function.strDateEtTempFromVidTimestamp

fun logHErartchiDataBase(
    produitsList: MutableList<OutputNoSqlModel.Produit>,
    name: String,
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
        )

        println("\n========TEST $name COMPLETED SUCCESSFULLY ========\n")

    } catch (e: Exception) {
        println("Erreur dans SepareReferentialDataBases: ${e.message}")
        throw e
    }
}
