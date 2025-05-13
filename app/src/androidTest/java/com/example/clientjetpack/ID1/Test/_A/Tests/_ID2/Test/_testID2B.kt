package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test

import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.Modules.Log.logHErartchiDataBase
import com.example.clientjetpack.ID1.Test.Packages.Repository.Output.OutputNoSqlModelRepositoryImp
import V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.Packages.ViewModel.TarificationViewModel
import kotlinx.coroutines.flow.first
import kotlin.test.assertEquals

suspend fun testID_2_B(viewModel: TarificationViewModel) {
    kotlinx.coroutines.delay(100)

    val repoImpl = viewModel.outputNoSqlRepository as? OutputNoSqlModelRepositoryImp
    repoImpl?.refreshData()

    viewModel.addTest(viewModel)

    kotlinx.coroutines.delay(200)

    repoImpl?.refreshData()

    kotlinx.coroutines.delay(100)

    val currentProductCount = viewModel.outputNoSqlFlow.first().produits.size

    assertEquals(
        5,
        currentProductCount,
        "Expected 5 products but found $currentProductCount"
    )

    logHErartchiDataBase(
        viewModel.outputNoSqlFlow.first().produits.toMutableList(),
        "testID_2_BaddNewTestDataTarificationEtClient"
    )
}

