package com.example.clientjetpack.ID1.Test._ID2.Test

import com.example.clientjetpack.ID1.Test.Z.Fragment.A.ViewModel.TarificationViewModel
import com.example.clientjetpack.ID1.Test._ID1.Test.Modules.Log.logHErartchiDataBase
import kotlinx.coroutines.flow.first

suspend fun testID2(viewModel: TarificationViewModel) {
    logHErartchiDataBase(
        viewModel.outputNoSqlFlow.first().produits.toMutableList(),
        "logHErartchiDataBase"
    )
}
