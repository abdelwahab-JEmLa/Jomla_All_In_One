package com.example.clientjetpack.ID1.Test._A.Tests._ID2.Test

import com.example.clientjetpack.ID1.Test.Packages.Modules.Log.logHErartchiDataBase
import com.example.clientjetpack.ID1.Test.Packages.ViewModel.TarificationViewModel
import kotlinx.coroutines.flow.first

suspend fun testID2(viewModel: TarificationViewModel) {
    logHErartchiDataBase(
        viewModel.outputNoSqlFlow.first().produits.toMutableList(),
        "logHErartchiDataBase"
    )
}
