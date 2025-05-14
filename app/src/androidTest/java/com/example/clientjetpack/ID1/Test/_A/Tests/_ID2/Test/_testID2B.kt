package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test

import com.example.clientjetpack.ID1.Test.Packages.Modules.Log.logHErartchiDataBase
import com.example.clientjetpack.ID1.Test._A.Tests.Packages.Repository.Output.OutputNoSqlModelRepositoryImp
import com.example.clientjetpack.ID1.Test.Packages.ViewModel.TarificationViewModel
import kotlinx.coroutines.flow.first
import kotlin.test.assertEquals

suspend fun testID_2_B(viewModel: TarificationViewModel) {
    kotlinx.coroutines.delay(100)

    val repoImpl = viewModel.outputNoSqlRepository as? com.example.clientjetpack.ID1.Test._A.Tests.Packages.Repository.Output.OutputNoSqlModelRepositoryImp
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

