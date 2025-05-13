package com.example.clientjetpack.ID1.Test._ID1.Test.Modules.Log

import com.example.clientjetpack.ID1.Test._ID1.Test.Models.OutputNoSqlModel
import com.example.clientjetpack.ID1.Test._ID1.Test.Z.Function.strDateEtTempFromVidTimestamp
import com.example.clientjetpack.Id1.PrixChangable.Test._ID1.Test.Modules.Log.logProduits

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
